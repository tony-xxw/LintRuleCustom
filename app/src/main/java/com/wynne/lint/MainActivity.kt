package com.wynne.lint

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        findViewById<TextView>(R.id.tvContent).setTextColor(Color.parseColor("35BCDC"))
        Log.d("XXW", "1111111111111111")

    }
}