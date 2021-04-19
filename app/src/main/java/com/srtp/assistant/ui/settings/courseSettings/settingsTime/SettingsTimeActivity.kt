package com.srtp.assistant.ui.settings.courseSettings.settingsTime

import android.annotation.SuppressLint
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.logic.model.Course


import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textview.MaterialTextView
import com.srtp.assistant.R
import com.srtp.assistant.logic.model.CourseTime
import com.srtp.assistant.logic.model.MyTime
import com.srtp.assistant.logic.model.getClassTime
import kotlinx.android.synthetic.main.activity_settings_time.*
import java.util.*
import kotlin.collections.ArrayList

class SettingsTimeActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel by lazy { ViewModelProvider(this).get(SettingsTimeViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_time)
        setSupportActionBar(toolbar_settings_time)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_settings_time.setNavigationOnClickListener{
            turnToHome()
        }

        init()
    }

    /**
     * 显示初始化数据
     */
    @SuppressLint("SetTextI18n")
    private fun init(){
        setting_time_same_break_time.setOnClickListener(this)
        setting_time_different_break_time.setOnClickListener(this)
        setting_time_one_class_time.setOnClickListener(this)

        viewModel.courseSettings = viewModel.getSavedCourseSettings(this)

        setting_time_different_break_time.text = "${viewModel.courseSettings.differentClassBetweenTime} 分钟"
        setting_time_same_break_time.text = "${viewModel.courseSettings.sameClassBetweenTime} 分钟"
        setting_time_one_class_time.text = "${viewModel.courseSettings.classTime} 分钟"

        setTimeTable()
    }


    /**
     * 手动修改调整时间表
     */
    private fun adjustTimeList(){
        val sameBreakTime = viewModel.courseSettings.sameClassBetweenTime
        val differentBreakTime = viewModel.courseSettings.differentClassBetweenTime
        val classTime = viewModel.courseSettings.classTime

    }

    /**
     **设置时间表
      */
    @SuppressLint("SetTextI18n")
    private fun setTimeTable(){
        val initSize = viewModel.courseSettings.lessonTimeList?.size ?:0
        if (initSize <viewModel.courseSettings.courseSumNumber){
            for (i in initSize until viewModel.courseSettings.courseSumNumber){
                (viewModel.courseSettings.lessonTimeList as ArrayList<CourseTime>)
                    .add(CourseTime(MyTime(0,0),MyTime(0,0)))
            }
        }

        settings_time_view.removeAllViews()
        for (i in 0 until viewModel.courseSettings.courseSumNumber){
            val layoutView = LinearLayoutCompat(this) //每一列时间的显示布局
            val tv1 = MaterialTextView(this)  //显示第几节课
            val tv2 = MaterialTextView(this)  //不显示字符，起占位对齐的作用
            val tv3 = MaterialTextView(this)  //显示上课时间
            val tv4 = MaterialTextView(this)  //显示分隔号“-”
            val tv5 = MaterialTextView(this)  //显示下课时间

            //获取第i+1节课的上课时间
            val course = Course()
            course.sectionStart = i+1
            course.sectionNum = 1
            val time = AssistantApplication.getClassTime(this,course)

            //布局参数
            val params = LinearLayoutCompat.LayoutParams(
                                                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                                                LinearLayoutCompat.LayoutParams.WRAP_CONTENT)


            layoutView.orientation = LinearLayoutCompat.HORIZONTAL  //水平方向的线性布局
            layoutView.layoutParams = params
            layoutView.setPadding(resources.getDimension(R.dimen.settings_time_margin_bottom).toInt())

            tv1.text = "第 ${i+1} 节"
            tv1.textSize = resources.getDimension(R.dimen.settings_time_font_size) //设置字体大小
            tv1.setTextColor(resources.getColor(R.color.colorCourseTime))   //字体颜色


            val p = LinearLayoutCompat.LayoutParams(0,LinearLayoutCompat.LayoutParams.WRAP_CONTENT)
            p.weight = 1F   //设置weight = 1，width = 0,表示占满该行内所有空位
            tv2.layoutParams = p

            tv3.text = time.startTime
            tv3.textSize = resources.getDimension(R.dimen.settings_time_font_size)
            tv3.setTextColor(resources.getColor(R.color.colorTipsFont))
            tv3.setOnClickListener {
                timePicker(tv3,i,true)    //调用时间选择器
            }

            tv4.text = "-"
            tv4.textSize = resources.getDimension(R.dimen.settings_time_font_size)
            tv4.setTextColor(resources.getColor(R.color.colorTipsFont))

            tv5.text = time.endTime
            tv5.textSize = resources.getDimension(R.dimen.settings_time_font_size)
            tv5.setTextColor(resources.getColor(R.color.colorTipsFont))
            tv5.setOnClickListener {
                timePicker(tv5,i,false)
            }

            //将组件添加到布局中
            layoutView.addView(tv1)
            layoutView.addView(tv2)
            layoutView.addView(tv3)
            layoutView.addView(tv4)
            layoutView.addView(tv5)

            settings_time_view.addView(layoutView)
        }
    }


    /**
     * 时间选择器
     * @param textView:MaterialTextView
     * @param isStartTime:Boolean
     */
    private fun timePicker(textView: MaterialTextView,i:Int,isStartTime:Boolean){
        val ca = Calendar.getInstance()
        var mHour = ca[Calendar.HOUR_OF_DAY]
        var mMinute = ca[Calendar.MINUTE]

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                val mTime = "${hourOfDay}:${minute}"
                textView.text = mTime
                if (isStartTime){
                    viewModel.courseSettings.lessonTimeList?.get(i)?.start = MyTime(mHour,mMinute)
                }else{
                    viewModel.courseSettings.lessonTimeList?.get(i)?.end = MyTime(mHour,mMinute)
                }
            },
            mHour, mMinute, true
        )
        timePickerDialog.show()
    }


    /**
     * 添加菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_course_menu, menu)
        return true
    }

    /**
     * 添加菜单选择处理函数
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.submit_time_change -> submit()
            R.id.restore_default -> restoreDefault()
        }
        return true
    }

    /**
     * 恢复默认
     */
    private fun restoreDefault() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setPositiveButton("确认") { _, _ ->
            viewModel.courseSettings.lessonTimeList = ArrayList<CourseTime>()
            for (i in 0 until viewModel.courseSettings.courseSumNumber){

                    if (i<15){

                    getClassTime(i+1).startTime.split(":")[0].toInt()

                    val t1 = MyTime(getClassTime(i+1).startTime.split(":")[0].toInt(),
                                    getClassTime(i+1).startTime.split(":")[1].toInt())

                    val t2 = MyTime(getClassTime(i+1).endTime.split(":")[0].toInt(),
                                    getClassTime(i+1).endTime.split(":")[1].toInt())

                    val course = CourseTime(t1,t2)

                    (viewModel.courseSettings.lessonTimeList as ArrayList<CourseTime>).add(course)

                }else{
                    (viewModel.courseSettings.lessonTimeList as ArrayList<CourseTime>)
                        .add(CourseTime(MyTime(0,0),MyTime(0,0)))
                }
            }

            viewModel.courseSettings.sameClassBetweenTime = 5
            viewModel.courseSettings.differentClassBetweenTime = 15
            viewModel.courseSettings.classTime = 45
            viewModel.saveCourseSettings(this)
            init()
        }
        builder.setNegativeButton("取消") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.setMessage("确认恢复默认？")
        builder.setTitle("提示信息")
        builder.show()
    }


    /**
     * 提交修改
     */
    private fun submit() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setPositiveButton("确认") { _, _ ->
            viewModel.saveCourseSettings(this)
        }
        builder.setNegativeButton("取消") { dialogInterface, _ -> dialogInterface.dismiss()
        }
        builder.setMessage("确认修改？")
        builder.setTitle("提示信息")
        builder.show()
    }

    private fun turnToHome() {
        finish()
    }

    override fun onClick(p0: View?) {
        when(p0){
            setting_time_same_break_time -> setSameBreakTime()
            setting_time_different_break_time -> setDifferentBreakTime()
            setting_time_one_class_time -> setClassTime()
        }
    }

    /**
     * 设置每一节课的上课时间
     */
    @SuppressLint("SetTextI18n")
    private fun setClassTime() {
        val editText = MaterialAutoCompleteTextView(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入每一节课的上课时间").setView(editText)
        inputDialog.setPositiveButton("确定"){_,_ ->
            viewModel.courseSettings.classTime = editText.text.toString().toInt()
            setting_time_one_class_time.text = "${viewModel.courseSettings.classTime} 分钟"
        }
        inputDialog.setNegativeButton("取消"){dialog,_ ->
            dialog.dismiss()
        }
        inputDialog.show()
    }

    /**
     * 设置不同讲之间的课间休息时间
     */
    @SuppressLint("SetTextI18n")
    private fun setDifferentBreakTime() {
        val editText = MaterialAutoCompleteTextView(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入不同讲课之间的休息时间").setView(editText)
        inputDialog.setPositiveButton("确定"){_,_ ->
            viewModel.courseSettings.differentClassBetweenTime = editText.text.toString().toInt()
            setting_time_different_break_time.text =
                                "${viewModel.courseSettings.differentClassBetweenTime} 分钟"
        }
        inputDialog.setNegativeButton("取消"){dialog,_ ->
            dialog.dismiss()
        }
        inputDialog.show()
    }


    /**
     * 设置同一讲课之间的休息时间
     */
    @SuppressLint("SetTextI18n")
    private fun setSameBreakTime() {
        val editText = MaterialAutoCompleteTextView(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入同一讲课不同节之间的休息时间").setView(editText)
        inputDialog.setPositiveButton("确定"){_,_ ->
            viewModel.courseSettings.sameClassBetweenTime = editText.text.toString().toInt()
            setting_time_same_break_time.text =
                                    "${viewModel.courseSettings.sameClassBetweenTime} 分钟"
        }
        inputDialog.setNegativeButton("取消"){dialog,_ ->
            dialog.dismiss()
        }
        inputDialog.show()
    }
}