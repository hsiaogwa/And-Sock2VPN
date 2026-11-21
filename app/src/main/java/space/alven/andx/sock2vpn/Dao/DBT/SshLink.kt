package space.alven.andx.sock2vpn.Dao.DBT

import android.net.Uri
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "SshLink",
    foreignKeys = [
        ForeignKey(
            entity = Link::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FKey::class,
            parentColumns = ["uri"],
            childColumns = ["privateKeyUri"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SshLink(
    @PrimaryKey
    var id: Int = 0,

    var host: String,
    var port: Int = 22,
    var user: String,
    var pwd: String? = null,
    var localPort: Int? = 1080,
    val privateKeyUri: String? = null,
    val privateKeyPassphrase: String? = null,
    var isDefaultTrust: Boolean
)
