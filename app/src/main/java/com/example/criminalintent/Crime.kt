package com.example.criminalintent

import java.util.*

class Crime(
    val id: UUID = UUID.randomUUID(),
    var title: String? = "nameless",
    var date: Date = Date(),
    var solved: Boolean = false,
    var suspect: String? = null
) :
    Comparable<Crime> {


    override operator fun compareTo(other: Crime): Int {
        return this.id.compareTo(other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Crime

        if (title != other.title) return false
        if (solved != other.solved) return false
        if (id != other.id) return false
        if (date != other.date) return false

        return true
    }
}