import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class SearchLogic {

    public static void search(final JFrame frame,final JTextArea Text) {
        final JDialog findDialog = new JDialog(frame, "Replace&Search", true);
        Container con = findDialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel searchContentLabel = new JLabel("Find(N) :");
        JLabel replaceContentLabel = new JLabel("Replace(P) :");
        final JTextField findText = new JTextField(22);
        final JTextField replaceText = new JTextField(22);
        final JCheckBox matchcase = new JCheckBox("Match case");
        ButtonGroup bGroup = new ButtonGroup();
        final JRadioButton up = new JRadioButton("Up(U)");
        final JRadioButton down = new JRadioButton("Down(D)");
        down.setSelected(true);
        bGroup.add(up);
        bGroup.add(down);
        JButton searchNext = new JButton("Find next(F)");
        JButton replace = new JButton("Replace(R)");
        final JButton replaceAll = new JButton("Replace All(A)");
        searchNext.setPreferredSize(new Dimension(110, 22));
        replace.setPreferredSize(new Dimension(110, 22));
        replaceAll.setPreferredSize(new Dimension(150, 22));
        // replace button 
        replace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (replaceText.getText().length() == 0 && Text.getSelectedText() != null)
                    Text.replaceSelection("");
                if (replaceText.getText().length() > 0 && Text.getSelectedText() != null)
                    Text.replaceSelection(replaceText.getText());
            }
        });

        // replace all button
        replaceAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Text.setCaretPosition(0); // put cursor to the start in the text area
                int a = 0, b = 0, replaceCount = 0;

                if (findText.getText().length() == 0) {
                    JOptionPane.showMessageDialog(findDialog, "Input the search content!", "Notice", JOptionPane.WARNING_MESSAGE);
                    findText.requestFocus(true);
                    return;
                }
                while (a > -1) {

                    int FindStartPos = Text.getCaretPosition();
                    String str1, str2, str3, str4, strA, strB;
                    str1 = Text.getText();
                    str2 = str1.toLowerCase();
                    str3 = findText.getText();
                    str4 = str3.toLowerCase();

                    if (matchcase.isSelected()) {
                        strA = str1;
                        strB = str3;
                    } else {
                        strA = str2;
                        strB = str4;
                    }

                    if (up.isSelected()) {
                        if (Text.getSelectedText() == null) {
                            a = strA.lastIndexOf(strB, FindStartPos - 1);
                        } else {
                            a = strA.lastIndexOf(strB, FindStartPos - findText.getText().length() - 1);
                        }
                    } else if (down.isSelected()) {
                        if (Text.getSelectedText() == null) {
                            a = strA.indexOf(strB, FindStartPos);
                        } else {
                            a = strA.indexOf(strB, FindStartPos - findText.getText().length() + 1);
                        }

                    }

                    if (a > -1) {
                        if (up.isSelected()) {
                            Text.setCaretPosition(a);
                            b = findText.getText().length();
                            Text.select(a, a + b);
                        } else if (down.isSelected()) {
                            Text.setCaretPosition(a);
                            b = findText.getText().length();
                            Text.select(a, a + b);
                        }
                    } else {
                        if (replaceCount == 0) {
                            JOptionPane.showMessageDialog(findDialog, "Fail search!", "Notepad", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(findDialog, "Succeed replace" + " "+ replaceCount + " "+"matching content!", "Replace successful", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    if (replaceText.getText().length() == 0 && Text.getSelectedText() != null) {
                        Text.replaceSelection("");
                        replaceCount++;
                    }
                    if (replaceText.getText().length() > 0 && Text.getSelectedText() != null) {
                        Text.replaceSelection(replaceText.getText());
                        replaceCount++;
                    }
                }// end while
            }
        }); 

        // find next button action 
        searchNext.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int a = 0, b = 0;
                int FindStartPos = Text.getCaretPosition();
                String str1, str2, str3, str4, strA, strB;
                str1 = Text.getText();
                str2 = str1.toLowerCase();
                str3 = findText.getText();
                str4 = str3.toLowerCase();
                // choose matching case box
                if (matchcase.isSelected()) {
                    strA = str1;
                    strB = str3;
                } else {
                    strA = str2;
                    strB = str4;
                }

                if (up.isSelected()) {
                    if (Text.getSelectedText() == null) {
                        a = strA.lastIndexOf(strB, FindStartPos - 1);
                    } else {
                        a = strA.lastIndexOf(strB, FindStartPos - findText.getText().length() - 1);
                    }
                } else if (down.isSelected()) {
                    if (Text.getSelectedText() == null) {
                        a = strA.indexOf(strB, FindStartPos);
                    } else {
                        a = strA.indexOf(strB, FindStartPos - findText.getText().length() + 1);
                    }

                }
                if (a > -1) {
                    if (up.isSelected()) {
                        Text.setCaretPosition(a);
                        b = findText.getText().length();
                        Text.select(a, a + b);
                    } else if (down.isSelected()) {
                        Text.setCaretPosition(a);
                        b = findText.getText().length();
                        Text.select(a, a + b);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Fail search!", "Notepad", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        });
        // cancel button action 
        JButton cancel = new JButton("Cancel");
        cancel.setPreferredSize(new Dimension(110, 22));
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findDialog.dispose();
            }
        });

        //create window for search&
        JPanel bottomPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel topPanel = new JPanel();

        JPanel direction = new JPanel();
        direction.setBorder(BorderFactory.createTitledBorder("Direction "));
        direction.add(up);
        direction.add(down);
        direction.setPreferredSize(new Dimension(170, 60));
        JPanel replacePanel = new JPanel();
        replacePanel.setLayout(new GridLayout(2, 1));
        replacePanel.add(replace);
        replacePanel.add(replaceAll);


        topPanel.add(searchContentLabel);
        topPanel.add(findText);
        topPanel.add(searchNext);
        centerPanel.add(replaceContentLabel);
        centerPanel.add(replaceText);
        centerPanel.add(replacePanel);
        bottomPanel.add(matchcase);
        bottomPanel.add(direction);
        bottomPanel.add(cancel);

        con.add(topPanel);
        con.add(centerPanel);
        con.add(bottomPanel);

        // setting size sizable position visible
        findDialog.setSize(410, 210);
        findDialog.setResizable(false);
        findDialog.setLocation(230, 280);
        findDialog.setVisible(true);
    }
}
