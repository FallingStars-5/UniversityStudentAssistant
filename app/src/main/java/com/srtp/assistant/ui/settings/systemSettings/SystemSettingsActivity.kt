package com.srtp.assistant.ui.settings.systemSettings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.srtp.assistant.R
import kotlinx.android.synthetic.main.activity_system_settings.*

class SystemSettingsActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(SystemSettingsViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_settings)

        setSupportActionBar(toolbar_system_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_system_settings.setNavigationOnClickListener{
            turnToHome()
        }

        initView()

        //设置选择按钮监听事件
        is_use_ring_remind.setOnCheckedChangeListener { _, _ ->
            if (is_use_ring_remind.isChecked){
                viewModel.systemSettings.isRing = 1
            }else{
                viewModel.systemSettings.isRing = 0
            }
            viewModel.saveSystemSettings(this)
        }

        is_use_vibrates_remind.setOnCheckedChangeListener { _, _ ->
            if (is_use_vibrates_remind.isChecked){
                viewModel.systemSettings.isvibrates = 1
            }else{
                viewModel.systemSettings.isvibrates = 0
            }
            viewModel.saveSystemSettings(this)
        }
    }


    /**
     * 返回主页
     */
    private fun turnToHome() {
        finish()
    }


    /**
     * 初始化显示
     */
    private fun initView(){

        viewModel.systemSettings = viewModel.getSavedSystemSettings(this)
        is_use_ring_remind.isChecked = viewModel.systemSettings.isRing == 1
        is_use_vibrates_remind.isChecked = viewModel.systemSettings.isvibrates == 1
    }
}