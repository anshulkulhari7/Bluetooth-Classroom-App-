package com.example.multiplebt;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class CheckQuiz extends AppCompatActivity {

    EditText id, password;
    Button nextButton;
    private Firebase mRootRef;
    Boolean teacherid, teacherpassword;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_quiz);

        status = (TextView) findViewById(R.id.status);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        nextButton = (Button) findViewById(R.id.nextButton);

        teacherid = false;
        teacherpassword = false;
        //Log.i(TAG, "oncreateFRag");
        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Classroom/Users/Teacher");
        final String[] tID = new String[1];
        final String[] tPass = new String[1];


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teacherpassword == false && teacherid == false) {
                    final String mID = id.getText().toString();
                    final String mPass = password.getText().toString();

                    mRootRef.child("id").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Log.i(TAG,"ondatachange");
                            String value = dataSnapshot.getValue(String.class);
                            //Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
                            tID[0] = value;
                            Toast.makeText(getApplicationContext(), tID[0], Toast.LENGTH_LONG).show();
                            if (mID.equals(tID[0])){
                                mRootRef.child("password").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String value = dataSnapshot.getValue(String.class);
                                        tPass[0] = value;

                                        if (mPass.equals(tPass[0])){
                                            Toast.makeText(getApplicationContext(), "Login Succesful", Toast.LENGTH_LONG).show();
                                            id.getText().clear();
                                            password.getText().clear();
                                            teacherpassword = true;
                                            id.setHint("Enter Roll no. of student");
                                            password.setHint("Enter Quiz no.");
                                        }
                                        else {
                                            status.setText("Incorrect Password");
                                        }
                                        //Toast.makeText(getApplicationContext(),tPass[0],Toast.LENGTH_LONG).show();
                                    }
                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });
                            }
                            else {
                                status.setText("Incorrect ID");
                            }
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            //Log.i(TAG, "cancelled");
                        }
                    });
                }
                else {
                    String rollNo = id.getText().toString();
                    String quizNo = password.getText().toString();

                    Intent intent = new Intent(getApplicationContext(), Marks.class);
                    intent.putExtra("rollNo", rollNo);
                    intent.putExtra("quizNo", quizNo);
                    startActivity(intent);
                }
            }
        });

    }

}
