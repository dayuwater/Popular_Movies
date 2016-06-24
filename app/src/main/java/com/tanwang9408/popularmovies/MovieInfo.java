package com.tanwang9408.popularmovies;

/**
 * Created by tanwang on 6/24/16.
 */
public class MovieInfo {

    public String title="";


    public String imgUrl="";

    public String plot="";

    public double rating=0.0;

    public String date="2016-7-1";

    public enum index{


        TITLE(0), URL(1), PLOT(2), RATING(3), DATE(4);
        private int ind;

        private index(int index){
            ind=index;
        }
    }

    public String[] toStringArray(){
        String[] result=new String[5];
        result[0]=title;
        result[1]=imgUrl;
        result[2]=plot;
        result[3]=Double.toString(rating);
        result[4]=date;

        return result;
    }
}
