package com.example.multiplebt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class listenClass extends AppCompatActivity {

    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChatMulti";
    int REQUEST_ENABLE_BLUETOOTH = 1;
    // Member fields
    private BluetoothAdapter mAdapter;
    private AcceptThread mAcceptThread;
    TextView status1;
    EditText id, password;
    Button nextButton;
    Boolean teacherid, teacherpassword;
    private Firebase mRootRef;

    private String mConnectedDeviceName = null;

    public static final int MESSAGE_DEVICE_NAME = 4;

    private ArrayList<UUID> mUuids;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    TextView status;
    Button listenButton, endButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_class);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mUuids = new ArrayList<UUID>();
        // 7 randomly-generated UUIDs. These must match on both server and client.
        mUuids.add(UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
        mUuids.add(UUID.fromString("2d64189d-5a2c-4511-a074-77f199fd0834"));
        mUuids.add(UUID.fromString("e442e09a-51f3-4a7b-91cb-f638491d1412"));
        mUuids.add(UUID.fromString("a81d6504-4536-49ee-a475-7d96d09439e4"));
        mUuids.add(UUID.fromString("aa91eab1-d8ad-448e-abdb-95ebba4a9b55"));
        mUuids.add(UUID.fromString("4d34da73-d0a4-4f40-ac38-917e0a9dee97"));
        mUuids.add(UUID.fromString("5e14d4df-9c8a-4db7-81e4-c937564c86e0"));

        status1 = findViewById(R.id.status1);
        listenButton = findViewById(R.id.listenButton);
        endButton = findViewById(R.id.endButton);
        teacherid = false;
        teacherpassword = false;
        //Log.i(TAG, "oncreateFRag");
        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Classroom/Users/Teacher");
        //Firebase maRef = mRootRef.child("yo");
        //maRef.setValue("2");

        status = (TextView) findViewById(R.id.status);

        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        nextButton = (Button) findViewById(R.id.nextButton);

        mAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        final String[] tID = new String[1];
        final String[] tPass = new String[1];

        mRootRef.child("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i(TAG,"ondatachange");
                String value = dataSnapshot.getValue(String.class);
                //Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
                tID[0] = value;
                //Toast.makeText(getApplicationContext(),tID[0],Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //Log.i(TAG, "cancelled");
            }
        });

        mRootRef.child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tPass[0] = value;
                //Toast.makeText(getApplicationContext(),tPass[0],Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teacherid.equals(false) || teacherpassword.equals(false)) {
                    final String mID = id.getText().toString();
                    final String mPass = password.getText().toString();

                    String a = mID;
                    String b = tID[0];
                    String c = mPass;
                    String d = tPass[0];

                    if (a.equals(b)) {
                        teacherid = true;
                        if (c.equals(d)) {
                            teacherpassword = true;
                            id.setVisibility(View.GONE);
                            password.setVisibility(View.GONE);
                            //status.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                            AcceptThread acceptThread = new AcceptThread();
                            acceptThread.start();
                            mAcceptThread = acceptThread;
                            Toast.makeText(getApplicationContext(), "Listening", Toast.LENGTH_SHORT).show();
                            nextButton.setVisibility(View.GONE);
                            id.setVisibility(View.GONE);
                            password.setVisibility(View.GONE);
                            status.setVisibility(View.GONE);
                        } else {
                            status.setText("Incorrect Password");
                        }
                    } else {
                        status.setText("Incorrect ID");
                    }
                }
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptThread acceptThread = new AcceptThread();
                acceptThread.start();
                mAcceptThread = acceptThread;
                Toast.makeText(getApplicationContext(), "Listening", Toast.LENGTH_SHORT).show();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAcceptThread.cancel();

                status.setVisibility(View.GONE);
                listenButton.setVisibility(View.GONE);
                endButton.setVisibility(View.GONE);

                FragmentManager fm = getSupportFragmentManager();
                TeacherFrag fragment = new TeacherFrag();
                fm.beginTransaction().replace(R.id.teacherFrag, fragment).commit();
            }
        });
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private class AcceptThread extends Thread {
        BluetoothServerSocket serverSocket = null;

        public AcceptThread() {
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;
            try {
                // Listen for all 7 UUIDs
                for (int i = 0; i < mUuids.size(); i++) {
                    serverSocket = mAdapter.listenUsingRfcommWithServiceRecord(NAME, mUuids.get(i));
                    socket = serverSocket.accept();
                    if (socket != null) {
                        connected(socket, socket.getRemoteDevice(), i);
                        serverSocket.close();
                    }
                }
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, int i) {
        if (D) Log.d(TAG, "connected");

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName() + "  " + String.valueOf(i));
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        ((ApplicationClass) getApplication()).setSockets(socket);
    }

}
