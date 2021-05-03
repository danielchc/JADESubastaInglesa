import javax.swing.*;
import java.awt.*;

public class GUIVendedor extends JFrame {
    private JPanel panel;

    public GUIVendedor(){
        super("Practica 8");
        setSize(500,500);
        panel= new JPanel();
        panel.add(new JLabel("Tocou"));
        setContentPane(panel);

    }
}
