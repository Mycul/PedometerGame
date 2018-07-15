package com.example.michael.pedometertest;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    //firebase stuff
    //UserInfo
    public static final int RC_SIGN_IN = 1;
    String mUsername;
    String mUserID;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference databaseUsers;
    DatabaseReference userData;

    public int maxSetting = 10;
    private int numSteps = 0;
    private int currentLevel = 1;
    private int currentLevelProgress = 0;

    private int currentFunds = 0;

    public UserModel mUser = new UserModel(mUserID, mUsername, 0, currentLevel, currentLevelProgress, 10 ,0);


    private TextView TvSteps;
    private TextView currentProgressTV;
    private TextView currentLevelTV;
    private TextView totalForLevelTV;
    private TextView currencyTV;
    private Button BtnStart;
    private Button BtnStop;
    private Button BtnReset;
    private Button BtnLeaderBoard;
    private ProgressBar levelProgressBar;



    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String KEY_NUM_STEPS = "numSteps";
    private static final String KEY_CURRENT_LEVEL = "currentLevel";
    private static final String KEY_CURRENT_PROGRESS = "currentLevelProgress";
    private static final String KEY_MULT = "multiplier";
    private static final String KEY_MAX = "max";
    private static final String TEXT_NUM_STEPS = "Total Number of Steps: ";
    private static final String TEXT_CURRENT_STEPS = "Current Steps: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //firebase var instantiation
        mFirebaseAuth = FirebaseAuth.getInstance();
        //get a reference of the User node;
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");

        //Authentication onCreate
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user is signed in
                    onSignedInInitialize(user.getDisplayName());
                    mUserID = user.getUid();
                    mUser.setUserID(mUserID);
                    mUser.setUserName(mUsername);
                    addUser();

                }else{
                    //user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()
                                    ))
                                    .build(), RC_SIGN_IN
                    );
                }
            }
        };

        //get an instance of the sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) findViewById(R.id.tv_steps);
        totalForLevelTV = (TextView)findViewById(R.id.totalForLevelTV);
        currentProgressTV = (TextView)findViewById(R.id.currentProgressTV);
        currentLevelTV = (TextView)findViewById(R.id.currentLevelTV);
        currencyTV = (TextView)findViewById(R.id.tv_currency);
        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);
        BtnReset = (Button) findViewById(R.id.btn_reset);
        BtnLeaderBoard = (Button) findViewById(R.id.btn_leaderboard);
        levelProgressBar = (ProgressBar) findViewById(R.id.levelProgressBar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        levelProgressBar.setProgress(mUser.getUserCurrentProgress());
        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });

        BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sensorManager.unregisterListener(MainActivity.this);
                updateTextViews();
            }
        });
        BtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numSteps = 0;
                currentLevel = 1;
                currentLevelProgress = 0;
                maxSetting = 10;
                currentFunds = 0;
                mUser.setUserCurrentProgress(currentLevelProgress);
                mUser.setUserTotalSteps(numSteps);
                mUser.setUserCurrentLevel(currentLevel);
                mUser.setUserMaxSetting(maxSetting);
                mUser.setUserCurrentCurrency(currentFunds);
                databaseUsers.child(mUserID).setValue(mUser);
                updateTextViews();
                sensorManager.unregisterListener(MainActivity.this);
            }
        });
        BtnLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Leaderboard.class);
                startActivity(intent);
            }
        });
    }

    //Menu handling functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.sign_out:
                AuthUI.getInstance().signOut(MainActivity.this  );
                return true;
            case R.id.notifications:
                PopupMenu popup = new PopupMenu(this, findViewById(R.id.topRight));
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.notifications_items, popup.getMenu());
                popup.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //authentication listener handling for onPause/onResume
    @Override
    protected void onPause(){
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mAuthStateListener != null){
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK){
                Toast.makeText(MainActivity.this, "You're now signed in!", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void onSignedInInitialize(String username){
        mUsername = username;
    }

    private void onSignedOutCleanup(){
        mUsername = "NULL";
    }

    private void addUser(){
        //creating the user object
        databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mUserID)){
                    //do nothing, user is already in database
                    userData = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserID);
                    userData.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUser = dataSnapshot.getValue(UserModel.class);
                                numSteps = mUser.getUserTotalSteps();
                                currentLevel = mUser.getUserCurrentLevel();
                                currentLevelProgress = mUser.getUserCurrentProgress();
                                maxSetting = mUser.getUserMaxSetting();
                                currentFunds = mUser.getUserCurrentCurrency();
                                updateTextViews();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //levelProgressBar.setProgress(mUser.getUserCurrentProgress());
                    //refreshProgressBar();
                }else{

                    databaseUsers.child(mUserID).setValue(mUser);


                    userData = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserID);
                    userData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUser = dataSnapshot.getValue(UserModel.class);
                            if(mUser != null) {
                                numSteps = mUser.getUserTotalSteps();
                                currentLevel = mUser.getUserCurrentLevel();
                                currentLevelProgress = mUser.getUserCurrentProgress();
                                maxSetting = mUser.getUserMaxSetting();
                                currentFunds = mUser.getUserCurrentCurrency();
                                updateTextViews();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateTextViews(){
        totalForLevelTV.setText("/" + mUser.getUserMaxSetting());
        levelProgressBar.setMax(mUser.getUserMaxSetting());
        currentProgressTV.setText(TEXT_CURRENT_STEPS + mUser.getUserCurrentProgress());
        levelProgressBar.setProgress(mUser.getUserCurrentProgress());
        TvSteps.setText(TEXT_NUM_STEPS + mUser.getUserTotalSteps());
        currentLevelTV.setText("Level: " + mUser.getUserCurrentLevel());
        currencyTV.setText("Funds: $" + mUser.getUserCurrentCurrency());


    }


    public void refreshProgressBar(){
        int baseVal = 10;
        if(currentLevel == 1){
            maxSetting = baseVal;
        }
        mUser.setUserMaxSetting(maxSetting);
        levelProgressBar.setMax(maxSetting);
        levelProgressBar.setProgress(mUser.getUserCurrentProgress());
        updateTextViews();
    }
    public void updateProgressBar(){
        int baseVal = 10;
        if(currentLevel == 1){
            maxSetting = baseVal * currentLevel;
        }else{
            maxSetting = maxSetting * currentLevel;
        }
        mUser.setUserMaxSetting(maxSetting);
        levelProgressBar.setProgress(0);
        levelProgressBar.setMax(maxSetting);
        updateTextViews();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        currentFunds++;
        currentLevelProgress++;
        //database update attempt
        mUser.setUserCurrentProgress(currentLevelProgress);
        mUser.setUserCurrentCurrency(currentFunds);
        mUser.setUserTotalSteps(numSteps);
        levelProgressBar.incrementProgressBy(1);
        if(currentLevelProgress >= levelProgressBar.getMax()){
            currentLevel++;
            currentLevelProgress = 0;

            mUser.setUserCurrentProgress(0);


            updateProgressBar();
        }
        mUser.setUserCurrentLevel(currentLevel);
        updateTextViews();
        databaseUsers.child(mUserID).setValue(mUser);
    }


}
