package com.example.pokeshake;

import org.json.JSONException;

import java.util.List;

public interface FragmentListener {
    public void changePage(int page);
    public int getMoney();
    public void updateMoney(int money);
    public void updateMoneyView();
    public void adoptPokemon(Pokemon pokemon);
    void closeApplication();
}
