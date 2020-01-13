package com.siti.groupchatsiti.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.siti.groupchatsiti.R
import com.siti.groupchatsiti.adapter.CreateGroupAdapter
import com.siti.groupchatsiti.model.GroupUserModel
import com.siti.groupchatsiti.model.UserModel
import com.siti.groupchatsiti.utility.Utility.Companion.getCurrentUser
import kotlinx.android.synthetic.main.activity_create_group.*
import java.io.Serializable

class CreateGroupActivity : AppCompatActivity(), View.OnClickListener, CreateGroupAdapter.CheckBoxData {

    private var progressBar: ProgressDialog? = null
    internal lateinit var userModelList: List<UserModel>
    private lateinit var recyclerViewLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
     var userName : String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        supportActionBar!!.hide()
        iv_next.setOnClickListener(this)
        iv_backUserList1.setOnClickListener {
            super@CreateGroupActivity.onBackPressed()
        }
        recyclerViewLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
        recyclerView_Newgroup.layoutManager = recyclerViewLayoutManager
        getAllUser()
    }

    private fun getAllUser() {
        val rootRef = FirebaseDatabase.getInstance().reference
        val usersdRef = rootRef.child("User")
        progressBar = ProgressDialog(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        progressBar!!.setMessage("Fetching all user......")
        progressBar!!.setCancelable(false)
        progressBar!!.show()
        usersdRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userModelList = ArrayList()
                var userModel: UserModel
                for (ds in dataSnapshot.children) {
                    val emailcheck = ds.child("email").getValue(String::class.java)
                    if (emailcheck.equals(getCurrentUser())) {
                        userName= ds.child("name").getValue(String::class.java)
                        Log.e("currentUser", "" + getCurrentUser())
                    } else {
                        val name = ds.child("name").getValue(String::class.java)
                        val email = ds.child("email").getValue(String::class.java)
                        Log.e("User", email)
                        userModel = UserModel(name, email)
                        (userModelList as ArrayList<UserModel>).add(userModel)
                    }
                }
                setDataToAdapter()
                Log.e("All User", "" + userModelList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                progressBar!!.dismiss()
                Log.d("onCancelled", "onCancelled")
            }
        })
    }

    private fun setDataToAdapter() {
        val createGroupAdapter = CreateGroupAdapter(userModelList)
        createGroupAdapter.setListener(this)
        recyclerView_Newgroup.adapter = createGroupAdapter
        progressBar!!.dismiss()
    }

    override fun checkData(position: Int, isCheck: Boolean) {
        userModelList[position].isSelected = isCheck
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_next ->{
                checkGroupMemberList()
            }
        }
    }

    private fun checkGroupMemberList() {
        val selectedGroupList=ArrayList<GroupUserModel>()
        for (model in userModelList) {
            Log.e("model", model.toString())
           if(model.isSelected){
               val groupUserModel=GroupUserModel(model.email,model.name,"","yes")
               selectedGroupList.add(groupUserModel)
           }
        }
        if(selectedGroupList.size>0){
            val groupUserModel=GroupUserModel(getCurrentUser(),userName,"admin","yes")
            selectedGroupList.add(groupUserModel)
            Toast.makeText(applicationContext, " user  "+selectedGroupList.size, Toast.LENGTH_SHORT).show()
            val i = Intent(this, GroupNameActivity::class.java)
            i.putExtra("userList",selectedGroupList as Serializable)
            startActivity(i)
            finish()
        }else{
            Toast.makeText(applicationContext, "Please select at least one user", Toast.LENGTH_SHORT).show()
        }
    }
}