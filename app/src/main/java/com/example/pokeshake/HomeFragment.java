package com.example.pokeshake;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private Button adoptBtn;
    private Button pokeMenuBtn;
    private Button exitBtn;
    private FragmentListener fragmentListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.adoptBtn = view.findViewById(R.id.btnAdoptEgg);
        this.pokeMenuBtn = view.findViewById(R.id.btnMyPokemon);
        this.exitBtn = view.findViewById(R.id.btnExit);

        this.pokeMenuBtn.setOnClickListener(this);

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
        if(view == this.pokeMenuBtn){
            this.fragmentListener.changePage(2);
        }
    }
}
