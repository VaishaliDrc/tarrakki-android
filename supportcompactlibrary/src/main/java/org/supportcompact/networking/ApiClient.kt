package org.supportcompact.networking

import android.util.Log
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import org.supportcompact.CoreApp
import org.supportcompact.R
import org.supportcompact.ktx.getLoginToken
import org.supportcompact.ktx.isNetworkConnected
import org.supportcompact.ktx.postError
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


object ApiClient {

    private val OKHTTP_TIMEOUT = 30 // seconds
    private var retrofit: Retrofit? = null
    private var retrofitHeader: Retrofit? = null
    private lateinit var okHttpClient: OkHttpClient
    const val BUILD_TYPE_DEBUG = true

    /***
     * CAMS api base url Test Url
     * */
    const val PASSKEY = "S1DSS#q76S458G9h6u5DF7pk5T7Lpart"
    /*private const val CAMS_API_BASE_URL = "https://eiscuat1.camsonline.com/cispl/services_kycenquiry.asmx"
    const val CAMS_USER_ID = "PLUTOWS"
    const val CAMS_PASSWORD = "kra\$36369"*/

    /***
     * CAMS api base url Live Url
     * */
    private const val CAMS_API_BASE_URL = "https://www.camskra.com/"
    const val CAMS_USER_ID = "PLUTOWS"
    const val CAMS_PASSWORD = "kra\$36369"

    /**
     * Test Url
     **/
/*    private const val BASE_URL = "http://172.10.29.76:8005/api/v1/" /// Latest url
    const val IMAGE_BASE_URL = "http://172.10.29.76:8005" /// Latest url*/

    /**
     * Live Url
     **/

      private const val BASE_URL = "http://tarrakki.edx.drcsystems.com/api/v1/" /// Latest url
      const val IMAGE_BASE_URL = "http://tarrakki.edx.drcsystems.com" /// Latest url
    /**
     * @return [Retrofit] object its single-tone
     */

    fun clear() {
        retrofit = null
        retrofitHeader = null
    }

    fun getApiClient(): Retrofit {
        if (retrofit == null) {
            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create()

            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOKHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }
        return retrofit!!
    }

    fun getSOAPClient(baseUrl: String = CAMS_API_BASE_URL): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val strategy = AnnotationStrategy()
        val serializer = Persister(strategy)
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build()

        val retrofit = Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(serializer))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addConverterFactory(StringConverterFactory.create(serializer))
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()
        return retrofit
    }

    /**
     * You can create multiple methods for different BaseURL
     *
     * @return [Retrofit] object
     */

    fun getApiClient(baseUrl: String): Retrofit {
        /*val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create()*/
        val builder = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)

        if (BUILD_TYPE_DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        builder.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.header("Content-Type", "application/x-www-form-urlencoded")
            chain.proceed(requestBuilder.build())
        }

        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                //.addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    /**
     * settings like caching, Request Timeout, Logging can be configured here.
     *
     * @return [OkHttpClient]
     */
    private fun getOKHttpClient(): OkHttpClient {
        return if (!::okHttpClient.isInitialized) {
            val builder = OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)

            if (BUILD_TYPE_DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(loggingInterceptor)
            }

            builder.addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                requestBuilder.header("Accept", "application/json")
                requestBuilder.header("api-key", "gduy$&#(@0jdfid")
                chain.proceed(requestBuilder.build())
            }
            okHttpClient = builder.build()
            okHttpClient
        } else {
            okHttpClient
        }
    }


    fun getHeaderClient(header: String? = CoreApp.getInstance().getLoginToken()): Retrofit {

        if (retrofitHeader == null) {
            val builder = OkHttpClient.Builder()
                    .addInterceptor(HeaderInterceptor(header))
                    .retryOnConnectionFailure(true)
                    .connectTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(OKHTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)

            if (BUILD_TYPE_DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(loggingInterceptor)
            }

            val okHttpClient = builder.build()

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create()

            retrofitHeader = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

        }
        return retrofitHeader!!
    }

    private class HeaderInterceptor internal constructor(private val headerString: String?) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            Log.v("Service", "Request")

            val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("api-key", "gduy$&#(@0jdfid")
                    .addHeader("Authorization", "Bearer $headerString")
                    .build()

            return chain.proceed(request)
        }
    }
}

interface SingleCallback<A> {
    /**
     * @param o        Whole response Object
     * @param apiNames [A] to differentiate Apis
     */
    fun onSingleSuccess(o: Any?, apiNames: A)

    /**
     * @param throwable returns [Throwable] for checking Exception
     * @param apiNames  [A] to differentiate Apis
     */
    fun onFailure(throwable: Throwable, apiNames: A)
}

fun <T, A> subscribeToSingle(observable: Observable<T>, apiNames: A, singleCallback: SingleCallback<A>?) {
    Single.fromObservable(observable)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<T> {
                override fun onSuccess(t: T) {
                    singleCallback?.onSingleSuccess(t, apiNames)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    when (e) {
                        is SocketTimeoutException -> e.postError(R.string.try_again_to)
                        is IOException -> {
                            if (CoreApp.getInstance().isNetworkConnected()) {
                                e.postError(R.string.server_connection)
                            } else {
                                e.postError(R.string.internet_connection)
                            }
                        }
                        else -> singleCallback?.onFailure(e, apiNames)
                    }
                }
            })
}