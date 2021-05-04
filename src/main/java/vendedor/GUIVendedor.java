package vendedor;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GUIVendedor extends JFrame {
    private javax.swing.JButton btnEngadir;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblPrezo;
    private javax.swing.JLabel lblIncremento;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField tfNomeLibro;
    private javax.swing.JTextField tfIncremento;
    private javax.swing.JTextField tfPrezo;
    private EventManager eventManager;

    public GUIVendedor(Vendedor vendedor){
        super("Practica 8");
        ModeloSubastas modeloSubastas=new ModeloSubastas();

        eventManager=new EventManager() {
            @Override
            public void actualizarSubasta(Subasta subasta) {
                modeloSubastas.actualizarSubasta(subasta);
            }
            @Override
            public void engadirSubasta(Subasta subasta) {
                modeloSubastas.engadirSubasta(subasta);
            }
        };

        jScrollPane2 = new javax.swing.JScrollPane();
        btnEngadir = new javax.swing.JButton();
        tfNomeLibro = new javax.swing.JTextField();
        tfIncremento = new javax.swing.JTextField();
        tfPrezo = new javax.swing.JTextField();
        lblNome = new javax.swing.JLabel();
        lblPrezo = new javax.swing.JLabel();
        lblIncremento = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnEngadir.setText("Engadir");
        btnEngadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if((tfNomeLibro.getText().isEmpty())||(tfIncremento.getText().isEmpty())||(tfPrezo.getText().isEmpty()))
                    return;
                int prezo=Integer.parseInt(tfPrezo.getText());
                int incremento=Integer.parseInt(tfIncremento.getText());
                Subasta subasta=new Subasta(tfNomeLibro.getText(),prezo,incremento);
                if(!vendedor.existeSubasta(subasta)){
                    vendedor.engadirSubasta(subasta);
                }

            }
        });

        tfNomeLibro.setText("Libro");
        tfIncremento.setText("5");
        tfPrezo.setText("10");
        lblNome.setText("Nome");
        lblPrezo.setText("Prezo");
        lblIncremento.setText("Incremento");


        jTable2.setModel(modeloSubastas);
        jScrollPane3.setViewportView(jTable2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(52, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jScrollPane3)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblNome)
                                                        .addComponent(lblPrezo))
                                                .addGap(53, 53, 53)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(tfPrezo, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lblIncremento)
                                                                .addGap(27, 27, 27)
                                                                .addComponent(tfIncremento, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(tfNomeLibro, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addComponent(btnEngadir)))
                                .addContainerGap(44, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(64, 64, 64)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(tfNomeLibro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblNome))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(tfIncremento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(tfPrezo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblPrezo)
                                                        .addComponent(lblIncremento)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(52, 52, 52)
                                                .addComponent(btnEngadir)))
                                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }
    public EventManager getEventManager(){
        return eventManager;
    }
}
