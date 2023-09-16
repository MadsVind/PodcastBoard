import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class CustomDialog extends JDialog{
    public CustomDialog(JFrame parent, String title, JPanel panel, JButton closeButton) {
        super(parent, title, true); // The true argument makes it a modal dialog.
        setResizable(false);

        // Add an ActionListener to the custom button
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(parent);
    }
}
