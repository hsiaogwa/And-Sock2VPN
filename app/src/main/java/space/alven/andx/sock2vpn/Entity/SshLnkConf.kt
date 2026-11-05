package space.alven.andx.sock2vpn.Entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import java.io.File

@Entity(tableName = "SshLink")
data class SshLnkConf(
    @PrimaryKey(autoGenerate = true) private val id: Int = 0,
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