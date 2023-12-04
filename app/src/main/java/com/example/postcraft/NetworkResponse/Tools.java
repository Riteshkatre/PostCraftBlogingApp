package com.example.postcraft.NetworkResponse;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.postcraft.R;

public class Tools {

    private Context context;
    private Dialog dialog;
    private Handler handler;

    public Tools() {
    }

    public Tools(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        handler = new Handler(Looper.getMainLooper());
    }

    public static boolean isValidEmail(String str) {
        if (!TextUtils.isEmpty(str)) {
            return Patterns.EMAIL_ADDRESS.matcher(str.toLowerCase()).matches();
        }
        return false;
    }



    public void showLoading() {
        try {
            if (dialog != null) {
                dialog.setContentView(R.layout.loadingdialog);
                dialog.setCancelable(false);
                if (!dialog.isShowing()) {
                    dialog.show();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLoading() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}