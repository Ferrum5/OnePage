package com.github.waterpeak.onepage.example

import android.graphics.Color
import android.view.Gravity
import org.jetbrains.anko.*

class SecondPage : BasePage(){
    override fun _FrameLayout.createAnkoView() {
        textView{
            textSize = 20f
            textColor = Color.BLACK
            text = """
                This is second page
                managed by OnePage.
                You had navigated from
                frist page.
                Click to unwind
                Click back button to go back
            """.trimIndent()
            setOnClickListener {
                unwind()
            }
        }.lparams{
            gravity = Gravity.CENTER
            horizontalMargin = dip(50)
        }
    }
}