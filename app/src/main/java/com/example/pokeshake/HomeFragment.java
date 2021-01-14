package com.example.pokeshake;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private TextView moneyTV;
    private Button adoptBtn;
    private Button pokeMenuBtn;
    private Button exitBtn;
    private Button shakeBtn;
    private FragmentListener fragmentListener;
    private FrameLayout progressBarHolder;

    private long mLastClickTime = 0;
    private Button testBtn;
    private Random rand;
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    private String myLog = "myLog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.moneyTV = view.findViewById(R.id.money_tv);
        this.moneyTV.setText("Point: "+this.fragmentListener.getMoney());

        this.adoptBtn = view.findViewById(R.id.btnAdoptEgg);
        this.pokeMenuBtn = view.findViewById(R.id.btnMyPokemon);
        this.exitBtn = view.findViewById(R.id.btnExit);
        this.progressBarHolder = view.findViewById(R.id.progressBarHolder);

        this.rand = new Random();

        this.adoptBtn.setOnClickListener(this);
        this.pokeMenuBtn.setOnClickListener(this);
        this.exitBtn.setOnClickListener(this);
        //sensor test
        this.shakeBtn = view.findViewById(R.id.btnTestShakePage);
        this.shakeBtn.setOnClickListener(this);

        this.testBtn = view.findViewById(R.id.btnTestPage);

        this.testBtn.setOnClickListener(this);

        view.setOnClickListener(this);


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

    @Override
    public void onClick(View view) {
        if(view.getId() == this.adoptBtn.getId()){
            if (SystemClock.elapsedRealtime() - mLastClickTime > 1000){
                if(this.fragmentListener.getMoney()>=1){
                    claimPokemon();
                    MyTask mt = new MyTask();
                    mt.execute();
                }
            }
            mLastClickTime = SystemClock.elapsedRealtime();

        }
        else if(view.getId() == this.pokeMenuBtn.getId()){
            this.fragmentListener.changePage(2);
        }else if(view.getId() == this.exitBtn.getId()){
            Log.d("debug", "tombol keluar");

            AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
            builderAlert.setTitle("Keluar")
                    .setMessage("Apakah kamu ingin keluar?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("debug", "clicked Delete");
                            fragmentListener.closeApplication();
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builderAlert.create();
            builderAlert.show();
        }else if(view == this.testBtn){
            this.fragmentListener.changePage(3);
        }
        else if(view == this.shakeBtn){
            this.fragmentListener.changePage(4);
        }
    }

    public void claimPokemon(){
        //save context in a var for later
        Context ctx = this.getContext();

        //get random pokemon by evolution ID, lewat endpoint yg ini bisa hemat request (setau saya?)
        int pokeEvolID = rand.nextInt(148)+1; //variasi ID dari 1 sampe 148 (ditentuin sama kita)

        /*Fetching Data dengan Volley*/
        //Request bagian Luar: Tujuannya dapetin ID Pokemon dan URL Speciesnya
        RequestQueue queue = Volley.newRequestQueue(this.getContext()); //Buat Queue Object 1
        String url ="https://pokeapi.co/api/v2/evolution-chain/"+pokeEvolID+"/";    //Gabung ID ke URL yang mau di Fetch

        //Volley Request 1
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //Ambil URL dari hasil Response
                        JSONObject res = new JSONObject(response);
                        JSONObject poke = (JSONObject)((JSONObject)res.get("chain")).get("species");
                        URI uri = new URI(poke.getString("url"));   //Ambil URL

                        //Pecahin dari URL jadi dapet ID Pokemon
                        String[] segments = uri.getPath().split("/");
                        String idStr = segments[segments.length-1];
                        int pokeID = Integer.parseInt(idStr);   //Ambil ID

                        //Request bagian tengah: Tujuannya dapetin growth rate ID
                        RequestQueue queue2 = Volley.newRequestQueue(ctx);
                        String speciesUrl = poke.getString("url");

                        //Volley Request 2
                        StringRequest req2 = new StringRequest(Request.Method.GET, speciesUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response2) {
                                    try{
                                        JSONObject res2 = new JSONObject(response2);
                                        URI growth_url = new URI(((JSONObject)res2.get("growth_rate")).getString("url"));
                                        //Pecahin dari URL jadi dapet ID Growth Type
                                        String[] segmentGrowth = growth_url.getPath().split("/");
                                        String rateStr = segmentGrowth[segmentGrowth.length-1];
                                        int growth_rate = Integer.parseInt(rateStr);   //Ambil growth ID

                                        //Request bagian dalem: Tujuannya dapetin stats pokemon
                                        RequestQueue queue3 = Volley.newRequestQueue(ctx);
                                        String pokeDataUrl = "https://pokeapi.co/api/v2/pokemon/"+pokeID;
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
                                                        for(int i=0; i<stats.length() ;i++){
                                                            JSONObject temp = (JSONObject) stats.get(i);
                                                            statsArr[i] = temp.getInt("base_stat");
                                                        }
                                                        String pkmnType = "";
                                                        JSONArray types = res3.getJSONArray("types");
                                                        for(int i=0; i<types.length(); i++){
                                                            JSONObject type = (JSONObject) ((JSONObject) types.get(i)).get("type");
                                                            pkmnType+="#"+type.getString("name");
                                                        }

                                                        Pokemon newPoke = new Pokemon(pokeID, name, 0, 0, growth_rate, pkmnType);

                                                        fragmentListener.adoptPokemon(newPoke);
                                                        int money = fragmentListener.getMoney();
                                                        fragmentListener.updateMoney(money-1);
                                                        moneyTV.setText("Point: "+(money-1)+"");

                                                        AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
                                                        builderAlert.setTitle("Egg Claimed")
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override public void onClick(DialogInterface dialog, int which) {}
                                                            });
                                                        builderAlert.create();
                                                        builderAlert.show();
                                                    }
                                                    catch (JSONException e) {e.printStackTrace();}
                                                }
                                            }, new Response.ErrorListener(){@Override public void onErrorResponse(VolleyError error){}}
                                        );
                                        queue3.add(req3);
                                    }
                                    catch (JSONException | URISyntaxException e) {e.printStackTrace();}
                                }
                            }, new Response.ErrorListener() {@Override public void onErrorResponse(VolleyError error) {}}
                        );
                        queue2.add(req2);

                    }
                    catch (JSONException | URISyntaxException e) {e.printStackTrace();}
                }
            }, new Response.ErrorListener() {@Override public void onErrorResponse(VolleyError error) {}}
        );//end of string request 1
        queue.add(stringRequest);
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adoptBtn.setEnabled(false);
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            adoptBtn.setEnabled(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < 3; i++) {
                    Log.d(myLog, "Emulating some task.. Step " + i);
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
