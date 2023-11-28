package com.example.biblioteca.Administrador

import android.app.Application
import com.google.firebase.storage.FirebaseStorage
import android.text.format.DateFormat
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Locale

class MisFunciones : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        fun formatoTiempo (tiempo : Long) : String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            //dd/MM/yyyy
            cal.timeInMillis = tiempo
            return DateFormat.format("dd/MM/yyy",cal).toString()
        }

        fun CargarTamanioPdf(pdfUrl : String, pdfTitulo : String, tamanio : TextView){
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener {metadata->
                    val bytes = metadata.sizeBytes.toDouble()

                    val KB = bytes/1024
                    val MB = KB/1024

                    if(MB>1024){
                        tamanio.text = "${String.format("%2f", MB)} MB"
                    }
                    else if(KB>=1){
                        tamanio.text = "${String.format("%2f", KB)} KB"
                    }
                    else{
                        tamanio.text = "${String.format("%2f", bytes)} Bytes"
                    }

                }
                .addOnFailureListener{e->

                }

        }

        fun CargarPdfUrl(pdfUrl: String, pdfTitulo: String, pdfView: PDFView, progressBar: ProgressBar, paginaTv : TextView?){
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constantes.Maximo_bytes_pdf)
                .addOnSuccessListener {bytes->
                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError {t->
                            progressBar.visibility = View.INVISIBLE
                        }
                        .onPageError{page, pageCount->
                            progressBar.visibility = View.INVISIBLE
                        }
                        .onLoad{Pagina->
                            progressBar.visibility = View.INVISIBLE
                            if (paginaTv != null){
                                paginaTv.text = "$paginaTv"
                            }
                        }
                        .load()

                }
                .addOnFailureListener{e->

                }
        }

        fun CargarCategoria(categoriaId : String, categoriaTv : TextView){
            val ref = FirebaseDatabase.getInstance().getReference("Categorias")
            ref.child(categoriaId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val categoria = "${snapshot.child("categoria").value}"
                        categoriaTv.text = categoria
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }
}