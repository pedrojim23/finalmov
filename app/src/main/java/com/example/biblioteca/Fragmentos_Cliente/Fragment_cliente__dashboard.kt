package com.example.biblioteca.Fragmentos_Cliente

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.biblioteca.Administrador.ModeloCategoria
import com.example.biblioteca.Cliente.AdaptadorCategoria_Cliente
import com.example.biblioteca.Cliente.TopDescargados
import com.example.biblioteca.Cliente.TopVistos
import com.example.biblioteca.R
import com.example.biblioteca.databinding.FragmentClienteCuentaBinding
import com.example.biblioteca.databinding.FragmentClienteDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Fragment_cliente__dashboard : Fragment() {

    private lateinit var binding: FragmentClienteDashboardBinding
    private lateinit var mContext : Context

    private lateinit var categoriasArrayList : ArrayList<ModeloCategoria>
    private lateinit var adaptadorcategoria: AdaptadorCategoria_Cliente

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentClienteDashboardBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarCategorias()

        binding.BuscarCategoria.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(categoria: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adaptadorcategoria.filter.filter(categoria)
                }catch (e: Exception){
                    Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.BtnMasVistos.setOnClickListener {
            startActivity (Intent (mContext, TopVistos::class.java))
        }
        binding.BtnMasDescargados.setOnClickListener {
            startActivity (Intent (mContext, TopDescargados::class.java))
        }
    }

    private fun cargarCategorias() {
        categoriasArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriasArrayList.add(modelo!!)
                }
                adaptadorcategoria = AdaptadorCategoria_Cliente(mContext, categoriasArrayList)
                binding.categoriasRv.adapter = adaptadorcategoria
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}