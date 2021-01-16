package com.example.pokeshake;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;


public class ViewFragment extends Fragment implements View.OnClickListener {
    private FragmentListener fragmentListener;
    private RadarChart radarChart;
    protected Pokemon pokemon;
    int pokeIdx;

    String[] labels = {"HP", "Defense", "Sp.Attack", "Sp.Defense", "Attack"};

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
        View view = inflater.inflate(R.layout.fragment_view_pokemon, container, false);
        Pokemon pkmn = this.fragmentListener.getSinglePokemonByIndex(this.pokeIdx);

        //Todo: Set TextViews & Images with Pokemon pkmn data

        this.radarChart = view.findViewById(R.id.radarChart);

        RadarDataSet dataSet = new RadarDataSet(dataValue(), "Pokemon");

        dataSet.setColor(Color.RED);

        RadarData data = new RadarData();
        data.addDataSet(dataSet);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        radarChart.setData(data);
        radarChart.invalidate();

        return view;
    }

    private ArrayList<RadarEntry> dataValue(){
        ArrayList<RadarEntry> temp = new ArrayList<>();
        temp.add(new RadarEntry(7));
        temp.add(new RadarEntry(4));
        temp.add(new RadarEntry(8));
        temp.add(new RadarEntry(2));
        temp.add(new RadarEntry(6));
        return temp;
    }


    @Override
    public void onClick(View v) {

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
