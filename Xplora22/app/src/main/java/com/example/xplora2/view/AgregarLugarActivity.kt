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

class AgregarLugarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_lugar)

        val nombreEditText = findViewById<EditText>(R.id.nombreLugarEditText)
        val paisEditText = findViewById<EditText>(R.id.paisLugarEditText)
        val ciudadEditText = findViewById<EditText>(R.id.ciudadLugarEditText)
        val descripcionEditText = findViewById<EditText>(R.id.descripcionLugarEditText)
        val imagenUrlEditText = findViewById<EditText>(R.id.imagenUrlEditText)
        val fechaFundacionEditText = findViewById<EditText>(R.id.fechaFundacionEditText)
        val tipoLugarEditText = findViewById<EditText>(R.id.tipoLugarEditText)
        val idiomasEditText = findViewById<EditText>(R.id.idiomasEditText)
        val etiquetasEditText = findViewById<EditText>(R.id.etiquetasEditText)
        val latitudEditText = findViewById<EditText>(R.id.latitudEditText)
        val longitudEditText = findViewById<EditText>(R.id.longitudEditText)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarLugar)

        btnGuardar.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val pais = paisEditText.text.toString()
            val ciudad = ciudadEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val imagenUrl = imagenUrlEditText.text.toString()
            val fechaFundacion = fechaFundacionEditText.text.toString()
            val tipoLugar = tipoLugarEditText.text.toString()
            val idiomas = idiomasEditText.text.toString().split(",").map { it.trim() }
            val etiquetas = etiquetasEditText.text.toString().split(",").map { it.trim() }
            val latitud = latitudEditText.text.toString().toDoubleOrNull()
            val longitud = longitudEditText.text.toString().toDoubleOrNull()

            if (nombre.isBlank() || pais.isBlank() || ciudad.isBlank() || descripcion.isBlank() ||
                imagenUrl.isBlank() || fechaFundacion.isBlank() || tipoLugar.isBlank() ||
                idiomas.isEmpty() || etiquetas.isEmpty() || latitud == null || longitud == null
            ) {
                Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lugar = Lugar(
                nombre = nombre,
                descripcion = descripcion,
                ciudad = ciudad,
                pais = pais,
                imagen_url = imagenUrl,
                fecha_fundacion = fechaFundacion,
                tipo_lugar = tipoLugar,
                idiomas = idiomas,
                etiquetas = etiquetas,
                coordenadas = Coordenadas(latitud, longitud)
            )

            // Enviar el lugar al backend
            ApiClient.lugarApiService.agregarLugar(lugar).enqueue(object : Callback<Lugar> {
                override fun onResponse(call: Call<Lugar>, response: Response<Lugar>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AgregarLugarActivity, "Lugar agregado exitosamente", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la actividad
                    } else {
                        Toast.makeText(this@AgregarLugarActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Lugar>, t: Throwable) {
                    Toast.makeText(this@AgregarLugarActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}