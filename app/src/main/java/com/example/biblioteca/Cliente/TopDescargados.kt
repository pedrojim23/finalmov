package com.example.biblioteca.Cliente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.biblioteca.Administrador.Modelopdf
import com.example.biblioteca.R
import com.example.biblioteca.databinding.ActivityTopDescargadosBinding
import com.example.biblioteca.databinding.ActivityTopVistosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TopDescargados : AppCompatActivity() {

    private lateinit var binding: ActivityTopDescargadosBinding
    private lateinit var pdfArrayList: ArrayList<Modelopdf>
    private lateinit var adaptadorPdfCliente: AdaptadorPdfCliente
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopDescargadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topDescargados()
    }

    private fun topDescargados() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference ("Libros")
        ref.orderByChild("contadorDescargas").limitToLast (10)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange (snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val modelo = ds.getValue (Modelopdf::class.java)
                        pdfArrayList.add(modelo!!)
                    }
                    adaptadorPdfCliente = AdaptadorPdfCliente (this@TopDescargados, pdfArrayList)
                    binding. RvTopDescargados.adapter = adaptadorPdfCliente
                }
                override fun onCancelled (error: DatabaseError) {
                    TODO ("Not yet implemented")
                }
            })
    }
}