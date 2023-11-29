package com.example.biblioteca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.biblioteca.Administrador.Constantes
import com.example.biblioteca.databinding.ActivityLeerLibroBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class LeerLibro : AppCompatActivity() {

    private lateinit var binding: ActivityLeerLibroBinding

    var idLibro = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLeerLibroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        cargarInformacionLibro()
    }

    private fun cargarInformacionLibro() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pdfUrl = snapshot.child("url").value
                    cargarLibroStorage("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun cargarLibroStorage(pdfUrl: String) {
        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Constantes.Maximo_bytes_pdf)
            .addOnSuccessListener {bytes->
                //cargar pdf
                binding.VisualizadorPDF.fromBytes(bytes)
                    .swipeHorizontal(false)
                    .onPageChange{pagina, contadorPaginas->
                        val paginaActual = pagina+1
                        binding.TxtLeerLibro.text = "$paginaActual/$contadorPaginas"
                    }
                    .load()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener{
                binding.progressBar.visibility = View.GONE
            }
    }
}