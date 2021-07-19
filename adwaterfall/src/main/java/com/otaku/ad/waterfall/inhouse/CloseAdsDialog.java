package com.otaku.ad.waterfall.inhouse;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.otaku.ad.waterfall.R;


public class CloseAdsDialog extends DialogFragment {
    public interface Listener {
        void OnResume();

        void OnClose();
    }

    private Listener mListener;

    public CloseAdsDialog(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Close video?")
                .setIcon(R.drawable.ic_gift)
                .setMessage("You will lose your reward")
                .setPositiveButton("Resume Video", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.OnResume();
                        dismiss();
                    }
                })
                .setNegativeButton("Close video", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.OnClose();
                        dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        return dialog;
    }
}