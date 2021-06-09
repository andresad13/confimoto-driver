package com.motuber.motuber_driver

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker

lateinit var controlImage: Number
lateinit var photodni1: ImageView
lateinit var take1: Button
lateinit var photodni2: ImageView
lateinit var take2: Button
lateinit var photolic1: ImageView
lateinit var takelic1: Button
lateinit var photolic2: ImageView
lateinit var takelic2: Button
lateinit var photoprop1: ImageView
lateinit var takeprop1: Button
lateinit var photoprop2: ImageView
lateinit var takeprop2: Button

class TakePhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)
         take1 = findViewById<Button>(R.id.takephot1)
         photodni1 = findViewById<ImageView>(R.id.dni1)
        take2 = findViewById<Button>(R.id.takephot2)
        photodni2 = findViewById<ImageView>(R.id.dni2)
        takelic1 = findViewById<Button>(R.id.takelicencia1)
        photolic1 = findViewById<ImageView>(R.id.licencia1)
        takelic2 = findViewById<Button>(R.id.takelicencia2)
        photolic2 = findViewById<ImageView>(R.id.licencia2)
        takeprop1 = findViewById<Button>(R.id.takepropidad1)
        photoprop1 = findViewById<ImageView>(R.id.propiedad1)
        takeprop2 = findViewById<Button>(R.id.takepropidad2)
        photoprop2 = findViewById<ImageView>(R.id.propiedad2)


        take1.setOnClickListener {

            controlImage = 1
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        take2.setOnClickListener {

            controlImage = 2
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        takelic1.setOnClickListener {

            controlImage = 3
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        takelic2.setOnClickListener {

            controlImage = 4
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        takeprop1.setOnClickListener {

            controlImage = 5
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        takeprop2.setOnClickListener {

            controlImage = 6
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions

            when(controlImage){
                1 -> photodni1.setImageURI(uri)
                2 -> photodni2.setImageURI(uri)
                3 -> photolic1.setImageURI(uri)
                4 -> photolic2.setImageURI(uri)
                5 -> photoprop1.setImageURI(uri)
                6 -> photoprop2.setImageURI(uri)
            }


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}