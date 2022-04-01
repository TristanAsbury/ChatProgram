package Client;

import javax.swing.JDialog;
import javax.swing.event.*;
import java.awt.*;

public class StartupDialog extends JDialog implements DocumentListener{
    
    ConnectionToServer cts;

    public StartupDialog(ConnectionToServer cts){
        this.cts = cts;
        setupDialog();
    }


    public void insertUpdate(DocumentEvent e){ 
        
    }

    public void removeUpdate(DocumentEvent e){ 
        
    }

    public void changedUpdate(DocumentEvent e){ }

    private void setupDialog(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/4, (int)d.getHeight()/4);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("Living Things");
        setVisible(true);
    }
}
