package com.tarrakki.api

import android.util.Log
import com.google.gson.GsonBuilder
import com.tarrakki.App
import com.tarrakki.api.model.ApiResponse
import com.tarrakki.module.maintenance.MaintenanceActivity
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
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreApp
import org.supportcompact.R
import org.supportcompact.events.Maintenance
import org.supportcompact.ktx.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


/**
 *
 * http://13.235.141.219/admin13579/
 * admin/Drc@1234
 *
 * */

/*bhavikdarji.bd24@gmail.com
Bhavik@4292*/

object ApiClient {

    private val OKHTTP_TIMEOUT = 30 * 10 // seconds
    private var retrofit: Retrofit? = null
    private var retrofitHeader: Retrofit? = null
    private lateinit var okHttpClient: OkHttpClient

    /**
     * Remote Testing Url
     **/
    /*private const val BASE_URL = "https://1c60e7488eb7.ngrok.io/api/v3/"
    const val IMAGE_BASE_URL = "https://1c60e7488eb7.ngrok.io"
    const val BANK_REDIRECT_URL = "https://1c60e7488eb7.ngrok.io/api/v3/transactions/payment-status/"*/

   /* *
     * Staging Test Url
     **/
//    private const val BASE_URL = "http://192.168.24.51:8000/api/v4/"
//    const val IMAGE_BASE_URL = "http://192.168.24.51/"
//    const val BANK_REDIRECT_URL = "http://192.168.24.51:8000/api/v4/transactions/payment-status/"
    /**
     * Staging Test Url
     **/
 //   private const val BASE_URL = "http://13.235.141.219/api/v5/"
 //   const val IMAGE_BASE_URL = "http://13.235.141.219"
 //   const val BANK_REDIRECT_URL = "http://13.235.141.219/api/v5/transactions/payment-status/"

    /**
     * Live Url
     * https://www.
     **/
     private const val BASE_URL = "https://www.tarrakki.com/api/v7/" /// Latest url
     const val IMAGE_BASE_URL = "https://www.tarrakki.com" /// Latest url
     const val BANK_REDIRECT_URL = "https://www.tarrakki.com/api/v7/transactions/payment-status/" /// Latest url


    fun clear() {
        retrofit = null
        retrofitHeader = null
    }

    /**
     * @return [Retrofit] object its single-tone
     */
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
        return if (!ApiClient::okHttpClient.isInitialized) {
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

interface SingleCallback1<T> {
    /**
     * @param o        Whole response Object
     * @param apiNames [A] to differentiate Apis
     */
    fun onSingleSuccess(o: T)

    /**
     * @param throwable returns [Throwable] for checking Exception
     * @param apiNames  [A] to differentiate Apis
     */
    fun onFailure(throwable: Throwable)
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
                    try {
                        if (t is ApiResponse) {
                            if (t.maintenanceDetails != null && t.status?.code == 503 && t.maintenanceDetails.endTime?.hasTime() == true) {
                                EventBus.getDefault().postSticky(Maintenance(MaintenanceActivity::class.java, t.maintenanceDetails.endTime))
                            } else {
                                singleCallback?.onSingleSuccess(t, apiNames)
                            }
                        } else {
                            singleCallback?.onSingleSuccess(t, apiNames)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    when (e) {
                        is HttpException -> {
                            if (e.code() == 401) {
                                App.INSTANCE.isRefreshing.value = false
                                EventBus.getDefault().postSticky(ONLOGOUT)
                            } else if (e.code() == 500) {
                                singleCallback?.onFailure(e, apiNames)
                            } else {
                                App.INSTANCE.isRefreshing.value = false
                                e.postError(R.string.server_connection)
                            }
                        }
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

fun <T> subscribeToSingle(observable: Observable<T>, singleCallback: SingleCallback1<T>?) {
    Single.fromObservable(observable)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<T> {
                override fun onSuccess(t: T) {
                    try {
                        if (t is ApiResponse) {
                            if (t.maintenanceDetails != null && t.status?.code == 503 && t.maintenanceDetails.endTime?.hasTime() == true) {
                                EventBus.getDefault().postSticky(Maintenance(MaintenanceActivity::class.java, t.maintenanceDetails.endTime))
                            } else {
                                singleCallback?.onSingleSuccess(t)
                            }
                        } else {
                            singleCallback?.onSingleSuccess(t)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    when (e) {
                        is HttpException -> {
                            if (e.code() == 401) {
                                EventBus.getDefault().postSticky(ONLOGOUT)
                            } else if (e.code() == 500) {
                                singleCallback?.onFailure(e)
                            } else {
                                App.INSTANCE.isRefreshing.value = false
                                e.postError(R.string.server_connection)
                            }
                        }
                        is SocketTimeoutException -> e.postError(R.string.try_again_to)
                        is IOException -> {
                            if (CoreApp.getInstance().isNetworkConnected()) {
                                e.postError(R.string.server_connection)
                            } else {
                                e.postError(R.string.internet_connection)
                            }
                        }
                        else -> singleCallback?.onFailure(e)
                    }
                }
            })
}