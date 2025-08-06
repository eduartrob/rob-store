import com.robstore.features.weather.domain.model.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather>

}