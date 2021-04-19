package com.srtp.assistant.ui.work.workDetail

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.R
import com.srtp.assistant.logic.dao.WorkDao
import com.srtp.assistant.ui.work.addWork.AddWorkActivity
import kotlinx.android.synthetic.main.activity_work_detail.*

class WorkDetailActivity : AddWorkActivity(),View.OnClickListener {

    private val workDetailViewModel by lazy { ViewModelProvider(this).get(WorkDetailViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_detail)

        setSupportActionBar(toolbarWorkDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarWorkDetail.setNavigationOnClickListener{
            finish()
        }

        initView()

        showData()

        //作业项已完成、未完成单选框处理函数
        radioGroupIsCompeted.setOnCheckedChangeListener { _, i ->

            if (i == notCompeted.id) {
                workDetailViewModel.work.isCompleted = 0
                notCompeted.isChecked = true
                competed.isChecked = false
            }

            if (i == competed.id) {
                workDetailViewModel.work.isCompleted = 1
                competed.isChecked = true
                notCompeted.isChecked = false
            }

            workDetailViewModel.updateWork()

            showData()
        }

    }

    private fun initView(){
        workDetailViewModel.firstSaveTimeStamp = intent.getLongExtra("work_id",0)

        try {
            workDetailViewModel.work = workDetailViewModel.findWorkByFirstSaveTime(workDetailViewModel.firstSaveTimeStamp)!!
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }

        textViewHomeworkItemDetailsCourse.setOnClickListener(this)
        plusHomeworkItemDetailsPicturesOne.setOnClickListener(this)
        plusHomeworkItemDetailsPicturesTwo.setOnClickListener(this)
        plusHomeworkItemDetailsPicturesThree.setOnClickListener(this)
        editTextHomeworkItemDetailsWork.setOnClickListener(this)
        textViewHomeworkItemDetailsAsDate.setOnClickListener(this)
        textViewDetailsDeletePicturesOne.setOnClickListener(this)
        textViewDetailsDeletePicturesTwo.setOnClickListener(this)
        textViewDetailsDeletePicturesThree.setOnClickListener(this)
        uploadDetailsPicturesIcon.setOnClickListener(this)
        buttonTime.setOnClickListener(this)
        buttonDate.setOnClickListener(this)
        fabSubmitDetailWork.setOnClickListener(this)
    }

    /**
     * 添加菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toobar_work_detail, menu)
        return true
    }

    /**
     * 添加菜单选择处理函数
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteWork -> deleteWork()
            R.id.changeWork -> changeWork()
            R.id.cancelChange -> cancelChange()
        }
        return true
    }

    private fun cancelChange() {
        editTextHomeworkItemDetailsWork.isFocusableInTouchMode = false
        editTextHomeworkItemDetailsWork.hint = ""
        buttonDate.visibility = View.GONE
        buttonTime.visibility = View.GONE
        textViewDetailsDeletePicturesOne.visibility = View.GONE
        textViewDetailsDeletePicturesTwo.visibility = View.GONE
        textViewDetailsDeletePicturesThree.visibility = View.GONE
        uploadDetailsPicturesIcon.visibility = View.GONE
        textViewHomeworkItemDetailsAsDate.visibility = View.VISIBLE
        fabSubmitDetailWork.visibility = View.GONE
        workDetailViewModel.work = workDetailViewModel.findWorkByFirstSaveTime(workDetailViewModel.firstSaveTimeStamp)!!
        showData()
    }

    /**
     * 重写返回键响应函数，当页面处于编辑状态时先退出编辑状态；否则返回上一个页面
     */
    override fun onBackPressed() {
        if(editTextHomeworkItemDetailsWork.isFocusableInTouchMode){
            cancelChange()
        }else{
            super.onBackPressed()
        }

    }

    private fun submitWork() {

        if (viewModel.date!=""){
            workDetailViewModel.date = viewModel.date
        }
        if (viewModel.time!=""){
            workDetailViewModel.time = viewModel.time
        }

        workDetailViewModel.work.content = editTextHomeworkItemDetailsWork.text.toString()

        workDetailViewModel.updateWork()

        editTextHomeworkItemDetailsWork.isFocusableInTouchMode = false
        buttonDate.visibility = View.GONE
        buttonTime.visibility = View.GONE
        textViewDetailsDeletePicturesOne.visibility = View.GONE
        textViewDetailsDeletePicturesTwo.visibility = View.GONE
        textViewDetailsDeletePicturesThree.visibility = View.GONE
        uploadDetailsPicturesIcon.visibility = View.GONE
        textViewHomeworkItemDetailsAsDate.visibility = View.VISIBLE
        editTextHomeworkItemDetailsWork.hint = ""
        fabSubmitDetailWork.visibility = View.GONE




        showData()
    }

    private fun showData() {

        workDetailViewModel.firstSaveTimeStamp = intent.getLongExtra("work_id",0)

        try {

            workDetailViewModel.work = workDetailViewModel.findWorkByFirstSaveTime(workDetailViewModel.firstSaveTimeStamp)!!

            textViewHomeworkItemDetailsCourse.text = workDetailViewModel.work.courseName
            editTextHomeworkItemDetailsWork.setText(workDetailViewModel.work.content)
            textViewHomeworkItemDetailsAsDate.text = workDetailViewModel.work.time.let {
                AssistantApplication.getStrTime(it)
            }

            textViewHomeworkItemDetailsRestTime.text = AssistantApplication.getDifferTime(workDetailViewModel.work.time.let {
                AssistantApplication.getStrTime(it)
            })


            if (workDetailViewModel.work.PicturesOnePath!=""){
                plusHomeworkItemDetailsPicturesOne.setImageURI(Uri.parse(workDetailViewModel.work.PicturesOnePath))
                plusHomeworkItemDetailsPicturesOne.visibility = View.VISIBLE
            }

            if (workDetailViewModel.work.PicturesTwoPath!=""){
                plusHomeworkItemDetailsPicturesTwo.setImageURI(Uri.parse(workDetailViewModel.work.PicturesTwoPath))
                plusHomeworkItemDetailsPicturesTwo.visibility = View.VISIBLE
            }

            if (workDetailViewModel.work.PicturesThreePath!=""){
                plusHomeworkItemDetailsPicturesThree.setImageURI(Uri.parse(workDetailViewModel.work.PicturesThreePath))
                plusHomeworkItemDetailsPicturesThree.visibility = View.VISIBLE
            }

            if (workDetailViewModel.work.isCompleted==0){
                notCompeted.isChecked = true
                competed.isChecked = false
            }
            else
            {
                competed.isChecked = true
                notCompeted.isChecked = false
            }


        }catch (e:java.lang.Exception){
            //数据库读取异常，强制退出页面
            Toast.makeText(this,"非常抱歉，该作业项不存在，请下拉刷新试试！",Toast.LENGTH_SHORT).show()
            finish()
            e.printStackTrace()
        }


    }

    /**
     * 修改作业
     */
    private fun changeWork() {

        //viewModel.addingWork = workDetailViewModel.work
        editTextHomeworkItemDetailsWork.isFocusableInTouchMode = true
        editTextHomeworkItemDetailsWork.hint = "请输入作业内容"

        try {
            val s = AssistantApplication.getStrTime(workDetailViewModel.work.time).split(" ")
            buttonDate.text = s[0]
            buttonTime.text = s[1]
        }catch (e:Exception){
            e.printStackTrace()
        }

        buttonDate.visibility = View.VISIBLE
        buttonTime.visibility = View.VISIBLE
        fabSubmitDetailWork.visibility = View.VISIBLE

        var i = 0
        if (workDetailViewModel.work.PicturesOnePath!=""){
            textViewDetailsDeletePicturesOne.visibility = View.VISIBLE
            i++
        }
        if (workDetailViewModel.work.PicturesTwoPath!=""){
            textViewDetailsDeletePicturesTwo.visibility = View.VISIBLE
            i++
        }
        if (workDetailViewModel.work.PicturesThreePath!=""){
            textViewDetailsDeletePicturesThree.visibility = View.VISIBLE
            i++
        }
        if (i<3){
            uploadDetailsPicturesIcon.visibility = View.VISIBLE
        }
        textViewHomeworkItemDetailsAsDate.visibility = View.GONE
    }

    /**
     * 删除作业
     */
    private fun deleteWork() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setPositiveButton("是") { _, _ ->
            workDetailViewModel.deleteWork()
            finish()
        }
        builder.setNegativeButton("否") { dialogInterface, _ -> dialogInterface.dismiss()
        }
        builder.setMessage("是否删除该作业？")
        builder.setTitle("提示信息")
        builder.show()
    }

