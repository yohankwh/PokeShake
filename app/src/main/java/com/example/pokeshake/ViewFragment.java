package com.example.pokeshake;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class ViewFragment extends Fragment implements View.OnClickListener {
    private FragmentListener fragmentListener;
    private RadarChart radarChart;
    protected Pokemon pokemon;
    private View types_layout;
    private TextView name;
    private TextView level;
    private TextView exp;
    private TextView type1;
    private TextView type2;
    private ImageView pokeImg;
    int pokeIdx;
    private boolean loadedOnce;

    private Button trainBtn;
    private Button releaseBtn;

    String[] labels = {"HP", "Attack", "Defense", "Sp.Atk", "Sp.Def", "Speed"};

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//
//        Bundle bundle = this.getArguments();
//        if(bundle != null){
//            this.pokeIdx = bundle.getInt("pokeIdx");
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pokemon, container, false);
        this.types_layout = view.findViewById(R.id.types_layout);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            this.pokeIdx = bundle.getInt("pokeIdx");
        }

        loadedOnce = true;

        this.type1 = view.findViewById(R.id.tv_type_1);
        this.type2 = view.findViewById(R.id.tv_type_2);
        this.trainBtn = view.findViewById(R.id.btn_train);
        this.trainBtn.setOnClickListener(this);
        this.releaseBtn = view.findViewById(R.id.btn_release);
        this.releaseBtn.setOnClickListener(this);
        this.releaseBtn = view.findViewById(R.id.btn_release);
        this.releaseBtn.setOnClickListener(this);
        this.name = view.findViewById(R.id.tv_poke_name);
        this.level = view.findViewById(R.id.tv_poke_lvl);
        this.exp = view.findViewById(R.id.tv_poke_exp);
        this.pokeImg = view.findViewById(R.id.iv_poke_img);
        this.radarChart = view.findViewById(R.id.radarChart);

        this.pokemon = this.fragmentListener.getSinglePokemonByIndex(this.pokeIdx);
        attachPokeData(pokemon);


        return view;
    }

    private ArrayList<RadarEntry> dataValue(Pokemon pkmn){
        ArrayList<RadarEntry> temp = new ArrayList<>();
        for(int stat : pkmn.getStats()){
            temp.add(new RadarEntry(stat));
        }
        return temp;
    }

    public void attachPokeData(Pokemon pkmn){
        this.name.setText(pkmn.getName());
        this.level.setText("Level "+pkmn.getLevel());

        if(pkmn.isEgg()){
            this.exp.setText("");
            InputStream ims = null;
            try {
                ims = getActivity().getAssets().open("egg.png");
            } catch (IOException e) { e.printStackTrace();}
            Drawable d = Drawable.createFromStream(ims, null);
            this.pokeImg.setImageDrawable(d);
        }else{
            String expText = pkmn.getCurExp()+" / "+ExpPoolCounter.getExpPool(pkmn.getLevel(), pkmn.getGrowthRate());
            this.exp.setText(expText);
            Picasso.get().load(pkmn.getImageUrl()).into(this.pokeImg);
        }

        this.radarChart.getDescription().setEnabled(false);
        this.radarChart.getLegend().setEnabled(false);
        this.radarChart.setWebColorInner(Color.WHITE);

        RadarDataSet dataSet = new RadarDataSet(dataValue(pkmn), "Pokemon");
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

        if(!this.pokemon.isEgg()){
            String text = this.pokemon.getTypes();
            String[] typesArr = text.split("#", 3);

            setTVStyling(this.type1, typesArr[1]);
            if(typesArr.length>2){
                setTVStyling(this.type2, typesArr[2]);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == this.trainBtn){
            this.fragmentListener.changePage(5, this.pokeIdx);
        }else if(v== this.releaseBtn){
            try {
                this.fragmentListener.releasePokemon(this.pokeIdx);
            } catch (JSONException e) {e.printStackTrace();}
            this.fragmentListener.changePage(1, -1);
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

    private void setTVStyling(TextView tv, String type){
        ColorStateList colorStateList;
        switch(type) {
            case "bug":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_bug);
            break;
            case "dark":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_dark);
                break;
            case "dragon":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_dragon);
                break;
            case "electric":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_electric);
                break;
            case "fairy":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_fairy);
                break;
            case "fire":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_fire);
                break;
            case "fighting":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_fighting);
                break;
            case "flying":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_flying);
                break;
            case "ghost":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_ghost);
                break;
            case "grass":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_grass);
                break;
            case "ground":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_ground);
                break;
            case "ice":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_ice);
                break;
            case "normal":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_normal);
                break;
            case "poison":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_poison);
                break;
            case "psychic":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_psychic);
                break;
            case "rock":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_rock);
                break;
            case "steel":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_steel);
                break;
            case "water":
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.type_water);
                break;
            default:
                colorStateList = ContextCompat.getColorStateList(getContext(), R.color.black);
        }

        if(!type.equals("")){type = type.substring(0,1).toUpperCase() + type.substring(1);}
        tv.setText(type);
        tv.setVisibility(View.VISIBLE);
        tv.setBackgroundTintList(colorStateList);
    }
}
