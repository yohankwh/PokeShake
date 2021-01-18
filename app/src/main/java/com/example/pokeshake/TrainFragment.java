package com.example.pokeshake;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import static android.content.Context.SENSOR_SERVICE;

public class TrainFragment extends Fragment implements SensorEventListener {
    private FragmentListener fragmentListener;
    private TrainPresenter presenter;
    protected Pokemon pokemon;
    protected PokeBlueprint blueprint;
    private TextView pokeName;
    private ImageView pokeImage;
    private TextView expPool;
    private TextView curExp;
    private TextView pokeLvl;
    private FrameLayout loadingCircleHolder;
    int pokeIdx;

    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;

    // variables for shake detection
    private static final float SHAKE_THRESHOLD = 0.6f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 250;
    private long mLastShakeTime;
    private SensorManager mSensorMgr;

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

        this.pokemon = this.fragmentListener.getSinglePokemonByIndex(this.pokeIdx);
        this.presenter = new TrainPresenter(this.fragmentListener,
                                            this.pokemon.getLevel(),
                                            this.pokemon.getGrowthRate());

        //Todo: Set TextViews & Images with Pokemon pkmn data
        this.loadingCircleHolder = view.findViewById(R.id.loadingCircleHolder);
        this.pokeName = view.findViewById(R.id.train_pokename_tv);
        this.pokeName.setText(this.pokemon.getName());
        this.pokeImage = view.findViewById(R.id.train_poke_iv);
        this.curExp = view.findViewById(R.id.curexp_train_tv);
        this.curExp.setText(this.pokemon.getCurExp()+"");
        this.pokeLvl = view.findViewById(R.id.train_pokelvl_tv);
        this.pokeLvl.setText("Level "+this.pokemon.getLevel());
        this.expPool = view.findViewById(R.id.exppool_train_tv);
        String expText = "Exp Points: "+ExpPoolCounter.getExpPool(this.pokemon.getLevel(), this.pokemon.getGrowthRate());
        this.expPool.setText(expText);

        Picasso.get().load(this.pokemon.getImageUrl()).into(this.pokeImage);

        //Setup Evolve Chain Data: Stored in this.blueprint
        fetchTrainData();

        // Get a sensor manager to listen for shakes
        mSensorMgr = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);

        // Listen for shakes
        Sensor accelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            mSensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
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
        Log.d("blueprint",this.blueprint.getId()+"");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //shake detection taken from:
        //https://stackoverflow.com/questions/5271448/how-to-detect-shake-event-with-android#answer-32803134
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                if (acceleration > SHAKE_THRESHOLD) { //if device is s h o o k
                    mLastShakeTime = curTime;
                    this.presenter.addExp(this.pokemon);
                    this.curExp.setText(this.pokemon.getCurExp()+"");
                    if(this.presenter.isLeveledUp()){
                        this.pokeLvl.setText("Level "+this.pokemon.getLevel());
                        this.expPool.setText("Exp Points: "+this.presenter.getExpPool());
                        this.presenter.resetLeveledUp();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

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
