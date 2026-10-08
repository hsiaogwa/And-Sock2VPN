package space.alven.andx.sock2vpn.Brain

import kotlinx.coroutines.*
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import space.alven.andx.sock2vpn.Entity.SshLnkConf
import java.net.ServerSocket


class SshMngr(
    private val link: SshLnkConf
) {
    private var job: Job? = null;
    private var ssh: SSHClient? = null;
    @Volatile private var isRunning: Boolean = false;

    fun start(scope: CoroutineScope) {
        this.isRunning = true;
        this.job = scope.launch(Dispatchers.IO) {
            while (isRunning) {
                try {
                    this@SshMngr.ssh = SSHClient();

                    // TODO host key use file just like $ ssh -i
                    ssh.addHostKeyVerifier(PromiscuousVerifier());
                    ssh.connect(link.host, link.port);

                    if (link.pwd != null) {
                        ssh.authPassword(link.user, link.pwd);
                    }

                    // TODO port dynamic forward >> e.g. local:1080
                    ssh.newDirectConnection("127.0.0.1", 1080);

                } catch (e: Exception) {
                    e.printStackTrace();
                    if (isRunning) {
                        delay(5000);
                    }
                } finally {
                    ssh?.close();
                }
            }
        }
    }

    fun stop() {
        isRunning = false;
        runCatching { ssh?.disconnect() }
        ssh = null;
        job?.cancel();
        job = null;
    }

    fun close() {}

}