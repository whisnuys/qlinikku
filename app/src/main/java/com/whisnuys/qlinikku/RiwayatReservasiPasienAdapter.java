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
import com.whisnuys.qlinikku.Models.RiwayatReservasi;

import java.text.SimpleDateFormat;

public class RiwayatReservasiPasienAdapter extends FirebaseRecyclerAdapter<RiwayatReservasi, RiwayatReservasiPasienAdapter.ViewHolder> {
    Context context;
    private DatabaseReference ref1, ref2;

    public RiwayatReservasiPasienAdapter(@NonNull FirebaseRecyclerOptions<RiwayatReservasi> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull RiwayatReservasiPasienAdapter.ViewHolder holder, int position, @NonNull RiwayatReservasi model) {
        holder.app_dr_name.setText("Dr. " + model.getName());
        holder.app_tanggal.setText(model.getTanggal());

        if(isEmpty(model.getGambar())){
            holder.app_dr_image.setImageResource(R.drawable.avatarhome);
        } else {
            Glide.with(holder.itemView.getContext()).load(model.getGambar().trim()).into(holder.app_dr_image);
        }
    }

    @NonNull
    @Override
    public RiwayatReservasiPasienAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_riwayat_reservasi, parent, false);
        return new ViewHolder(view);
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        final ShapeableImageView app_dr_image;
        final TextView app_dr_name, app_tanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            app_dr_image = itemView.findViewById(R.id.app_dr_image);
            app_dr_name = itemView.findViewById(R.id.app_dr_name);
            app_tanggal = itemView.findViewById(R.id.app_tanggal);

        }
    }
}
