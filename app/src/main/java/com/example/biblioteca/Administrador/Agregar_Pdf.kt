package com.example.biblioteca.Administrador

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager.Strings
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import com.example.biblioteca.R
import com.example.biblioteca.databinding.ActivityAgregarPdfBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class Agregar_Pdf : AppCompatActivity() {

    private lateinit var binding : ActivityAgregarPdfBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog : ProgressDialog
    private lateinit var categoriaArrayList : ArrayList<ModeloCategoria>
    private var pdfUri : Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        CargarCategorias()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.AdjuntarPdfIb.setOnClickListener{
            ElegirPdf()
        }

        binding.TvCategoriaLibro.setOnClickListener{
            SeleccionarCat()
        }

        binding.BtSubirLibro.setOnClickListener{
            ValidarInformacion()
        }
    }

    private  var titulo = ""
    private  var descripcion = ""
    private  var categoria = ""
    private fun ValidarInformacion() {
        titulo = binding.EtTituloLibro.text.toString().trim()
        descripcion = binding.EtDescripcionLibro.text.toString().trim()
        categoria = binding.TvCategoriaLibro.text.toString().trim()

        if(titulo.isEmpty()){
            Toast.makeText(this, "Ingrese titulo", Toast.LENGTH_SHORT).show()
        } else if(descripcion.isEmpty()) {
            Toast.makeText(this, "Ingrese descripcion", Toast.LENGTH_SHORT).show()
        } else if(categoria.isEmpty()) {
            Toast.makeText(this, "Seleccione Categoría", Toast.LENGTH_SHORT).show()
        } else if (pdfUri == null){
            Toast.makeText(this, "Adjunte un libro", Toast.LENGTH_SHORT).show()
        } else {
            SubirPdfStore()

        }
    }

    private fun SubirPdfStore() {
        progressDialog.setMessage("Subiendo PDF")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()
        val ruta_libro = "Libros/$tiempo"
        val storageReference = FirebaseStorage.getInstance().getReference(ruta_libro)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {tarea->
                val UriTask : Task<Uri> = tarea.storage.downloadUrl
                while (!UriTask.isSuccessful);
                val UrlPdfSubido = "${UriTask.result}"
                SubirPdfBD(UrlPdfSubido, tiempo)

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                    Toast.makeText(this, "Fallo la Subida del archivo debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun SubirPdfBD(urlPdfSubido: String, tiempo: Long) {
        progressDialog.setMessage("Subiendo pdf a BD")
        val uid = firebaseAuth.uid

        val hashMap : HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$tiempo"
        hashMap["titulo"] = titulo
        hashMap["descripcion"] = descripcion
        hashMap["categoria"] = id_categoria
        hashMap["url"] = urlPdfSubido
        hashMap["tiempo"] = tiempo
        hashMap["contadorVistas"] = 0
        hashMap["contadorDescargas"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Libro subido con exito", Toast.LENGTH_SHORT).show()
                binding.EtTituloLibro.setText("")
                binding.EtDescripcionLibro.setText("")
                binding.TvCategoriaLibro.setText("")
                pdfUri = null

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "Fallo la Subida del archivo debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun CargarCategorias() {
        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriaArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var id_categoria = ""
    private var titulo_categoria = ""
    private fun SeleccionarCat() {
        val categoriasArray = arrayOfNulls<String>(categoriaArrayList.size)
        for (i in categoriasArray.indices) {
            categoriasArray[i] = categoriaArrayList[i].categoria
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar Categoria")
            .setItems(categoriasArray) {
            dialog, which ->
                id_categoria = categoriaArrayList[which].id
                titulo_categoria = categoriaArrayList[which].categoria
                binding.TvCategoriaLibro.text = titulo_categoria
        }
            .show()


    }

    private fun ElegirPdf() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityRL.launch(intent)
    }

    val pdfActivityRL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {resultado->
            if (resultado.resultCode == RESULT_OK) {
                pdfUri = resultado.data!!.data
            } else {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    )

}