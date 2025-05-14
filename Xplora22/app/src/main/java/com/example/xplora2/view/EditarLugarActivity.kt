package com.example.xplora2.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.xplora2.R
import com.example.xplora2.controller.ApiClient
import com.example.xplora2.model.Coordenadas
import com.example.xplora2.model.Lugar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarLugarActivity : AppCompatActivity() {

    private lateinit var lugar: Lugar
    private lateinit var lugarId: String

    private lateinit var nombreEditText: EditText
    private lateinit var paisEditText: EditText
    private lateinit var ciudadEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var imagenEditText: EditText
    private lateinit var fechaEditText: EditText
    private lateinit var tipoEditText: EditText
    private lateinit var idiomasEditText: EditText
    private lateinit var etiquetasEditText: EditText
    private lateinit var latitudEditText: EditText
    private lateinit var longitudEditText: EditText

    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_lugar)

        // Obtener ID del lugar
        lugarId = intent.getStringExtra("LUGAR_ID") ?: run {
            Toast.makeText(this, "ID del lugar no recibido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar inputs
        nombreEditText = findViewById(R.id.nombreLugarEditText)
        paisEditText = findViewById(R.id.paisLugarEditText)
        ciudadEditText = findViewById(R.id.ciudadLugarEditText)
        descripcionEditText = findViewById(R.id.descripcionLugarEditText)
        imagenEditText = findViewById(R.id.imagenUrlEditText)
        fechaEditText = findViewById(R.id.fechaFundacionEditText)
        tipoEditText = findViewById(R.id.tipoLugarEditText)
        idiomasEditText = findViewById(R.id.idiomasEditText)
        etiquetasEditText = findViewById(R.id.etiquetasEditText)
        latitudEditText = findViewById(R.id.latitudEditText)
        longitudEditText = findViewById(R.id.longitudEditText)

        btnActualizar = findViewById(R.id.btnActualizarLugar)
        btnEliminar = findViewById(R.id.btnEliminarLugar)

        cargarLugar()

        btnActualizar.setOnClickListener {
            val lugarActualizado = Lugar(
                _id = lugarId,
                nombre = nombreEditText.text.toString(),
                pais = paisEditText.text.toString(),
                ciudad = ciudadEditText.text.toString(),
                descripcion = descripcionEditText.text.toString(),
                imagen_url = imagenEditText.text.toString(),
                fecha_fundacion = fechaEditText.text.toString(),
                tipo_lugar = tipoEditText.text.toString(),
                idiomas = idiomasEditText.text.toString().split(",").map { it.trim() },
                etiquetas = etiquetasEditText.text.toString().split(",").map { it.trim() },
                coordenadas = Coordenadas(
                    latitud = latitudEditText.text.toString().toDoubleOrNull() ?: 0.0,
                    longitud = longitudEditText.text.toString().toDoubleOrNull() ?: 0.0
                )
            )

            ApiClient.lugarApiService.updateLugar(lugarId, lugarActualizado)
                .enqueue(object : Callback<Lugar> {
                    override fun onResponse(call: Call<Lugar>, response: Response<Lugar>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@EditarLugarActivity, "Lugar actualizado", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@EditarLugarActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Lugar>, t: Throwable) {
                        Toast.makeText(this@EditarLugarActivity, "Error de red", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        btnEliminar.setOnClickListener {
            ApiClient.lugarApiService.eliminarLugar(lugarId)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@EditarLugarActivity, "Lugar eliminado", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@EditarLugarActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@EditarLugarActivity, "Error de red", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun cargarLugar() {
        ApiClient.lugarApiService.obtenerLugarPorId(lugarId)
            .enqueue(object : Callback<Lugar> {
                override fun onResponse(call: Call<Lugar>, response: Response<Lugar>) {
                    if (response.isSuccessful) {
                        lugar = response.body() ?: return
                        nombreEditText.setText(lugar.nombre)
                        paisEditText.setText(lugar.pais)
                        ciudadEditText.setText(lugar.ciudad)
                        descripcionEditText.setText(lugar.descripcion)
                        imagenEditText.setText(lugar.imagen_url)
                        fechaEditText.setText(lugar.fecha_fundacion)
                        tipoEditText.setText(lugar.tipo_lugar)
                        idiomasEditText.setText(lugar.idiomas.joinToString(", "))
                        etiquetasEditText.setText(lugar.etiquetas.joinToString(", "))
                        latitudEditText.setText(lugar.coordenadas.latitud.toString())
                        longitudEditText.setText(lugar.coordenadas.longitud.toString())
                    } else {
                        Toast.makeText(this@EditarLugarActivity, "Error al cargar lugar", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<Lugar>, t: Throwable) {
                    Toast.makeText(this@EditarLugarActivity, "Error de red", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
    }
}