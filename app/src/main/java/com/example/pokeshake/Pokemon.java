package com.example.pokeshake;

public class Pokemon {
    private int evolID;
    private int id;
    private String name;
    private int level;
    private int curExp;
    private int growthRate;
    private String types;
    private int[] stats;//In Order: HP, Atk, Def, Sp.Atk, Sp.Def, Speed

    public Pokemon(int id, String name, int level, int curExp, int growthRate, String types, int evolID, int[] stats){
        this.id = id;
        this.name = name;
        this.level = level;
        this.curExp = curExp;
        this.growthRate = growthRate;
        this.types = types;
        this.evolID = evolID;
        this.stats = stats;
    }

    public void hatch(){this.setLevel(5);}

    public String getName() {
        return isEgg() ? "Egg" : this.name;//if level<5, display name as egg
    }

    public String getActualName(){
        return this.name;
    }

    public void setTypes(String types) { this.types = types; }

    public String getTypes(){return this.types;}

    public void setStats(int[] stats){ this.stats = stats; }

    public String getImageUrl(){
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+getID()+".png";
    }

    public boolean isEgg(){
        return this.level < 5;//if level<5, form is egg
    }

    public int[] getStats(){return this.stats;}

    public int getID(){ return this.id; }

    public int getEvolID(){return this.evolID;}

    public int getGrowthRate() {return growthRate;}

    public void setID(int id){ this.id = id; }

    public void setName(String name) {this.name = name;}

    public int getLevel() {return level;}

    public void setLevel(int level) {this.level = level;}

    public int getCurExp() {return curExp;}

    public void setCurExp(int curExp) {this.curExp = curExp;}

    public void train(){
        if(isEgg()){
            this.curExp+=10;
        }else{
            this.curExp+=30;
        }
    }

}
