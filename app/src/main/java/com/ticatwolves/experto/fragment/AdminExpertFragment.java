package com.ticatwolves.experto.fragment;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.dataobjects.AddExperts;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AdminExpertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminExpertFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView expert;
    ExpertListAdaptor adaptor;
    DatabaseReference edatabase;
    private ProgressDialog pd;

    public AdminExpertFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminExpertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminExpertFragment newInstance(String param1, String param2) {
        AdminExpertFragment fragment = new AdminExpertFragment();
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
        return inflater.inflate(R.layout.fragment_admin_expert, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pd = new ProgressDialog(getActivity());

        expert = (RecyclerView) getActivity().findViewById(R.id.expert_recycler_view);
        expert.setLayoutManager(new LinearLayoutManager(getActivity()));
        edatabase = FirebaseDatabase.getInstance().getReference("ExpertsRegistered");

        final List<String> id = new ArrayList<>();
        final List<String> email = new ArrayList<>();
        final List<String> field = new ArrayList<>();

        pd.setMessage("please wait...");
        //pd.setCancelable(false);
        pd.show();

        final List<AddExperts> data = new ArrayList<>();
        final List<String> ids = new ArrayList<>();
        edatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ids.clear();
                data.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Log.e("mofo",postSnapshot.getKey());
                    ids.add(postSnapshot.getKey());
                    AddExperts experts = postSnapshot.getValue(AddExperts.class);
                    data.add(experts);
                }
                //creating adapter
                pd.dismiss();
                adaptor = new ExpertListAdaptor(getActivity(),ids,data);//field);
                expert.setAdapter(adaptor);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class ExpertListAdaptor extends RecyclerView.Adapter<ExpertListAdaptor.MyOwnHolder> {

        Context ctx;
        List<String> id;
        List<AddExperts> data;

        public ExpertListAdaptor(Context ctx,List<String> id,List<AddExperts> data){
            this.ctx = ctx;
            this.id = id;
            this.data = data;
        }

        @Override
        public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater myinflat = LayoutInflater.from(ctx);
            View myOwnView = myinflat.inflate(R.layout.admin_expert_view,parent,false);
            return new MyOwnHolder(myOwnView);
        }

        @Override
        public void onBindViewHolder(ExpertListAdaptor.MyOwnHolder holder, final int position) {
            AddExperts d = data.get(position);
            holder.id.setText("Expert ID "+id.get(position));
            holder.email.setText("Email id "+d.getEmail());
            holder.name.setText("Name "+d.getName());
            //holder.field.setText("Field" + d.getFields().toString());
            holder.icon.setText((String)d.getName().toUpperCase().substring(0,1));
            holder.delete_expert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //FirebaseDatabase.getInstance().getReference("Experts").child(field.get(position)).child(id.get(position)).removeValue();
                    FirebaseDatabase.getInstance().getReference("ExpertsRegistered").child(id.get(position)).removeValue();
                    Toast.makeText(ctx,"delete expert",Toast.LENGTH_SHORT).show();
                    id.remove(position);
                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,id.size());

                }
            });
        }

        @Override
        public int getItemCount() {
            return id.size();
        }

        public class MyOwnHolder extends RecyclerView.ViewHolder {
            TextView id,email,field,icon,name;
            Button delete_expert;
            public MyOwnHolder(View itemView) {
                super(itemView);
                id = (TextView) itemView.findViewById(R.id.expert_id_show);
                icon = (TextView) itemView.findViewById(R.id.pimage);
                email = (TextView) itemView.findViewById(R.id.expert_email_show);
                field = (TextView) itemView.findViewById(R.id.expert_field);
                name = (TextView) itemView.findViewById(R.id.expert_name_show);
                delete_expert = (Button) itemView.findViewById(R.id.delete_expert);
            }
        }
    }
}
