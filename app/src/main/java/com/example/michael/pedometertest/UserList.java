package com.example.michael.pedometertest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Michael on 7/13/2018.
 */

public class UserList extends ArrayAdapter<UserModel> {
    private Activity context;
    List<UserModel> users;

    public UserList(Activity context, List<UserModel> users){
        super(context, R.layout.layout_user_list, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_user_list, null, true);
        TextView textViewCurrentLevel = (TextView) listViewItem.findViewById(R.id.textViewCurrentLevel);
        TextView textViewUserName = (TextView) listViewItem.findViewById(R.id.textViewUserName);
        TextView textViewTotalSteps = (TextView) listViewItem.findViewById(R.id.textViewTotalSteps);

        UserModel user = users.get(position);
        textViewUserName.setText((position + 1) + ": " + user.getUserName());
        textViewCurrentLevel.setText("Level: " + user.getUserCurrentLevel());
        textViewTotalSteps.setText("Total Steps: " + user.getUserTotalSteps());

        return listViewItem;
    }
}
