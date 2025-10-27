// CellButton.java
import javax.swing.*;

public class CellButton extends JButton {
    private final int row, col;

    public CellButton(int row, int col) {
        super();
        this.row = row; this.col = col;
        setFocusPainted(false);
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
}
