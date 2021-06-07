package edu.nuce.apps.newflowtypes

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity(R.layout.activity_test) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val result = intent?.getLongExtra(MainActivity::class.java.simpleName, -1L)

        findViewById<TextView>(R.id.tv_hello2).text = result.toString()
    }
}