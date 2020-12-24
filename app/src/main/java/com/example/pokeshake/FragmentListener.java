package com.example.pokeshake;

import org.json.JSONException;

import java.util.List;

public interface FragmentListener {
    public void changePage(int page);
    public int getMoney();
    public List<Pokemon> loadPokemons() throws JSONException;
}
