package com.example.pokeshake;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

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

public class HomePresenter {
    private FragmentListener fragmentListener;
    private FrameLayout progressBarHolder;

    private long mLastClickTime = 0;
    private Button testBtn;
    private Random rand;
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    private String myLog = "myLog";

    private boolean isClaiming; //in java default is false

    public HomePresenter(FragmentListener fl){
        this.fragmentListener = fl;
        this.rand = new Random();
    }

    public int getMoney(){return this.fragmentListener.getMoney();}

    public boolean getIsClaiming(){return this.isClaiming;}

    public void changePage(int pagenum){this.fragmentListener.changePage(pagenum);}

    public void claimPokemon(Context ctx){
        if(!this.isClaiming) {//if not claiming (prevents spam click on claiming)

            this.isClaiming = true;

            //get random pokemon by evolution ID, lewat endpoint yg ini bisa hemat request (setau saya?)
            int pokeEvolID = rand.nextInt(148) + 1; //variasi ID dari 1 sampe 148 (ditentuin sama kita)

            /*Fetching Data dengan Volley*/
            //Request bagian Luar: Tujuannya dapetin ID Pokemon dan URL Speciesnya
            RequestQueue queue = Volley.newRequestQueue(ctx); //Buat Queue Object 1
            String url = "https://pokeapi.co/api/v2/evolution-chain/" + pokeEvolID + "/";    //Gabung ID ke URL yang mau di Fetch

            //Volley Request 1
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //Ambil URL dari hasil Response
                                JSONObject res = new JSONObject(response);
                                JSONObject poke = (JSONObject) ((JSONObject) res.get("chain")).get("species");
                                URI uri = new URI(poke.getString("url"));   //Ambil URL

                                //Pecahin dari URL jadi dapet ID Pokemon
                                String[] segments = uri.getPath().split("/");
                                String idStr = segments[segments.length - 1];
                                int pokeID = Integer.parseInt(idStr);   //Ambil ID

                                //Request bagian tengah: Tujuannya dapetin growth rate ID
                                RequestQueue queue2 = Volley.newRequestQueue(ctx);
                                String speciesUrl = poke.getString("url");

                                //Volley Request 2
                                StringRequest req2 = new StringRequest(Request.Method.GET, speciesUrl,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response2) {
                                                try {
                                                    JSONObject res2 = new JSONObject(response2);
                                                    URI growth_url = new URI(((JSONObject) res2.get("growth_rate")).getString("url"));
                                                    //Pecahin dari URL jadi dapet ID Growth Type
                                                    String[] segmentGrowth = growth_url.getPath().split("/");
                                                    String rateStr = segmentGrowth[segmentGrowth.length - 1];
                                                    int growth_rate = Integer.parseInt(rateStr);   //Ambil growth ID

                                                    //Request bagian dalem: Tujuannya dapetin stats pokemon
                                                    RequestQueue queue3 = Volley.newRequestQueue(ctx);
                                                    String pokeDataUrl = "https://pokeapi.co/api/v2/pokemon/" + pokeID;
                                                    StringRequest req3 = new StringRequest(Request.Method.GET, pokeDataUrl,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response3) {
                                                                    try {
                                                                        //bentuk objek pokemonnya disini
                                                                        JSONObject res3 = new JSONObject(response3);
                                                                        String name = res3.getString("name");
                                                                        int[] statsArr = new int[6];
                                                                        JSONArray stats = res3.getJSONArray("stats");
                                                                        for (int i = 0; i < stats.length(); i++) {
                                                                            JSONObject temp = (JSONObject) stats.get(i);
                                                                            statsArr[i] = temp.getInt("base_stat");
                                                                        }
                                                                        String pkmnType = "";
                                                                        JSONArray types = res3.getJSONArray("types");
                                                                        for (int i = 0; i < types.length(); i++) {
                                                                            JSONObject type = (JSONObject) ((JSONObject) types.get(i)).get("type");
                                                                            pkmnType += "#" + type.getString("name");
                                                                        }

                                                                        Pokemon newPoke = new Pokemon(pokeID, name, 0, 0, growth_rate, pkmnType);

                                                                        fragmentListener.adoptPokemon(newPoke);
                                                                        int money = fragmentListener.getMoney();
                                                                        fragmentListener.updateMoney(money - 1);
                                                                        fragmentListener.updateMoneyView();

                                                                        AlertDialog.Builder builderAlert = new AlertDialog.Builder(ctx);
                                                                        builderAlert.setTitle("Egg Claimed")
                                                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                    }
                                                                                });
                                                                        builderAlert.create();
                                                                        builderAlert.show();

                                                                        //kalo udah beres baru set flag false lagi
                                                                        isClaiming = false;
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                        }
                                                    }
                                                    );
                                                    queue3.add(req3);
                                                } catch (JSONException | URISyntaxException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                }
                                );
                                queue2.add(req2);

                            } catch (JSONException | URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }
            );//end of string request 1
            queue.add(stringRequest);
        }
    }

}
