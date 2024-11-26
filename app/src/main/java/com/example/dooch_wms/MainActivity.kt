package com.example.dooch_wms

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import com.example.dooch_wms.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflate(layoutInflater)로 초기화 한다
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        // binding.root 뷰를 화면에 표시하도록 설정
        setContentView(mainBinding.root)

        mainBinding.btnMenu.setOnClickListener{
            mainBinding.layoutMenu.openDrawer(GravityCompat.START)
        }
        mainBinding.naviView.setNavigationItemSelectedListener(this)

        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_make_production -> {val intent = Intent(this, W_work::class.java)
                startActivity(intent)
            }
        }
        mainBinding.layoutMenu.closeDrawer(GravityCompat.START)
        return false
    }

}


