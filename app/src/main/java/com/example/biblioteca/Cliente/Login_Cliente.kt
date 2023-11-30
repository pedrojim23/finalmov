package com.example.biblioteca.Cliente

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.biblioteca.MainActivity
import com.example.biblioteca.R
import com.example.biblioteca.databinding.ActivityLoginClienteBinding
import com.google.firebase.auth.FirebaseAuth

class Login_Cliente : AppCompatActivity() {

    private lateinit var binding : ActivityLoginClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLoginCliente.setOnClickListener{
            ValidarInformacion()
        }
    }

    private var email = ""
    private var password = ""
    private fun ValidarInformacion() {
        email = binding.EtEmailCliente.text.toString().trim()
        password = binding.EtPasswordCliente.text.toString().trim()

        if (email.isEmpty()){
            binding.EtEmailCliente.error = "Ingrese su correo"
            binding.EtEmailCliente.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailCliente.error = "Correo no valido"
            binding.EtEmailCliente.requestFocus()
        }
        else if(password.isEmpty()){
            binding.EtPasswordCliente.error = "Ingrese su contraseña"
            binding.EtPasswordCliente.requestFocus()
        }
        else{
            LoginCliente()
        }
    }

    private fun LoginCliente() {
        progressDialog.setMessage("Iniciando sesion")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            progressDialog.dismiss()
            startActivity(Intent(this@Login_Cliente, MainActivity::class.java))
            finishAffinity()
        }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No es posible iniciar sesion debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
}