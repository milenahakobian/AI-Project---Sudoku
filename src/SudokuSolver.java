import sources.LinkedQueue;
import java.util.*;

public class SudokuSolver {
    private int backtrackCount = 0;
    private int valueAssignmentCount = 0;


    public SudokuBoard Backtracking_Search(SudokuBoard csp, String varStrategy, String valStrategy, String inferenceStrategy) {
        return Backtrack(csp, varStrategy, valStrategy, inferenceStrategy);
    }

    public SudokuBoard Backtrack(SudokuBoard csp, String variable, String val, String inference) {
        synchronized (this) {
            backtrackCount++; // Increment backtrack count
        }

        if (csp.isSolved()) {
            System.out.println();

            return csp;
        }

        SudokuCell var = select_unassigned_var(csp, variable);
        ArrayList<Integer> domain = select_value(csp, var, val);

        for (int value : domain) {
            synchronized (this) {
                valueAssignmentCount++; // Increment value assignment count
            }

            SudokuBoard assignment = new SudokuBoard(csp);
            SudokuCell temp_var = assignment.getCell(var.getCoords().getX(), var.getCoords().getY());
            if (assignment.isValueOk(var.getCoords().getX(), var.getCoords().getY(), value)) {
                assignment.getCell(var.getCoords().getX(), var.getCoords().getY()).setValue(value);
                assignment.getCell(var.getCoords().getX(), var.getCoords().getY()).setFixed(true);

                boolean inferencesResult = inference(temp_var, assignment, inference);

                if (inferencesResult) {
                    SudokuBoard result = Backtrack(assignment, variable, val, inference); // Recursive call
                    if (result != null) {
                        return result;
                    }
                }
            }
        }

        return null;
    }


    public synchronized int getBacktrackCount() {
        return backtrackCount;
    }

    public synchronized int getValueAssignmentCount() {
        return valueAssignmentCount;
    }



    // SELECTING THE VARIABLE
    public SudokuCell select_unassigned_var(SudokuBoard copy, String strategy) {
        return switch (strategy) {
            case "random" -> random_var(copy);
            case "next" -> next_var(copy);
            case "mrv" -> mrv(copy);
            default -> null;
        };
    }

    public SudokuCell random_var(SudokuBoard copy) {
        int size = copy.getSize();
        Random random = new Random();


        // Generate random indices until an unassigned cell is found
        int i = random.nextInt(size * size);
        int j = random.nextInt(size * size);
        while (copy.getCell(i, j).getFixed()) {
            i = random.nextInt(size * size);
            j = random.nextInt(size * size);
        }

        return copy.getCell(i, j);
    }


    public SudokuCell next_var(SudokuBoard copy) {
        int size = copy.getSize();

        for (int i = 0; i < size * size; i++) {
            for (int j = 0; j < size * size; j++) {
                if (!copy.getCell(i, j).getFixed()) {
                    return copy.getCell(i, j);

                }
            }
        }
        return null;
    }


    public SudokuCell mrv(SudokuBoard csp) {
        int size = csp.getSize();
        SudokuCell min = null;

        for (int i = 0; i < size * size; i++) {
            for (int j = 0; j < size * size; j++) {
                SudokuCell currentCell = csp.getCell(i, j);
                if (!currentCell.getFixed()) {
                    if (min == null || currentCell.getDomain().size() < min.getDomain().size()) {
                        min = currentCell;
                    }
                }
            }
        }
        return min;
    }


    public ArrayList<Integer> select_value(SudokuBoard copy, SudokuCell var, String strategy) {
        return switch (strategy) {
            case "ordered" -> asc_order_domain(var);
            case "random" -> rand_domain(var);
            case "lcv" -> lcv(var, copy);
            default -> null;
        };
    }

    //SELECTING A VALUE FOR THE VARIABLE
    public ArrayList<Integer> asc_order_domain(SudokuCell var) {
        return var.getDomain();
    }

