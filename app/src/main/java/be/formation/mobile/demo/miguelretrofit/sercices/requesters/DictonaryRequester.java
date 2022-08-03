package be.formation.mobile.demo.miguelretrofit.sercices.requesters;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.formation.mobile.demo.miguelretrofit.models.DictionaryResult;
import be.formation.mobile.demo.miguelretrofit.sercices.requesters.api.DictionaryApiService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DictonaryRequester {

    public interface IDictonaryRequesterListener {
        void onResult(String error, List<DictionaryResult> results);
    }

    private DictionaryApiService service;
    private IDictonaryRequesterListener requesterListener;

    public DictonaryRequester(@NonNull IDictonaryRequesterListener requesterResult) {

        // Initialisation de retrofit et création du service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dictionaryapi.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(DictionaryApiService.class);

        // Recuperation du listener pour traiter le resultat de la requete
        requesterListener = requesterResult;
    }

    public void searchWord(String word) {

        // Création d'un "executor" pour travailler dans un thread dédié
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // Utilisation de l'executor pour réaliser la requete (en background)
        executor.submit(new Runnable() {

            // Note : Le code peut être ecrit sous forme d'expression Lambda
            @Override
            public void run() {
                Call<List<DictionaryResult>> dicoCall = service.getDefinition(word);

                List<DictionaryResult> results = null;
                String error = null;

                try {
                    Response<List<DictionaryResult>> response = dicoCall.execute();

                    if(response.isSuccessful()) {
                        results = response.body();
                    }
                    else {
                        error = "Le mot n'a pas été trouvé !";
                    }

                } catch (IOException e) {
                    error = e.getMessage();
                }

                // Déclanchement d'un méthode pour retourner dans le thread principal
                executeInMainThread(error, results);
            }
        });
    }

    private void executeInMainThread(String error, List<DictionaryResult> results) {

        // Récuperation du thread principal (Thread UI)
        Handler uiThread = new Handler(Looper.getMainLooper());

        // Utilisation du thread du principal pour renvoyer les données (via le listener)
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                requesterListener.onResult(error, results);
            }
        });
    }

}
