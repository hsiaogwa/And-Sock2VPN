package space.alven.andx.sock2vpn.Dao

import androidx.room3.RoomDatabase
import androidx.room3.Database

@Database(
    entities = [space.alven.andx.sock2vpn.Entity.SshLnkConf::class, space.alven.andx.sock2vpn.Entity.SockLnkConf::class],
    version = 0x1022647a,
    exportSchema = true
)
abstract class Database : RoomDatabase() {
    abstract fun procedures(): Procedures
}