    public ArrayList<Integer> rand_domain(SudokuCell var) {
        ArrayList<Integer> domain = new ArrayList<>(var.getDomain());
        Collections.shuffle(domain);
        return domain;
    }

    public ArrayList<Integer> lcv(SudokuCell var, SudokuBoard assignment) {
        ArrayList<Integer> domain = new ArrayList<>(var.getDomain());
        Map<Pair, List<Pair>> neighborsMap = assignment.getNeighbors();

        List<Pair> neighbors = neighborsMap.get(var.getCoords());
        Map<Integer, Integer> conflicts = new HashMap<>();

        // Calculate conflicts for each value in the domain
        for (int value : domain) {
            int totalConflicts = 0;
            for (Pair neighbor : neighbors) {
                SudokuCell neighborCell = assignment.getCell(neighbor.getX(), neighbor.getY());
                if (!neighborCell.getFixed() && neighborCell.getDomain().contains(value)) {
                    totalConflicts += 1;
                }
            }
            conflicts.put(value, totalConflicts);
        }

        //System.out.println("Conflicts Map: " + conflicts); // Print conflicts map for debugging
        domain.sort(Comparator.comparingInt(conflicts::get));

        return domain;
    }


    //Inferences
    public boolean inference(SudokuCell var, SudokuBoard assignment, String strategy) {
        return switch (strategy) {
            case "none" -> true; // No inference, always return true
            case "mac" -> mac(var, assignment);
            case "forward_checking" -> forward_checking(var, assignment);
            default -> false; // Or throw an exception for unsupported strategy
        };
    }

    public boolean forward_checking(SudokuCell var, SudokuBoard assignment) {
        int value = var.getValue();
        List<Pair> neighbors = assignment.getNeighbors().get(var.getCoords());

        for (Pair neighbor : neighbors) {
            SudokuCell cur = assignment.getCell(neighbor.getX(), neighbor.getY());
            if (!cur.getFixed()) {
                cur.getDomain().remove(Integer.valueOf(value));
                if (cur.getDomain().isEmpty())
                    return false;
            }
        }
        return true;
    }



    public boolean mac(SudokuCell var, SudokuBoard assignment) {
        List<Pair> neighbors = assignment.getNeighbors().get(var.getCoords());
        LinkedQueue<Arc> arcs = new LinkedQueue<Arc>();

        for (Pair neighbor : neighbors) {
            SudokuCell neighborCell = assignment.getCell(neighbor.getX(), neighbor.getY());
            if (!neighborCell.getFixed()) {
                arcs.enqueue(new Arc(neighbor, var.getCoords()));
            }
        }

        return ac3(arcs, assignment);
    }

    // AC-3 algorithm
    public boolean ac3(LinkedQueue<Arc> arcs, SudokuBoard assignment) {
        while (!arcs.isEmpty()) {
            Arc arc = arcs.dequeue();
            SudokuCell Xi = assignment.getCell(arc.getXi().getX(), arc.getXi().getY());
            SudokuCell Xj = assignment.getCell(arc.getXj().getX(), arc.getXj().getY());

            if (revise(Xi, Xj)) {
                if (Xi.getDomain().isEmpty())
                    return false;

                List<Pair> XiNeighbors = assignment.getNeighbors().get(Xi.getCoords());
                for (Pair neighbor : XiNeighbors) {
                    SudokuCell neighborCell = assignment.getCell(neighbor.getX(), neighbor.getY());
                    if (!neighborCell.getFixed() && !neighbor.equals(arc.getXj())) {
                        arcs.enqueue(new Arc(neighbor, arc.getXi()));
                    }
                }
            }
        }
        return true;
    }

    // Existing revise method
    public boolean revise(SudokuCell Xi, SudokuCell Xj) {
        int domain_size = Xi.getDomain().size();
        if (Xj.getDomain().size() == 1) {
            Xi.getDomain().remove(Xj.getDomain().getFirst());
        }
        return (Xi.getDomain().size() - domain_size != 0);
    }
}

