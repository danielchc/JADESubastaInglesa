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
        if (col == 0) return "Titulo";
        if (col == 1) return "Prezo Maximo";
        if (col == 2) return "Prezo Actual";
        if (col == 3) return "Ganador Actual";
        if (col == 4) return "Estado";
        return "";
    }

    public Class getColumnClass(int col) {
        Class clase = null;
        switch (col) {
            case 0:
                clase = java.lang.String.class;
                break;
            case 1:
                clase = java.lang.Integer.class;
                break;
            case 2:
                clase = java.lang.Integer.class;
                break;
            case 3:
                clase = java.lang.String.class;
                break;
            case 4:
                clase = java.lang.String.class;
                break;
        }
        return clase;
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
                switch (getByIndex(row).getEstadoObxectivo()){
                    case ESPERANDO:
                        resultado="Esperando";
                        break;
                    case GANADA:
                        resultado="Ganada";
                        break;
                    case EN_CURSO:
                        resultado="En curso";
                        break;
                    case RETIRADO:
                        resultado="Retirado";
                        break;
                };
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
