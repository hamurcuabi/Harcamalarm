package com.emrehmrc.harcamalarm.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrehmrc.harcamalarm.R;
import com.emrehmrc.harcamalarm.models.SpinnerItemModel;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<SpinnerItemModel> {

    Activity context;
    ArrayList<SpinnerItemModel> list;
    LayoutInflater inflater;

    public SpinnerAdapter(Activity context, int id, ArrayList<SpinnerItemModel>
            list) {
        super(context, id, list);
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = inflater.inflate(R.layout.spinner_row, parent, false);
        ImageView imageView = itemView.findViewById(R.id.imgType);
        imageView.setImageResource(list.get(position).getImageId());
        TextView textView = itemView.findViewById(R.id.txtDescp);
        textView.setText(list.get(position).getDescp());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent) {
        return getView(position, convertView, parent);

    }

}
