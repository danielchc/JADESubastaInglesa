package poxador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIPoxador extends JFrame {
    private final Poxador poxador;
    private JButton btnEliminar;
    private JButton btnEngadir;
    private JFormattedTextField tfPrezo;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JScrollPane jScrollPane1;
    private JTable tablaObxectivos;
    private JTextField tfTitulo;

    ModeloObxectivos modeloObxectivos;
    public GUIPoxador(Poxador poxador){
        super("Practica 6 - "+poxador.getName());
        setResizable(false);
        this.poxador=poxador;
        this.modeloObxectivos=new ModeloObxectivos();

        jScrollPane1 = new javax.swing.JScrollPane();
        tablaObxectivos = new javax.swing.JTable();
        btnEngadir = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tfTitulo = new javax.swing.JTextField();
        tfPrezo = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tablaObxectivos.setModel(modeloObxectivos);
        jScrollPane1.setViewportView(tablaObxectivos);

        btnEngadir.setText("Engadir");
        btnEliminar.setText("Eliminar");
        btnEngadir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(tfTitulo.getText().isEmpty() || tfPrezo.getText().isEmpty())
                    return;
                if(!poxador.existeObxectivo(tfTitulo.getText())){
                    poxador.engadirObxectivo(new Obxectivo(tfTitulo.getText(),Integer.parseInt(tfPrezo.getText())));
                    tfTitulo.setText("");
                    tfPrezo.setText("");
                }
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(tablaObxectivos.getSelectedRow()!=-1){
                    String index= (String) modeloObxectivos.getValueAt(tablaObxectivos.getSelectedRow(),0);
                    poxador.eliminarObxectivo(index);
                }
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Obxectivos");

        jLabel2.setText("Prezo Maximo");

        jLabel3.setText("Titulo");

        tfPrezo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(215, 215, 215)
                                .addComponent(jLabel1)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addGap(18, 18, 18)
                                                .addComponent(tfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tfPrezo)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnEngadir))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(btnEliminar)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(17, 17, 17)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnEngadir)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3)
                                        .addComponent(tfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tfPrezo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(23, 23, 23))
        );

        pack();
    }

    public void actualizarObxectivo(Obxectivo obxectivo) {
        modeloObxectivos.actualizarObxectivo(obxectivo);
    }

    public void engadirObxectivo(Obxectivo obxectivo) {
        modeloObxectivos.engadirObxectivo(obxectivo);
    }

    public void eliminarObxectivo(String obxectivo) {
        modeloObxectivos.eliminarObxectivo(obxectivo);
    }
}
