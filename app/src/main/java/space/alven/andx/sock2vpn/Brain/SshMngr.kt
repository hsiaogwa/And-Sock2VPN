package space.alven.andx.sock2vpn.Brain

import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier

import space.alven.andx.sock2vpn.Entity.SshLnkConf
import space.alven.andx.sock2vpn.Service.SshVpnService
import space.alven.andx.sock2vpn.Util.LnkState


public class SshMngr(
    private val link: SshLnkConf,
    private val vpnService: SshVpnService,
    private val redirector: VpnForward
): LnkMngr(
    vpnService, redirector
) {
    private var job: Job? = null;
    private var ssh: SSHClient? = null;
    @Volatile private var isRunning: Boolean = false;
    private var isSocksReady: MutableStateFlow<LnkState> = MutableStateFlow(LnkState.STOPPED);
    private val lifecycleLock = Any()

    public fun timeout(t: Int, lim: Long = 60000L): Long {
        val time: List<Long> = listOf(1000L, 2000L, 5000L, 10000L, 20000L, 30000L, 60000L, 120000L);
        var c: Int = 0;
        var d: Int = t;
        while (d > 0 && c <= 6) {
            d /= 2;
            c++;
        }
        if (time[c] >= lim) return lim;
        return time[c];
    }

    fun start(scope: CoroutineScope): SshMngr {
        synchronized(lifecycleLock) {
            if (isRunning) return this;

            this.isRunning = true;
            this.job = scope.launch(Dispatchers.IO) {
                this@SshMngr.loop();
            }
        }
        return this;
    }

    fun stop(): SshMngr {
        val oldClient: SSHClient?

        synchronized(lifecycleLock) {
            isRunning = false;
            oldClient = ssh;
            ssh = null;
            job?.cancel();
            job = null;
        }

        runCatching {
            oldClient?.disconnect();
        }

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
        var failure: Int = 0;
        while (isRunning) {
            try {
                this.conn();
                failure = 0;
            } catch (e: CancellationException) {
                throw e;
            } catch (e: Exception) {
                failure++;
                e.printStackTrace();
            } finally {
                this.close();
            }

            if (isRunning) {
                delay(timeout(failure));
            }
        }
    }

    private suspend fun conn() {
        val client: SSHClient = SSHClient();

        synchronized(lifecycleLock) {
            if (!isRunning) {
                client.close()
                return
            }

            ssh = client
        }

        try {
            client.addHostKeyVerifier(PromiscuousVerifier());
            client.connect(link.host, link.port);

            this.auth(client);

            this.forward();

            this.isSocksReady.value = LnkState.CONNECTED;
        } finally {
            synchronized(lifecycleLock) {
                if (ssh === client) {
                    ssh = null
                }
            }

            runCatching { client.disconnect() }
            runCatching { client.close() }
        }

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

    suspend fun awaitSocksReady() {
        isSocksReady.first { it == LnkState.CONNECTED }
    }


}