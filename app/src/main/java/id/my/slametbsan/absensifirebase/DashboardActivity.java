package id.my.slametbsan.absensifirebase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DashboardActivity extends AppCompatActivity implements LocationListener {
    //definisi object firebase
    private static FirebaseDatabase firebaseDatabase;
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    String uid;

    //definisi komponen UI
    TextView tvNama, tvEmail, tvHariTanggal, tvLokasi;
    Button btnHadir, btnPulang;
    ImageButton btnKamera, btnEdit, btnLogout;
    ImageView ivFotoProfil;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Absensi> dftAbsensi; //list absensi
    private ArrayList<AbsensiRv> arrAbsensiRv; //list untuk RecclerView

    //
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //inisialisasi komponen UI
        tvNama = findViewById(R.id.tvNama);
        tvEmail = findViewById(R.id.tvEmail);
        tvHariTanggal = findViewById(R.id.tvHariTanggal);
        tvLokasi = findViewById(R.id.tvLokasi);
        ivFotoProfil = findViewById(R.id.fotoProfil);

        //inisialisasi object firebase
        if(firebaseDatabase == null){
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        database = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();

        //inisialisasi referensi ke Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        //cek file foto sudah ada atau belum
        String lokasiDir = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        String namaFileFoto = lokasiDir + "/" + "ABSEN_" + uid + ".jpg";
        Log.e("LOK", namaFileFoto);
        File cekFile = new File(namaFileFoto);
        if(cekFile.exists()){
            //file lokal ditemukan
            ivFotoProfil.setImageURI(Uri.fromFile(cekFile));
        } else {
            //file lokal tidak ditemukan, cek online
            final StorageReference fileOnline = storageReference.child("profil/" + "ABSEN_" + uid + ".jpg");
            fileOnline.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(ivFotoProfil);

                    //simpan di lokal biar gampang berikutnya
                    File lokasiDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File gbrLokal = new File(lokasiDir, "ABSEN_" + uid + ".jpg");
                    fileOnline.getFile(gbrLokal).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(DashboardActivity.this, "File sudah diunduh", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DashboardActivity.this, "Ada masalah: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        //inisialisasi button
        btnLogout = findViewById(R.id.btnLogout);
        btnHadir  = findViewById(R.id.btnHadir);
        btnPulang = findViewById(R.id.btnPulang);
        btnKamera = findViewById(R.id.btnKamera);

        //ambil data dari realtime database utk current user
        database.child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //tampilkan di sini
                //tvNama.setText(uid);
                tvNama.setText(dataSnapshot.child("nama").getValue().toString());
                tvEmail.setText(dataSnapshot.child("email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //RecyclerView
        recyclerView = findViewById(R.id.rvAbsensi);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //ambil data absensi berdasar UID
        database
                .child("absensi")
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrAbsensiRv = new ArrayList<>();

                for (DataSnapshot dtAbsensi : dataSnapshot.getChildren()){
                    AbsensiRv absensiRv = new AbsensiRv();
                    //masukkan key sbg tanggal
                    absensiRv.setTanggal(dtAbsensi.getKey());

                    for(DataSnapshot detilAbsen : dtAbsensi.getChildren()){
                        Absensi absensi = detilAbsen.getValue(Absensi.class);
                        String key = detilAbsen.getKey();

                        //IF key = hadir ELSE key = pulang
                        if(key.equals("hadir")){
                            absensiRv.setHadir(absensi.getWaktu());
                            absensiRv.setHadirLat(absensi.getLatitude());
                            absensiRv.setHadirLong(absensi.getLongitude());
                        } else {
                            absensiRv.setPulang(absensi.getWaktu());
                            absensiRv.setPulangLat(absensi.getLatitude());
                            absensiRv.setPulangLong(absensi.getLongitude());
                        }
                    }
                    arrAbsensiRv.add(absensiRv);
                }

                //urutkan secara LIFO
                Collections.sort(arrAbsensiRv, new Comparator<AbsensiRv>() {
                    @Override
                    public int compare(AbsensiRv o1, AbsensiRv o2) {
                        //return o1.getTanggal().compareTo(o2.getTanggal());
                        return o2.getTanggal().compareTo(o1.getTanggal());
                    }
                });

                adapter = new AdapterAbsensi(arrAbsensiRv, DashboardActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //fungsikan btnLogout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        //fungsikan btnKamera
        btnKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek dulu ijin akses
                //Toast.makeText(DashboardActivity.this, "Kamera!", Toast.LENGTH_SHORT).show();
                cekAksesKamera();
            }
        });

        //fungsikan btnHadir
        btnHadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAbsen("hadir");
            }
        });

        //fungsikan btnPulang
        btnPulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAbsen("pulang");
            }
        });

        //tampilkan hari dan tanggal
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd MMM yyyy");
        format.setTimeZone(TimeZone.getDefault());
        String tglSekarang = format.format(calendar.getTime());
        tvHariTanggal.setText(tglSekarang);

        //tampilkan lokasi berdasar gps
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String mprovider = locationManager.getBestProvider(criteria, false);
        if(mprovider != null && !mprovider.equals("")){
            if(ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                //request permissin
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 15000, 1, this);

            if(location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "No location provide found", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    //definisikan variabel untuk longitude dan latitude GPS
    double longitude;
    double latitude;

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        String lokasi = "Longitude/Bujur: " + longitude + ", Latitude/Lintang: " + latitude;
        //tvLokasi.setText(lokasi);

        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if(addresses.isEmpty()){
                //tampilkan koordinat jika alamat tidak ditemukan
                tvLokasi.setText(lokasi);
            } else {
                if(addresses.size() > 0){
                    //tampilkan alamat jika ditemukan
                    tvLokasi.setText(addresses.get(0).getAddressLine(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void doAbsen(final String status){
        //tampilkan hari dan tanggal
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat fmtTgl = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat fmtJam = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String tgl = fmtTgl.format(calendar.getTime());
        String jam = fmtJam.format(calendar.getTime());

        Absensi absensi = new Absensi(jam, String.valueOf(latitude), String.valueOf(longitude));

        database.child("absensi")
                .child(uid)
                .child(tgl)
                .child(status)
                .setValue(absensi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DashboardActivity.this,
                                "Absensi " + status + " berhasil!", Toast.LENGTH_SHORT).show();
                        //tampilkan di recyclerview di dashboard
                    }
                });
    }

    public static final int PERMISSION_CODE = 101;
    public void cekAksesKamera(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //akses belum diberikan, minta user untuk ijin akses
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
        } else {
            //Toast.makeText(this, "Kamera Lagi", Toast.LENGTH_SHORT).show();
            ambilFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this, "Siap-siap foto", Toast.LENGTH_SHORT).show();
                ambilFoto();
            } else {
                Toast.makeText(DashboardActivity.this, "Akses kamera diperlukan untuk ambil gambar profil", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static final int CAMERA_REQUEST_CODE = 102;
    private void ambilFoto() {
        Intent intenFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //gunakan kamera depan
        intenFoto.putExtra("android.intent.extras.CAMERA_FACING", 1);

        if(intenFoto.resolveActivity(getPackageManager()) != null){
            File fileFoto = null;
            try {
                fileFoto = buatFileGambar();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //lanjut setelah fileFoto dibuat
            if(fileFoto != null){
                Toast.makeText(this, "siap-siap ya...", Toast.LENGTH_SHORT).show();
                Uri fotoURI = FileProvider.getUriForFile(this, "id.my.slametbsan.absensifirebase", fileFoto);
                intenFoto.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI);
                startActivityForResult(intenFoto, CAMERA_REQUEST_CODE);
            }
        }
    }

    String lokasiFile;
    private File buatFileGambar() throws IOException {
        String namaFile = "ABSEN_" + uid;
        File lokasiDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File gambar = new File(lokasiDir, namaFile + ".jpg");

        lokasiFile = gambar.getAbsolutePath();
        Log.e("LOK2", lokasiFile);
        return gambar;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            File f = new File(lokasiFile);
            ivFotoProfil.setImageResource(0);
            ivFotoProfil.setImageURI(Uri.fromFile(f));

            //handleImageOrientation
            orientasiGambar();
            //unggah ke Firebase Storage
            unggahKeFirebase(f.getName(), Uri.fromFile(f));
        }
    }

    //method untuk upload ke Firebase Storage
    private void unggahKeFirebase(String name, Uri fileURI) {
        final StorageReference gambar = storageReference.child("profil/" + name);
        gambar.putFile(fileURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //jika upload sukses, download gambar untuk ditampilkan
                Toast.makeText(DashboardActivity.this, "Foto profil berhasil diunggah",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //jika gagal unggah
                Toast.makeText(DashboardActivity.this, "Foto profil gagal diunggah",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //supaya tampilan hasil download benar tidak rotated
    private void orientasiGambar(){
        Bitmap bitmap = BitmapFactory.decodeFile(lokasiFile);
        Bitmap rotatedBmp;

        try{
            ExifInterface ei = new ExifInterface(lokasiFile);
            int orientasi = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientasi){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBmp = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBmp = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBmp = rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBmp = bitmap;
            }

            if(rotatedBmp != bitmap){
                FileOutputStream fout = new FileOutputStream(lokasiFile);

                //compress biar gak anget
                rotatedBmp.compress(Bitmap.CompressFormat.JPEG, 30, fout);
                fout.flush();
                fout.close();
            }

            bitmap.recycle();
            rotatedBmp.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
