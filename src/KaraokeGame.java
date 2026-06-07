import ui.GameplayPanel;
import ui.MainMenuPanel;
import ui.SettingsPanel;
import ui.TestPanel;
import model.Song;

import javax.swing.*;

public class KaraokeGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Noteworthy");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);

            showMainMenu(frame);

            frame.setVisible(true);
        });
    }

    private static void showMainMenu(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new MainMenuPanel(song -> showGameplay(frame, song)));
        frame.revalidate();
        frame.repaint();
    }

    private static void showGameplay(JFrame frame, Song song) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new GameplayPanel(song));
        frame.revalidate();
        frame.repaint();
    }
}
