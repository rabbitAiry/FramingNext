package com.airy.framingnext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.airy.framingnext.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.main_container, TableFragment()).commit()
    }
}