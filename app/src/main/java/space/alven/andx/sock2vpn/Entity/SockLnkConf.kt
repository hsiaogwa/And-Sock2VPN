package space.alven.andx.sock2vpn.Entity

import java.io.File

class SockLnkConf(
    var host: String,
    var port: Int,
    var user: String,
    var pwd: String?,
    val privateKey: File?,
    val privateKeyPassphrase: String?,
    var localPort: Int?

): LnkConf() {
}
/*
var host: String;
var port: Int;
var user: String;
var pwd: String?;
var localPort: Int?;
val privateKey: File?;
val privateKeyPassphrase: String?;*/
