package id.my.slametbsan.absensifirebase;

import java.io.Serializable;

public class Users implements Serializable {
    private String uid;
    private String nama;
    private String email;
    private String telepon;
    private Boolean admin;

    public Users(String nama, String email, String telepon, Boolean admin) {
        this.nama = nama;
        this.email = email;
        this.telepon = telepon;
        this.admin = admin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
