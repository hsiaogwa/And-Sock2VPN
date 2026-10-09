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

    public fun timeout(t: Int): Long {
        val time: List<Long> = listOf(1000L, 2000L, 5000L, 10000L, 20000L, 30000L, 60000L);
        var c: Int = 0;
        var d: Int = t;
        while (d > 0 && c <= 6) {
            d /= 2;
            c++;
        }
        return time[c];
    }

    fun start(scope: CoroutineScope): SshMngr {
        if (isRunning) return this;
        this.isRunning = true;
        this.job = scope.launch(Dispatchers.IO) {
            this@SshMngr.loop();
        }
        return this;
    }

    fun stop(): SshMngr {
        isRunning = false;
        runCatching { ssh?.disconnect() }
        ssh = null;
        job?.cancel();
        job = null;
        return this;
    }

    fun close(): SshMngr {
        runCatching {
            ssh?.disconnect()
        }
        ssh = null
        return this;
    }

    private suspend fun loop() {
        while (isRunning) {
            try {
                this.conn();
            } catch (e: CancellationException) {
                throw e;
            } catch (e: Exception) {
                e.printStackTrace();
            } finally {
                this.close();
            }

            if (isRunning) {
                delay(5000L);
            }
        }
    }

    private fun conn() {
        val client: SSHClient = SSHClient();
        this.ssh = client;

        client.addHostKeyVerifier(PromiscuousVerifier());
        client.connect(link.host, link.port);

        if (link.pwd != null && link.pwd != "") {
            this.auth(client);
        }

        // TODO host key use file just like $ ssh -i
        // TODO port dynamic forward >> e.g. local:1080
        this.redirect();

    }

    private fun auth(client: SSHClient) {
        if (link.privateKey != null) {

            val keys = if (link.privateKeyPassphrase != null) {
                client.loadKeys(
                    link.privateKey.absolutePath,
                    link.privateKeyPassphrase
                )
            } else {
                client.loadKeys(
                    link.privateKey.absolutePath
                )
            }

            client.authPublickey(
                link.user,
                keys
            )

        } else if (link.pwd != null) {

            client.authPassword(
                link.user,
                link.pwd
            )

        } else {
            error("unknown authentication")
        }
    }
    private fun redirect() {
        // TODO Method Socks5
    }

}