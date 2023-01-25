package com.kull.simplesense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.kull.simplesense.models.*
import com.kull.simplesense.providers.ClientProvider
import com.kull.simplesense.providers.UsersProvider
import com.kull.simplesense.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var txtEmail: EditText? = null
    var txtPassword: EditText? = null
    var usersProvider = UsersProvider()
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SimpleSense)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtEmail = findViewById(R.id.txtLoginUser)
        txtPassword = findViewById(R.id.txtLoginPassword)
        progressBar = findViewById(R.id.progressBarLogin)
        progressBar.visibility = View.GONE
        var btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin?.setOnClickListener{
            login()
        }
    }

    private fun login() {
        val email = txtEmail?.text.toString().trim()
        val password = txtPassword?.text.toString()

        val loginRequest = LoginRequest(email = "${email}", password = "${password}")

        if (isValidForm(email, password)) {
            progressBar.visibility = View.VISIBLE
            usersProvider.login(loginRequest)?.enqueue(object: Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response != null) {
                        progressBar.visibility = View.GONE
                        if (response.body()?.token != null) {
                            Toast.makeText(this@MainActivity, "El usuario inició sesión correctamente", Toast.LENGTH_LONG).show()
                            Log.e("RESPONSE_BODY", "response: ${response.body()?.token}")
                            saveUserInSession(response.body()?.token.toString())
                            startActivity(Intent(this@MainActivity, QRScanActivity::class.java))
                        } else {
                            Toast.makeText(this@MainActivity, "Error, usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
                            Log.e("RESPONSE_FAILED", "response: ${response.body()?.token}")
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, "Error, no se obtuvo respuesta", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("Login", "Error: ${t.message}")
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(this@MainActivity, "Error, no se pudo iniciar sesisón", Toast.LENGTH_LONG).show()
        }
    }

    private fun isValidForm(email: String, password: String): Boolean {
        if (email.isBlank()) {
            return false
        }
        if (password.isBlank()) {
            return false
        }
        return true
    }

    private fun saveUserInSession(token: String) {
        var user: User = getUserData(token)
        val sharedPref = SharedPref(this)
        sharedPref.save("user", user)
    }

    private fun getUserData(token: String): User {
        var jwt = JWT(token)

        val idUser = jwt.getClaim("id").asString()
        val emailUser = jwt.getClaim("email").asString()
        val nameUser = jwt.getClaim("name").asString()
        val lastnameUser = jwt.getClaim("lastname").asString()
        val rolUser = jwt.getClaim("rol").asString()

        var user = User(id = idUser, email = emailUser, name = nameUser, lastname = lastnameUser, rol = rolUser)
        Log.e("USER", "User: ${user}")
        return user
    }

}