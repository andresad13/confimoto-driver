package com.motuber.motuber_driver


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth// ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        var username = findViewById<EditText>(R.id.reg_username)
        var pass = findViewById<EditText>(R.id.reg_pass)
        auth = FirebaseAuth.getInstance()

        var pass2 = findViewById<EditText>(R.id.reg_pass_2)
        var reg_butt = findViewById<Button>(R.id.reg__butt)
        var reg_photo_butt = findViewById<Button>(R.id.getDocs)


        reg_photo_butt.setOnClickListener {
            startActivity(Intent(this, TakePhotoActivity::class.java))

        }

        reg_butt.setOnClickListener {
            if (!pass.text.toString().equals(pass2.text.toString())){

                Toast.makeText(this, "las contraseñas no coinciden.", Toast.LENGTH_LONG).show()

            }
            else{
                if((pass2.text.toString().equals(""))||(pass.text.toString().equals(""))){
                    Toast.makeText(this, "las contraseñas no pueden estar vacias.", Toast.LENGTH_LONG).show()

                }else if (username.text.toString().isEmailValid()){

                    auth.createUserWithEmailAndPassword(username.text.toString(), pass.text.toString())
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(this, "usuario creado satisfactoriamente.", Toast.LENGTH_LONG).show()
                                    //startActivity(Intent(this, HomeActivity::class.java))
                                    //mDatabase?.child("users")?.child(auth.currentUser?.uid.toString())?.child("email")?.setValue(auth.currentUser?.email.toString())


                                    Log.d(null, "createUserWithEmail:success")
                                    val user = auth.currentUser
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(null, "createUserWithEmail:failure", task.exception)
                                    Toast.makeText(baseContext, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show()
                                }


                            }
                }
                else{
                    Toast.makeText(this, "el correo no es valido", Toast.LENGTH_LONG).show()

                }
            }
        }



        var reg_to_login = findViewById<Button>(R.id.reg_login)
        reg_to_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }

    }

    fun String.isEmailValid() =
            Pattern.compile(
                    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                            "\\@" +
                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                            "(" +
                            "\\." +
                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                            ")+"
            ).matcher(this).matches()
}
