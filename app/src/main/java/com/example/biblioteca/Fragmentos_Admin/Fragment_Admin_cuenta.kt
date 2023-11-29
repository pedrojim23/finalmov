package com.example.biblioteca.Fragmentos_Admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.biblioteca.Eligir_rol
import com.example.biblioteca.R
import com.example.biblioteca.databinding.FragmentAdminCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.biblioteca.Administrador.MisFunciones
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Fragment_Admin_cuenta : Fragment() {

    private lateinit var binding : FragmentAdminCuentaBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminCuentaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        binding.CerrarSesionAdmin.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(context, Eligir_rol::class.java))
            activity?.finishAffinity()
        }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener datos user ac
                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    var t_registro = "${snapshot.child("tiempo_registrado").value}"
                    val rol = "${snapshot.child("rol").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    if (t_registro == null){
                        t_registro = "0"
                    }

                    //conv format fecha
                    val formato_fecha = MisFunciones.formatoTiempo(t_registro.toLong())

                    //set info
                    binding.TxtNombresAdmin.text = nombres
                    binding.TxtEmailAdmin.text = email
                    binding.TxtTiempoRegistroAdmin.text = formato_fecha
                    binding.TxtRolAdmin.text = rol

                    //set img
                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.imgPerfilAdmin)
                    }catch (e:Exception){
                        Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}