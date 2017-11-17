package chua.cs.mylistapplication.network;

import chua.cs.mylistapplication.model.remote.Menu;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by christopherchua on 11/15/17.
 */

public interface RetrofitService {

    @GET
    Call<Menu> getMenu(@Url String urlPath);
}
