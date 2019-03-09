package com.github.waterpeak.onepage.example

import android.content.Context
import android.view.View
import com.github.waterpeak.onepage.OnePage
import org.jetbrains.anko._FrameLayout
import org.jetbrains.anko.frameLayout

abstract class BasePage: OnePage() {

    override fun onCreateContentView(context: Context): View {
        return context.frameLayout {
            createAnkoView()
        }
    }

    override fun onCreate() {
        super.onCreate()
        onCreateWork()
    }

    protected open fun _FrameLayout.createAnkoView() = Unit

    protected open fun onCreateWork() = Unit
}