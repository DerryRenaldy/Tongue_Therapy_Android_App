package com.example.tonguetherapy.bluetooth2;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tonguetherapy.R;
import com.example.tonguetherapy.common.ActivityProfile;
import com.example.tonguetherapy.personalData.PersonalData;
import com.example.tonguetherapy.personalData.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MonitoringScreen2 extends AppCompatActivity {

    private Vibrator vibrator;
    Button btnSave, btnDis, btnGraph, btnAverage, btnTpExercise, btnTsExercise1, btnTsExercise2, btnInfo;
    String dataTanggal, dataStatus1, dataStatus2, dataStatus3, dataLimit, dataEmail;

    private static final String TAG = "BlueTest5-MainActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    StorageReference reference;

    private boolean mIsUserInitiatedDisconnect = false;

    //graph
    GraphView graphView, graphView2;

    //FirebaseAuth
    FirebaseAuth mAuth;

    // All controls here
    private TextView mTxtBantuan, mTvString1, mTvString2, mTvStringResult1, mTvStringResult2, mTvRepetisi,
            mTvTanggal, mTvStatusPembacaan, mTvUser, mTvReport, mTvRataTekanan, mTvButtonExercise, mTvStatusTekanan, mTvBatasTekanan,
            mTvDataStatus1, mTvDataStatus2, mTvDataStatus3;

    private ScrollView scrollView2;
    private EditText mTxtReceive,mEtBatas;
    private Button mBtnClearInput, mBtnReport;
    private boolean mIsBluetoothConnected = false;
    private BluetoothDevice mDevice;
    private ProgressDialog progressDialog;

    //Realtime Database
    DatabaseReference references;
    FirebaseDatabase db;

    int sum = 0;
    int sum2 = 0;
    int i = 0;
    int r = 1;
    int x = 0;
    int y = 0;
    String[] myStringArray = new String[6];
    String[] myStringArray2 = new String[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_screen2);

        //Vibrate
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //Grafik
        graphView = findViewById(R.id.graph);
        graphView2 = findViewById(R.id.graph2);

        //FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //id declaration
        btnDis = findViewById(R.id.button4);
        btnSave = findViewById(R.id.buttonSend);
        btnGraph = findViewById(R.id.button3);
        btnAverage = findViewById(R.id.buttonAverage);
        mBtnReport = findViewById(R.id.buttonReport);
        btnTpExercise = findViewById(R.id.btnTonguePress);
        btnTsExercise1 = findViewById(R.id.btnTongueSlide1);
        btnTsExercise2 = findViewById(R.id.btnTongueSlide2);
        btnInfo = findViewById(R.id.btnInfo);

        //----------------------------------------------FOLDER SIMPANAN----------------------------------------------//
        reference = FirebaseStorage.getInstance().getReference().child("Data Tekanan");

        ActivityHelper2.initialize(this);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivityBt2.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivityBt2.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivityBt2.BUFFER_SIZE);
        Log.d(TAG, "Ready");
        mTxtReceive = (EditText) findViewById(R.id.txtReceive);
        mEtBatas = (EditText) findViewById(R.id.etBatas);
        mTxtBantuan = (TextView) findViewById(R.id.textBantuan);
        mTvString1 = (TextView) findViewById(R.id.tvString1);
        mTvString2 = (TextView) findViewById(R.id.tvString2);
        mTvStringResult1 = (TextView) findViewById(R.id.tvStringResult1);
        mTvStringResult2 = (TextView) findViewById(R.id.tvStringResult2);
        mTvRepetisi = (TextView) findViewById(R.id.tvRepetisi);
        mTvTanggal = (TextView) findViewById(R.id.tvTanggal);
        mTvUser = (TextView) findViewById(R.id.tvUser);
        mTvReport = (TextView) findViewById(R.id.tvReport);
        mTvRataTekanan = (TextView) findViewById(R.id.tvRataTekanan);
        mTvButtonExercise = (TextView) findViewById(R.id.tvButtonExercise);
        mTvStatusTekanan = (TextView) findViewById(R.id.tvStatusTekanan);
        mTvDataStatus1 = (TextView) findViewById(R.id.tvDataStatus1);
        mTvDataStatus2 = (TextView) findViewById(R.id.tvDataStatus2);
        mTvDataStatus3 = (TextView) findViewById(R.id.tvDataStatus3);
        scrollView2 = (ScrollView) findViewById(R.id.viewScroll2);
        mTvStatusPembacaan = (TextView) findViewById(R.id.tvStatusPembacaan);
        mTvBatasTekanan = (TextView) findViewById(R.id.tvBatasTekanan);
        mBtnClearInput = (Button) findViewById(R.id.btnClearInput);
        mTvReport.setMovementMethod(new ScrollingMovementMethod());


        //----------------------------------------------INITIAL----------------------------------------------//
        mTvReport.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
        btnAverage.setEnabled(false);
        btnGraph.setEnabled(false);
        mBtnClearInput.setEnabled(false);
        graphView.setVisibility(View.GONE);
        graphView2.setVisibility(View.GONE);
        //----------------------------------------------BUTTON INFO----------------------------------------------//
        btnInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.show(getSupportFragmentManager(),"MyFragment");
            }
        });

        //----------------------------------------------GET USER----------------------------------------------//

        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            String userEmail = user.getEmail();

            mTvUser.setText(userEmail);
        } else {
            mTvUser.setText("No User Signed In");
        }

        //----------------------------------------------Date----------------------------------------------//

        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);
        mTvTanggal.setText(formattedDate);

        //----------------------------------------------BUTTON EXERCISE----------------------------------------------//
        btnTpExercise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAverage.setEnabled(true);
                btnGraph.setEnabled(true);
                mBtnClearInput.setEnabled(true);
                btnTsExercise1.setEnabled(false);
                btnTsExercise2.setEnabled(false);

                String tpExercise = btnTpExercise.getText().toString();
                mTvButtonExercise.setText(tpExercise);
            }
        });

        btnTsExercise1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAverage.setEnabled(true);
                btnGraph.setEnabled(true);
                mBtnClearInput.setEnabled(true);
                btnTpExercise.setEnabled(false);
                btnTsExercise2.setEnabled(false);

                String tsExercise1 = btnTsExercise1.getText().toString();
                mTvButtonExercise.setText(tsExercise1);
            }
        });

        btnTsExercise2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAverage.setEnabled(true);
                btnGraph.setEnabled(true);
                mBtnClearInput.setEnabled(true);
                btnTpExercise.setEnabled(false);
                btnTsExercise1.setEnabled(false);

                String tsExercise2 = btnTsExercise2.getText().toString();
                mTvButtonExercise.setText(tsExercise2);
            }
        });

        //----------------------------------------------BUTTON----------------------------------------------//

        //graph function
        btnGraph.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick (View v) {
                mTvBatasTekanan.setText(mEtBatas.getText().toString());
                String setBatas = mEtBatas.getText().toString();
                if(TextUtils.isEmpty(setBatas)) {
                    mEtBatas.setError("This Field Can't Be Empty");
                    return;
                }
                else{
                    //disable button
                    btnGraph.setEnabled(false);
                    btnSave.setEnabled(false);
                    mBtnReport.setEnabled(false);
                    //send signal
                    sendSignal("4");
                    //inputGrafik
                    mTxtReceive.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String data = mTxtReceive.getEditableText().toString();
                            mTxtBantuan.setText(data);
                            if (r < 6) {
                                mTvRepetisi.setText("Repetition (" + r + ")");
                                //mTvRepetisi.setTextColor(Color.parseColor("#FF00000"));
                                if (i < 10) {
                                    mBtnClearInput.setEnabled(false);
                                    mTvStatusPembacaan.setText("reading not finished " + "(" + i + ")");

                                    //SENSOR 1
                                    char ch1 = mTxtBantuan.getText().toString().charAt(0);
                                    mTvString1.setText(String.valueOf(ch1));
                                    sum = sum + Integer.parseInt(mTvString1.getText().toString());
                                    mTvStringResult1.setText(Integer.toString(sum) + "00");

                                    //SENSOR 2
                                    char ch2 = mTxtBantuan.getText().toString().charAt(5);
                                    mTvString2.setText(String.valueOf(ch2));
                                    sum2 = sum2 + Integer.parseInt(mTvString2.getText().toString());
                                    mTvStringResult2.setText(Integer.toString(sum2) + "00");

                                    i++;
                                } else if (i == 10) {
                                    mTvStatusPembacaan.setText("10 Kali Pembacaan");
                                }
                            } else if (r > 4) {
                                mBtnClearInput.setText("FINISH");
                                mTvRepetisi.setText("Please Click the Show Result Button");
                                mBtnReport.setEnabled(true);
                                btnAverage.setEnabled(false);
                                mTvRepetisi.setTextColor(Color.parseColor("#FF0000"));
                            }

                            //---------------------STATUS TEKANAN---------------------//
                            int batass = Integer.parseInt(mEtBatas.getText().toString());
                            if(batass>0){
                                if (mTvButtonExercise.getText().toString().equals("Tongue Press")){
                                    mTvStringResult2.setTextColor(Color.parseColor("#008000"));
                                    int pembandingtp = Integer.parseInt(mTvString2.getText().toString()) * 100;
                                    int pembandingBatas = Integer.parseInt(mEtBatas.getText().toString());

                                    if(pembandingtp >= pembandingBatas){
                                        mTvStatusTekanan.setText("Enough Pressure");
                                        mTvStatusTekanan.setTextColor(Color.parseColor("#008000"));
                                    }
                                    else{
                                        mTvStatusTekanan.setText("Not Enough Pressure!");
                                        mTvStatusTekanan.setTextColor(Color.parseColor("#FF0000"));

                                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                                    }
                                }

                                else if (mTvButtonExercise.getText().toString().equals("Tongue Slide 1") ){
                                    mTvStringResult1.setTextColor(Color.parseColor("#008000"));
                                    int pembandingts1 = Integer.parseInt(mTvString1.getText().toString()) * 100;
                                    int pembandingBatas = Integer.parseInt(mEtBatas.getText().toString());

                                    if(pembandingts1 >= pembandingBatas){
                                        mTvStatusTekanan.setText("Enough Pressure");
                                        mTvStatusTekanan.setTextColor(Color.parseColor("#008000"));
                                    }
                                    else{
                                        mTvStatusTekanan.setText("Not Enough Pressure!");
                                        mTvStatusTekanan.setTextColor(Color.parseColor("#FF0000"));

                                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                                    }
                                }

                                else if (mTvButtonExercise.getText().toString().equals("Tongue Slide 2") ){
                                    mTvStringResult2.setTextColor(Color.parseColor("#008000"));
                                    int pembandingts2 = Integer.parseInt(mTvString2.getText().toString()) * 100;
                                    int pembandingBatas = Integer.parseInt(mEtBatas.getText().toString());

                                    if(pembandingts2 >= pembandingBatas){
                                        mTvStatusTekanan.setText("Enough Pressure");
                                        mTvStatusTekanan.setTextColor(Color.parseColor("#008000"));
                                    }
                                    else{
                                        mTvStatusTekanan.setText("Not Enough Pressure!");
                                        mTvStatusTekanan.setTextColor(Color.parseColor("#FF0000"));

                                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                                    }
                                }
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {


                        }
                    });

                }
            }
        });

        //button control
        btnAverage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                Average();
                btnAverage.setEnabled(false);
                mBtnClearInput.setEnabled(true);

            }
        });

        btnDis.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
                r = 1;
            }
        });

        mBtnClearInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Average();
                sum = 0;
                sum2 = 0;
                i = 0;
                r++;
                mTvStringResult1.setText("0");
                mTvStringResult1.setTextColor(Color.parseColor("#FF000000"));
                mTvStringResult2.setText("0");
                mTvStringResult2.setTextColor(Color.parseColor("#FF000000"));
                btnAverage.setEnabled(true);
            }
        });

        mBtnReport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mEtBatas.setText("0");
                btnTsExercise2.setEnabled(false);
                btnTsExercise1.setEnabled(false);
                btnTpExercise.setEnabled(false);
                Report();
                btnSave.setEnabled(true);
                btnAverage.setEnabled(false);
                mBtnReport.setEnabled(false);
                mEtBatas.setVisibility(View.GONE);
                mTxtReceive.setVisibility(View.GONE);
                mTvReport.setVisibility(View.VISIBLE);
                mTvStatusPembacaan.setVisibility(View.GONE);
                mTvRataTekanan.setVisibility(View.GONE);
                mTvStringResult1.setVisibility(View.GONE);
                mTvStringResult2.setVisibility(View.GONE);
                mTvRepetisi.setVisibility(View.GONE);
                mTvStatusTekanan.setVisibility(View.GONE);
                graphView.setVisibility(View.VISIBLE);
                graphView2.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                btnTpExercise.setVisibility(View.GONE);
                btnTsExercise1.setVisibility(View.GONE);
                btnTsExercise2.setVisibility(View.GONE);

            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //progressDialog = ProgressDialog.show(MonitoringScreen.this,"Upload File", "Uploading text file...");
                btnAverage.setEnabled(false);
                btnSave.setEnabled(false);

                String latihan = mTvButtonExercise.getText().toString();
                String data = mTvReport.getText().toString();
                String user = mTvUser.getText().toString();
                String tanggal = mTvTanggal.getText().toString();
                reference.child(latihan).child(tanggal).child(user + ".txt").putBytes(data.getBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //progressDialog.dismiss();
                        Toast.makeText(MonitoringScreen2.this, "Upload successful!", Toast.LENGTH_SHORT).show();

                        //dataLatihan
                        mEtBatas.setText("0");
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        dataTanggal = mTvTanggal.getText().toString();
                        dataStatus1 = mTvDataStatus1.getText().toString();
                        dataStatus2 = mTvDataStatus2.getText().toString();
                        dataStatus3 = mTvDataStatus3.getText().toString();
                        dataLimit = mTvBatasTekanan.getText().toString();
                        dataEmail = mTvUser.getText().toString();

                        if (currentFirebaseUser!=null){

                        }else {
                            Toast.makeText(MonitoringScreen2.this, "User Not Signed In", Toast.LENGTH_SHORT).show();
                        }

                        DataLatihan dataLatihan = new DataLatihan(dataTanggal, dataStatus1, dataStatus2, dataStatus3, dataLimit, dataEmail);
                        db = FirebaseDatabase.getInstance();
                        references = db.getReference("Data Latihan");
                        references.child(currentFirebaseUser.getUid()).setValue(dataLatihan).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mTvTanggal.setText(dataTanggal);
                                mTvDataStatus1.setText(dataStatus1);
                                mTvDataStatus2.setText(dataStatus2);
                                mTvDataStatus3.setText(dataStatus3);
                                mEtBatas.setText(dataLimit);
                                mTvUser.setText(dataEmail);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //exception.printStackTrace();
                        //progressDialog.dismiss();
                        Toast.makeText(MonitoringScreen2.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });

    }

    //----------------------------------------------FUNCTION----------------------------------------------//

    private void sendSignal ( String number ) {
        if ( mBTSocket!= null ) {
            try {
                mBTSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void Disconnect () {
        if ( mBTSocket!=null ) {
            try {
                mBTSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void Average () {

        sum = sum*100 / 10;
        sum2 = sum2*100 / 10;
        mTvStringResult1.setText(sum + " gram");
        mTvStringResult1.setTextColor(Color.parseColor("#FF0000"));

        mTvStringResult2.setText(sum2 + " gram");
        mTvStringResult2.setTextColor(Color.parseColor("#FF0000"));

        if(r < 6){
            String value = String.valueOf(sum);
            String value2 = String.valueOf(sum2);

            myStringArray[r] = value;
            myStringArray2[r] = value2;
        }else {
            //Toast.makeText(MonitoringScreen2.this, "Done", Toast.LENGTH_SHORT).show();
            mBtnClearInput.setEnabled(false);
        }
    }

    private void Report() {

        r = 1;
        mTvRepetisi.setTextColor(Color.parseColor("#FF000000"));

        //Graphic 1
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1, Double.parseDouble(myStringArray[1])),
                new DataPoint(2, Double.parseDouble(myStringArray[2])),
                new DataPoint(3, Double.parseDouble(myStringArray[3])),
                new DataPoint(4, Double.parseDouble(myStringArray[4])),
                new DataPoint(5, Double.parseDouble(myStringArray[5]))
        });
        graphView.setTitle("SENSOR 1");
        graphView.setTitleColor(R.color.purple_200);
        graphView.setTitleTextSize(32);
        graphView.addSeries(series);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        series.setSpacing(20);

        //Graphic 2
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1, Double.parseDouble(myStringArray2[1])),
                new DataPoint(2, Double.parseDouble(myStringArray2[2])),
                new DataPoint(3, Double.parseDouble(myStringArray2[3])),
                new DataPoint(4, Double.parseDouble(myStringArray2[4])),
                new DataPoint(5, Double.parseDouble(myStringArray2[5]))
        });
        graphView2.setTitle("SENSOR 2");
        graphView2.setTitleColor(R.color.purple_200);
        graphView2.setTitleTextSize(32);
        graphView2.addSeries(series2);
        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.RED);
        series2.setSpacing(20);

        String latihan = mTvButtonExercise.getText().toString();
        String tanggal = mTvTanggal.getText().toString();
        String user = mTvUser.getText().toString();
        String batas = mTvBatasTekanan.getText().toString();
        mTvReport.append(latihan + "\n");
        mTvReport.append(tanggal + "\n");
        mTvReport.append("User = " + user + "\n" + "\n");
        mTvReport.append("Limit = " + batas+ "\n");
        mTvReport.append("SENSOR 1" + "\n");

        int[] jumlah = new int[myStringArray.length];
        int batasTekanan = Integer.parseInt(mTvBatasTekanan.getText().toString());
        for (i = 1; i < myStringArray.length; i++){
            jumlah[i] = Integer.parseInt(myStringArray[i]);
            if (jumlah[i] > batasTekanan){
                x++;
            }
            if(myStringArray[i].length() > 2){
                mTvReport.append(myStringArray[i] + " gram" + "\n");
            }
            else if (myStringArray[i].length() == 2){
                mTvReport.append("0" + myStringArray[i] + " gram" + "\n");
            }
            else{
                mTvReport.append(myStringArray[i] + "00 gram" + "\n");
            }
        }

        int rata = (jumlah[1]+jumlah[2]+jumlah[3]+jumlah[4]+jumlah[5])/5;
        String sRata = String.valueOf(rata);
        mTvReport.append("\n");
        mTvReport.append("Average of Sensor 1 = " + sRata + " gram" );

        int sRataIntBatas = Integer.parseInt(mTvBatasTekanan.getText().toString());
        int sRataInt = Integer.parseInt(sRata);

        //---------------------STATUS TEKANAN---------------------//

        if (mTvButtonExercise.getText().toString().equals("Tongue Slide 1") ){
            if (sRataInt < sRataIntBatas){
                mTvReport.append("\n");
                mTvReport.append("The Pressure Hasn't Reached The Limit");
                mTvDataStatus2.setText("The Pressure Hasn't Reached The Limit");
            } else {
                mTvReport.append("\n");
                mTvReport.append("The Pressure Has Reached The Limit");
                mTvDataStatus2.setText("The Pressure Has Reached The Limit");
            }
        }

        if(x==0){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 0%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else if (x==1){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 20%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else if (x==2){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 40%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else if (x==3){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 60%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else if (x==4){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 80%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else {
            mTvReport.append("\n");
            mTvReport.append("Success Rate 100%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }


        mTvReport.append("\n");
        mTvReport.append("SENSOR 2" + "\n");
        int[] jumlah2 = new int[myStringArray.length];

        for (i = 1; i < myStringArray2.length; i++){
            jumlah2[i] = Integer.parseInt(myStringArray2[i]);
            if (jumlah2[i] > batasTekanan){
                y++;
            }
            if(myStringArray2[i].length() > 2){
                mTvReport.append(myStringArray2[i] + " gram" + "\n");
            }
            else if (myStringArray2[i].length() == 2){
                mTvReport.append("0" + myStringArray2[i] + " gram" + "\n");
            }
            else{
                mTvReport.append(myStringArray2[i] + "00 gram" + "\n");
            }
        }
        int rata2 = (jumlah2[1]+jumlah2[2]+jumlah2[3]+jumlah2[4]+jumlah2[5])/5;
        String sRata2 = String.valueOf(rata2);
        mTvReport.append("\n");
        mTvReport.append("Average of Sensor 2 = " + sRata2 + " gram");

        int sRataInt2 = Integer.parseInt(sRata2);

        //---------------------STATUS TEKANAN---------------------//
        if (mTvButtonExercise.getText().toString().equals("Tongue Press")){
            if (sRataInt2 < sRataIntBatas){
                mTvReport.append("\n");
                mTvReport.append("The Pressure Hasn't Reached The Limit");
                mTvDataStatus1.setText("The Pressure Hasn't Reached The Limit");
            } else {
                mTvReport.append("\n");
                mTvReport.append("The Pressure Has Reached The Limit");
                mTvDataStatus1.setText("The Pressure Has Reached The Limit");
            }
        }

        else if (mTvButtonExercise.getText().toString().equals("Tongue Slide 2") ){
            if (sRataInt2 < sRataIntBatas){
                mTvReport.append("\n");
                mTvReport.append("The Pressure Hasn't Reached The Limit");
                mTvDataStatus3.setText("The Pressure Hasn't Reached The Limit");
            } else {
                mTvReport.append("\n");
                mTvReport.append("The Pressure Has Reached The Limit");
                mTvDataStatus3.setText("The Pressure Has Reached The Limit");
            }
        }

        if(y==0){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 0%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else if (y==1){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 20%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else if (y==2){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 40%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else if (y==3){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 60%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else if (y==4){
            mTvReport.append("\n");
            mTvReport.append("Success Rate 80%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
        else {
            mTvReport.append("\n");
            mTvReport.append("Success Rate 100%" + "\n");
            mTvReport.append("-----------------------------" + "\n");
        }
    }

    //----------------------------------------------NEW THREAD----------------------------------------------//

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */

                            mTxtReceive.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTxtReceive.setText(strInput);


                                    int txtLength = mTxtReceive.getEditableText().length();
                                    if (txtLength > mMaxChars) {
                                        mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars);
                                    }

                                }
                            });

                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MonitoringScreen2.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    //BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }

}