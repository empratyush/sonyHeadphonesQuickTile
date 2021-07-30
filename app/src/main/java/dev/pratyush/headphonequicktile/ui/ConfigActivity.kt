package dev.pratyush.headphonequicktile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import dev.pratyush.headphonequicktile.databinding.ConfigActivityBinding

class ConfigActivity : AppCompatActivity() {

    private lateinit var views : ConfigActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setDecorFitsSystemWindows(true)

        views = ConfigActivityBinding.inflate(layoutInflater)
        setContentView(views.root)

       /* supportFragmentManager.beginTransaction().add(
            views.fragmentContainer.id,
            SettingsPreference()
        ).commit()*/

    }
}