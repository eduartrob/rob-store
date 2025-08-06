
import com.robstore.features.weather.data.model.WeatherResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface WeatherService {
    @GET("forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): Response<WeatherResponseDTO>
}