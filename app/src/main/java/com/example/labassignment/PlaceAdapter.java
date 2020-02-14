package com.example.labassignment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class PlaceAdapter extends ArrayAdapter {


    Context mContext;
    int layoutRes;
    List<favoritePlace> places;
    //SQLiteDatabase mDatabase;

    DatabaseHelper mDatabase;

    public PlaceAdapter( Context mContext, int layoutRes, List<favoritePlace> places, DatabaseHelper mDatabase) {
        super(mContext, layoutRes,places);
        this.mContext = mContext;
        this.layoutRes = layoutRes;
        this.places = places;
        this.mDatabase = mDatabase;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(layoutRes, null);
        TextView tvaddress = v.findViewById(R.id.address);
        TextView tvlat = v.findViewById(R.id.latitude);
        TextView tvlng = v.findViewById(R.id.longitude);
        TextView tvDate = v.findViewById(R.id.date);

        final favoritePlace fav_place = places.get(position);
        tvaddress.setText(fav_place.getAddress());
        tvlat.setText(fav_place.getFavLatitude());
        tvlng.setText(fav_place.getFavLongitude());
        tvDate.setText(fav_place.getDate());


//        v.findViewById(R.id.btn_edit_employee).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateEmployee(employee);
//            }
//        });
//
//        v.findViewById(R.id.btn_delete_employee).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteplace(fav_place);
//
//            }
//        });
        return v;
    }

    public void deleteplace(final favoritePlace place) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                String sql = "DELETE FROM employees WHERE id = ?";
                mDatabase.execSQL(sql,new Integer[]{employee.getId()});

                 */

                if(mDatabase.deletePlaces(place.getId()))
                    loadplaces();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



//    private void updatePlace(final favoritePlace place) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
////        LayoutInflater inflater = LayoutInflater.from(mContext);
////        View v = inflater.inflate(R.layout.dialog_layout_update_employee, null);
////        builder.setView(v);
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//
//
//        final EditText etname = v.findViewById(R.id.edittextname);
//        final EditText etslary = v.findViewById(R.id.edittextsalary);
//        final Spinner spinner = v.findViewById(R.id.spinnerdepartment);
//
//        String[] deptarray = mContext.getResources().getStringArray(R.array.departments);
//        int position = Arrays.asList(deptarray).indexOf(employee.getDept());
//
//        etname.setText(employee.getName());
//        etslary.setText(String.valueOf(employee.getSalary()));
//        spinner.setSelection(position);
//
//        v.findViewById(R.id.btn_update_employee).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = etname.getText().toString().trim();
//                String salary = etslary.getText().toString().trim();
//
//                String dept = spinner.getSelectedItem().toString();
//
//                if(name.isEmpty()){
//                    etname.setError("name field is empty");
//                    etname.requestFocus();
//                    return;
//                }
//
//                if(salary.isEmpty()){
//                    etslary.setError("name field is empty");
//                    etslary.requestFocus();
//                    return;
//                }
///*
//                String sql = " UPDATE employees SET name =?,salary =?,department=? WHERE id = ?";
//              mDatabase.execSQL(sql,new String[]{ name,salary,dept, String.valueOf(employee.getId())});
//                Toast.makeText(mContext, "employee update", Toast.LENGTH_SHORT).show();
//
// */
//                if(mDatabase.updateEmployee(place.getId(), address, dept, Double.parseDouble(salary))){
//                    Toast.makeText(mContext, "employee update", Toast.LENGTH_SHORT).show();
//                    loadplaces();
//                }
//                else
//                    Toast.makeText(mContext, "employee not update", Toast.LENGTH_SHORT).show();
//
//                // loadEmployees();
//                alertDialog.dismiss();
//
//            }
//        });
//
//
//    }


    private void loadplaces() {

/*
        String sql = "SELECT * FROM employees";
        Cursor cursor = mDatabase.rawQuery(sql, null);

 */
        Cursor cursor = mDatabase.getAllPlaces();

        if(cursor.moveToFirst()){
           places.clear();
            do{
                places.add(new favoritePlace(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3),
                        cursor.getString(4)
                ));
            }while (cursor.moveToNext());

            cursor.close();
        }
        notifyDataSetChanged();



    }




}
