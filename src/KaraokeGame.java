import ui.GameplayPanel;
import ui.MainMenuPanel;
import java.awt.*;
import javax.swing.*;
import javax.swing.SwingUtilities;

public class KaraokeGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Noteworthy");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            //frame.add(new MainMenuPanel());
            frame.add(new GameplayPanel());
            frame.setVisible(true);
        });
    }
}
