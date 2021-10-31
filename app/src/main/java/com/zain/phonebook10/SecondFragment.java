package com.zain.phonebook10;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.zain.phonebook10.databinding.FragmentSecondBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SecondFragment extends Fragment implements ContactAdapter.OnItemClickListener {

private FragmentSecondBinding binding;

    private int batch = 10;
    private int localOffset = 0;
    private int localBatch = 7;
    private int restCount = -1;
    private RecyclerView contactList;
    private RecyclerView.LayoutManager manager;

    private Boolean isScrolling = false;

    List<Contact> contacts = new ArrayList<Contact>();
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
      binding = FragmentSecondBinding.inflate(inflater, container, false);
      return binding.getRoot();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        contactList = binding.contacts;
        manager = new LinearLayoutManager(view.getContext());
        contactList.setLayoutManager(manager);

        super.onViewCreated(view, savedInstanceState);

        contactList.setAdapter(new ContactAdapter(contacts,this));

        loadData();
        loadLocalData(true);

        contactList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrolling && isLastVisible() && dy > 0)
                {
                    isScrolling = false;
                    loadLocalData(false);
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                if(swipeDir == ItemTouchHelper.RIGHT)
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+contacts.get(position).getcontactNumber()));
                    contactList.getAdapter().notifyDataSetChanged();
                    startActivity(intent);
                }
                else if(swipeDir == ItemTouchHelper.LEFT)
                {
                    AndroidNetworking.get(FirstFragment.url + "/deletecontact")
                            .addHeaders("token", FirstFragment.token)
                            .addHeaders("contactid", contacts.get(position).getContactId() + "")
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("SUCCESS")) {
                                            MainActivity.db.deleteContact(contacts.get(position));
                                            contacts.remove(position);
                                            contactList.getAdapter().notifyDataSetChanged();
                                            Toast.makeText(view.getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(view.getContext(), "Failed Please try again", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(view.getContext(), "Failed Please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(contactList);



        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactList.invalidate();
                contacts.clear();
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_addContactFragment);

            }
        });

        binding.Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactList.invalidate();
                contacts.clear();
                MainActivity.db.clear();
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });


        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                contacts = MainActivity.db.getContact(Integer.parseInt(FirstFragment.userid),charSequence.toString());
                contactList.setAdapter(new ContactAdapter(contacts,SecondFragment.this));
                if(charSequence.length() == 0)
                {
                    loadLocalData(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadData() {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable periodicTask = new Runnable() {
            public void run() {
                // Invoke method(s) to do the work
                int localRows = MainActivity.db.numberOfRows();
                AndroidNetworking.get(FirstFragment.url + "/contactcount")
                        .addHeaders("token", FirstFragment.token)
                        .addHeaders("userid", FirstFragment.userid)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("SUCCESS")) {
                                        restCount = Integer.parseInt(response.getString("message"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
                if (localRows < restCount)
                    AndroidNetworking.get(FirstFragment.url + "/getcontact")
                            .addHeaders("token", FirstFragment.token)
                            .addHeaders("userid", FirstFragment.userid)
                            .addHeaders("limit", batch + "")
                            .addHeaders("offset", MainActivity.db.numberOfRows() + "")
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("SUCCESS")) {
                                            JSONArray data = new JSONArray(response.getString("data"));
                                            for (int i = 0; i < data.length(); i++) {
                                                JSONObject jdata = data.getJSONObject(i);
                                                Contact cont = new Gson().fromJson(jdata.toString(), Contact.class);
                                                MainActivity.db.addContact(cont);
                                            }
                                            if (localRows==0)
                                            {
                                                loadLocalData(true);
                                            }
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(getContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                                }
                            });


            }
        };

        executor.scheduleAtFixedRate(periodicTask, 0, 10, TimeUnit.SECONDS);
    }

    private void loadLocalData(boolean initial){
        List<Contact> cont = new ArrayList<Contact>();
        if(initial) {
            localBatch = 7;
            localOffset = 0;
            cont = MainActivity.db.getAllCotacts(Integer.parseInt(FirstFragment.userid),localBatch,localOffset);
            contacts = cont;
            contactList.setAdapter(new ContactAdapter(contacts,this));
        }
        else {
            cont = MainActivity.db.getAllCotacts(Integer.parseInt(FirstFragment.userid),localBatch,localOffset);
            binding.progressBar.setVisibility(View.VISIBLE);
            contacts.addAll(cont);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    contactList.getAdapter().notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            }, 500);
        }
        localOffset = localOffset + cont.size();
    }

    private boolean isLastVisible() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
        int scrolledItems = layoutManager.findFirstVisibleItemPosition();
        int totalcount = manager.getItemCount();
        int currentItems = manager.getChildCount();
        return (currentItems + scrolledItems == totalcount);
    }

    private void openContactDetails(Contact contact){
        ContactDetails details = new ContactDetails(contact);
        details.show(getActivity().getSupportFragmentManager(), "Details");
    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

    @Override
    public void OnItemClick(int position) {
        openContactDetails(contacts.get(position));
        Toast.makeText(getContext(), "Clicked "+position, Toast.LENGTH_SHORT).show();
    }
    public void backBtnMethod()
    {
        NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_addContactFragment);
    }


}
