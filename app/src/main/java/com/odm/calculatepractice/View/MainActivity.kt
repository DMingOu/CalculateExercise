package com.odm.calculatepractice.View

import android.os.Bundle
import com.odm.calculatepractice.R
import me.yokeyword.fragmentation.SupportActivity

class MainActivity : SupportActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //加载主页面 Fragment
        if(findFragment(HomeFragment::class.java) == null) {
            loadRootFragment(R.id.fl_container_main, HomeFragment() ,true,false)
        }
    }
}