package id.my.slametbsan.absensifirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateUserActivity extends AppCompatActivity {
    private String uid;
    private EditText nama, telepon, email;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        nama    = findViewById(R.id.editNama);
        telepon = findViewById(R.id.editTelepon);
        email   = findViewById(R.id.editEmail);

        //ambil referensi Firebase
        database = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //uid     = getIntent().getStringExtra("uid");
        //email.setText(getIntent().getStringExtra("email"));
        uid     = firebaseAuth.getUid();
        email.setText(firebaseAuth.getCurrentUser().getEmail());

        Button btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buat variabel lokal
                String n = nama.getText().toString().trim();
                String m = email.getText().toString().trim();
                String t = telepon.getText().toString().trim();

                if(TextUtils.isEmpty(n) && TextUtils.isEmpty(t) && TextUtils.isEmpty(m)){
                    Toast.makeText(CreateUserActivity.this, "Semua harus diisi", Toast.LENGTH_LONG).show();

                } else {
                    //susun data sesuai model
                    Users users = new Users(n, m, t, false);
                    //akses ke (table) users
                    database.child("users")
                            .child(uid) //buat child (primary key berdasar uid auth)
                            .setValue(users)    //simpan data di dalamnya
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(CreateUserActivity.this, "Data user berhasil disimpan", Toast.LENGTH_SHORT).show();
                                    //lanjut ke dashboard
                                    startActivity(new Intent(CreateUserActivity.this, DashboardActivity.class));
                                    finish();
                                }
                            });
                }
            }
        });

    }
}
