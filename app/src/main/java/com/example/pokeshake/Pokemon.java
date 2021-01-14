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
    private String name;
    private int level;
    private int curExp;
    private int growthRate;
    private String types;

    public Pokemon(int id, String name, int level, int curExp, int growthRate, String types){
        this.id = id;
        this.name = name;
        this.level = level;
        this.curExp = curExp;
        this.growthRate = growthRate;
        this.types = types;
    }

    public void hatch(){this.setLevel(5);}

    public String getName() {
        return isEgg() ? "Egg" : this.name;//if level<5, display name as egg
    }

    public String getImageUrl(){
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+getID()+".png";
    }

    public boolean isEgg(){
        return this.level < 5;//if level<5, form is egg
    }

    public int getID(){ return this.id; }

    public void setID(int id){ this.id = id; }

    public void setName(String name) {this.name = name;}

    public int getLevel() {return level;}

    public void setLevel(int level) {this.level = level;}

    public int getCurExp() {return curExp;}

    public void setCurExp(int curExp) {this.curExp = curExp;}

    public void train(){
        this.curExp+=5;
    }
}
