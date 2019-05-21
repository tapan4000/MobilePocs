package com.example.tapanj.mapsdemo.integration.Retrofit;

import com.example.tapanj.mapsdemo.common.Utility.Utility;
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
import java.util.Date;

public class ServiceBuilder {
    //private static final String URL = "http://cmpclsstgntfrontend.eastus.cloudapp.azure.com:8085/FrontEndService/api/";
    private static final String URL = "http://10.0.2.2:8085/FrontEndService/api/";

    // Create logger
    private static HttpLoggingInterceptor httpLoggingInterceptor =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static Interceptor jsonResponseAnalyserIntercepter = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("auth", "")
                    .addHeader("App-Id", "24656349-8bd1-4447-93e2-a576a93b80fb")
                    .build();
            Response response = chain.proceed(authenticatedRequest);
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
    // TODO: Make sure that the retrofit object is being reused and being created on each request.
    private  static OkHttpClient.Builder okHttpClient =
            new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).addInterceptor(jsonResponseAnalyserIntercepter);

    // Create retrofit object

    private static Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDateDeserializer()).create();

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(new LiveDataAdapterFactory())
            .client(okHttpClient.build());

    private static Retrofit retrofit = builder.build();

    public static <S> S buildService(Class<S> serviceType){
        return retrofit.create(serviceType);
    }
}
