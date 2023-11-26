package com.example.biblioteca.Administrador

import android.app.ProgressDialog
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.biblioteca.MainActivity
import com.example.biblioteca.R
import com.example.biblioteca.databinding.ActivityLoginAdminBinding
import com.google.firebase.auth.FirebaseAuth

class Login_admin : AppCompatActivity() {

    private lateinit var binding: ActivityLoginAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLoginAdmin.setOnClickListener{
            ValidarInformacion()
        }
    }

    private var email = ""
    private var password = ""
    private fun ValidarInformacion() {
        email = binding.EtEmailAdmin.text.toString().trim()
        password = binding.EtPasswordAdmin.text.toString().trim()

        if (email.isEmpty()){
            binding.EtEmailAdmin.error = "Ingrese su correo"
            binding.EtEmailAdmin.requestFocus()
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailAdmin.error = "Correo no valido"
            binding.EtEmailAdmin.requestFocus()
        }
        else if(password.isEmpty()){
            binding.EtPasswordAdmin.error = "Ingrese su contraseña"
            binding.EtPasswordAdmin.requestFocus()
        }
        else{
            LoginAdmin()
        }
    }

    private fun LoginAdmin() {
        progressDialog.setMessage("Iniciando sesion")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            progressDialog.dismiss()
            startActivity(Intent(this@Login_admin, MainActivity::class.java))
            finishAffinity()
        }
        .addOnFailureListener {e->
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "No es posible iniciar sesion debido a ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }
}