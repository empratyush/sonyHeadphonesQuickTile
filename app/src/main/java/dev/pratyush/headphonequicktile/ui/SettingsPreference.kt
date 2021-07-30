package dev.pratyush.headphonequicktile.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import dev.pratyush.headphonequicktile.R

class SettingsPreference : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
    }
}