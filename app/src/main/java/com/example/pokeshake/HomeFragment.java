package com.example.pokeshake;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.RotatingCircle;

import org.json.JSONException;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private ProgressBar progressBar;
    private TextView moneyTV;
    private Button adoptBtn;
    private Button pokeMenuBtn;
    private Button exitBtn;
    private Button shakeBtn;
    private Button testBtn;
    private HomePresenter presenter;

    private FragmentListener fragmentListener;
    private FrameLayout progressBarHolder;

    private long mLastClickTime = 0;
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    private String myLog = "myLog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.progressBar = (ProgressBar)view.findViewById(R.id.spin_kit_home);
        Sprite rotatingCircle = new RotatingCircle();
        progressBar.setIndeterminateDrawable(rotatingCircle);

        this.presenter = new HomePresenter(this.fragmentListener);

        this.moneyTV = view.findViewById(R.id.money_tv);
        setMoneyTV();
        this.moneyTV.setOnClickListener(this);

        this.adoptBtn = view.findViewById(R.id.btnAdoptEgg);
        this.adoptBtn.setOnClickListener(this);

        this.pokeMenuBtn = view.findViewById(R.id.btnMyPokemon);
        this.pokeMenuBtn.setOnClickListener(this);

        this.exitBtn = view.findViewById(R.id.btnExit);
        this.exitBtn.setOnClickListener(this);

        this.progressBarHolder = view.findViewById(R.id.progressBarHolder);

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
        if(view.getId() == this.adoptBtn.getId()){
            if(this.presenter.getMoney()>=1 && !this.presenter.getIsClaiming()){
                this.presenter.claimPokemon(this.getContext()); //claim the pokemon
                MyTask mt = new MyTask(); //show loading while claiming
                mt.execute();
            }
        }
        else if(view.getId() == this.pokeMenuBtn.getId()){
            this.presenter.changePage(2);
        }
        else if(view.getId() == this.exitBtn.getId()){
            AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
            builderAlert.setTitle("Keluar")
                    .setMessage("Apakah kamu ingin keluar?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("debug", "clicked Delete");
                            fragmentListener.closeApplication();
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builderAlert.create();
            builderAlert.show();
        }
        else if(view == this.testBtn){
            this.presenter.changePage(3);
        }
        else if(view == this.shakeBtn){
            this.presenter.changePage(4);
        }else if(view == this.moneyTV){
            try {
                this.fragmentListener.addMoney();
            } catch (JSONException e) { e.printStackTrace();}
        }
    }

    public void setMoneyTV(){
        this.moneyTV.setText("Point: "+this.presenter.getMoney());
    }

    /*Progress Bar on Adopt Egg*/
    //TrainFragment has the same class, might change to own class?
    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adoptBtn.setEnabled(false);
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            adoptBtn.setEnabled(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while(presenter.getIsClaiming()){
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
