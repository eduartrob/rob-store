import com.robstore.features.weather.domain.model.Weather


class WeatherRepositoryImpl(
    private val api: WeatherService
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather> {
        return try {
            val response = api.getCurrentWeather(lat, lon)
            if (response.isSuccessful) {
                val body = response.body()?.current_weather
                if (body != null) {
                    Result.success(
                        Weather(
                            temperature = body.temperature,
                            windspeed = body.windspeed,
                            winddirection = body.winddirection,
                            weathercode = body.weathercode,
                            time = body.time
                        )
                    )
                } else {
                    Result.failure(Exception("Respuesta vacía"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
