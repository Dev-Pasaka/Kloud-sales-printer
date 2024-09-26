package data.local.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.ubunuworks.kloudsales.pc.externalprinter.AppDatabase
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.migration.AutomaticSchemaMigration




object Database {
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:AppDatabase.db")
    val database:AppDatabase
    init {
        AppDatabase.Schema.create(driver)
        database = AppDatabase(driver)
    }
}


