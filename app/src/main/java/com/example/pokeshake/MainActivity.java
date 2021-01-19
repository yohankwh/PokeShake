package com.example.pokeshake;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
    private TrainFragment trainFragment;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* loading saved data on storage */
        List pokeList = new LinkedList<Pokemon>();
        try {
            initSavedProfileData();             // load Money
            pokeList.addAll(loadPokeStorage()); // load pokemon list
        }
        catch (JSONException e) {e.printStackTrace();}

        PokeAdapter adapter = new PokeAdapter(this, pokeList);

        this.fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = this.fragmentManager.beginTransaction();
        this.homeFragment = new HomeFragment();
        this.trainFragment = new TrainFragment();
        this.pokeMenuFragment = new PokeMenuFragment(adapter);
        this.viewFragment = new ViewFragment();

        ft.add(R.id.fragment_container, this.homeFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public Pokemon getSinglePokemonByIndex(int index){//not an mvp way
        return this.pokeMenuFragment.getPokemonByIndex(index);
    }

    public void updateMoneyChanges() throws JSONException {
        JSONObject money = new JSONObject();
        money.put("money",this.money);
        saveProfileData(money.toString());
    }

    public void initSavedProfileData() throws JSONException {
        String saved = loadProfileData();
        if(saved.equals("empty")){
            JSONObject saveObj = new JSONObject();
            this.money = 10;//TEMPORARY VALUE: 5
            saveObj.put("money", this.money);

            saveProfileData(saveObj.toString());
        }else{
            JSONObject object = new JSONObject(saved);
            this.money = object.getInt("money");
        }
    }

    /* Get Pokemon Data from Device as LISTS, just ignore what's inside */
    public List loadPokeStorage() throws JSONException {
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

                JSONArray stats = obj.getJSONArray("stats");
                int[] statsArr = new int[stats.length()];
                for(int s=0; s<stats.length(); s++){
                    statsArr[s] = stats.getInt(s);
                }

                Pokemon pkmn = new Pokemon(obj.getInt("id"),
                        obj.getString("name"),
                        obj.getInt("level"),
                        obj.getInt("curExp"),
                        obj.getInt("gRate"),
                        obj.getString("types"),
                        obj.getInt("evolID"),
                        statsArr);
                pokemons.add(pkmn);
            }
        }
        return pokemons;
    }

    public void storePokemonData() throws JSONException {
        JSONObject pokemons = new JSONObject();
        JSONArray pokeArray = new JSONArray();
        List<Pokemon> pokeList = this.pokeMenuFragment.getAllPokemon();
        for(int i=0 ; i<pokeList.size() ; i++){
            Pokemon poke = pokeList.get(i);
            JSONObject pokeObj = new JSONObject();
            pokeObj.put("id",poke.getID());
            pokeObj.put("name",poke.getActualName());
            pokeObj.put("level",poke.getLevel());
            pokeObj.put("curExp",poke.getCurExp());
            pokeObj.put("gRate",poke.getGrowthRate());
            pokeObj.put("types",poke.getTypes());
            pokeObj.put("evolID",poke.getEvolID());
            pokeObj.put("stats",new JSONArray(poke.getStats()));
            pokeArray.put(i, pokeObj);
        }
        savePokeData(pokemons.put("pokemons",pokeArray).toString());
    }

    /* Get Money Data from Device, just ignore what's inside */
    private String loadProfileData(){
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

    /* Get Pokemon Data from Device, just ignore what's inside */
    private String loadPokeData(){
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

    @Override
    public void sendBlueprint(PokeBlueprint blueprint){
        this.trainFragment.receiveBlueprint(blueprint);
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        //if in EditFragment, when press back run changePage to avoid error!
        if (f instanceof TrainFragment) {
            Pokemon pkmn = this.trainFragment.getPokemon();
            this.pokeMenuFragment.updatePokeInList(this.trainFragment.getCurrentPokeIdx(), pkmn);

            try {this.storePokemonData();}
            catch (JSONException e) {e.printStackTrace();}

            this.viewFragment.attachPokeData(pkmn);
            this.changePage(3, this.trainFragment.getCurrentPokeIdx());
            super.onBackPressed();
//            this.changePage(1, -1);
            Log.d("Through here","bro");
        }else if(f instanceof ViewFragment) {
            this.changePage(2, -1);
            super.onBackPressed();
        }else if(f instanceof PokeMenuFragment) {
            this.changePage(1, -1);
            super.onBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void changePage(int page, int pokeIdx) {
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
            if(this.trainFragment.isAdded()){
                ft.hide(this.trainFragment);
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
            if(this.trainFragment.isAdded()){
                ft.hide(this.trainFragment);
            }
            if(this.viewFragment.isAdded()){
                ft.hide(this.viewFragment);
            }
        } else if (page == 3) {
            if(pokeIdx!=-1){
                Bundle bundle = new Bundle();
                bundle.putInt("pokeIdx",pokeIdx);
                this.viewFragment.setArguments(bundle);
            }
            if(this.viewFragment.isAdded()){
                ft.show(this.viewFragment);
            }else{
                ft.add(R.id.fragment_container, this.viewFragment)
                        .addToBackStack(null);
            }

            if(this.homeFragment.isAdded()){
                ft.hide(this.homeFragment);
            }
            if(this.pokeMenuFragment.isAdded()){
                ft.hide(this.pokeMenuFragment);
            }
            if(this.trainFragment.isAdded()){
                ft.hide(this.trainFragment);
            }
        }else if (page == 5) {
            if(pokeIdx!=-1){
                Bundle bundle = new Bundle();
                bundle.putInt("pokeIdx",pokeIdx);
                this.trainFragment.setArguments(bundle);
            }
            if(this.trainFragment.isAdded()){
                ft.show(this.trainFragment);
            }else{
                ft.add(R.id.fragment_container, this.trainFragment)
                        .addToBackStack(null);
            }

            if(this.pokeMenuFragment.isAdded()){
                ft.hide(this.pokeMenuFragment);
            }
            if(this.homeFragment.isAdded()){
                ft.hide(this.homeFragment);
            }
            if(this.viewFragment.isAdded()){
                ft.hide(this.viewFragment);
            }
        }
        ft.commit();
    }

    @Override
    public int getMoney() {return this.money;}

    @Override
    public void updateMoney(int money) throws JSONException {
        this.money = money;
        this.updateMoneyChanges();
    }

    @Override
    public void updateMoneyView() {
        this.homeFragment.setMoneyTV();
    }

    @Override
    public void adoptPokemon(Pokemon pokemon) throws JSONException {
        this.pokeMenuFragment.addPokemon(pokemon);
    }

    @Override
    public void savePokeChanges() throws JSONException {
        this.storePokemonData();
    }

    @Override
    public void closeApplication(){
        this.moveTaskToBack(true);
        this.finish();
    }

    @Override
    public void releasePokemon(int index) throws JSONException {
        this.pokeMenuFragment.releasePokemon(index);
        savePokeChanges();
    }

    @Override
    public void addMoney() throws JSONException {
        this.money+=10;
        this.homeFragment.setMoneyTV();
        this.updateMoneyChanges();
    }
}