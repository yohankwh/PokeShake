package com.example.pokeshake;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private TextView moneyTV;
    private Button adoptBtn;
    private Button pokeMenuBtn;
    private Button exitBtn;
    private FragmentListener fragmentListener;

    private long mLastClickTime = 0;
    private Button testBtn;
    private Random rand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.moneyTV = view.findViewById(R.id.money_tv);
        this.moneyTV.setText("Point: "+this.fragmentListener.getMoney());

        this.adoptBtn = view.findViewById(R.id.btnAdoptEgg);
        this.pokeMenuBtn = view.findViewById(R.id.btnMyPokemon);
        this.exitBtn = view.findViewById(R.id.btnExit);

        this.rand = new Random();

        this.adoptBtn.setOnClickListener(this);
        this.pokeMenuBtn.setOnClickListener(this);
        this.exitBtn.setOnClickListener(this);

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
    }

    public void claimPokemon(){
        int pokeEvolID = rand.nextInt(148)+1; //evol chain data is 1 to 148
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url ="https://pokeapi.co/api/v2/evolution-chain/"+pokeEvolID+"/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONObject poke = (JSONObject)((JSONObject)res.get("chain")).get("species");
                            URI uri = new URI(poke.getString("url"));
                            String[] segments = uri.getPath().split("/");
                            String idStr = segments[segments.length-1];
                            int pokeID = Integer.parseInt(idStr);
                            String pokeName = poke.getString("name");

                            Pokemon newPoke = new Pokemon(pokeID, pokeName, 1, 0);
                            fragmentListener.adoptPokemon(newPoke);
                            int money = fragmentListener.getMoney();
                            fragmentListener.updateMoney(money-1);
                            moneyTV.setText("Point: "+(money-1)+"");

                            AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
                            builderAlert.setTitle("Egg Claimed")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builderAlert.create();
                            builderAlert.show();

                        } catch (JSONException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);
    }
}
