package com.example.biblioteca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.biblioteca.Administrador.Registrar_admin
import com.example.biblioteca.Cliente.Registro_Cliente
import com.example.biblioteca.databinding.ActivityEligirRolBinding

class Eligir_rol : AppCompatActivity() {

    private lateinit var binding : ActivityEligirRolBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEligirRolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnRolAdministrador.setOnClickListener(){
            //Toast.makeText(applicationContext, "Rol administrador", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@Eligir_rol, Registrar_admin::class.java))
        }

        binding.BtnRolCliente.setOnClickListener(){
            //Toast.makeText(applicationContext, "Rol cliente", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@Eligir_rol, Registro_Cliente::class.java))
        }

    }
}