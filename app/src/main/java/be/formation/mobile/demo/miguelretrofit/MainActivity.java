package be.formation.mobile.demo.miguelretrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import be.formation.mobile.demo.miguelretrofit.models.DictionaryResult;
import be.formation.mobile.demo.miguelretrofit.models.Meaning;
import be.formation.mobile.demo.miguelretrofit.sercices.requesters.DictonaryRequester;
import be.formation.mobile.demo.miguelretrofit.sercices.requesters.api.DictionaryApiService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Button btnCallApi;
    TextView tvResult;
    EditText etWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCallApi = findViewById(R.id.btn_main_api_call);
        tvResult = findViewById(R.id.tv_main_api_result);
        etWord = findViewById(R.id.et_main_word);

        btnCallApi.setOnClickListener(view -> requestApi());
    }

    /*
    private void requestApi() {
        // Utilisation « Basique » de retrofit

        //region Ugly fix for request ! Don't use this shit (╯°□°）╯︵ ┻━┻
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //endregion

        // Config de Retrofit avec le converter Gson
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dictionaryapi.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Création du service API sur base de l'interface "DictionaryApiService"
        DictionaryApiService service = retrofit.create(DictionaryApiService.class);

        // Configuration de la requete a envoyer
        String word = etWord.getText().toString();
        Call<List<DictionaryResult>> dicoCall = service.getDefinition(word);


        Response<List<DictionaryResult>> result = null;
        try {
            // Execution de la requete
            result = dicoCall.execute();
            Log.d("TEST_API", "requestApi: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    private void requestApi() {
        DictonaryRequester requester = new DictonaryRequester((error, results) -> {
            if (error != null) {
                tvResult.setText(error);
                return;
            }

            StringBuilder displayResult = new StringBuilder();
            displayResult
                    .append("Resultat pour « ").append(results.get(0).getWord()).append(" »")
                    .append("\n");
            for (DictionaryResult result : results) {
                for(Meaning meaning : result.getMeanings()) {
                    displayResult
                            .append("\t - ").append(meaning.getDefinitions().get(0).getDefinition())
                            .append("\n");
                }
            };
            tvResult.setText(displayResult);
        });

        String word = etWord.getText().toString();
        requester.searchWord(word);
    }
}