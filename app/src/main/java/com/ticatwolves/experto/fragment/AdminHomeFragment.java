package com.ticatwolves.experto.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.admin.AdminAddExpertActivity;
import com.ticatwolves.experto.admin.AdminHomeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AdminHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button addUser,addExpert,addField;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addUser = (Button) getActivity().findViewById(R.id.add_user);
        addExpert = (Button) getActivity().findViewById(R.id.add_expert);
        addField = (Button) getActivity().findViewById(R.id.add_field);

        addField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.admin_addfield_dailog);
                dialog.setTitle("Custom Dialog");
                dialog.show();

                Button declineButton = (Button) dialog.findViewById(R.id.close_action);
                Button confirmButton = (Button) dialog.findViewById(R.id.confirm_action);
                final EditText field_input = (EditText) dialog.findViewById(R.id.admin_add_field);
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String f = field_input.getText().toString();
                        f = f.substring(0, 1).toUpperCase() + f.substring(1);
                        FirebaseDatabase.getInstance().getReference("Tags").push().setValue(f);
                        dialog.dismiss();
                    }
                });

            }
        });

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Add User",Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.admin_adduser_dailog);
                dialog.setTitle("Add User");

                dialog.show();

                Button declineButton = (Button) dialog.findViewById(R.id.close_action);
                Button confirmButton = (Button) dialog.findViewById(R.id.confirm_action);
                final EditText rollno = (EditText) dialog.findViewById(R.id.adduserrollno);

                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(validate(rollno.getText().toString().trim())){
                            addUser(rollno.getText().toString().trim());
                            Toast.makeText(getContext(),"user added",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(getContext(),"try later, some insternal error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        addExpert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AdminAddExpertActivity.class));
                Toast.makeText(getContext(),"Add Expert",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Boolean validate(String id) {
        if (id.length() != 10 || !(id.matches("[0-9]+"))) {
            Toast.makeText(getActivity(), "ID Should be of length 10 and numerical only", Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
        return true;
    }

    public Boolean addUser(final String rollno){
        try {
            FirebaseDatabase.getInstance().getReference("Users").child(rollno).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(getActivity(), "Already Registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("adding user",rollno);
                        FirebaseDatabase.getInstance().getReference("Users").child(rollno).child("email").setValue("yes");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            return true;
        }
        catch (Exception e){
            Log.e("error",e.toString());
            return false;
        }
    }

}
