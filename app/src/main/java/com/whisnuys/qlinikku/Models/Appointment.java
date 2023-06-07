package com.whisnuys.qlinikku.Models;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whisnuys.qlinikku.DateUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Appointment implements Comparable<Appointment>{
    private Date date;
    private String dokterID, pasienID, appointmentID;
    private static int DAY_IN_MILLISECONDS = 86400000;

    public Appointment() {}

    public Appointment(Date date, Dokter dokter, Pasien pasien) {
        this.date = date;
        this.dokterID = dokter.getUid();

        if (pasien == null) {
            this.setPasienID("");
        } else {
            this.setPasienID(pasien.getId());
        }

        this.appointmentID = hashCode() + "";
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDokterID() {
        return dokterID;
    }

    public void setDokterID(String dokterID) {
        this.dokterID = dokterID;
    }

    public String getPasienID() {
        return pasienID;
    }

    public void setPasienID(String PasienID) {
        pasienID = PasienID;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public boolean isBooked() {
        return !this.pasienID.isEmpty();
    }

    public boolean isPassed() {
        return this.date.compareTo(new Date(System.currentTimeMillis())) < 0;
    }

    public static Appointment generateAvailableAppointment(Date date, Dokter dokter) {
        if(date != null && dokter != null) {
            Appointment availableAppt = new Appointment(date, dokter, null);

            FirebaseDatabase.getInstance().getReference("Appointments").child(availableAppt.getAppointmentID()).setValue(availableAppt).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        dokter.addAvailableAppointment(availableAppt);

                        FirebaseDatabase.getInstance().getReference("Users").child(dokter.getUid()).setValue(dokter);
                    } else {
                        Log.i("appt_error", "Failed to add new available appointment to Firebase");
                    }
                }
            });
            return availableAppt;
        } else {
            return null;
        }
    }

    public static void generateAvailableAppointmentsForOneDoctor(Dokter dokter, Date generateApptDate){
        // Generates one day of appointments for all doctors (9am, 11am, 1pm, 3pm)
        int day = DateUtility.getDay(generateApptDate);
        int month = DateUtility.getMonth(generateApptDate);
        int year = DateUtility.getYear(generateApptDate);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        for (int j = 0; j < 5; j++) { //Generating the day's appts 9,11am,1,3,5pm
            Appointment.generateAvailableAppointment(c.getTime(), dokter);
            c.add(Calendar.HOUR_OF_DAY, 2);
        }
    }

    public static void updateAvailableAppointmentsForAllDoctors() {
        //Checks to see if the last updated (availability) of doctor date and current date
        //to see if there is still 7 days worth of appointments!
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                for (DataSnapshot userChild : usersSnapshot.getChildren()) {
                    String type = userChild.child("role").getValue(String.class);
                    //If the user is a doctor, fetch then call generateAvailableAppointment for available time slots
                    if (type.equals("dokter")){
                        Date lastUpdated = userChild.child("lastAutoGeneratedApptDate").getValue(Date.class);
                        Calendar sevenDaysAhead = Calendar.getInstance();//Since we automatically generated 7 weeks of appts when doctor register
                        sevenDaysAhead.add(Calendar.DAY_OF_MONTH, 7);
                        Dokter dokter = userChild.getValue(Dokter.class);
                        if ((sevenDaysAhead.getTimeInMillis() - lastUpdated.getTime()) >= DAY_IN_MILLISECONDS) {
                            dokter.setLastAutoGeneratedApptDate(sevenDaysAhead.getTime());
                            generateAvailableAppointmentsForOneDoctor(dokter, sevenDaysAhead.getTime());
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(dokter.getUid())
                                    .setValue(dokter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("appt_error", "Failed to get Doctor info from Firebase");
            }
        });
    }

    public static void bookAppointment(String appointmentID, String pasienID) {
        // Get appt
        DatabaseReference apptRef = FirebaseDatabase.getInstance().getReference("Appointments").child(appointmentID);
        apptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Appointment appt = snapshot.getValue(Appointment.class);
                if (appt != null) {
                    appt.setPasienID(pasienID); // Update patientID in appointment
                    apptRef.setValue(appt); // Send back to Firebase

                    // Patient:
                    // Add appointment to upcomingAppointmentIDs
                    DatabaseReference patientUpcomingApptIDsRef = FirebaseDatabase.getInstance().getReference("Users")
                            .child(pasienID)
                            .child("upcomingAppointmentIDs");
                    patientUpcomingApptIDsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot patientUpcomingApptIDsSnapshot) {
                            List<String> upcomingApptIDs = new ArrayList<>();
                            for (DataSnapshot patientUpcomingApptIDChild : patientUpcomingApptIDsSnapshot.getChildren()) {
                                upcomingApptIDs.add(patientUpcomingApptIDChild.getValue(String.class));
                            }
                            upcomingApptIDs.add(appointmentID);
                            patientUpcomingApptIDsRef.setValue(upcomingApptIDs);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    // Doctor:
                    // Add appointment to upcomingAppointmentIDs
                    DatabaseReference doctorUpcomingApptIDsRef = FirebaseDatabase.getInstance().getReference("Users")
                            .child(appt.getDokterID())
                            .child("upcomingAppointmentIDs");
                    doctorUpcomingApptIDsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot doctorUpcomingApptIDsSnapshot) {
                            List<String> upcomingApptIDs = new ArrayList<>();
                            for (DataSnapshot doctorUpcomingApptIDChild : doctorUpcomingApptIDsSnapshot.getChildren()) {
                                upcomingApptIDs.add(doctorUpcomingApptIDChild.getValue(String.class));
                            }
                            upcomingApptIDs.add(appointmentID);
                            doctorUpcomingApptIDsRef.setValue(upcomingApptIDs);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    // Remove appointment from availableAppointmentIDs
                    DatabaseReference doctorAvailableApptIDsRef = FirebaseDatabase.getInstance().getReference("Users")
                            .child(appt.getDokterID())
                            .child("availableAppointmentIDs");
                    doctorAvailableApptIDsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot doctorAvailableApptIDsSnapshot) {
                            List<String> availableApptIDs = new ArrayList<>();
                            for (DataSnapshot doctorAvailableApptIDChild : doctorAvailableApptIDsSnapshot.getChildren()) {
                                availableApptIDs.add(doctorAvailableApptIDChild.getValue(String.class));
                            }
                            availableApptIDs.remove(appointmentID);
                            doctorAvailableApptIDsRef.setValue(availableApptIDs);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { // Error: patient doesn't exist
            }
        });
    }

    public static void bookAppointment(String appointmentID, Pasien pasien) {
        bookAppointment(appointmentID, pasien.getId());
    }
    public static void bookAppointment(Appointment appt, Pasien pasien) {
        bookAppointment(appt.getAppointmentID(), pasien.getId());
    }
    public static void bookAppointment(Appointment appt, String pasienID) {
        bookAppointment(appt.getAppointmentID(), pasienID);
    }

    public static void expireAppointments(){
        FirebaseDatabase.getInstance().getReference("Appointments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot apptSnapshot) {
                for (DataSnapshot apptChild : apptSnapshot.getChildren()) { // Loop through all appointments
                    Appointment appt = apptChild.getValue(Appointment.class);

                    FirebaseDatabase.getInstance().getReference("Appointments") // Prevent expiring appointment that is already expired
                            .child(appt.getAppointmentID())
                            .child("passed")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot passedSnapshot) {
                                    if (!passedSnapshot.getValue(boolean.class)) { // Appointment is not expired yet (ie. not updated in Firebase)
                                        if (appt != null && appt.date != null && appt.isPassed()) { // Appointment has passed
                                            if (appt.isBooked()) { // Appointment is passed & booked
                                                String pasienID = appt.getPasienID();
                                                String dokterID = appt.getDokterID();
                                                String apptID = appt.getAppointmentID();

                                                // Update appointment (ie. update isPassed())
                                                FirebaseDatabase.getInstance().getReference("Appointments")
                                                        .child(apptID)
                                                        .setValue(appt);

                                                // Patient:
                                                // Remove appointment from upcomingAppointmentIDs
                                                DatabaseReference patientUpcomingApptIDsRef = FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(pasienID)
                                                        .child("upcomingAppointmentIDs");
                                                patientUpcomingApptIDsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot patientUpcomingApptIDsSnapshot) {
                                                        List<String> upcomingApptIDs = new ArrayList<>();
                                                        for (DataSnapshot patientUpcomingApptIDChild : patientUpcomingApptIDsSnapshot.getChildren()) {
                                                            String patientUpcomingApptID = patientUpcomingApptIDChild.getValue(String.class);
                                                            if (!upcomingApptIDs.contains(patientUpcomingApptID) && !patientUpcomingApptID.equals(apptID)) // Prevent duplicates, remove apptID from upcomingApptIDs
                                                                upcomingApptIDs.add(patientUpcomingApptID);
                                                        }
//                                                        upcomingApptIDs.remove(apptID);
                                                        patientUpcomingApptIDsRef.setValue(upcomingApptIDs);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });

                                                // Add appointment to prevAppointmentIDs
                                                DatabaseReference patientPrevApptIDsRef = FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(pasienID)
                                                        .child("prevAppointmentIDs");
                                                patientPrevApptIDsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot patientPrevApptIDsSnapshot) {
                                                        List<String> prevApptIDs = new ArrayList<>();
                                                        for (DataSnapshot patientPrevApptIDChild : patientPrevApptIDsSnapshot.getChildren()) {
                                                            String patientPrevApptID = patientPrevApptIDChild.getValue(String.class);
                                                            if (!prevApptIDs.contains(patientPrevApptID) && !patientPrevApptID.equals(apptID)) // Prevent duplicates
                                                                prevApptIDs.add(patientPrevApptID);
                                                        }
                                                        prevApptIDs.add(apptID);
                                                        patientPrevApptIDsRef.setValue(prevApptIDs);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });

                                                // Add doctorID to seenDoctorIDs
                                                DatabaseReference patientSeenDoctorIDsRef = FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(pasienID)
                                                        .child("seenDoctorIDs");
                                                patientSeenDoctorIDsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot patientSeenDoctorIDsSnapshot) {
                                                        List<String> seenDoctorIDs = new ArrayList<>();
                                                        for (DataSnapshot patientSeenDoctorIDChild : patientSeenDoctorIDsSnapshot.getChildren()) {
                                                            String patientSeenDoctorID = patientSeenDoctorIDChild.getValue(String.class);
                                                            if (!seenDoctorIDs.contains(patientSeenDoctorID) && !patientSeenDoctorID.equals(dokterID)) // Prevent duplicates
                                                                seenDoctorIDs.add(patientSeenDoctorID);
                                                        }
                                                        seenDoctorIDs.add(dokterID);
                                                        patientSeenDoctorIDsRef.setValue(seenDoctorIDs);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });

                                                // Doctor:
                                                // Remove appointment from upcomingAppointmentIDs
                                                DatabaseReference doctorUpcomingApptIDsRef = FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(dokterID)
                                                        .child("upcomingAppointmentIDs");
                                                doctorUpcomingApptIDsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot doctorUpcomingApptIDsSnapshot) {
                                                        List<String> upcomingApptIDs = new ArrayList<>();
                                                        for (DataSnapshot doctorUpcomingApptIDChild : doctorUpcomingApptIDsSnapshot.getChildren()) {
                                                            String doctorUpcomingApptID = doctorUpcomingApptIDChild.getValue(String.class);
                                                            if (!upcomingApptIDs.contains(doctorUpcomingApptID) && !doctorUpcomingApptID.equals(apptID)) // Prevent duplicates, remove apptID from upcoming
                                                                upcomingApptIDs.add(doctorUpcomingApptID);
                                                        }
//                                                        upcomingApptIDs.remove(apptID);
                                                        doctorUpcomingApptIDsRef.setValue(upcomingApptIDs);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });

                                                // Add appointment to prevAppointmentIDs
                                                DatabaseReference doctorPrevApptIDsRef = FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(dokterID)
                                                        .child("prevAppointmentIDs");
                                                doctorPrevApptIDsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot doctorPrevApptIDsSnapshot) {
                                                        List<String> prevApptIDs = new ArrayList<>();
                                                        for (DataSnapshot doctorPrevApptIDChild : doctorPrevApptIDsSnapshot.getChildren()) {
                                                            String doctorPrevApptID = doctorPrevApptIDChild.getValue(String.class);
                                                            if (!prevApptIDs.contains(doctorPrevApptID) && !doctorPrevApptID.equals(apptID)) // Prevent duplictaes
                                                                prevApptIDs.add(doctorPrevApptID);
                                                        }
                                                        prevApptIDs.add(apptID);
                                                        doctorPrevApptIDsRef.setValue(prevApptIDs);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });

                                                // Add patientID to seenPatientIDs
                                                DatabaseReference doctorSeenDoctorIDsRef = FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(dokterID)
                                                        .child("seenPatientIDs");
                                                doctorSeenDoctorIDsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot doctorSeenPatientIDsSnapshot) {
                                                        List<String> seenPatientIDs = new ArrayList<>();
                                                        for (DataSnapshot doctorSeenPatientIDChild : doctorSeenPatientIDsSnapshot.getChildren()) {
                                                            String doctorSeenPatientID = doctorSeenPatientIDChild.getValue(String.class);
                                                            if (!seenPatientIDs.contains(doctorSeenPatientID) && !doctorSeenPatientID.equals(pasienID)) // Prevent duplicates
                                                                seenPatientIDs.add(doctorSeenPatientID);
                                                        }
                                                        seenPatientIDs.add(pasienID);
                                                        doctorSeenDoctorIDsRef.setValue(seenPatientIDs);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });

                                            } else { // Appointment has passed but not been booked
                                                DatabaseReference doctorRef = FirebaseDatabase.getInstance().getReference("Users").child(appt.getDokterID());
                                                doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot doctorSnapshot) {
                                                        Dokter dokter = doctorSnapshot.getValue(Dokter.class);
                                                        if (dokter != null) {
                                                            dokter.removeAvailableAppointment(appt); // Remove passed&unbooked appt from doctor.availableAppointmentIDs
                                                            doctorRef.setValue(dokter); // Send updated doctor back to Firebase
                                                        }
                                                        apptChild.getRef().removeValue(); // Remove passed&unbooked appt from Firebase
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error2) {
                                                        Log.i("appt_error", "Failed to get doctor from Firebase");
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return getDate().equals(that.getDate()) && getDokterID().equals(that.getDokterID()) && getPasienID().equals(that.getPasienID()) && getAppointmentID().equals(that.getAppointmentID());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getDokterID(), getPasienID());
    }

    @Override
    public int compareTo(Appointment appointment) {
        return 0;
    }
}