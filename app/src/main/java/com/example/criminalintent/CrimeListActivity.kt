package com.example.criminalintent

import androidx.fragment.app.Fragment


class CrimeListActivity: SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return CrimeListFragment()
    }
}