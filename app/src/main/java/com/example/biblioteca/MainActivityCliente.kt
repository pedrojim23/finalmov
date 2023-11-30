package com.example.biblioteca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.biblioteca.Fragmentos_Admin.Fragment_Admin_cuenta
import com.example.biblioteca.Fragmentos_Admin.Fragment_admin_dashboard
import com.example.biblioteca.Fragmentos_Cliente.Fragment_cliente__dashboard
import com.example.biblioteca.Fragmentos_Cliente.Fragment_cliente_cuenta
import com.example.biblioteca.Fragmentos_Cliente.Fragment_cliente_favoritos
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

        VerFragmentoDashboard()

        binding.BottomNvCliente.setOnItemSelectedListener {item->
            when(item.itemId){
                R.id.Menu_dashboard_cliente->{
                    VerFragmentoDashboard()
                    true
                }
                R.id.Menu_favoritos_cliente->{
                    VerFragmentoFavoritos()
                    true
                }
                R.id.Menu_cuenta_cliente->{
                    VerFragmentoCuenta()
                    true
                }
                else->{
                    false
                }
            }
        }
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

    private fun VerFragmentoDashboard(){
        val nombre_titulo = "Dashboard"
        binding.TituloRLCliente.text = nombre_titulo

        val fragment = Fragment_cliente__dashboard()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsCliente.id, fragment, "Fragment dashboard")
        fragmentTransaction.commit()
    }

    private fun VerFragmentoFavoritos(){
        val nombre_titulo = "Favoritos"
        binding.TituloRLCliente.text = nombre_titulo

        val fragment = Fragment_cliente_favoritos()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsCliente.id, fragment, "Fragment favoritos")
        fragmentTransaction.commit()
    }

    private fun VerFragmentoCuenta(){
        val nombre_titulo = "Mi cuenta"
        binding.TituloRLCliente.text = nombre_titulo

        val fragment = Fragment_cliente_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsCliente.id, fragment, "Fragment cuenta")
        fragmentTransaction.commit()
    }


}