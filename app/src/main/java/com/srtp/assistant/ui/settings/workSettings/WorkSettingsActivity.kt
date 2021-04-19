package com.srtp.assistant.ui.settings.workSettings

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.srtp.assistant.R
import com.srtp.assistant.ui.work.WorkViewModel
import kotlinx.android.synthetic.main.activity_course_settings.*
import kotlinx.android.synthetic.main.activity_work.*
import kotlinx.android.synthetic.main.activity_work_settings.*

class WorkSettingsActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(WorkSettingsViewModel::class.java) }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_settings)

        setSupportActionBar(toolbar_work_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_work_settings.setNavigationOnClickListener{
            turnToHome()
        }

        initView()

        //设置作业编辑提交提醒时间监听函数
        setting_work_remind_time.setOnClickListener {

            if (viewModel.getSavedWorkSettings(this).isRemind ==1){

                val editText = MaterialAutoCompleteTextView(this)
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.hint = "请输入天数"
                //editText.setPadding(100,20,100,20)
                val inputDialog = AlertDialog.Builder(this)
                inputDialog.setTitle("设置提醒时间").setView(editText)
                inputDialog.setPositiveButton("确定"){_,_ ->
                    viewModel.workSettings.remindDayTime = editText.text.toString().toInt()
                    viewModel.saveWorkSettings(this)
                    setting_work_remind_time.text = "剩余 ${viewModel.workSettings.remindDayTime} 天"
                }
                inputDialog.setNegativeButton("取消"){dialog,_ ->
                    dialog.dismiss()
                }
                inputDialog.show()
            }
        }


        is_use_remind_work.setOnCheckedChangeListener { _, _ ->
            if (is_use_remind_work.isChecked){
                viewModel.workSettings.isRemind = 1
            }else{
                viewModel.workSettings.isRemind = 0
            }
            viewModel.saveWorkSettings(this)
        }
    }

    private fun turnToHome() {
        finish()
    }

    /**
     * 初始化显示数据
     */
    @SuppressLint("SetTextI18n")
    private fun initView(){

        //读取已保存的作业设置数据
        viewModel.workSettings = viewModel.getSavedWorkSettings(this)

        //当isRemind = 1时，设置已选中;否则设置未选中
        is_use_remind_work.isChecked = viewModel.workSettings.isRemind==1
        setting_work_remind_time.text = "剩余 ${viewModel.workSettings.remindDayTime} 天"
    }

}