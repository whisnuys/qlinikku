package com.whisnuys.qlinikku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TambahJadwalDokter extends AppCompatActivity {

    public static String dokterID;

    TextView timer1, timer2, selectedDate;
    Button btn, btnPilihTanggal;

    int t1Hr, t1Min, t2Hr, t2Min;
    SimpleDateFormat sdf;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_jadwal_dokter);
        dokterID = getIntent().getStringExtra("dokterID");
        ref = FirebaseDatabase.getInstance().getReference("Dokter").child(dokterID).child("slots");

        timer1 = findViewById(R.id.timer1);
        timer2 = findViewById(R.id.timer2);
        btn = findViewById(R.id.confirm_btn);
        List<Date> dates = new ArrayList<Date>();
        selectedDate = findViewById(R.id.show_selected_date);
        btnPilihTanggal = findViewById(R.id.btnPilihTanggal);
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        btnPilihTanggal.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Pair<Long,Long> selection) {

                        selectedDate.setText("Tanggal Pilih : " + materialDatePicker.getHeaderText());
                        Long startDate = selection.first;
                        Long endDate = selection.second;
//                        String startDateString = DateFormat.format("dd/MM/yyyy", new Date(startDate)).toString();
//                        String endDateString = DateFormat.format("dd/MM/yyyy", new Date(endDate)).toString();
//                        String date = "Start: " + startDateString + " End: " + endDateString;
//                        Toast.makeText(TambahJadwalDokter.this, date, Toast.LENGTH_SHORT).show();
//                        Log.e("DATE", date);

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        long interval = 24*1000 * 60 * 60;
                        long endTime =endDate;
                        long curTime = startDate;
                        while (curTime <= endTime) {
                            dates.add(new Date(curTime));
                            curTime += interval;
                        }
                        for(int i=0;i<dates.size();i++){
                            String result = formatter.format(dates.get(i));
                        }

                        Log.e("DATES", dates.toString());

                    }

                });

        timer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale.setDefault(new Locale("in", "ID"));
                TimePickerDialog timePickerDialog = new TimePickerDialog(TambahJadwalDokter.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                t1Hr = hourOfDay;
                                t1Min = minute;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0, 0, 0, t1Hr, t1Min);

                                String dateAndroid = android.text.format.DateFormat.format(
                                        "kk:mm", calendar).toString();

                                timer1.setText(dateAndroid);
                            }
                        }, 12, 0, true);
                timePickerDialog.updateTime(t1Hr, t1Min);
                timePickerDialog.show();
            }
        });

        timer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale.setDefault(new Locale("in", "ID"));
                TimePickerDialog timePickerDialog = new TimePickerDialog(TambahJadwalDokter.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                t2Hr = hourOfDay;
                                t2Min = minute;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0, 0, 0, t2Hr, t2Min);

                                String dateAndroid = android.text.format.DateFormat.format(
                                        "kk:mm", calendar).toString();

                                timer2.setText(dateAndroid);
                            }
                        }, 12, 0, true);
                timePickerDialog.updateTime(t2Hr, t2Min);
                timePickerDialog.show();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {
                Locale.setDefault(new Locale("in", "ID"));
                int min = findMinutes();

                Calendar calendar = Calendar.getInstance();
                ArrayList<String> results = new ArrayList<>();
                sdf = new SimpleDateFormat("HH:mm");

                calendar.set(0, 0, 0, t1Hr, t1Min);

                for (int i = 0; i < min; i++) {
                    String  day1 = sdf.format(calendar.getTime());
                    // add 15 minutes to the current time; the hour adjusts automatically!
                    calendar.add(Calendar.MINUTE, 15);
                    String day2 = sdf.format(calendar.getTime());

                    String day = day1 + " - " + day2;
                    results.add(day);
                }

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                if(results.size() != 0) {
                    Toast.makeText(getApplicationContext(), "Berhasil di Update", Toast.LENGTH_SHORT).show();
                    ref.setValue(null);
                    for(int i=0;i<dates.size();i++){
                        String resultDate = formatter.format(dates.get(i));
                        for(String slot: results) {
                            ref.child(resultDate).child(slot).setValue("Available");
                            startActivity(new Intent(getApplicationContext(), AdminHome.class));
                            finish();
                        }
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "Mohon untuk memilih waktu yang benar", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("SimpleDateFormat")
    private int findMinutes() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(0, 0, 0, t1Hr, t1Min);
        String d1 = android.text.format.DateFormat.format(
                "kk:mm", calendar).toString();

        calendar.set(0, 0, 0, t2Hr, t2Min);
        String d2 = android.text.format.DateFormat.format(
                "kk:mm", calendar).toString();

        sdf = new SimpleDateFormat("HH:mm");

        Date date1 = null;
        try {
            date1 = sdf.parse(d1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = sdf.parse(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = date2.getTime() - date1.getTime();
        int min = (int) (diff / (1000 * 60));
        min = min/15;
        return (min);
    }

    public void jadwalDokterBackToHome(View view) {
        startActivity(new Intent(this, ListDataDokter.class));
    }
}