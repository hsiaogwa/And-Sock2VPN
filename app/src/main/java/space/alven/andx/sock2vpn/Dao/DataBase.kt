package space.alven.andx.sock2vpn.Dao

import androidx.room3.RoomDatabase
import androidx.room3.Database

@Database(
    entities = [space.alven.andx.sock2vpn.Entity.SshLnkConf::class, space.alven.andx.sock2vpn.Entity.SockLnkConf::class],
    version = 0x102, //26471 to now
    exportSchema = true
) abstract class Database : RoomDatabase() {}
