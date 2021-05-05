package com.srtp.assistant.ui.course

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.larswerkman.holocolorpicker.ColorPicker
import com.larswerkman.holocolorpicker.OpacityBar
import com.larswerkman.holocolorpicker.SVBar
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.R
import com.srtp.assistant.logic.model.Course
import com.srtp.assistant.ui.settings.courseSettings.CourseSettingsActivity
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_course.*
import kotlinx.android.synthetic.main.dialog_add_course.*
import kotlinx.android.synthetic.main.select_week.view.*
import org.jsoup.Jsoup
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.set


@Suppress("DEPRECATION")
class CourseActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(CourseViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)

        setSupportActionBar(toolbarCourse)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarCourse.setNavigationOnClickListener{
            finish()
        }


        //背景图与状态栏颜色融入一体
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

       refreshBackground()

        fabSubmitCourse.setOnClickListener {
            setData(false)
            fabSubmitCourse.visibility = View.GONE
        }

        course_background.background.alpha = 200
        AssistantApplication.getSavedCourseSettings(this)

        setData(false)
    }

    /**
     * 刷新课程表背景
     */
    private fun refreshBackground(){
        val courseSettings = AssistantApplication.getSavedCourseSettings(this)
        if (courseSettings.backgroundType==1){
            val bg = Drawable.createFromPath(courseSettings.background)
            if (bg!=null){
                course_background.background = bg
            }
        }else if (courseSettings.backgroundType==0){
            course_background.setBackgroundColor(resources.getColor(courseSettings.background.toInt()))
        }
    }

    /**
     * 添加菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toorbar_course, menu)
        return true
    }

    /**
     * 添加菜单选择处理函数
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addCourse -> addCourse()               //添加课程
            R.id.shareCourse -> shareCourse()           //分享课程表
            R.id.settingsCourse -> settings()           //课程设置
            R.id.changeBackground -> changeBackground() //更换课表显示背景
            R.id.clearCourse -> clearAllCourse()        //清空当前课程表
        }
        return true
    }

    /**
     * 清空正在显示课表中的所有课程
     */
    private fun clearAllCourse() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("温馨提示")
        builder.setMessage("您确定要清空课程表吗？（清空后不可恢复）")
        builder.setPositiveButton("确定"){ _, _->
            viewModel.deleteAllCourseByTable(viewModel.courseSettings.showingLessonTable)
        }
        builder.setNegativeButton("取消"){ dialog, _->
            dialog.dismiss()
        }
        builder.show()
    }

    /**
     * 更换背景图片
     */
    private fun changeBackground() {
        showAlbumAction()
    }

    /**
     * 分享课程表
     */
    private fun shareCourse() {

    }

    /**
     * 启动课程设置页面
     */
    private fun settings() {
        val intent = Intent(this, CourseSettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        setData(false)
    }


    /**
     * 导入课程
     */
    private fun addCourse() {
        showAddCourseWayListDialog()  //展示导入课程列表
    }


    /**
     * 展示导入课程方式列表
     */
    private fun showAddCourseWayListDialog() {
        val items = arrayOf("手动输入", "从教务网导入", "图片智能识别导入")
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择添加方式：")
        listDialog.setItems(items) { _, which -> // which 下标从0开始
            when(which){
                0 -> {
                    setData(true)
                    fabSubmitCourse.visibility = View.VISIBLE
                    Toast.makeText(this, "根据课程的开始节和星期点击添加，长按删除", Toast.LENGTH_LONG).show()
                }
                1 -> leadCourseFromNetWork()
                2 -> leadCourseByPictures()
            }
        }
        listDialog.show()
    }

    /**
     * 通过图片智能识别方式导入
     */
    private fun leadCourseByPictures() {
        Toast.makeText(this, "暂未实现", Toast.LENGTH_SHORT).show()
    }

    /**
     * 通过教务网导入
     */
    private fun leadCourseFromNetWork() {
        Toast.makeText(this, "暂未实现", Toast.LENGTH_SHORT).show()
    }



    override fun onBackPressed() {
        if (fabSubmitCourse.visibility==View.VISIBLE){
            setData(false)
            fabSubmitCourse.visibility=View.GONE
        }else{
            super.onBackPressed()
        }

    }

    /**设置课程表的显示数据
     * @param editable: Boolean 是否可编辑
     */
    private fun setData(editable: Boolean){

        viewModel.courseSettings = AssistantApplication.getSavedCourseSettings(this)


        //设置当前周和星期
        val title = "第 ${viewModel.courseSettings.nowWeek} 周 " +
                "${viewModel.year}/${viewModel.month}/${viewModel.day} " +
                AssistantApplication.getStringWeek(viewModel.week)
        val strTitle = "我的课表<br><font color= \"#338DFB\"><small><small>$title</small></small></font>"
        course_title.text = Html.fromHtml(strTitle)

        addHeadCourse()  //显示课程表头部，星期
        addTimeListCourse()  //显示课程表左侧时间表

        //移除星期一到星期日表格布局中所有view
        main_course_gridLayout_week1.removeAllViews()
        main_course_gridLayout_week2.removeAllViews()
        main_course_gridLayout_week3.removeAllViews()
        main_course_gridLayout_week4.removeAllViews()
        main_course_gridLayout_week5.removeAllViews()
        main_course_gridLayout_week6.removeAllViews()
        main_course_gridLayout_week7.removeAllViews()

        //确定是否显示周末的课程
        var w = 7  //一周有多少天显示课程
        if (viewModel.courseSettings.isShowWeekend==0){       //不显示周末
            w = 5
            main_course_gridLayout_week6.visibility = View.GONE
            main_course_gridLayout_week7.visibility = View.GONE
        }else{  //显示周末
            main_course_gridLayout_week6.visibility = View.VISIBLE
            main_course_gridLayout_week7.visibility = View.VISIBLE
        }

        for (j in 1..w){  //从星期一到星期天循环
            var i = 0
            while (i<viewModel.courseSettings.courseSumNumber){   //从第一节到最后一节课循环

                //读取节为i+1，星期数为j的当前课表所有课程
                val courseList = viewModel.courseListWeekAndSection(
                    viewModel.courseSettings.showingLessonTable,
                    i + 1,
                    j
                )

                //设置表格布局的布局参数:(i,0)坐标，权重为1.0f
                val rowSpec = GridLayout.spec(i, 1, 1.0f)  //行
                val columnSpec = GridLayout.spec(0)               //列
                val params = GridLayout.LayoutParams(rowSpec, columnSpec)
                params.width = this.resources.displayMetrics.widthPixels/(w+1)  //设置表格宽度为屏幕宽度的1/(w+1)

                //设置表格高度
                if (courseList.isNotEmpty()) {
                    //有课时表格高度为表格宽度的5/4乘以该课程所占的节数
                    params.height = params.width*5/4*courseList[0].sectionNum
                    params.bottomMargin = resources.getDimension(R.dimen.course_item_card_padding).toInt()
                }else {
                    //无课时表格高度等于宽度的5/4
                    params.height = params.width*5/4
                }

                //设置表格对齐方式为垂直方向居中
                params.setGravity(Gravity.CENTER_VERTICAL)

                val tv = getCard(courseList, i + 1, j, editable)  //通过星期和节来确定课程

                //根据星期将课程添加到对应的表格布局中
                when(j){
                    1 -> main_course_gridLayout_week1.addView(tv, params)
                    2 -> main_course_gridLayout_week2.addView(tv, params)
                    3 -> main_course_gridLayout_week3.addView(tv, params)
                    4 -> main_course_gridLayout_week4.addView(tv, params)
                    5 -> main_course_gridLayout_week5.addView(tv, params)
                    6 -> main_course_gridLayout_week6.addView(tv, params)
                    7 -> main_course_gridLayout_week7.addView(tv, params)
                }

                    if (courseList.isNotEmpty()){
                    //有课时节数循环变量跳过该课程所占节数
                    i += courseList[0].sectionNum
                }else{
                    i++
                }
            }
        }
    }

    /**定义课程表每个小格子
     * @param courseList:List<Course>   课程列表
     * @param startSection:Int    开始节
     * @param week:Int   星期数
     * @param editable:Boolean    是否可添加课程
     * @return 返回一个小格子的卡片布局
     */
    @SuppressLint("SetTextI18n")
    private fun getCard(courseList: List<Course>, startSection: Int, week: Int, editable: Boolean): MaterialCardView {

        val card = MaterialCardView(this)

        if (courseList.isNotEmpty()){

            //设置卡片布局的属性
            card.strokeColor = resources.getColor(R.color.course_item_card_border_color)  //边框颜色
            card.strokeWidth = resources.getDimension(R.dimen.course_item_card_strokeWidth).toInt()  //边框宽度
            card.radius = resources.getDimension(R.dimen.course_item_corner)  //圆角半径
            card.elevation = resources.getDimension(R.dimen.course_item_card_strokeWidthDefault)   //阴影宽度

            val course = courseList[0]
            if (course.color!=0){
                card.setCardBackgroundColor(resources.getColor(course.color))  //设置卡片的背景颜色
                //card.background.alpha = 200  //设置卡片的背景透明度，值范围为0~255,值越小透明度越高
            }

            //定义TextView显示课程数据
            val tv = MaterialTextView(this)
            tv.text = "${course.name}\n(${course.teacher})\n${course.classRoom}" //设置字体
            tv.textSize = resources.getDimension(R.dimen.course_item_font_size)  //设置字体大小
            tv.setPadding(resources.getDimension(R.dimen.course_item_text_padding).toInt())  //设置边距
            tv.textAlignment = MaterialTextView.TEXT_ALIGNMENT_CENTER  //设置文字对齐方式为居中对齐
            tv.setTextColor(resources.getColor(R.color.course_item_font_color)) //设置字体颜色

            //判断该课程当前周是否有课
            val s = StringBuffer(course.classWeekNum)
            if(viewModel.courseSettings.nowWeek>=1&&viewModel.courseSettings.nowWeek<=viewModel.courseSettings.weekSum){
                if (s[viewModel.courseSettings.nowWeek - 1]=='0'){

                    //当前周无课时设置卡片背景透明度为100，并提示该课程是非本周课程
                    card.background.alpha = 100
                    tv.setTextColor(resources.getColor(R.color.colorTipsFont))
                    tv.text = "${course.name}\n(${course.teacher})\n${course.classRoom}\n(非本周)"
                }else{

                    //判断该课程是否正在上课
                    val classTime = AssistantApplication.getClassTime(this, course)
                    val df = SimpleDateFormat("HH:mm", Locale.CHINA)//hh表示12小时制，HH表示24小时制
                    val now = df.parse(df.format(Date()))!! //当前时间
                    val beginTime = df.parse(classTime.startTime)!!
                    val endTime = df.parse(classTime.endTime)!!
                    if (AssistantApplication.belongCalendar(now, beginTime, endTime)&&course.week==AssistantApplication.getWeek(
                            viewModel.week
                        )){
                        tv.setTextColor(resources.getColor(R.color.colorPrimary))
                    }
                }
            }




            card.addView(tv)  //将TextView加入卡片布局中
        }
        else{

            //设置无课时的卡片属性
            card.radius = resources.getDimension(R.dimen.course_item_corner_default)
            card.strokeColor = resources.getColor(R.color.course_item_card_border_color_default)
            card.strokeWidth = resources.getDimension(R.dimen.course_item_card_strokeWidthDefault).toInt()
            if (!editable){
                //不可编辑时无课的卡片占屏幕位置但不可见，不可见控件的监听函数无效
                card.visibility = View.INVISIBLE
            }

            card.setCardBackgroundColor(resources.getColor(R.color.course_item_card_default_color))
        }

        //设置点击监听函数，实现点击卡片时显示、修改或添加课程
        card.setOnClickListener {
            //Toast.makeText(this,"$startSection $week",Toast.LENGTH_SHORT).show()
            showDialog(week, startSection)
        }

        //设置长按监听函数，实现长按卡片时删除课程
        card.setOnLongClickListener {
            if (courseList.isNotEmpty()){
                val builder = android.app.AlertDialog.Builder(this)
                builder.setPositiveButton("是") { _, _ ->
                    viewModel.deleteCourseByStartSectionAndWeek(
                        viewModel.courseSettings.showingLessonTable,
                        startSection,
                        week
                    )
                    setData(false)
                }
                builder.setNegativeButton("否") { dialogInterface, _ -> dialogInterface.dismiss()
                }
                builder.setMessage("是否删除该课程？")
                builder.setTitle("提示信息")
                builder.show()
            }
            return@setOnLongClickListener true
        }

        return card
    }


    /**
     * 添加课表左侧时间表信息
     */
    @SuppressLint("SetTextI18n")
    private fun addTimeListCourse(){

        main_course_gridLayout_time.removeAllViews() //移除所有view



        for (i in 0 until viewModel.courseSettings.courseSumNumber){

            //设置表格布局的属性
            val rowSpec = GridLayout.spec(i, 1, 1.0f)
            val columnSpec = GridLayout.spec(0)
            val params = GridLayout.LayoutParams(rowSpec, columnSpec)

            //显示周末时
            params.width = this.resources.displayMetrics.widthPixels/8  //屏幕宽度的1/8

            if (viewModel.courseSettings.isShowWeekend==0){ //不显示周末
                params.width = this.resources.displayMetrics.widthPixels/6
            }

            params.height = params.width*5/4
            params.setGravity(Gravity.CENTER_VERTICAL)

            //定义显示时间的TextView
            val tvTime = MaterialTextView(this)
            tvTime.setTextColor(resources.getColor(R.color.colorCourseTime))
            tvTime.textSize = resources.getDimension(R.dimen.course_time)
            tvTime.width = params.width
            tvTime.typeface = Typeface.defaultFromStyle(Typeface.BOLD)//加粗
            tvTime.textAlignment = MaterialTextView.TEXT_ALIGNMENT_CENTER

            //获取第i+1节的上课、下课时间
            val course = Course()
            course.sectionStart = i+1
            course.sectionNum = 1
            val time = AssistantApplication.getClassTime(this, course)

            tvTime.text = "${time.startTime}\n${time.endTime}"

            //定义显示节数的TextView
            val tvSection = MaterialTextView(this)
            tvSection.setTextColor(resources.getColor(R.color.colorCourseTime))
            tvSection.textSize = resources.getDimension(R.dimen.course_item_right_font_size)
            tvSection.width = params.width
            tvSection.typeface = Typeface.defaultFromStyle(Typeface.BOLD)//加粗
            tvSection.textAlignment = MaterialTextView.TEXT_ALIGNMENT_CENTER
            tvSection.text = (i+1).toString()

            val df = SimpleDateFormat("HH:mm", Locale.CHINA)//hh表示12小时制，HH表示24小时制

            //当前时间的时间表项颜色设为蓝色
            val now = df.parse(df.format(Date()))!! //当前时间
            val beginTime = df.parse(time.startTime)!!
            val endTime = df.parse(time.endTime)!!
            if (AssistantApplication.belongCalendar(now, beginTime, endTime)){
                tvTime.setTextColor(resources.getColor(R.color.colorPrimary))
                tvSection.setTextColor(resources.getColor(R.color.colorPrimary))
            }

            //定义布局
            val layout = LinearLayoutCompat(this)
            layout.orientation = LinearLayoutCompat.VERTICAL
            layout.gravity = Gravity.CENTER

            layout.addView(tvSection)
            layout.addView(tvTime)
            main_course_gridLayout_time.addView(layout, params)
        }
    }

    /**
     * 显示课程表头部信息，星期一到星期日
     */
    @SuppressLint("SetTextI18n")
    private fun addHeadCourse(){
        main_course_head_gridLayout.removeAllViews()
        main_course_head_gridLayout.columnCount = 8 //设置表格布局的行数为8
        main_course_head_gridLayout.rowCount = 1    //设置表格布局的列数为1

        //确定是否显示周末
        var w = 7
        if (viewModel.courseSettings.isShowWeekend==0){
            w = 5
        }

        //循环显示周一到周日
        for (i in 0..w){
            val rowSpec = GridLayout.spec(0, 1)
            val columnSpec = GridLayout.spec(i, 1, 1.0f)
            val params = GridLayout.LayoutParams(rowSpec, columnSpec)
            params.width = this.resources.displayMetrics.widthPixels/(w+1)  //屏幕宽度的1/8
            params.height = params.width
            val tv = MaterialTextView(this)
            when(i){
                0 -> tv.text = "节\\周"
                1 -> tv.text = "一"
                2 -> tv.text = "二"
                3 -> tv.text = "三"
                4 -> tv.text = "四"
                5 -> tv.text = "五"
                6 -> tv.text = "六"
                7 -> tv.text = "日"
            }

            //设置当前星期的显示颜色为蓝色，非当前星期颜色为黑色
            if (viewModel.week!=1&&viewModel.week-1==i){
                tv.setTextColor(resources.getColor(R.color.colorPrimary))
            }else if(viewModel.week==1&&i==7){
                tv.setTextColor(resources.getColor(R.color.colorPrimary))
            }
            else{
                tv.setTextColor(resources.getColor(R.color.color_course_week))
            }
            tv.textSize = resources.getDimension(R.dimen.course_head_font_size)

            tv.gravity = Gravity.CENTER

            main_course_head_gridLayout.addView(tv, params)
        }
    }

    /**
     * 颜色选择器
     */
    private fun showDialogColorPicker(){
        val view = LayoutInflater.from(this).inflate(R.layout.color_picker, null, false)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .setTitle("颜色选择器")
            .create()
        val picker:ColorPicker = view.findViewById(R.id.picker)
        val opacityBar:OpacityBar =view.findViewById(R.id.opacity_bar)
        val svBar:SVBar = view.findViewById(R.id.sv_bar)
        val cancel: MaterialButton = view.findViewById(R.id.cancel_select_color)
        val sure: MaterialButton = view.findViewById(R.id.sure_select_color)
        picker.addSVBar(svBar)
        picker.addOpacityBar(opacityBar)

        sure.setOnClickListener {
            dialog.dismiss()
        }

        cancel.setOnClickListener {
//            viewModel.course.color = picker.color
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            this.resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels * 2 / 3
        )
    }

    /**自定义添加、展示、修改课表项的对话框
     * @param week:Int 星期
     * @param sectionStart: Int 开始节
     */
    @SuppressLint("SetTextI18n")
    private fun showDialog(week: Int, sectionStart: Int){

        //加载布局文件
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_course, null, false)

        //创建对话框并设置view
        val dialog = AlertDialog.Builder(this).setView(view).create()

        //通过view绑定布局文件中的组件id
        val cancelCourse: MaterialTextView = view.findViewById(R.id.cancel_course)          //关闭对话框按钮
        val submitCourse: MaterialTextView = view.findViewById(R.id.submit_course)          //提交修改或添加按钮
        val selectColor: ChipGroup = view.findViewById(R.id.select_course_color)            //从给定颜色列表选择颜色的组件
        val selectPersonColor:MaterialTextView = view.findViewById(R.id.select_person_color)//从颜色选择器选择颜色的组件
        val selectWeek: ChipGroup = view.findViewById(R.id.select_week_group)               //选择上课周的组件
        val name: AppCompatEditText = view.findViewById(R.id.add_course_name)               //课程名编辑框
        val place:AppCompatEditText = view.findViewById(R.id.add_course_place)              //上课地点编辑框
        val teacher:AppCompatEditText = view.findViewById(R.id.add_course_teacher)          //上课教师姓名编辑框
        val numberSection:MaterialTextView = view.findViewById(R.id.course_section_number)  //课程节数显示组件
        val credit:AppCompatEditText = view.findViewById(R.id.add_course_credit)            //学分编辑框
        val remark:AppCompatEditText = view.findViewById(R.id.add_course_remark)            //备注编辑框
        val addSection: ImageFilterView = view.findViewById(R.id.add_one_section)           //上课节数加一按钮
        val reduceSection: ImageFilterView = view.findViewById(R.id.reduce_one_section)     //上课节数减一按钮
        val typeWeek:RadioGroup = view.findViewById(R.id.select_week_type_group)            //上课周类型选择组件
        val time:MaterialTextView = view.findViewById(R.id.add_course_time)                 //上课时间范围显示组件
        val singWeek:RadioButton = view.findViewById(R.id.single_week)                      //单周
        val doubleWeek:RadioButton = view.findViewById(R.id.double_week)                    //双周
        val allWeek:RadioButton = view.findViewById(R.id.all_week)                          //全周
        val personWeek:RadioButton = view.findViewById(R.id.person_week)                    //自定义周
        val personColorButton:Button = view.findViewById(R.id.checked_color)                //颜色选择器已选颜色显示按钮

        //在布局文件中初始化将选择上课周的Chip组件设为已选，获取所有Chip组件的id的列表
        viewModel.listCheckedWeek = selectWeek.checkedChipIds as ArrayList<Int>

        //从数据库中查找满足条件的课程信息
        val courseList = viewModel.courseListWeekAndSection(
            viewModel.courseSettings.showingLessonTable,
            sectionStart,
            week
        )

        //若该位置存在课程，则显示该课程；否则显示空白对话框来添加新课程
        if (courseList.isNotEmpty()){
            val course = courseList[0]
            name.setText(course.name)  //显示课程名
            place.setText(course.classRoom)  //显示上课地点
            teacher.setText(course.teacher)  //显示授课教师姓名
            numberSection.text = course.sectionNum.toString() //显示上课节数

            //显示上课时间区间
            val classTime = AssistantApplication.getClassTime(this, course)
            time.text = "${classTime.startTime}-${classTime.endTime}"

            credit.setText(course.credits.toString())  //显示学分

            //显示备注信息
            if (course.remark!=""){
                remark.setText(course.remark)
            }


            //确定周类型并显示每一周是否有课
            var singleW = true
            var doubleW = true
            var allW = true
            val weekList = StringBuilder(course.classWeekNum)
            for (i in 0 until viewModel.listCheckedWeek.size){
                if (i%2==0&&weekList[i]=='1'){  //当i+1为单周并且有课
                    doubleW = false
                }
                if (i%2==1&&weekList[i]=='1'){ //当i+1为双周并且有课
                    singleW = false
                }

                //设置每个Chip的选中状态，有课则设置已选中，无课则设置未选中
                val k = view.findViewById<Chip>(viewModel.listCheckedWeek[i])
                if (weekList[i]=='0'){
                    allW = false
                    k.isChecked = false
                } else{
                    k.isChecked = true
                }
            }

            //设置周类型
            personWeek.isChecked = true  //自定义周
            if (singleW){
                singWeek.isChecked = true  //单周
            }
            if (doubleW){
                doubleWeek.isChecked = true  //双周
            }
            if (allW){
                allWeek.isChecked = true  //全周
            }

            viewModel.course.color = course.color

            //设置课程的背景颜色对应的Chip为已选中状态，其余为未选中状态
            when(course.color){
                R.color.colorCourse1 -> view.findViewById<Chip>(R.id.chip_color_1).isChecked = true
                R.color.colorCourse2 -> view.findViewById<Chip>(R.id.chip_color_2).isChecked = true
                R.color.colorCourse3 -> view.findViewById<Chip>(R.id.chip_color_3).isChecked = true
                R.color.colorCourse4 -> view.findViewById<Chip>(R.id.chip_color_4).isChecked = true
                R.color.colorCourse5 -> view.findViewById<Chip>(R.id.chip_color_5).isChecked = true
                R.color.colorCourse6 -> view.findViewById<Chip>(R.id.chip_color_6).isChecked = true
                R.color.colorCourse7 -> view.findViewById<Chip>(R.id.chip_color_7).isChecked = true
                R.color.colorCourse8 -> view.findViewById<Chip>(R.id.chip_color_8).isChecked = true
                R.color.colorCourse9 -> view.findViewById<Chip>(R.id.chip_color_9).isChecked = true
                R.color.colorCourse10 -> view.findViewById<Chip>(R.id.chip_color_10).isChecked =
                    true
                R.color.colorCourse11 -> view.findViewById<Chip>(R.id.chip_color_11).isChecked =
                    true
                R.color.colorCourse12 -> view.findViewById<Chip>(R.id.chip_color_12).isChecked =
                    true
                else ->{
                    personColorButton.setBackgroundColor(resources.getColor(course.color))
                }
            }
        }else{ //不存在课程

            //获取当前位置的上课时间区域
            val course = Course()
            course.sectionStart = sectionStart  //开始节为点击位置的节数
            course.sectionNum = 1  //上课节数默认为1
            val classTime = AssistantApplication.getClassTime(this, course)
            time.text = "${classTime.startTime}-${classTime.endTime}"

            viewModel.course.color = R.color.colorCourseDefault  //设置显示颜色为默认颜色
        }


        /**
         * 上课周的选择变化监听函数
         */
        typeWeek.setOnCheckedChangeListener { _, i ->

            //设置所有Chip为已选中状态
            for (x in viewModel.listCheckedWeek){
                val k = view.findViewById<Chip>(x)
                if(!k.isChecked){
                    k.isChecked = true
                }
            }
            when(i){
                R.id.single_week -> {
                    //选择单周时，设置所有双周的Chip为未选中状态
                    for (x in 0 until viewModel.listCheckedWeek.size) {
                        if (x % 2 == 1) {
                            val k = view.findViewById<Chip>(viewModel.listCheckedWeek[x])
                            k.isChecked = false
                        }
                    }
                }

                R.id.double_week -> {
                    //选择双周时，设置所有单周的Chip为未选中状态
                    for (x in 0 until viewModel.listCheckedWeek.size) {
                        if (x % 2 == 0) {
                            val k = view.findViewById<Chip>(viewModel.listCheckedWeek[x])
                            k.isChecked = false
                        }
                    }
                }

                R.id.person_week -> {
                    //选择自定义周时，所有周的Chip均设为未选中状态
                    for (x in viewModel.listCheckedWeek) {
                        val k = view.findViewById<Chip>(x)
                        k.isChecked = false
                    }
                }
            }
        }

        //从给定的颜色列表选择颜色并保存在变量中
        selectColor.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.chip_color_1 -> viewModel.course.color = R.color.colorCourse1
                R.id.chip_color_2 -> viewModel.course.color = R.color.colorCourse2
                R.id.chip_color_3 -> viewModel.course.color = R.color.colorCourse3
                R.id.chip_color_4 -> viewModel.course.color = R.color.colorCourse4
                R.id.chip_color_5 -> viewModel.course.color = R.color.colorCourse5
                R.id.chip_color_6 -> viewModel.course.color = R.color.colorCourse6
                R.id.chip_color_7 -> viewModel.course.color = R.color.colorCourse7
                R.id.chip_color_8 -> viewModel.course.color = R.color.colorCourse8
                R.id.chip_color_9 -> viewModel.course.color = R.color.colorCourse9
                R.id.chip_color_10 -> viewModel.course.color = R.color.colorCourse10
                R.id.chip_color_11 -> viewModel.course.color = R.color.colorCourse11
                R.id.chip_color_12 -> viewModel.course.color = R.color.colorCourse12
                else -> viewModel.course.color = R.color.colorCourseDefault
            }
        }


        //上课节数加一按钮
        addSection.setOnClickListener {
            if (sectionStart+numberSection.text.toString().toInt()<=viewModel.courseSettings.courseSumNumber){
                numberSection.text = (numberSection.text.toString().toInt()+1).toString()
                val c = Course()
                c.sectionStart = sectionStart
                c.sectionNum = numberSection.text.toString().toInt()
                val t = AssistantApplication.getClassTime(this, c)
                time.text = "${t.startTime}-${t.endTime}"
            }else{
                Toast.makeText(this, "主人，已到达时间表最后一节课，不可再增加哦", Toast.LENGTH_SHORT).show()
            }
        }

        //上课节数减一按钮
        reduceSection.setOnClickListener {
            if (numberSection.text.toString().toInt()>1){
                numberSection.text = (numberSection.text.toString().toInt()-1).toString()
                val c = Course()
                c.sectionStart = sectionStart
                c.sectionNum = numberSection.text.toString().toInt()
                val t = AssistantApplication.getClassTime(this, c)
                time.text = "${t.startTime}-${t.endTime}"
            }else{
                Toast.makeText(this, "主人，最小设置节数为1，不可再减小哦", Toast.LENGTH_SHORT).show()
            }

        }


        //自定义颜色选择监听函数
        selectPersonColor.setOnClickListener {
            showDialogColorPicker()
            //viewModel.course.color = picker.color
            if (viewModel.course.color!=0){
                personColorButton.setBackgroundColor(resources.getColor(viewModel.course.color))
            }
        }

        //关闭对话框按钮
        cancelCourse.setOnClickListener{
            dialog.dismiss()
        }

        //提交按钮
        submitCourse.setOnClickListener {
            //保存数据
            //保存上课周数
           for(i in viewModel.listCheckedWeek){
                val k = view.findViewById<Chip>(i)
                val j = k.text.toString().replace(" ", "")
                if (k.isChecked){
                    viewModel.weekList[j.toInt() - 1] = '1'
                }else{
                  viewModel.weekList[j.toInt() - 1] = '0'
                }
            }


            //保存课程名、上课地点、授课教师、上课节数、星期等
            viewModel.course.week = week
            viewModel.course.classRoom = place.text.toString()
            viewModel.course.name = name.text.toString()
            viewModel.course.teacher = teacher.text.toString()
            viewModel.course.sectionStart = sectionStart

            if (numberSection.text.toString()!=""){
                viewModel.course.sectionNum = numberSection.text.toString().toInt()
            }
            viewModel.course.sectionNum = numberSection.text.toString().toInt()
            //Log.e("dan","sectionNum=${viewModel.course.sectionNum}")
            if(credit.text.toString()!=""){
                viewModel.course.credits = credit.text.toString().toInt()
            }
            viewModel.course.remark = remark.text.toString()

            //原先没课则保存，原先有课则修改
            if (courseList.isEmpty()){
                viewModel.saveCourse()
                setData(true)
            }else{
                val c = courseList[courseList.size - 1]
                if (viewModel.isNeedChange(c)){//课程信息有改动则修改，无改动则不处理
                    val builder = android.app.AlertDialog.Builder(this)
                    builder.setPositiveButton("确认") { _, _ ->
                        viewModel.course.id = c.id
                        viewModel.updateCourse()
//                        for (i in CourseDao.findCourses()){
//                            Log.e("dan",i.toString())
//                        }
                        setData(false)
                    }
                    builder.setNegativeButton("取消") { dialogInterface, _ -> dialogInterface.dismiss()
                    }
                    builder.setMessage("确认修改该课程？")
                    builder.setTitle("提示信息")
                    builder.show()
               }
                //检查，更新，提示是否确认修改
            }
            dialog.dismiss()
        }
        dialog.show() //显示对话框

        //设置对话框的长和宽
        dialog.window?.setLayout(
            this.resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels * 2 / 3
        )
    }


    /**
     * 检查是否有打开系统相册的权限，没有则动态申请，有则打开相册
     */
    private fun showAlbumAction() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        } else {
            openAlbum()
        }
    }

    /**
     * 调起系统相册
     */
    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            viewModel.choosePhoto
        ) //打开相册

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            viewModel.choosePhoto -> {
                if (resultCode == RESULT_OK) {
                    //处理从系统相册返回的图片
                    if (data == null) { //返回未空，则退出
                        return
                    } else {
                        //调用UCrop开源裁剪库进行裁剪
                        val options = UCrop.Options()
                        options.setCompressionQuality(100)  //设置裁剪后的图片质量，值从0-100。100表示和原图片同样的质量
                        //设置toolbar颜色
                        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary))
                        //设置状态栏颜色
                        options.setStatusBarColor(
                            ActivityCompat.getColor(
                                this,
                                R.color.colorPrimary
                            )
                        )
                        viewModel.uriTempFile = Uri.fromFile(
                            File(
                                cacheDir,
                                "CourseBackground.jpeg"
                            )
                        )
                        UCrop.of(data.data!!, viewModel.uriTempFile!!).withOptions(options)
                            .withAspectRatio(
                                this.resources.displayMetrics.widthPixels.toFloat(),
                                this.resources.displayMetrics.heightPixels.toFloat()
                            ).start(this)
                    }
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    saveCourseBackground(data) //保存裁剪后的图片到本地
                }
            }
        }
    }


    /**
     * 保存课表的背景颜色
     */
    private fun saveCourseBackground(data: Intent?){
        viewModel.courseSettings.backgroundType = 1
        viewModel.courseSettings.background = AssistantApplication.savePictureToLocal(data)
        viewModel.saveCourseSettings(this)
        refreshBackground()
    }
}
