package com.example.biblioteca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.biblioteca.Fragmentos_Admin.Fragment_Admin_cuenta
import com.example.biblioteca.Fragmentos_Admin.Fragment_admin_dashboard
import com.example.biblioteca.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        ComprobarSesion()
        VerFragmentoDashboard()

        binding.BottomNvAdmin.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.Menu_panel->{
                    VerFragmentoDashboard()
                    true
                }
                R.id.Menu_cuenta->{
                    VerFragmentoCuenta()
                    true
                }
                else->{
                    false
                }
            }
        }
    }

    private fun VerFragmentoDashboard(){
        val nombre_titulo = "Dashboard"
        binding.TituloRLAdmin.text = nombre_titulo

        val fragment = Fragment_admin_dashboard()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment dashboard")
        fragmentTransaction.commit()
    }

    private fun VerFragmentoCuenta(){
        val nombre_titulo = "Mi cuenta"
        binding.TituloRLAdmin.text = nombre_titulo

        val fragment = Fragment_Admin_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsAdmin.id, fragment, "Fragment mi cuenta")
        fragmentTransaction.commit()
    }

    private fun ComprobarSesion(){
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            startActivity(Intent(this, Eligir_rol::class.java))
            finishAffinity()
        }
        else{
            //Toast.makeText(applicationContext, "Bienvenido ${firebaseUser.email}", Toast.LENGTH_SHORT).show()
        }
    }
}