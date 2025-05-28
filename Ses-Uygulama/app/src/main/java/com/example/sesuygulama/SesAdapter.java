package com.example.sesuygulama;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class SesAdapter extends RecyclerView.Adapter<SesAdapter.SesViewHolder> {

    private Context context;
    private List<Ses> sesListesi;
    private int seciliKategoriId;
    public SesAdapter(Context context, List<Ses> sesListesi, int seciliKategoriId) {
        this.context = context;
        this.sesListesi = sesListesi;
        this.seciliKategoriId = seciliKategoriId;
    }

    @NonNull
    @Override
    public SesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ses, parent, false);
        return new SesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SesViewHolder holder, int position) {
        Ses ses = sesListesi.get(position);
        holder.txtAd.setText(ses.getAd());

        // Kategori adını göster
        holder.txtKategori.setText(MainActivity.kategoriIsimleri[ses.getKategoriId()]);

        if (seciliKategoriId == 0) {
            holder.txtKategori.setVisibility(View.VISIBLE);
        } else {
            holder.txtKategori.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(ses.getDosyaYolu());
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public int getItemCount() {
        return sesListesi.size();
    }

    public void guncelle(List<Ses> yeniListe, int seciliKategoriId) {
        this.sesListesi = yeniListe;
        this.seciliKategoriId = seciliKategoriId;
        notifyDataSetChanged();
    }

    public static class SesViewHolder extends RecyclerView.ViewHolder {
        TextView txtAd, txtKategori;

        public SesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAd = itemView.findViewById(R.id.txtSesAdi);
            txtKategori = itemView.findViewById(R.id.txtKategoriAdi);
        }
    }
}
