package com.siti.groupchatsiti.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.siti.groupchatsiti.R
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.util.HashMap

class SettingActivity : AppCompatActivity() {

    private var UpdateSettingButten: Button? = null
    private var NameSetting: EditText? = null
    private var StatueSetting: EditText? = null
    private var ProfilePic: CircleImageView? = null
    private var CurrentUSerID: String? = null
    private var mAuth_set: FirebaseAuth? = null
    private var RootRef: DatabaseReference? = null
    private val filePath: Uri? = null
    lateinit var storage: FirebaseStorage
    private var UserProfileImageRef: StorageReference? = null
    private var FilePathSR: StorageReference? = null

    private val PICK_IMAGE_REQUEST = 71


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        InitializeField()
        RetriveUserInfo()
        //        GetImage();
        ProfilePic!!.setOnClickListener { chooseImage() }
        UpdateSettingButten!!.setOnClickListener { UpdateSetting() }
    }

    private fun RetriveUserInfo() {
        RootRef!!.child("Users").child(CurrentUSerID!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.hasChild("Name") && dataSnapshot.hasChild(
                                        "Image"
                                )
                        ) {

                            val RetriveNameInfo = dataSnapshot.child("Name").value!!.toString()
                            val RetriveStatusInfo = dataSnapshot.child("Status").value!!.toString()
                            val RetriveImageInfo = dataSnapshot.child("Image").value!!.toString()
                            NameSetting!!.setText(RetriveNameInfo)
                            StatueSetting!!.setText(RetriveStatusInfo)
                            Picasso.get().load(RetriveImageInfo).into(ProfilePic)


                        } else if (dataSnapshot.exists() && dataSnapshot.hasChild("Name")) {
                            val RetriveNameInfo = dataSnapshot.child("Name").value!!.toString()
                            val RetriveStatusInfo = dataSnapshot.child("Status").value!!.toString()
                            NameSetting!!.setText(RetriveNameInfo)
                            StatueSetting!!.setText(RetriveStatusInfo)

                        } else {
                            Toast.makeText(
                                    this@SettingActivity,
                                    "Please Set and Update your Info",
                                    Toast.LENGTH_SHORT
                            ).show()

                        }
                    }

                    override fun onCancelled(@NonNull databaseError: DatabaseError) {

                    }
                })


    }

    private fun UpdateSetting() {
        val NameSetting_ST = NameSetting!!.text.toString()
        val StatueSetting_ST = StatueSetting!!.text.toString()
        if (TextUtils.isEmpty(NameSetting_ST)) {
            Toast.makeText(this@SettingActivity, "Please write your Name", Toast.LENGTH_SHORT)
                    .show()
        }
        if (TextUtils.isEmpty(StatueSetting_ST)) {
            Toast.makeText(this@SettingActivity, "Please write your Statue", Toast.LENGTH_SHORT)
                    .show()
        } else {
            val profileMap = HashMap<String, Any?>()
            profileMap["UID"] = CurrentUSerID
            profileMap["Name"] = NameSetting_ST
            profileMap["Status"] = StatueSetting_ST
            RootRef!!.child("Users").child(CurrentUSerID!!).updateChildren(profileMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@SettingActivity, "Updated", Toast.LENGTH_SHORT).show()
                            SendUserToMainActivity()

                        } else {

                            Toast.makeText(
                                    this@SettingActivity,
                                    task.exception!!.toString(),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            Toast.makeText(this@SettingActivity, "Updated", Toast.LENGTH_SHORT).show()

        }
    }

    private fun InitializeField() {
        supportActionBar?.title = "Account Setting"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        UpdateSettingButten = findViewById(R.id.update_settings_button)
        NameSetting = findViewById(R.id.set_user_name)
        StatueSetting = findViewById(R.id.set_profile_status)
        ProfilePic = findViewById(R.id.set_profile_image)
        mAuth_set = FirebaseAuth.getInstance()
        CurrentUSerID = mAuth_set!!.currentUser!!.uid
        RootRef = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        //        storageReference = storage.getReference();
        //         forestRef = storageReference.child("images/"+CurrentUSerID+".jpeg");
        UserProfileImageRef = FirebaseStorage.getInstance().reference.child("Profiles Images")
        FilePathSR = UserProfileImageRef!!.child(CurrentUSerID!! + ".jpg")


    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.data != null
        ) {
            val ImageUri = data.data
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            Toast.makeText(this, "Photo Added", Toast.LENGTH_SHORT).show()

            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                FilePathSR!!.putFile(resultUri).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.getException()!!
                    }
                    FilePathSR!!.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downUri = task.result
                        val PP = downUri!!.toString()
                        RootRef!!.child("Users").child(CurrentUSerID!!).child("Image").setValue(PP)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                                this@SettingActivity,
                                                "Image Saved in DataBase",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                                this@SettingActivity,
                                                task.exception!!.toString(),
                                                Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                    }

                    //                    FilePathSR.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    //                        @Override
                    //                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    //
                    //                            if (task.isSuccessful()){
                    //                                final String PPdownloadUrl=task.getResult().getStorage().getDownloadUrl().toString();
                    //                                RootRef.child("Users").child(CurrentUSerID).child("Image").setValue(PPdownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                    //                                    @Override
                    //                                    public void onComplete(@NonNull Task<Void> task) {
                    //                                    if(task.isSuccessful()){
                    //                                        Toast.makeText(SettingActivity.this, "Image Saved in DataBase", Toast.LENGTH_SHORT).show();
                    //                                    }     else {
                    //                                        Toast.makeText(SettingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    //                                    }
                    //                                    }
                    //                                });
                    //                                Toast.makeText(SettingActivity.this, "your Profile Picture uploaded Sucessfuly ", Toast.LENGTH_SHORT).show();
                    //
                    //                            }else {
                    //                                Toast.makeText(SettingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    //
                    //                            }
                }

            }

        }


        //            filePath = data.getData();
        //            try {
        //                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
        //                ProfilePic.setImageBitmap(bitmap);
        //                uploadImage();
        //            }
        //            catch (IOException e)
        //            {
        //                e.printStackTrace();
        //            }
    }

    //    private void uploadImage() {
    //
    //        if(filePath != null)
    //        {
    //            final ProgressDialog progressDialog = new ProgressDialog(this);
    //            progressDialog.setTitle("Uploading...");
    //            progressDialog.show();
    //
    //            StorageReference ref = storageReference.child("images/"+CurrentUSerID);
    //            ref.putFile(filePath)
    //                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
    //                        @Override
    //                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
    //                            progressDialog.dismiss();
    //                            Toast.makeText(SettingActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
    //                        }
    //                    })
    //                    .addOnFailureListener(new OnFailureListener() {
    //                        @Override
    //                        public void onFailure(@NonNull Exception e) {
    //                            progressDialog.dismiss();
    //                            Toast.makeText(SettingActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
    //                        }
    //                    })
    //                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
    //                        @Override
    //                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
    //                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
    //                                    .getTotalByteCount());
    //                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
    //                        }
    //                    });
    //        }
    //    }

    private fun SendUserToMainActivity() {
        val SettoMainActivityIntent = Intent(this@SettingActivity, MainActivity::class.java)
        SettoMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(SettoMainActivityIntent)
        finish()
    }
}
