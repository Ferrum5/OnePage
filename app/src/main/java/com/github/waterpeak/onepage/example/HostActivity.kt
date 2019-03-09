package com.github.waterpeak.onepage.example

import com.github.waterpeak.onepage.OnePage
import com.github.waterpeak.onepage.OnePageActivity

class HostActivity : OnePageActivity(){


    override fun createFirstPage(): OnePage = FirstPage()


}