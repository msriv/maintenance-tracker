package com.moto.tracker.domain.usecase.registry

import com.moto.tracker.domain.model.VinDecodeResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Named

class DecodeVinUseCase @Inject constructor(
    private val okHttpClient: OkHttpClient,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) {
    private val vinJson = Json { ignoreUnknownKeys = true }

    suspend operator fun invoke(vin: String): VinDecodeResult = withContext(ioDispatcher) {
        runCatching {
            val url = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVinValues/${vin.trim()}?format=json"
            val request = Request.Builder().url(url).build()
            val responseBody = okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@runCatching VinDecodeResult(
                    isSuccess = false,
                    errorMessage = "HTTP error: ${response.code}"
                )
                response.body?.string() ?: return@runCatching VinDecodeResult(
                    isSuccess = false,
                    errorMessage = "Empty response"
                )
            }

            val root = vinJson.parseToJsonElement(responseBody).jsonObject
            val results = root["Results"]?.jsonArray ?: return@runCatching VinDecodeResult(
                isSuccess = false,
                errorMessage = "No results in response"
            )

            if (results.isEmpty()) {
                return@runCatching VinDecodeResult(isSuccess = false, errorMessage = "VIN not found")
            }

            val result = results[0].jsonObject
            val make = result["Make"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            val model = result["Model"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            val yearStr = result["ModelYear"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            val trim = result["Trim"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            val engineType = result["EngineConfiguration"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }

            VinDecodeResult(
                make = make,
                model = model,
                year = yearStr?.toIntOrNull(),
                trim = trim,
                engineType = engineType,
                isSuccess = make != null || model != null
            )
        }.getOrElse { e ->
            VinDecodeResult(isSuccess = false, errorMessage = e.message)
        }
    }
}
