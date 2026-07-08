package com.example.final_project.network.models;

public class AnalyticsResponse {

    private int rows;
    private int columns;
    private String[] column_names;
    private double mean;
    private double max;
    private double min;
    private String image;

    public int getRows(){ return rows; }
    public int getColumns(){ return columns; }
    public double getMean(){ return mean; }
    public double getMax(){ return max; }
    public double getMin(){ return min; }
    public String getImage(){ return image; }

}
