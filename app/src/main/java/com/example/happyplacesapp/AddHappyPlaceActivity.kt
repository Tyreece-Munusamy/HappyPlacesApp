package com.example.happyplacesapp

import android.Manifest
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity() , View.OnClickListener {

    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        setSupportActionBar(findViewById(R.id.toolbar_add_place))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toolbar_add_place = findViewById<Toolbar>(R.id.toolbar_add_place)
        toolbar_add_place.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDateInView()
            }

        findViewById<AppCompatEditText>(R.id.et_date).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_add_image).setOnClickListener(this)
    }
//boom
    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(this@AddHappyPlaceActivity
                    , dateSetListener
                    , calendar.get(Calendar.YEAR)
                    , calendar.get(Calendar.MONTH)
                    , calendar.get(Calendar.DAY_OF_MONTH)).show()
            }

            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(
                    pictureDialogItems
                ) { dialog, which ->
                    when (which) {
                        // Here we have create the methods for image selection from GALLERY
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(this@AddHappyPlaceActivity,"Camera selection coming soon...", Toast.LENGTH_SHORT).show()
                    }
                }
                pictureDialog.show()
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        findViewById<AppCompatEditText>(R.id.et_date).setText(sdf.format(calendar.time).toString())
    }

    private fun choosePhotoFromGallery() {
        // TODO(Step 6 : Asking the permissions of Storage using DEXTER Library which we have added in gradle file.)
        // START
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {

                        Toast.makeText(this@AddHappyPlaceActivity,"Storage READ/WRITE permission are granted. Now you can select an image from GALLERY or lets says phone storage.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

}