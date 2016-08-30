package org.unimelb.itime.vendor.contact.helper;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.contact.widgets.Contact;

/**
 * Created by yuhaoliu on 17/08/16.
 */
public class LoadImgHelper {
    private static LoadImgHelper loadImgHelper;
    private final String TAG = "MyAPP";

    public static LoadImgHelper getInstance() {
        if(loadImgHelper == null)
        {
            loadImgHelper = new LoadImgHelper();
        }
        return loadImgHelper;
    }

    public void bindUrlWithImageView(Context mContext, Contact contact, ImageView img_v){
        if (contact.getUrl() != null){
            Log.i(TAG, "url: " + contact.getUrl());
            Picasso.with(mContext).load(contact.getUrl()).placeholder(R.drawable.invitee_selected_loading).into(img_v);
        }else {
            Picasso.with(mContext).load(R.drawable.invitee_selected_default_picture).into(img_v);
        }
    }
}
