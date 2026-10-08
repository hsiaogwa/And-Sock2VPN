package space.alven.andx.sock2vpn.Entity

class SshLnkConf(
    var host: String,
    var port: Int = 22,
    var user: String,
    var pwd: String? = null,
    val privateKey: File? = null,
    val privateKeyPassphrase: String? = null,
    val knownHosts: File? = null,
    var localPort: Int = 1080,
    var name: String
) {
}