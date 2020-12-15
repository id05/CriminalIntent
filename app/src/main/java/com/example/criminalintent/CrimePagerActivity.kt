package com.example.criminalintent

import android.content.Context
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import java.util.*
const val EXTRA_CRIME_ID = "com.example.criminalIntent.EXTRA_CRIME_ID"

fun crimeIntent(packageContext: Context, crimeID: UUID): Intent {
    val intent = Intent(packageContext, CrimePagerActivity::class.java)
    intent.putExtra(EXTRA_CRIME_ID, crimeID)
    return intent
}

class CrimePagerActivity : AppCompatActivity() {
    lateinit var crimePager:ViewPager2
    private lateinit var crimeLab: CrimeLab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)
        var id = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        crimePager = findViewById(R.id.crime_view_pager)
        crimeLab = CrimeLab.getInstance(applicationContext)
        crimePager.adapter = CrimeFragmentAdapter(this)
        crimePager.currentItem = crimeLab.getCrimeIndex(id)!!
    }

    internal class CrimeFragmentAdapter(activity:FragmentActivity): FragmentStateAdapter(activity){
        private val crimeLab = CrimeLab.getInstance(activity.applicationContext)
        override fun getItemCount(): Int = crimeLab.crimes.size
        override fun createFragment(position: Int): Fragment = CrimeFragment.newInstance(crimeLab.crimes[position].id)
    }
}