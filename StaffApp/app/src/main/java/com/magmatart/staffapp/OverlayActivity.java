/* OverlayActivity.java - Last modified : 2017.08.27 */

package com.magmatart.staffapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Exchanger;

public class OverlayActivity extends Activity implements Serializable{

    public static final String TAG = "OverlayActivity";
    private final int drawerItemSelected = 1000;        // Overlay Activities' return code
    DisplayMetrics dm;                  // Metrics for get Display width and height
    int displayWidth, displayHeight;    // Display width and height

    TimerTask task;
    Timer timer;

    private String html = "";
    private Socket socket;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    private String IPAddress = "192.168.43.103";
    private int port = 9297;

    private String[] navItems = {
            "Floor 1",
            "Floor 2",
            "Floor 3",
            "Floor 4",
            "Floor 5",
            "Floor 6",
            "Floor 7",
            "Floor 8",
            "Floor 9",
            "Floor 10"
    };
    private ListView navList;                   // Navigation Drawer Item List View
    private FrameLayout flContainer;            // Layout Container

    static Cart cart;               // List contain Carts
    String msg;                                 // Receive message by server in this

    private int floorNumber;                    // Current showing floor number
    private Intent intent;

    String[] integers;

    RelativeLayout rl;

    /*
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);

        navList = findViewById(R.id.left_drawer);
        flContainer = findViewById(R.id.content_frame);

        navList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, navItems));
        navList.setOnItemClickListener(new DrawerItemClickListener());

        rl = findViewById(R.id.layout_relative);

        floorNumber = getIntent().getIntExtra("floorNumber", 1);
        cart = (Cart)getIntent().getSerializableExtra("cart");

        dm = getApplicationContext().getResources().getDisplayMetrics();
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;

        new Thread(){
            public void run() {
                try{
                    setSocket(IPAddress, port);
                }catch(IOException e){
                    e.printStackTrace();
                }

            }
        }.start();

        checkUpdate.start();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    // Navigation Drawer Item Click Listener
    private class DrawerItemClickListener implements ListView.OnItemClickListener, Serializable{

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id){
            Intent intent = new Intent();
            intent.putExtra("floorNumber", position+1);     //Drawer item position + 1 = floor number
            intent.putExtra("cart", cart);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private Thread checkUpdate = new Thread(){
        public void run(){
            super.run();

            try {
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                String line;
                Log.d(TAG, "Start Chatting Thread");
                while(true){
                    Log.d("TAG", "Good");
                    if(socket.isConnected()) {
                        if (networkReader.ready()) {
                            line = networkReader.readLine();
                            html = line;
                            Log.d(TAG, "Ready to Toast");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    integers = html.split(",");
                                    if(html.equals("0")){

                                    } else {
                                        cart = null;
                                        cart = new Cart(Integer.parseInt(integers[4]), (int) (((double) displayWidth / (double) Integer.parseInt(integers[0])) * (double) (Integer.parseInt(integers[2]))), (int) (((double) displayHeight / (double) (Integer.parseInt(integers[1]))) * (double) (Integer.parseInt(integers[3]))));

                                        ImageView temp = new ImageView(getApplicationContext());
                                        temp.setImageResource(R.drawable.cart);
                                        rl.removeAllViews();
                                        cart.draw(rl, temp);
                                    }
                                    /*
                                    try {
                                        networkReader.reset();
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    */
                                }
                            });
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };

    public void setSocket(String ip, int port) throws IOException{
        try{
            Log.d(TAG, "setSocket Called");
            socket = new Socket(ip, port);
            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            e.printStackTrace();
        }

        try{
            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}