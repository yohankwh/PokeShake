package com.example.pokeshake;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static android.content.Context.SENSOR_SERVICE;

public class TrainFragment extends Fragment implements SensorEventListener, View.OnClickListener {
    private ProgressBar progressBar;
    private FragmentListener fragmentListener;
    private TrainPresenter presenter;
    protected Pokemon pokemon;
    protected PokeBlueprint blueprint;
    private TextView endTraining;
    private TextView pokeName;
    private ImageView pokeImage;
    private TextView expPool;
    private TextView curExp;
    private TextView pokeLvl;
    private TextView hpStat;
    private TextView atkStat;
    private TextView defStat;
    private TextView spAtkStat;
    private TextView spDefStat;
    private TextView speedStat;

    private FrameLayout loadingCircleHolder;
    private int pokeIdx;

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

        this.progressBar = (ProgressBar)view.findViewById(R.id.spin_kit_train);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

        this.pokemon = this.fragmentListener.getSinglePokemonByIndex(this.pokeIdx);
        this.presenter = new TrainPresenter(this.fragmentListener,
                                            this.pokemon.getLevel(),
                                            this.pokemon.getGrowthRate());

        //Todo: Set TextViews & Images with Pokemon pkmn data
        this.loadingCircleHolder = view.findViewById(R.id.loadingCircleHolder);
        this.endTraining = view.findViewById(R.id.endTraining);
        this.endTraining.setOnClickListener(this);
        this.pokeName = view.findViewById(R.id.train_pokename_tv);
        this.pokeImage = view.findViewById(R.id.train_poke_iv);
        this.curExp = view.findViewById(R.id.curexp_train_tv);
        this.pokeLvl = view.findViewById(R.id.train_pokelvl_tv);
        this.expPool = view.findViewById(R.id.exppool_train_tv);
        //:( banyak beud
        this.hpStat = view.findViewById(R.id.hp_train_tv);
        this.atkStat = view.findViewById(R.id.atk_train_tv);
        this.defStat = view.findViewById(R.id.def_train_tv);
        this.spAtkStat = view.findViewById(R.id.spatk_train_tv);
        this.spDefStat = view.findViewById(R.id.spdef_train_tv);
        this.speedStat = view.findViewById(R.id.speed_train_tv);

        attachPokeData();

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
                    if(!this.presenter.isFetching()){
                        mLastShakeTime = curTime;
                        boolean wasEgg = this.pokemon.isEgg();
                        this.presenter.addExp(this.pokemon);

                        if(!this.pokemon.isEgg()){
                            this.curExp.setText(this.pokemon.getCurExp()+"");
                            String expText = "Exp Points: "+this.presenter.getExpPool();
                            this.expPool.setText(expText);
                        }

                        if(this.presenter.isLeveledUp()){
                            if(!this.pokemon.isEgg()){
                                try {
                                    this.fragmentListener.addMoney();
                                } catch (JSONException e) {e.printStackTrace();}
                            }

                            this.pokeLvl.setText("Level "+this.pokemon.getLevel());

                            this.presenter.resetLeveledUp();

                            //if hatch
                            if(this.pokemon.isEgg()!=wasEgg){
                                attachPokeData();
                            }
                            //if evolve (1st Condition: if actually has evolution | 2nd: if current level is level needed for evol)
                            if(this.blueprint.nextLevelEvol != -1 && this.pokemon.getLevel() >= this.blueprint.nextLevelEvol){
                                this.pokemon.setID(this.pokemon.getID()+1);
                                evolvePokemon();
                                attachPokeData();
                                fetchTrainData();
                            }
                        }
                    }
                }
            }
        }
    }

    public void attachPokeData(){
        this.pokeName.setText(capitalize(this.pokemon.getName()));
        this.pokeLvl.setText("Level "+this.pokemon.getLevel());
        //set types UI as well

        if(this.pokemon.isEgg()){
            this.curExp.setText("");
            this.expPool.setText("");
            InputStream ims = null;
            try {
                ims = getActivity().getAssets().open("egg.png");
            } catch (IOException e) { e.printStackTrace();}
            Drawable d = Drawable.createFromStream(ims, null);
            this.pokeImage.setImageDrawable(d);
            this.hpStat.setText("?");
            this.atkStat.setText("?");
            this.defStat.setText("?");
            this.spAtkStat.setText("?");
            this.spDefStat.setText("?");
            this.speedStat.setText("?");
        }else{
            int[] statsArr = this.pokemon.getStats();
            this.curExp.setText(this.pokemon.getCurExp()+"");
            String expText = "Exp Points: "+this.presenter.getExpPool();
            this.expPool.setText(expText);
            Picasso.get().load(this.pokemon.getImageUrl()).into(this.pokeImage);
            this.hpStat.setText(String.valueOf(statsArr[0]));
            this.atkStat.setText(String.valueOf(statsArr[1]));
            this.defStat.setText(String.valueOf(statsArr[2]));
            this.spAtkStat.setText(String.valueOf(statsArr[3]));
            this.spDefStat.setText(String.valueOf(statsArr[4]));
            this.speedStat.setText(String.valueOf(statsArr[5]));
        }
    }

    public void evolvePokemon(){
        String newName = capitalize(this.blueprint.getName());
        this.pokemon.setName(newName);
        this.pokeName.setText(this.pokemon.getName());
        this.pokemon.setTypes(this.blueprint.getTypes());
        this.pokemon.setStats(this.blueprint.getStatsArr());
    }

    public String capitalize(String in){//Capitalize first letter
        if(!in.equals("")){in = in.substring(0,1).toUpperCase() + in.substring(1);}
        return in;
    }

    public int getCurrentPokeIdx(){
        return this.pokeIdx;
    }

    public Pokemon getPokemon(){
        return this.pokemon;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onClick(View view) {
        if (view == this.endTraining) {
            this.fragmentListener.saveTrainingData(this.pokemon);

            this.fragmentListener.changePage(3, this.pokeIdx);
            getActivity().onBackPressed();
        }
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
