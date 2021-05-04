package vendedor;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class ModeloSubastas extends AbstractTableModel {
    private ArrayList<Subasta> subastas;
    public ModeloSubastas(){
        subastas=new ArrayList<>();
    }
    @Override
    public int getRowCount() {
        return subastas.size();
    }

    @Override
    public String getColumnName(int col){
        if(col==0) return "Titulo";
        if(col==1) return "Prezo";
        if(col==2) return "Incremento";
        if(col==3) return "Ganador";
        return "";
    }

    public Class getColumnClass(int col){
        Class clase=null;
        switch (col){
            case 0: clase= java.lang.String.class; break;
            case 1: clase= java.lang.Integer.class; break;
            case 2: clase=java.lang.Integer.class; break;
            case 3: clase=java.lang.String.class; break;
        }
        return clase;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object resultado=null;
        switch (col){
            case 0: resultado= subastas.get(row).getTitulo(); break;
            case 1: resultado= subastas.get(row).getPrezo(); break;
            case 2: resultado=subastas.get(row).getIncremento();break;
            case 3: resultado="aa"; break;
        }
        return resultado;

    }
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void engadirSubasta(Subasta subasta) {
        subastas.add(subasta);
        fireTableDataChanged();
    }
}
