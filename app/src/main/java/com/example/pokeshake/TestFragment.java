package com.example.pokeshake;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TestFragment extends Fragment implements View.OnClickListener {
    private FragmentListener fragmentListener;
    private Button test;
    private TextView testtv;
    private ImageView testiv;
    private Button trainTestBtn;

    private final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";
    private final String SPECIES_URL = "https://pokeapi.co/api/v2/pokemon-species/";

    private Pokemon pokemon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_testing, container, false);

        this.test = view.findViewById(R.id.btn_test);
        this.testtv = view.findViewById(R.id.tv_test);
        this.testiv = view.findViewById(R.id.iv_test);
        this.trainTestBtn = view.findViewById(R.id.train_btn_test);

        this.test.setOnClickListener(this);
        this.trainTestBtn.setOnClickListener(this);

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentListener){
            this.fragmentListener = (FragmentListener) context;
        } else{
            throw new ClassCastException(context.toString()
                    + "must implement FragmentListener");
        }
    }
//
//    public void sendAPIRequest(int id){
//        Context ctx = this.getContext();
//        int levelSelected = 5;
//
//        String url = SPECIES_URL+id;
//
//        RequestQueue queue = Volley.newRequestQueue(this.getContext());
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        RequestQueue queue2 = Volley.newRequestQueue(ctx);
//                        Pokemon pkmn = new Pokemon(1,"2",3,4);
//
//                        try {
//                            JSONObject growth = new JSONObject(response);
//                            String growthUrl = ((JSONObject)growth.get("growth_rate")).getString("url");
//                            String evolveUrl = ((JSONObject)growth.get("evolution_chain")).getString("url");
//
//                            StringRequest req2 = new StringRequest(Request.Method.GET, growthUrl,
//                                new Response.Listener<String>() {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        RequestQueue queue3 = Volley.newRequestQueue(ctx);
//
//                                        try {
//                                            JSONArray levelsArr = new JSONObject(response).getJSONArray("levels");
//                                            JSONObject expForCurrLvl = (JSONObject)levelsArr.get(levelSelected - 1);
//                                            pkmn.setExpPool(expForCurrLvl.getInt("experience"));
//
//                                            StringRequest req3 = new StringRequest(Request.Method.GET, evolveUrl,
//                                                new Response.Listener<String>() {
//                                                    @Override
//                                                    public void onResponse(String response) {
//                                                        try {
//                                                            JSONObject chain = (JSONObject) new JSONObject(response).get("chain");
//                                                            if(chain.getString("species").equals(pkmn.getName())){
//
//                                                            }
//                                                        } catch (JSONException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                    }
//                                                }, new Response.ErrorListener() {
//                                                @Override
//                                                public void onErrorResponse(VolleyError error) {
//                                                    testtv.setText("That didn't work!");
//                                                }
//                                            });
//                                            queue3.add(req3);
//
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    testtv.setText("That didn't work!");
//                                }
//                            });
//                            queue2.add(req2);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                testtv.setText("That didn't work!");
//            }
//        });
//
//        queue.add(stringRequest);
//    }
//
//    public void registerPokemon(Pokemon pkmn){
//        this.pokemon = pkmn;
//        String toShow = "name: "+pkmn.getName()+"\n lvl: "+pkmn.getLevel()+"\n exp_pool: "+pkmn.getExpPool();
//        testtv.setText(toShow);
//        Log.d("WHAT","WTF U DOIN");
//    }
//
//    public void getPokemonData(){
//        int id = 1;
//        sendAPIRequest(id);
//    }
//
//    public void train(){
//        this.pokemon.train();
//    }

    @Override
    public void onClick(View view) {
//        if(view==this.test){
//            getPokemonData();
//        }else if(view==this.trainTestBtn){
//            train();
//        }
    }
}
