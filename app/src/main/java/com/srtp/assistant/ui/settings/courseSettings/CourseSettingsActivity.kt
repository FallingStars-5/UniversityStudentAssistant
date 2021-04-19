package com.srtp.assistant.ui.settings.courseSettings

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.R
import com.srtp.assistant.ui.settings.courseSettings.settingsTime.SettingsTimeActivity
import kotlinx.android.synthetic.main.activity_course_settings.*
import java.util.*

class CourseSettingsActivity : AppCompatActivity(), View.OnClickListener {

    private val viewModel by lazy { ViewModelProvider(this).get(CourseSettingsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_settings)
        setSupportActionBar(toolbar_course_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_course_settings.setNavigationOnClickListener{
            turnToHome()  //返回主页
        }
        init()
    }


    /**
     * 初始化加载数据
     */
    @SuppressLint("SetTextI18n")
    private fun init(){
        setting_course_name.setOnClickListener(this)
        setting_course_class_list_time.setOnClickListener(this)
        setting_course_sum_week_number.setOnClickListener(this)
        setting_course_day_section_number.setOnClickListener(this)
        setting_course_first_week_time.setOnClickListener(this)
        setting_course_now_week.setOnClickListener(this)
        setting_course_remind_time.setOnClickListener(this)

        viewModel.courseSettings = AssistantApplication.getSavedCourseSettings(this)

        //设置是否启用课程提醒服务
        is_use_remind_course.setOnCheckedChangeListener{_,_ ->
            if (is_use_remind_course.isChecked){
                viewModel.courseSettings.isRemindClass = 1
            }else{
                viewModel.courseSettings.isRemindClass = 0
            }
            viewModel.saveCourseSettings(this)
        }

        //设置是否显示周末
        is_show_weekend_course.setOnCheckedChangeListener { _, b ->
            if (is_show_weekend_course.isChecked){
                viewModel.courseSettings.isShowWeekend = 1
            }else{
                viewModel.courseSettings.isShowWeekend = 0
            }
            viewModel.saveCourseSettings(this)
        }

        setting_course_name.text = viewModel.courseSettings.showingLessonTable
        setting_course_first_week_time.text = viewModel.courseSettings.startDate
        setting_course_day_section_number.text = "${viewModel.courseSettings.courseSumNumber} 节"
        setting_course_sum_week_number.text = "${viewModel.courseSettings.weekSum} 周"
        setting_course_now_week.text = "第 ${viewModel.courseSettings.nowWeek} 周"
        is_use_remind_course.isChecked = viewModel.courseSettings.isRemindClass==1
        setting_course_remind_time.text = "${viewModel.courseSettings.remindMinuteTime} 分钟"
        is_show_weekend_course.isChecked = viewModel.courseSettings.isShowWeekend==1
    }

    /**
     * 跳转到主页
     */
    private fun turnToHome() {
        finish()
    }

    override fun onClick(p0: View?) {
        when(p0){
            setting_course_name -> setCourseTableName()
            setting_course_class_list_time -> setClassTime()
            setting_course_day_section_number -> setCourseSectionNumber()
            setting_course_first_week_time -> setStartDate()
            setting_course_now_week -> setNowWeek()
            setting_course_sum_week_number -> setSumWeek()
            setting_course_remind_time -> setRemindTime()
        }
    }

    /**
     * 设置提醒时间，即距上课多少分钟时发送提醒上课消息
     */
    @SuppressLint("SetTextI18n")
    private fun setRemindTime() {
        if (AssistantApplication.getSavedCourseSettings(this).isRemindClass==1){
            val editText = MaterialAutoCompleteTextView(this)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            val inputDialog = AlertDialog.Builder(this)
            inputDialog.setTitle("请输入上课提醒时间").setView(editText)
            inputDialog.setPositiveButton("确定"){_,_ ->
                viewModel.courseSettings.remindMinuteTime = editText.text.toString().toInt()
                viewModel.saveCourseSettings(this)
                setting_course_remind_time.text = "${viewModel.courseSettings.remindMinuteTime} 分钟"
            }
            inputDialog.setNegativeButton("取消"){dialog,_ ->
                dialog.dismiss()
            }
            inputDialog.show()
        }
    }


    /**
     * 设置每学期的总周数
     */
    @SuppressLint("SetTextI18n")
    private fun setSumWeek() {
        val editText = MaterialAutoCompleteTextView(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入学期总周数").setView(editText)
        inputDialog.setPositiveButton("确定"){_,_ ->
            viewModel.courseSettings.weekSum = editText.text.toString().toInt()
            viewModel.saveCourseSettings(this)
            setting_course_sum_week_number.text = "${viewModel.courseSettings.weekSum} 周"
        }
        inputDialog.setNegativeButton("取消"){dialog,_ ->
            dialog.dismiss()
        }
        inputDialog.show()
    }

    /**
     * 设置当前周
     */
    @SuppressLint("SetTextI18n")
    private fun setNowWeek() {
        val editText = MaterialAutoCompleteTextView(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入当前周").setView(editText)
        inputDialog.setPositiveButton("确定"){_,_ ->
            viewModel.courseSettings.nowWeek = editText.text.toString().toInt()
            viewModel.saveCourseSettings(this)
            setting_course_now_week.text = "第 ${viewModel.courseSettings.nowWeek} 周"
        }
        inputDialog.setNegativeButton("取消"){dialog,_ ->
            dialog.dismiss()
        }
        inputDialog.show()
    }


    /**
     * 设置开学日期
     */
    private fun setStartDate() {
        val ca = Calendar.getInstance()
        var mYear = ca[Calendar.YEAR]
        var mMonth = ca[Calendar.MONTH]
        var mDay = ca[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                mYear = year
                mMonth = month
                mDay = dayOfMonth
                val mDate = "${year}-${month + 1}-${dayOfMonth}"
                // 将选择的日期赋值给TextView
                setting_course_first_week_time.text = mDate
                viewModel.courseSettings.startDate = mDate
                AssistantApplication.getSavedCourseSettings(this)
                viewModel.saveCourseSettings(this)
            },
            mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }


    /**
     * 设置每日的课程总节数
     */
    @SuppressLint("SetTextI18n")
    private fun setCourseSectionNumber() {
        val editText = MaterialAutoCompleteTextView(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入课程总节数").setView(editText)
        inputDialog.setPositiveButton("确定"){_,_ ->
            viewModel.courseSettings.courseSumNumber = editText.text.toString().toInt()
            viewModel.saveCourseSettings(this)
            setting_course_day_section_number.text = "${viewModel.courseSettings.courseSumNumber} 节"
        }
        inputDialog.setNegativeButton("取消"){dialog,_ ->
            dialog.dismiss()
        }
        inputDialog.show()
    }


    /**
     * 设置上课时间
     */
    private fun setClassTime() {
        val intent = Intent(this, SettingsTimeActivity::class.java)
        startActivity(intent)
    }


    /**
     * 设置课程表的名称
     */
    private fun setCourseTableName() {
        val editText = MaterialAutoCompleteTextView(this)
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入课表名称").setView(editText)
        inputDialog.setPositiveButton("确定"){_,_ ->
            viewModel.courseSettings.showingLessonTable = editText.text.toString()
            viewModel.saveCourseSettings(this)
            setting_course_name.text = viewModel.courseSettings.showingLessonTable
        }
        inputDialog.setNegativeButton("取消"){dialog,_ ->
            dialog.dismiss()
        }
        inputDialog.show()
    }
}