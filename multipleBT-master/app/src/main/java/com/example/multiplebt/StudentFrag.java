package com.example.multiplebt;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import android.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class StudentFrag extends Fragment {


    final String secretK = "ssshhhhhhhhhhh!!!!";
    EditText ansText;
    TextView queView, textView, textView2;
    Button sendAns;
    BluetoothSocket socket;
    ConnectedThread mConnectedThread;
    ConnectedThread1 mConnectedThread1;
    private static int KEY_SIZE = 10;
    private ArrayList<BluetoothSocket> mSockets;
    private ArrayList<ConnectedThread1> mConnThreads;
    Activity activity;
    String rollNo;
    int i;
    int a;
    private ArrayList<SecretKeySpec> secretKeySpecs;
    private static SecretKeySpec secretKey = null;
    private static byte[] key;
    private PublicKey publicKey = null;
    //encrypted aes key and ivs combined
    private byte[] encryptedAESKey = null;

    public StudentFrag() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        socket = ((ApplicationClass) getActivity().getApplication()).getSocket();
        i = 1;
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        mSockets = new ArrayList<BluetoothSocket>();
        mSockets = ((ApplicationClass) getActivity().getApplication()).getSockets();
        Toast.makeText(getContext(), String.valueOf(mSockets.size()) + " Sockets", Toast.LENGTH_SHORT).show();
        if (mSockets.size() > 0) {
            mConnThreads = new ArrayList<ConnectedThread1>();
            connected(mSockets);
            secretKeySpecs = new ArrayList<>(mSockets.size());
        }
        secretKey = getKey(getRandomString(KEY_SIZE));
        //i = 1;
        //j=1;

    }

    public synchronized void connected(ArrayList<BluetoothSocket> mSockets) {

        for (int i = 0; i < mSockets.size(); i++) {
            BluetoothSocket socket1 = mSockets.get(i);
            secretKeySpecs.set(i, getKey(getRandomString(KEY_SIZE)));
            mConnectedThread1 = new ConnectedThread1(socket1);
            mConnectedThread1.start();
            mConnThreads.add(mConnectedThread1);
        }
    }

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student, container, false);
        ansText = (EditText) view.findViewById(R.id.ansText);
        queView = (TextView) view.findViewById(R.id.queView);
        sendAns = (Button) view.findViewById(R.id.sendAns);
        textView = (TextView) view.findViewById(R.id.textView);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        i = 1;
        a = 0;
        textView.setText("Question " + String.valueOf(i));
        activity = getActivity();

        sendAns.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String string = String.valueOf(ansText.getText());
                ansText.getText().clear();
                //Toast.makeText(getContext(),"onClick",Toast.LENGTH_LONG).show();
                string = encrypt(string);
                Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();

                mConnectedThread.write(string.getBytes());
                i++;
                textView.setText("Question " + String.valueOf(i));
            }
        });
        return view;
    }

    public static SecretKeySpec getKey(String myKey)
    {
        MessageDigest sha = null;
        SecretKeySpec secretKeySpec = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKeySpec = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return secretKeySpec;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String strToEncrypt)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),Base64.DEFAULT);
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String strToDecrypt)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public void write1(byte[] out) {
        // When writing, try to write out to all connected threads
        for (int i = 0; i < mConnThreads.size(); i++) {
            try {
                // Create temporary object
                ConnectedThread1 r;
                // Synchronize a copy of the ConnectedThread
                synchronized (this) {
                    //if (mState != STATE_CONNECTED) return;
                    r = mConnThreads.get(i);
                }
                // Perform the write unsynchronized
                r.write(out);
            } catch (Exception e) {
                Toast.makeText(getContext(), i + "exception", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            //Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {
            //Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            String s;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String x = new String(buffer, 0, bytes);
                    write1(x.getBytes());
                    if (publicKey == null){
                        byte[] publicBytes = Base64.decode(x,Base64.DEFAULT);
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                        KeyFactory keyFactory = null;
                        try {
                            keyFactory = KeyFactory.getInstance("RSA");
                            PublicKey pubKey = keyFactory.generatePublic(keySpec);
                            publicKey = pubKey;
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                        encryptedAESKey = (byte[]) RSAEncryptDecrypt.encryptRSA(secretKey.getEncoded(), publicKey);
                        String string = android.util.Base64.encodeToString(encryptedAESKey, android.util.Base64.DEFAULT);
                        write(string.getBytes());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"sent AES key",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        final String a = decrypt(x);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queView.setText(a);
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    //Log.e(TAG, "disconnected", e);
                    //connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                //mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                //      .sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread1 extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread1(BluetoothSocket socket) {
            //Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            //Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            String s;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    s = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity
                    final String finalS = s;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConnectedThread.write(finalS.getBytes());
                            //Firebase AnsRef = teachRef.child("Answers");
                            //Firebase b = AnsRef.child("Answer" + String.valueOf(j));
                            //j++;
                            //b.setValue(finalS);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    //Log.e(TAG, "disconnected", e);
                    //connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                //mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                //      .sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
