package com.example.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class SingleFragmentActivity : AppCompatActivity() {
    abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)
        if(fragment == null){
            fragment = createFragment()
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }
}