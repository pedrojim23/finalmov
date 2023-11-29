package com.example.biblioteca.Administrador

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.biblioteca.LeerLibro
import com.example.biblioteca.R
import com.example.biblioteca.databinding.ActivityDetalleLibroBinding
import com.example.biblioteca.databinding.ItemLibroAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleLibro : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleLibroBinding
    private var idLibro=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtLeerLibro.setOnClickListener {
            val intent = Intent(this@DetalleLibro, LeerLibro::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity(intent)
        }

        cargarDetalleLibro()
    }

    private fun cargarDetalleLibro() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener info del libro por id
                    val categoria = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVistas = "${snapshot.child("contadorVistas").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    val titulo = "${snapshot.child("titulo").value}"
                    val url = "${snapshot.child("url").value}"

                    //formato tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())
                    //cargar cate
                    MisFunciones.CargarCategoria(categoria, binding.categoriaD)
                    //cargar miniatura
                    MisFunciones.CargarPdfUrl("$url", "$titulo", binding.VisualizadorPdf, binding.progressBar, binding.paginasD)
                    //cargar tamanio
                    MisFunciones.CargarTamanioPdf("$url", "$titulo", binding.tamanioD)


                    //Set info rest
                    binding.tituloLibroD.text = titulo
                    binding.descripcionD.text = descripcion
                    binding.vistasD.text = contadorVistas
                    binding.descargasD.text = contadorDescargas
                    binding.fechaD.text = fecha

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}