package com.zain.phonebook10;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.zain.phonebook10.databinding.FragmentAddContactBinding;
import com.zain.phonebook10.databinding.FragmentRegisterBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

//Fragment for adding contacts
public class AddContactFragment extends Fragment {
    private FragmentAddContactBinding binding;

    private final int PICK_IMAGE = 100;
    private boolean image_pick = false;
    private String encodedimage;
    private final int destWidth = 350;

    //Image converstion to byte array
    public byte[] convertImageToByte(Uri uri){
        byte[] data = null;
        try {
            ContentResolver cr = this.getContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if(bitmap.getWidth()>destWidth)
            bitmap = Bitmap.createScaledBitmap(bitmap, destWidth, bitmap.getHeight()/(bitmap.getWidth()/destWidth), false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        Uri imageUri;

        if (resultCode == RESULT_OK && reqCode == 100){
            imageUri = data.getData();
            byte[] img = convertImageToByte(imageUri);
            image_pick = true;
            encodedimage = Base64.getEncoder().encodeToString(img);
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            binding.contactImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, binding.contactImage.getWidth(),binding.contactImage.getHeight(), false));
        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddContactBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    image_pick = false;
                    Intent gallery = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, PICK_IMAGE);

            }
        });

        binding.addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.addContactName.getText().length()==0 || binding.addContactNumber.getText().length()==0)
                    Toast.makeText(view.getContext(), "Contact Name and number are mandatory", Toast.LENGTH_SHORT).show();
                else
                {
                    Contact cont = new Contact(Integer.parseInt(FirstFragment.userid),binding.addContactName.getText().toString()
                            ,binding.addContactNumber.getText().toString(),binding.addContactEmail.getText().toString(),"0",encodedimage);
                    AndroidNetworking.post(FirstFragment.url+"/addcontact")//Api call to insert the contact
                            .addHeaders("token",FirstFragment.token)
                            .addStringBody(new Gson().toJsonTree(cont).toString())
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getString("status").equals("SUCCESS")) {
                                            MainActivity.db.addContact(cont);
                                            NavHostFragment.findNavController(AddContactFragment.this)
                                                    .navigate(R.id.action_addContactFragment_to_SecondFragment);
                                            Toast.makeText(view.getContext(), "Contact Added", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(view.getContext(), "Failed Please try again", Toast.LENGTH_SHORT).show();
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

        binding.addContactBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AddContactFragment.this)
                        .navigate(R.id.action_addContactFragment_to_SecondFragment);
            }
        });
    }

}