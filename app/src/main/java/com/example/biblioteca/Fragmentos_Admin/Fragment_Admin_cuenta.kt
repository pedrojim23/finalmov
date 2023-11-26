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
import com.google.firebase.database.core.Context


class Fragment_Admin_cuenta : Fragment() {

    private lateinit var binding : FragmentAdminCuentaBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var mContext : Context

    override fun onAttach(context: android.content.Context) {
        //mContext = context
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
        binding.CerrarSesionAdmin.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(context, Eligir_rol::class.java))
            activity?.finishAffinity()
        }
    }

}