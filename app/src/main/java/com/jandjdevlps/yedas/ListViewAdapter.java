package com.jandjdevlps.yedas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewitem> data;
    private int layout;
    private LayoutInflater inflater;
    public ListViewAdapter(Context context, int layout, ArrayList<ListViewitem> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }


    public String getDocname(int position) {
        return data.get(position).getDocs();
    }
    public String getWritername(int position){ return data.get(position).getName();}

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }
        ListViewitem listviewitem=data.get(position);

        TextView documents = convertView.findViewById(R.id.document_name);
        documents.setText(listviewitem.getDocs());

        TextView name=convertView.findViewById(R.id.sender_name);
        name.setText(listviewitem.getName());
        return convertView;
    }
}
