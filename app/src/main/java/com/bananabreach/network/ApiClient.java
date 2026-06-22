package com.bananabreach.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static ApiClient instance;
    private Retrofit retrofit;
    private ApiService apiService;

    // Intentional: hardcoded API key shipped in the client — see docs/VULNERABILITIES.md
    private static final String API_KEY = "BB_API_DEV_KEY_2024";

    private ApiClient() {
        // Intentional: full request/response bodies logged at runtime
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .header("User-Agent", "BananaBreach/1.0")
                                .header("X-API-Key", API_KEY)
                                .header("Accept", "application/json")
                                .method(original.method(), original.body())
                                .build();

                        android.util.Log.d("ApiClient",
                                "Making request: " + request.url() +
                                        " Headers: " + request.headers());

                        return chain.proceed(request);
                    }
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Intentional: plaintext HTTP base URL — see docs/VULNERABILITIES.md
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.bananabreach.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}
