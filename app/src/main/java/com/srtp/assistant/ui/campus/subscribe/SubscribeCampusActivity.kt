package com.srtp.assistant.ui.campus.subscribe

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.marginBottom
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.circularreveal.CircularRevealLinearLayout
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textview.MaterialTextView
import com.srtp.assistant.R
import com.srtp.assistant.logic.model.CampusAddress
import kotlinx.android.synthetic.main.activity_subscribe_campus.*

class SubscribeCampusActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(SubscribeCampusViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe_campus)

        setSupportActionBar(toolbar_subscribe_campus)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_subscribe_campus.setNavigationOnClickListener{
            finish()
        }

        setData()
    }

    @SuppressLint("ResourceType", "UseCompatLoadingForColorStateLists")
    private fun setData() {
        contain_of_chips.removeAllViews()
        val sortNameList = viewModel.findAllCampusAddressName()
        //Log.e("dan",sortNameList.toString())
        for (sortName in sortNameList){ //遍历所有分类名称

            val layout = LinearLayoutCompat(this)
            val params = LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

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

            val labelList = viewModel.findCampusAddressBySortName(sortName)
            for (label in labelList){ //遍历该分类名称中所有的标签
                val chip = Chip(this)
                chip.text = label.name

                chip.setTextColor(resources.getColorStateList(R.color.campus_label_text))
                chip.chipBackgroundColor = resources.getColorStateList(R.color.campus_label_bg)
                chip.isCheckable = true
                chip.isChecked = label.isSubscribe == 1
                chip.isCheckedIconVisible = false
                chip.isCloseIconVisible = true
                chip.closeIconTint = resources.getColorStateList(R.color.campus_label_cancel_bg)
                chip.setOnCheckedChangeListener { compoundButton, _ ->
                    if (compoundButton.isChecked){
                        label.isSubscribe = 1

                    }else{
                        label.isSubscribe = 0
                    }
                    viewModel.updateCampusAddressById(label)
                }
                chip.setOnCloseIconClickListener {
                    viewModel.deleteCampusAddressById(label.id)
                    setData()
                }

                chipGroup.addView(chip)
            }

            layout.addView(chipGroup)

            contain_of_chips.addView(layout)

        }

    }

    /**
     * 添加菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.campus_subscribe_menu, menu)
        return true
    }

    /**
     * 添加菜单选择处理函数
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.define_campus_label -> defineLabel()               //自定义标签
        }
        return true
    }

    private fun defineLabel() {
        //加载布局文件
        val view = LayoutInflater.from(this).inflate(R.layout.add_new_campus_label, null, false)

        //创建对话框并设置view
        val dialog = AlertDialog.Builder(this).setView(view).create()

        //绑定组件
        val submit:MaterialTextView = view.findViewById(R.id.submit_label)
        val cancel:MaterialTextView = view.findViewById(R.id.cancel_label)
        val name:MaterialAutoCompleteTextView = view.findViewById(R.id.website_name)
        val address: MaterialAutoCompleteTextView = view.findViewById(R.id.website_address)
        val defineLabel: MaterialAutoCompleteTextView = view.findViewById(R.id.define_sort)
        val sortSpinner: AppCompatSpinner = view.findViewById(R.id.choose_sort_spinner)
        val defineLayoutSort: CircularRevealLinearLayout = view.findViewById(R.id.define_layout_sort)

        val campusAddress = CampusAddress()

        //设置分类名称
        val sortNameList = viewModel.findAllCampusAddressName() as ArrayList
        sortNameList.add("自定义分类名称")
        sortSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_activated_1,
            sortNameList
        )
        sortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (sortNameList[p2]!="自定义分类名称"){
                        campusAddress.sortName = sortNameList[p2]
                        defineLayoutSort.visibility = View.GONE
                    }else{
                        defineLayoutSort.visibility = View.VISIBLE
                    }

                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    if (sortNameList[0]!="自定义分类名称"){
                        campusAddress.sortName = sortNameList[0]
                        defineLayoutSort.visibility = View.GONE
                    }else{
                        defineLayoutSort.visibility = View.VISIBLE
                    }
                }
            }



        submit.setOnClickListener {

            campusAddress.isSubscribe = 1 //默认订阅

            if (defineLayoutSort.visibility == View.VISIBLE){
                 campusAddress.sortName = defineLabel.text.toString()
            }

            campusAddress.name = name.text.toString()
            campusAddress.address = address.text.toString()

            viewModel.addCampusAddress(campusAddress)

            setData()

            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show() //显示对话框

        //设置对话框的长和宽
        dialog.window?.setLayout(
            this.resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels * 1 / 2
        )

    }

}