package com.whisnuys.qlinikku;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.whisnuys.qlinikku.Models.Appointment;
import com.whisnuys.qlinikku.Models.Dokter;
import com.whisnuys.qlinikku.Models.PersonDokter;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapterListDokter extends RecyclerView.Adapter<RecycleViewAdapterListDokter.ViewHolder> implements Filterable{
    ArrayList<PersonDokter> listDokter;
    ListDataDokter context;
    ArrayList<PersonDokter> listDokterSearch;

    //  Filter Data
    Filter setSearch = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<PersonDokter> filterDokter = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filterDokter.addAll(listDokterSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (PersonDokter item : listDokterSearch){
                    if(item.getSpesialis().toLowerCase().trim().contains(filterPattern)){
                        filterDokter.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterDokter;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listDokter.clear();
            listDokter.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public RecycleViewAdapterListDokter(ArrayList<PersonDokter> listDokter, ListDataDokter context){
        this.listDokter = listDokter;
        this.context = context;
        this.listDokterSearch = listDokter;
    }

    @Override
    public Filter getFilter() {
        return setSearch;
    }

    @NonNull
    @Override
    public RecycleViewAdapterListDokter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_dokter_design, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapterListDokter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final String NamaLengkap = listDokter.get(position).getNamaLengkap();
        final String Jeniskelamin = listDokter.get(position).getJenisKelamin();
        final String NoHP = listDokter.get(position).getNoTelepon();
        final String Spesialis = listDokter.get(position).getSpesialis();
        final String GambarDokter = listDokter.get(position).getGambar();

        holder.namaLengkap.setText(": Dr. " + NamaLengkap);
        holder.jenisKelamin.setText(": " + Jeniskelamin);
        holder.noHp.setText(": " + NoHP);
        holder.spesialis.setText(": " + Spesialis);

        if(isEmpty(GambarDokter)){
            holder.gambarDokter.setImageResource(R.drawable.avatarhome);
        } else {
            Glide.with(holder.itemView.getContext()).load(GambarDokter.trim()).into(holder.gambarDokter);
        }

        holder.listItem.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(final View v){
                final String[] action = {"Update", "Delete"};
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Apa yang akan anda pilih?");
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Bundle bundle = new Bundle();
                                bundle.putString("dataNamaDokter", listDokter.get(position).getNamaLengkap());
                                bundle.putString("dataJkDokter", listDokter.get(position).getJenisKelamin());
                                bundle.putString("dataNoHpDokter", listDokter.get(position).getNoTelepon());
                                bundle.putString("dataSpesialisDokter", listDokter.get(position).getSpesialis());
                                bundle.putString("dataGambarDokter", listDokter.get(position).getGambar());
                                bundle.putString("getPrimaryKey", listDokter.get(position).getUid());



                                Intent intent = new Intent(v.getContext(), AdminHome.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                break;

                            case 1:
                                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                                alert.setTitle("Apakah anda yakin menghapus data ini?");
                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DatabaseReference doctorRef = FirebaseDatabase.getInstance().getReference("Users").child(listDokter.get(position).getUid());
                                        doctorRef.removeValue();
                                        FirebaseDatabase.getInstance().getReference("Appointments").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot apptChild : snapshot.getChildren()){
                                                    Appointment appt = apptChild.getValue(Appointment.class);
                                                    if(appt.getDokterID().equals(listDokter.get(position).getUid())){
                                                        apptChild.getRef().removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

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
                });
                alert.create();
                alert.show();
                return true;
            }
        });

        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "Selected Dr. " + listDokter.get(position).getNamaLengkap(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(), DetailDokter.class);
                intent.putExtra("dokterID", listDokter.get(position).getUid());
                intent.putExtra("dataNamaDokter", listDokter.get(position).getNamaLengkap());
                intent.putExtra("dataJkDokter", listDokter.get(position).getJenisKelamin());
                intent.putExtra("dataNoHpDokter", listDokter.get(position).getNoTelepon());
                intent.putExtra("dataSpesialisDokter", listDokter.get(position).getSpesialis());
                intent.putExtra("dataGambarDokter", listDokter.get(position).getGambar());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDokter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView namaLengkap, jenisKelamin, noHp, spesialis;
        private ShapeableImageView gambarDokter;
        private LinearLayout listItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaLengkap = itemView.findViewById(R.id.namaDokter);
            jenisKelamin = itemView.findViewById(R.id.jkDokter);
            noHp = itemView.findViewById(R.id.noHpDokter);
            spesialis = itemView.findViewById(R.id.spesialisDokter);
            gambarDokter = itemView.findViewById(R.id.gambar_dokter);
            listItem = itemView.findViewById(R.id.list_item_dokter);
        }
    }
}
