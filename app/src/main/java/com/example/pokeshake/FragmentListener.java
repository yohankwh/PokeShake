package com.example.pokeshake;

import org.json.JSONException;

public interface FragmentListener {
    public void changePage(int page, int pokeID);
    public int getMoney();
    public void updateMoney(int money) throws JSONException;
    public void updateMoneyView();
    public void adoptPokemon(Pokemon pokemon) throws JSONException;
    public Pokemon getSinglePokemonByIndex(int index);
    public void sendBlueprint(PokeBlueprint blueprint);
    public void savePokeChanges() throws JSONException;
    public void addMoney() throws JSONException;
    public void releasePokemon(int index) throws JSONException;
    void closeApplication();
}
