package com.whisnuys.qlinikku;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
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
import com.whisnuys.qlinikku.Models.Dokter;
import com.whisnuys.qlinikku.Models.PersonDokter;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapterListDokter extends RecyclerView.Adapter<RecycleViewAdapterListDokter.ViewHolder> implements Filterable {
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
                    if(item.getNamaLengkap().toLowerCase().contains(filterPattern) || item.getSpesialis().contains(filterPattern)){
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

        holder.namaLengkap.setText(": " + NamaLengkap);
        holder.jenisKelamin.setText(": " + Jeniskelamin);
        holder.noHp.setText(": " + NoHP);
        holder.spesialis.setText(": " + Spesialis);

        if(isEmpty(GambarDokter)){
            holder.gambarDokter.setImageResource(R.drawable.avatarhome);
        } else {
            Glide.with(holder.itemView.getContext()).load(GambarDokter.trim()).into(holder.gambarDokter);
        }

//        holder.listItem.setOnLongClickListener(new View.OnLongClickListener(){
//            @Override
//            public boolean onLongClick(final View v){
//                final String[] action = {"Update", "Delete"};
//                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
//                alert.setTitle("Apa yang akan anda pilih?");
//                alert.setItems(action, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        switch (i){
//                            case 0:
//                                Bundle bundle = new Bundle();
//                                bundle.putString("dataNIM", listMahasiswa.get(position).getNim());
//                                bundle.putString("dataNama", listMahasiswa.get(position).getNama());
//                                bundle.putString("dataFakultas", listMahasiswa.get(position).getFakultas());
//                                bundle.putString("dataGol_dar", listMahasiswa.get(position).getHasilgol());
//                                bundle.putString("dataJenis_kelamin", listMahasiswa.get(position).getRjeniskelamin());
//                                bundle.putString("dataTanggal", listMahasiswa.get(position).getTgllahir());
//                                bundle.putString("dataEmail", listMahasiswa.get(position).getCrudemail());
//                                bundle.putString("dataNohp", listMahasiswa.get(position).getNohp());
//                                bundle.putString("dataIpk", listMahasiswa.get(position).getIpk());
//                                bundle.putString("dataAlamat", listMahasiswa.get(position).getAlamat());
//                                bundle.putString("dataProdi", listMahasiswa.get(position).getProdi());
//                                bundle.putString("dataGambar", listMahasiswa.get(position).getGambar());
//                                bundle.putString("dataPrimaryKey", listMahasiswa.get(position).getKey());
//
//                                Intent intent = new Intent(v.getContext(), Update.class);
//                                intent.putExtras(bundle);
//                                context.startActivity(intent);
//                                break;
//
//                            case 1:
//                                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
//                                alert.setTitle("Apakah anda yakin menghapus data ini?");
//                                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("Admin")
//                                                .child("Mahasiswa").child(listMahasiswa.get(position).getKey());
//                                        mPostReference.removeValue();
//                                    }
//                                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        return;
//                                    }
//                                });
//                                alert.create();
//                                alert.show();
//                        }
//                    }
//                });
//                alert.create();
//                alert.show();
//                return true;
//            }
//        });
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
