package com.siti.groupchatsiti.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.siti.groupchatsiti.R
import com.siti.groupchatsiti.utility.Utility
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var login: Button
    private var signUp: TextView? = null
    private lateinit var emailEt: EditText
    private var passwordEt: EditText? = null
    private var auth: FirebaseAuth? = null
    private var progressBar: ProgressDialog? = null

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val animationDrawable = rootLayout.getBackground() as AnimationDrawable

        // kodingan untuk membuat animasi backgroudnnya bergerak
        animationDrawable.setEnterFadeDuration(3000)
        animationDrawable.setExitFadeDuration(3000)
        animationDrawable.start()


        auth = FirebaseAuth.getInstance()
        val currentUser = auth!!.currentUser
        if (currentUser != null) {
            Log.e("email ", "" + currentUser.email)
            val i = Intent(baseContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }
        Log.e("mAuth", "mAuth")
        this.findViewById()
        signUp!!.setOnClickListener(this)
        login.setOnClickListener(this)
        val signin = findViewById<View>(R.id.sign_in_button) as SignInButton
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signin.setOnClickListener {
            v: View? -> signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completeTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completeTask.getResult(ApiException::class.java)
            updateUI(account)
        }catch (e: ApiException){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI(account: GoogleSignInAccount?) {
        val target = Intent(this, MainActivity::class.java)
        target.putExtra("email", account!!.email.toString())
        FirebaseAuth.getInstance().signOut()
        startActivity(target)
        finish()
    }

    private fun findViewById() {
        login = findViewById(R.id.btSignIn)
        signUp = findViewById(R.id.text_register)
        passwordEt = findViewById(R.id.emailinput)
        emailEt = findViewById(R.id.passwordinput)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btSignIn -> {
                val email: String = emailEt.text.toString().trim()
                val password: String = passwordEt!!.text.toString().trim()
                if (checkValidation(email, password)) {
                    progressBar = ProgressDialog(this@LoginActivity, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    progressBar!!.setMessage("Please wait......")
                    progressBar!!.setCancelable(false)
                    progressBar!!.show()
                    userLogin(email, password)
                }
            }
            R.id.text_register -> {
                val i = Intent(baseContext, RegisterActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun checkValidation(email: String, password: String): Boolean {
        if (email == "") {
            Toast.makeText(this, "Email id must be require", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Utility.isValidEmail(email)) {
            Toast.makeText(this, "Please enter valid email id", Toast.LENGTH_SHORT).show()
            return false
        } else if (password == "") {
            Toast.makeText(this, "Password must be require", Toast.LENGTH_SHORT).show()
        } else if (password.length < 5) {
            Toast.makeText(this, "Password must be 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun userLogin(email: String, password: String) {
        auth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e("userLogin", "success")
                        progressBar!!.dismiss()
                        val i = Intent(baseContext, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        progressBar!!.dismiss()
                        Toast.makeText(this, "Please enter correct email and password", Toast.LENGTH_SHORT).show()
                        Log.e("userLogin", "fail ")
                    }
                }
    }
}
