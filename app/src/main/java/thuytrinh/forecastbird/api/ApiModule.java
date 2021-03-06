package thuytrinh.forecastbird.api;

import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import thuytrinh.forecastbird.BuildConfig;

@Module
public class ApiModule {
  @Provides @Singleton OkHttpClient httpClient(ExecutorService executorService) {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder()
        // Inject an appropriate ExecutorService for Android apps.
        // The default ExecutorService provided by OkHttp isn't optimal for Android apps.
        .dispatcher(new Dispatcher(executorService));
    if (BuildConfig.DEBUG) {
      final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
      builder.addInterceptor(loggingInterceptor);
    }
    return builder.build();
  }

  @Provides @Singleton Gson gson() {
    return new Gson();
  }

  @Provides ForecastApi yahooApi(OkHttpClient httpClient, Gson gson, Scheduler scheduler) {
    return new Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(scheduler))
        .baseUrl("https://query.yahooapis.com/")
        .build()
        .create(ForecastApi.class);
  }
}