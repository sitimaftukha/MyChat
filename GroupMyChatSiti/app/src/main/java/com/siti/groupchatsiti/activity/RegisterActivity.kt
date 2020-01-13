package com.siti.groupchatsiti.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.siti.groupchatsiti.R
import com.siti.groupchatsiti.model.SignUpModel
import com.siti.groupchatsiti.utility.Utility
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressDialog? = null
    private var UserCancel: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val animationDrawable = relatif2.getBackground() as AnimationDrawable

        // kodingan untuk membuat animasi backgroudnnya bergerak
        animationDrawable.setEnterFadeDuration(3000)
        animationDrawable.setExitFadeDuration(3000)
        animationDrawable.start()


        mAuth = FirebaseAuth.getInstance()
        btnRegister!!.setOnClickListener(this)

        btnCancel.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

    }

    override fun onClick(v: View?) {
        val name: String = edtfullname.text.toString().trim()
        val email: String = edit_email!!.text.toString().trim()
        val pass: String = edit_password!!.text.toString().trim()
        if (checkValidation()) {
            if (Utility.isOnline(this)) {
                progressBar = ProgressDialog(this@RegisterActivity, android.app.AlertDialog
                        .THEME_DEVICE_DEFAULT_LIGHT)
                progressBar!!.setTitle("Creating New Account")
                progressBar!!.setMessage("Please wait, while we're creating an new account for you")
                progressBar!!.setCancelable(false)
                progressBar!!.show()
                val signUpModel = SignUpModel(name, email, pass)
                Log.e("test", "test")
                saveData(signUpModel)
            }
        }
    }

    private fun saveData(signUpModel: SignUpModel) {
        mAuth!!.createUserWithEmailAndPassword(signUpModel.getEmail()!!, signUpModel.getPassword()!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e("Success", "Sign up success")
                        saveDataOnFirebase(signUpModel)
                    } else {
                        progressBar!!.dismiss()
                        Log.e("Fail", "Sign up fail")
                    }
                }
    }

    private fun saveDataOnFirebase(signUpModel: SignUpModel) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        var email: String = signUpModel.getEmail()!!
        email = email.replace(".", "")
        myRef.child("User").child(email).setValue(signUpModel).addOnSuccessListener {
            Log.e("saveDataOnFirebase", "saveDataOnFirebase success")
            progressBar!!.dismiss()
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }.addOnFailureListener {
            progressBar!!.dismiss()
            Log.e("saveDataOnFirebase", "saveDataOnFirebase fail")
        }
    }

    private fun checkValidation(): Boolean {
        if (edtfullname.text.toString().trim() == "") {
            Toast.makeText(this, "User name must be require", Toast.LENGTH_SHORT).show()
            return false
        } else if (edit_email!!.text.toString().trim() == "") {
            Toast.makeText(this, "Email id must be require", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Utility.isValidEmail(edit_email!!.text.toString().trim())) {
            Toast.makeText(this, "Please enter valid email id", Toast.LENGTH_SHORT).show()
            return false
        } else if (edit_password!!.text.toString().trim() == "") {
            Toast.makeText(this, "Password must be require", Toast.LENGTH_SHORT).show()
        } else if (edit_password!!.text.toString().trim().length < 5) {
            Toast.makeText(this, "Password must be 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}
