package com.example.pokeshake;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.job.JobInfo;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.github.mikephil.charting.charts.RadarChart;

public class MainActivity extends AppCompatActivity implements FragmentListener{
    private int money;
    private FragmentManager fragmentManager;
    private PokeMenuFragment pokeMenuFragment;
    private ViewFragment viewFragment;
    private HomeFragment homeFragment;

    private TestFragment testFragment;
    private List<Pokemon> pokeList;

    private RadarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.pokeList = new LinkedList<Pokemon>();
        try {                       //load saved data
            initSavedProfileData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = this.fragmentManager.beginTransaction();
        this.homeFragment = new HomeFragment();
        this.pokeMenuFragment = new PokeMenuFragment();
        this.viewFragment = new ViewFragment();
        this.testFragment = new TestFragment();

        ft.add(R.id.fragment_container, this.homeFragment)
                .addToBackStack(null)
                .commit();
    }

    public void initSavedProfileData() throws JSONException {
        String saved = loadProfileData();
        if(saved.equals("empty")){
            JSONObject saveObj = new JSONObject();
            this.money = 5;//TEMPORARY VALUE: 5
            saveObj.put("money", 5);

            saveProfileData(saveObj.toString());
        }else{
            JSONObject object = new JSONObject(saved);
            this.money = object.getInt("money");
        }
        this.loadPokeStorage();
    }

    public String loadProfileData(){
        File file = new File(this.getFilesDir(),"saved.txt");

        try (FileInputStream fis = new FileInputStream(file)) {
            int content;
            String msg = "";
            while ((content = fis.read()) != -1) {
                msg=msg+(char)content;
            }
            return msg;

        } catch (IOException e) {
            e.printStackTrace();
            return "empty";
        }
    }

    public void saveProfileData(String content){
        File file = new File(this.getFilesDir(),"saved.txt");

        try (FileOutputStream fop = new FileOutputStream(file)) {
            if (!file.exists()) { file.createNewFile(); }

            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
//            Log.d("LOCATION: ",this.getFilesDir()+"");
        } catch (IOException e) {e.printStackTrace();}
    }

    public void savePokeData(String content){
        File file = new File(this.getFilesDir(),"pokemons.txt");

        try (FileOutputStream fop = new FileOutputStream(file)) {
            if (!file.exists()) { file.createNewFile(); }

            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
//            Log.d("LOCATION: ",this.getFilesDir()+"");
        } catch (IOException e) {e.printStackTrace();}
    }

    public String loadPokeData(){
        File file = new File(this.getFilesDir(),"pokemons.txt");

        try (FileInputStream fis = new FileInputStream(file)) {
            int content;
            String msg = "";
            while ((content = fis.read()) != -1) {
                msg=msg+(char)content;
            }
            return msg;

        } catch (IOException e) {
            e.printStackTrace();
            //ini buat testing ;)
            return "empty";
        }
    }

    @Override
    public void changePage(int page) {
        FragmentTransaction ft = this.fragmentManager.beginTransaction();
        if (page == 1) {
            if(this.homeFragment.isAdded()){
                ft.show(this.homeFragment);
            }else{
                ft.add(R.id.fragment_container, this.homeFragment)
                        .addToBackStack(null);
            }

            if(this.pokeMenuFragment.isAdded()){
                ft.hide(this.pokeMenuFragment);
            }
            if(this.viewFragment.isAdded()){
                ft.hide(this.viewFragment);
            }
        } else if (page == 2) {
            if(this.pokeMenuFragment.isAdded()){
                ft.show(this.pokeMenuFragment);
            }else{
                ft.add(R.id.fragment_container, this.pokeMenuFragment)
                        .addToBackStack(null);
            }

            if(this.homeFragment.isAdded()){
                ft.hide(this.homeFragment);
            }
            if(this.testFragment.isAdded()){
                ft.hide(this.testFragment);
            }
        } else if (page == 3) {
            if(this.viewFragment.isAdded()){
                ft.show(this.viewFragment);
            }else{
                ft.add(R.id.fragment_container, this.viewFragment)
                        .addToBackStack(null);
            }

            if(this.homeFragment.isAdded()){
                ft.hide(this.homeFragment);
            }
            if(this.testFragment.isAdded()){
                ft.hide(this.testFragment);
            }
        }
        ft.commit();
    }

    @Override
    public int getMoney() {return this.money;}

    @Override
    public void updateMoney(int money) {
        this.money = money;
    }

    @Override
    public List<Pokemon> getPokemons(){
        return this.pokeList;
    }

    public void loadPokeStorage() throws JSONException {
        String data = loadPokeData();
        List<Pokemon> pokemons = new LinkedList<Pokemon>();
        if(data.equals("empty")){
            JSONArray pkmnArr = new JSONArray();
            JSONObject pkmnData = new JSONObject();
            pkmnData.put("pokemons",pkmnArr);
            savePokeData(pkmnData.toString());
        }else{
            JSONObject pkmnData = new JSONObject(data);
            JSONArray pkmnArr = pkmnData.getJSONArray("pokemons");
            for(int i=0 ; i<pkmnArr.length() ; i++){
                JSONObject obj = pkmnArr.getJSONObject(i);
                Log.d("name is:",obj.getString("name"));
                Pokemon pkmn = new Pokemon(obj.getInt("id"),
                                           obj.getString("name"),
                                           obj.getInt("level"),
                                           obj.getInt("curExp"),
                                           obj.getInt("gRate"),
                                           obj.getString("types"));
                pokemons.add(pkmn);
            }
        }

        this.pokeList.addAll(pokemons);
    }

    @Override
    public void adoptPokemon(Pokemon pokemon) {
        this.pokeList.add(pokemon);
//        this.pokeMenuFragment.addPokemon(pokemon);
    }

    @Override
    public void closeApplication(){
        this.moveTaskToBack(true);
        this.finish();
    }


}