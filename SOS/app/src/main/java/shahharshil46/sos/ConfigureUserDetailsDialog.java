package shahharshil46.sos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by HOME on 29-03-2015.
 */
public class ConfigureUserDetailsDialog extends DialogFragment {

    Context context;



    public ConfigureUserDetailsDialog(){

    }

    public ConfigureUserDetailsDialog(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }



    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // Get the layout inflater
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        final View v = inflater.inflate(R.layout.configure_user_details, null);
//
//        final EditText userNameEditTxt = (EditText) v.findViewById(R.id.userNameEditText);
//        final EditText userMobileEditText = (EditText) v.findViewById(R.id.userMobileEditText);
//        final EditText userEmailEditText = (EditText) v.findViewById(R.id.userEmailEditText);
//
//        SharedPreferences sharedPreferences = context.getSharedPreferences("SOS_PREF", Context.MODE_PRIVATE);
//        boolean fetchAccountDetails = false;
//        if(sharedPreferences.contains("USER_NAME") && sharedPreferences.contains("USER_MOBILE_NO") && sharedPreferences.contains("USER_EMAIL_ID")){
//            Log.d("WHEREAMI","USER_NAME in sharedPreferences is "+sharedPreferences.getString("USER_NAME",""));
//            if(sharedPreferences.getString("USER_NAME","").length()>0){
//                userNameEditTxt.setText(sharedPreferences.getString("USER_NAME",""));
//            }
//
//            if(sharedPreferences.getString("USER_MOBILE_NO","").length()>0){
//                userMobileEditText.setText(sharedPreferences.getString("USER_MOBILE_NO",""));
//            }
//
//            if(sharedPreferences.getString("USER_EMAIL_ID","").length()>0){
//                userEmailEditText.setText(sharedPreferences.getString("USER_EMAIL_ID",""));
//            }
//
//        }
//        builder.setTitle("Change User Details");
//        builder.setView(v)
//                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//
//                        if(userNameEditTxt.getText()!=null && userMobileEditText.getText()!=null && userEmailEditText.getText()!=null){
//                            SharedPreferences sharedPreferences = context.getSharedPreferences("SOS_PREF", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
////                            editor.remove("USER_NAME");
////                            editor.remove("USER_EMAIL_ID");
////                            editor.remove("USER_MOBILE_NO");
//                            editor.putString("USER_NAME",userNameEditTxt.getText().toString());
//                            editor.putString("USER_EMAIL_ID",userMobileEditText.getText().toString());
//                            editor.putString("USER_MOBILE_NO",userEmailEditText.getText().toString());
//                            editor.commit();
//                            OnFinishedDialogListener onFinishedDialogListener = (OnFinishedDialogListener) getActivity();
//                            onFinishedDialogListener.onFinishDialog("All User details saved in shared preferences");
//                            dismiss();
//                        }
//                        else{
//                            Toast.makeText(getActivity().getApplicationContext(), "All fields are necessary", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
        AlertDialog dialog = builder.create();
//        dialog.setCancelable(false);
////        dialog.setOnKeyListener(new DialogInterface.OnKeyListener()
////        {
////            @Override
////            public boolean onKey(android.content.DialogInterface dialog,
////                                 int keyCode,android.view.KeyEvent event)
////            {
////                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
////                {
////                    // To dismiss the fragment when the back-button is pressed.
//////                    dismiss();
//////                    return true;
////                }
////                // Otherwise, do nothing else
////                else return false;
////            }
////        });
        return dialog;
//
    };


}
