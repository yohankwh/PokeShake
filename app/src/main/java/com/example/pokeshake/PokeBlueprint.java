package com.example.pokeshake;

/*
    Helper Class to hold next pokemon evolution data
    Since not all previous poke data is different
 */
public class PokeBlueprint {
    protected int id;
    protected String name;
    protected String types;
    protected int[] statsArr;
    protected int nextLevelEvol;

    public PokeBlueprint(int id, String name, String types, int[] statsArr, int nextLevelEvol){
        this.id = id;
        this.name = name;
        this.types = types;
        this.statsArr = statsArr;
        this.nextLevelEvol = nextLevelEvol;
    }

    public int getNextLevelEvol() {return nextLevelEvol;}

    public int getId() {return id;}

    public String getName() {return name;}

    public String getTypes() {return types;}

    public int[] getStatsArr() {return statsArr;}

    public boolean isEmptyBlueprint(){
        return this.id == -1;
    }
}
