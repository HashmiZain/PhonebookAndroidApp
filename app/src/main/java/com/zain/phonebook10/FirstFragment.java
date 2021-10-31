package com.zain.phonebook10;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.zain.phonebook10.databinding.FragmentFirstBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

// Login fragment
public class FirstFragment extends Fragment {

private FragmentFirstBinding binding;
public static String token;
public static String userid;
public static String url = "http://192.168.10.98:4567";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

      binding = FragmentFirstBinding.inflate(inflater, container, false);
      return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.Login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (binding.editTextPassword.getText().length()==0 || binding.editTextPassword.getText().length()==0)
                    Toast.makeText(view.getContext(), "Please Fill all the fields", Toast.LENGTH_SHORT).show();

                else {
                    AndroidNetworking.get(url+"/auth") // Api call for auth
                            .addHeaders("username",binding.editTextUsername.getText().toString())
                            .addHeaders("password",passHash(binding.editTextPassword.getText().toString()))
                            .build()
                            .getAsString(new StringRequestListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        // Go to contact page if success
                                            JSONObject json = new JSONObject(response);
                                            if(json.getString("status").equals("SUCCESS")) {
                                                JSONObject dataObject = json.getJSONObject("data");
                                                token = dataObject.getString("token");
                                                JSONObject tokendata = new JSONObject(new String(Base64.getDecoder().decode(token.split("\\.")[1])));
                                                userid = tokendata.getString("aud");
                                                NavHostFragment.findNavController(FirstFragment.this)
                                                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
                                            }
                                            else
                                                Toast.makeText(view.getContext(), "Failed. Please try again", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        Toast.makeText(view.getContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }

                                }
                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(view.getContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

        //Register button. Takes us to the register fragment
        binding.Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_registerFragment);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String passHash(String input){
        byte[] bytesOfMessage = new byte[0];
        try {
            bytesOfMessage = input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theMD5digest = md.digest(bytesOfMessage);
            return Base64.getEncoder().encodeToString(theMD5digest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}