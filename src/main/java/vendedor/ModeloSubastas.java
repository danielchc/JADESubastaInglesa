package vendedor;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedHashMap;

public class ModeloSubastas extends AbstractTableModel {
    private LinkedHashMap<String, Subasta> subastas;

    public ModeloSubastas() {
        subastas = new LinkedHashMap<>();
    }

    @Override
    public int getRowCount() {
        return subastas.size();
    }

    @Override
    public String getColumnName(int col) {
        return (new String[]{"Titulo","Prezo","Incremento","Ganador","Interesados","Estado"})[col];
    }

    public Class getColumnClass(int col) {
        return (new Class[]{String.class, Integer.class, Integer.class, String.class,Integer.class, String.class})[col];
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object resultado = null;
        switch (col) {
            case 0:
                resultado = getByIndex(row).getTitulo();
                break;
            case 1:
                resultado = getByIndex(row).getPrezo();
                break;
            case 2:
                resultado = getByIndex(row).getIncremento();
                break;
            case 3:
                if (getByIndex(row).getGanadorActual() != null) {
                    resultado = getByIndex(row).getGanadorActual().getName().toString();
                } else {
                    resultado = "<Sen ganador>";
                }
                break;
            case 4:
                resultado = getByIndex(row).getInteresados().size();
                break;
            case 5:
                resultado = getByIndex(row).getEstado().toString();
                break;
        }
        return resultado;

    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void engadirSubasta(Subasta subasta) {
        subastas.put(subasta.getTitulo(), subasta);
        fireTableDataChanged();
    }

    private Subasta getByIndex(int i) {
        return (Subasta) subastas.values().toArray()[i];
    }

    public void actualizarSubasta(Subasta subasta) {
        subastas.replace(subasta.getTitulo(), subasta);
        fireTableDataChanged();
    }

    public void eliminarSubasta(String subastaTitulo) {
        subastas.remove(subastaTitulo);
        fireTableDataChanged();
    }
}
