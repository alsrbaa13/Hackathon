/* Cart.java - Last modified : 2017.08.31 */

package com.magmatart.staffapp;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.Serializable;

public class Cart implements Serializable{
    private int floor;      // Floor number of this cart position
    private int coordX;     // X coord of this cart
    private int coordY;     // Y coord of this cart

    public Cart(){
        floor = coordX = coordY = 0;
    }

    public Cart(int f, int x, int y){
        floor = f;
        coordX = x;
        coordY = y;
    }

    // Draw cart on map
    public void draw(RelativeLayout rl, ImageView iv){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(24, 23);
        params.leftMargin = coordX;
        params.topMargin = coordY;

        rl.addView(iv, params);
    }

    public int getFloor(){
        return floor;
    }

    public int getCoordX(){
        return coordX;
    }

    public int getCoordY(){
        return coordY;
    }

    public void setFloor(int f){
        floor = f;
    }

    public void setCoordX(int x){
        coordX = x;
    }

    public void setCoordY(int y){
        coordY = y;
    }
}
