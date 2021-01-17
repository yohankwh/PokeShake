package com.example.pokeshake;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class TrainFragment extends Fragment {
    private FragmentListener fragmentListener;
    private TrainPresenter presenter;
    protected Pokemon pokemon;
    protected PokeBlueprint blueprint;
    private TextView pokeName;
    private ImageView pokeImage;
    private FrameLayout loadingCircleHolder;
    int pokeIdx;

    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            this.pokeIdx = bundle.getInt("pokeIdx");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train, container, false);

        this.presenter = new TrainPresenter(this.fragmentListener);

        this.pokemon = this.fragmentListener.getSinglePokemonByIndex(this.pokeIdx);

        //Todo: Set TextViews & Images with Pokemon pkmn data

        this.loadingCircleHolder = view.findViewById(R.id.loadingCircleHolder);

        Log.d("poke name is ",this.pokemon.getName());
        this.pokeName = view.findViewById(R.id.train_pokename_tv);
        this.pokeName.setText(this.pokemon.getName());

        this.pokeImage = view.findViewById(R.id.train_poke_iv);
        Picasso.get().load(this.pokemon.getImageUrl()).into(this.pokeImage);

        //Setup Evolve Chain Data: Stored in this.blueprint
        fetchTrainData();
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

    public void fetchTrainData(){
        this.presenter.loadTrainData(this.getContext(), this.pokemon.getID(), this.pokemon.getEvolID());
        LoadingDisplay task = new LoadingDisplay();
        task.execute();
    }

    public void receiveBlueprint(PokeBlueprint blueprint){
        this.blueprint = blueprint;
        Log.d("blueprint",this.blueprint.getName());
    }

    private class LoadingDisplay extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            loadingCircleHolder.setAnimation(inAnimation);
            loadingCircleHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            loadingCircleHolder.setAnimation(outAnimation);
            loadingCircleHolder.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while(presenter.isFetching()){
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
