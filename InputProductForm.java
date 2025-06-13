// File: InputProductForm.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Date;
import java.util.List; 
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDate; 
import java.time.ZoneId; 
import java.time.format.DateTimeFormatter; 
import java.time.temporal.ChronoUnit; 
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;
import javax.imageio.ImageIO; //  import ini untuk ImageIO
import java.text.SimpleDateFormat;
import java.util.Calendar; 

// import github
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class InputProductForm extends JFrame {
    // --- PENAMBAHAN DEKLARASI tfStok ---
    private JTextField tfBarcode, tfNama, tfHarga, tfKategori, tfStok;
    private JSpinner spinnerProduksi, spinnerExpired;
    private ProductDAO productDAO;
    private JLabel photoLabel;
    private File selectedPhoto;

    private Webcam webcam = null;
    private WebcamPanel webcamPanel = null;
    private Thread barcodeScannerThread = null;
    private JButton startWebcamButton, stopWebcamButton, captureButton;
    private JPanel webcamContainer;

    public InputProductForm(ProductDAO productDAO) {
        this.productDAO = productDAO;

        setTitle("Input Produk Baru");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Form Tambah Produk");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(250, 10, 300, 30);
        panel.add(titleLabel);

        JLabel lblBarcode = new JLabel("Barcode:");
        lblBarcode.setBounds(30, 50, 100, 25);
        panel.add(lblBarcode);

        tfBarcode = new JTextField();
        tfBarcode.setBounds(150, 50, 200, 25);
        panel.add(tfBarcode);

        JButton btnScanBarcode = new JButton("Scan Barcode");
        btnScanBarcode.setBounds(370, 50, 150, 25);
        panel.add(btnScanBarcode);

        JLabel lblNama = new JLabel("Nama Produk:");
        lblNama.setBounds(30, 90, 100, 25);
        panel.add(lblNama);

        tfNama = new JTextField();
        tfNama.setBounds(150, 90, 200, 25);
        panel.add(tfNama);

        JLabel lblHarga = new JLabel("Harga:");
        lblHarga.setBounds(30, 130, 100, 25);
        panel.add(lblHarga);

        tfHarga = new JTextField();
        tfHarga.setBounds(150, 130, 200, 25);
        panel.add(tfHarga);

        JLabel lblKategori = new JLabel("Kategori:");
        lblKategori.setBounds(30, 170, 100, 25);
        panel.add(lblKategori);

        tfKategori = new JTextField();
        tfKategori.setBounds(150, 170, 200, 25);
        panel.add(tfKategori);

        JLabel lblProduksi = new JLabel("Tanggal Produksi:");
        lblProduksi.setBounds(30, 210, 120, 25);
        panel.add(lblProduksi);

        spinnerProduksi = new JSpinner(new SpinnerDateModel());
        spinnerProduksi.setBounds(150, 210, 200, 25);
        JSpinner.DateEditor editorProduksi = new JSpinner.DateEditor(spinnerProduksi, "yyyy-MM-dd");
        spinnerProduksi.setEditor(editorProduksi);
        panel.add(spinnerProduksi);

        JLabel lblExpired = new JLabel("Tanggal Expired:");
        lblExpired.setBounds(30, 250, 120, 25);
        panel.add(lblExpired);

        spinnerExpired = new JSpinner(new SpinnerDateModel());
        spinnerExpired.setBounds(150, 250, 200, 25);
        JSpinner.DateEditor editorExpired = new JSpinner.DateEditor(spinnerExpired, "yyyy-MM-dd");
        spinnerExpired.setEditor(editorExpired);
        panel.add(spinnerExpired);

        JLabel lblPhoto = new JLabel("Foto Produk:");
        lblPhoto.setBounds(30, 290, 100, 25);
        panel.add(lblPhoto);

        photoLabel = new JLabel("Tidak ada foto");
        photoLabel.setBounds(150, 290, 200, 150);
        panel.add(photoLabel);

        JButton btnPilihFoto = new JButton("Pilih Foto");
        btnPilihFoto.setBounds(370, 290, 150, 25);
        panel.add(btnPilihFoto);

        startWebcamButton = new JButton("Mulai Webcam");
        startWebcamButton.setBounds(150, 460, 120, 25);
        panel.add(startWebcamButton);

        stopWebcamButton = new JButton("Berhenti Webcam");
        stopWebcamButton.setBounds(280, 460, 150, 25);
        panel.add(stopWebcamButton);

        captureButton = new JButton("Ambil Gambar");
        captureButton.setBounds(440, 460, 150, 25);
        panel.add(captureButton);

        webcamContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        webcamContainer.setBounds(150, 500, 400, 300); // ukuran lebih kecil dan tengah
        panel.add(webcamContainer);

        JButton btnSimpan = new JButton("Simpan Produk");
        btnSimpan.setBounds(200, 620, 130, 30);
        panel.add(btnSimpan);

        JButton btnBatal = new JButton("Batal");
        btnBatal.setBounds(350, 620, 100, 30);
        panel.add(btnBatal);

        add(panel);

        // TODO: Tambahkan fungsi webcam dan event handling sesuai kebutuhan
    }
}
