package com.example.multiplebt;

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
import android.util.Base64;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class TeacherFrag extends Fragment {

    ApplicationClass app;
    EditText aquizNo, queText;
    TextView ansView, textView, textView2;
    Button sendQue, endQuiz, nextButton;
    HashMap<String, Integer> hashMap;
    BluetoothSocket socket;
    String rollNo, quizNo;
    private Firebase mRootRef;
    private Firebase queRef;
    private Firebase mTeacherRef;
    private Firebase teachRef;
    int i, j;
    Boolean flag;

    private ConnectedThread mConnectedThread;
    private ArrayList<BluetoothSocket> mSockets;
    private ArrayList<ConnectedThread> mConnThreads;

    //RSA key pair (public and private)
    private KeyPair rsaKey = null;
    private static SecretKeySpec secretKey = null;
    //encrypted aes key and ivs combined
    private byte[] encryptedAESKey = null;

    public TeacherFrag() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSockets = new ArrayList<BluetoothSocket>();
        mSockets = ((ApplicationClass) getActivity().getApplication()).getSockets();
        Toast.makeText(getContext(), String.valueOf(mSockets.size()) + " Sockets", Toast.LENGTH_SHORT).show();
        mConnThreads = new ArrayList<ConnectedThread>();
        connected(mSockets);
        //i = 1;
        //j=1;

    }

    public synchronized void connected(ArrayList<BluetoothSocket> mSockets) {

        for (int i = 0; i < mSockets.size(); i++) {
            BluetoothSocket socket1 = mSockets.get(i);
            mConnectedThread = new ConnectedThread(socket1);
            mConnectedThread.start();
            mConnThreads.add(mConnectedThread);
        }
        this.rsaKey = RSAEncryptDecrypt.generateRSAKey();
        sendPublicKey(rsaKey.getPublic());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);
        queText = (EditText) view.findViewById(R.id.queText);
        ansView = (TextView) view.findViewById(R.id.ansView);
        sendQue = (Button) view.findViewById(R.id.sendQue);
        endQuiz = (Button) view.findViewById(R.id.endQuiz);
        textView = (TextView) view.findViewById(R.id.textView);
        //aquizNo = (EditText) view.findViewById(R.id.quizNo);
        //nextButton = (Button) view.findViewById(R.id.nextButton);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        flag = false;
        //mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz");
        hashMap = new HashMap<String, Integer>();

        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz");

        sendQue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] ques = new String[1];

                queRef.child("Question" + String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        ques[0] = value;
                        Toast.makeText(getContext(), ques[0],Toast.LENGTH_SHORT).show();
                        queText.setText(ques[0]);
                        write(ques[0].getBytes());
                        textView.setText("Question " + String.valueOf(i));
                        i++;
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });

        endQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = "end quiz";
                write(string.getBytes());
                Firebase marksRef = mTeacherRef.child("no");
                marksRef.setValue(i-1);
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public void write(byte[] out) {
        // When writing, try to write out to all connected threads
        for (int i = 0; i < mConnThreads.size(); i++) {
            try {
                // Create temporary object
                ConnectedThread r;
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

    private void sendPublicKey(PublicKey aPublic) {
        String publicK = Base64.encodeToString(aPublic.getEncoded(), Base64.DEFAULT);
        Toast.makeText(getContext(),"sent key",Toast.LENGTH_SHORT).show();
        write(publicK.getBytes());
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
            return new String(cipher.doFinal(Base64.decode(strToDecrypt,Base64.DEFAULT)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        Boolean flag1;

        public ConnectedThread(BluetoothSocket socket) {
            //Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            flag1 = false;
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
                    if (flag1.equals(false)) {
                        flag1 = true;
                        encryptedAESKey = Base64.decode(finalS, Base64.DEFAULT);
                        byte[] key1 = (RSAEncryptDecrypt.decryptRSA(encryptedAESKey, rsaKey.getPrivate()));
                        SecretKeySpec originalKey = new SecretKeySpec(key1, 0, key1.length, "AES");
                        secretKey = originalKey;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"Connection secure!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        getActivity().runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                String a = decrypt(finalS);
                                ansView.setText(a);
                                teachRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz/" + "Quiz" + quizNo + "/" + rollNo);
                                Firebase AnsRef = teachRef.child("Answers");
                                int j = hashMap.get(rollNo);
                                Firebase b = AnsRef.child("Answer" + String.valueOf(j));
                                b.setValue(a);
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
