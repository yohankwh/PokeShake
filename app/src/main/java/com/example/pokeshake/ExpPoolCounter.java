package com.example.pokeshake;

public class ExpPoolCounter {
    public static int getExpPool(int level, int growthRate){
        int expPool = 0;
        switch(growthRate){
            case 1:
                expPool = (int) Math.pow(level,3)*5/4;
                break;
            case 2:
                expPool = (int) Math.pow(level, 3);
                break;
            case 3:
                expPool = (int) Math.pow(level,3)*4/5;
                break;
            case 4:
                expPool = (int) (6/5*Math.pow(level,3) - 15*Math.pow(level, 2) + 100*level - 140);
                break;
            case 5:
                if(level<=50){
                    expPool = (int) ( Math.pow(level, 3) * (100-level) /50 );
                }else if(level>50&&level<=68){
                    expPool = (int) ( Math.pow(level, 3) * (150-level) /100 );
                }else if(level>68&&level<=98){
                    expPool = (int) ( Math.pow(level, 3) * Math.floor((1911-10*level)/3) /500 );
                }else if(level>98&&level<=100) {
                    expPool = (int) ( Math.pow(level, 3) * (160-level) /100 );
                }else{
                    expPool = 0;
                }
                break;
            case 6:
                if(level<=15){
                    expPool = (int) ( Math.pow(level, 3) *  (Math.floor((level+1)/3)+24)/50 );
                }else if(level>15&&level<=36){
                    expPool = (int) ( Math.pow(level, 3) *  (level+14)/50);
                }else if(level>36&&level<=100   ){
                    expPool = (int) ( Math.pow(level, 3) * (Math.floor(level/2)+32)/50 );
                }else{
                    expPool = 0;
                }
                break;
            default:
                expPool = -1;
                break;
        }
        return expPool;
    }
}
