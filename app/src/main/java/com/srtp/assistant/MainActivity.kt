package com.srtp.assistant

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.srtp.assistant.logic.Repository
import com.srtp.assistant.logic.model.*
import com.srtp.assistant.ui.campus.CampusActivity
import com.srtp.assistant.ui.course.CourseActivity
import com.srtp.assistant.ui.course.CourseRemindService
import com.srtp.assistant.ui.settings.courseSettings.CourseSettingsActivity
import com.srtp.assistant.ui.settings.systemSettings.SystemSettingsActivity
import com.srtp.assistant.ui.settings.workSettings.WorkSettingsActivity
import com.srtp.assistant.ui.work.addWork.AddWorkActivity
import com.srtp.assistant.ui.work.WorkActivity
import com.srtp.assistant.ui.work.WorkRemindService
import kotlinx.android.synthetic.main.activity_course.*
import kotlinx.android.synthetic.main.activity_course_settings.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_work.*
import kotlinx.android.synthetic.main.current_course.*
import kotlinx.android.synthetic.main.current_work.*
import kotlinx.android.synthetic.main.headpage_head.*
import kotlinx.android.synthetic.main.headpage_lead.*
import org.litepal.LitePal
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),View.OnClickListener {

    private val viewModel by lazy { ViewModelProvider(this).get(MainActivityViewModel::class.java) }

    private lateinit var adapter: MainViewAdapter
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val retrofit = Retrofit
        LitePal.initialize(this)
        LitePal.getDatabase()
        val settings = getSharedPreferences("init", MODE_PRIVATE)
        val init = settings.getBoolean("init",false)
        //程序安装时初始化
        if (!init){
            for (i in 1..74){
                getCampusAddress(i).let { Repository.addCampusAddress(it) }
            }
            val editor = settings.edit()
            editor.putBoolean("init",true)
            editor.apply()
        }


        //背景图与状态栏颜色融入一体
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        val workSettings = viewModel.getSavedWorkSettings(this)
        val start = Intent(this,WorkRemindService::class.java)
        if (workSettings.isRemind==1){
            startService(start)
        }else{
            stopService(start)
        }



        val startCourseRemindService = Intent(this,CourseRemindService::class.java)
        viewModel.courseSettings = AssistantApplication.getSavedCourseSettings(this)
        if (viewModel.courseSettings.isRemindClass==1)
        {
            startService(startCourseRemindService)
        }else{
            stopService(startCourseRemindService)
        }

        setMenu()

        if (AssistantApplication.getSavedCourseSettings(this).nowWeek ==-1){
            val builder = AlertDialog.Builder(this)
            builder.setPositiveButton("前往设置") { _, _ ->
                setStartDate()
            }
            builder.setNegativeButton("稍后设置") { dialogInterface, _ ->
                val mDate = "${viewModel.year}-${viewModel.month}-${viewModel.day}"
                // 将选择的日期赋值给TextView
                viewModel.courseSettings.startDate = mDate
                viewModel.saveCourseSettings(this)
                dialogInterface.dismiss()
            }
            builder.setMessage("请先设置开学日期，否则系统默认将当前周作为第1周")
            builder.setTitle("温馨提示")
            builder.show()

        }



        initView()
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
                viewModel.courseSettings.startDate = mDate
                viewModel.saveCourseSettings(this)
            },
            mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun setMenu() {
        navView.inflateHeaderView(R.layout.nav_head)
        navView.inflateMenu(R.menu.nav_menu)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.courseItem -> turnToCourse()
                R.id.workItem -> turnToWork()
                R.id.addWorkItem -> turnToAddWorkInHeadPage()
                R.id.settingsCourseItem -> turnToCourseSettings()
                R.id.settingsWorkItem -> turnToWorkSettings()
                R.id.settingsSystemItem -> turnToSystemSettings()
                R.id.appInfoItem -> turnToAppInfo()
            }
            return@setNavigationItemSelectedListener false
        }
    }

    /**
     * 关于系统
     */
    private fun turnToAppInfo() {

    }

    /**
     * 跳转到系统设置页面
     */
    private fun turnToSystemSettings() {
        val intent = Intent(this,SystemSettingsActivity::class.java)
        startActivity(intent)
    }

    /**
     * 跳转到作业设置页面
     */
    private fun turnToWorkSettings() {
        val intent = Intent(this,WorkSettingsActivity::class.java)
        startActivity(intent)
    }

    /**
     * 跳转到课程设置页面
     */
    private fun turnToCourseSettings() {
        val intent = Intent(this,CourseSettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        initView()
    }

    /**
     * 界面初始化，加载数据
     */
    @SuppressLint("SetTextI18n")
    private fun initView() {
        workLead.setOnClickListener(this)
        courseLead.setOnClickListener(this)
        addCurrentWork.setOnClickListener(this)
        addWorkInHeadPage.setOnClickListener(this)
        more_works_leader.setOnClickListener(this)

        campusLead.setOnClickListener(this)

        myDraw.setOnClickListener(this)
        nextClassInfo.text = viewModel.nextCourseList(this)
        var s = "凌晨"
        if (viewModel.hour in 4..6) {
            s = "拂晓"
        }
         if(viewModel.hour in 7..7){
            s = "清晨"
        }

        if (viewModel.hour in 8..11){
            s = "上午"
        }

        if (viewModel.hour in 12..13){
            s = "中午"
        }

        if (viewModel.hour in 14..17){
            s = "下午"
        }

        if (viewModel.hour in 16..16){
            s = "黄昏"
        }

        if (viewModel.hour in 17..17){
            s = "傍晚"
        }

        if (viewModel.hour in 18..22){
             s = "晚上"
        }

        if (viewModel.hour in 23..23){
            s = "午夜"
        }

        if (viewModel.hour in 0..1){
            s = "午夜"
        }

        showNowDateAndWeek.text = "${viewModel.month}月${viewModel.day}日 ${AssistantApplication.getStringWeek(viewModel.week)} $s"
        showCourseData()
        showWorkData()
    }


    /**
     * 随机展示一句话
     */
    private fun showRandomPoems(){
        //randomTextOfHeadPage.text = ""
    }

    /**
     * 展示当前课程数据
     */
    @SuppressLint("SetTextI18n")
    private fun showCourseData(){
        val nowList = viewModel.nowCourseList(this)
        if (nowList.isNotEmpty()){

            classTimeStartCurrent.text = getClassTime(nowList[0].sectionStart).startTime
            classTimeEndCurrent.text = getClassTime(nowList[0].sectionStart+nowList[0].sectionNum-1).endTime
            courseNameCurrent.text = nowList[0].name
            classroomPlaceCurrent.text = nowList[0].classRoom
            classSectionCurrent.text = " | 第${nowList[0].sectionStart}节-第${nowList[0].sectionStart+nowList[0].sectionNum-1}节"

            val time =(viewModel.courseSettings.lessonTimeList?.get(nowList[0].sectionStart+nowList[0].sectionNum-2)?.end?.minus(
                MyTime(viewModel.hour,viewModel.minute)
            )
                ?: MyTime(viewModel.hour,viewModel.minute))
            if (time.hour>0){
                restTimeCurrent.text = "距下课还有 ${time.hour} 小时 ${time.minute} 分钟"
            }else{
                restTimeCurrent.text = "距下课还有 ${time.minute} 分钟"
            }


            nowCourseLayout.visibility = View.VISIBLE
            nowCourseLayoutEmpty.visibility = View.GONE

        }else{
            nowCourseLayout.visibility = View.GONE
            nowCourseLayoutEmpty.visibility = View.VISIBLE
        }
    }

    /**
     * 展示待提交作业列表数据
     */
    private fun showWorkData() {
        val workList = viewModel.findAllNotCompletedWork()
        viewModel.findAllNotCompletedWork()
        val layoutManager = LinearLayoutManager(this)
        workListScrollViewCurrent.layoutManager = layoutManager
        if (workList.isNotEmpty()){
            workListScrollViewCurrent.visibility = View.VISIBLE
            fillWorkFragment.visibility = View.GONE
        }
        if (workList.size>5){
            more_works_leader.visibility = View.VISIBLE
        }else{
            more_works_leader.visibility = View.GONE
        }
        adapter = MainViewAdapter(this, workList)
        workListScrollViewCurrent.adapter = adapter
    }

    /**
     * 点击事件监听处理函数
     */
    override fun onClick(p0: View?) {
        when (p0) {
            workLead  -> turnToWork()
            courseLead -> turnToCourse()
            addCurrentWork -> turnToAddWorkCarryCourseName()
            addWorkInHeadPage -> turnToAddWorkInHeadPage()
            myDraw -> drawerLayout.openDrawer(GravityCompat.START)
            more_works_leader -> turnToWork()

            campusLead -> turnToCampus()
        }
    }

    private fun turnToCampus() {
        val intent = Intent(this,CampusActivity::class.java)
        startActivity(intent)
    }

    /**
     * 监听返回键，当侧滑菜单处于打开状态时，先关闭侧滑菜单；否则返回
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    /**
     * 从主页的作业列表跳转至添加作业页面
     */
    private fun turnToAddWorkInHeadPage() {
        val intent = Intent(this, AddWorkActivity::class.java)
        startActivity(intent)
    }


    /**
     * 从当前课程携带课程名跳转至添加作业页面
     */
    private fun turnToAddWorkCarryCourseName() {
        val intent = Intent(this, AddWorkActivity::class.java).apply {
            putExtra("courseName","此处填入当前课程的课程名称")
        }
        startActivity(intent)
    }

    /**
     * 跳转到课程表页面
     */
    private fun turnToCourse() {
        val intent = Intent(this,CourseActivity::class.java)
        startActivity(intent)
    }

    /**
     * 跳转到作业页面
     */
    private fun turnToWork() {
        val intent = Intent(this,WorkActivity::class.java)
        startActivity(intent)
    }
}