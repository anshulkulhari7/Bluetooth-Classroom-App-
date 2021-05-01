package com.example.multiplebt;

import android.app.Application;
import android.bluetooth.BluetoothSocket;



import java.util.ArrayList;


public class ApplicationClass extends Application {

    public static String TAG = "dev";
    public BluetoothSocket socket;
    public String rollNo;
    public MainActivity mainActivity;
    public int count;
    String quizNo;
    public ArrayList<BluetoothSocket> mSockets = new ArrayList<BluetoothSocket>();
    private static ApplicationClass app;


    public ApplicationClass getApp(){
        return app;
    }

    public void setSockets(BluetoothSocket socket1){
        mSockets.add(socket1);
    }

    public ArrayList<BluetoothSocket> getSockets(){
        return mSockets;
    }

    public void setSocket(BluetoothSocket socket1){
        this.socket = socket1;
    }

    public BluetoothSocket getSocket(){
        return socket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Firebase.setAndroidContext(this);
    }

    public void setQuizNo(String string){
        this.quizNo = string;
    }

    public String getQuizNo(){
        return quizNo;
    }

    public void setCount(int a){
        this.count = a;
    }
    public int getCount(){
        return count;
    }

    public void setRollNo(String s1){
        this.rollNo = s1;
        //Toast.makeText(getApplicationContext(),"setS "+ s,Toast.LENGTH_LONG).show();
    }

    public String getRollNo(){
        ///Toast.makeText(getApplicationContext(),"getS "+s,Toast.LENGTH_LONG).show();
        return rollNo;
    }


}
