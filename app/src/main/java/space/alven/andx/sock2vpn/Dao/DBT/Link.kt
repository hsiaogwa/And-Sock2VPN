package space.alven.andx.sock2vpn.Dao.DBT

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "Link",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Link(

    @PrimaryKey(autoGenerate = true)
    open var id: Int = 0,

    open var name: String = "",

    open var type: Int = 0,

    @ColumnInfo(defaultValue = "0")
    open val trust: Boolean = false
)
