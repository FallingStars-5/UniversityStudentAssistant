package com.srtp.assistant.ui.campus

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.srtp.assistant.R
import com.srtp.assistant.ui.campus.showingCampus.ShowingCampusActivity
import com.srtp.assistant.ui.campus.subscribe.SubscribeCampusActivity
import com.srtp.assistant.ui.campus.subscribe.SubscribeCampusViewModel
import kotlinx.android.synthetic.main.activity_campus.*
import kotlinx.android.synthetic.main.campus_main.*


class CampusActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(SubscribeCampusViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campus)

        setSupportActionBar(toolbarCampus)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarCampus.setNavigationOnClickListener{
            finish()
        }

        setData()
    }

    private fun turnToShowingPage(id:Long) {
        val intent = Intent(this,ShowingCampusActivity::class.java)
        intent.putExtra("campus_address_id",id)
        startActivity(intent)
    }

    /**
     * 添加菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.campus_menu, menu)
        return true
    }

    /**
     * 添加菜单选择处理函数
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.subscribe -> subscribeCampus()               //订阅
        }
        return true
    }

    /**
     * 跳转到订阅页面
     */
    private fun subscribeCampus() {
        val intent = Intent(this,SubscribeCampusActivity::class.java)
        startActivity(intent)
    }

    override fun onRestart() {
        setData()
        super.onRestart()
    }


    @SuppressLint("ResourceType", "UseCompatLoadingForColorStateLists")
    private fun setData() {
        campus_main_layout.removeAllViews()
        val sortNameList = viewModel.findAllCampusAddressName()
        for (sortName in sortNameList){ //遍历所有分类名称

            val layout = LinearLayoutCompat(this)
            val params = LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            layout.orientation = LinearLayoutCompat.VERTICAL
            layout.layoutParams = params
            layout.setPadding(0,0,0,50)

            val text = MaterialTextView(this)
            text.text = sortName
            text.textSize = resources.getDimension(R.dimen.campus_name_text_size)
            text.setTextColor(resources.getColor(R.color.colorThemeFont))

            layout.addView(text)

            val chipGroup = ChipGroup(this)
            chipGroup.isSingleSelection = false

            var isHaveLabel = false

            val labelList = viewModel.findCampusAddressBySortName(sortName)
            for (label in labelList){ //遍历该分类名称中所有的标签
                if (label.isSubscribe == 1){
                    isHaveLabel = true
                    val chip = Chip(this)
                    chip.text = label.name
                    chip.setTextColor(resources.getColorStateList(R.color.campus_label_text))
                    chip.chipBackgroundColor = resources.getColorStateList(R.color.campus_label_bg)
                    chip.isCheckable = false
                    chip.isChecked = true
                    chip.isCheckedIconVisible = false
                    chip.setOnClickListener {
                        turnToShowingPage(label.id)
                    }
                    chipGroup.addView(chip)
                }
            }

            layout.addView(chipGroup)

            if (isHaveLabel){
                campus_main_layout.addView(layout)
            }
        }

    }
}