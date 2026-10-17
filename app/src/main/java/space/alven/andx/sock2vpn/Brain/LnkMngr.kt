package space.alven.andx.sock2vpn.Brain

import android.net.VpnService
import space.alven.andx.sock2vpn.Service.MyVpnService

public open class LnkMngr(
    private val vpnService: MyVpnService,
    private val redirector: VpnForward
) {

    /**
     *
     *
     * TODO port dynamic forward >> e.g. local:1080
     */
    protected suspend fun forward() {
        this.redirector.start(
            vpnService = this.vpnService,
            socksHost = "127.0.0.1",
            socksPort = 1080
        )
    }
}