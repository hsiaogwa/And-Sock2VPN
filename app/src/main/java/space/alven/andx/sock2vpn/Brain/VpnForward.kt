package space.alven.andx.sock2vpn.Brain

import android.net.VpnService
import android.os.ParcelFileDescriptor
import hev.htproxy.TProxyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class VpnForward(
    private val mtu: Int = 3000 // Tun pack size def 3000byte
) {
    private var tun: ParcelFileDescriptor? = null
    @Volatile private var running = false

    public suspend fun start(
        vpnService: VpnService,
        socksHost: String,
        socksPort: Int
    ) = withContext(Dispatchers.IO) {
        check(!running) { "VPN forward already running" }
        require(socksPort in 1..65535) { "Invalid port num" }

        val fileConfig = File(
            vpnService.filesDir,
            "hev-socks5-tunnel.yml"
        )

        fileConfig.writeText(
            """
            tunnel:
              mtu: $mtu
              ipv4: 198.18.0.1
              icmp: 'off'

            socks5:
              address: '$socksHost'
              port: $socksPort
              udp: 'udp'
            """.trimIndent()
        )

        val bld = vpnService.Builder()
            .setMtu(mtu)
            .setSession("Sock2VPN")
            .addAddress("198.18.0.1", 30) //TO ASK
            .addRoute("0.0.0.0", 0)
            .addDnsServer("1.1.1.1")
            .establish()
            ?: throw IOException( "t2v build eee" );

        tun = bld;

        try {
            /*
             * TODO
             * 這裡先使用 bld.fd 作為 API 示意。
             * 實際整合前，必須確認目前 AAR 的 JNI 實作
             * 是否會複製、接管或關閉傳入的 file descriptor。
             */
            val started = TProxyService.TProxyStartService(
                fileConfig.absolutePath,
                bld.fd
            );
            check(started) { "unknown start eee" };

            running = true;

            while (
                currentCoroutineContext().isActive
                && TProxyService.TProxyIsRunning()
            ) {
                delay(500);
            }
        } catch (e: CancellationException) {
            throw e;
        } catch (e: Exception) {
            e.printStackTrace();
            throw e;
        } finally {
            this@VpnForward.stopNative();
        }
    }

    suspend fun stop() = withContext(Dispatchers.IO) {
        stopNative();
    }

    private fun stopNative() {
        this.running = false;

        runCatching {
            if (TProxyService.TProxyIsRunning()) {
                TProxyService.TProxyStopService();
            }
        }

        runCatching {
            this.tun?.close();
        }

        this.tun = null;
    }
}