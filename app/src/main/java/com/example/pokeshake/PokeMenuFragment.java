package com.example.pokeshake;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

public class PokeMenuFragment extends Fragment implements View.OnClickListener{
    private PokeAdapter adapter;
    private ListView pokemons;
    private FragmentListener fragmentListener;

    public PokeMenuFragment(PokeAdapter adapter){
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_pokemon, container, false);

        this.pokemons = view.findViewById(R.id.lst_pokemons);

        this.pokemons.setAdapter(this.adapter);

        return view;
    }

    public Pokemon getPokemonByIndex(int index){
        return this.adapter.getPokemonByIndex(index);
    }

    public List<Pokemon> getAllPokemon(){ return this.adapter.getPokeList(); }

    public void updateList(List<Pokemon> pokemons){
        this.adapter.update(pokemons);
    }

    public void addPokemon(Pokemon pokemon) throws JSONException {
        this.adapter.addNewPokeToList(pokemon);
    }

    public void updatePokeInList(int index, Pokemon pokemon){
        this.adapter.updateInList(index, pokemon);
    }

    public void releasePokemon(int index){
        this.adapter.releaseInList(index);
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

    }
}
