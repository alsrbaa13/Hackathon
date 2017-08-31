/* MainActivity.java - Last modified : 2017.08.27 */

package com.magmatart.staffapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.Serializable;

public class MainActivity extends Activity implements Serializable{

    public static final String TAG = "MainActivity";

    private final int drawerItemSelected = 1000;    // Overlay Activities' return code
    private static boolean isInitial = true;        // Check is initial executing
    private int floorNumber;                        // Current showing floor number

    static Cart cart;                   // List contain Carts

    Intent intent;

    TextView floorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floorText = findViewById(R.id.floor_text);

        if(isInitial){
            floorNumber = 1;
            isInitial = false;
            cart = new Cart();

            floorText.setText("Floor : " + floorNumber);

            intent = new Intent(MainActivity.this, OverlayActivity.class);
            intent.putExtra("floorNumber", floorNumber);
            intent.putExtra("cart", cart);
            startActivityForResult(intent, drawerItemSelected);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == drawerItemSelected){
            if(resultCode == RESULT_OK){
                floorNumber = data.getIntExtra("floorNumber", 1);
                cart = (Cart)data.getSerializableExtra("cart");
                floorText.setText("Floor : " + floorNumber);

                intent = null;
                intent = new Intent(MainActivity.this, OverlayActivity.class);
                intent.putExtra("floorNumber", floorNumber);
                intent.putExtra("cart", cart);
                startActivityForResult(intent, drawerItemSelected);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}