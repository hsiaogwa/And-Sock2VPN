package space.alven.andx.sock2vpn.Dao

import android.content.Context

@Deprecated("Now use Dao.Database (using Room) to instead this class (using OpenHelper).",
    ReplaceWith("Database", "space.alven.andx.sock2vpn.Dao.DataBase"),
    DeprecationLevel.WARNING)
class Procedures(
    val CONTEXT: Context,
    val DB_NAME: String = "defalt.db",
    val DB_VERSION: Int = 0x100,

) {}
