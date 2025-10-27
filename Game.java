// Game.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Game {

    private final BattleshipBoard board;
    private final CellButton[][] cells;
    private final JFrame frame;
    private final JLabel missLabel, strikeLabel, totalMissLabel, totalHitLabel;
    private int missCounter = 0;
    private int strikeCounter = 0;
    private int totalMiss = 0;
    private int totalHit = 0;
    private final ImageIcon iconWave, iconMiss, iconHit;

    public Game() {
        board = new BattleshipBoard();
        cells = new CellButton[BattleshipBoard.SIZE][BattleshipBoard.SIZE];

        // Load icons from resources folder
        iconWave = loadIcon("resources/wave.png", 56, 56);
        iconMiss = loadIcon("resources/miss.png", 56, 56);
        iconHit = loadIcon("resources/hit.png", 56, 56);

        frame = new JFrame("Lab07C_BattleShip");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(8,8));

        // Top title
        JLabel title = new JLabel("Battleship - Single Player", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        frame.add(title, BorderLayout.NORTH);

        // Center grid
        JPanel grid = new JPanel(new GridLayout(BattleshipBoard.SIZE, BattleshipBoard.SIZE, 2,2));
        grid.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        for (int r=0;r<BattleshipBoard.SIZE;r++) {
            for (int c=0;c<BattleshipBoard.SIZE;c++) {
                CellButton btn = new CellButton(r,c);
                btn.setPreferredSize(new Dimension(56,56));
                btn.setIcon(iconWave);
                btn.addActionListener(e -> handleClick((CellButton)e.getSource()));
                cells[r][c] = btn;
                grid.add(btn);
            }
        }
        frame.add(grid, BorderLayout.CENTER);

        // Right side counters and buttons
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        missLabel = new JLabel("Miss (streak): 0");
        strikeLabel = new JLabel("Strike: 0");
        totalMissLabel = new JLabel("Total Miss: 0");
        totalHitLabel = new JLabel("Total Hit: 0");

        missLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        strikeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        totalMissLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        totalHitLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        side.add(missLabel);
        side.add(Box.createVerticalStrut(8));
        side.add(strikeLabel);
        side.add(Box.createVerticalStrut(8));
        side.add(totalMissLabel);
        side.add(Box.createVerticalStrut(8));
        side.add(totalHitLabel);
        side.add(Box.createVerticalStrut(20));

        JButton playAgain = new JButton("Play Again");
        playAgain.addActionListener(e -> promptPlayAgain());
        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> doQuit());

        side.add(playAgain);
        side.add(Box.createVerticalStrut(8));
        side.add(quit);

        frame.add(side, BorderLayout.EAST);

        frame.pack();
        frame.setLocationRelativeTo(null);

        startNewGame();
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            java.io.File f = new java.io.File(path);
            if (!f.exists()) return null;
            ImageIcon ic = new ImageIcon(path);
            Image img = ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void startNewGame() {
        // reset counters and board
        missCounter = 0; strikeCounter = 0; totalMiss = 0; totalHit = 0;
        updateLabels();
        board.resetAndPlaceShips();
        // reset cells
        for (int r=0;r<BattleshipBoard.SIZE;r++) {
            for (int c=0;c<BattleshipBoard.SIZE;c++) {
                cells[r][c].setEnabled(true);
                cells[r][c].setIcon(iconWave);
                cells[r][c].setToolTipText(null);
            }
        }
        frame.setVisible(true);
    }

    private void handleClick(CellButton btn) {
        int r = btn.getRow(), c = btn.getCol();
        btn.setEnabled(false);
        if (board.hasShipAt(r,c)) {
            // hit
            int id = board.registerHit(r,c);
            totalHit++;
            missCounter = 0;
            btn.setIcon(iconHit);
            if (board.isSunk(id)) {
                JOptionPane.showMessageDialog(frame, "You sunk a ship of size " + getShipSize(id) + "!");
                if (totalHit >= 17) {
                    int res = JOptionPane.showConfirmDialog(frame, "You win! Play again?", "Victory", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION) startNewGame();
                    else System.exit(0);
                }
            }
        } else {
            // miss
            totalMiss++; missCounter++;
            btn.setIcon(iconMiss);
            if (missCounter >= 5) {
                strikeCounter++;
                missCounter = 0;
                if (strikeCounter >= 3) {
                    int res = JOptionPane.showConfirmDialog(frame, "You lost (3 strikes). Play again?", "Defeat", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION) startNewGame();
                    else System.exit(0);
                }
            }
        }
        updateLabels();
    }

    private int getShipSize(int shipId) {
        int count = 0;
        for (int r=0;r<BattleshipBoard.SIZE;r++) for (int c=0;c<BattleshipBoard.SIZE;c++) if (board.getShipId(r,c)==shipId) count++;
        return count;
    }

    private void updateLabels() {
        missLabel.setText("Miss (streak): " + missCounter);
        strikeLabel.setText("Strike: " + strikeCounter);
        totalMissLabel.setText("Total Miss: " + totalMiss);
        totalHitLabel.setText("Total Hit: " + totalHit);
    }

    private void promptPlayAgain() {
        int res = JOptionPane.showConfirmDialog(frame, "Are you sure you want to start a new game?", "Play Again", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) startNewGame();
    }

    private void doQuit() {
        int res = JOptionPane.showConfirmDialog(frame, "Quit the game?", "Quit", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game());
    }
}
