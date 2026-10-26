package space.alven.andx.sock2vpn.Dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

abstract class Hooks(
    val CONTEXT: Context,
    val DB_NAME: String = "defalt.db",
    val DB_VERSION: Int = 1,

): SQLiteOpenHelper(CONTEXT, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        // Table link
        db.execSQL("""
            CREATE TABLE Link(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE,
                type INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE SshLink(
                id INTEGER PRIMARY KEY REFERENCES Link(id),
                host TEXT NOT NULL,
                port INTEGER,
                user TEXT NOT NULL,
                pwd TEXT,
                localPort INTEGER,
                pkfuri TEXT,
            )
        """.trimIndent())
        // TODO 表補全，查詢 privateKeyPassphrase 含義
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}