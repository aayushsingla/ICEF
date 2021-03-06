package com.macbitsgoa.icef.fragments;


import android.os.Bundle;
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
import com.macbitsgoa.icef.Adapters.Adapter_Contacts;
import com.macbitsgoa.icef.R;
import com.macbitsgoa.icef.fragments.base.BaseFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IcefContact#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IcefContact extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    Adapter_Contacts adapter;
    private ArrayList<com.macbitsgoa.icef.Lists.ContactList> ContactList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;


    public IcefContact() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment IcefContact.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(String param1) {
        BaseFragment fragment = new IcefContact();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mView = inflater.inflate(R.layout.fragment_icef_contact, container, false);
        RecyclerView mContactList = mView.findViewById(R.id.contactList);
        adapter = new Adapter_Contacts(ContactList, getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        // Snackbar.make(mView,"hgvhvukbukhklhn",Snackbar.LENGTH_LONG);
        Log.e("msg", "launched");
        mContactList.setLayoutManager(linearLayoutManager);
        mContactList.setAdapter(adapter);
        //if (FirebaseAuth.getInstance().getCurrentUser() != null) {

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference mContacts = FirebaseDatabase.getInstance().getReference().child("Contact").child(mParam1);
        mContacts.keepSynced(true);

        mContacts.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ContactList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.macbitsgoa.icef.Lists.ContactList newItem = snapshot.getValue(com.macbitsgoa.icef.Lists.ContactList.class);
                    ContactList.add(newItem);
                    Log.d("Tag", newItem.getName());
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showSnack();
                showToast(databaseError.getDetails());
                Log.d("db ref ", "Error");


            }

        });


        return mView;
    }

          /*      @Override
               public void onResume(){
                    IcefContact icefContact= IcefContact.newInstance("")

                    super.onResume();
                }
*/

}



