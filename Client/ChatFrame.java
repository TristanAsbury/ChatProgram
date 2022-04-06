package Client;

import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Dimension;

public class ChatFrame extends JFrame {
    ConnectionToServer cts;

    public ChatFrame(ConnectionToServer cts){
        this.cts = cts;

        setupFrame();
    }

    public void addBuddy(Buddy buddy){
        
    }

    private void setupFrame(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize(400, 200);
        setLocation((int)d.getWidth()/2, (int)d.getHeight()/2);
        setTitle("Login");
        setVisible(true);
    }
}