    override fun onClick(p0: View?) {
        when(p0){
            textViewDetailsDeletePicturesOne -> deletePictureOne()
            textViewDetailsDeletePicturesTwo -> deletePictureTwo()
            textViewDetailsDeletePicturesThree -> deletePictureThree()
            uploadDetailsPicturesIcon -> showAlbumAction()
            fabSubmitDetailWork -> submitWork()
            buttonDate -> {
                btnDate(buttonDate)
            }
            buttonTime -> {
                btnTime(buttonTime)
            }
            plusHomeworkItemDetailsPicturesOne -> {
                showBigPictures(Uri.parse(workDetailViewModel.work.PicturesOnePath))
                viewModel.dia?.show()
            }
            plusHomeworkItemDetailsPicturesTwo -> {
                showBigPictures(Uri.parse(workDetailViewModel.work.PicturesTwoPath))
                viewModel.dia?.show()
            }
            plusHomeworkItemDetailsPicturesThree -> {
                showBigPictures(Uri.parse(workDetailViewModel.work.PicturesThreePath))
                viewModel.dia?.show()
            }

        }
    }

    private fun deletePictureOne() {
        textViewDetailsDeletePicturesOne.visibility = View.GONE
        workDetailViewModel.work.PicturesOnePath=""
        plusHomeworkItemDetailsPicturesOne.visibility = View.GONE
    }
    private fun deletePictureTwo() {
        textViewDetailsDeletePicturesTwo.visibility = View.GONE
        workDetailViewModel.work.PicturesTwoPath=""
        plusHomeworkItemDetailsPicturesTwo.visibility = View.GONE
    }
    private fun deletePictureThree() {
        textViewDetailsDeletePicturesThree.visibility = View.GONE
        workDetailViewModel.work.PicturesThreePath=""
        plusHomeworkItemDetailsPicturesThree.visibility = View.GONE
    }

