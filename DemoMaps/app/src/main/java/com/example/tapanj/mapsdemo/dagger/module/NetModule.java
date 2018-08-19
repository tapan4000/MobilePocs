package com.example.tapanj.mapsdemo.dagger.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;
import java.io.IOException;

@Module
public class NetModule {
    String baseUrl;

    public NetModule(String baseUrl){
        this.baseUrl = baseUrl;
    }

    @Provides
    @Reusable
    Gson gson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    // TODO: Also add the interceptor for sending the token. If the token has expired then the intercepter can make the login call to refresh token
    // TODO: Add the interceptor that logs every outgoing call.
    // TODO: Add retry for transient errors.
    // TODO: Add Http Headers with interceptor.
    // TODO: Add the read/connect timeouts as per requirement.
    // TODO: A request can be cancelled using request.Cancel. Determine if we need to cancel all requests if user moves out of the pages.
    // TODO: Use a progress bar whenever we make a Web call.
    @Provides
    @Reusable
    OkHttpClient okHttpClient(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor jsonResponseInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                return response;
            }
        };

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(jsonResponseInterceptor);
        return httpClientBuilder.build();
    }

    @Provides
    @Reusable
    Retrofit retrofit(Gson gson, OkHttpClient okHttpClient){
        return new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient).build();
    }
}
