package shahharshil46.sos;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import shahharshil46.db.CreateDatabase;
import shahharshil46.pojo.ContactPOJO;


public class MainActivity extends Activity implements LocationListener, OnFinishedDialogListener {

    private static final int PICK_CONTACT = 1;
    private LocationManager locationManager;
    private String provider;
    static TextView msgText;
    private TextView smsCount, welcomeText;
//    EditText phoneNoEditText;
    Button sendSMS;

    boolean isGPSEnabled, isNetworkEnabled;
    static double lng, lat;
    public static ArrayList<ContactPOJO> smsContactList = new ArrayList<ContactPOJO>();

    public static CreateDatabase myDb;

    private static LinearLayout showNumberLayout;

    private static LinearLayout showNameLayout;

    private static Context context;

    private static String helpMsg = "";

    private static String add = "";

    public static ContactPOJO contact;

    public static MainActivity mainActivity;

    PendingIntent sentPI, deliveredPI;

    SentBroadcastReceiver sentReceiver;
    DeliveredBroadcastReceiver deliveredReceiver;

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    public String userName, userEmailId, userMobileNo;

    private String id = null;
    private String email = null;
    private String phone = null;
    private String accountName = null;
    private String name = null;

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        mainActivity = this;

        sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        msgText = (TextView) findViewById(R.id.locationText);

        welcomeText = (TextView) findViewById(R.id.welcometxt);

        sendSMS = (Button) findViewById(R.id.sendSMSBtn);
        smsCount = (TextView) findViewById(R.id.smsCount);
        showNumberLayout = (LinearLayout) findViewById(R.id.showNumberLayout);


        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        Log.d("WHEREAMI", "local countryCode is "+countryCode);
        String myCountry = Locale.getDefault().getCountry();
        Log.d("WHEREAMI", "local myCountry is "+myCountry);



        myDb = CreateDatabase.getInstance(getApplicationContext());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.

        Log.d("WHEREAMI","Criteria is "+criteria.describeContents());
        provider = locationManager.getBestProvider(criteria, true);
        Log.d("WHEREAMI","Provider obtained is "+provider);
        Location location = null;
        if(provider!=null) {
            location = locationManager.getLastKnownLocation(provider);
            if(location!=null)
                Log.d("WHEREAMI", "Provider obtained location is " + location.getLatitude()+" : "+location.getLongitude());
            else
                Log.d("WHEREAMI", "Provider obtained location is null");

        }
        else{
            Toast.makeText(context,"Please enable location provider first",Toast.LENGTH_SHORT).show();
        }

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("WHEREAMI","isGPSEnabled "+isGPSEnabled);
        Log.d("WHEREAMI","isNetworkEnabled "+isNetworkEnabled);
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 400, 1, this);
            Log.d("WHEREAMI", "Network Enabled");
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    add = getMyLocationAddress(lat, lng);
                    Log.d("WHEREAMI", "Lat : Long - " + String.valueOf(lat)
                            + " : " + String.valueOf(lng) + " add - "
                            + add);
                    helpMsg = "I am at "+((add.length()>0)?("Addr: "+add+"."):"") +" "+this.formMapURL(lat, lng);
                    Log.d("WHEREAMI","Help Msg is "+helpMsg+" sms Count: "+new Integer((int) Math.ceil(new Double(helpMsg.length())/140)));
					msgText.setText(helpMsg);
                    smsCount.setText("sms Count: "+new Integer((int) Math.ceil(new Double(helpMsg.length())/140)));
                }
                else{
                    helpMsg = "Network Location is not available.";
                    msgText.setText(helpMsg);
                    smsCount.setText("sms Count: "+new Integer((int) Math.ceil(new Double(helpMsg.length())/140)));
                }
            }
            else{
                Log.d("WHEREAMI","Network Location Manager is null");
            }
        }
        // if GPS Enabled get lat/long using GPS Services
        else if (isGPSEnabled) {
//            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 400, 1, this);
                Log.d("WHEREAMI", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        Log.d("WHEREAMI", "Lat : Long - " + String.valueOf(lat)
                                + " : " + String.valueOf(lng) + " add - "
                                + getMyLocationAddress(lat, lng));
                        add = getMyLocationAddress(lat, lng);
                        helpMsg = "I am at "+((add.length()>0)?("Addr: "+add+"."):"") +" "+this.formMapURL(lat, lng);
                        Log.d("WHEREAMI","Help Msg is "+helpMsg+" sms Count: "+new Integer((int) Math.ceil(new Double(helpMsg.length())/140)));
                        msgText.setText(helpMsg);
                        smsCount.setText("sms Count: "+new Integer((int) Math.ceil(new Double(helpMsg.length())/140)));
                    }
                    else{
                        Log.d("WHEREAMI","GPS Location is null");
                        helpMsg = "GPS Location is not available.";
                        msgText.setText(helpMsg);
                        smsCount.setText("sms Count: "+new Integer((int) Math.ceil(new Double(helpMsg.length())/140)));
                    }
                }
                else{
                    Log.d("WHEREAMI","GPS Location Manager is null");
                }

