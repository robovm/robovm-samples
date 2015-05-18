package org.robovm.samples.contractr.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;

public class ClientListAdapter extends BaseAdapter {

    ClientModel clientModel;
    LayoutInflater inflater;

    public ClientListAdapter(ClientModel clientModel, LayoutInflater inflater) {
        this.clientModel = clientModel;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return clientModel.count();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Client c = clientModel.get(position);
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(c.getName());

        return view;

    }

    @Override
    public Object getItem(int position) {
        return clientModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }
}