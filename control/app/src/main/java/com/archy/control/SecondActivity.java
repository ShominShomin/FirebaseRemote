package com.archy.control;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class SecondActivity extends AppCompatActivity {
    private TextView username;
    private EditText batNumber;
    DatabaseReference myRef;
    DatabaseReference myRefTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(user.getDisplayName()).child("Function");
        myRefTime = database.getReference(user.getDisplayName()).child("Time");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("Log Value", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Log Value", "Failed to read value.", error.toException());
            }
        });

        setContentView(R.layout.activity_second);
        username = (TextView) findViewById(R.id.tv_username);
        batNumber = (EditText)  findViewById(R.id.et_bat_number);
        username.setText(user.getDisplayName().toString());
        username.setSelected(false);
    }


    public void logoutButtonClick(View v) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            Intent k = new Intent(SecondActivity.this, MainActivity.class);
                            startActivity(k);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public void enterButtonClick(View v) {
        myRef.setValue("Enter");
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
    }

    public void leftButtonClick(View v) {
        myRef.setValue("Left");
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
    }

    public void rightButtonClick(View v) {
        myRef.setValue("Right");
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
    }

    public void upButtonClick(View v) {
        myRef.setValue("Up");
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
    }

    public void downButtonClick(View v) {
        myRef.setValue("Down");
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
    }

    public void mediaPauseButtonClick(View v) {
        myRef.setValue("Pause");
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
    }

    public void mediaJumpBackButtonClick(View v) {
        myRef.setValue("JumpBackward");
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
    }

    public void mediaJumpForButtonClick(View v) {
        myRef.setValue("JumpForward");
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
    }

    public void turnOffButtonClick(View v) {
        myRef.setValue("TurnOff");
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
        finish();
    }

    public void batExecuteButtonClick(View v) {
        myRef.setValue("Command_" + batNumber.getText().toString());
        myRefTime.setValue(Calendar.getInstance().getTimeInMillis());
    }
}
