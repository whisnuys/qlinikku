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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whisnuys.qlinikku.Models.Dokter;

import java.util.ArrayList;
import java.util.List;

public class PilihDokterAdapter extends RecyclerView.Adapter<PilihDokterAdapter.ViewHolder> implements Filterable {
    ArrayList<Dokter> listDokter;
    PilihDokter context;
    ArrayList<Dokter> listDokterSearch;

    //  Filter Data
    Filter setSearch = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Dokter> filterDokter = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filterDokter.addAll(listDokterSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Dokter item : listDokterSearch){
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

    public PilihDokterAdapter(ArrayList<Dokter> listDokter, PilihDokter context){
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
    public PilihDokterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_dokter_design, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull PilihDokterAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailPilihDokter.class);

                intent.putExtra("dokterNama", listDokter.get(position).getNamaLengkap());
                intent.putExtra("dokterJK", listDokter.get(position).getJenisKelamin());
                intent.putExtra("dokterSpesialis", listDokter.get(position).getSpesialis());
                intent.putExtra("dokterNoHp", listDokter.get(position).getNoTelepon());
                intent.putExtra("dokterGambar", listDokter.get(position).getGambar());
                intent.putExtra("dokterID", listDokter.get(position).getUid());

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
