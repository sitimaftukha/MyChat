package com.siti.groupchatsiti.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.siti.groupchatsiti.R
import com.siti.groupchatsiti.adapter.AddUserToGroupAdapter
import com.siti.groupchatsiti.model.GroupUserModel
import com.siti.groupchatsiti.model.UserModel
import kotlinx.android.synthetic.main.activity_add_user_to_group.*
import java.util.*

class AddUserToGroupActivity : AppCompatActivity(), androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing=false
    }

    private var recyclerViewLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    private var progressBar: ProgressDialog? = null
    internal lateinit var allGroupUserList: List<GroupUserModel>
    internal lateinit var allUserModelList: List<UserModel>
    private var groupName: String = ""
    var userName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_to_group)
        supportActionBar!!.hide()
        groupName = intent.getStringExtra("groupName")
        getAllUser()
        swipeRefreshLayout.setOnRefreshListener(this)
        getAllGroupUser()
        iv_backUserList.setOnClickListener {
            super@AddUserToGroupActivity.onBackPressed()
        }
        recyclerViewLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView_addNewMemberForGroup.layoutManager = recyclerViewLayoutManager
    }

    private fun getAllUser() {
        val rootRef = FirebaseDatabase.getInstance().reference
        val usersdRef = rootRef.child("User")
        progressBar = ProgressDialog(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        progressBar!!.setMessage("Fetching all user......")
        progressBar!!.setCancelable(false)
       // progressBar!!.show()
        usersdRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allUserModelList = ArrayList()
                var userModel: UserModel
                for (ds in dataSnapshot.children) {
                    val emailcheck = ds.child("email").getValue(String::class.java)
                    val name = ds.child("name").getValue(String::class.java)
                    val email = ds.child("email").getValue(String::class.java)
                    Log.e("User", email)
                    userModel = UserModel(name, email)
                    (allUserModelList as ArrayList<UserModel>).add(userModel)

                }
                Log.e("All User", "" + allUserModelList)
               // progressBar!!.dismiss()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                progressBar!!.dismiss()
                Log.d("onCancelled", "onCancelled")
            }
        })
    }

    private fun getAllGroupUser() {
        val rootRef = FirebaseDatabase.getInstance().reference
        val usersdRef = rootRef.child("Group")
        progressBar = ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        progressBar!!.setMessage("Fetching all user......")
        progressBar!!.setCancelable(true)
       // progressBar!!.show()
        usersdRef.child(intent.getStringExtra("groupName")).child("user")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        allGroupUserList = ArrayList()
                        for (ds in dataSnapshot.children) {
                            val name = ds.child("name").getValue(String::class.java)
                            val email = ds.child("email").getValue(String::class.java)
                            val admin = ds.child("adminTxt").getValue(String::class.java)
                            val userActive=ds.child("activeUser").getValue(String::class.java)
                            val groupUserModel=GroupUserModel(email,name,admin,userActive)
                            //val showAllGroupMemberModel = ShowAllGroupMemberModel(email, name, admin)
                            ((allGroupUserList as ArrayList<GroupUserModel>).add(groupUserModel))
                        }
                        setDataToAdapter()
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        progressBar!!.dismiss()
                    }
                })
       // progressBar!!.dismiss()
    }

    private fun setDataToAdapter() {
        val addUserToGroupAdapter=AddUserToGroupAdapter(allUserModelList,allGroupUserList,this,groupName)
        recyclerView_addNewMemberForGroup.adapter=addUserToGroupAdapter
       // progressBar!!.dismiss()
    }
}
