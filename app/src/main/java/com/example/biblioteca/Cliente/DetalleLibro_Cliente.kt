package com.example.biblioteca.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import com.example.biblioteca.Administrador.Constantes
import com.example.biblioteca.Administrador.MisFunciones
import com.example.biblioteca.LeerLibro
import com.example.biblioteca.R
import com.example.biblioteca.databinding.ActivityDetalleLibroClienteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class DetalleLibro_Cliente : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleLibroClienteBinding
    private var idLibro = ""

    private var tituloLibro = ""
    private var urlLibro = ""
    private lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleLibroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idlibro")!!
        MisFunciones.incrementarVistas(idLibro)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        binding. IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        binding.BtLeerLibroC.setOnClickListener() {
            val intent= Intent (this@DetalleLibro_Cliente, LeerLibro::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity (intent)
        }

        binding.BtnDescargarLibroC.setOnClickListener {
            //descargarLibro()
            if (ContextCompat.checkSelfPermission (this, android. Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED) {
            descargarLibro()
        }else{
            permisoAlmacenamiento.launch (android. Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        }

        cargarDetalleLibro()
    }

    private fun descargarLibro() {
        progressDialog.setMessage("Descargando libro")
        progressDialog.show()
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlLibro)
        storageReference.getBytes(Constantes.Maximo_bytes_pdf)
            .addOnSuccessListener {bytes->
                guardarLibroDisp(bytes)
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText (applicationContext,"${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarLibroDisp(bytes  : ByteArray) {
        val nombreLibro_extension = "$tituloLibro" + System.currentTimeMillis()+".pdf"
        try {
            val carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            carpeta.mkdirs()
            val archivo_ruta = carpeta.path+"/"+nombreLibro_extension
            val out = FileOutputStream(archivo_ruta)
            out.write(bytes)
            out.close()
            Toast.makeText (applicationContext,"Libro guardado con éxito", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            incrementarNumDesc()
        }catch (e:Exception) {
            Toast.makeText (applicationContext,"${e.message}", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }
    }

    private fun cargarDetalleLibro(){
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener info del libro por id
                    val categoria = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVistas = "${snapshot.child("contadorVistas").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    tituloLibro = "${snapshot.child("titulo").value}"
                    urlLibro = "${snapshot.child("url").value}"

                    //formato tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())
                    //cargar cate
                    MisFunciones.CargarCategoria(categoria, binding.categoriaD)
                    //cargar miniatura
                    MisFunciones.CargarPdfUrl("$urlLibro", "$tituloLibro", binding.VisualizadorPdf, binding.progressBar, binding.paginasD)
                    //cargar tamanio
                    MisFunciones.CargarTamanioPdf("$urlLibro", "$tituloLibro", binding.tamanioD)


                    //Set info rest
                    binding.tituloLibroD.text = tituloLibro
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

    private fun incrementarNumDesc () {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent (object: ValueEventListener{
            override fun onDataChange (snapshot: DataSnapshot) {
                var contDescarActual = "${snapshot.child("contadorDescargas").value}"
                if (contDescarActual == "" || contDescarActual == "null"){
                    contDescarActual = "0"
                }
                val nuevaDes = contDescarActual.toLong() + 1
                val hashMap = HashMap<String, Any>()
                hashMap ["contador Descargas"] = nuevaDes
                val BDRef = FirebaseDatabase.getInstance().getReference ("Libros")
                BDRef.child(idLibro)
                    .updateChildren(hashMap)
            }
            override fun onCancelled (error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private val permisoAlmacenamiento = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {permiso_concedido->
        if (permiso_concedido) {
            descargarLibro()
        } else {
            Toast.makeText(
                applicationContext,
                "El permiso de almacenamiento ha sido denegado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}