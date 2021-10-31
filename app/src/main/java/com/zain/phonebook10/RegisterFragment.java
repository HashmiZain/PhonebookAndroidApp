package com.zain.phonebook10;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zain.phonebook10.databinding.FragmentFirstBinding;
import com.zain.phonebook10.databinding.FragmentRegisterBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Class for new user registration
public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;


    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ){

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.register.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (binding.registerUsername.getText().length()==0 || binding.registerpassword.getText().length()==0)
                    Toast.makeText(view.getContext(), "Please Fill all the fields", Toast.LENGTH_SHORT).show();
                else {
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("username", binding.registerUsername.getText());
                        jo.put("password", FirstFragment.passHash(binding.registerpassword.getText().toString()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONArray ja = new JSONArray();
                    ja.put(jo);
                    AndroidNetworking.post(FirstFragment.url+"/register") // Api call to register new user
                            .addStringBody(jo.toString())
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getString("status").equals("SUCCESS")) { // If success return to login page
                                            NavHostFragment.findNavController(RegisterFragment.this)
                                                    .navigate(R.id.action_registerFragment_to_FirstFragment);
                                            Toast.makeText(view.getContext(), "Created", Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                            Toast.makeText(view.getContext(), "Failed Please try again with a different Username", Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onError(ANError error) {
                                    Toast.makeText(view.getContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                                }
                            });

                }


            }
        });

        binding.registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(RegisterFragment.this)
                        .navigate(R.id.action_registerFragment_to_FirstFragment);
            }
        });



    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}