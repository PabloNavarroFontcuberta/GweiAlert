package app.android.gweialert



import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GasApiServiceCheap {

    @GET("api")
    fun hitCountCheck(@Query("module") module: String,
                      @Query("action") action: String,
                      @Query("apikey") apikey: String): Observable<ModelCheap.Result>

    companion object {
        fun create(): GasApiServiceCheap {

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.etherscan.io/")
                    .build()

            return retrofit.create(GasApiServiceCheap::class.java)
        }
    }

}