package com.example.dooch_wms_mobile_v12

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.dooch_wms_mobile_v12.databinding.ActivityMainBinding
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


