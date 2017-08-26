package com.example.dsm2016.smartcart;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by dsm2016 on 2017-08-23.
 */

public class Home_Listview_adapter extends BaseAdapter{

    private ArrayList<Home_listview_item> home_listview_items = new ArrayList<>();

    @Override
    public int getCount() {
        return home_listview_items.size();
    }

    @Override
    public Object getItem(int position) {
        return home_listview_items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        final int pos = position;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom, parent, false);
        }

        ImageView iv_img = (ImageView)convertView.findViewById(R.id.iv_img);
        final TextView tv_name = (TextView)convertView.findViewById(R.id.tv_name);
        final TextView tv_contents = (TextView)convertView.findViewById(R.id.tv_contents);

        Home_listview_item myItem = (Home_listview_item) getItem(position);

        iv_img.setImageDrawable(myItem.getIcon());
        tv_name.setText(myItem.getName());
        tv_contents.setText(myItem.getContents());

        iv_img.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               Toast.makeText(context, pos + "번쨰 이미지 선택", Toast.LENGTH_SHORT).show();
            }
        });

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "선택 : " + tv_name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        tv_contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "선택 : " + tv_contents.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    public void addItem(Drawable image, String name, String contents){
        Home_listview_item mItem = new Home_listview_item();

        mItem.setIcon(image);
        mItem.setName(name);
        mItem.setContents(contents);

        home_listview_items.add(mItem);
    }


}
