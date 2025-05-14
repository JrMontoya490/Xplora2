package com.example.xplora2.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
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
    private lateinit var searchView: SearchView
    private val lugares = mutableListOf<Lugar>()
    private val lugaresFiltrados = mutableListOf<Lugar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate llamado")

        recyclerView = findViewById(R.id.rvLugares)
        searchView = findViewById(R.id.searchView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lugarAdapter = LugarAdapter(lugaresFiltrados) { lugar ->
            Log.d("MAIN", "Lugar seleccionado: ${lugar.nombre}, ID: ${lugar._id}")
            if (lugar._id.isNotEmpty()) {
                val intent = Intent(this@MainActivity, DetalleActivity::class.java)
                intent.putExtra("LUGAR_ID", lugar._id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "ID del lugar no disponible", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.adapter = lugarAdapter

        // Buscar texto
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarLugares(newText)
                return true
            }
        })

        // Cargar desde API
        cargarLugaresDesdeAPI()

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
                        lugares.clear()
                        lugares.addAll(lugaresApi)
                        filtrarLugares("") // Mostrar todos al inicio
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

    private fun filtrarLugares(texto: String?) {
        lugaresFiltrados.clear()

        if (texto.isNullOrEmpty()) {
            lugaresFiltrados.addAll(lugares)
        } else {
            val textoFiltrado = texto.lowercase()
            lugaresFiltrados.addAll(
                lugares.filter {
                    it.nombre.lowercase().contains(textoFiltrado) ||
                            it.ciudad.lowercase().contains(textoFiltrado) ||
                            it.pais.lowercase().contains(textoFiltrado)
                }
            )
        }

        lugarAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume llamado")
        cargarLugaresDesdeAPI()
    }
}
