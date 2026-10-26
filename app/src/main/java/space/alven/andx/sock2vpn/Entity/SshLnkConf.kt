package space.alven.andx.sock2vpn.Entity

import java.io.File

class SshLnkConf(
    override var name: String,
    override var host: String,
    override var port: Int = 22,
    override var user: String,
    override var pwd: String? = null,
    override var localPort: Int? = 1080,
    override val privateKey: File? = null,
    override val privateKeyPassphrase: String? = null,
    val knownHosts: File? = null,
    var isDefaultTrust: Boolean

): LnkConf {
}