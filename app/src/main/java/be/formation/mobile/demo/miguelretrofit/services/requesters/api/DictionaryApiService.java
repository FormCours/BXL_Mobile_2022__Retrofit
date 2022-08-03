package be.formation.mobile.demo.miguelretrofit.services.requesters.api;

import java.util.List;

import be.formation.mobile.demo.miguelretrofit.models.DictionaryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    Call<List<DictionaryResult>> getDefinition(@Path("word") String word);
}



