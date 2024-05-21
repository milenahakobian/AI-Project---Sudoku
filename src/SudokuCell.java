import java.util.ArrayList;

public class SudokuCell {
    private int value;
    private boolean fixed;
    private Pair coords;
    private ArrayList<Integer> domain;



    //construct an arraylist
    public  SudokuCell(int v, boolean f, ArrayList<Integer> d, Pair c){
        value = v;
        fixed = f;
        domain = d;
        coords = c;
    }

    public int getValue(){ return value;}
    public boolean getFixed(){ return fixed;}
    public Pair getCoords(){return coords;}
    public ArrayList<Integer> getDomain(){ return domain;}


    public void setValue(int value){this.value = value;}
    public void setFixed(boolean fixed) {this.fixed = fixed;}
    public void setDomain(ArrayList<Integer> domain) { this.domain = domain;}

    public void setCoords(int x, int y) { coords = new Pair(x, y);
    }
}
