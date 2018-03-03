package com.firastech.syncsqlitewithrestfulapi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firastech.syncsqlitewithrestfulapi.R;
import com.firastech.syncsqlitewithrestfulapi.model.Nama;

import java.util.List;

/**
 * Created by Firas Luthfi on 3/3/2018.
 */

public class NameAdapter
        extends ArrayAdapter<Nama> {

    private List<Nama> names;
    private Context context;

    public NameAdapter(Context context, int layout, List<Nama> names){
        super(context, layout, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listViewItem = inflater.inflate(R.layout.names, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        ImageView imageViewStatus = (ImageView) listViewItem.findViewById(R.id.imageViewStatus);

        Nama name = names.get(position);

        textViewName.setText(name.getName());

        if(name.getStatus()==0){
            imageViewStatus.setBackgroundResource(R.drawable.stopwatch);
        }else
            imageViewStatus.setBackgroundResource(R.drawable.success);

            return listViewItem;
        }
    }