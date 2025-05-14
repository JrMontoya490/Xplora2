package com.example.xplora2.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.xplora2.R
import com.example.xplora2.controller.ApiClient
import com.example.xplora2.model.Lugar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleActivity : AppCompatActivity() {

    private lateinit var lugarId: String
    private val api = ApiClient.lugarApiService

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var infoExtraTextView: TextView
    private lateinit var imagenImageView: ImageView
    private lateinit var btnEditar: Button
    private lateinit var btnEliminar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        lugarId = intent.getStringExtra("LUGAR_ID") ?: ""
        if (lugarId.isEmpty()) {
            Toast.makeText(this, "ID del lugar no recibido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        initViews()
        cargarLugar()

        btnEditar.setOnClickListener {
            val intent = Intent(this, EditarLugarActivity::class.java)
            intent.putExtra("LUGAR_ID", lugarId)
            startActivityForResult(intent, 101)
        }

        btnEliminar.setOnClickListener {
            confirmarEliminacion()
        }
    }

    private fun initViews() {
        nombreTextView = findViewById(R.id.nombreLugar)
        descripcionTextView = findViewById(R.id.descripcionLugar)
        infoExtraTextView = findViewById(R.id.infoExtra)
        imagenImageView = findViewById(R.id.imagenLugar)
        btnEditar = findViewById(R.id.btnEditarDetalle)
        btnEliminar = findViewById(R.id.btnEliminarDetalle)
    }

    private fun cargarLugar() {
        api.obtenerLugarPorId(lugarId).enqueue(object : Callback<Lugar> {
            override fun onResponse(call: Call<Lugar>, response: Response<Lugar>) {
                if (response.isSuccessful) {
                    response.body()?.let { mostrarLugar(it) }
                } else {
                    Toast.makeText(this@DetalleActivity, "Lugar no encontrado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Lugar>, t: Throwable) {
                Log.e("DETALLE", "Error API: ${t.message}", t)
                Toast.makeText(this@DetalleActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun mostrarLugar(lugar: Lugar) {
        nombreTextView.text = lugar.nombre
        descripcionTextView.text = lugar.descripcion
        infoExtraTextView.text = """
            País: ${lugar.pais}
            Ciudad: ${lugar.ciudad}
            Tipo: ${lugar.tipo_lugar}
            Fundación: ${lugar.fecha_fundacion}
            Idiomas: ${lugar.idiomas.joinToString(", ")}
            Etiquetas: ${lugar.etiquetas.joinToString(", ")}
        """.trimIndent()

        Glide.with(this).load(lugar.imagen_url).into(imagenImageView)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            val ubicacion = LatLng(lugar.coordenadas.latitud, lugar.coordenadas.longitud)
            googleMap.addMarker(MarkerOptions().position(ubicacion).title(lugar.nombre))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 14f))
        }
    }

    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar lugar")
            .setMessage("¿Estás seguro de que deseas eliminar este lugar?")
            .setPositiveButton("Sí") { _, _ -> eliminarLugar() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarLugar() {
        api.eliminarLugar(lugarId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DetalleActivity, "Lugar eliminado", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK, Intent().putExtra("HUBO_CAMBIOS", true))
                    finish()
                } else {
                    Toast.makeText(this@DetalleActivity, "No se pudo eliminar el lugar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@DetalleActivity, "Error al eliminar: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data?.getBooleanExtra("HUBO_CAMBIOS", false) == true) {
            setResult(RESULT_OK, Intent().putExtra("HUBO_CAMBIOS", true))
            finish()
        }
    }
}
