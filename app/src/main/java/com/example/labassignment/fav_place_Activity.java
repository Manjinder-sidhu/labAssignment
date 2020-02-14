package com.example.labassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class fav_place_Activity extends AppCompatActivity {

    DatabaseHelper mDataBase;
    List<favoritePlace> placesList;
//    ListView listView;
    SwipeMenuListView listView;
    PlaceAdapter placeAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_place_);

//        listView = findViewById(R.id.LVPlaces);
        listView = findViewById(R.id.listview);
        placesList = new ArrayList<>();

        // mDataBase = openOrCreateDatabase(MainActivity.DATABASE_NAME,MODE_PRIVATE,null);
        mDataBase = new DatabaseHelper(this);
        loadPlaces();


        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                editItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                editItem.setWidth((250));

                // set item title fontsize
                editItem.setTitleSize(18);
                // set item title font color
                editItem.setTitleColor(Color.WHITE);
                // add to menu

                editItem.setIcon(R.drawable.edit);
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth((250));
                // set a icon
                deleteItem.setIcon(R.drawable.delete);
                // add to menu
                menu.addMenuItem(deleteItem);

            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // edit
                        favoritePlace place = placesList.get(position);
                        double latitude = Double.parseDouble(place.getFavLatitude());
                        double longitude = Double.parseDouble(place.getFavLongitude());

                        Intent intent = new Intent(fav_place_Activity.this,MainActivity.class);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);
                        LatLng latLng = new LatLng(latitude,longitude);
                        setResult(MainActivity.RESULT_OK, intent);
                        finish();

                        break;

                    case 1:
                        // delete
                        Toast.makeText(fav_place_Activity.this, "delete clicked", Toast.LENGTH_SHORT).show();
                        favoritePlace place1 = placesList.get(position);
                        int id = place1.getId();
                        if(mDataBase.deletePlaces(id))
                            placesList.remove(position);
                            placeAdapter.notifyDataSetChanged();

                        break;
                }

                // false : close the menu; true : not close the menu
                return true;
            }
        });

listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                favoritePlace place = placesList.get(position);
                double latitude = Double.parseDouble(place.getFavLatitude());
                double longitude = Double.parseDouble(place.getFavLongitude());

                Intent intent = new Intent(fav_place_Activity.this,MainActivity.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                LatLng latLng = new LatLng(latitude,longitude);
                setResult(MainActivity.RESULT_OK, intent);
                finish();

                Toast.makeText(fav_place_Activity.this, "cell clicked", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadPlaces() {
        /*
        String sql = "SELECT * FROM employees";


        Cursor cursor = mDataBase.rawQuery(sql, null);

         */
        Cursor cursor = mDataBase.getAllPlaces();
        if(cursor.moveToFirst()){
            do {
               placesList.add(new favoritePlace(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3),
                        cursor.getString(4)
                ));


            }while (cursor.moveToNext());
            cursor.close();
            //show item in a listView
            //we use a custom adapter to show employees

         placeAdapter = new PlaceAdapter(this, R.layout.list_layout_place, placesList, mDataBase);
//            placeAdapter.notifyDataSetChanged();
            listView.setAdapter(placeAdapter);

        }
    }

//    private void deleteplace(final int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(fav_place_Activity.this);
//        builder.setTitle("Are you sure?");
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                /*
//                String sql = "DELETE FROM employees WHERE id = ?";
//                mDatabase.execSQL(sql,new Integer[]{employee.getId()});
//
//                 */
//
//                final favoritePlace place = placesList.get(position);
//                int id = place.getId();
//            }
//        });
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//
//            }
//        });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

}
