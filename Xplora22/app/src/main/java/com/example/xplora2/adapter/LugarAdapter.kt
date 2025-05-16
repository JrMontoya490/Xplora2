package com.example.xplora2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xplora2.model.Lugar
import com.example.xplora2.R

// RecyclerView que muestra una lista de lugares
class LugarAdapter(
    private var lugares: MutableList<Lugar>,
    private val onClick: (Lugar) -> Unit
) : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

    // ViewHolder de cada ítem del RecyclerView
    class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreText: TextView = itemView.findViewById(R.id.tvNombre)
        val ciudadText: TextView = itemView.findViewById(R.id.tvCiudad)
        val imagen: ImageView = itemView.findViewById(R.id.ivLugar)
    }

    // Crea y retorna un nuevo ViewHolder inflando el layout del ítem
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lugar, parent, false)
        return LugarViewHolder(view)
    }

    // Enlaza los datos de un lugar con los elementos del ViewHolder
    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val lugar = lugares[position]
        holder.nombreText.text = lugar.nombre
        holder.ciudadText.text = "${lugar.ciudad}, ${lugar.pais}"

        // Carga la imagen desde la URL usando Glide
        Glide.with(holder.itemView.context).load(lugar.imagen_url).into(holder.imagen)

        holder.itemView.setOnClickListener { onClick(lugar) }
    }

    // Retorna la cantidad de elementos en la lista
    override fun getItemCount(): Int = lugares.size

    // Método para actualizar la lista de lugares y refrescar el RecyclerView
    fun actualizarLugares(nuevosLugares: List<Lugar>) {
        this.lugares.clear()
        this.lugares.addAll(nuevosLugares)
        notifyDataSetChanged()
    }
}
