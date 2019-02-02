package com.android.citygroom;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsSection extends Fragment {


    public SettingsSection() {
        // Required empty public constructor
    }

    Button logout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_settings_section, container, false);

       logout = view.findViewById(R.id.logout_button);
       logout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

               builder.setMessage("Are you sure you want to log out?")
                       .setCancelable(false)
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {

                               SharedPreferences sharedpreferences = getActivity().getSharedPreferences(SigninSection.MyPREFERENCES, Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor = sharedpreferences.edit();
                               editor.clear();
                               editor.commit();
                               getActivity().finish();

                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           public void onClick(final DialogInterface dialog, final int id) {
                               dialog.cancel();
                           }
                       });
               final AlertDialog alert = builder.create();
               alert.show();
           }
       });
       return view;
    }

}
