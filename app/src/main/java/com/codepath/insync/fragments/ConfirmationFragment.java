package com.codepath.insync.fragments;

import android.os.Bundle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;


public class ConfirmationFragment extends DialogFragment {

    public interface UpdateDraftDialogListener {
        void onConfirmUpdateDialog(int position);
    }

    public ConfirmationFragment() {
        // Empty constructor required for DialogFragment
    }

    public static ConfirmationFragment newInstance(String message, String positive, String negative) {
        ConfirmationFragment frag = new ConfirmationFragment();
        Bundle args = new Bundle();

        args.putString("message", message);
        args.putString("positive", positive);
        args.putString("negative", negative);

        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getArguments().getString("message"));
        String positive = getArguments().getString("positive");
        String negative = getArguments().getString("negative");
        final UpdateDraftDialogListener listener = (UpdateDraftDialogListener) getActivity();

        alertDialogBuilder.setPositiveButton(positive,  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int position) {
                listener.onConfirmUpdateDialog(DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onConfirmUpdateDialog(DialogInterface.BUTTON_NEGATIVE);
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }
}