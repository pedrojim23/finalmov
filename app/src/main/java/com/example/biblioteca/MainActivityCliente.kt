package com.example.biblioteca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.biblioteca.databinding.ActivityMainBinding
import com.example.biblioteca.databinding.ActivityMainClienteBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivityCliente : AppCompatActivity() {

    private lateinit var binding : ActivityMainClienteBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        ComprobarSesion()
        //VerFragmentoDashboard()
    }

    private fun ComprobarSesion(){
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            startActivity(Intent(this, Eligir_rol::class.java))
            finishAffinity()
        }
        else{
            Toast.makeText(applicationContext, "Bienvenido ${firebaseUser.email}", Toast.LENGTH_SHORT).show()
        }
    }
}