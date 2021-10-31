package com.zain.phonebook10;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Base64;


public class ContactDetails extends AppCompatDialogFragment {

    Contact contact;

    ContactDetails(Contact cont)
    {
        contact = cont;
    }

    TextView name,number,mail;
    ImageView image;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view  = inflater.inflate(R.layout.contacnt_details,null);

        name = view.findViewById(R.id.contactName);
        number = view.findViewById(R.id.contactNumber);
        mail = view.findViewById(R.id.contactEmail);
        image = view.findViewById(R.id.viewContactImage);
        name.setText(contact.getcontactName());
        number.setText(contact.getcontactNumber());
        mail.setText(contact.getcontactEmail());
        if(contact.getContactPicture()!= null) {
            byte[] img = Base64.getDecoder().decode(contact.getContactPicture());
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 140,140, false));
        }
        else
            image.setImageResource(R.mipmap.ic_launcher_round);
        builder.setView(view)
                .setTitle("Contact Details")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }
}
