package com.example.biblioteca.Cliente

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.biblioteca.Administrador.Login_admin
import com.example.biblioteca.MainActivity
import com.example.biblioteca.MainActivityCliente
import com.example.biblioteca.R
import com.example.biblioteca.databinding.ActivityRegistroClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registro_Cliente : AppCompatActivity() {

    private lateinit var binding : ActivityRegistroClienteBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnResgistrarCliente.setOnClickListener{
            ValidarInformacion()
        }

        binding.TxtTengoCuentaCliente.setOnClickListener{
            startActivity(Intent(this@Registro_Cliente, Login_Cliente::class.java))
        }
    }

    var nombres = ""
    var edad = ""
    var email = ""
    var password = ""
    var r_password = ""
    private fun ValidarInformacion() {
        nombres = binding.EtNombresCliente.text.toString().trim()
        edad = binding.EtEdadCliente.text.toString().trim()
        email = binding.EtEmailCliente.text.toString().trim()
        password = binding.EtPasswordCliente.text.toString().trim()
        r_password = binding.EtRPasswordCliente.text.toString().trim()

        if(nombres.isEmpty()){
            binding.EtNombresCliente.error = "Por favor ingrese su(s) nombre(s)"
            binding.EtNombresCliente.requestFocus()
        }
        else if(edad.isEmpty()){
            binding.EtEdadCliente.error = "Por favor ingrese su edad)"
            binding.EtEdadCliente.requestFocus()
        }
        else if(email.isEmpty()){
            binding.EtEmailCliente.error = "Por favor ingrese un correo electronico"
            binding.EtEmailCliente.requestFocus()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailCliente.error = "Correo electronico invalido"
            binding.EtEmailCliente.requestFocus()
        }
        else if(password.isEmpty()){
            binding.EtPasswordCliente.error = "Por favor ingrese una contraseña"
            binding.EtPasswordCliente.requestFocus()
        }
        else if(password.length < 6){
            binding.EtPasswordCliente.error = "La longitud minima de la contraseña debe ser de 6 caracteres"
            binding.EtPasswordCliente.requestFocus()
        }
        else if(r_password.isEmpty()){
            binding.EtRPasswordCliente.error = "Por favor repita la contraseña"
            binding.EtRPasswordCliente.requestFocus()
        }
        else if (password != r_password){
            binding.EtRPasswordCliente.error = "Las contraseñas no coinciden"
            binding.EtRPasswordCliente.requestFocus()
        }
        else{
            CrearCuentaCliente(email, password)
        }
    }

    private fun CrearCuentaCliente(email: String, password: String) {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            AgregarInfoBD()
        } .addOnFailureListener {e->
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "No se pudo crear la cuenta debido a ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun AgregarInfoBD() {
        progressDialog.setMessage("Recolectando informacion...")
        val tiempo = System.currentTimeMillis()
        val uid = firebaseAuth.uid!!

        val datos_cliente : HashMap<String, Any?> = HashMap()
        datos_cliente["uid"] = uid
        datos_cliente["nombres"] = nombres
        datos_cliente["edad"] = edad
        datos_cliente["email"] = email
        datos_cliente["rol"] = "cliente"
        datos_cliente["tiempo_registrado"] = tiempo
        datos_cliente["imagen"] = ""

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid)
            .setValue(datos_cliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Cunenta creada", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo guardar la informacion debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}