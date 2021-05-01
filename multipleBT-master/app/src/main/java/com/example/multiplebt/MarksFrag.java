package com.example.multiplebt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseError;


public class MarksFrag extends Fragment {

    TextView queView, ansView, textView, textView2, totalMarks;
    EditText marks;
    Button nextButton;
    String rollNo;
    String quizNo;
    int i, j, total_marks;
    private Firebase mTeacherRef, mStudentRef;


    public MarksFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marks, container, false);
        queView = (TextView) view.findViewById(R.id.queView);
        ansView = (TextView) view.findViewById(R.id.ansView);
        marks = (EditText) view.findViewById(R.id.marks);
        nextButton = (Button) view.findViewById(R.id.nextButton);
        textView = (TextView) view.findViewById(R.id.textView);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        totalMarks = (TextView) view.findViewById(R.id.totalMarks);

        totalMarks.setVisibility(View.INVISIBLE);
        i = 1;
        total_marks = 0;
        j = ((ApplicationClass)getActivity().getApplication()).getCount();
        rollNo = ((ApplicationClass)getActivity().getApplication()).getRollNo();
        quizNo = ((ApplicationClass)getActivity().getApplication()).getQuizNo();
        mTeacherRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz/"+"Quiz"+quizNo);
        mStudentRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz/"+"Quiz"+quizNo+"/"+rollNo);

        if(rollNo == null){
            Toast.makeText(getContext(),"rollNo null",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getContext(),rollNo,Toast.LENGTH_LONG).show();
        }

        final Firebase queRef = mTeacherRef.child("Questions");
        final Firebase ansRef = mStudentRef.child("Answers");
        final Firebase marksRef = mStudentRef.child("Marks");
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

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i<=j) {
                    String s = marks.getText().toString();
                    total_marks += Integer.valueOf(s);
                    (marksRef.child("Marks" + String.valueOf(i))).setValue(s);
                    i++;
                    marks.getText().clear();

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

                    if (i > j) {
                        marks.setVisibility(View.GONE);
                        nextButton.setText("Submit");
                        nextButton.setVisibility(View.GONE);
                        queView.setVisibility(View.GONE);
                        ansView.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        textView2.setVisibility(View.GONE);
                        marks.setVisibility(View.GONE);
                        totalMarks.setVisibility(View.VISIBLE);
                        totalMarks.setText(String.valueOf(total_marks));
                    }
                }
                else {
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

}
