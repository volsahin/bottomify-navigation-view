package com.volcaniccoder.bottomifysample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.volcaniccoder.bottomify.BottomifyNavigationView
import com.volcaniccoder.bottomify.OnNavigationItemChangeListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomify = findViewById<BottomifyNavigationView>(R.id.bottomify_nav)
        bottomify.setOnNavigationItemChangedListener(object : OnNavigationItemChangeListener {
            override fun onNavigationItemChanged(navigationItem: BottomifyNavigationView.NavigationItem) {
                Toast.makeText(this@MainActivity,
                        "Selected item at index ${navigationItem.position}",
                        Toast.LENGTH_SHORT).show()
            }
        })

        /* If you want to change active navigation item programmatically
         * bottomify.setActiveNavigationIndex(2)
         */
    }
}
