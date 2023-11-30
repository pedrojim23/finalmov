package com.example.biblioteca.Cliente

import android.widget.Filter
import com.example.biblioteca.Administrador.AdaptadorCategoria
import com.example.biblioteca.Administrador.ModeloCategoria

class FiltrarCategoria_Cliente : Filter {

    private var filtroLista : ArrayList<ModeloCategoria>
    private var adaptadorCategoriaCliente : AdaptadorCategoria_Cliente

    constructor(filtroLista: ArrayList<ModeloCategoria>, adaptadorCategoriaCliente: AdaptadorCategoria_Cliente) {
        this.filtroLista = filtroLista
        this.adaptadorCategoriaCliente = adaptadorCategoriaCliente
    }

    override fun performFiltering(categoria: CharSequence?): FilterResults {
        var categoria = categoria
        var resultados = FilterResults()

        if(categoria != null && categoria.isNotEmpty()){
            categoria = categoria.toString().uppercase()
            val modeloFiltardo : ArrayList<ModeloCategoria> = ArrayList()
            for(i in 0 until filtroLista.size){
                if (filtroLista[i].categoria.uppercase().contains(categoria)){
                    modeloFiltardo.add(filtroLista[i])
                }
                resultados.count = modeloFiltardo.size
                resultados.values = modeloFiltardo
            }
        }

        else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados
    }

    override fun publishResults(p0: CharSequence?, resultados: FilterResults) {
        adaptadorCategoriaCliente.categoriaArrayList = resultados.values as ArrayList<ModeloCategoria>
        adaptadorCategoriaCliente.notifyDataSetChanged()
    }
}