package id.my.slametbsan.absensifirebase;

public class AbsensiRv {
    private String tanggal;
    private String hadir;
    private String hadirLat;
    private String hadirLong;
    private String pulang;
    private String pulangLat;
    private String pulangLong;

    public AbsensiRv() {
    }

    public AbsensiRv(String tanggal, String hadir, String hadirLat, String hadirLong,
                     String pulang, String pulangLat, String pulangLong) {
        this.tanggal = tanggal;
        this.hadir = hadir;
        this.hadirLat = hadirLat;
        this.hadirLong = hadirLong;
        this.pulang = pulang;
        this.pulangLat = pulangLat;
        this.pulangLong = pulangLong;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getHadir() {
        return hadir;
    }

    public void setHadir(String hadir) {
        this.hadir = hadir;
    }

    public String getHadirLat() {
        return hadirLat;
    }

    public void setHadirLat(String hadirLat) {
        this.hadirLat = hadirLat;
    }

    public String getHadirLong() {
        return hadirLong;
    }

    public void setHadirLong(String hadirLong) {
        this.hadirLong = hadirLong;
    }

    public String getPulang() {
        return pulang;
    }

    public void setPulang(String pulang) {
        this.pulang = pulang;
    }

    public String getPulangLat() {
        return pulangLat;
    }

    public void setPulangLat(String pulangLat) {
        this.pulangLat = pulangLat;
    }

    public String getPulangLong() {
        return pulangLong;
    }

    public void setPulangLong(String pulangLong) {
        this.pulangLong = pulangLong;
    }
}
