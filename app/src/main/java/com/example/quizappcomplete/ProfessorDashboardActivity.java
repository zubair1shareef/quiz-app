package com.example.quizappcomplete;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizappcomplete.DataAdapters.QuizListAdapter;
import com.example.quizappcomplete.Model.QuizInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ProfessorDashboardActivity extends AppCompatActivity {

    private static final String TAG = "ProfessorDashboard" ;
    private static final String MY_PREFS_NAME = "CurrentUser";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;

    FloatingActionButton fabNewQuiz;

    RecyclerView mRecyclerView;
    ArrayList<QuizInfo> mQuizInfoArrayList;
    ArrayList<String> mQuizIdList;
    QuizListAdapter mAdapter;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    TextView tvName, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_professor_dashboard);

        getSupportActionBar ().setTitle ("Your Quizzes");
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);

        mAuth = FirebaseAuth.getInstance ();
        mDatabase = FirebaseDatabase.getInstance ();

        mDrawerLayout = findViewById(R.id.activity_professor_dashboard);
        mActionBarDrawerToggle = new ActionBarDrawerToggle (this, mDrawerLayout,R.string.Open, R.string.Close);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mNavigationView = findViewById(R.id.navpane_prof);
        tvEmail = mNavigationView.getHeaderView (0).findViewById (R.id.navEmail);
        tvName = mNavigationView.getHeaderView (0).findViewById (R.id.navName);

        tvName.setText (mAuth.getCurrentUser ().getDisplayName ());
        tvEmail.setText (mAuth.getCurrentUser ().getEmail ());

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.nav_prof_quiz:
                        Toast.makeText(ProfessorDashboardActivity.this, "Your Quiz",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_prof_signout:{
                        mAuth.signOut ();
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.clear ();
                        editor.commit();
                        Intent intent = new Intent (ProfessorDashboardActivity.this, WelcomeActivity.class);
                        finish ();
                        startActivity (intent);
                    }
                    default:
                        return true;
                }
                return true;
            }
        });

        fabNewQuiz = findViewById (R.id.fab_new_quiz);
        fabNewQuiz.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ProfessorDashboardActivity.this, ProfessorNewQuizActivity.class);
                startActivity (intent);
            }
        });

        mQuizIdList = new ArrayList<> ();
        mQuizInfoArrayList = new ArrayList<> ();

        mRecyclerView = findViewById (R.id.prof_quiz_list_recycler);
        mRecyclerView.setLayoutManager (new LinearLayoutManager (this));
        mAdapter = new QuizListAdapter (mQuizInfoArrayList, mQuizIdList, this, ProfessorViewQuizActivity.class);
        mRecyclerView.setAdapter (mAdapter);

        mReference = mDatabase.getReference ("Quiz");
        Query query = mReference.orderByChild ("createdByUid").equalTo (mAuth.getUid ());


        query.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d (TAG, "onDataChange: Data = "+dataSnapshot.toString ());
                if(dataSnapshot.exists ())
                {
                    for (DataSnapshot data: dataSnapshot.getChildren ())
                    {
                        Log.d (TAG, "onDataChange: "+data);
                        QuizInfo quiz = data.getValue (QuizInfo.class);
                        String id = data.getKey ();

                        if(!mQuizIdList.contains (id)) {
                            mQuizInfoArrayList.add (quiz);
                            mQuizIdList.add (id);
                            mAdapter.notifyDataSetChanged ();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText (ProfessorDashboardActivity.this, "Database Error", Toast.LENGTH_SHORT).show ();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mActionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}
