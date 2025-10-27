// BattleshipBoard.java
import java.util.*;

public class BattleshipBoard {
    public static final int SIZE = 10;
    private int[][] shipId; // 0 = empty, >0 ship id
    private int[] shipSize;
    private int[] shipHits;
    private int shipsCount;

    public BattleshipBoard() {
        shipId = new int[SIZE][SIZE];
        shipsCount = 0;
    }

    public void resetAndPlaceShips() {
        for (int r=0;r<SIZE;r++) for (int c=0;c<SIZE;c++) shipId[r][c]=0;
        int[] sizes = new int[]{5,4,3,3,2};
        shipSize = new int[sizes.length+1];
        shipHits = new int[sizes.length+1];
        shipsCount = sizes.length;
        Random rnd = new Random();
        int id=1;
        for (int s : sizes) {
            boolean placed=false;
            for (int attempt=0; attempt<1000 && !placed; attempt++) {
                boolean horiz = rnd.nextBoolean();
                int r = rnd.nextInt(SIZE);
                int c = rnd.nextInt(SIZE);
                if (horiz) {
                    if (c + s > SIZE) continue;
                    boolean ok=true;
                    for (int k=0;k<s;k++) if (shipId[r][c+k]!=0) { ok=false; break; }
                    if (!ok) continue;
                    for (int k=0;k<s;k++) shipId[r][c+k]=id;
                    placed=true;
                } else {
                    if (r + s > SIZE) continue;
                    boolean ok=true;
                    for (int k=0;k<s;k++) if (shipId[r+k][c]!=0) { ok=false; break; }
                    if (!ok) continue;
                    for (int k=0;k<s;k++) shipId[r+k][c]=id;
                    placed=true;
                }
            }
            shipSize[id] = s;
            shipHits[id] = 0;
            id++;
        }
    }

    public int getShipId(int row, int col) { return shipId[row][col]; }

    public boolean hasShipAt(int row, int col) { return shipId[row][col] != 0; }

    public int registerHit(int row, int col) {
        int id = shipId[row][col];
        if (id==0) return 0;
        shipHits[id]++;
        return id;
    }

    public boolean isSunk(int shipId) {
        if (shipId<=0) return false;
        return shipHits[shipId] >= shipSize[shipId];
    }

    public int totalShips() { return shipsCount; }
}
