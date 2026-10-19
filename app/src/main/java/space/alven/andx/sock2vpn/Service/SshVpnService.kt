package space.alven.andx.sock2vpn.Service

import android.net.VpnService

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import space.alven.andx.sock2vpn.Brain.SshMngr
import space.alven.andx.sock2vpn.Brain.VpnForward
import space.alven.andx.sock2vpn.Entity.SshLnkConf

class SshVpnService: VpnService() {

    private val serviceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO);

    private var sshMngr: SshMngr? = null;
    private var vpnForward: VpnForward? = null;
    private var startJob: Job? = null;

    @Synchronized
    fun startTunnel(link: SshLnkConf) {
        if (startJob?.isActive == true) return;

        startJob = serviceScope.launch {
            val forward = VpnForward();
            val ssh = SshMngr(link, this@SshVpnService, forward);

            // 由服務建立並持有兩個物件
            sshMngr = ssh;
            vpnForward = forward;

            try {
                // 1. 啟動 SSH 連線管理
                ssh.start(this@SshVpnService.serviceScope);

                // 2. 等待 SOCKS5 監聽端點確實可用
                ssh.awaitSocksReady();

                // 3. 建立 TUN，啟動 hev 引擎
                forward.start(
                    vpnService = this@SshVpnService,
                    socksHost = "127.0.0.1",
                    socksPort = 1080
                );
            } finally {
                // 不論正常結束或發生錯誤，都清理資源
                withContext(NonCancellable) {
                    runCatching { forward.stop() }
                    runCatching { ssh.stop() }
                }

                if (sshMngr === ssh) sshMngr = null;
                if (vpnForward === forward) vpnForward = null;
            }
        }
    }


    @Synchronized
    fun stopTunnel() {
        startJob?.cancel();
        sshMngr?.stop();
        /*
        serviceScope.launch {
            vpnForward?.stop();
            sshMngr?.stop();
        }
        */
        val forward = vpnForward;
        if (forward != null) {
            serviceScope.launch {
                runCatching {
                    forward.stop();
                    sshMngr?.stop();
                }
            }
        }
    }

    override fun onDestroy() {
        startJob?.cancel();
        sshMngr?.stop();

        // 先要求停止 Native 引擎，再取消服務的工作
        val forward = vpnForward;
        if (forward != null) {
            kotlinx.coroutines.runBlocking {
                withContext(NonCancellable + Dispatchers.IO) {
                    runCatching { forward.stop() };
                }
            }
        }

        serviceScope.cancel();
        super.onDestroy();
    }

    private val binder = LocalBinder();

    inner class LocalBinder : android.os.Binder() {
        fun getService(): SshVpnService = this@SshVpnService;
    }

    override fun onBind(intent: android.content.Intent): android.os.IBinder {
        return binder;
    }

}