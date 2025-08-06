import com.robstore.features.weather.domain.model.Weather

class WeatherUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double
    ): Result<Weather> {
        return repository.getCurrentWeather(latitude, longitude)
    }
}