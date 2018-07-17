package com.example.tapanj.mapsdemo.integration.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ServiceBuilder {
    private static final String URL = "http://10.0.2.2:8080/FrontEndService/api/";

    // Create logger
    private static HttpLoggingInterceptor httpLoggingInterceptor =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static Interceptor jsonResponseAnalyserIntercepter = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            return response;
        }
    };

    // TODO: Also add the interceptor for sending the token. If the token has expired then the intercepter can make the login call to refresh token
    // TODO: Add the interceptor that logs every outgoing call.
    // TODO: Add retry for transient errors.
    // TODO: Add Http Headers with interceptor.
    // TODO: Add the read/connect timeouts as per requirement.
    // TODO: A request can be cancelled using request.Cancel. Determine if we need to cancel all requests if user moves out of the pages.
    // TODO: Use a progress bar whenever we make a Web call.
    private  static OkHttpClient.Builder okHttpClient =
            new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).addInterceptor(jsonResponseAnalyserIntercepter);

    // Create retrofit object
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build());

    private static Retrofit retrofit = builder.build();

    public static <S> S buildService(Class<S> serviceType){
        return retrofit.create(serviceType);
    }
}
