package poxador;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;

public class GUIPoxador extends JFrame {
    private Poxador poxador;
    private JButton btnEliminar;
    private JButton btnEngadir;
    private JFormattedTextField tfPrezo;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTable tablaObxectivos;
    private JTextField tfTitulo;
    private JTextArea taLog;

    ModeloObxectivos modeloObxectivos;
    public GUIPoxador(Poxador poxador){
        super("Practica 6 - "+poxador.getName());
        setSize(500,700);
        setResizable(false);
        this.poxador=poxador;
        this.modeloObxectivos=new ModeloObxectivos();
        initComponents();

    }

    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tablaObxectivos = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        taLog = new javax.swing.JTextArea();
        btnEliminar = new javax.swing.JButton();
        btnEngadir = new javax.swing.JButton();
        tfPrezo = new javax.swing.JFormattedTextField();
        tfTitulo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(500, 500));

        tablaObxectivos.setModel(modeloObxectivos);
        jScrollPane1.setViewportView(tablaObxectivos);
        DefaultCaret caret = (DefaultCaret) taLog.getCaret();
        caret.setUpdatePolicy(ALWAYS_UPDATE);
        taLog.setEditable(false);
        taLog.setColumns(20);
        taLog.setRows(5);
        jScrollPane2.setViewportView(taLog);

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               eliminarDaLista();
            }
        });

        btnEngadir.setText("Engadir");
        btnEngadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engadirALista();
            }
        });

        jLabel1.setText("Titulo");

        jLabel2.setText("Prezo");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Obxectivos");

        jLabel4.setText("Axente: "+poxador.getName());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jScrollPane2)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(btnEliminar))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(jLabel1)
                                                .addGap(18, 18, 18)
                                                .addComponent(tfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                                                .addComponent(jLabel2)
                                                .addGap(18, 18, 18)
                                                .addComponent(tfPrezo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(46, 46, 46)
                                                .addComponent(btnEngadir))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel4)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEliminar)
                                .addGap(13, 13, 13)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnEngadir)
                                        .addComponent(tfPrezo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        pack();
    }






    private void eliminarDaLista() {
        if(tablaObxectivos.getSelectedRow()!=-1){
            String index= (String) modeloObxectivos.getValueAt(tablaObxectivos.getSelectedRow(),0);
            poxador.eliminarObxectivo(index);
        }
    }

    private void engadirALista() {
        if(tfTitulo.getText().isEmpty() || tfPrezo.getText().isEmpty())
            return;
        if(!poxador.existeObxectivo(tfTitulo.getText())){
            poxador.engadirObxectivo(new Obxectivo(tfTitulo.getText(),Integer.parseInt(tfPrezo.getText())));
            tfTitulo.setText("");
            tfPrezo.setText("");
        }
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

    public void imprimirMensaxe(String msg){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String dat=String.format("[%s] %s \n",dateFormat.format(cal.getTime()),msg);
        taLog.setText(taLog.getText()+dat);
    }
}
