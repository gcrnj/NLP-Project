package com.giotech.nlpproject

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class NLPService() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://172.20.10.4:8000")
        .client(getUnsafeOkHttpClient())  // <-- add unsafe client
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val nlpApi = retrofit.create(NLPApi::class.java)

    fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true } // allow all hostnames
                // Increase timeouts here
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * Usage:
     * ```
     * val pdfPart = createPdfPart(File("/path/to/file.pdf"))
     * service.processPdf(pdfPart)
     *```
     */

    private fun getNLPResponse(response: Response<NLPResponse>): NLPResponse? {
        return if (response.isSuccessful) {
            response.body()
        } else {
            // handle error (throw exception or return null)
            NLPResponse(error = response.errorBody().toString())
        }
    }

    suspend fun summarizeFromText(text: String): NLPResponse? {
        try {
            val response = nlpApi.processText(text)
            return getNLPResponse(response)
        } catch (e: Exception) {
            e.printStackTrace()
            return NLPResponse(error = e.message)
        }
    }

}