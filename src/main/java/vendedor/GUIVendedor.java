package vendedor;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GUIVendedor extends JFrame {
    private final javax.swing.JButton btnEngadir;
    private final javax.swing.JLabel lblNome;
    private final javax.swing.JLabel lblPrezo;
    private final javax.swing.JLabel lblIncremento;
    private final javax.swing.JScrollPane jScrollPane2;
    private final javax.swing.JScrollPane jScrollPane3;
    private final javax.swing.JTable tableSubastasActivas;
    private final javax.swing.JTextField tfNomeLibro;
    private final javax.swing.JFormattedTextField tfIncremento;
    private final javax.swing.JFormattedTextField tfPrezo;
    private final JLabel jLabel1;
    private final ModeloSubastas modeloSubastas;

    public GUIVendedor(Vendedor vendedor) {
        super("Practica 6 - Subastador");
        setResizable(false);
        setSize(500,500);
        modeloSubastas = new ModeloSubastas();
        jScrollPane2 = new javax.swing.JScrollPane();
        btnEngadir = new javax.swing.JButton();
        tfNomeLibro = new javax.swing.JTextField();
        tfIncremento = new javax.swing.JFormattedTextField();
        tfPrezo = new javax.swing.JFormattedTextField();
        lblNome = new javax.swing.JLabel();
        lblPrezo = new javax.swing.JLabel();
        lblIncremento = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jLabel1 = new JLabel();
        tableSubastasActivas = new javax.swing.JTable();


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnEngadir.setText("Engadir");
        btnEngadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if ((tfNomeLibro.getText().isEmpty()) || (tfIncremento.getText().isEmpty()) || (tfPrezo.getText().isEmpty()))
                    return;
                int prezo = Integer.parseInt(tfPrezo.getText());
                int incremento = Integer.parseInt(tfIncremento.getText());
                Subasta subasta = new Subasta(tfNomeLibro.getText(), prezo, incremento);
                if (!vendedor.existeSubasta(subasta)) {
                    vendedor.engadirSubasta(subasta);
                    tfNomeLibro.setText("");
                    tfIncremento.setText("");
                    tfPrezo.setText("");
                }

            }
        });

        tfNomeLibro.setText("Libro");
        tfIncremento.setText("5");
        tfPrezo.setText("10");
        lblNome.setText("Nome");
        lblPrezo.setText("Prezo");
        lblIncremento.setText("Incremento");
        jLabel1.setText("Subastas");
        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        tfPrezo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        tfIncremento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));


        tableSubastasActivas.setModel(modeloSubastas);
        tableSubastasActivas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(tableSubastasActivas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup()
                        .addGap(265, 265, 265)
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(17, 17, 17)
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


    public void actualizarSubasta(Subasta subasta) {
        modeloSubastas.actualizarSubasta(subasta);
    }

    public void engadirSubasta(Subasta subasta) {
        modeloSubastas.engadirSubasta(subasta);
    }
}
