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
    private List<Pokemon> pokeList;

    public PokeMenuFragment(){
        this.adapter = new PokeAdapter((Activity) this.fragmentListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_pokemon, container, false);

        this.pokemons = view.findViewById(R.id.lst_pokemons);
        this.adapter = new PokeAdapter((Activity)this.fragmentListener);
        this.pokeList = new LinkedList<Pokemon>();

        Log.d("LOAD:","ON PROGRESS");
        this.pokeList.addAll(this.fragmentListener.getPokemons());
        Log.d("LOADED: ",this.pokeList.get(0).getImageUrl());

        this.pokemons.setAdapter(this.adapter);
        this.adapter.update(this.pokeList);

        return view;
    }

    public void updateList(List<Pokemon> pokemons){
        this.adapter.update(pokemons);
    }

    public void addPokemon(Pokemon pokemon){
        this.pokeList.add(pokemon);
        this.updateList(this.pokeList);
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
