package com.ssudio.julofeature.contact.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ssudio.julofeature.contact.model.Contact;

import java.util.ArrayList;

public class JuloContactProvider {
    private ContentResolver contentResolver;

    public JuloContactProvider(ContentResolver cr) {
        contentResolver = cr;
    }

    public ArrayList<Contact> getPhoneContacts() {
        ArrayList<Contact> result = new ArrayList<>();

        Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (phones != null) {
            Log.d("JuloContactProvider", "" + phones.getCount());

            if (phones.getCount() == 0) {
                return result;
            }

            while (phones.moveToNext()) {
                String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String emailAddress = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));

                /*String imageThumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                Bitmap bitmapThumbnail = null;

                try {
                    if (imageThumb != null) {
                        bitmapThumbnail = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(imageThumb));
                    } else {
                        Log.e("JuloContactProvider", "No image found");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                Contact contact = new Contact();

                contact.setImagePath(openPhoto(Long.parseLong(id)));
                contact.setName(name);
                contact.setPhoneNumber(phoneNumber);

                result.add(contact);
            }
        } else {
            Log.e("JuloContactProvider", "Cursor closed");
        }

        return result;
    }

    @Nullable
    private Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = contentResolver.query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);

        if (cursor == null) {
            return null;
        }

        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);

                if (data != null) {
                    return BitmapFactory.decodeByteArray(data, 0, data.length - 1);
                }
            }
        } finally {
            cursor.close();
        }

        return null;
    }
}
