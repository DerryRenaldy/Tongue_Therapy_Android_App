package com.example.tonguetherapy.personalData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tonguetherapy.R;
import com.example.tonguetherapy.databinding.ActivityMainBinding;
import com.example.tonguetherapy.databinding.ActivityPersonalDataBinding;
import com.example.tonguetherapy.mainMenu.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PersonalData extends AppCompatActivity {

    ActivityPersonalDataBinding binding;
    String emailUser, fullName, age, dateBirth, uid;

    private TextView mTvDataDiri;

    private String CurrentUserID;

    //Firebase Auth
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    //Realtime Database
    DatabaseReference reference;
    FirebaseDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnReadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentFirebaseUser!=null){
                    String userUid = currentFirebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Data User");
                    reference.child(userUid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                if (task.getResult().exists()){
                                    Toast.makeText(PersonalData.this,"Successfully Read",Toast.LENGTH_SHORT).show();
                                    DataSnapshot dataSnapshot = task.getResult();
                                    String emailUser = String.valueOf(dataSnapshot.child("emailUser").getValue());
                                    String fullName = String.valueOf(dataSnapshot.child("fullName").getValue());
                                    String age = String.valueOf(dataSnapshot.child("age").getValue());
                                    String dateBirth = String.valueOf(dataSnapshot.child("dateBirth").getValue());
                                    binding.etEmail.setText(emailUser);
                                    binding.etFullName.setText(fullName);
                                    binding.etAge.setText(age);
                                    binding.etDateBirth.setText(dateBirth);
                                }else {
                                    Toast.makeText(PersonalData.this,"User Doesn't Exist",Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Toast.makeText(PersonalData.this,"Failed to read",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(PersonalData.this, "No User Signed In", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalData.this, MainActivity.class));
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                fullName = binding.etFullName.getText().toString();
                age = binding.etAge.getText().toString();
                dateBirth = binding.etDateBirth.getText().toString();
                uid = binding.tvUid.getText().toString();


                if (!fullName.isEmpty() && !age.isEmpty() && !dateBirth.isEmpty()){


                    if (currentFirebaseUser != null) {

                        String email = currentFirebaseUser.getEmail();
                        binding.etEmail.setText(email);
                        emailUser = binding.etEmail.getText().toString();
                        binding.tvUid.setText(emailUser);
                    } else {
                        Toast.makeText(PersonalData.this, "User Not Signed In", Toast.LENGTH_SHORT).show();
                    }

                    Users users = new Users(emailUser,fullName,age,dateBirth,uid);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Data User");
                    reference.child(currentFirebaseUser.getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.etEmail.setText("");
                            binding.etFullName.setText("");
                            binding.etAge.setText("");
                            binding.etDateBirth.setText("");
                            Toast.makeText(PersonalData.this, "Succesfuly Updated", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });


    }
}