package com.example.xplora2.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var detalleLauncher: ActivityResultLauncher<Intent>

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var lugarAdapter: LugarAdapter
    private val lugares = mutableListOf<Lugar>()
    private val lugaresFiltrados = mutableListOf<Lugar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.rvLugares)
        searchView = findViewById(R.id.searchView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lugarAdapter = LugarAdapter(lugaresFiltrados) { lugar ->
            if (lugar._id.isNotEmpty()) {
                val intent = Intent(this, DetalleActivity::class.java).apply {
                    putExtra("LUGAR_ID", lugar._id)
                }
                detalleLauncher.launch(intent)
            } else {
                Toast.makeText(this, "ID del lugar no disponible", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = lugarAdapter

        // Listener del SearchView para filtrar en tiempo real
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarLugares(newText)
                return true
            }
        })

        findViewById<FloatingActionButton>(R.id.fabAgregarLugar).setOnClickListener {
            startActivity(Intent(this, AgregarLugarActivity::class.java))
        }

        findViewById<FloatingActionButton>(R.id.fabCerrarSesion).setOnClickListener {
            cerrarSesion()
        }

        // Registrar launcher para recibir resultados desde DetalleActivity
        detalleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val huboCambios = result.data?.getBooleanExtra("HUBO_CAMBIOS", false) ?: false
                if (huboCambios) cargarLugaresDesdeAPI()
            }
        }

        cargarLugaresDesdeAPI()
    }

    //Llama a la API y carga la lista de lugares
    private fun cargarLugaresDesdeAPI() {
        ApiClient.lugarApiService.getLugares().enqueue(object : Callback<List<Lugar>> {
            override fun onResponse(call: Call<List<Lugar>>, response: Response<List<Lugar>>) {
                if (response.isSuccessful) {
                    response.body()?.let { lugaresApi ->
                        lugares.clear()
                        lugares.addAll(lugaresApi)
                        filtrarLugares("")
                    } ?: Log.w("MainActivity", "Respuesta vacía.")
                } else {
                    Log.e("MainActivity", "Error en respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Lugar>>, t: Throwable) {
                Log.e("MainActivity", "Error de red: ${t.message}")
                Toast.makeText(this@MainActivity, "Error al cargar lugares", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Filtra la lista según el texto introducido en el SearchView.

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

    //Cierra la sesión de Firebase y Google

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()

        val googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
        googleSignInClient.signOut().addOnCompleteListener {

            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
