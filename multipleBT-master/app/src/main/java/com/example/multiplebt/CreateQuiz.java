package com.example.multiplebt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;


public class CreateQuiz extends AppCompatActivity {

    Button next, endQuiz;
    TextView qtext;
    EditText queText, quiz;
    Boolean flag;
    String quizno;
    int i;

    private Firebase mRootRef, childRef, quizRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        flag = false;
        i = 1;
        next = (Button) findViewById(R.id.nextQue);
        endQuiz = (Button) findViewById(R.id.submitQuiz);
        qtext = (TextView) findViewById(R.id.qtext);
        queText = (EditText) findViewById(R.id.queText);
        quiz = (EditText) findViewById(R.id.quiz);

        endQuiz.setVisibility(View.INVISIBLE);
        queText.setVisibility(View.INVISIBLE);
        qtext.setVisibility(View.INVISIBLE);

        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag.equals(false)){
                    flag = true;
                    quizno = quiz.getText().toString();
                    quizRef = mRootRef.child("Quiz"+quizno);
                    childRef = quizRef.child("Questions");
                    quiz.setVisibility(View.GONE);
                    endQuiz.setVisibility(View.VISIBLE);
                    queText.setVisibility(View.VISIBLE);
                    qtext.setText("Question" + String.valueOf(i));
                    qtext.setVisibility(View.VISIBLE);
                }

                else {
                    String string = queText.getText().toString();
                    queText.getText().clear();
                    Firebase Ref = childRef.child("Question" + String.valueOf(i));
                    Ref.setValue(string);
                    i++;
                    qtext.setText("Question" + String.valueOf(i));
                }
            }
        });

        endQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Quiz Successfully created",Toast.LENGTH_LONG).show();
                quizRef = quizRef.child("no");
                quizRef.setValue(String.valueOf(i-1));
                onBackPressed();
            }
        });
    }
}
