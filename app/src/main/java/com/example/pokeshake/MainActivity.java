package com.example.pokeshake;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentListener{
    private FragmentManager fragmentManager;
    private PokeMenuFragment pokeMenuFragment;
    private ViewFragment viewFragment;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = this.fragmentManager.beginTransaction();

        this.homeFragment = new HomeFragment();
        this.pokeMenuFragment = new PokeMenuFragment();
        this.viewFragment = new ViewFragment();

        ft.add(R.id.fragment_container, this.homeFragment)
                .addToBackStack(null)
                .commit();
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
            if(this.viewFragment.isAdded()){
                ft.hide(this.viewFragment);
            }
        }
        ft.commit();
    }
}