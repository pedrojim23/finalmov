package com.example.biblioteca.Administrador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.databinding.ItemCategoriaAdminBinding
import android.content.Context

class AdaptadorCategoria : RecyclerView.Adapter<AdaptadorCategoria.HolderCategoria>{

    private lateinit var binding : ItemCategoriaAdminBinding

    private var m_context : Context
    private var categoriaArrayList : ArrayList<ModeloCategoria>

    constructor(m_context: Context, categoriaArrayList: ArrayList<ModeloCategoria>) {
        this.m_context = m_context
        this.categoriaArrayList = categoriaArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaAdminBinding.inflate(LayoutInflater.from(m_context),parent,false)
        return HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modelo = categoriaArrayList[position]
        val id = modelo.id
        val categoria = modelo.categoria
        val tiempo = modelo.tiempo
        val uid = modelo.uid

        holder.categoriaTv.text = categoria

        holder.eliminarCatIb.setOnClickListener {

        }
    }

    inner class HolderCategoria (itemView : View) :RecyclerView.ViewHolder(itemView){
        var categoriaTv : TextView = binding.ItemNombreCatA
        var eliminarCatIb : ImageButton = binding.EliminarCat

    }

}