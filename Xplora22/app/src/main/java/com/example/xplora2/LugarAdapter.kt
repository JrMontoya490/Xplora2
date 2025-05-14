package com.example.xplora2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LugarAdapter(
    private var lugares: MutableList<Lugar>,
    private val onClick: (Lugar) -> Unit
) : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

    class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreText: TextView = itemView.findViewById(R.id.tvNombre)
        val ciudadText: TextView = itemView.findViewById(R.id.tvCiudad)
        val imagen: ImageView = itemView.findViewById(R.id.ivLugar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lugar, parent, false)
        return LugarViewHolder(view)
    }

    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val lugar = lugares[position]
        holder.nombreText.text = lugar.nombre
        holder.ciudadText.text = "${lugar.ciudad}, ${lugar.pais}"
        Glide.with(holder.itemView.context).load(lugar.imagen_url).into(holder.imagen)
        holder.itemView.setOnClickListener { onClick(lugar) }
    }

    override fun getItemCount(): Int = lugares.size

    // Nuevo método para actualizar la lista
    fun actualizarLugares(nuevosLugares: List<Lugar>) {
        this.lugares.clear()
        this.lugares.addAll(nuevosLugares)
        notifyDataSetChanged()
    }
}