//            }
//            else{
//                Log.d("WHEREAMI","GPS Location is null");
//            }
        }
        sendSMS.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String msgToSend = msgText.getText().toString();
                if(msgToSend.length()>0){
                    Log.d("WHEREAMI", "Sending SMS");
                    smsContactList = myDb.getContactNumbers(context);
                    if(smsContactList.size()==0){
                        Toast.makeText(getApplicationContext(),
                                "Please enter a mobile number", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        for(int i=0;i<smsContactList.size();i++){
                            String phoneNo = smsContactList.get(i).getMobileNo()+"";
                            // working from regular SMS
                            if (phoneNo.length() >= 10) {

                                Log.d("WHEREAMI","Sending SMS to "+phoneNo+" : message content is "+msgToSend);
                                try {
                                    mainActivity.sendSMS(phoneNo, msgToSend);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),
                                            "SMS faild, please try again.",
                                            Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        phoneNo+" is invalid.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });
        populateContactList(context);
//        OwnerInfo ownerInfo = new OwnerInfo(this);
        SharedPreferences sharedPreferences = this.getSharedPreferences("SOS_PREF", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean fetchFromDevice = false;
        if(sharedPreferences.contains("USER_NAME")){
            Log.d("WHEREAMI","USER_NAME in sharedPreferences is "+sharedPreferences.getString("USER_NAME",""));
            if(sharedPreferences.getString("USER_NAME","").length()>0){
                userName = sharedPreferences.getString("USER_NAME","");
                userEmailId = sharedPreferences.getString("USER_EMAIL_ID","");
                userMobileNo = sharedPreferences.getString("USER_MOBILE_NO","");
                welcomeText.setText("Hello "+userName+"\n"+"Your Help Message is as below");

            }
            else{
                fetchFromDevice = true;
            }
        }
        else{
//            ConfigureUserDetailsDialog configureUserDetailsDialog = new ConfigureUserDetailsDialog(context);
            fetchFromDevice = true;
        }

        if(fetchFromDevice){
            showAccountsDialog(context);
        }
        Account acctSelected;
        AccountManager manager;

    }

    @Override
    protected void onResume() {
        super.onResume();
        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.

        Log.d("WHEREAMI","Criteria is "+criteria.describeContents());
        provider = locationManager.getBestProvider(criteria, true);
        if(provider!=null)
            locationManager.requestLocationUpdates(provider, 10, 1, this);
        else{
            Toast.makeText(context,"Please enable location provider first",Toast.LENGTH_SHORT).show();
        }

        welcomeText.setText("Hello "+userName+"\n"+"Your Help Message is as below");
        welcomeText.setText("Hello "+userName+"\n"+"Your Help Message is as below");

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
//        setMenuBackground();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_import_contact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
            return true;
        }
        else if(id == R.id.action_create_contact){
            MainActivity.contact = new ContactPOJO();
            GetContactInfoDialog dialog = new GetContactInfoDialog(getApplicationContext());
            dialog.show(getFragmentManager(), "");
            return true;
        }
        else if(id == R.id.action_configure_user_details){
            ConfigureUserDetailsDialog configureUserDetailsDialog = new ConfigureUserDetailsDialog(context);
            configureUserDetailsDialog.show(getFragmentManager(), "");
            return true;
        }
        else if(id == R.id.action_show_user_location_history){
            Toast.makeText(context, "WIP...!!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    protected void setMenuBackground(){
//        // Log.d(TAG, "Enterting setMenuBackGround");
//        getLayoutInflater().setFactory( new LayoutInflater.Factory() {
//            public View onCreateView(String name, Context context, AttributeSet attrs) {
//                if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
//                    try { // Ask our inflater to create the view
//                        LayoutInflater f = getLayoutInflater();
//                        final View view = f.createView( name, null, attrs );
//                        /* The background gets refreshed each time a new item is added the options menu.
//                        * So each time Android applies the default background we need to set our own
//                        * background. This is done using a thread giving the background change as runnable
//                        * object */
//                        new Handler().post( new Runnable() {
//                            public void run () {
//                                view.setAlpha(1);
//                                // sets the background color
//                                view.setBackgroundColor(Color.DKGRAY);
//                                // sets the text color
//                                ((TextView) view).setTextColor(Color.BLACK);
//                                // sets the text size
//                                ((TextView) view).setTextSize(18);
//                            }
//                        } );
//                        return view;
//                    }
//                    catch ( InflateException e ) {}
//                    catch ( ClassNotFoundException e ) {}
//                }
//                return null;
//            }});
//    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                Uri contactData = data.getData();
                Cursor c = managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {

                    String id = c.getString(c
                            .getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    String hasPhone = c
                            .getString(c
                                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + id, null, null);
                        phones.moveToFirst();
                        String cNumber = phones.getString(phones
                                .getColumnIndex("data1"));
                        System.out.println("number is:" + cNumber);
//					if(cNumber.length()>10)
//						cNumber = cNumber.substring((cNumber.length()-11));
//					System.out.println("truncated number is:" + cNumber);
                        if(cNumber.length()>=10){
                            String name = ""+c
                                    .getString(c
                                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            System.out.println("name is: " + name);
                            ContactPOJO contact1 = new ContactPOJO();
                            contact1.setContactName(name);
                            contact1.setMobileNo(cNumber);
                            ContentValues values = new ContentValues();
                            Log.d("WHEREAMI","Saving "+contact1.getMobileNo()+" - "+contact1.getContactName());
                            values.put("contact_number", contact1.getMobileNo()+"");
                            values.put("contact_name", contact1.getContactName());
                            Object myDb;
                            int contactId = (int) MainActivity.myDb.insert("sms_contact", null, values);
                            Log.d("WHEREAMI","Contact ID is "+contactId);
                            MainActivity.smsContactList.add(contact1);
                            MainActivity.populateContactList(context);
                            Toast.makeText(context, "Contact saved successfully.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Selected contact has no mobile number.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(context, "Selected contact has no phone number.", Toast.LENGTH_SHORT).show();
                    }


                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("ResourceType")
    public static void populateContactList(final Context context) {
        ArrayList<ContactPOJO> smsContactList1 = myDb.getContactNumbers(context);
		/*ArrayList<TextView> smsContactTextViewList = new ArrayList<TextView>();
		ArrayList<TextView> smsContactNameTextViewList = new ArrayList<TextView>();*/
        showNumberLayout.removeAllViews();
        for (int i = 0; i < smsContactList1.size(); i++) {
            Log.d("WHEREAMI", "Populating view for " + smsContactList1.get(i).toString());
            final RelativeLayout newContactLayout = new RelativeLayout(context);
            final LinearLayout newNumberLayout = new LinearLayout(context);
            final LinearLayout newNameLayout = new LinearLayout(context);
            final LinearLayout newButtonLayout = new LinearLayout(context);

            newContactLayout.setGravity(Gravity.FILL_VERTICAL);

            newNameLayout.setOrientation(LinearLayout.HORIZONTAL);
            newNameLayout.setGravity(Gravity.FILL_HORIZONTAL);

            newNumberLayout.setOrientation(LinearLayout.HORIZONTAL);
            newNumberLayout.setGravity(Gravity.FILL_HORIZONTAL);

            newButtonLayout.setOrientation(LinearLayout.VERTICAL);
            newButtonLayout.setGravity(Gravity.FILL_VERTICAL);

            final TextView smsContactNumberAddedByUser = new TextView(context);
            final TextView smsContactNameAddedByUser = new TextView(context);

            smsContactNameAddedByUser.setTextColor(Color.DKGRAY);
            Log.d("WHEREAMI","(int) context.getResources().getDimension(R.dimen.font_size_15sp) is "+(int) context.getResources().getDimension(R.dimen.font_size_15sp));
            smsContactNameAddedByUser.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) context.getResources().getDimension(R.dimen.font_size_15sp));
//            smsContactNameAddedByUser.setTextSize((int) context.getResources().getDimension(R.dimen.font_size_15sp));

            smsContactNumberAddedByUser.setTextColor(Color.BLACK);
            smsContactNumberAddedByUser.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) context.getResources().getDimension(R.dimen.font_size_15sp));

            smsContactNumberAddedByUser.setText("" + smsContactList1.get(i).getMobileNo());
            smsContactNameAddedByUser.setText(""+smsContactList1.get(i).getContactName());

            newNameLayout.addView(smsContactNameAddedByUser);
            newNumberLayout.addView(smsContactNumberAddedByUser);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) context.getResources().getDimension(R.dimen.dimen_30dp), (int) context.getResources().getDimension(R.dimen.dimen_30dp));
            params.setMargins(5, 5, 5, 5);
            Button smsBtn = new Button(context);
            smsBtn.setBackgroundResource(R.drawable.sms1);
            smsBtn.setLayoutParams(params);
            smsBtn.setTag(smsContactList1.get(i).getContactId());
            newButtonLayout.addView(smsBtn);

            Button deleteBtn = new Button(context);
            deleteBtn.setBackgroundResource(R.drawable.button_cancel);
            deleteBtn.setLayoutParams(params);
            deleteBtn.setTag(smsContactList1.get(i).getContactId());
            newButtonLayout.addView(deleteBtn);

            newContactLayout.removeAllViews();
            newNameLayout.setId(1);
            newNumberLayout.setId(2);
            newButtonLayout.setId(3);


            RelativeLayout.LayoutParams lNameprams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams lNumberprams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams lButtonprams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            lNameprams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            lNumberprams.addRule(RelativeLayout.BELOW, 1);
            lButtonprams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

            newNameLayout.setLayoutParams(lNameprams);
            newNumberLayout.setLayoutParams(lNumberprams);
            newButtonLayout.setLayoutParams(lButtonprams);

            newContactLayout.addView(newNameLayout);
            newContactLayout.addView(newNumberLayout);
            newContactLayout.addView(newButtonLayout);

            View line = new View(context);
            line.setBackgroundColor(Color.rgb(51, 51, 51));
            RelativeLayout.LayoutParams lContactLayoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 2);
            lContactLayoutparams.addRule(RelativeLayout.BELOW, 3);
            lContactLayoutparams.setMargins(0, 5, 0, 2);
            line.setLayoutParams(lContactLayoutparams);
            newContactLayout.addView(line);



            showNumberLayout.addView(newContactLayout);

            smsContactNumberAddedByUser.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });




            smsBtn.setOnClickListener(new View.OnClickListener() {

                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                    String msgToSend = msgText.getText().toString();
                    Log.d("WHEREAMI","Sending SMS to "+v.getTag()+" : message content is "+msgToSend);

                    int contactId = 0;
                    contactId = Integer.parseInt(v.getTag()+"");
                    if(contactId>0){
                        ContactPOJO smsToContact = myDb.getContactFromContactID(context, contactId);
                        try {
                            mainActivity.sendSMS(smsToContact.getMobileNo(), msgToSend);
                            Toast.makeText(context,
                                    "Sending SMS to " + smsToContact.getContactName() + ".",
                                    Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e) {
                            Toast.makeText(context,
                                    "Faild to send SMS to "+smsToContact.getContactName()+", please try again.",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
            });


            deleteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//					myDb.executeQuery("delete from SMS_CONTACT where contact_number="+Long.parseLong((String) v.getTag()));
                    Log.d("WHEREAMI","delete contact "+v.getTag());
                    int id = myDb.delete("sms_contact","sms_id ="+v.getTag(),null);
                    Log.d("WHEREAMI","Deleted contactId = "+id);
                    smsContactNumberAddedByUser.setText("");
                    smsContactNumberAddedByUser.setVisibility(View.INVISIBLE);
                    smsContactNumberAddedByUser.setWidth(0);
                    smsContactNumberAddedByUser.setHeight(0);

                    smsContactNameAddedByUser.setText("");
                    smsContactNameAddedByUser.setVisibility(View.INVISIBLE);
                    smsContactNameAddedByUser.setWidth(0);
                    smsContactNameAddedByUser.setHeight(0);

//					showNumberLayout.removeView(smsContactNumberAddedByUser);
//					showNumberLayout.removeView((View) smsContactNumberAddedByUser.getParent());

                    showNumberLayout.removeAllViews();
                    MainActivity.populateContactList(context);
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        String add = getMyLocationAddress(lat, lng);
		if (msgText != null) {
            add = getMyLocationAddress(lat, lng);
            helpMsg = "I am at "+((add.length()>0)?("Addr: "+add+"."):"") +" "+this.formMapURL(lat, lng);
            Log.d("WHEREAMI","Help Msg is "+helpMsg+" sms Count: "+new Integer((int) Math.ceil(new Double(helpMsg.length())/140)));
            msgText.setText(helpMsg);
            smsCount.setText("sms Count: "+ new Integer((int) Math.ceil(new Double(helpMsg.length())/140))  );
		} else {
			Log.d("WHEREAMI", "Lat : Long - " + String.valueOf(lat) + " : "
					+ String.valueOf(lng) + "" + add);
		}
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(sentReceiver!=null)
            unregisterReceiver(sentReceiver);
        if(deliveredReceiver!=null)
            unregisterReceiver(deliveredReceiver);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    public String getMyLocationAddress(double lat, double lng) {
        String add = "";
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> address = geocoder.getFromLocation(lat, lng, 1);
            Log.d("WHEREAMI", "size of address is " + address.size());
            if (address.size() > 0) {
                Address fetchedAddress = address.get(0);
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(
                            "");
                }
                add = strAddress.toString();
                Log.d("WHEREAMI", "Add is " + add);
            } else {
                add = "Error while fetching address";
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return add;
    }

    private void sendSMS(final String phoneNumber, final String message)
    {
        Log.d("WHEREAMI", "sendSMS for : "+phoneNumber+" message : "+message);
        //---when the SMS has been sent---
        sentReceiver = new SentBroadcastReceiver(phoneNumber, message);
        registerReceiver(sentReceiver, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        deliveredReceiver = new DeliveredBroadcastReceiver(phoneNumber, message);
        registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
    private String formMapURL(double lat, double lon){
        int ZOOM_LEVEL = 12;
        String mapUrl = "http://maps.google.com/maps?q=loc"+lat+"+"+lon+"&z="+ZOOM_LEVEL;
//         https://www.google.com/maps?q=loc:19.19883915+72.85672971&z=10
//         z is the zoom level (1-20)
//         q is the search query, if it is prefixed by loc: then google assumes it is a lat lon separated by a +
        return mapUrl;

    }

    private void showAccountsDialog(final Context context){
//            OwnerInfo ownerInfo = new OwnerInfo(context);

            final AccountManager manager = AccountManager.get(context);
            final Account[] accounts = manager.getAccountsByType("com.google");
            int selectedAccountIndex;
            if(accounts.length>0){
                String accountNameArray[] = new String[accounts.length];
                for(int i=0;i<accounts.length;i++){
                    Log.d("WHEREAMI","accounts[i].name "+accounts[i].name+" accounts[i].type "+accounts[i].type);
                    accountNameArray[i] = accounts[i].name;
                }
                Log.d("WHEREAMI","Multiple google accounts found");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select a Google Account")
                        .setItems(accountNameArray, new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("WHEREAMI", "accounts[which].name " + accounts[which].name + " accounts[which].type " + accounts[which].type);
                                Toast.makeText(context, "You selected " + accounts[which].name, Toast.LENGTH_LONG);

                                if (accounts[which].name != null) {
                                    accountName = accounts[which].name;

                                    ContentResolver cr = context.getContentResolver();
                                    Cursor emailCur = cr.query(
                                            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                            ContactsContract.CommonDataKinds.Email.DATA + " = ?",
                                            new String[]{accountName}, null);
                                    SharedPreferences sharedPreferences = context.getSharedPreferences("SOS_PREF", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    while (emailCur.moveToNext()) {
                                        id = emailCur
                                                .getString(emailCur
                                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
                                        email = emailCur
                                                .getString(emailCur
                                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                        String newName = emailCur
                                                .getString(emailCur
                                                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                        if (name == null || newName.length() > name.length())
                                            name = newName;

                                        Log.d("WHEREAMI", "Got contacts " + "ID " + id + " Email : " + email
                                                + " Name : " + name);

                                        editor.putString("USER_NAME",name);
                                        editor.putString("USER_EMAIL_ID",email);

                                    }

                                    emailCur.close();
                                    if (id != null) {

                                        // get the phone number
                                        Cursor pCur = cr.query(
                                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                        + " = ?", new String[]{id}, null);

                                        while (pCur.moveToNext()) {
                                            phone = pCur
                                                    .getString(pCur
                                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                            Log.d("WHEREAMI", "Got contacts " + "phone" + phone);
                                        }
                                        editor.putString("USER_MOBILE_NO",phone);
                                        pCur.close();
                                    }
                                    editor.commit();

                                    userName = sharedPreferences.getString("USER_NAME",accounts[which].name);
                                    userEmailId = sharedPreferences.getString("USER_EMAIL_ID",accounts[which].name);
                                    userMobileNo = sharedPreferences.getString("USER_MOBILE_NO","");
                                    welcomeText.setText("Hello "+userName+"\n"+"Your Help Message is as below");
                                } else {
                                    Log.d("WHEREAMI", "Google account name not available.");
                                    Toast.makeText(context, "Please set up Google account in your phone", Toast.LENGTH_LONG).show();
                                    // decide next action
                                    // navigate user to accounts to set up google account or show dialog to enter user name
                                }
//                                OnFinishedDialogListener onFinishedDialogListener = mainActivity;
//                                onFinishedDialogListener.onFinishDialog("User has selected the account");
                            }
                        });
                AlertDialog accountSelectorDialog = builder.create();
                accountSelectorDialog.show();

            }

//        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setTitle("Reset...");
//        alertDialog.setMessage("Are you sure?");
//        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//// here you can add functions
//            }
//        });
////        alertDialog.setIcon(R.drawable.icon);
//        alertDialog.show();

    }
//    protected synchronized void buildGoogleApiClient() {
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Plus.API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
//                .setAccountName("users.account.name@gmail.com")
//                .build();
//        client.connect();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }


    @Override
    public void onFinishDialog(String inputText) {
        Log.d("WHEREAMI","Returning from dialog with inputText as "+inputText);
        SharedPreferences sharedPreferences = context.getSharedPreferences("SOS_PREF", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("USER_NAME","");
        userEmailId = sharedPreferences.getString("USER_EMAIL_ID","");
        userMobileNo = sharedPreferences.getString("USER_MOBILE_NO","");
        welcomeText.setText("Hello "+userName+"\n"+"Your Help Message is as below");
    }
}
