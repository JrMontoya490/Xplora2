package com.example.xplora2

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleActivity : AppCompatActivity() {

    private lateinit var lugarId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        val nombreTextView = findViewById<TextView>(R.id.nombreLugar)
        val descripcionTextView = findViewById<TextView>(R.id.descripcionLugar)
        val infoExtraTextView = findViewById<TextView>(R.id.infoExtra)
        val imagenImageView = findViewById<ImageView>(R.id.imagenLugar)
        val btnEditar = findViewById<Button>(R.id.btnEditarDetalle)
        val btnEliminar = findViewById<Button>(R.id.btnEliminarDetalle)

        lugarId = intent.getStringExtra("LUGAR_ID") ?: ""

        Log.d("DETALLE", "ID recibido: $lugarId")

        if (lugarId.isEmpty()) {
            Toast.makeText(this, "ID no recibido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val api = ApiClient.lugarApiService

        api.obtenerLugarPorId(lugarId).enqueue(object : Callback<Lugar> {
            override fun onResponse(call: Call<Lugar>, response: Response<Lugar>) {
                Log.d("DETALLE", "Respuesta recibida: ${response.code()}")

                if (response.isSuccessful) {
                    val lugar = response.body()
                    Log.d("DETALLE", "Lugar recibido: $lugar")

                    if (lugar != null) {
                        nombreTextView.text = lugar.nombre
                        descripcionTextView.text = lugar.descripcion
                        infoExtraTextView.text = """
                            País: ${lugar.pais}
                            Ciudad: ${lugar.ciudad}
                            Tipo: ${lugar.tipo_lugar}
                            Fecha fundación: ${lugar.fecha_fundacion}
                            Idiomas: ${lugar.idiomas.joinToString(", ")}
                            Etiquetas: ${lugar.etiquetas.joinToString(", ")}
                        """.trimIndent()

                        Glide.with(this@DetalleActivity)
                            .load(lugar.imagen_url)
                            .into(imagenImageView)

                        val mapFragment = supportFragmentManager
                            .findFragmentById(R.id.mapFragment) as SupportMapFragment
                        mapFragment.getMapAsync { googleMap ->
                            val latLng = LatLng(lugar.coordenadas.latitud, lugar.coordenadas.longitud)
                            googleMap.addMarker(MarkerOptions().position(latLng).title(lugar.nombre))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                        }
                    }
                } else {
                    Toast.makeText(this@DetalleActivity, "Lugar no encontrado", Toast.LENGTH_SHORT).show()
                    Log.e("DETALLE", "Error 404 o similar: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Lugar>, t: Throwable) {
                Log.e("DETALLE", "Error al llamar a API: ${t.message}", t)
                Toast.makeText(this@DetalleActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        btnEditar.setOnClickListener {
            val intent = Intent(this, EditarLugarActivity::class.java)
            intent.putExtra("LUGAR_ID", lugarId)
            startActivity(intent)
        }

        btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar lugar")
                .setMessage("¿Estás seguro de eliminar este lugar?")
                .setPositiveButton("Sí") { _, _ ->
                    api.eliminarLugar(lugarId).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@DetalleActivity, "Lugar eliminado", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@DetalleActivity, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@DetalleActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}