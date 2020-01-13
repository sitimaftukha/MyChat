package com.siti.groupchatsiti.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.siti.groupchatsiti.R
import com.siti.groupchatsiti.activity.GroupChatActivity
import com.siti.groupchatsiti.model.GroupNameModel
import kotlinx.android.synthetic.main.group_adpter_layout.view.*

class GroupListAdapter(groupList1: List<GroupNameModel>, mContext: Context?) :
        androidx.recyclerview.widget.RecyclerView.Adapter<GroupListAdapter.UserViewHolder>() {

    var groupList: List<GroupNameModel> = groupList1
    val context = mContext

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): UserViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.group_adpter_layout, viewGroup, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, p1: Int) {
        var groupNametest = groupList[p1].groupName
        groupNametest = groupNametest!!.substring(0, (groupNametest.length - 2))
        Log.e("groupName", groupNametest)
        //holder.groupName.text=groupList[p1].groupName
        holder.groupName.text = groupNametest
    }

    inner class UserViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var groupName = itemView.tv_groupName!!

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            Log.e("position", position.toString() + "  " + groupList[position].groupName)
            //val intent=Intent(this@GroupListAdapter,GroupChatActivity::class.java)
            val intent = Intent(context, GroupChatActivity::class.java)
            intent.putExtra("groupName", groupList[position].groupName)
            context!!.startActivity(intent)
        }
    }
}