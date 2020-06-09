package id.my.slametbsan.absensifirebase;

public class Absensi {
    private String waktu;
    private String latitude;
    private String longitude;

    public Absensi() {
    }

    public Absensi(String waktu, String latitude, String longitude) {
        this.waktu = waktu;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
