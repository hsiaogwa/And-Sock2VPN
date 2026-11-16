package space.alven.andx.sock2vpn.Dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

@Deprecated("Now use Dao.Database (using Room) to instead this class (using OpenHelper).",
    ReplaceWith("Database", "space.alven.andx.sock2vpn.Dao.DataBase"),
    DeprecationLevel.WARNING)
abstract class Hooks(

    val CONTEXT: Context,
    val DB_NAME: String = "defalt.db",
    val DB_VERSION: Int = 0x1002646e,

): SQLiteOpenHelper(CONTEXT, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        /**
         * Tables
         */

        db.execSQL("""
            CREATE TABLE Link(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE,
                type INTEGER NOT NULL,
                trust INTEGER NOT NULL DEFAULT(0)
            )
        """.trimIndent());
        db.execSQL("""
            CREATE TABLE SshLink(
                id INTEGER PRIMARY KEY REFERENCES Link(id),
                host TEXT NOT NULL,
                port INTEGER,
                user TEXT NOT NULL,
                pwd TEXT,
                localPort INTEGER,
                pkfuri TEXT REFERENCES fkey(uri),
                pkphrase TEXT
            )
        """.trimIndent());
        db.execSQL("""
            CREATE TABLE fkey(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE,
                uri TEXT UNIQUE
            )
        """.trimIndent());

    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

}
