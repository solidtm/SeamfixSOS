package com.solid.seamfixsos.features

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.solid.seamfixsos.databinding.ActivityMainBinding
import com.solid.seamfixsos.features.sos.SosActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            sosBtn.setOnClickListener {
                startActivity(Intent(this@MainActivity, SosActivity::class.java))
            }
        }
    }
}