package id.my.slametbsan.absensifirebase;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterAbsensi extends RecyclerView.Adapter<AdapterAbsensi.ViewHolder> {
    private ArrayList<AbsensiRv> absensiList;

    AdapterAbsensi(ArrayList<AbsensiRv> absensiList, Context context) {
        this.absensiList = absensiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_absen_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //konversi tanggal dari Realtime Database
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tgl = "";
        Date now = new Date();
        try{
            Date d = sdf.parse(absensiList.get(position).getTanggal());

            //konversi balik ke format yg diinginkan; Hari, tanggal
            sdf.applyPattern("EEEE, dd MMM yyyy");
            tgl = sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String n = sdf.format(now);

        final String strHadir = "Hadir: " + adaData(absensiList.get(position).getHadir());
        final String strPulang = "Pulang: " + adaData(absensiList.get(position).getPulang());

        final String strLat1 = adaData(absensiList.get(position).getHadirLat());
        final String strLat2 = adaData(absensiList.get(position).getPulangLat());
        final String strLong1 = adaData(absensiList.get(position).getHadirLong());
        final String strLong2 = adaData(absensiList.get(position).getPulangLong());

        final String lok1 = "Lat:  " + strLat1 + "\nLong: " + strLong1;
        final String lok2 = "Lat:  " + strLat2 + "\nLong: " + strLong2;

        //periksa apakah tanggal dari Realtime Database sama dengan hari ini
        if(tgl.equals(n)){
            //jika YA, beri warna biru
            holder.tvHariAbsen.setTextColor(Color.BLUE);
        }

        //tampilkan ke RecycleView
        holder.tvHariAbsen.setText(tgl);
        holder.tvHadir.setText(strHadir);
        holder.tvLokasiHadir.setText(lok1);
        holder.tvPulang.setText(strPulang);
        holder.tvLokasiPulang.setText(lok2);
    }

    /*  method untuk memeriksa data yg diperoleh dari Realtime Database
        jika "ada data" tampilkan data tersebut.
        jika "kosong/null" tampilkan "--"
    * */

    private String adaData(String teks){
        if(TextUtils.isEmpty(teks)){
            teks = "--";
        }
        return teks;
    }

    @Override
    public int getItemCount() {
        return absensiList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHariAbsen;
        TextView tvHadir, tvLokasiHadir;
        TextView tvPulang, tvLokasiPulang;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHariAbsen = itemView.findViewById(R.id.tvHariAbsen);
            tvHadir = itemView.findViewById(R.id.tvHadir);
            tvLokasiHadir = itemView.findViewById(R.id.tvLokasiHadir);
            tvPulang = itemView.findViewById(R.id.tvPulang);
            tvLokasiPulang = itemView.findViewById(R.id.tvLokasiPulang);
        }
    }
}
