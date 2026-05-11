package com.codingindia.tamater

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingindia.tamater.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _mainBinding: ActivityMainBinding? = null
    private val mainBinding get() = _mainBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

    }
}