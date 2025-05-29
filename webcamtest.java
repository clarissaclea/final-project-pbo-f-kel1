import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;

public class webcamtest {
    public static void main(String[] args) {
        Webcam webcam = Webcam.getDefault();

        if (webcam == null) {
            System.out.println("Tidak ada webcam ditemukan.");
            return;
        }
        
        webcam.setViewSize(new java.awt.Dimension(640, 480));
        WebcamPanel panel = new WebcamPanel(webcam);

        JFrame frame = new JFrame("Webcam Test");
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
