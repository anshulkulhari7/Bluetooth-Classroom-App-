package com.example.multiplebt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class createClass extends AppCompatActivity {


    Button createButton, listen;
    ListView listView;
    TextView status;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;

    int flag, flag1, muuid;

    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    private ConnectThread mConnectThread;
    private AcceptThread mAcceptThread;
    private int mState;
    private String mConnectedDeviceName = null;

    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    private static final String NAME = "BluetoothChatMulti";
    public static final String TOAST = "toast";

    private ArrayList<UUID> mUuids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        createButton = (Button) findViewById(R.id.createButton);
        listView = (ListView) findViewById(R.id.listView);
        status = (TextView) findViewById(R.id.status);
        listen = (Button) findViewById(R.id.listen);

        listen.setVisibility(View.INVISIBLE);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mUuids = new ArrayList<UUID>();
        // 7 randomly-generated UUIDs. These must match on both server and client.
        mUuids.add(UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
        mUuids.add(UUID.fromString("2d64189d-5a2c-4511-a074-77f199fd0834"));
        mUuids.add(UUID.fromString("e442e09a-51f3-4a7b-91cb-f638491d1412"));
        mUuids.add(UUID.fromString("a81d6504-4536-49ee-a475-7d96d09439e4"));
        mUuids.add(UUID.fromString("aa91eab1-d8ad-448e-abdb-95ebba4a9b55"));
        mUuids.add(UUID.fromString("4d34da73-d0a4-4f40-ac38-917e0a9dee97"));
        mUuids.add(UUID.fromString("5e14d4df-9c8a-4db7-81e4-c937564c86e0"));
        //j=0;
        flag = 1;
        flag1 = 1;

        boolean isEnabled = Settings.System.getInt(
                (getApplicationContext()).getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        Settings.System.putInt(
                (getApplicationContext()).getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);

// Post an intent to reload
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", !isEnabled);
        sendBroadcast(intent);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SERVICE_STATE");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("AirplaneMode", "Service state changed");
            }
        };

        (getApplicationContext()).registerReceiver(receiver, intentFilter);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //listDevices.s
                // etVisibility(View.GONE);
                if (flag == 1) {
                    Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                    String[] strings = new String[bt.size()];
                    btArray = new BluetoothDevice[bt.size()];
                    int index = 0;

                    if (bt.size() > 0) {
                        for (BluetoothDevice device : bt) {
                            btArray[index] = device;
                            strings[index] = device.getName();
                            index++;
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                        listView.setAdapter(arrayAdapter);
                    }
                    flag = 2;

                } else {
                    listen.setVisibility(View.GONE);
                    createButton.setVisibility(View.GONE);
                    FragmentManager fm = getSupportFragmentManager();
                    StudentFrag fragment = new StudentFrag();
                    fm.beginTransaction().replace(R.id.studentFrag, fragment).commit();
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag1 == 1) {
                    mUuids.remove(muuid);
                    AcceptThread acceptThread = new AcceptThread();
                    acceptThread.start();
                    mAcceptThread = acceptThread;
                    Toast.makeText(getApplicationContext(), "Listening", Toast.LENGTH_SHORT).show();
                    flag1 = 2;
                }
                else {
                    mAcceptThread.cancel();
                    listen.setVisibility(View.GONE);
                    createButton.setVisibility(View.GONE);
                    FragmentManager fm = getSupportFragmentManager();
                    StudentFrag fragment = new StudentFrag();
                    fm.beginTransaction().replace(R.id.studentFrag, fragment).commit();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //status.setVisibility(View.GONE);
                //listView.setVisibility(View.GONE);
                connect(btArray[i]);
                status.setText("Connecting");
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
                case MESSAGE_TOAST:
                    //if (!msg.getData().getString(TOAST).contains("Unable to connect device")) {
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    //}
                    break;
            }
        }
    };

    public synchronized void connect(BluetoothDevice device) {


        // Create a new thread and attempt to connect to each UUID one-by-one.
        for (int i = 0; i < mUuids.size(); i++) {
            try {
                mConnectThread = new ConnectThread(device, mUuids.get(i), i);
                mConnectThread.start();
                //setState(STATE_CONNECTING);
            } catch (Exception e) {
            }
        }
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final int q) {

        ((ApplicationClass) getApplication()).setSocket(socket);
        // Start the thread to manage the connection and perform transmissions

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName() + "   " + String.valueOf(q));
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        muuid = q;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                createButton.setText("Go to Chat");
                listen.setVisibility(View.VISIBLE);
            }
        });

        //setState(STATE_CONNECTED);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private UUID tempUuid;
        int i, x;

        public ConnectThread(BluetoothDevice device, UUID uuidToTry, int a) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            tempUuid = uuidToTry;
            i = 0;
            x = a;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(uuidToTry);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
                // Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                if (tempUuid.toString().contentEquals(mUuids.get(6).toString())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Cant connect to device", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
                return;
            }
            synchronized (createClass.this) {
                mConnectThread = null;

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), String.valueOf(x), Toast.LENGTH_SHORT).show();
                }
            });
            // Start the connected thread
            connected(mmSocket, mmDevice, x);
            // Reset the ConnectThread because we're done
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

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
                    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, mUuids.get(i));
                    socket = serverSocket.accept();
                    if (socket != null) {
                        connected1(socket, socket.getRemoteDevice(), i);
                        serverSocket.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listen.setText("End Listen");
                            }
                        });
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

    public synchronized void connected1(BluetoothSocket socket, BluetoothDevice device, int i) {
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
