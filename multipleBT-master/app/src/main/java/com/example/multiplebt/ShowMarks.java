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

public class ShowMarks extends AppCompatActivity {

    String rollNo;
    TextView queView, ansView, textView, textView2, totalMarks, marks;
    Button nextButton;
    String quizNo;
    int i, j, total_marks;
    private Firebase mTeacherRef, mStudentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_marks);

        final Intent intent = getIntent();
        rollNo = intent.getStringExtra("rollNo");
        quizNo = intent.getStringExtra("quizNo");


        queView = (TextView) findViewById(R.id.queView);
        ansView = (TextView) findViewById(R.id.ansView);
        marks = (TextView) findViewById(R.id.marks);
        nextButton = (Button) findViewById(R.id.nextButton);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        totalMarks = (TextView) findViewById(R.id.totalMarks);

        totalMarks.setVisibility(View.INVISIBLE);
        i = 1;
        total_marks = 0;
        mTeacherRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz/" + "Quiz" + quizNo);
        mStudentRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz/" + "Quiz" + quizNo + "/" + rollNo);


        final Firebase queRef = mTeacherRef.child("Questions");
        final Firebase ansRef = mStudentRef.child("Answers");
        final Firebase marksRef = mStudentRef.child("Marks");


        mTeacherRef.child("no").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(Integer.class);
                j = value;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        queRef.child("Question" + String.valueOf(i)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                queView.setText(value);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


        ansRef.child("Answer" + String.valueOf(i)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                ansView.setText(value);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        marksRef.child("Marks" + String.valueOf(i)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                marks.setText(value);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i <= j) {
                    String s = marks.getText().toString();
                    total_marks += Integer.valueOf(s);
                    i++;

                    queRef.child("Question" + String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                            queView.setText(value);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                    ansRef.child("Answer" + String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                            ansView.setText(value);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                    marksRef.child("Marks" + String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                            marks.setText(value);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    if (i > j) {
                        marks.setVisibility(View.GONE);
                        nextButton.setText("Submit");
                        queView.setVisibility(View.GONE);
                        ansView.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        textView2.setVisibility(View.GONE);
                        marks.setVisibility(View.GONE);
                        totalMarks.setVisibility(View.VISIBLE);
                        totalMarks.setText(String.valueOf(total_marks));
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

        });

    }
}

