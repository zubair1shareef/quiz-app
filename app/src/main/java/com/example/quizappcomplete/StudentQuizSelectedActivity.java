package com.example.quizappcomplete;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizappcomplete.Model.QuizInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class StudentQuizSelectedActivity extends AppCompatActivity {

    QuizInfo mQuizInfo;
    String mQuizId;
    TextView Date,Time,Marksperquestion,Totalmaerks,Timealloted,Createdby,Semister,Branch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_student_quiz_selected);
        Date=(TextView) findViewById(R.id.tvDate_Student);
        Time=(TextView) findViewById(R.id.tvTime_Student);
        Marksperquestion=(TextView) findViewById(R.id.tvMarksPerQues_Student);
        Totalmaerks=(TextView) findViewById(R.id.tvTotalMarks_Student);
        Timealloted=(TextView) findViewById(R.id.tvTimeAllowed_Student);
        Createdby=(TextView) findViewById(R.id.tvCreatedBy_Student);
        Semister=(TextView) findViewById(R.id.tvSemester_Student);
        Branch=(TextView) findViewById(R.id.tvBranch_Student);

        Intent intent = getIntent ();
        mQuizInfo = (QuizInfo) intent.getSerializableExtra ("quizInfo");
        mQuizId = intent.getStringExtra ("quizId");

        getSupportActionBar ().setTitle (mQuizInfo.getTitle ());
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
        update();


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    public void update()
    {
        Date.setText(mQuizInfo.getDate());
        Time.setText(mQuizInfo.getTime());
        Timealloted.setText(mQuizInfo.getDuration());
        Totalmaerks.setText(mQuizInfo.getTotalMarks());
        Timealloted.setText(mQuizInfo.getDuration ());
        Createdby.setText(mQuizInfo.getCreatedBy());

       // Subject.setText(mQuizInfo.se);
        Semister.setText(mQuizInfo.getSemester());
        Branch.setText(mQuizInfo.getBranch());
        Marksperquestion.setText(mQuizInfo.getMarksperquestion());

    }
    public void startquiz(View v)
    {    final String uid;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();


        java.util.Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        String todayString = formatter.format(todayDate);
        String ddd=mQuizInfo.getDate();
        String s = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        //  Toast.makeText(getApplicationContext(), todayString,Toast.LENGTH_SHORT).show();

        if(todayString.compareTo(mQuizInfo.getDate())>0)
        {
           if(s.compareTo(mQuizInfo.getTime())>0)
             {
                 DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                 rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot snapshot) {
                         if (snapshot.child("Results").child(mQuizId).hasChild(uid)) {



                             Toast.makeText(getApplicationContext(),"Already Attempted",Toast.LENGTH_SHORT).show();
                         }
                         else
                         {
                             Intent indent = new Intent(StudentQuizSelectedActivity.this, Quiz.class);
                             indent.putExtra("quizid", String.valueOf(mQuizId));
                             indent.putExtra("quizInfo", mQuizInfo);
                             finish();
                             startActivity(indent);

                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });

             }
           else if (s.compareTo(mQuizInfo.getTime())<0)
           {
               Toast.makeText(getApplicationContext(), "quiz will start at"+mQuizInfo.getTime(),Toast.LENGTH_SHORT).show();
           }

        }
        else   if(todayString.equals(mQuizInfo.getDate()))
        {

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.child("Results").child(mQuizId).hasChild(uid)) {



                        Toast.makeText(getApplicationContext(),"Already Attempted",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent indent = new Intent(StudentQuizSelectedActivity.this, Quiz.class);
                        indent.putExtra("quizid", String.valueOf(mQuizId));
                        indent.putExtra("quizInfo", mQuizInfo);
                        finish();
                        startActivity(indent);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        else if(todayString.compareTo(mQuizInfo.getDate())<0)
        {
            Toast.makeText(getApplicationContext(), "quiz will be available from "+mQuizInfo.getDate(),Toast.LENGTH_SHORT).show();
        }

    }

}
