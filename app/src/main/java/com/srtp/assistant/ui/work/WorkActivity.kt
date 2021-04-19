package com.srtp.assistant.ui.work

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.srtp.assistant.R
import com.srtp.assistant.ui.work.addWork.AddWorkActivity
import kotlinx.android.synthetic.main.activity_work.*

class WorkActivity : AppCompatActivity(), View.OnClickListener {

    private val viewModel by lazy { ViewModelProvider(this).get(WorkViewModel::class.java) }

    private lateinit var adapter: WorkRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            turnToHome()
        }
        initView()
        showData()
        fabAddWork.setOnClickListener(this)
        swipeRefresh.isRefreshing = false
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            initView()
            swipeRefresh.isRefreshing = false
        }

    }

    /**
     * 初始化显示
     */
    private fun initView(){

        viewModel.workSetting = viewModel.getSavedWorkSettings(this)

        when (viewModel.workSetting.showingWays) {
            0 -> {
                showNotCompletedWork()
            }
            1 -> {
                showCompletedWork()
            }
            else -> {
                showAllWork()
            }
        }
    }


    /**
     * 重写restart方法，当回到该界面时，刷新数据
     */
    override fun onRestart() {
        super.onRestart()
        initView()
        showData()
    }

    private fun showData() {
        val layoutManager = LinearLayoutManager(this)
        workListRecyclerView.layoutManager = layoutManager

        adapter = WorkRecyclerViewAdapter(this, viewModel.workList)
        workListRecyclerView.adapter = adapter
    }

    /**
     * 跳转到主页面
     */
    private fun turnToHome() {
        finish()
    }

    /**
     * 添加菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_work, menu)
        return true
    }

    /**
     * 添加菜单选择处理函数
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_not_completed_work -> showNotCompletedWork()
            R.id.show_completed_work -> showCompletedWork()
            R.id.show_all_work -> showAllWork()
        }
        return true
    }

    /**
     * 显示所有作业
     */
    private fun showAllWork() {
        viewModel.showAllWork()
        viewModel.workSetting.showingWays = 2
        viewModel.saveWorkSettings(this)
        showData()
    }

    /**
     * 只显示已完成作业
     */
    private fun showCompletedWork() {
        viewModel.showCompletedWork()
        viewModel.workSetting.showingWays = 1
        viewModel.saveWorkSettings(this)
        showData()
    }

    /**
     * 只显示未完成作业
     */
    private fun showNotCompletedWork() {
        viewModel.showNotCompletedWork()
        viewModel.workSetting.showingWays = 0
        viewModel.saveWorkSettings(this)
        showData()
    }

    /**
     * 单击事件监听处理函数
     */
    override fun onClick(p0: View?) {
        when (p0){
            fabAddWork -> turnToAddWork()
        }
    }

    /**
     * 跳转到添加作业页面
     */
    private fun turnToAddWork() {
        val intent = Intent(this, AddWorkActivity::class.java)
        startActivity(intent)
    }
}