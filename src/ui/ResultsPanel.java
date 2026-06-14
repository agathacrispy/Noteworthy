package ui;


import model.PerformanceResult;
import model.PitchFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
//import java.io.FontFormatException;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ResultsPanel extends BackgroundPanel {

    private static final Random RNG = new Random();

    private static String pickMessage(String grade) {
        String[] msgs = switch (grade) {
            case "A+" -> new String[]{"oh wowww!!", "i guess you really are noteWORTHY", "awesome job!"};
            case "A" -> new String[]{"i guess you really are noteWORTHY", "slay", "you rock!!"};
            case "A-" -> new String[]{"you are almost noteWORTHY", "ouuu shii"};
            case "B+" -> new String[]{"okay okay, not bad", "i see you i see you"};
            case "B" -> new String[]{"okay okay, getting there", "average, very average"};
            case "B-" -> new String[]{"okay...", "ehhh... could be worse"};
            case "C+" -> new String[]{"ooouff... practice more?", "almost..."};
            case "C" -> new String[]{"good effort at least", "nice try..."};
            case "C-" -> new String[]{"nice try...", "ouch"};
            case "D" -> new String[]{"nice try... NOT", "ay u suck"};
            case "F" -> new String[]{"ay u suck", "that was cheeks", "yikes"};
            case "Eric" -> new String[]{"yikes", "eric would be proud"};
            default -> new String[]{""};
        };
        return msgs[RNG.nextInt(msgs.length)];
    }

    private static final float HOVER_SPEED = 0.02f;
    private static final float HOVER_AMPLITUDE = 6f;

    private final PerformanceResult result;
    private final BufferedImage gradeImg;
    private final String gradeMessage;
    private Font minecraftFont;
    private float hoverAngle = 0f;

    public ResultsPanel(PerformanceResult result, Runnable onBackToMenu) {
        this.result = result;
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("res/" + result.getGrade().toLowerCase() + ".png"));
        } catch (IOException ignored) {
        }
        gradeImg = img;
        gradeMessage = pickMessage(result.getGrade());

        Font menuFont;
        try {
            menuFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(20f);
            minecraftFont = menuFont.deriveFont(16f);
        } catch (FontFormatException | IOException e) {
            menuFont = new Font("Segoe UI", Font.PLAIN, 20);
            minecraftFont = new Font("Segoe UI", Font.PLAIN, 16);
        }

        setLayout(new BorderLayout());

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setOpaque(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(menuFont);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> onBackToMenu.run());


        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        topBar.add(backBtn, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);
    }

    @Override
    protected void tick() {
        hoverAngle += HOVER_SPEED;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int graphTop = getHeight() - getHeight() / 5;
        int imgCenterY = graphTop / 2;

        int imgH = 220;
        if (gradeImg != null) {
            int imgW = gradeImg.getWidth() * imgH / gradeImg.getHeight();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(gradeImg, cx - imgW / 2, imgCenterY - imgH / 2, imgW, imgH, null);
        }

        String msg = gradeMessage;
        if (msg != null && minecraftFont != null) {
            g2d.setFont(minecraftFont.deriveFont(22f));
            FontMetrics fm = g2d.getFontMetrics();
            int hoverY = Math.round((float) Math.sin(hoverAngle) * HOVER_AMPLITUDE);
            int textY = imgCenterY + imgH / 2 + 40 + hoverY;
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.drawString(msg, cx - fm.stringWidth(msg) / 2 + 1, textY + 1);
            g2d.setColor(Color.WHITE);
            g2d.drawString(msg, cx - fm.stringWidth(msg) / 2, textY);
        }

        drawPitchGraph(g2d);
    }


    private void drawPitchGraph(Graphics2D g2d) {
        int gh = getHeight() / 5;
        int gy = getHeight() - gh;
        int gx = 0;
        int gw = getWidth();

        g2d.setColor(new Color(0x10, 0x0f, 0x1a, 210));
        g2d.fillRect(gx, gy, gw, gh);

        List<PitchFrame> song = result.getSongPitches();
        List<PitchFrame> user = result.getUserPitches();
        if (song == null || user == null || song.isEmpty()) return;

        long totalMs = song.get(song.size() - 1).getMs();
        if (totalMs == 0) return;

        int step = Math.max(1, song.size() / 500);
        int midiMin = 36;
        int midiMax = 90;

        drawPitchLine(g2d, song, gx, gy, gw, gh, midiMin, midiMax, totalMs, step, new Color(80, 180, 80, 200));
        drawPitchLine(g2d, user, gx, gy, gw, gh, midiMin, midiMax, totalMs, step, new Color(220, 60, 60, 200));
    }

    private void drawPitchLine(Graphics2D g2d, List<PitchFrame> frames, int gx, int gy, int gw, int gh, int midiMin, int midiMax, long totalMs, int step, Color color) {
        if (frames == null || frames.isEmpty()) return;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));

        int n = frames.size();
        int prevX = -1, prevY = -1;
        int lastMidi = (midiMin + midiMax) / 2;

        for (int i = 0; i < n; i += step) {
            PitchFrame frame = frames.get(i);
            int midi = frame.getPitch();
            if (midi == -1) midi = lastMidi;
            else lastMidi = midi;
            int px = gx + (int) ((double) frame.getMs() / totalMs * gw);
            int py = gy + gh - (int) ((double) (midi - midiMin) / (midiMax - midiMin) * gh);
            if (prevX != -1) g2d.drawLine(prevX, prevY, px, py);
            prevX = px;
            prevY = py;
        }
    }
}
