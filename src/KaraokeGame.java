import ui.GameplayPanel;
import ui.MainMenuPanel;
import ui.SettingsPanel;
import ui.TestPanel;

import java.util.ArrayList;
import javax.swing.*;
import javax.swing.SwingUtilities;

public class KaraokeGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Noteworthy");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);

            //frame.add(new MainMenuPanel(song -> {
            //    frame.getContentPane().removeAll();
            //    frame.getContentPane().add(new GameplayPanel(song));
            //    frame.revalidate();
            //    frame.repaint();
            //}));

            frame.add(new SettingsPanel());
            //frame.add(new TestPanel());
            frame.setVisible(true);
        });
    }
}
