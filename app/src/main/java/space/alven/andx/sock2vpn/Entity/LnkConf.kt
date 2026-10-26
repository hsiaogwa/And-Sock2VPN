package space.alven.andx.sock2vpn.Entity

import java.io.File

interface LnkConf{
    var name: String;
    var host: String;
    var port: Int;
    var user: String;
    var pwd: String?;
    var localPort: Int?;
    val privateKey: File?;
    val privateKeyPassphrase: String?;
}