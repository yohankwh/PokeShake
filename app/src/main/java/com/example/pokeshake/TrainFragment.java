package com.example.pokeshake;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

public class TrainFragment extends Fragment {
    private FragmentListener fragmentListener;
    protected Pokemon pokemon;
    private TextView pokeName;
    private ImageView pokeImage;
    int pokeIdx;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            this.pokeIdx = bundle.getInt("pokeID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train, container, false);

        this.pokemon = this.fragmentListener.getSinglePokemonByIndex(this.pokeIdx);

        //Todo: Set TextViews & Images with Pokemon pkmn data
        /*Todo: Setup Evolve Chain Data,
                - next pokemon name
                - next pokemon type
                - next pokemon stats
         */
        Log.d("poke name is ",this.pokemon.getName());
        this.pokeName = view.findViewById(R.id.train_pokename_tv);
        this.pokeName.setText(this.pokemon.getName());

        this.pokeImage = view.findViewById(R.id.train_poke_iv);
        Picasso.get().load(this.pokemon.getImageUrl()).into(this.pokeImage);

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            this.fragmentListener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + "must implement FragmentListener");
        }
    }
}
