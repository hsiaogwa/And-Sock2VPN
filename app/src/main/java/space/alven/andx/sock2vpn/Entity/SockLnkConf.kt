package space.alven.andx.sock2vpn.Entity

import java.io.File

class SockLnkConf(
    override var host: String,
    override var port: Int,
    override var user: String,
    override var pwd: String?,
    override val privateKey: File?,
    override val privateKeyPassphrase: String?,
    override var localPort: Int?,
    override var name: String

): LnkConf {
}