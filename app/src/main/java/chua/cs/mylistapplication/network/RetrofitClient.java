package chua.cs.mylistapplication.network;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by christopherchua on 11/15/17.
 */

public class RetrofitClient {
    private final static String BASE_URL = "https://api.androidhive.info/list_paging/";
    private final static int TIME_OUT = 5;

    Retrofit retrofit = null;

    private RetrofitClient() {
    }

    public static RetrofitClient getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public Retrofit getClient() {
        if (retrofit == null) {
            final SimpleXmlConverterFactory converterFactory =
                    SimpleXmlConverterFactory.createNonStrict(
                            new Persister(new AnnotationStrategy()));

            final OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();

            final Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(converterFactory);

            retrofit = builder.build();
        }
        return retrofit;
    }

    private static class SingletonHelper {
        private static final RetrofitClient INSTANCE = new RetrofitClient();
    }
}
