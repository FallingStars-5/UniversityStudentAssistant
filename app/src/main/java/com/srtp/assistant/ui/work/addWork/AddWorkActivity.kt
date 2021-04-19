package com.srtp.assistant.ui.work.addWork

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.EnvironmentCompat
import androidx.lifecycle.ViewModelProvider
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.R
import com.srtp.assistant.ui.work.WorkActivity
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_add_work.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

open class AddWorkActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var viewModel: AddWorkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work)
        viewModel = ViewModelProvider(this).get(AddWorkViewModel::class.java)
        setSupportActionBar(toolbarAddWork)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarAddWork.setNavigationOnClickListener{
            finish()
        }
        initView()

    }

    /**
     * 初始化，声明监听函数并显示初始数据
     */
    @SuppressLint("SetTextI18n")
    private fun initView() {
        chooseCourseName()
        fabSubmitWork.setOnClickListener(this)
        btn_Date.setOnClickListener(this)
        btn_Time.setOnClickListener(this)
        uploadPicturesIcon.setOnClickListener(this) //调用相册
        textViewAddHomeworkContentClear.setOnClickListener(this) //清空作业文字内容
        homeworkPicturesOne.setOnClickListener(this)
        homeworkPicturesTwo.setOnClickListener(this)
        homeworkPicturesThree.setOnClickListener(this)
        textViewDeletePicturesOne.setOnClickListener(this)
        textViewDeletePicturesTwo.setOnClickListener(this)
        textViewDeletePicturesThree.setOnClickListener(this)

        btn_Date.text = "${viewModel.year}-" +
                        "${AssistantApplication.formatStrNumber(viewModel.month)}-" +
                        AssistantApplication.formatStrNumber(viewModel.day)

        btn_Time.text = "${AssistantApplication.formatStrNumber(viewModel.hour)}:" +
                        AssistantApplication.formatStrNumber(viewModel.minute)
    }

    /**
     * 添加菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toobar_add_work, menu)
        return true
    }

    /**
     * 添加菜单选择处理函数
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.takePhoto -> takePhoto()
            R.id.clearEditingWork -> clearEdit()
        }
        return true
    }

    /**
     * 清除正在编辑的所有内容
     */
    @SuppressLint("SetTextI18n")
    private fun clearEdit() {
        editTextHomeworkItem.setText("")
        homeworkPicturesOne.visibility = View.GONE
        homeworkPicturesTwo.visibility = View.GONE
        homeworkPicturesThree.visibility = View.GONE
        textViewDeletePicturesOne.visibility = View.GONE
        textViewDeletePicturesTwo.visibility = View.GONE
        textViewDeletePicturesThree.visibility = View.GONE
        uploadPicturesIcon.visibility = View.VISIBLE
        viewModel.addingWork.PicturesOnePath = ""
        viewModel.addingWork.PicturesTwoPath = ""
        viewModel.addingWork.PicturesThreePath = ""

        btn_Date.text = "${viewModel.year}-" +
                "${AssistantApplication.formatStrNumber(viewModel.month)}-" +
                AssistantApplication.formatStrNumber(viewModel.day)

        btn_Time.text = "${AssistantApplication.formatStrNumber(viewModel.hour)}:" +
                AssistantApplication.formatStrNumber(viewModel.minute)
    }

    /**
     * 提交作业
     */
    private fun submitWork() {

        viewModel.addingWork.content = editTextHomeworkItem.text.toString()  //保存作业文字内容
        viewModel.saveWork()

        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("返回主页") { _, _ ->
            val intent = Intent()
            intent.setClass(this, WorkActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("继续添加作业") { dialogInterface, _ ->
            clearEdit()
            dialogInterface.dismiss()
        }
        builder.setMessage("作业添加成功！请选择：")
        builder.setTitle("提示信息")
        builder.show()
    }

    /**
     * 调用相机
     */
    private fun takePhoto() {
        checkPermissionAndCamera()
    }

    /**
     * 事件点击监听函数
     */
    override fun onClick(p0: View?) {
        when(p0){
            fabSubmitWork -> submitWork()
            btn_Date -> btnDate(btn_Date)
            btn_Time -> btnTime(btn_Time)
            textViewAddHomeworkContentClear -> editTextHomeworkItem.setText("")
            uploadPicturesIcon -> showAlbumAction()
            textViewDeletePicturesOne -> deleteOnePictures()
            textViewDeletePicturesTwo -> deleteTwoPictures()
            textViewDeletePicturesThree -> deleteThreePictures()
            homeworkPicturesOne -> {
                showBigPictures(Uri.parse(viewModel.addingWork.PicturesOnePath))
                viewModel.dia?.show()
            }
            homeworkPicturesTwo -> {
                showBigPictures(Uri.parse(viewModel.addingWork.PicturesTwoPath))
                viewModel.dia?.show()
            }
            homeworkPicturesThree -> {
                showBigPictures(Uri.parse(viewModel.addingWork.PicturesThreePath))
                viewModel.dia?.show()
            }

        }
    }


    /**
     * 删除图片1
     */
    private fun deleteOnePictures() {
        homeworkPicturesOne.visibility = View.GONE
        viewModel.addingWork.PicturesOnePath = ""
        textViewDeletePicturesOne.visibility = View.GONE
    }

    /**
     * 删除图片2
     */
    private fun deleteTwoPictures() {
        homeworkPicturesTwo.visibility = View.GONE
        viewModel.addingWork.PicturesTwoPath = ""
        textViewDeletePicturesTwo.visibility = View.GONE
    }

    /**
     * 删除图片3
     */
    private fun deleteThreePictures() {
        homeworkPicturesThree.visibility = View.GONE
        viewModel.addingWork.PicturesThreePath = ""
        textViewDeletePicturesThree.visibility = View.GONE
    }


    /**
     * 点击查看大图
     */
    fun showBigPictures(uri: Uri) {
        //val context: Context = this
        viewModel.dia = Dialog(this, R.style.edit_AlertDialog_style)
        viewModel.dia!!.setContentView(R.layout.activity_start_dialog)
        val imageView = viewModel.dia!!.findViewById<ImageView>(R.id.start_img)
        imageView.setImageURI(uri)
        imageView.setOnClickListener { viewModel.dia!!.dismiss() }
    }

    /**
     * 检查是否有打开系统相册的权限，没有则动态申请，有则打开相册
     */
    fun showAlbumAction() {

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

    fun btnDate(btn_date:Button){
        // 日期选择器
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
                btn_date.text = mDate
                viewModel.date = mDate
            },
            mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }
    fun btnTime(btn_time:Button){
        // 时间选择器
        val ca = Calendar.getInstance()
        var mHour = ca[Calendar.HOUR_OF_DAY]
        var mMinute = ca[Calendar.MINUTE]

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                val mTime = "${hourOfDay}:${minute}"
                btn_time.text = mTime
                viewModel.time = mTime
            },
            mHour, mMinute, true
        )
        timePickerDialog.show()
    }

    //选择课程名称
    private fun chooseCourseName() {
        val courseNameList = viewModel.getCourseNameList(this)
        if (courseNameList.isEmpty()){
            courseNameList.add("暂无课程名可用，请先完善课程表")
        }
        choose_course_spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_activated_1,
            courseNameList
        )
        choose_course_spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val str: String = courseNameList[i]
                viewModel.addingWork.courseName = str
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                val str: String = courseNameList[0]
                viewModel.addingWork.courseName = str
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                if (viewModel.isAndroidQ) {
                    // Android 10 使用图片uri加载
                    viewModel.mCameraUri?.let { setMyImageUri(it) }
                } else {
                    // 使用图片路径加载
                    addPicturesByPath()
                }
            } else {
                Toast.makeText(this, "取消", Toast.LENGTH_LONG).show()
            }
        }


        //调用相册裁剪
        when (requestCode) {
            viewModel.choosePhoto -> {
                if (resultCode == RESULT_OK) {
                    //处理从系统相册返回的图片
                    if (data == null) { //返回未空，则退出
                        return
                    } else {
                        //调用UCrop开源裁剪库进行裁剪
                        val options = UCrop.Options()
                        //设置toolbar颜色
                        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary))
                        //设置状态栏颜色
                        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimary))
                        options.setCompressionQuality(100)  //设置裁剪后的图片质量，值从0-100。100表示和原图片同样的质量
                        viewModel.uriTempFile = Uri.fromFile(
                            File(
                                cacheDir,
                                "AssistantWork_${viewModel.year}${viewModel.month}${viewModel.day}${viewModel.hour}${viewModel.minute}.png"
                            )
                        )
                        UCrop.of(data.data!!, viewModel.uriTempFile!!).withOptions(options)
                            .withAspectRatio(
                                4f,
                                3f
                            ).start(this)
                    }
                }
            }


            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    saveWorkPictures(data) //保存裁剪后的图片到本地
                }
            }
        }
    }

    /**
     * 保存作业图片内容
     */
    open fun saveWorkPictures(data:Intent?){
        val filePath = AssistantApplication.savePictureToLocal(data)
        if (filePath!=""){
            when {
                viewModel.addingWork.PicturesOnePath == "" -> {
                    homeworkPicturesOne.setImageURI(Uri.parse(filePath))
                    textViewDeletePicturesOne.visibility = View.VISIBLE
                    homeworkPicturesOne.visibility = View.VISIBLE
                    viewModel.addingWork.PicturesOnePath = filePath
                }
                viewModel.addingWork.PicturesTwoPath == "" -> {
                    homeworkPicturesTwo.setImageURI(Uri.parse(filePath))
                    textViewDeletePicturesTwo.visibility = View.VISIBLE
                    homeworkPicturesTwo.visibility = View.VISIBLE
                    viewModel.addingWork.PicturesTwoPath = filePath
                }
                viewModel.addingWork.PicturesThreePath == "" -> {
                    homeworkPicturesThree.setImageURI(Uri.parse(filePath))
                    textViewDeletePicturesThree.visibility = View.VISIBLE
                    homeworkPicturesThree.visibility = View.VISIBLE
                    viewModel.addingWork.PicturesThreePath = filePath
                }
                else -> {
                    Toast.makeText(this, "最多可添加3张图片！", Toast.LENGTH_LONG).show()
                    uploadPicturesIcon.visibility = View.GONE
                }
            }
        }
    }

    open fun addPicturesByPath() {
        when {
            viewModel.addingWork.PicturesOnePath == "" -> {
                homeworkPicturesOne.setImageBitmap(
                    BitmapFactory.decodeFile(
                        viewModel.mCameraImagePath
                    )
                )
                textViewDeletePicturesOne.visibility = View.VISIBLE
                homeworkPicturesOne.visibility = View.VISIBLE
                viewModel.addingWork.PicturesOnePath = viewModel.mCameraImagePath.toString()
            }
            viewModel.addingWork.PicturesTwoPath == "" -> {
                homeworkPicturesTwo.setImageBitmap(
                    BitmapFactory.decodeFile(
                        viewModel.mCameraImagePath
                    )
                )
                textViewDeletePicturesTwo.visibility = View.VISIBLE
                homeworkPicturesTwo.visibility = View.VISIBLE
                viewModel.addingWork.PicturesTwoPath = viewModel.mCameraImagePath.toString()
            }
            viewModel.addingWork.PicturesThreePath == "" -> {
                homeworkPicturesThree.setImageBitmap(
                    BitmapFactory.decodeFile(
                        viewModel.mCameraImagePath
                    )
                )
                textViewDeletePicturesThree.visibility = View.VISIBLE
                homeworkPicturesThree.visibility = View.VISIBLE
                viewModel.addingWork.PicturesThreePath = viewModel.mCameraImagePath.toString()
            }
            else -> {
                Toast.makeText(this, "最多可添加3张图片！", Toast.LENGTH_LONG).show()
                uploadPicturesIcon.visibility = View.GONE
            }
        }
    }


    open fun setMyImageUri(uri: Uri) {
        when {
            viewModel.addingWork.PicturesOnePath == "" -> {
                homeworkPicturesOne.setImageURI(uri)
                textViewDeletePicturesOne.visibility = View.VISIBLE
                homeworkPicturesOne.visibility = View.VISIBLE
                viewModel.addingWork.PicturesOnePath = uri.toString()
            }
            viewModel.addingWork.PicturesTwoPath == "" -> {
                homeworkPicturesTwo.setImageURI(uri)
                textViewDeletePicturesTwo.visibility = View.VISIBLE
                homeworkPicturesTwo.visibility = View.VISIBLE
                viewModel.addingWork.PicturesTwoPath = uri.toString()
            }
            viewModel.addingWork.PicturesThreePath == "" -> {
                homeworkPicturesThree.setImageURI(uri)
                textViewDeletePicturesThree.visibility = View.VISIBLE
                homeworkPicturesThree.visibility = View.VISIBLE
                viewModel.addingWork.PicturesThreePath = uri.toString()
            }
            else -> {
                Toast.makeText(this, "最多可添加3张图片！", Toast.LENGTH_LONG).show()
                uploadPicturesIcon.visibility = View.GONE
            }
        }
    }

    /**
     * 调起相机拍照
     */
    private fun openCamera() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // 判断是否有相机
        if (captureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            var photoUri: Uri? = null
            if (viewModel.isAndroidQ) {
                // 适配android 10
                photoUri = createImageUri()
            } else {
                try {
                    photoFile = createImageFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (photoFile != null) {
                    viewModel.mCameraImagePath = photoFile.absolutePath
                    photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        FileProvider.getUriForFile(this, "$packageName.fileprovider", photoFile)
                    } else {
                        Uri.fromFile(photoFile)
                    }
                }
            }
            viewModel.mCameraUri = photoUri
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(captureIntent, 1)
            }
        }
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private fun createImageUri(): Uri? {
        val status = Environment.getExternalStorageState()
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        return if (status == Environment.MEDIA_MOUNTED) {
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
        } else {
            contentResolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, ContentValues())
        }
    }

    /**
     * 创建保存图片的文件
     */
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val imageName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir!!.exists()) {
            storageDir.mkdir()
        }
        val tempFile = File(storageDir, imageName)
        return if (Environment.MEDIA_MOUNTED != EnvironmentCompat.getStorageState(tempFile)) {
            null
        } else tempFile
    }

     /**
     * 检查权限并拍照。
     * 调用相机前先检查权限。
     */
    fun checkPermissionAndCamera() {
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.CAMERA
        )
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            //有调起相机拍照。
            openCamera()
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                9
            )
        }
    }

    /**
     * 处理权限申请的回调。
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 9) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera()
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_LONG).show()
            }
        }
    }
}

