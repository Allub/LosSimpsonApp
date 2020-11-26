package cl.inacap.lossimpsonapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cl.inacap.lossimpsonapp.adapters.PersonajesAdapter;
import cl.inacap.lossimpsonapp.dto.Personaje;

public class MainActivity extends AppCompatActivity {

    private List<Personaje> personajes = new ArrayList<>();
    private ListView personajesList;
    private PersonajesAdapter personajesAdapter;
    private RequestQueue queue;
    private Spinner spinner;
    private Button solicitar;
    private Integer[] citas = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queue = Volley.newRequestQueue(this);
        this.personajesList = findViewById(R.id.lista_citas);
        this.personajesAdapter = new PersonajesAdapter(this
                , R.layout.list_personajes, this.personajes);
        this.personajesList.setAdapter(this.personajesAdapter);
        this.spinner = findViewById(R.id.spinner);
        ArrayAdapter<Integer> spinnerAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, citas);
        spinner.setAdapter(spinnerAdapter);
        this.solicitar = findViewById(R.id.solicitar_consejo);
        this.solicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nCitas = spinner.getSelectedItem().toString();
                final String url = "https://thesimpsonsquoteapi.glitch.me/quotes?count=";
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                        Request.Method.GET, url + nCitas
                        , null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            personajes.clear();
                            Personaje[] arreglo = new Gson()
                                    .fromJson(response.toString(), Personaje[].class);
                            personajes.addAll(Arrays.asList(arreglo));
                        } catch (Exception ex) {
                            personajes.clear();
                            Log.e("PERSONAJES_FRAGMENT", "Error de peticion");
                        } finally {
                            personajesAdapter.notifyDataSetChanged();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        personajes.clear();
                        Log.e("PERSONAJES FRAGMENT", error.toString());
                        personajesAdapter.notifyDataSetChanged();
                    }
                });
                queue.add(jsonArrayRequest);
            }
        });

    }
}