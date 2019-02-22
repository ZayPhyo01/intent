package com.example.implicitpower;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 12;
    Button btnTimer, btnCalender, btnVideo, btnContact, btnBrowser;
    public static final int VIDEO_CAPTURE = 3;
    public static final int SEARCH_QUERY = 5;
    public static final int GET_CONTENT = 4;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnVideo = findViewById(R.id.btn_video);
        btnContact = findViewById(R.id.btn_contact);
        btnBrowser = findViewById(R.id.btn_browser);
        btnCalender = findViewById(R.id.btn_calender);
        btnTimer = findViewById(R.id.btn_timer);

        btnTimer.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnCalender.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnBrowser.setOnClickListener(this);

        videoView = findViewById(R.id.vv_video);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_video:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);
                }

                break;
            case R.id.btn_browser:
                Intent searchQuery = new Intent(Intent.ACTION_WEB_SEARCH);
                if (searchQuery.resolveActivity(getPackageManager()) != null) {
                    searchQuery.setData(Uri.parse("http://www.youtube.com"));
                    startActivity(searchQuery);
                }
                break;
            case R.id.btn_calender:
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                intent.putExtra("title", " My Calender ");
                startActivity(intent);
                break;
            case R.id.btn_contact:
                if (ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this ,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                }

                break;

            case R.id.btn_timer:
                Intent intent2 = new Intent(AlarmClock.ACTION_SET_ALARM)
                        .putExtra(AlarmClock.EXTRA_HOUR, 12)
                        .putExtra(AlarmClock.EXTRA_MINUTES, 5);
                if (intent2.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent2);
                }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {

            Toast.makeText(this, "Video", Toast.LENGTH_LONG).show();
            videoView.setVideoURI(data.getData());
            videoView.start();

        } else if (requestCode == GET_CONTENT && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};


Cursor cursor = getContentResolver().query(data.getData(),
        null,
        null,
        null,null);
         if (cursor != null ) {

             cursor.moveToFirst();

             String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
             String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

             String myContact = "Name : " + name + " \n" + "Number : " + id+" Phone num :";
             Toast.makeText(this, myContact, Toast.LENGTH_LONG).show();
         }

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            Intent intent1 = new Intent(Intent.ACTION_PICK);
            intent1.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(intent1, GET_CONTENT);
        }
    }
}
