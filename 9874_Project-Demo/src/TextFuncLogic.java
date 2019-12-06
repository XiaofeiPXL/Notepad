import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class TextFuncLogic {

    public static int getlineNumber(final JTextArea Text) {
        int totalLine = Text.getLineCount();
        int[] lineNumber = new int[totalLine + 1];
        int pos = 0, t = 0, num = 0, i = 0;
        String s = Text.getText();
        while (true) {
            pos = s.indexOf('\12', pos); // back \n position
            if (pos == -1)
                break;
            lineNumber[t++] = pos++;
        }
        if (Text.getCaretPosition() <= lineNumber[0])
            num = 1;
        else {
            if (Text.getCaretPosition() > lineNumber[Text.getLineCount() - 1])
                num = Text.getLineCount();
            for (i = 0; i < totalLine + 1; i++) {
                if (Text.getCaretPosition() <= lineNumber[i]) {
                    num = i + 1;
                    break;
                } else
                    continue;
            }
        }
        return num;
    }

    public static void showCharCount(int count, final JFrame frame) {
        final JDialog dialog = new JDialog(frame, "Char Count", true);
        Container con = dialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel searchContentLabel = new JLabel("Char count : " + count);
        con.add(searchContentLabel);

        JButton cancel = new JButton("Ok");
        cancel.setPreferredSize(new Dimension(110, 22));
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        con.add(cancel);

        dialog.setSize(200, 80);
        dialog.setResizable(false);
        dialog.setLocation(230, 280);
        dialog.setVisible(true);
    }
}
