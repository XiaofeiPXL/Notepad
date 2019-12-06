import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class NotepadFont implements ActionListener {
    final JDialog fontDialog;
    static JButton fontOkButton; // sure(OK) button
    final JTextField tfFont, tfSize, tfStyle;
    final int fontStyleConst[] = {
            Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD + Font.ITALIC
    };
    final JList listStyle, listFont, listSize;
    JLabel sample;
    JPanel pane1, pane2, pane3, pane4;
    static JTextArea text;
    
    // constructor My font
    public NotepadFont(JFrame frame,JTextArea Text) {
        text = Text;
        fontDialog = new JDialog(frame, "Setting Font", true);
        Container con = fontDialog.getContentPane();
        con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));
        pane1 = new JPanel();
        pane2 = new JPanel();
        pane3 = new JPanel();
        pane4 = new JPanel();
        Font currentFont = Text.getFont();

        JLabel lblFont = new JLabel("Font(F):");
        JLabel lblStyle = new JLabel("Style(Y):");
        JLabel lblSize = new JLabel("Size(S):");

        lblFont.setHorizontalAlignment(SwingConstants.CENTER);
        lblStyle.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSize.setHorizontalAlignment(SwingConstants.CENTER);
        lblFont.setPreferredSize(new Dimension(90, 20));
        lblStyle.setPreferredSize(new Dimension(110, 20));
        lblSize.setPreferredSize(new Dimension(100, 20));
        tfFont = new JTextField(15);// number of characteristics in the field
        tfFont.setText(currentFont.getFontName());
        tfFont.selectAll();
        tfFont.setPreferredSize(new Dimension(250, 20));
        tfStyle = new JTextField(12);
        if (currentFont.getStyle() == Font.PLAIN)
            tfStyle.setText("Regular");
        else if (currentFont.getStyle() == Font.BOLD)
            tfStyle.setText("Bold");
        else if (currentFont.getStyle() == Font.ITALIC)
            tfStyle.setText("Italic");
        else if (currentFont.getStyle() == (Font.BOLD + Font.ITALIC))
            tfStyle.setText("Bold Italic");

        tfFont.selectAll();
        tfStyle.setPreferredSize(new Dimension(200, 20));
        tfSize = new JTextField(7);
        tfSize.setText(currentFont.getSize() + "");
        tfSize.selectAll();
        tfSize.setPreferredSize(new Dimension(200, 20));

        final String fontStyle[] = {
                "Regular", "Bold", "Italic", "Bold Italic"
        };
        listStyle = new JList(fontStyle);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String fontName[] = ge.getAvailableFontFamilyNames();
        int defaultFontIndex = 0;
        for (int i = 0; i < fontName.length; i++) {
            if (fontName[i].equals(currentFont.getFontName())) {
                defaultFontIndex = i;
                break;
            }
        }
        listFont = new JList(fontName);
        listFont.setSelectedIndex(defaultFontIndex);
        listFont.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listFont.setVisibleRowCount(7);
        listFont.setFixedCellWidth(99);
        listFont.setFixedCellHeight(20);
        listFont.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                tfFont.setText(fontName[listFont.getSelectedIndex()]);
                tfFont.requestFocus();
                tfFont.selectAll();
                updateSample();
            }
        });

        listStyle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (currentFont.getStyle() == Font.PLAIN)
            listStyle.setSelectedIndex(0);
        else if (currentFont.getStyle() == Font.BOLD)
            listStyle.setSelectedIndex(1);
        else if (currentFont.getStyle() == Font.ITALIC)
            listStyle.setSelectedIndex(2);
        else if (currentFont.getStyle() == (Font.BOLD + Font.ITALIC))
            listStyle.setSelectedIndex(3);

        listStyle.setVisibleRowCount(7);
        listStyle.setFixedCellWidth(85);
        listStyle.setFixedCellHeight(20);
        listStyle.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                tfStyle.setText(fontStyle[listStyle.getSelectedIndex()]);
                tfStyle.requestFocus();
                tfStyle.selectAll();
                updateSample();
            }
        });

        final String fontSize[] = {
                "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"
        };
        listSize = new JList(fontSize);
        int defaultFontSizeIndex = 0;
        for (int i = 0; i < fontSize.length; i++) {
            if (fontSize[i].equals(currentFont.getSize() + "")) {
                defaultFontSizeIndex = i;
                break;
            }
        }
        listSize.setSelectedIndex(defaultFontSizeIndex);

        listSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSize.setVisibleRowCount(7);
        listSize.setFixedCellWidth(50);
        listSize.setFixedCellHeight(20);
        listSize.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                tfSize.setText(fontSize[listSize.getSelectedIndex()]);
                tfSize.requestFocus();
                tfSize.selectAll();
                updateSample();
            }
        });
        fontOkButton = new JButton("OK");
        fontOkButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fontDialog.dispose();
            }
        });

        sample = new JLabel(" Note ");
        sample.setHorizontalAlignment(SwingConstants.CENTER);
        sample.setPreferredSize(new Dimension(175, 55));

        JPanel samplePanel = new JPanel();
        samplePanel.setBorder(BorderFactory.createTitledBorder("Sample"));
        samplePanel.add(sample);

        pane1.add(lblFont);
        pane1.add(lblStyle);
        pane1.add(lblSize);
        pane2.add(tfFont);
        pane2.add(tfStyle);
        pane2.add(tfSize);

        pane3.add(new JScrollPane(listFont));
        pane3.add(new JScrollPane(listStyle));
        pane3.add(new JScrollPane(listSize));
        pane4.add(samplePanel);
        pane4.add(fontOkButton);
        pane4.add(cancelButton);
        con.add(pane1);
        con.add(pane2);
        con.add(pane3);
        con.add(pane4);
        updateSample();

        fontDialog.pack();
        fontDialog.setSize(400, 400);
        fontDialog.setLocation(200, 200);
        fontDialog.setResizable(false);
        fontDialog.setVisible(true);
    }// end of constructor

    // update sample
    public void updateSample() {
        Font sampleFont =
                new Font(tfFont.getText(), fontStyleConst[listStyle.getSelectedIndex()], Integer.parseInt(tfSize
                        .getText()));
        sample.setFont(sampleFont);
    }// End method updateSample

    // setting the font style in the text edit area
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fontOkButton) {
            Font tempFont =
                    new Font(tfFont.getText(), fontStyleConst[listStyle.getSelectedIndex()],
                            Integer.parseInt(tfSize.getText()));
            text.setFont(tempFont);
            fontDialog.dispose();
        }
    }// End method actionPerformed
}/* End of class MyFont */