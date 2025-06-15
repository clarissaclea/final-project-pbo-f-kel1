import javax.swing.*;
import java.awt.*;

public class CustomPanel extends JPanel {
    private Image backgroundImage;

    public CustomPanel(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
        setOpaque(false); // Ini penting supaya background tidak menutupi komponen lain
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);

            int x = (getWidth() - imgWidth) / 2;
            int y = (getHeight() - imgHeight) / 2;

            g.drawImage(backgroundImage, x, y, this);
        }
    }
}
