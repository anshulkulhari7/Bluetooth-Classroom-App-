package com.example.multiplebt;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class Register extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton teacher, student;
    Button nextButton;
    EditText id, password, rollNo;
    Boolean flag;
    private Firebase mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        flag = false;
        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Classroom/Users");
        final Firebase mTeacherRef = mRootRef.child("Teacher");
        final Firebase mStudentRef = mRootRef.child("Students");

        teacher = (RadioButton) findViewById(R.id.radioTeacher);
        student = (RadioButton) findViewById(R.id.radioStudent);
        nextButton = (Button) findViewById(R.id.nextButton);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        rollNo = (EditText) findViewById(R.id.rollNo);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();

        nextButton.setVisibility(View.INVISIBLE);
        id.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        rollNo.setVisibility(View.INVISIBLE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if ((checkedId == R.id.radioTeacher)) {
                    rollNo.setVisibility(View.GONE);
                    flag = true;
                } else {
                    rollNo.setVisibility(View.VISIBLE);
                    flag = false;
                }

                nextButton.setVisibility(View.VISIBLE);
                id.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sid = id.getText().toString();
                String spass = password.getText().toString();

                if (flag.equals(true)){
                    Firebase idRef = mTeacherRef.child("id");
                    Firebase passRef = mTeacherRef.child("password");

                    idRef.setValue(sid);
                    passRef.setValue(spass);
                }
                else {
                    String sroll = rollNo.getText().toString();

                    Firebase rollRef = mStudentRef.child(sroll);
                    Firebase idRef = rollRef.child("id");
                    Firebase passRef = rollRef.child("password");

                    idRef.setValue(sid);
                    passRef.setValue(spass);
                }
                Toast.makeText(getApplicationContext(), "Sign up successful",Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }
}

