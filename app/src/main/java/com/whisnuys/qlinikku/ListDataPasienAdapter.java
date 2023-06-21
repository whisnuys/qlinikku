package com.whisnuys.qlinikku;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whisnuys.qlinikku.Models.Pasien;

import java.util.ArrayList;
import java.util.List;

public class ListDataPasienAdapter extends RecyclerView.Adapter<ListDataPasienAdapter.ViewHolder> implements Filterable {
    ArrayList<Pasien> listPasien;
    ListDataPasien context;
    ArrayList<Pasien> listPasienSearch;

    //  Filter Data
    Filter setSearch = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Pasien> filterPasien = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filterPasien.addAll(listPasienSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Pasien item : listPasienSearch){
                    if(item.getNamaLengkap().toLowerCase().trim().contains(filterPattern)){
                        filterPasien.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterPasien;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listPasien.clear();
            listPasien.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public ListDataPasienAdapter(ArrayList<Pasien> listPasien, ListDataPasien context){
        this.listPasien = listPasien;
        this.context = context;
        this.listPasienSearch = listPasien;
    }

    @Override
    public Filter getFilter() {
        return setSearch;
    }

    @NonNull
    @Override
    public ListDataPasienAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_pasien_design, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ListDataPasienAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final String NamaLengkap = listPasien.get(position).getNamaLengkap();
        final String Jeniskelamin = listPasien.get(position).getJenisKelamin();
        final String NoHP = listPasien.get(position).getNoHp();
        final String nik = listPasien.get(position).getNik();
        final String tglLahir = listPasien.get(position).getTanggalLahir();
        final String email = listPasien.get(position).getEmail();
        final String alamat = listPasien.get(position).getAlamat();
        final String GambarPasien = listPasien.get(position).getGambar();

        holder.namaLengkap.setText(": " + NamaLengkap);
        holder.jenisKelamin.setText(": " + Jeniskelamin);
        holder.noHp.setText(": " + NoHP);
        holder.nik.setText(": " + nik);
        holder.tglLahir.setText(": " + tglLahir);
        holder.email.setText(": " + email);
        holder.alamat.setText(": " + alamat);

        if(isEmpty(GambarPasien)){
            holder.gambarPasien.setImageResource(R.drawable.avatarhome);
        } else {
            Glide.with(holder.itemView.getContext()).load(GambarPasien.trim()).into(holder.gambarPasien);
        }

        holder.listItem.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(final View v){
                final String[] action = {"Reservasi Pasien","Update","Delete"};
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Apa yang akan anda pilih?");
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference checkExist = FirebaseDatabase.getInstance().getReference("Users").child(listPasien.get(position).getId()).child("active_app");
                        switch (i){
                            case 0:
                                checkExist.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()) {
                                            Bundle bundleRes = new Bundle();
                                            bundleRes.putString("dataPasienID", listPasien.get(position).getId());
                                            Intent intentRes = new Intent(v.getContext(), ReservasiAktifPasien.class);
                                            intentRes.putExtras(bundleRes);
                                            context.startActivity(intentRes);
                                        } else {
                                            Toast.makeText(v.getContext(), "Pasien tidak memiliki reservasi aktif", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                break;
                            case 1:
                                checkExist.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            Toast.makeText(v.getContext(), "Pasien sedang memiliki reservasi aktif", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("dataPasienID", listPasien.get(position).getId());
                                            Intent intent = new Intent(v.getContext(), EditDataPasien.class);
                                            intent.putExtras(bundle);
                                            context.startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                break;
                            case 2:
                                checkExist.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            Toast.makeText(v.getContext(), "Pasien sedang memiliki reservasi aktif", Toast.LENGTH_SHORT).show();
                                        } else {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                                            alert.setTitle("Apakah anda yakin menghapus data ini?");
                                            alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    DatabaseReference pasienRef = FirebaseDatabase.getInstance().getReference("Users").child(listPasien.get(position).getId());
                                                    pasienRef.removeValue();

                                                }
                                            }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    return;
                                                }
                                            });
                                            alert.create();
                                            alert.show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                break;
                        }
                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listPasien.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView namaLengkap, jenisKelamin, noHp, alamat, email, nik, tglLahir;
        private ShapeableImageView gambarPasien;
        private LinearLayout listItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaLengkap = itemView.findViewById(R.id.namaPasien);
            jenisKelamin = itemView.findViewById(R.id.jkPasien);
            noHp = itemView.findViewById(R.id.noHpPasien);
            alamat = itemView.findViewById(R.id.alamatPasien);
            email = itemView.findViewById(R.id.emailPasien);
            nik = itemView.findViewById(R.id.nikPasien);
            tglLahir = itemView.findViewById(R.id.tglLahirPasien);
            gambarPasien = itemView.findViewById(R.id.gambar_pasien);
            listItem = itemView.findViewById(R.id.list_item_pasien);
        }
    }
}
