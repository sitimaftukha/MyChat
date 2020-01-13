package com.siti.groupchatsiti.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.siti.groupchatsiti.R
import com.siti.groupchatsiti.activity.UserChatActivity
import com.siti.groupchatsiti.model.UserDetailModel

class UserListAdapter(context: Context?, userList1: List<UserDetailModel>) :
        androidx.recyclerview.widget.RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    var userList: List<UserDetailModel> = userList1
    private var mContext = context

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): UserViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.user_adapter_layout, viewGroup, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, p1: Int) {
        holder.userName.text = userList[p1].name
        holder.userEmail.text = userList[p1].email
    }

    inner class UserViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var userName: TextView = itemView.findViewById(R.id.tv_userName)
        var userEmail: TextView = itemView.findViewById(R.id.tv_userEmail)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            Log.e("position", "" + position)
            val name: String = userList[position].name
            val email: String = userList[position].email
            val i = Intent(mContext, UserChatActivity::class.java)
            i.putExtra("email", email)
            i.putExtra("name", name)
            mContext!!.startActivity(i)
        }
    }
}