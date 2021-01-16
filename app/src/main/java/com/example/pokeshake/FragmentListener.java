package com.example.pokeshake;

public interface FragmentListener {
    public void changePage(int page, int pokeID);
    public int getMoney();
    public void updateMoney(int money);
    public void updateMoneyView();
    public void adoptPokemon(Pokemon pokemon);
    public Pokemon getSinglePokemonByIndex(int index);
    void closeApplication();
}
