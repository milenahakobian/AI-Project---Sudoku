import java.util.*;

public class SudokuBoard {
    private int size;
    private SudokuCell[][] grid;
    private Map<Pair, List<Pair>> neighbors ;


    public SudokuBoard(int[][] startState) {
        this.size = (int) Math.sqrt(startState.length);
        this.grid = new SudokuCell[startState.length][startState.length];
        this.neighbors = new HashMap<>();


        // Initialize the grid
        for (int i = 0; i < startState.length; i++) {
            for (int j = 0; j < startState[i].length; j++) {
                ArrayList<Integer> domain = new ArrayList<>();
                if (startState[i][j] != 0) {
                    domain.add(startState[i][j]);
                } else {
                    for (int value = 1; value <= startState.length; value++) {
                        domain.add(value);
                    }
                }
                Pair coords = new Pair(i, j);
                this.grid[i][j] = new SudokuCell(startState[i][j], startState[i][j] != 0, domain, coords);
            }
        }

        // Populate neighbors for each cell
        for (int i = 0; i < startState.length; i++) {
            for (int j = 0; j < startState[i].length; j++) {
                Pair coords = grid[i][j].getCoords();
                this.neighbors.put(coords, findNeighbors(i, j));
            }
        }
        // Revise domains for each empty cell
        reviseDomains();
    }








    // Copy constructor
    public SudokuBoard(SudokuBoard other) {
        this.size = other.size;
        this.grid = new SudokuCell[size*size][size*size];
        this.neighbors = new HashMap<>();

        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                SudokuCell otherCell = other.getCell(i, j);
                ArrayList<Integer> domainCopy = new ArrayList<>(otherCell.getDomain());
                int x = otherCell.getCoords().getX();
                int y = otherCell.getCoords().getY();
                this.grid[i][j] = new SudokuCell(otherCell.getValue(), otherCell.getFixed(), domainCopy, new Pair(x,y));
            }

        }
        //Populate neighbors for each cell
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                Pair coords = grid[i][j].getCoords();
                this.neighbors.put(coords, findNeighbors(i, j));
            }
        }
    }


    public SudokuBoard(int size, char difficulty) {
        // Solve the board using backtracking to generate a random full Sudoku
        SudokuSolver solver = new SudokuSolver();
        SudokuBoard s = solver.Backtrack(new SudokuBoard(new int[size*size][size*size]), "mrv", "lcv", "mac");

        this.size = size;
        this.grid = s.grid;
        this.neighbors = s.neighbors;

        // Calculate the number of cells to empty based on the difficulty level
        int cellsToEmpty;
        switch (difficulty) {
            case 'e':
                cellsToEmpty = (int) Math.pow(size, 4) / 4;
                break;
            case 'm':
                cellsToEmpty = (int) Math.pow(size, 4) / 2;
                break;
            case 'h':
                cellsToEmpty = 3 * (int) Math.pow(size, 4) / 4;
                break;
            default:
                cellsToEmpty = (int) Math.pow(size, 4) / 2; // Default to medium
                break;
        }
        // Randomly empty the given number of cells and fix the remaining cells
        Random random = new Random();
        int emptyCount = 0;
        while (emptyCount < cellsToEmpty) {
            int x = random.nextInt(size*size);
            int y = random.nextInt(size*size);
            if (grid[x][y].getValue() != 0) {
                grid[x][y].setValue(0);
                grid[x][y].setFixed(false);
                emptyCount++;
            }
        }
    }



    public int getSize(){ return this.size;}
    public SudokuCell getCell(int i, int j){ return grid[i][j];}

    public Map<Pair, List<Pair>> getNeighbors() {
        return neighbors;
    }

    // Method to find neighbors of a cell
    // Method to find neighbors of a cell
    private List<Pair> findNeighbors(int row, int col) {
        List<Pair> neighbors = new ArrayList<>();

        // Add row neighbors
        for (int i = 0; i < grid.length; i++) {
            if (i != row) {
                neighbors.add(grid[i][col].getCoords());
            }
        }

        // Add column neighbors
        for (int j = 0; j < grid.length; j++) {
            if (j != col) {
                neighbors.add(grid[row][j].getCoords());
            }
        }

        // Add subgrid neighbors
        int subgridRowStart = (row / size) * size;
        int subgridColStart = (col / size) * size;
        for (int i = subgridRowStart; i < subgridRowStart + size; i++) {
            for (int j = subgridColStart; j < subgridColStart + size; j++) {
                if (i != row || j != col) {
                    neighbors.add(grid[i][j].getCoords());
                }
            }
        }

        return neighbors;
    }




    // Check if assigning a value to an empty cell maintains consistency
    public boolean isValueOk(int i, int j, int value) {
        // Row & Column
        for (int k = 0; k < grid.length; k++) {
            // Check row
            if (k != j && grid[i][k].getValue() == value && value != 0)
                return false;
            // Check column
            if (k != i && grid[k][j].getValue() == value  && value != 0)
                return false;
        }

        // Subgrid
        int sgRow = i - i % size; // Subgrid row start
        int sgCol = j - j % size; // Subgrid column start
        for (int k = sgRow; k < sgRow + size; k++) {
            for (int m = sgCol; m < sgCol + size; m++)
                if (k != i && m != j && grid[k][m].getValue() == value  && value != 0)
                    return false;
        }
        return true;
    }


    public boolean isSolved(){
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid.length; j++) {
                int value = grid[i][j].getValue();
                if (grid[i][j].getValue() == 0 || !isValueOk(i, j, value))
                    return false;
            }
        }
        return true;
    }

    public void reviseDomains() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                SudokuCell cell = grid[i][j];
                if (!cell.getFixed()) { // Check if the cell is empty
                    ArrayList<Integer> revisedDomain = new ArrayList<>();
                    for (int value = 1; value <= grid.length; value++) {
                        if (isValueOk(i, j, value)) {
                            revisedDomain.add(value);
                        }
                    }
                    cell.setDomain(revisedDomain); // Update the domain of the cell
                }
            }
        }
    }

    public void print() {
        for (int i = 0; i < grid.length; i++) {
            if (i % size == 0 && i != 0) {
                for (int j = 0; j < 2*grid[0].length + size; j++) {
                    System.out.print("-");
                }
                System.out.println();
            }
            for (int j = 0; j < grid[0].length; j++) {
                if (j % size == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(grid[i][j].getValue() + " ");
            }
            System.out.println();
        }
    }
}
