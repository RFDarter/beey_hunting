package com.rfdarter.beehunting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Einfacher Platzhalter; ersetze durch eigenes Layout wenn nötig
        setContentView(android.R.layout.simple_list_item_1)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Einstellungen"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}