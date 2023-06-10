package com.whisnuys.qlinikku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whisnuys.qlinikku.Models.Appointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailDokter extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Appointment selectedAppointment = null;
    private Map<Integer, Appointment> indexToAppt = new HashMap<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
    private SimpleDateFormat titleFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
//    String dokterID = getIntent().getStringExtra("dokterID");
//    String dokterJK = getIntent().getStringExtra("dataJkDokter");
//    String dokterSpesialis = getIntent().getStringExtra("dataSpesialisDokter");
//    String dokterGambar = getIntent().getStringExtra("dataGambarDokter");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dokter);


        RadioGroup appointmentGroup = (RadioGroup) findViewById(R.id.appointmentRadioGroup);
        appointmentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                RadioButton selected = (RadioButton) appointmentGroup.getChildAt(index);
                Toast.makeText(getApplicationContext(),"Selected: " + selected.getText(),
                        Toast.LENGTH_SHORT).show();

                selectedAppointment = indexToAppt.get(selected.getId());
            }
        });

        final long ONE_WEEK_AS_MILLISECONDS = 604800000;
        DialogFragment datePicker = new DatePickerFragment(System.currentTimeMillis(), System.currentTimeMillis()+ONE_WEEK_AS_MILLISECONDS); // Disables selecting appointments in the past; week into future
//        DialogFragment datePicker = new DatePickerFragment(System.currentTimeMillis(), -1); // Disables selecting appointments in the past; unlimited in future
        datePicker.show(getSupportFragmentManager(), "Appointment date");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        String dokterNama = getIntent().getStringExtra("dataNamaDokter");
        TextView title = findViewById(R.id.selectApptTitle);
        Calendar apptDateCalendar = Calendar.getInstance();
        apptDateCalendar.set(Calendar.YEAR, year);
        apptDateCalendar.set(Calendar.MONTH, month);
        apptDateCalendar.set(Calendar.DAY_OF_MONTH, day);
        apptDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
        apptDateCalendar.set(Calendar.MINUTE, 0);
        apptDateCalendar.set(Calendar.SECOND, 0);
        apptDateCalendar.set(Calendar.MILLISECOND, 0);
        title.setText(dokterNama + "'s available\nappointments on\n" + titleFormat.format(apptDateCalendar.getTime()));
        displayAppointmentsOnDate(apptDateCalendar.getTime());
    }

    public void displayAppointments(ArrayList<Appointment> appts) {
        if (appts.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Error: no availabilities on that date", Toast.LENGTH_SHORT).show();
        } else {
            //Adding appointments to RadioGroup
            int index = 0;
            RadioGroup apptGroup = (RadioGroup) findViewById(R.id.appointmentRadioGroup);
            apptGroup.removeAllViews();

            indexToAppt.clear(); // Reset just in case old appts
            for (Appointment appt : appts) {
                indexToAppt.put(index, appt);

                RadioButton apptRadioBtn = new RadioButton(this);
                apptRadioBtn.setText(sdf.format(appt.getDate()));

                apptGroup.addView(apptRadioBtn);
                apptRadioBtn.setId(index);
                apptRadioBtn.setPadding(0,0,0,16);
                index++;
            }
        }
    }

    public void displayAppointmentsOnDate(Date date) { // Only show availabilities from Doctor that is passed from BookYourAppointmentsMain
        ArrayList<Appointment> appts = new ArrayList<>();
        String doctorID = getIntent().getStringExtra("dokterID");

        // Get doctor's availableAppointmentIDs
        FirebaseDatabase.getInstance().getReference("Users")
                .child(doctorID)
                .child("availableAppointmentIDs")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot availableApptIDsSnapshot) {
                        for (DataSnapshot availableApptIDChild : availableApptIDsSnapshot.getChildren()) { // Loop through all of doctor's availableApptIDs
                            String availableApptID = availableApptIDChild.getValue(String.class);

                            // For each availableAppointmentID, get the Appointment object
                            FirebaseDatabase.getInstance().getReference("Appointments")
                                    .child(availableApptID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot availableApptSnapshot) {
                                            Appointment appt = availableApptSnapshot.getValue(Appointment.class);
                                            if (appt != null && !appt.isBooked() && !appt.isPassed() && DateUtility.isSameDay(appt.getDate(), date)) {
                                                appts.add(appt);
                                                displayAppointments(appts);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { // Error: Appointment associated w/ availableAppointmentID not found
                                            Toast.makeText(getApplicationContext(), "Error: Couldn't find available appointment", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { // Error: No availableAppointmentIDs found
                        Toast.makeText(getApplicationContext(), "Error: No available appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}