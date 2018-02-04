package com.ticatwolves.experto.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.adaptor.QueryAdaptor;
import com.ticatwolves.experto.dataobjects.AddProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ExpertForAllFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpertForAllFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ExpertForAllFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpertForAllFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpertForAllFragment newInstance(String param1, String param2) {
        ExpertForAllFragment fragment = new ExpertForAllFragment();
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

    RecyclerView queryRecyclerView;
    private DatabaseReference databaseReference;
    final List<String> replies = new ArrayList<>();
    final List<AddProblem> data = new ArrayList<>();
    private QueryAdaptor queryAdaptor;
    final List<String> url = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expert_for_all, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        queryRecyclerView = (RecyclerView) getActivity().findViewById(R.id.expert_all_recycler_view);

        queryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseReference = FirebaseDatabase.getInstance().getReference("Queries");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                replies.clear();
                data.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot d : postSnapshot.child("all").getChildren()){
                        AddProblem problems = d.getValue(AddProblem.class);
                        url.add(postSnapshot.getKey()+" all "+d.getKey()+"");
                        Log.e("asdfghjkl",snapshot.getKey()+"/"+postSnapshot.getKey()+"/all/"+d.getKey()+"");
                        try{
                            Log.e("Total Replies = ",""+d.child("replies").getChildrenCount());
                            replies.add(""+d.child("replies").getChildrenCount());
                        }
                        catch (Exception e){
                            Log.e("Total Replies = ","0");
                            replies.add("0");
                        }
                        data.add(problems);
                    }
                }
                queryAdaptor= new QueryAdaptor(getActivity(),data,replies,url);
                queryRecyclerView.setAdapter(queryAdaptor);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
