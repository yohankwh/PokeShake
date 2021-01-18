package com.example.pokeshake;

import android.content.Context;
import android.hardware.SensorManager;
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
    private int level;
    private int growthRate;
    private int expPool;
    private boolean leveledUp;

    private PokeBlueprint blueprint;

    public TrainPresenter(FragmentListener fl, int level, int growthRate){
        this.fragmentListener = fl;
        this.allowTraining = true;
        this.level = level;
        this.growthRate = growthRate;
        setNewExpPool();
    }

    public void addExp(Pokemon pokemon){
        if(this.allowTraining){
            pokemon.train();
            if(pokemon.getCurExp()>this.expPool){
                pokemon.setLevel(pokemon.getLevel()+1);
                pokemon.setCurExp(0);
                this.level++;
                this.leveledUp = true;
                setNewExpPool();
            }
        }
    }

    public void setNewExpPool(){
        Log.d("prev expool",this.expPool+"");
        this.expPool = ExpPoolCounter.getExpPool(this.level, this.growthRate);
        Log.d("after expool",this.expPool+"");
    }

    public int getExpPool(){
        return this.expPool;
    }
    public boolean isLeveledUp(){return this.leveledUp;}
    public void resetLeveledUp(){this.leveledUp=false;}

    /*Loads needed data for next evolution*/
    public void loadTrainData(Context ctx, int curID, int evolID){
        if(!isFetching()){
            this.fetchingData = true;

            //1st Request: request to get the Evolution Chain Array
            RequestQueue queue1 = Volley.newRequestQueue(ctx);
            String evolChainURL = "https://pokeapi.co/api/v2/evolution-chain/" + evolID + "/";
            Log.d("evol chain url is ",evolChainURL);
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
                                    fetchingData = false;
                                    fragmentListener.sendBlueprint(blueprint);
                                }
                                else{//it is not the last form, the id is curID+1
                                    int nxtLvlEvol = evolChain.getJSONArray("evolution_details")
                                                    .getJSONObject(0)
                                                    .optInt("min_level",-1);
                                    //Request next pokemon data
                                    RequestQueue queue2 = Volley.newRequestQueue(ctx);
                                    String nextPokeData = "https://pokeapi.co/api/v2/pokemon/" + (curID+1) + "/";
                                    Log.d("link is ",nextPokeData);
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
                                                    }
                                                    catch (JSONException e) {e.printStackTrace();}
                                                    fetchingData = false;
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
