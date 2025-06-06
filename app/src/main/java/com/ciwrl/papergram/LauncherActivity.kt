package com.ciwrl.papergram

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ciwrl.papergram.data.UserPreferences
import androidx.appcompat.app.AppCompatDelegate

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedTheme = UserPreferences.getTheme(this)
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        if (UserPreferences.isOnboardingCompleted(this)) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }
        finish()
    }
}