package com.example.pokeshake;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class ViewFragment extends Fragment implements View.OnClickListener {
    private FragmentListener fragmentListener;
    private RadarChart radarChart;
    protected Pokemon pokemon;
    private TextView name;
    private TextView level;
    private TextView exp;
    private ImageView pokeImg;
    int pokeIdx;

    private Button trainBtn;
    private Button releaseBtn;

    String[] labels = {"HP", "Attack", "Defense", "Sp.Atk", "Sp.Def", "Speed"};

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
        View view = inflater.inflate(R.layout.fragment_view_pokemon, container, false);
        this.trainBtn = view.findViewById(R.id.btn_train);
        this.trainBtn.setOnClickListener(this);
        this.releaseBtn = view.findViewById(R.id.btn_release);
        this.releaseBtn.setOnClickListener(this);
        this.name = view.findViewById(R.id.tv_poke_name);
        this.level = view.findViewById(R.id.tv_poke_lvl);
        this.exp = view.findViewById(R.id.tv_poke_exp);
        this.pokeImg = view.findViewById(R.id.iv_poke_img);

        this.pokemon = this.fragmentListener.getSinglePokemonByIndex(this.pokeIdx);

        this.name.setText(this.pokemon.getName());
        this.level.setText("Level "+this.pokemon.getLevel());
        String expText = this.pokemon.getCurExp()+" / "+ExpPoolCounter.getExpPool(this.pokemon.getLevel(), this.pokemon.getGrowthRate());
        this.exp.setText(expText);

        if(this.pokemon.isEgg()){
            InputStream ims = null;
            try {
                ims = getActivity().getAssets().open("egg.png");
            } catch (IOException e) { e.printStackTrace();}
            Drawable d = Drawable.createFromStream(ims, null);
            this.pokeImg.setImageDrawable(d);
        }else{
            Picasso.get().load(this.pokemon.getImageUrl()).into(this.pokeImg);
        }

//        int[] statsArr = this.pokemon.getStats();
//
//        Log.d("Stats",statsArr[0]+"");

        //Todo: Set TextViews & Images with Pokemon pkmn data

        this.radarChart = view.findViewById(R.id.radarChart);
        this.radarChart.getDescription().setEnabled(false);
        this.radarChart.getLegend().setEnabled(false);
        this.radarChart.setWebColorInner(Color.WHITE);

        RadarDataSet dataSet = new RadarDataSet(dataValue(), "Pokemon");
        dataSet.setValueTextColor(Color.WHITE);

        dataSet.setColor(Color.RED);

        RadarData data = new RadarData();
        data.addDataSet(dataSet);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextColor(Color.WHITE);
        YAxis yAxis=radarChart.getYAxis();
        yAxis.setTextColor(Color.GRAY);

        radarChart.setData(data);
        radarChart.invalidate();

        return view;
    }

    private ArrayList<RadarEntry> dataValue(){
        ArrayList<RadarEntry> temp = new ArrayList<>();
        for(int stat : this.pokemon.getStats()){
            temp.add(new RadarEntry(stat));
        }
        return temp;
    }

    @Override
    public void onClick(View v) {
        if(v == this.trainBtn){
            this.fragmentListener.changePage(5, this.pokeIdx);
        }
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
