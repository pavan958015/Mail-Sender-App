import java.awt.*;
import javax.swing.*;

public class GradientPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        Color c1 = new Color(52, 152, 219);   // Blue
        Color c2 = new Color(155, 89, 182);   // Purple

        GradientPaint gp = new GradientPaint(
                0, 0, c1,
                getWidth(), getHeight(), c2
        );

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}