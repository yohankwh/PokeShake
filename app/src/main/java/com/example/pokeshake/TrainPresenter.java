package com.example.pokeshake;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class TrainPresenter {
    private FragmentListener fragmentListener;
    private boolean fetchingData;
    private boolean allowTraining;

    private PokeBlueprint blueprint;

    public TrainPresenter(FragmentListener fl){
        this.fragmentListener = fl;
    }

    /*Loads needed data for next evolution*/
    public void loadTrainData(Context ctx, int curID, int evolID){
        if(!isFetching()){
            this.fetchingData = true;

            //1st Request: request to get the Evolution Chain Array
            RequestQueue queue1 = Volley.newRequestQueue(ctx);
            String evolChainURL = "https://pokeapi.co/api/v2/evolution-chain/" + evolID + "/";
            StringRequest evolChainRequest = new StringRequest(Request.Method.GET, evolChainURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //2nd Request: request to get next pokemon evolution data
                            int checkID = -1;
                            try {
                                JSONObject evolChain = new JSONObject(response).getJSONObject("chain");
                                JSONArray tempArray;

                                while(checkID != curID){//checks whether it's the last pokemon form :)
                                    Log.d("pls","i want sleep");
                                    URI uri = new URI(evolChain.getJSONObject("species").getString("url"));
                                    //Pecahin dari URL jadi dapet ID Pokemon
                                    String[] segments = uri.getPath().split("/");
                                    String idStr = segments[segments.length - 1];
                                    checkID = Integer.parseInt(idStr);

                                    tempArray = evolChain.getJSONArray("evolves_to");
                                    if(tempArray.length()>0){
                                        evolChain = tempArray.getJSONObject(0);
                                    }else{
                                        evolChain = new JSONObject().put("flag",true);
                                    }
                                }

                                if(evolChain.has("flag")){//it is the last form, send blank class
                                    blueprint = new PokeBlueprint(-1,"","",new int[0],0);
                                }else{//it is not the last form, the id is curID+1
                                    int nxtLvlEvol = evolChain.getJSONArray("evolution_details")
                                                    .getJSONObject(0)
                                                    .getInt("min_level");
                                    //Request next pokemon data
                                    RequestQueue queue2 = Volley.newRequestQueue(ctx);
                                    String nextPokeData = "https://pokeapi.co/api/v2/pokemon/" + (curID+1) + "/";
                                    StringRequest nextPokeRequest = new StringRequest(Request.Method.GET, nextPokeData,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject nextPokeResp = new JSONObject(response);
                                                        int nextId = nextPokeResp.getInt("id");
                                                        String nextName = nextPokeResp.getString("name");
                                                        int[] nextStats = new int[6];
                                                        JSONArray stats = nextPokeResp.getJSONArray("stats");
                                                        for(int i = 0; i < stats.length(); i++) {
                                                            nextStats[i] = stats.getJSONObject(i).getInt("base_stat");
                                                        }
                                                        JSONArray types = nextPokeResp.getJSONArray("types");
                                                        String nextTypes="";
                                                        for(int i = 0; i < types.length(); i++){
                                                            nextTypes+="#"+types.getJSONObject(i).getJSONObject("type").getString("name");
                                                        }
                                                        blueprint = new PokeBlueprint(nextId, nextName, nextTypes, nextStats, nxtLvlEvol);
                                                        fragmentListener.sendBlueprint(blueprint);
                                                        fetchingData = false;
                                                    }
                                                    catch (JSONException e) {e.printStackTrace();}
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    //show sneaker message: Connection Problem
                                                    fetchingData = false;
                                                }
                                            }
                                    );
                                    queue2.add(nextPokeRequest);
                                }
                            }
                            catch (JSONException | URISyntaxException e) {e.printStackTrace();}
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //show sneaker message: Connection Problem
                            fetchingData = false;
                        }
                    }
            );
            queue1.add(evolChainRequest);
        }
    }

    public PokeBlueprint getPokeBlueprint(){
        return this.blueprint;
    }

    public boolean isFetching(){
        return this.fetchingData;
    }
    public boolean isAllowTraining(){return this.allowTraining;}
}
