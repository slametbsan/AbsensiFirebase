package id.my.slametbsan.absensifirebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    //definisikan object EditText dan Button
    EditText inputEmail, inputPassword;
    Button btnLogin, btnDaftar;

    //deklarasi instance FirebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inisialisasi FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //inisialisasi object ke komponen UI
        inputEmail      = findViewById(R.id.inputEmail);
        inputPassword   = findViewById(R.id.inputPassword);
        btnLogin        = findViewById(R.id.btnLogin);
        btnDaftar       = findViewById(R.id.btnDaftar);

        //setOnClickListener pada btnLogin
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aksi ketika btnLogin di-klik
                userLogin();
            }
        });

        //setOnClickListener pada btnDaftar
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //panggil method registerUser
                registerUser();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //periksa apakah user sudah login
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }
    }

    public void registerUser(){
        //buat variabel lokal untuk menampung inputan dari Form
        String email    = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        //periksa inputan email, tampilkan toast jika kosong
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Email harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        //periksa inputan password, tampilkan toast jika kosong
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        //jika input email dan password oke, lanjutkan dengan FirebaseAuth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Registrasi selesai", Toast.LENGTH_SHORT).show();
                            createUser();
                        } else {
                            Toast.makeText(LoginActivity.this, "Registrasi gagal, email sudah digunakan", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void createUser(){
        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = reference.child("users").child(uid);

        uidRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Intent intent = new Intent(LoginActivity.this, CreateUserActivity.class);
                    startActivity(intent);
                    //finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DB_ERR", databaseError.getMessage());
            }
        });
    }

    private void userLogin(){
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        //periksa isian email dan password tidak boleh kosong
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Masukkan email!", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Masukkan password!", Toast.LENGTH_LONG).show();
            return;
        }

        //proses login Firebase
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //jika sukses
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Login gagal, pastikan User dan Password benar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}