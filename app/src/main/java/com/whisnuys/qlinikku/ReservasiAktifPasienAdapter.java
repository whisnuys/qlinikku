package com.whisnuys.qlinikku;

import static android.text.TextUtils.isEmpty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whisnuys.qlinikku.Models.ReservasiAktif;

public class ReservasiAktifPasienAdapter extends FirebaseRecyclerAdapter<ReservasiAktif, ReservasiAktifPasienAdapter.ViewHolder> {
    Context context;
    String pasienID;
    private DatabaseReference ref1, ref2;

    public ReservasiAktifPasienAdapter(@NonNull FirebaseRecyclerOptions<ReservasiAktif> options, Context context, String pasienID){
        super(options);
        this.context = context;
        this.pasienID = pasienID;
    }

    @Override
    protected void onBindViewHolder(@NonNull ReservasiAktifPasienAdapter.ViewHolder holder, int position, @NonNull ReservasiAktif model) {
        holder.app_dr_name.setText("Dr. " + model.getName());
        holder.app_dr_time.setText(model.getTime());

        if(isEmpty(model.getImage())){
            holder.app_dr_image.setImageResource(R.drawable.avatarhome);
        } else {
            Glide.with(holder.itemView.getContext()).load(model.getImage().trim()).into(holder.app_dr_image);
        }

        ref1 = FirebaseDatabase.getInstance().getReference("Dokter");

        ref2 = FirebaseDatabase.getInstance().getReference("Users").child(pasienID);

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Reservasi Selesai");
                dialog.setMessage("Terima kasih telah menggunakan Qlinikku");

                dialog.setPositiveButton("Ya", (dialog1, which) -> {
                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren()) {
                                if(ds.child("namaLengkap").getValue().equals(model.getName())) {
                                    DatabaseReference ref = ref1.child(ds.getKey());
                                    ref.child("slots").child(model.getTime()).setValue("Available");
                                    ref.child("my_app").child(model.getTime()).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Users").child(pasienID).child("riwayat");
                    ref3.child(model.getName() + model.getTime()).setValue(model);
                    ref2.child("active_app").child(model.getName() + model.getTime()).removeValue();
                });

                dialog.setNegativeButton("Batal", (dialog12, which) -> {

                });

                dialog.show();
            }
        });
    }

    @NonNull
    @Override
    public ReservasiAktifPasienAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_active_appointments, parent, false);
        return new ViewHolder(view);
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        final ShapeableImageView app_dr_image;
        final TextView app_dr_name, app_dr_time;
        Button del, reschedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            app_dr_image = itemView.findViewById(R.id.app_dr_image);
            app_dr_name = itemView.findViewById(R.id.app_dr_name);
            app_dr_time = itemView.findViewById(R.id.app_dr_time);

            del = itemView.findViewById(R.id.selesai);
        }
    }

}
