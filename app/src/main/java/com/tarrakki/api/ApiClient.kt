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
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import org.supportcompact.CoreApp
import org.supportcompact.R
import org.supportcompact.events.Maintenance
import org.supportcompact.ktx.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


/**
 *
* http://13.235.141.219/admin13579/
* admin/Drc@1234
 *
 * */

object ApiClient {

    private val OKHTTP_TIMEOUT = 30 * 10 // seconds
    private var retrofit: Retrofit? = null
    private var retrofitHeader: Retrofit? = null
    private lateinit var okHttpClient: OkHttpClient
    //const val BUILD_TYPE_DEBUG = true

    /***
     * CAMS api base url Test Url
     * */
    const val PASSKEY = "S1DSS#q76S458G9h6u5DF7pk5T7Lpart"
    /*private const val CAMS_API_BASE_URL = "https://eiscuat1.camsonline.com/cispl/services_kycenquiry.asmx/"
    const val CAMS_USER_ID = "PLUTOWS"
    const val CAMS_PASSWORD = "kra\$36369"*/


    /***
     * CAMS api base url Live Url
     * */
    private const val CAMS_API_BASE_URL = "https://www.camskra.com/"
    const val CAMS_USER_ID = "PLUTOWS"
    const val CAMS_PASSWORD = "kra\$36369"

    /**
     * Tarrakki Jyada 172.10.29.38:8000
     * */
    /*private const val BASE_URL = "http://172.10.29.38:8002/api/v1/"
    const val IMAGE_BASE_URL = "http://172.10.29.38:8002"
    const val BANK_REDIRECT_URL = "http://172.10.29.38:8002/api/v1/transactions/payment-status/"*/

    /*private const val BASE_URL = "http://172.10.29.36:8005/api/v1/"
    const val IMAGE_BASE_URL = "http://172.10.29.36:8005"
    const val BANK_REDIRECT_URL = "http://172.10.29.36:8005/api/v1/transactions/payment-status/"*/

    /**
     * Staging Test Url
     **/
    /*private const val BASE_URL = "http://13.235.141.219/api/v1/" /// Latest url
    const val IMAGE_BASE_URL = "http://13.235.141.219" /// Latest url
    const val BANK_REDIRECT_URL = "http://13.235.141.219/api/v1/transactions/payment-status/"*/

    /**
     * Test Url`
     **/
    /*private const val BASE_URL = "http://172.10.24.51:8000/api/v2/" /// Latest url
    const val IMAGE_BASE_URL = "http://172.10.24.51:8000" /// Latest urls
    const val BANK_REDIRECT_URL = "http://172.10.24.51:8000/api/v2/transactions/payment-status/"*/

    /***
     * Beta URL
     * */
    /*private const val BASE_URL = "http://13.235.124.120/api/v2/"
    const val IMAGE_BASE_URL = "http://13.235.124.120"
    const val BANK_REDIRECT_URL = "http://13.235.124.120/api/v2/transactions/payment-status/"*/

    /**
     * Live Url
     **/
    /*private const val BASE_URL = "http://tarrakki.edx.drcsystems.com/api/v1/" /// Latest url
    const val IMAGE_BASE_URL = "http://tarrakki.edx.drcsystems.com" /// Latest url
    const val BANK_REDIRECT_URL = "http://tarrakki.edx.drcsystems.com/api/v1/transactions/payment-status/" /// Latest url*/

    /**
     * Live Url
     **/
    /*private const val BASE_URL = "https://tarrakkilive.edx.drcsystems.com/api/v1/" /// Latest url
    const val IMAGE_BASE_URL = "https://tarrakkilive.edx.drcsystems.com" /// Latest url
    const val BANK_REDIRECT_URL = "https://tarrakkilive.edx.drcsystems.com/api/v1/transactions/payment-status/" /// Latest url*/


    /*private const val BASE_URL = "http://172.10.24.51:8000/api/v2/" /// Latest url
    const val IMAGE_BASE_URL = "http://172.10.24.51:8000" /// Latest url
    const val BANK_REDIRECT_URL = "http://172.10.24.51:8000/api/v2/transactions/payment-status/" /// Latest url*/

    private const val BASE_URL = "http://13.235.141.219/api/v3/"
    const val IMAGE_BASE_URL = "http://13.235.141.219"
    const val BANK_REDIRECT_URL = "http://13.235.141.219/api/v3/transactions/payment-status/"


    /*private const val BASE_URL = "https://6fbf1fa5.ngrok.io/api/v3/"
    const val IMAGE_BASE_URL = "https://6fbf1fa5.ngrok.io"
    const val BANK_REDIRECT_URL = "https://6fbf1fa5.ngrok.io/api/v3/transactions/payment-status/"*/

    /*private const val BASE_URL = "http://172.10.24.81:9000/api/v3/"
    const val IMAGE_BASE_URL = "http://172.10.24.81:9000"
    const val BANK_REDIRECT_URL = "http://172.10.24.81:9000/api/v3/transactions/payment-status/"*/

    /**
     * Tarrakki Jyada 172.10.29.38:8000
     * */
    /*private const val BASE_URL = "http://172.10.24.81:8000/api/v3/"
    const val IMAGE_BASE_URL = "http://172.10.24.81:8000"
    const val BANK_REDIRECT_URL = "http://172.10.24.81:8000/api/v3/transactions/payment-status/"*/

    /**
     * Live Url
     **/
    /*private const val BASE_URL = "https://www.tarrakki.com/api/v2/" /// Latest url
    const val IMAGE_BASE_URL = "https://www.tarrakki.com" /// Latest url
    const val BANK_REDIRECT_URL = "https://tarrakki.com/api/v2/transactions/payment-status/" /// Latest url*/

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