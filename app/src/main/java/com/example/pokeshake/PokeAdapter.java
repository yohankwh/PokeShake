package com.example.pokeshake;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PokeAdapter extends BaseAdapter {
    private Activity activity;
    private List<Pokemon> pokeList;

    public PokeAdapter(Activity activity, List<Pokemon> pokemons){
        this.activity = activity;
        this.pokeList = new ArrayList<Pokemon>();
        this.pokeList.addAll(pokemons);
    }

    /* Ubah seluruh isi list pokemon */
    public void update(List<Pokemon> pokemons){
        this.pokeList.clear();
        this.pokeList.addAll(pokemons);
        this.notifyDataSetChanged();
    }

    /* Tambah pokemon baru ke list */
    public void addNewPokeToList(Pokemon pokemon){
        this.pokeList.add(pokemon);
        this.notifyDataSetChanged();
    }

    /* Ubah Data Pokemon berdasarkan index di List */
    public void updateInList(int index, Pokemon pokemon){
        this.pokeList.set(index, pokemon);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {return this.pokeList.size();}

    @Override
    public Object getItem(int i) {return this.pokeList.get(i);}

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.activity).inflate(R.layout.item_list_pokemon, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            viewHolder.updateView((Pokemon)this.getItem(i),i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public long getItemId(int i) {return 0;}

    private class ViewHolder implements View.OnClickListener{
        private Pokemon poke;
        private int position;
        private FragmentListener fragmentListener;

        private ImageView miniPokeIV;
        private TextView pokeNameTV;

        public ViewHolder(View view){
            this.miniPokeIV = view.findViewById(R.id.mini_poke_iv);
            this.pokeNameTV = view.findViewById(R.id.poke_name_tv);
        }

        @Override
        public void onClick(View view) {

        }

        public void updateView(Pokemon poke, int position) throws IOException {
            this.position = position;
            this.poke = poke;
            this.pokeNameTV.setText(poke.getName());
            if(poke.isEgg()){
                // get input stream
                InputStream ims = activity.getAssets().open("egg.png");
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                this.miniPokeIV.setImageDrawable(d);
            }else{
                Picasso.get().load(poke.getImageUrl()).into(this.miniPokeIV);
            }
        }
    }
}
