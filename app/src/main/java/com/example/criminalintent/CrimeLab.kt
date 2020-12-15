package com.example.criminalintent

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import com.example.criminalintent.CrimeDbSchema.CrimeTable
import java.util.*


inline fun Boolean.int(): Int = if (this) 1 else 0


class CrimeLab private constructor(private val context: Context) {
    companion object {
        private var INSTANCE: CrimeLab? = null
        fun getInstance(context: Context) = INSTANCE ?: INSTANCE ?: CrimeLab(context).also {
            INSTANCE = it
        }
    }

    private var database: SQLiteDatabase = CrimeBaseHelper(context).writableDatabase

    private fun queryCrimes(whereClause: String?, whereArgs: Array<String>?): CrimeCursorWrapper =
        CrimeCursorWrapper(
            database.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
            )
        )

    var _crimes: MutableList<Crime>? = null
    val crimes: MutableList<Crime>
        get() {
            if (_crimes == null)
                queryCrimes(null, null).use {
                    _crimes = it.toMutableList()
                }
            return _crimes!!
        }


    fun getCrimeIndex(id: UUID): Int? {
        for ((index, crime) in crimes.withIndex()) {
            if (crime.id == id)
                return index
        }
        return null
    }

    fun deleteCrime(id: UUID) {
        database.delete(
            CrimeTable.NAME,
            CrimeTable.Cols.UUID + " = ?",
            arrayOf(id.toString())
        )
        crimes.removeAt(getCrimeIndex(id)!!)
    }


    fun updateCrime(crime: Crime) {
        database.update(
            CrimeTable.NAME,
            getContentValues(crime),
            CrimeTable.Cols.UUID + " = ?",
            arrayOf(crime.id.toString())
        )
        val index = getCrimeIndex(crime.id)
        if (index != null) {
            crimes[index] = crime
        }

    }

    private fun getContentValues(crime: Crime): ContentValues = contentValuesOf(
        CrimeTable.Cols.UUID to crime.id.toString(),
        CrimeTable.Cols.TITLE to crime.title,
        CrimeTable.Cols.DATE to crime.date.time,
        CrimeTable.Cols.SOLVED to crime.solved.int(),
        CrimeTable.Cols.SUSPECT to crime.suspect
    )


    operator fun plus(crime: Crime): CrimeLab {
        database.insert(CrimeTable.NAME, null, getContentValues(crime))
        crimes += crime
        return this
    }

    operator fun get(id: UUID): Crime? {
        queryCrimes(
            CrimeTable.Cols.UUID + " = ?",
            arrayOf(id.toString())
        ).use {
            if (it.count == 0) {
                return null
            }
            it.moveToFirst()
            return it.getCrime()
        }
    }
}