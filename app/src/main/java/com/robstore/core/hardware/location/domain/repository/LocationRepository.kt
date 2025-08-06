package com.robstore.core.hardware.location.domain.repository

import android.location.Location


interface LocationRepository {
    suspend fun getLastLocation(): Result<Location>
    suspend fun getRegionFromLocation(location: Location): Result<String>
    suspend fun getLatLng(): Result<Pair<Double, Double>>

}