    /**
     * 保存作业图片内容
     */
      override  fun saveWorkPictures(data: Intent?){
        val filePath = AssistantApplication.savePictureToLocal(data)
        if (filePath!=""){
            when {
                workDetailViewModel.work.PicturesOnePath == "" -> {
                    plusHomeworkItemDetailsPicturesOne.setImageURI(Uri.parse(filePath))
                    textViewDetailsDeletePicturesOne.visibility = View.VISIBLE
                    plusHomeworkItemDetailsPicturesOne.visibility = View.VISIBLE
                    workDetailViewModel.work.PicturesOnePath = filePath
                }
                workDetailViewModel.work.PicturesTwoPath == "" -> {
                    plusHomeworkItemDetailsPicturesTwo.setImageURI(Uri.parse(filePath))
                    textViewDetailsDeletePicturesTwo.visibility = View.VISIBLE
                    plusHomeworkItemDetailsPicturesTwo.visibility = View.VISIBLE
                    workDetailViewModel.work.PicturesTwoPath = filePath
                }
                workDetailViewModel.work.PicturesThreePath == "" -> {
                    plusHomeworkItemDetailsPicturesThree.setImageURI(Uri.parse(filePath))
                    textViewDetailsDeletePicturesThree.visibility = View.VISIBLE
                    plusHomeworkItemDetailsPicturesThree.visibility = View.VISIBLE
                    workDetailViewModel.work.PicturesThreePath = filePath
                }
                else -> {
                    Toast.makeText(this, "最多可添加3张图片！", Toast.LENGTH_SHORT).show()
                    uploadDetailsPicturesIcon.visibility = View.GONE
                }
            }
        }
    }

