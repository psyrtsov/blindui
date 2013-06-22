package com.AlinasAndroid.blindui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity implements View.OnTouchListener, TextToSpeech.OnInitListener {

    private float downXValue, downYValue;
    private LinearLayout layMain;
    private ArrayList<Contact> mContactList;
    private int contactNum;
    private TextToSpeech tts;
    private ContentResolver cr;
    private int numStarred;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        numStarred =0;
        mContactList = new ArrayList<Contact>();
        cr = getContentResolver();
        loadContacts();

        // Set main.XML as the layout for this Activity
        setContentView(R.layout.activity_main);

        contactNum = numStarred;
        setContactNameText(contactNum);

        tts = new TextToSpeech(this, this);

        layMain = (LinearLayout) findViewById(R.id.layout_main);
        layMain.setOnTouchListener((View.OnTouchListener) this);


        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);

        if (firstrun) {
            showTutorial();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
        }

    }


    private void showTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_tutorial, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void setContactNameText(int iContactNum) {
        TextView t1, t2, tPhone1, tPhone2, tStatus1, tStatus2;
        ImageView img1, img2;

        t1 = (TextView) findViewById(R.id.contact1);
        t2 = (TextView) findViewById(R.id.contact2);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        tPhone1 = (TextView) findViewById(R.id.contact1phone);
        tPhone2 = (TextView) findViewById(R.id.contact2phone);
        tStatus1 = (TextView) findViewById(R.id.status1);
        tStatus2 = (TextView) findViewById(R.id.status2);

        Uri photoUri = Uri.parse(mContactList.get(iContactNum).getPhoto());
        img1.setImageURI(photoUri);
        img2.setImageURI(photoUri);

        if(mContactList.get(iContactNum).getName() != null){
            t1.setText(mContactList.get(iContactNum).getName() + " - " + mContactList.get(iContactNum).getNumTypeString());
            t2.setText(mContactList.get(iContactNum).getName() + " - " + mContactList.get(iContactNum).getNumTypeString());
        }else {
            t1.setText("Unknown");
            t2.setText("Unknown");

        }
        tPhone1.setText(mContactList.get(iContactNum).getPhoneNumber());
        tPhone2.setText(mContactList.get(iContactNum).getPhoneNumber());
        tStatus1.setText(mContactList.get(iContactNum).getStatus());
        tStatus2.setText(mContactList.get(iContactNum).getStatus());

    }

    private void loadContacts() {

        loadFaveContacts();

        Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls._ID,
                CallLog.Calls.DATE,
                CallLog.Calls.CACHED_NUMBER_TYPE};

        String orderByDesc = CallLog.Calls.DATE + " DESC";
        Cursor people = cr.query(uri, projection, null, null, orderByDesc);


        assert people != null;

        int indexName = people.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
        int indexNumber = people.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
        int indexID = people.getColumnIndex(android.provider.CallLog.Calls._ID );
        int indexType = people.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE );

        if (people.moveToFirst()) {
            Set<String> seen = new HashSet<String>();
            do {
                String name = people.getString(indexName);
                String phoneNum = people.getString(indexNumber);
                long id = people.getLong(indexID);
                int type = people.getInt(indexType);

                if (seen.add(phoneNum)) {
                    Contact contact = new Contact();
                    contact.setName(name);
                    contact.setPhoneNumber(phoneNum);
                    contact.setID(id);
                    contact.setPhoto(getPhoto(cr, phoneNum));
                    contact.setStatus("Recent Call");
                    contact.setNumTypeInt(type);
                    mContactList.add(contact);
                }

            } while (people.moveToNext() && mContactList.size() < 40);

        }

    }

    private void loadFaveContacts() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.STARRED,
                ContactsContract.CommonDataKinds.Phone.TYPE};


        String orderByAsc = ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED + " ASC";
        Cursor starredPeople = cr.query(uri, projection, null, null, orderByAsc);


        assert starredPeople != null;

        int indexName = starredPeople.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = starredPeople.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int indexID = starredPeople.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
        int indexStarred =starredPeople.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED);
        int indexType = starredPeople.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);


        if (starredPeople.moveToFirst()) {

            do {
                String name = starredPeople.getString(indexName);
                String phoneNum = starredPeople.getString(indexNumber);
                int starred = starredPeople.getInt(indexStarred);
                long id = starredPeople.getLong(indexNumber);
                int type = starredPeople.getInt(indexType);

                if (starred == 1) {
                    Contact contact = new Contact();
                    contact.setName(name);
                    contact.setPhoneNumber(phoneNum);
                    contact.setID(id);
                    contact.setPhoto(getPhoto(cr, phoneNum));
                    contact.setStatus("Starred");
                    contact.setNumTypeInt(type);
                    mContactList.add(contact);
                    numStarred++;
                }

            } while (starredPeople.moveToNext());

        }

    }

    private String getPhoto(ContentResolver cr, String number) {
        String[] projection = new String[]{ContactsContract.PhoneLookup.PHOTO_URI};
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor c = cr.query(uri, projection, null, null, null);
        if (c.moveToFirst()) {
            String photoUri=c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));
            if(photoUri != null) {
                return photoUri;
            }
        }
        return "android.resource://com.AlinasAndroid.blindui/" + R.drawable.android_logo;
    }


    public boolean onTouch(View arg0, MotionEvent arg1) {

        // Get the action that was done on this touch event
        switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // store the X value when the user's finger was pressed down
                downXValue = arg1.getX();
                downYValue = arg1.getY();
                break;
            }

            case MotionEvent.ACTION_UP: {
                // Get the X value when the user released his/her finger
                float currentX = arg1.getX();
                float currentY = arg1.getY();
                float xDifference;
                float yDifference = currentY - downYValue;

                if ((currentX - downXValue) > (downXValue - currentX)) {
                    xDifference = (currentX - downXValue);
                } else {
                    xDifference = (downXValue - currentX);
                }

                // going backwards: pushing stuff to the right
                if (downXValue < currentX && xDifference > yDifference) {
                    if (contactNum != 0) {
                        contactNum--;
                        setContactNameText(contactNum);
                        speakContactName(mContactList.get(contactNum).getName());
                    } else {
                        break;
                    }
                    // Get a reference to the ViewFlipper
                    ViewFlipper vf = (ViewFlipper) findViewById(R.id.details);
                    // Set the animation
                    vf.setAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
                    // Flip!
                    vf.showPrevious();
                }

                // going forwards: pushing stuff to the left
                if (downXValue > currentX && xDifference > yDifference) {
                    if (contactNum < mContactList.size() - 1) {
                        contactNum++;
                        setContactNameText(contactNum);
                        speakContactName(mContactList.get(contactNum).getName());
                    } else {
                        break;
                    }
                    // Get a reference to the ViewFlipper
                    ViewFlipper vf = (ViewFlipper) findViewById(R.id.details);
                    // Set the animation
                    vf.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
                    // Flip!
                    vf.showNext();
                }

                // going down:calls contact

                if (yDifference > xDifference) {
                    Contact contact = mContactList.get(contactNum);
                    speakCalling(contact.getName());
                    call(contact.getPhoneNumber());
                }

                break;
            }
        }

        // if you return false, these actions will not be recorded
        return true;
    }

    private void speakContactName(String contactName) {

        if(contactName != null) {
            tts.speak(contactName + ", " + mContactList.get(contactNum).getNumTypeString(), TextToSpeech.QUEUE_FLUSH, null);
        } else {
            tts.speak("Unknown Caller"+ ", " + mContactList.get(contactNum).getPhoneNumberDigits(), TextToSpeech.QUEUE_FLUSH, null);

        }

    }

    private void speakCalling(String contactName) {

        tts.speak("Calling" + contactName, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakContactName(mContactList.get(contactNum).getName());
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
        }

    }


    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void call(String phone) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);
        } catch (Exception e) {
            Log.e("dialing", "Call failed", e);
        }
    }
}