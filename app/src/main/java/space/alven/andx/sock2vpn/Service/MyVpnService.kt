package space.alven.andx.sock2vpn.Service

import android.net.VpnService

import space.alven.andx.sock2vpn.Brain.SshMngr
import space.alven.andx.sock2vpn.Brain.VpnForward

class MyVpnService: VpnService() {

    private var sshMngr: SshMngr? = null;
    private var vpnForward: VpnForward? = null;
}