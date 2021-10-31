package com.zain.phonebook10;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

//Recyclerview Adapter class
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    //private List<Contact> contacts;
    private boolean lastItem = false;
    private List<Contact> contacts;
    private OnItemClickListener listener;
    public ContactAdapter(List<Contact> data,OnItemClickListener listener )
    {
        contacts = data;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.contact_item, parent,false);
        return new ContactViewHolder(view, listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        //String name = contacts.get(position).getContactName();
        Contact name = contacts.get(position);
        holder.contactName.setText(name.getcontactName()); //Assign contact name to the view holder
        if(name.getContactPicture()!= null) {
            byte[] img = Base64.getDecoder().decode(name.getContactPicture());
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            holder.contactImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 140,140, false));
        }
        else
            holder.contactImage.setImageResource(R.mipmap.ic_launcher_round);

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }



    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView contactImage;
        TextView contactName;

        OnItemClickListener listener;

        public ContactViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_name);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.OnItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }




}