    override fun setMyImageUri(uri: Uri) {
        when {
            viewModel.addingWork.PicturesOnePath == "" -> {
                plusHomeworkItemDetailsPicturesOne.setImageURI(uri)
                textViewDetailsDeletePicturesOne.visibility = View.VISIBLE
                plusHomeworkItemDetailsPicturesOne.visibility = View.VISIBLE
                viewModel.addingWork.PicturesOnePath = uri.toString()
            }
            viewModel.addingWork.PicturesTwoPath == "" -> {
                plusHomeworkItemDetailsPicturesTwo.setImageURI(uri)
                textViewDetailsDeletePicturesTwo.visibility = View.VISIBLE
                plusHomeworkItemDetailsPicturesTwo.visibility = View.VISIBLE
                viewModel.addingWork.PicturesTwoPath = uri.toString()
            }
            viewModel.addingWork.PicturesThreePath == "" -> {
                plusHomeworkItemDetailsPicturesThree.setImageURI(uri)
                textViewDetailsDeletePicturesThree.visibility = View.VISIBLE
                plusHomeworkItemDetailsPicturesThree.visibility = View.VISIBLE
                viewModel.addingWork.PicturesThreePath = uri.toString()
            }
            else -> {
                Toast.makeText(this, "最多可添加3张图片！", Toast.LENGTH_LONG).show()
                uploadDetailsPicturesIcon.visibility = View.GONE
            }
        }
    }

    override fun addPicturesByPath() {
        when {
            viewModel.addingWork.PicturesOnePath == "" -> {
                plusHomeworkItemDetailsPicturesOne.setImageBitmap(
                    BitmapFactory.decodeFile(
                        viewModel.mCameraImagePath
                    )
                )
                textViewDetailsDeletePicturesOne.visibility = View.VISIBLE
                plusHomeworkItemDetailsPicturesOne.visibility = View.VISIBLE
                viewModel.addingWork.PicturesOnePath = viewModel.mCameraImagePath.toString()
            }
            viewModel.addingWork.PicturesTwoPath == "" -> {
                plusHomeworkItemDetailsPicturesTwo.setImageBitmap(
                    BitmapFactory.decodeFile(
                        viewModel.mCameraImagePath
                    )
                )
                textViewDetailsDeletePicturesTwo.visibility = View.VISIBLE
                plusHomeworkItemDetailsPicturesTwo.visibility = View.VISIBLE
                viewModel.addingWork.PicturesTwoPath = viewModel.mCameraImagePath.toString()
            }
            viewModel.addingWork.PicturesThreePath == "" -> {
                plusHomeworkItemDetailsPicturesThree.setImageBitmap(
                    BitmapFactory.decodeFile(
                        viewModel.mCameraImagePath
                    )
                )
                textViewDetailsDeletePicturesThree.visibility = View.VISIBLE
                plusHomeworkItemDetailsPicturesThree.visibility = View.VISIBLE
                viewModel.addingWork.PicturesThreePath = viewModel.mCameraImagePath.toString()
            }
            else -> {
                Toast.makeText(this, "最多可添加3张图片！", Toast.LENGTH_LONG).show()
                uploadDetailsPicturesIcon.visibility = View.GONE
            }
        }
    }
}