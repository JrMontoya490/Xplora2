package com.example.xplora2.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xplora2.view.AgregarLugarActivity
import com.example.xplora2.view.DetalleActivity
import com.example.xplora2.R
import com.example.xplora2.adapter.LugarAdapter
import com.example.xplora2.controller.ApiClient
import com.example.xplora2.model.Lugar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var lugarAdapter: LugarAdapter
    private val lugares = mutableListOf<Lugar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate llamado")

        recyclerView = findViewById(R.id.rvLugares)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter con onClick que pasa el ID del lugar a DetalleActivity
        lugarAdapter = LugarAdapter(lugares) { lugar ->
            Log.d("MAIN", "Lugar seleccionado: ${lugar.nombre}, ID: ${lugar._id}") // Verifica el ID
            if (lugar._id.isNotEmpty()) {
                val intent = Intent(this@MainActivity, DetalleActivity::class.java)
                intent.putExtra("LUGAR_ID", lugar._id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "ID del lugar no disponible", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = lugarAdapter

        // Cargar lugares desde la API
        cargarLugaresDesdeAPI()

        // Botón para agregar nuevo lugar
        val btnAgregarLugar = findViewById<Button>(R.id.btnAgregarLugar)
        btnAgregarLugar.setOnClickListener {
            val intent = Intent(this, AgregarLugarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarLugaresDesdeAPI() {
        Log.d("MainActivity", "cargarLugaresDesdeAPI llamado")
        ApiClient.lugarApiService.getLugares().enqueue(object : Callback<List<Lugar>> {
            override fun onResponse(call: Call<List<Lugar>>, response: Response<List<Lugar>>) {
                Log.d("MainActivity", "onResponse llamado. Código: ${response.code()}")
                if (response.isSuccessful) {
                    response.body()?.let { lugaresApi ->
                        Log.d("MainActivity", "Cuerpo de la respuesta no nulo. Cantidad de lugares: ${lugaresApi.size}")
                        lugarAdapter.actualizarLugares(lugaresApi)
                        Log.d("MainActivity", "actualizarLugares llamado")
                    } ?: run {
                        Log.w("MainActivity", "Cuerpo de la respuesta nulo.")
                    }
                } else {
                    Log.e("MainActivity", "Error en la respuesta de la API. Código: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Lugar>>, t: Throwable) {
                Log.e("MainActivity", "onFailure llamado. Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Error al cargar lugares", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume llamado")
        cargarLugaresDesdeAPI()
    }
}