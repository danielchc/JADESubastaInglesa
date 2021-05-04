package vendedor;

import jade.core.AID;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

public class ModeloSubastas extends AbstractTableModel {
    private LinkedHashMap<String,Subasta> subastas;
    public ModeloSubastas(){
        subastas=new LinkedHashMap<>();
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
            case 0: resultado= getByIndex(row).getTitulo(); break;
            case 1: resultado= getByIndex(row).getPrezo(); break;
            case 2: resultado= getByIndex(row).getIncremento();break;
            case 3:
                if(getByIndex(row).getGanadorActual()!=null){
                    resultado=getByIndex(row).getGanadorActual().toString();
                }else{
                    resultado="<Sen ganador>";
                }
                break;
        }
        return resultado;

    }
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void engadirSubasta(Subasta subasta) {
        subastas.put(subasta.getTitulo(),subasta);
        fireTableDataChanged();
    }
    private Subasta getByIndex(int i){
        return (Subasta) subastas.values().toArray()[i];
    }

    public void actualizarSubasta(Subasta subasta) {
        subastas.replace(subasta.getTitulo(),subasta);
        fireTableDataChanged();
    }
}
