package com.siti.groupchatsiti.fragment

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.siti.groupchatsiti.R
import com.siti.groupchatsiti.adapter.UserListAdapter
import com.siti.groupchatsiti.model.UserDetailModel
import com.siti.groupchatsiti.utility.Utility
import com.siti.groupchatsiti.utility.Utility.Companion.isOnline
import kotlinx.android.synthetic.main.userlist_layout.*
import java.util.*

class UserListFragment : androidx.fragment.app.Fragment() {

    internal lateinit var userList: List<UserDetailModel>
    internal var auth: FirebaseAuth? = null
    private var userEmail: String = ""
    private var progressBar: ProgressDialog? = null
    private lateinit var recyclerViewLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.userlist_layout, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (!isOnline(context!!)  || isOnline(context!!)) {
            recyclerViewUserList.visibility = View.VISIBLE
            tv_noInternet.visibility = View.GONE
            auth = FirebaseAuth.getInstance()
            val currentUser = auth!!.currentUser
            if (currentUser != null) {
                userEmail = currentUser.email!!
            }
            recyclerViewLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            recyclerViewUserList.layoutManager = recyclerViewLayoutManager
            getAllUser()
        } else {
           /* tv_noInternet.visibility = View.VISIBLE
            recyclerViewUserList.visibility = View.GONE*/
        }
    }

    private fun getAllUser() {
        val rootRef = FirebaseDatabase.getInstance().reference
        val usersdRef = rootRef.child("User")
        progressBar = ProgressDialog(context, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        progressBar!!.setMessage("Fetching all user......")
        progressBar!!.setCancelable(false)
        progressBar!!.show()

        usersdRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var userDetailModel: UserDetailModel
                userList = ArrayList()
                for (ds in dataSnapshot.children) {
                    val emailcheck: String = ds.child("email").getValue(String::class.java)!!
                    if (emailcheck == userEmail) {
                        Log.e("user email", "" + emailcheck)
                        val name: String = ds.child("name").getValue(String::class.java)!!
                        Utility.saveCurrentUserName(context, "user_name", name)
                    } else {
                        Log.e("user email", "" + emailcheck)
                        val name: String = ds.child("name").getValue(String::class.java)!!
                        userDetailModel = UserDetailModel(emailcheck, name)
                        (userList as ArrayList<UserDetailModel>).add(userDetailModel)
                    }
                }
                setDataToAdapter()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                progressBar!!.dismiss()
                Log.d("onCancelled", "onCancelled")
            }
        })
    }

    private fun setDataToAdapter() {
        val userListAdapter = UserListAdapter(context, userList)
        if (recyclerViewUserList != null)
            recyclerViewUserList.adapter = userListAdapter
        progressBar!!.dismiss()
    }
}