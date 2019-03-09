package com.github.waterpeak.onepage.example

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import org.jetbrains.anko.*

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(AppCompatTextView(this).apply {
            text = "Welcome"
            gravity = Gravity.CENTER
            textSize = 30f
            textColor = Color.BLACK
        })
        Handler().postDelayed({
            startActivity(Intent(this,HostActivity::class.java))
        },1500)
    }
}