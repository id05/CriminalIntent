package com.example.criminalintent

import android.database.Cursor
import android.database.CursorWrapper
import com.example.criminalintent.CrimeDbSchema.*
import java.util.*

inline fun Int.toBool(): Boolean = this != 0

class CrimeCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getCrime(): Crime = Crime(
        UUID.fromString(wrappedCursor.getString(wrappedCursor.getColumnIndex(CrimeTable.Cols.UUID))),
        wrappedCursor.getString(wrappedCursor.getColumnIndex(CrimeTable.Cols.TITLE)),
        Date(wrappedCursor.getLong(wrappedCursor.getColumnIndex(CrimeTable.Cols.DATE))),
        wrappedCursor.getInt(wrappedCursor.getColumnIndex(CrimeTable.Cols.SOLVED)).toBool(),
        wrappedCursor.getString(wrappedCursor.getColumnIndex(CrimeTable.Cols.SUSPECT))
    )

    fun toMutableList(): MutableList<Crime> {
        wrappedCursor.moveToFirst()
        return MutableList(wrappedCursor.count) {
            val crime = getCrime()
            wrappedCursor.moveToNext()
            crime
        }
    }
}
