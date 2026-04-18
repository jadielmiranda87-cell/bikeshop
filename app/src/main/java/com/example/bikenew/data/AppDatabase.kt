package com.example.bikenew.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bikenew.data.dao.BikenewDao
import com.example.bikenew.data.entities.Bike
import com.example.bikenew.data.entities.CatalogItem
import com.example.bikenew.data.entities.Client
import com.example.bikenew.data.entities.ServiceOrder
import com.example.bikenew.data.entities.ServiceOrderItem
import com.example.bikenew.data.entities.User

@Database(entities = [Client::class, Bike::class, ServiceOrder::class, User::class, CatalogItem::class, ServiceOrderItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bikenewDao(): BikenewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bikenew_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
