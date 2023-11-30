package com.example.biblioteca.Cliente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.biblioteca.Administrador.AdaptadorPdfAdmin
import com.example.biblioteca.Administrador.Modelopdf
import com.example.biblioteca.R
import com.example.biblioteca.databinding.ActivityListaPdfClienteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaPdfCliente : AppCompatActivity() {

    private lateinit var binding: ActivityListaPdfClienteBinding

    private var idCategoria = ""
    private var tituloCategoria = ""

    private lateinit var pdfArrayList : ArrayList<Modelopdf>
    private lateinit var adaptadorPdfCliente: AdaptadorPdfCliente
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaPdfClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        idCategoria = intent.getStringExtra("idCategoria")!!
        tituloCategoria = intent.getStringExtra("tituloCategoria")!!

        binding.TxtCategoriaLibro.text = tituloCategoria

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        cargarLibros()
    }

    private fun cargarLibros() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.orderByChild("categoria").equalTo(idCategoria)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(Modelopdf::class.java)
                        if (modelo!=null){
                            pdfArrayList.add(modelo)
                        }
                    }
                    adaptadorPdfCliente = AdaptadorPdfCliente(this@ListaPdfCliente, pdfArrayList)
                    binding.RvLibrosCliente.adapter = adaptadorPdfCliente
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}