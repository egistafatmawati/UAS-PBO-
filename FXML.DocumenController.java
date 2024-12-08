package aplikasi;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FXMLDocumentController {

    // Variabel untuk permainan
    static double kecepatan = 3.0; // Kecepatan awal
    static final double maxKecepatan = 10.0; // Kecepatan maksimum
    static final double incrementKecepatan = 0.1; // Penambahan kecepatan setiap kali skor naik
    static int lebar = 20;    // Lebar grid
    static int tinggi = 20;   // Tinggi grid
    static int ukuranSisi = 25; // Ukuran setiap sisi grid
    static List<Segmen> ular = new ArrayList<>();
    static Arah arah = Arah.kiri; // Arah awal ular
    static boolean gameOver = false;
    static Random rand = new Random();
    static Makanan makanan;
    static Image kepalaUlar; // Gambar kepala ular
    static int skor = 0; // Variabel untuk menyimpan skor

    // Enum Arah
    public enum Arah {
        kiri, kanan, atas, bawah
    }

    // Kelas Segmen untuk mendefinisikan bagian tubuh ular
    public static class Segmen {
        private int x, y;

        public Segmen(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }

        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
    }

    @FXML
    private Canvas kanvas;  // Canvas untuk permainan

    @FXML
    private Button mulaiButton;  // Tombol untuk memulai permainan

    @FXML
    private Button keluarButton;  // Tombol untuk keluar dari permainan

    @FXML
    private Button mainLagiButton;  // Tombol untuk memulai ulang permainan

    @FXML
    private Button keluarGameOverButton;  // Tombol untuk keluar di menu Game Over

    @FXML
    private AnchorPane menuUtama; // Panel menu utama

    @FXML
    private AnchorPane gameScreen; // Panel permainan

    // Method untuk memulai permainan
    @FXML
    public void mulaiGame() {
        menuUtama.setVisible(false);  // Sembunyikan menu utama
        gameScreen.setVisible(true);  // Tampilkan layar permainan
        mainLagiButton.setVisible(false);  // Sembunyikan tombol main lagi
        keluarGameOverButton.setVisible(false);  // Sembunyikan tombol keluar di menu Game Over

        // Mengatur ulang permainan
        gameOver = false;
        skor = 0;
        kecepatan = 3.0; // Reset kecepatan awal
        ular.clear();
        ular.add(new Segmen(lebar / 2, tinggi / 2));
        makanan = new Makanan();
        arah = Arah.kiri;

        initialize();  // Inisialisasi permainan
    }

    // Method untuk keluar dari aplikasi
    @FXML
    public void keluar() {
        System.exit(0);  // Keluar dari aplikasi
    }

    // Method untuk memulai ulang permainan saat Game Over
    @FXML
    public void mainLagi() {
        mulaiGame(); // Memulai ulang permainan
    }

    // Inisialisasi permainan
    public void initialize() {
        kepalaUlar = new Image(getClass().getResource("/aplikasi/upmouth.png").toExternalForm());
        kanvas.setFocusTraversable(true);
        kanvas.requestFocus();  // Pastikan kanvas menerima fokus untuk menangkap input keyboard

        new AnimationTimer() {
            private long waktuTerakhir = 0;
            private final long interval = (long) (1_000_000_000 / kecepatan); // Interval berdasarkan kecepatan

            @Override
            public void handle(long now) {
                if (waktuTerakhir == 0 || now - waktuTerakhir >= interval) {
                    waktuTerakhir = now;
                    updateTampilan();
                }
            }
        }.start();

        kanvas.setOnKeyPressed(this::handleKeyPress); // Sambungkan event handler keyboard
    }

    // Update tampilan permainan
    public void updateTampilan() {
        GraphicsContext gc = kanvas.getGraphicsContext2D();

        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 100, 250);
            gc.setFont(new Font("", 30));
            gc.fillText("Skor Akhir: " + skor, 100, 300);

            // Tampilkan tombol "Main Lagi" dan "Keluar"
            mainLagiButton.setVisible(true);
            keluarGameOverButton.setVisible(true);

            return;
        }

        for (int i = ular.size() - 1; i > 0; i--) {
            ular.get(i).setX(ular.get(i - 1).getX());
            ular.get(i).setY(ular.get(i - 1).getY());
        }

        Segmen kepalaUlarSegmen = ular.get(0);
        switch (arah) {
            case atas:
                kepalaUlarSegmen.setY(kepalaUlarSegmen.getY() - 1);
                break;
            case bawah:
                kepalaUlarSegmen.setY(kepalaUlarSegmen.getY() + 1);
                break;
            case kiri:
                kepalaUlarSegmen.setX(kepalaUlarSegmen.getX() - 1);
                break;
            case kanan:
                kepalaUlarSegmen.setX(kepalaUlarSegmen.getX() + 1);
                break;
        }

        // Periksa tabrakan dengan makanan
        if (kepalaUlarSegmen.getX() == makanan.getX() && kepalaUlarSegmen.getY() == makanan.getY()) {
            ular.add(new Segmen(-1, -1));
            makanan = new Makanan();
            skor++; // Menambah skor setiap kali ular memakan makanan

            // Tingkatkan kecepatan jika skor bertambah
            if (kecepatan < maxKecepatan) {
                kecepatan += incrementKecepatan;
            }
        }

        // Periksa tabrakan dengan dinding
        if (kepalaUlarSegmen.getX() < 0 || kepalaUlarSegmen.getY() < 0 || 
            kepalaUlarSegmen.getX() >= lebar || kepalaUlarSegmen.getY() >= tinggi) {
            gameOver = true;
        }

        // Periksa tabrakan dengan tubuh sendiri
        for (int i = 1; i < ular.size(); i++) {
            if (kepalaUlarSegmen.getX() == ular.get(i).getX() && kepalaUlarSegmen.getY() == ular.get(i).getY()) {
                gameOver = true;
                break;
            }
        }

        // Gambar latar belakang
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, lebar * ukuranSisi, tinggi * ukuranSisi);

        // Gambar makanan
        gc.setFill(makanan.getWarna());
        gc.fillOval(makanan.getX() * ukuranSisi, makanan.getY() * ukuranSisi, ukuranSisi, ukuranSisi);

        // Gambar tubuh ular berbentuk oval
        for (int i = 1; i < ular.size(); i++) {
            Segmen segmen = ular.get(i);
            gc.setFill(Color.LIGHTBLUE);  // Mengubah warna tubuh ular menjadi biru terang
            gc.fillOval(segmen.getX() * ukuranSisi, segmen.getY() * ukuranSisi, ukuranSisi - 1, ukuranSisi - 1);
            gc.setFill(Color.BLUE);  // Mengubah warna tubuh ular menjadi biru
            gc.fillOval(segmen.getX() * ukuranSisi + 1, segmen.getY() * ukuranSisi + 1, ukuranSisi - 2, ukuranSisi - 2);
        }

        // Gambar kepala ular
        gc.drawImage(kepalaUlar, kepalaUlarSegmen.getX() * ukuranSisi, kepalaUlarSegmen.getY() * ukuranSisi, ukuranSisi, ukuranSisi);

        // Menampilkan skor di layar
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Skor: " + skor, 10, 30); // Menampilkan skor di pojok kiri atas
    }

    // Menangani input keyboard untuk mengubah arah ular
    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (gameOver) return;

        switch (event.getCode()) {
            case W:
                if (arah != Arah.bawah) {
                    arah = Arah.atas;
                }
                break;
            case A:
                if (arah != Arah.kanan) {
                    arah = Arah.kiri;
                }
                break;
            case S:
                if (arah != Arah.atas) {
                    arah = Arah.bawah;
                }
                break;
            case D:
                if (arah != Arah.kiri) {
                    arah = Arah.kanan;
                }
                break;
        }
    }
}
