package com.example.glus

import android.os.Parcelable
import androidx.room.*

@Entity
data class CountryDB(
        @ColumnInfo(name="name") val name:String,
        @ColumnInfo(name="region") val region:String?,
        @ColumnInfo(name="subregion") val subregion:String?,
        @ColumnInfo(name="capital") val capital:String?,
        @ColumnInfo(name="population") val population:Int?
){
    @PrimaryKey(autoGenerate = true) var uid:Int = 0
}

@Dao
interface CountryDAO{
    @Query("SELECT * FROM CountryDB")
    fun getAll():List<CountryDB>

    @Insert
    fun insert(countries:List<CountryDB>)

    @Query("DELETE FROM CountryDB")
    fun clear()

}

@Database(entities = arrayOf(CountryDB::class), version = 3)
abstract class CountryDatabase:RoomDatabase(){
    abstract fun countryDao():CountryDAO
}

















