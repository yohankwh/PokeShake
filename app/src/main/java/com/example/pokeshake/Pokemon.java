package com.example.pokeshake;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Pokemon {
    private int id;
    private boolean isEgg;
    private String name;
    private int level;
    private int curExp;
    private int growthRate;
    private String imageUrl;

    public Pokemon(int id, String name, int level, int curExp){
        this.id = id;
        this.imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+id+".png";
        this.name = name;
        this.level = level;
        this.curExp = curExp;
        this.isEgg = level < 5;//if <5, is an egg
    }

    public void hatch(){
        this.isEgg = false;
        this.level = 5;
    }

    public String getName() {
        return this.level < 5 ? "Egg" : this.name;//if level<5, display name as egg
    }

    public int getID(){ return this.id; }

    public void setID(int id){ this.id = id; }

    public String getImageUrl() {return imageUrl;}

    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

    public void setName(String name) {this.name = name;}

    public int getLevel() {return level;}

    public void setLevel(int level) {this.level = level;}

    public int getCurExp() {return curExp;}

    public void setCurExp(int curExp) {this.curExp = curExp;}

    public void train(){
        this.curExp+=5;
    }
}
