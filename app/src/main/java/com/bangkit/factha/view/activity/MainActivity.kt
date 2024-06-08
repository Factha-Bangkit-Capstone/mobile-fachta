package com.bangkit.factha.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bangkit.factha.R
import com.bangkit.factha.databinding.ActivityMainBinding
import com.bangkit.factha.view.fragment.main.ArticleFragment
import com.bangkit.factha.view.fragment.main.HomeFragment
import com.bangkit.factha.view.fragment.main.SaveFragment
import com.bangkit.factha.view.fragment.main.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        replaceFragment(HomeFragment())

        binding.btnNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.article -> {
                    replaceFragment(ArticleFragment())
                    true
                }
                R.id.save -> {
                    replaceFragment(SaveFragment())
                    true
                }
                R.id.setting -> {
                    replaceFragment(SettingFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
