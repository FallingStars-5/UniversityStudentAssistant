package com.srtp.assistant.ui.work

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.srtp.assistant.AssistantApplication
import com.srtp.assistant.R
import com.srtp.assistant.logic.model.Work
import com.srtp.assistant.ui.work.workDetail.WorkDetailActivity

class WorkRecyclerViewAdapter(val context:WorkActivity, private val mItems: List<Work>):
    RecyclerView.Adapter<WorkRecyclerViewAdapter.WorkItemViewHolder>() {

    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    inner class WorkItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val order:TextView = itemView.findViewById(R.id.textViewHomeworkItemId)
        val courseName:TextView = itemView.findViewById(R.id.textViewHomeworkItemCourseName)
        val workContent:TextView = itemView.findViewById(R.id.textViewHomeworkContent)
        val restTime:TextView = itemView.findViewById(R.id.textViewHomeworkDetailsRestTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val holder = WorkItemViewHolder(mLayoutInflater.inflate(R.layout.work_item,parent,false))
        holder.itemView.setOnClickListener{
            val position = holder.adapterPosition
            val id = mItems[position].firstSaveTimeStamp
            Log.e("dan","传递的的时间戳：${id}")
            val intent = Intent(parent.context, WorkDetailActivity::class.java).apply {
                putExtra("work_id",id)
            }
            context.startActivity(intent)
        }
        return holder
}

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        val work = this.mItems[position]
        holder.order.text = (position+1).toString()

        //取前6个字符显示
        if (work.courseName.length<7){
            holder.courseName.text = work.courseName
        }else{
            holder.courseName.text = "${work.courseName.substring(0,3)}..${work.courseName.substring(work.courseName.length-2)}"
        }

        if (work.PicturesOnePath!=""||work.PicturesTwoPath!=""||work.PicturesThreePath!=""){
            if (work.content.length<14){
                holder.workContent.text = "${work.content}[图片]"
            }else{
                holder.workContent.text = "${work.content.substring(0,14)}..[图片]"
            }
        }else{
            if (work.content.length<17){
                holder.workContent.text = work.content
            }else{
                holder.workContent.text = "${work.content.substring(0,16)}.."
            }
        }

        if (work.isCompleted==0){
            holder.restTime.text = AssistantApplication.getDifferTime(AssistantApplication.getStrTime(work.time))
        }else{
            holder.restTime.text = "已完成"
        }

    }

    override fun getItemCount() = mItems.size
}