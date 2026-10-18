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



    fun stopTunnel() {
        startJob?.cancel()
        startJob = null

        serviceScope.launch {
            vpnForward?.stop()
            sshMngr?.stop()
        }
    }

    override fun onDestroy() {
        startJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

}