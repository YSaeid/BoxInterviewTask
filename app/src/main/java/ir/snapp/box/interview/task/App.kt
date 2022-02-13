package ir.snapp.box.interview.task

import android.app.Application
import com.google.gson.GsonBuilder
import ir.snapp.box.interview.task.data.AppRepositoryImpl
import ir.snapp.box.interview.task.repository.datasource.retrofit.ApiServices
import ir.snapp.box.interview.task.ui.MainViewModel
import ir.snapp.box.interview.task.utils.AppContext
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class App : Application() {

    private val timeout = 30000L

    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
        startKoin {
            androidContext(this@App)
            modules(appModule())
        }
    }

    private fun appModule() = module {
        single { AppContext(androidContext()) }
        single {
            Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(
                    OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.MILLISECONDS)
                        .readTimeout(timeout, TimeUnit.MILLISECONDS)
                        .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                        .callTimeout(timeout, TimeUnit.MILLISECONDS).build()
                )
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                    )
                )
                .build()
                .create(ApiServices::class.java)
        }
        single { AppRepositoryImpl(get()) }
        viewModel { MainViewModel(get()) }
    }

}