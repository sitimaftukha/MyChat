package com.siti.groupchatsiti.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.siti.groupchatsiti.activity.LoginActivity

class LogoutDialogFragment : androidx.fragment.app.DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Are you sure")
                    .setTitle("Logout")
                    .setPositiveButton("Yes"
                    ) { dialog, id ->
                        logoutUser()
                    }
                    .setNegativeButton("No"
                    ) { dialog, id ->

                    }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent=Intent(context,LoginActivity::class.java)
        startActivity(intent)
    }
}