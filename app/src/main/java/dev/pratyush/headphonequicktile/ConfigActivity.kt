package dev.pratyush.headphonequicktile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setDecorFitsSystemWindows(false)
    }
}