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
import com.google.firebase.database.DatabaseReference;

public class ShowQuiz extends AppCompatActivity {


    TextView status;

    EditText rollNo, quizNo;
    Button nextButton;
    private Firebase mRootRef, mQuizNo;
    Boolean studentRollNo, studentQuizNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_quiz);

        studentRollNo = false;
        studentQuizNo = false;
        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz");
        status = (TextView) findViewById(R.id.status);
        rollNo = (EditText) findViewById(R.id.rollNo);
        quizNo = (EditText) findViewById(R.id.quizNo);
        nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mRollno = rollNo.getText().toString();
                final String mQuizno = quizNo.getText().toString();

                mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild("Quiz" + mQuizno)) {
                            Firebase mRollRef = mRootRef.child("Quiz" + mQuizno);
                            mRollRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.hasChild(mRollno)) {
                                        Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), ShowMarks.class);
                                        intent.putExtra("rollNo" , mRollno);
                                        intent.putExtra("quizNo", mQuizno);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else {
                                        status.setText("Incorrect Roll No");
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                        else {
                            status.setText("Incorrect Quiz No");
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
        });
    }
}

