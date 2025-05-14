package com.example.xplora2

data class Lugar(
    val _id: String = "",
    val nombre: String,
    val pais: String,
    val ciudad: String,
    val descripcion: String,
    val idiomas: List<String>,
    val fecha_fundacion: String,
    val tipo_lugar: String,
    val coordenadas: Coordenadas,
    val imagen_url: String,
    val etiquetas: List<String>
)

data class Coordenadas(
    val latitud: Double,
    val longitud: Double
)
