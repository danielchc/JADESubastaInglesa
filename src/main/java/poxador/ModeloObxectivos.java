package poxador;


import javax.swing.table.AbstractTableModel;
import java.util.LinkedHashMap;

public class ModeloObxectivos extends AbstractTableModel {
    private LinkedHashMap<String, Obxectivo> obxectivos;

    public ModeloObxectivos() {
        obxectivos = new LinkedHashMap<>();
    }

    @Override
    public int getRowCount() {
        return obxectivos.size();
    }

    @Override
    public String getColumnName(int col) {
        return (new String[]{"Titulo","Prezo Maximo","Poxa Actual","Ganador Actual","Estado"})[col];
    }

    public Class getColumnClass(int col) {
        return (new Class[]{String.class, Integer.class, Integer.class, String.class, String.class})[col];
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object resultado = null;
        switch (col) {
            case 0:
                resultado = getByIndex(row).getTitulo();
                break;
            case 1:
                resultado = getByIndex(row).getPrezoMaximo();
                break;
            case 2:
                resultado = getByIndex(row).getPrezoActual();
                break;
            case 3:
                if (getByIndex(row).getGanadorActual() != null) {
                    resultado = getByIndex(row).getGanadorActual().toString();
                } else {
                    resultado = "<Sen ganador>";
                }
                break;
            case 4:
                resultado=new String[]{"Esperando","Ganada","En curso","Retirado"}[getByIndex(row).getEstadoObxectivo().ordinal()];
                break;
        }
        return resultado;

    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private Obxectivo getByIndex(int i) {
        return (Obxectivo) obxectivos.values().toArray()[i];
    }

    public void engadirObxectivo(Obxectivo obxectivo) {
        obxectivos.put(obxectivo.getTitulo(), obxectivo);
        fireTableDataChanged();
    }

    public void actualizarObxectivo(Obxectivo obxectivo) {
        obxectivos.replace(obxectivo.getTitulo(), obxectivo);
        fireTableDataChanged();
    }

    public void eliminarObxectivo(String obxectivoTitulo) {
        obxectivos.remove(obxectivoTitulo);
        fireTableDataChanged();
    }
}
