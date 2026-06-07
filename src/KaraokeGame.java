import model.PerformanceResult;
import ui.GameplayPanel;
import ui.MainMenuPanel;
import ui.ResultsPanel;
import ui.SettingsPanel;
import ui.TitlePanel;

import javax.swing.*;

// main entry point - owns the frame and controls all panel transitions
// flow: TitlePanel -> MainMenuPanel -> GameplayPanel -> ResultsPanel
public class KaraokeGame {

    private static JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Noteworthy");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            showTitle();
            frame.setVisible(true);
        });
    }

    // replaces the current panel in the frame
    private static void swap(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
    }

    public static void showTitle() {
        swap(new TitlePanel(() -> showMainMenu()));
    }

    public static void showMainMenu() {
        swap(new MainMenuPanel(
            song -> showGameplay(song),
            () -> showSettings()
        ));
    }

    public static void showSettings() {
        swap(new SettingsPanel(() -> showMainMenu()));
    }

    public static void showGameplay(String song) {
        // onFinished is called by GameplayPanel when the backing track ends
        swap(new GameplayPanel(song, result -> showResults(result)));
    }

    // result is null until scoring is implemented
    public static void showResults(PerformanceResult result) {
        swap(new ResultsPanel(result, () -> showMainMenu()));
    }
}
