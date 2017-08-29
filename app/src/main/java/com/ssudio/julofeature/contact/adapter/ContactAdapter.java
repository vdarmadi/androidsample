package com.ssudio.julofeature.contact.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssudio.julofeature.R;
import com.ssudio.julofeature.contact.model.Contact;
import com.ssudio.julofeature.main.ui.IContactView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactItemViewHolder> {

    private IContactView container;
    private ArrayList<Contact> items;

    public class ContactItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgAvatar)
        public ImageView imgAvatar;

        @BindView(R.id.txtContactName)
        public TextView txtContactName;
        @BindView(R.id.txtContactPhone)
        public TextView txtContactPhone;

        @BindView(R.id.btnSMS)
        public Button btnSMS;

        public ContactItemViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public ContactAdapter(IContactView view, ArrayList<Contact> items) {
        this.container = view;
        this.items = items;
    }

    @Override
    public ContactItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);

        return new ContactItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactItemViewHolder holder, int position) {
        final Contact contact = items.get(position);

        if (contact.getImagePath() != null) {
            holder.imgAvatar.setImageBitmap(contact.getImagePath());
        }

        holder.txtContactName.setText(contact.getName());
        holder.txtContactPhone.setText(contact.getPhoneNumber());

        holder.btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                container.select(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
