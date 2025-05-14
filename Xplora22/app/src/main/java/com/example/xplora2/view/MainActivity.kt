package com.example.xplora2.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xplora2.R
import com.example.xplora2.adapter.LugarAdapter
import com.example.xplora2.controller.ApiClient
import com.example.xplora2.model.Lugar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var detalleLauncher: ActivityResultLauncher<Intent>
    private lateinit var recyclerView: RecyclerView
    private lateinit var lugarAdapter: LugarAdapter
    private lateinit var searchView: SearchView
    private val lugares = mutableListOf<Lugar>()
    private val lugaresFiltrados = mutableListOf<Lugar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rvLugares)
        searchView = findViewById(R.id.searchView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar el adaptador
        lugarAdapter = LugarAdapter(lugaresFiltrados) { lugar ->
            if (lugar._id.isNotEmpty()) {
                val intent = Intent(this@MainActivity, DetalleActivity::class.java)
                intent.putExtra("LUGAR_ID", lugar._id)
                detalleLauncher.launch(intent)
            } else {
                Toast.makeText(this, "ID del lugar no disponible", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = lugarAdapter

        // Configurar el buscador
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarLugares(newText)
                return true
            }
        })

        // Botón para agregar lugar
        val btnAgregarLugar = findViewById<Button>(R.id.btnAgregarLugar)
        btnAgregarLugar.setOnClickListener {
            val intent = Intent(this, AgregarLugarActivity::class.java)
            startActivity(intent)
        }

        // Inicializar el launcher
        detalleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val huboCambios = result.data?.getBooleanExtra("HUBO_CAMBIOS", false) ?: false
                if (huboCambios) {
                    cargarLugaresDesdeAPI()
                }
            }
        }

        // Cargar lugares
        cargarLugaresDesdeAPI()
    }

    private fun cargarLugaresDesdeAPI() {
        ApiClient.lugarApiService.getLugares().enqueue(object : Callback<List<Lugar>> {
            override fun onResponse(call: Call<List<Lugar>>, response: Response<List<Lugar>>) {
                if (response.isSuccessful) {
                    response.body()?.let { lugaresApi ->
                        lugares.clear()
                        lugares.addAll(lugaresApi)
                        filtrarLugares("")
                    } ?: run {
                        Log.w("MainActivity", "Respuesta vacía.")
                    }
                } else {
                    Log.e("MainActivity", "Error en la respuesta. Código: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Lugar>>, t: Throwable) {
                Log.e("MainActivity", "Error de red: ${t.message}")
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

}
