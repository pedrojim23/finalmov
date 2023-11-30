package com.example.biblioteca.Administrador

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.example.biblioteca.R
import com.example.biblioteca.databinding.ActivityEditarPerfilAdminBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.net.URI

class EditarPerfilAdmin : AppCompatActivity() {

    private lateinit var binding : ActivityEditarPerfilAdminBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private var imagenUri : Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Epere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.FbCambiarIMg.setOnClickListener{
            mostrarOpciones()
        }

        binding.BtActualizarInfo.setOnClickListener {
            validarInformacion()
        }
    }

    private fun mostrarOpciones() {
        val popupMenu = PopupMenu(this, binding.imgPerfilAdmin)
        popupMenu.menu.add(Menu.NONE,0,0,"Galeria")
        popupMenu.menu.add(Menu.NONE,1,1,"Camara")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item->
            val id = item.itemId
            if(id == 0){
                //Galeria
                selecImgGaleria()
            }
            else if (id == 1){
                //camara
                tomarFoto()
            }
            true
        }
    }

    private fun tomarFoto() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo_temp")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_temp")
        imagenUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)
        ARLCamara.launch(intent)
    }

    private val ARLCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                subirImagenStorage()
            }
            else{
                Toast.makeText(applicationContext, "Operacion cancelada", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun selecImgGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        ARLGaleria.launch(intent)
    }
    private val ARLGaleria = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data

                //binding.imgPerfilAdmin.setImageURI(imagenUri)
                subirImagenStorage()
            }
            else{
                Toast.makeText(applicationContext, "Operacion cancelada", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private var nombres = ""
    private fun validarInformacion() {
        nombres = binding.EtANombres.text.toString().trim()
        if (nombres.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese un nuevo nombre", Toast.LENGTH_SHORT).show()
        }
        else{
            ActualizarInfo()
        }
    }

    private fun subirImagenStorage() {
        progressDialog.setMessage("Subiendo imagen")
        progressDialog.show()

        val rutaImagen = "ImagenesPerfilAdmins/"+firebaseAuth.uid

        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.putFile(imagenUri!!)
            .addOnSuccessListener {taskSnapshot->
                val uriTask : Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagen = "${uriTask.result}"
                subirImagenDatabase(urlImagen)
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImagenDatabase(urlImagen : String) {
        progressDialog.setMessage("Actualizando Imagen")

        val hashMap : HashMap<String, Any> = HashMap()
        if (imagenUri != null){
            hashMap["imagen"] = urlImagen
        }

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Imagen actualizada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ActualizarInfo() {
        progressDialog.setMessage("Actualizando informacion")
        val hashMap:HashMap<String, Any> = HashMap()
        hashMap["nombres"] = "$nombres"
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Informacion actualizada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se puedo actualizar debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtain info
                    val nombre = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    //set
                    binding.EtANombres.setText(nombre)

                    try {
                        Glide.with(applicationContext)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.imgPerfilAdmin)
                    }catch (e: Exception){
                        Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}