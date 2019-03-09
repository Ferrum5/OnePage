package com.github.waterpeak.onepage.example

import android.graphics.Color
import android.view.Gravity
import org.jetbrains.anko.*

class FirstPage : BasePage(){
    override fun _FrameLayout.createAnkoView() {
        textView{
            textSize = 20f
            textColor = Color.BLACK
            text = """
                This is first page
                managed by OnePage.
                Click to navigate to next
            """.trimIndent()
            setOnClickListener {
                navigate(SecondPage())
            }
        }.lparams{
            gravity = Gravity.CENTER
            horizontalMargin = dip(50)
        }
    }
}