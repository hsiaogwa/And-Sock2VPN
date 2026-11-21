package space.alven.andx.sock2vpn.Dao.DBT

import android.net.Uri
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity()
data class FKey(

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var name: String,

    var uri: String,
)
