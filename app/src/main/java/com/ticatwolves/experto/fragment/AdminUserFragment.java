package com.ticatwolves.experto.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.ShowableListMenu;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AdminUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView users;
    UserListAdaptor adaptor;
    DatabaseReference udatabase;
    private ProgressDialog pd;

    public AdminUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUserFragment newInstance(String param1, String param2) {
        AdminUserFragment fragment = new AdminUserFragment();
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
        return inflater.inflate(R.layout.fragment_admin_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pd = new ProgressDialog(getActivity());

        users = (RecyclerView) getActivity().findViewById(R.id.users_recyclerview);
        users.setLayoutManager(new LinearLayoutManager(getActivity()));

        udatabase = FirebaseDatabase.getInstance().getReference("Users");
        final List<String> roll = new ArrayList<>();
        final List<String> email = new ArrayList<>();
        pd.setMessage("please wait...");
        pd.setCancelable(false);
        pd.show();
        udatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                roll.clear();
                email.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    roll.add(postSnapshot.getKey().toString());
                    Log.e("roll ",postSnapshot.getKey().toString());
                    email.add(postSnapshot.getValue().toString());
                }
                //creating adapter
                pd.dismiss();
                adaptor = new UserListAdaptor(getActivity(),roll,email);
                users.setAdapter(adaptor);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class UserListAdaptor extends RecyclerView.Adapter<UserListAdaptor.MyOwnHolder> {

        Context ctx;
        List<String> rollno,email;

        public UserListAdaptor(Context ctx,List<String> roll,List<String> email){
            this.ctx = ctx;
            this.rollno = roll;
            this.email = email;
        }

        @Override
        public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater myinflat = LayoutInflater.from(ctx);
            View myOwnView = myinflat.inflate(R.layout.admin_user_view,parent,false);
            return new MyOwnHolder(myOwnView);
        }

        @Override
        public void onBindViewHolder(MyOwnHolder holder, final int position) {
            holder.rollno.setText(rollno.get(position));
            holder.icon.setText((String)email.get(position).toUpperCase().substring(0,1));
            holder.deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("Users").child(rollno.get(position)).removeValue();
                    Toast.makeText(ctx,"Delete User",Toast.LENGTH_SHORT).show();

                    rollno.remove(position);
                    email.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,rollno.size());

                }
            });
        }

        @Override
        public int getItemCount() {
            return rollno.size();
        }

        public class MyOwnHolder extends RecyclerView.ViewHolder {
            TextView rollno,icon;
            Button deleteUser;
            public MyOwnHolder(View itemView) {
                super(itemView);
                rollno = (TextView) itemView.findViewById(R.id.rollno);
                icon = (TextView) itemView.findViewById(R.id.pimage);
                deleteUser = (Button) itemView.findViewById(R.id.deleteuser);
            }
        }
    }
}
