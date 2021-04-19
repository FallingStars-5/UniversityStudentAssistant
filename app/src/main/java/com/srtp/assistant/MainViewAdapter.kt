package com.srtp.assistant

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.srtp.assistant.logic.model.Work
import com.srtp.assistant.ui.work.workDetail.WorkDetailActivity

class MainViewAdapter(val context: MainActivity, private val mItems: List<Work>):
    RecyclerView.Adapter<MainViewAdapter.WorkItemViewHolder>() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    inner class WorkItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val workId:TextView = itemView.findViewById(R.id.textViewHomeworkItemId)
        val courseName:TextView = itemView.findViewById(R.id.textViewHomeworkItemCourseName)
        val workContent:TextView = itemView.findViewById(R.id.textViewHomeworkContent)
        val restTime:TextView = itemView.findViewById(R.id.textViewHomeworkDetailsRestTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val holder = WorkItemViewHolder(mLayoutInflater.inflate(R.layout.work_item_current,parent,false))
        holder.itemView.setOnClickListener{
            val position = holder.adapterPosition
            val id = mItems[position].firstSaveTimeStamp
            val intent = Intent(parent.context, WorkDetailActivity::class.java).apply {
                putExtra("work_id",id)
            }
            context.startActivity(intent)
            //context.finish()
        }
        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        if (position<5) {
            val work = this.mItems[position]

            holder.workId.text = (position+1).toString()

            //取前6个字符显示
            if (work.courseName.length<7){
                holder.courseName.text = work.courseName
            }else{
                holder.courseName.text = "${work.courseName.substring(0,3)}..${work.courseName.substring(work.courseName.length-2)}"
            }

            if (work.PicturesOnePath!=""||work.PicturesTwoPath!=""||work.PicturesThreePath!=""){
                if (work.content.length<8){
                    holder.workContent.text = "${work.content}[图片]"
                }else{
                    holder.workContent.text = "${work.content.substring(0,8)}..[图片]"
                }
            }else{
                if (work.content.length<12){
                    holder.workContent.text = work.content
                }else{
                    holder.workContent.text = "${work.content.substring(0,11)}.."
                }
            }

            holder.restTime.text = AssistantApplication.getDifferTime(AssistantApplication.getStrTime(work.time))

        }
    }

    override fun getItemCount() = mItems.size
}