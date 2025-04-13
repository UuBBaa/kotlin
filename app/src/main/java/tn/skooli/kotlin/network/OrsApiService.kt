package tn.skooli.kotlin.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrsApiService {
    // For driving-car profile; swap out “driving-car” for any other supported profile
    @POST("driving-car/geojson")
    suspend fun getRoute(
        @Body body: OrsDirectionsRequest
    ): Response<OrsDirectionsResponse>
}

// Request: list of [lon, lat] pairs
data class OrsDirectionsRequest(
    val coordinates: List<List<Double>>,
    val options: Map<String, Any>? = null
)

// Minimal response model for GeoJSON
data class OrsDirectionsResponse(
    val type: String,
    val features: List<Feature>
)

data class Feature(
    val type: String,
    val properties: Properties,
    val geometry: Geometry
)

data class Properties(
    val segments: List<Segment>
)

data class Segment(
    val distance: Double,
    val duration: Double,
    val steps: List<Step>
)

data class Step(
    val distance: Double,
    val duration: Double,
    val instruction: String
)

data class Geometry(
    val type: String,
    val coordinates: List<List<Double>> // [ [lon, lat], … ]
)
