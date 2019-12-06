import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class SettingLogic {

    public static int marginLeft = 1;
    public static int marginRight = 1;
    public static int marginTop = 1;
    public static int marginBottom = 1;
    
    public static void showSettingPage(final JFrame frame,final JTextArea Text){
        
        final JDialog settingDialog = new JDialog(frame, "Setting", true);
        Container con = settingDialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        //create window for search&
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        
        //JLabel settingPageTitle = new JLabel("Margin Setting");
        //topPanel.add(settingPageTitle);
        
        JLabel Lt = new JLabel("Left (in)");
        JLabel Rt = new JLabel("Right (in)");
        JLabel Lb = new JLabel("Top (in)");
        JLabel Rb = new JLabel("Bottom (in)");
        
        final JTextField leftMarginText = new JTextField(5);
        final JTextField rightMarginText = new JTextField(5);
        final JTextField topMarginText = new JTextField(5);
        final JTextField bottomMarginText = new JTextField(5);
        leftMarginText.setText(marginLeft + "");
        rightMarginText.setText(marginRight + "");
        topMarginText.setText(marginTop + "");
        bottomMarginText.setText(marginBottom + "");
        
        JPanel panelLt = new JPanel();
        JPanel panelRt = new JPanel();
        JPanel panelLb = new JPanel();
        JPanel panelRb = new JPanel();
        panelLt.setLayout(new GridLayout(2, 1));
        panelRt.setLayout(new GridLayout(2, 1));
        panelLb.setLayout(new GridLayout(2, 1));
        panelRb.setLayout(new GridLayout(2, 1));
        panelLt.add(Lt);
        panelRt.add(Rt);
        panelLb.add(Lb);
        panelRb.add(Rb);
        panelLt.add(leftMarginText);
        panelRt.add(rightMarginText);
        panelLb.add(topMarginText);
        panelRb.add(bottomMarginText);
        JPanel group = new JPanel();
        GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setVgap(20);
        gridLayout.setHgap(50);
        group.setLayout(gridLayout);
        group.add(panelLt);
        group.add(panelRt);
        group.add(panelLb);
        group.add(panelRb);
        
        JPanel direction = new JPanel();
        direction.setBorder(BorderFactory.createTitledBorder("Margin Setting"));
        direction.add(group);
        direction.setPreferredSize(new Dimension(250, 145));
        topPanel.add(direction);
        
        // cancel button action 
        JButton cancel = new JButton("Cancel");
        cancel.setPreferredSize(new Dimension(110, 22));
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settingDialog.dispose();
            }
        });
        JButton save = new JButton("Save");
        save.setPreferredSize(new Dimension(110, 22));
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //settingDialog.dispose();
                try{
                    marginLeft = Integer.parseInt(leftMarginText.getText());
                    marginRight = Integer.parseInt(rightMarginText.getText());
                    marginTop = Integer.parseInt(topMarginText.getText());
                    marginBottom = Integer.parseInt(bottomMarginText.getText());
                    
                    Insets inset = new Insets(marginTop, marginLeft, marginBottom, marginRight);
                    Text.setMargin(inset);
                    Text.invalidate();
                    String text = Text.getText();
                    Text.setText("");
                    Text.setText(text);
                }catch(NumberFormatException e1){
                    e1.printStackTrace();
                }catch (NullPointerException e2) {
                    e2.printStackTrace();
                }
                
                settingDialog.dispose();
            }
        });
        GridLayout gridButton = new GridLayout(2, 1);
        gridButton.setVgap(5);
        bottomPanel.setLayout(gridButton);
        bottomPanel.add(save);
        bottomPanel.add(cancel);
        
        con.add(topPanel);
        con.add(bottomPanel);

        // setting size sizable position visible
        settingDialog.setSize(400, 200);
        settingDialog.setResizable(false);
        settingDialog.setLocation(230, 280);
        settingDialog.setVisible(true);
    }
}
