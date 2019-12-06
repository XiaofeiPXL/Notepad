import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class Notepad extends JFrame implements ActionListener, DocumentListener 
{
    JMenu mFile, mEdit, mMode, mView, mHelp;
    // ---------------file menu
    JMenuItem mFile_New, mFile_Open, mFile_Save, mFile_ASave, mFile_Print, mFile_pageSet, mFile_Exit;
    // ---------------edit menu
    JMenuItem mEdit_Undo, mEdit_Redo, mEdit_Cut, mEdit_Copy, mEdit_Paste, mEdit_Del, mEdit_Search, mEdit_SearchNext,
            mEdit_Replace, mEdit_Turnto, mEdit_SelectAll, mEdit_TimeDate, mEdit_Character;
    // ---------------style menu
    JCheckBoxMenuItem formatMenu_LineWrap;
    JMenu formatMenu_Color;
    JMenuItem formatMenu_Font, formatMenu_Color_FgColor, formatMenu_Color_BgColor;
    // ---------------status menu
    JCheckBoxMenuItem viewMenu_Status;
    // ---------------help menu
    JMenuItem mHelp_HelpTopics, mHelp_About;
    // ---------------pop-up menu
    JPopupMenu popupMenu;
    JMenuItem popupMenu_Undo, popupMenu_Redo, popupMenu_Cut, popupMenu_Copy, popupMenu_Paste, popupMenu_Delete,
            popupMenu_SelectAll;
    // ---------------tool bar button
    JButton newButton, openButton, saveButton, saveAsButton, printButton, undoButton, redoButton, cutButton,
            copyButton, pasteButton, deleteButton, searchButton, timeButton, fontButton, boldButton, italicButton,
            fgcolorButton, bgcolorButton, helpButton;
    // text edit area
    static JTextArea Text;
    // status label
    JLabel statusLabel1, statusLabel2, statusLabel3, statusLabel4, statusLabel5, statusLabel6, statusLabel7;
    JToolBar statusBar;
    // ---------------system clip board
    
    // ---------------create undo manager
    protected UndoManager undo = new UndoManager();
    protected UndoableEditListener undoHandler = new UndoHandler();
    // ----------------other variable
    boolean isNewFile = true; // new file whether or not (not save)
    File currentFile; // current file name
    String oldValue; // compare the text if change
    
    private static final int PORT = 64876;
    
    // ----------------set default font
    protected Font defaultFont = new Font("", Font.PLAIN, 12);
    GregorianCalendar time = new GregorianCalendar();
    int hour = time.get(Calendar.HOUR_OF_DAY);
    int min = time.get(Calendar.MINUTE);
    int second = time.get(Calendar.SECOND);
    File saveFileName = null, fileName = null;
    PrintJob printjob = null;// create a print job
    Graphics graphics = null;// print job

    public Notepad() {
        super("Notepad");
        checkIfRunning(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Container container = getContentPane();
        // System.out.println(Text.getDragEnabled()); //support drag and drop
        JScrollPane scroll = new JScrollPane(Text);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Text.setWrapStyleWord(true); // set auto word wrap
        Text.setLineWrap(true);
        Text.setFont(defaultFont); // set default font
        Text.setBackground(Color.white); // set default back color
        Text.setForeground(Color.black); // set default front color
        oldValue = Text.getText(); // get old value of text area
        // --------------------------text monitor listener
        Text.getDocument().addUndoableEditListener(undoHandler); // undo listener
        Text.getDocument().addDocumentListener(this); // get change of monitor listener
        JMenuBar MenuBar = new JMenuBar();
        mFile = new JMenu("File(F)", true); // create menu
        mEdit = new JMenu("Edit(E)", true);
        mMode = new JMenu("Style(O)", true);
        mView = new JMenu("View(V)", true);
        mHelp = new JMenu("Help(H)", true);
        mEdit.addActionListener(new ActionListener() // register action listener
        {
            public void actionPerformed(ActionEvent e) {
                checkMenuItemEnabled(); // set visible of copy cut paste
            }
        });
        mFile.setMnemonic('F');
        mEdit.setMnemonic('E');
        mMode.setMnemonic('O');
        mView.setMnemonic('V');
        mHelp.setMnemonic('H');
        MenuBar.add(mFile);
        MenuBar.add(mEdit);
        MenuBar.add(mMode);
        MenuBar.add(mView);
        MenuBar.add(mHelp);
        // --------------file menu
        mFile_New = new JMenuItem("New(N)", 'N');
        mFile_Open = new JMenuItem("Open(O)", 'O');
        mFile_Save = new JMenuItem("Save(S)", 'S');
        mFile_ASave = new JMenuItem("Save As(A)", 'A');
        mFile_Print = new JMenuItem("Print(P)", 'P');
        mFile_pageSet = new JMenuItem("pageSet(U)", 'U');
        mFile_Exit = new JMenuItem("Exit(X)", 'X');
        mFile_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        mFile_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        mFile_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        mFile_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        mFile_pageSet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
        mFile_New.addActionListener(this); // register action listener
        mFile_Open.addActionListener(this);
        mFile_Save.addActionListener(this);
        mFile_ASave.addActionListener(this);
        mFile_Print.addActionListener(this);
        mFile_pageSet.addActionListener(this);
        mFile_Exit.addActionListener(this);
        mFile.add(mFile_New); // add sub-menu
        mFile.add(mFile_Open);
        mFile.add(mFile_Save);
        mFile.add(mFile_ASave);
        mFile.addSeparator(); // add parting line
        mFile.add(mFile_Print);
        mFile.add(mFile_pageSet);
        mFile.addSeparator(); // add parting line
        mFile.add(mFile_Exit);

        // --------------edit menu
        mEdit_Undo = new JMenuItem("Undo(U)", 'U');
        mEdit_Redo = new JMenuItem("Redo(R)", 'R');
        mEdit_Cut = new JMenuItem("Cut(T)", 'T');
        mEdit_Copy = new JMenuItem("Copy(C)", 'C');
        mEdit_Paste = new JMenuItem("Paste(P)", 'P');
        mEdit_Del = new JMenuItem("Delete(L)", 'L');
        mEdit_Search = new JMenuItem("Find(F)", 'F');
        mEdit_SearchNext = new JMenuItem("Find Next(N)", 'N');
        mEdit_Replace = new JMenuItem("Replace(R)", 'R');
        mEdit_Turnto = new JMenuItem("GoTo(G)", 'G');
        mEdit_SelectAll = new JMenuItem("SelectAll(A)", 'A');
        mEdit_Character = new JMenuItem("Chara Count(B)", 'B');
        mEdit_TimeDate = new JMenuItem("Time/Date(D)", 'D');
        mEdit_Cut.setEnabled(false);
        mEdit_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        mEdit_Redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
        mEdit_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        mEdit_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        mEdit_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        mEdit_Del.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        mEdit_Search.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        mEdit_SearchNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        mEdit_Replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        mEdit_Turnto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
        mEdit_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        mEdit_Character.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
        mEdit_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        mEdit_Undo.addActionListener(this);
        mEdit_Redo.addActionListener(this);// register action listener
        mEdit_Cut.addActionListener(this);
        mEdit_Copy.addActionListener(this);
        mEdit_Paste.addActionListener(this);
        mEdit_Del.addActionListener(this);
        mEdit_Search.addActionListener(this);
        mEdit_SearchNext.addActionListener(this);
        mEdit_Replace.addActionListener(this);
        mEdit_Turnto.addActionListener(this);
        mEdit_SelectAll.addActionListener(this);
        mEdit_Character.addActionListener(this);
        mEdit_TimeDate.addActionListener(this);
        mEdit.add(mEdit_Undo);
        mEdit.add(mEdit_Redo); // add sub-menu
        mEdit.addSeparator(); // add parting line
        mEdit.add(mEdit_Cut);
        mEdit.add(mEdit_Copy);
        mEdit.add(mEdit_Paste);
        mEdit.add(mEdit_Del);
        mEdit.addSeparator();
        mEdit.add(mEdit_Search);
        mEdit.add(mEdit_SearchNext);
        mEdit.add(mEdit_Replace);
        mEdit.add(mEdit_Turnto);
        mEdit.addSeparator();
        mEdit.add(mEdit_SelectAll);
        mEdit.add(mEdit_Character);
        mEdit.add(mEdit_TimeDate);

        // --------------style menu
        formatMenu_LineWrap = new JCheckBoxMenuItem("Word Wrap(W)");
        formatMenu_LineWrap.setMnemonic('W');
        formatMenu_LineWrap.setState(true);
        formatMenu_Font = new JMenuItem("Font(F)", 'F');
        formatMenu_Color = new JMenu("Color");
        formatMenu_Color_FgColor = new JMenuItem("Font Color");
        formatMenu_Color_BgColor = new JMenuItem("Back Color");
        formatMenu_LineWrap.addActionListener(this); // register action listener
        formatMenu_Font.addActionListener(this);
        formatMenu_Color_FgColor.addActionListener(this);
        formatMenu_Color_BgColor.addActionListener(this);
        mMode.add(formatMenu_LineWrap); // add sub menu
        mMode.addSeparator();
        mMode.add(formatMenu_Font);
        mMode.add(formatMenu_Color);
        formatMenu_Color.add(formatMenu_Color_FgColor);
        formatMenu_Color.add(formatMenu_Color_BgColor);

        // --------------view menu
        viewMenu_Status = new JCheckBoxMenuItem("StatusBar(S)");
        viewMenu_Status.setMnemonic('S');
        viewMenu_Status.setState(true);
        viewMenu_Status.addActionListener(this);
        mView.add(viewMenu_Status);

        // --------------help menu
        mHelp_HelpTopics = new JMenuItem("Help(H)", 'H');
        mHelp_About = new JMenuItem("About(A)", 'A');
        mHelp_HelpTopics.addActionListener(this);
        mHelp_About.addActionListener(this);
        mHelp.add(mHelp_HelpTopics);
        mHelp.addSeparator(); // add parting line
        mHelp.add(mHelp_About);

        // -------------------create right click menu
        popupMenu = new JPopupMenu();
        popupMenu_Redo = new JMenuItem("Redo(R)", 'R');
        popupMenu_Undo = new JMenuItem("Undo(U)", 'U');
        popupMenu_Cut = new JMenuItem("Cut(T)", 'T');
        popupMenu_Copy = new JMenuItem("Copy(C)", 'C');
        popupMenu_Paste = new JMenuItem("Paste(P)", 'P');
        popupMenu_Delete = new JMenuItem("Delete(D)", 'D');
        popupMenu_SelectAll = new JMenuItem("Select All(A)", 'A');

        popupMenu_Undo.setEnabled(false);// undo is not usable at first
        // ---------------add parting line in the right click menu
        popupMenu.add(popupMenu_Undo);
        popupMenu.add(popupMenu_Redo);
        popupMenu.addSeparator();
        popupMenu.add(popupMenu_Cut);
        popupMenu.add(popupMenu_Copy);
        popupMenu.add(popupMenu_Paste);
        popupMenu.add(popupMenu_Delete);
        popupMenu.addSeparator();
        popupMenu.add(popupMenu_SelectAll);
        // --------------------register listener for right click menu
        popupMenu_Undo.addActionListener(this);
        popupMenu_Redo.addActionListener(this);
        popupMenu_Cut.addActionListener(this);
        popupMenu_Copy.addActionListener(this);
        popupMenu_Paste.addActionListener(this);
        popupMenu_Delete.addActionListener(this);
        popupMenu_SelectAll.addActionListener(this);
        // --------------------register listener for text area
        Text.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                checkForTriggerEvent(e);
            }

            public void mouseReleased(MouseEvent e) {
                checkForTriggerEvent(e);

            }

            private void checkForTriggerEvent(MouseEvent e) {
                if (e.isPopupTrigger())
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());// position
                                                                         // x,y
                                                                         // show pop-up menu
                else {
                    statusLabel3.setText("Ln: " + TextFuncLogic.getlineNumber(Text));
                }
                checkMenuItemEnabled(); // Setting cut copy paste delete usable
                Text.requestFocus(); // get focus point for edit area
            }
        });

        // ----------------------------create tool bar
        JPanel toolBar = new JPanel();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        Icon newIcon = new ImageIcon("Icons/new.gif");
        Icon openIcon = new ImageIcon("Icons/open.gif");
        Icon saveIcon = new ImageIcon("Icons/save.gif");
        Icon saveAsIcon = new ImageIcon("Icons/saveas.gif");
        Icon printIcon = new ImageIcon("Icons/print.gif");
        Icon undoIcon = new ImageIcon("Icons/undo.gif");
        Icon redoIcon = new ImageIcon("Icons/Redo.gif");
        Icon cutIcon = new ImageIcon("Icons/cut.gif");
        Icon copyIcon = new ImageIcon("Icons/copy.gif");
        Icon pasteIcon = new ImageIcon("Icons/paste.gif");
        Icon deleteIcon = new ImageIcon("Icons/delete.gif");
        Icon searchIcon = new ImageIcon("Icons/search.gif");
        Icon timeIcon = new ImageIcon("Icons/time.gif");
        Icon fontIcon = new ImageIcon("Icons/font.gif");
        Icon boldIcon = new ImageIcon("Icons/bold.gif");
        Icon italicIcon = new ImageIcon("Icons/italic.gif");
        Icon bgcolorIcon = new ImageIcon("Icons/bgcolor.gif");
        Icon fgcolorIcon = new ImageIcon("Icons/fgcolor.gif");
        Icon helpIcon = new ImageIcon("Icons/help.gif");

        newButton = new JButton(newIcon);
        openButton = new JButton(openIcon);
        saveButton = new JButton(saveIcon);
        saveAsButton = new JButton(saveAsIcon);
        printButton = new JButton(printIcon);
        undoButton = new JButton(undoIcon);
        undoButton.setEnabled(false);
        redoButton = new JButton(redoIcon);
        redoButton.setEnabled(false);
        cutButton = new JButton(cutIcon);
        cutButton.setEnabled(false);
        copyButton = new JButton(copyIcon);
        copyButton.setEnabled(false);
        pasteButton = new JButton(pasteIcon);
        pasteButton.setEnabled(false);
        deleteButton = new JButton(deleteIcon);
        deleteButton.setEnabled(false);
        searchButton = new JButton(searchIcon);
        timeButton = new JButton(timeIcon);
        fontButton = new JButton(fontIcon);
        boldButton = new JButton(boldIcon);
        italicButton = new JButton(italicIcon);
        fgcolorButton = new JButton(fgcolorIcon);
        bgcolorButton = new JButton(bgcolorIcon);
        helpButton = new JButton(helpIcon);

        newButton.setPreferredSize(new Dimension(22, 22));
        openButton.setPreferredSize(new Dimension(22, 22));
        saveButton.setPreferredSize(new Dimension(22, 22));
        saveAsButton.setPreferredSize(new Dimension(22, 22));
        printButton.setPreferredSize(new Dimension(22, 22));
        undoButton.setPreferredSize(new Dimension(22, 22));
        redoButton.setPreferredSize(new Dimension(22, 22));
        cutButton.setPreferredSize(new Dimension(22, 22));
        copyButton.setPreferredSize(new Dimension(22, 22));
        pasteButton.setPreferredSize(new Dimension(22, 22));
        deleteButton.setPreferredSize(new Dimension(22, 22));
        searchButton.setPreferredSize(new Dimension(22, 22));
        timeButton.setPreferredSize(new Dimension(22, 22));
        fontButton.setPreferredSize(new Dimension(22, 22));
        boldButton.setPreferredSize(new Dimension(22, 22));
        italicButton.setPreferredSize(new Dimension(22, 22));
        fgcolorButton.setPreferredSize(new Dimension(22, 22));
        bgcolorButton.setPreferredSize(new Dimension(22, 22));
        helpButton.setPreferredSize(new Dimension(22, 22));
        // -----------------------------------register button action listener
        newButton.addActionListener(this);
        openButton.addActionListener(this);
        saveButton.addActionListener(this);
        saveAsButton.addActionListener(this);
        printButton.addActionListener(this);
        undoButton.addActionListener(this);
        redoButton.addActionListener(this);
        cutButton.addActionListener(this);
        copyButton.addActionListener(this);
        pasteButton.addActionListener(this);
        deleteButton.addActionListener(this);
        searchButton.addActionListener(this);
        timeButton.addActionListener(this);
        fontButton.addActionListener(this);
        boldButton.addActionListener(this);
        italicButton.addActionListener(this);
        fgcolorButton.addActionListener(this);
        bgcolorButton.addActionListener(this);
        helpButton.addActionListener(this);
        // ------------------------setting prompt word
        newButton.setToolTipText("new");
        openButton.setToolTipText("open");
        saveButton.setToolTipText("save");
        saveAsButton.setToolTipText("save as");
        printButton.setToolTipText("print");
        undoButton.setToolTipText("undo");
        redoButton.setToolTipText("redo");
        cutButton.setToolTipText("cut");
        copyButton.setToolTipText("copy");
        pasteButton.setToolTipText("paste");
        deleteButton.setToolTipText("delete select");
        searchButton.setToolTipText("replace&search");
        timeButton.setToolTipText("date&time");
        fontButton.setToolTipText("font");
        boldButton.setToolTipText("bold");
        italicButton.setToolTipText("italic");
        fgcolorButton.setToolTipText("font color");
        bgcolorButton.setToolTipText("back color");
        helpButton.setToolTipText("help");
        // if it is unusable, the picture as following
        undoButton.setDisabledIcon(new ImageIcon("Icons/undo1.gif"));
        redoButton.setDisabledIcon(new ImageIcon("Icons/redo1.gif"));
        cutButton.setDisabledIcon(new ImageIcon("Icons/cut1.gif"));
        copyButton.setDisabledIcon(new ImageIcon("Icons/copy1.gif"));
        pasteButton.setDisabledIcon(new ImageIcon("Icons/paste1.gif"));
        deleteButton.setDisabledIcon(new ImageIcon("Icons/delete1.gif"));
        // ------------------------add button
        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(saveAsButton);
        toolBar.add(printButton);
        toolBar.add(undoButton);
        toolBar.add(redoButton);
        toolBar.add(cutButton);
        toolBar.add(copyButton);
        toolBar.add(pasteButton);
        toolBar.add(deleteButton);
        toolBar.add(searchButton);
        toolBar.add(timeButton);
        toolBar.add(fontButton);
        toolBar.add(boldButton);
        toolBar.add(italicButton);
        toolBar.add(fgcolorButton);
        toolBar.add(bgcolorButton);
        toolBar.add(helpButton);

        // --------------------------------------add tool bar into container
        container.add(toolBar, BorderLayout.NORTH);
        // -----------------------------------create and add status bar

        statusBar = new JToolBar();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusLabel1 = new JLabel("Welcome to use our demo program               ");
        statusLabel2 = new JLabel("    Local time" + hour + ":" + min + ":" + second);
        statusLabel3 = new JLabel("Ln: " + TextFuncLogic.getlineNumber(Text));
        statusBar.add(statusLabel1);
        statusBar.addSeparator();
        statusBar.add(statusLabel2);
        statusBar.addSeparator();
        statusBar.add(statusLabel3);
        container.add(statusBar, BorderLayout.SOUTH);
        statusBar.setVisible(true);
        // ------------------------------------change the default icon
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.createImage("Icons/notepad.gif");
        this.setIconImage(image);
        this.setJMenuBar(MenuBar); // add menu bar to window
        container.add(scroll, BorderLayout.CENTER); // add text edit area to container
        this.pack();
        this.setSize(800, 800);
        this.setVisible(true);
        checkMenuItemEnabled();
        Text.requestFocus();
        // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                checkText();
            }
        });
        Clock clock = new Clock();
        clock.start();
    }

    public void saveFile() {
        try {
            FileWriter fw = new FileWriter(saveFileName + ".txt");
            fw.write(Text.getText());
            fw.close();
        } catch (Exception e) {
        }
    }

    public void checkMenuItemEnabled() {
        String selectText = Text.getSelectedText();

        if (selectText == null) {
            mEdit_Cut.setEnabled(false);
            popupMenu_Cut.setEnabled(false);
            cutButton.setEnabled(false);
            mEdit_Copy.setEnabled(false);
            popupMenu_Copy.setEnabled(false);
            copyButton.setEnabled(false);
            mEdit_Del.setEnabled(false);
            popupMenu_Delete.setEnabled(false);
            deleteButton.setEnabled(false);
        } else {
            mEdit_Cut.setEnabled(true);
            popupMenu_Cut.setEnabled(true);
            cutButton.setEnabled(true);
            mEdit_Copy.setEnabled(true);
            popupMenu_Copy.setEnabled(true);
            copyButton.setEnabled(true);
            mEdit_Del.setEnabled(true);
            popupMenu_Delete.setEnabled(true);
            deleteButton.setEnabled(true);
        }

        // able to use paste detect
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Clipboard clipBoard = toolKit.getSystemClipboard();
        Transferable contents = clipBoard.getContents(this);
        if (contents == null) {
            mEdit_Paste.setEnabled(false);
            popupMenu_Paste.setEnabled(false);
            pasteButton.setEnabled(false);
        } else {
            mEdit_Paste.setEnabled(true);
            popupMenu_Paste.setEnabled(true);
            pasteButton.setEnabled(true);
        }
    }

    /* end of my search() */

    // public void actionPerformed(ActionEvent e)
    public void actionPerformed(ActionEvent e) {
        // new
        ExampleFileFilter filter = new ExampleFileFilter();
        if (e.getActionCommand().equals("New(N)") || e.getSource() == newButton)
        // if(e.getSource()==mFile_New||e.getSource()==newButton)
        {
            Text.requestFocus();
            String currentValue = Text.getText();
            boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;

            if (isTextChange) {

                int saveChoose =
                        JOptionPane.showConfirmDialog(this, "Do you want to save the changes?", "Notice",
                                JOptionPane.YES_NO_CANCEL_OPTION);

                if (saveChoose == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setApproveButtonText("OK");
                    fileChooser.setDialogTitle("Save As");
                    int result = fileChooser.showSaveDialog(this);
                    if (result == JFileChooser.CANCEL_OPTION) {
                        statusLabel1.setText("Choose no file");
                        return;
                    }
                    saveFileName = fileChooser.getSelectedFile();
                    if (saveFileName == null || saveFileName.getName().equals(""))
                        JOptionPane.showMessageDialog(this, "Illegal file name", "Illegal file name",
                                JOptionPane.ERROR_MESSAGE);
                    else {
                        saveFile();
                        Text.setText("");
                        this.setTitle("New Document");
                        statusLabel1.setText("New document");
                    }
                } else if (saveChoose == JOptionPane.NO_OPTION) {
                    Text.replaceRange("", 0, Text.getText().length());
                    statusLabel1.setText("New file");
                    this.setTitle("Untitled - Notepad");
                    isNewFile = true;
                    undo.discardAllEdits(); // undo all undo operations
                    mEdit_Undo.setEnabled(false);
                    popupMenu_Undo.setEnabled(false);
                    undoButton.setEnabled(false);
                    mEdit_Redo.setEnabled(false);
                    popupMenu_Redo.setEnabled(false);
                    redoButton.setEnabled(false);
                    oldValue = Text.getText();
                } else if (saveChoose == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            } else {
                // Text.replaceRange("", 0, Text.getText().length());
                Text.setText("");
                statusLabel1.setText("New file");
                this.setTitle("Untitled - Notepad");
                isNewFile = true;
                undo.discardAllEdits();
                mEdit_Undo.setEnabled(false);
                popupMenu_Undo.setEnabled(false);
                undoButton.setEnabled(false);
                mEdit_Redo.setEnabled(false);
                popupMenu_Redo.setEnabled(false);
                redoButton.setEnabled(false);
                oldValue = Text.getText();
            }
        }// new operation end

        // open
        else if (e.getActionCommand().equals("Open(O)") || e.getSource() == openButton) {
            Text.requestFocus();
            String currentValue = Text.getText();
            boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;

            if (isTextChange) {

                int saveChoose =
                        JOptionPane.showConfirmDialog(this, "Do you want to save the changes?", "Notice",
                                JOptionPane.YES_NO_CANCEL_OPTION);

                if (saveChoose == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setApproveButtonText("OK");
                    fileChooser.setDialogTitle("Save As");

                    int result = fileChooser.showSaveDialog(this);

                    if (result == JFileChooser.CANCEL_OPTION) {
                        statusLabel1.setText("Choose no file");
                        return;
                    }

                    saveFileName = fileChooser.getSelectedFile();

                    if (saveFileName == null || saveFileName.getName().equals(""))
                        JOptionPane.showMessageDialog(this, "Illegal file name", "Illegal file name",
                                JOptionPane.ERROR_MESSAGE);
                    else {
                        saveFile();
                        isNewFile = false;
                        currentFile = saveFileName;
                        oldValue = Text.getText();
                        this.setTitle(saveFileName.getName() + "  - Notepad");
                        statusLabel1.setText("Current open file:" + saveFileName.getAbsoluteFile());
                    }
                } else if (saveChoose == JOptionPane.NO_OPTION) {
                    String str = null;
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setApproveButtonText("Ok");
                    fileChooser.setDialogTitle("Open file");
                    int result = fileChooser.showOpenDialog(this);
                    if (result == JFileChooser.CANCEL_OPTION) {
                        statusLabel1.setText("Choose no file");
                        return;
                    }
                    fileName = fileChooser.getSelectedFile();
                    if (fileName == null || fileName.getName().equals(""))
                        JOptionPane.showMessageDialog(this, "Illegal file name", "Illegal file name",
                                JOptionPane.ERROR_MESSAGE);
                    else {
                        try {
                            FileReader fr = new FileReader(fileName);
                            BufferedReader bfr = new BufferedReader(fr);
                            Text.setText("");
                            while ((str = bfr.readLine()) != null) {// read one line each until complete
                                Text.append(str + "\15\12");
                            }// end while
                            this.setTitle(fileName.getName() + "  - Notepad");
                            statusLabel1.setText("Current open file:" + fileName.getAbsoluteFile());
                            fr.close();
                            isNewFile = false;
                            currentFile = fileName;
                            oldValue = Text.getText();
                        } catch (IOException ioException) {
                        }
                    }
                } else {
                    return;
                }
            }

            else {
                String str = null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setApproveButtonText("OK");
                fileChooser.setDialogTitle("Open file");
                int result = fileChooser.showOpenDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel1.setText("Choose no file");
                    return;
                }
                fileName = fileChooser.getSelectedFile();
                if (fileName == null || fileName.getName().equals(""))
                    JOptionPane.showMessageDialog(this, "Illegal file name ", "Illegal file name ",
                            JOptionPane.ERROR_MESSAGE);
                else {
                    try {
                        FileReader fr = new FileReader(fileName);
                        BufferedReader bfr = new BufferedReader(fr);
                        Text.setText("");
                        while ((str = bfr.readLine()) != null) {// read one line each until complete
                            Text.append(str + "\15\12");
                        }// end while

                        this.setTitle(fileName.getName() + "  - Notepad");
                        statusLabel1.setText("Current open file:" + fileName.getAbsoluteFile());
                        fr.close();
                        isNewFile = false;
                        currentFile = fileName;
                        oldValue = Text.getText();
                    } catch (IOException ioException) {
                    }
                }

            }
        }// open operation complete

        // save
        else if (e.getSource() == mFile_Save || e.getSource() == saveButton) {
            filter.addExtension("txt");
            filter.setDescription("TXT Documents");
            Text.requestFocus();
            if (isNewFile) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(filter);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setApproveButtonText("Ok");
                fileChooser.setDialogTitle("Save As");
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel1.setText("Choose no file");
                    return;
                }
                saveFileName = fileChooser.getSelectedFile();
                if (saveFileName == null || saveFileName.getName().equals(""))
                    JOptionPane.showMessageDialog(this, "Illegal file name ", "Illegal file name ",
                            JOptionPane.ERROR_MESSAGE);
                else {
                    saveFile();
                    isNewFile = false;
                    currentFile = saveFileName;
                    oldValue = Text.getText();
                    this.setTitle(saveFileName.getName() + "  - Notepad");
                    statusLabel1.setText("Current open file:" + saveFileName.getAbsoluteFile());
                }
            } else {
                try {
                    FileWriter fw = new FileWriter(currentFile + ".txt");
                    BufferedWriter bfw = new BufferedWriter(fw);
                    bfw.write(Text.getText(), 0, Text.getText().length());
                    bfw.flush();
                    fw.close();
                } catch (IOException ioException) {
                }
            }
        }// save operation complete

        // save as
        else if (e.getSource() == mFile_ASave || e.getSource() == saveAsButton) {
            filter.addExtension("txt");
            filter.setDescription("TXT Documents");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(filter);
            Text.requestFocus();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setApproveButtonText("Ok");
            fileChooser.setDialogTitle("Save As");
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                statusLabel1.setText("Choose no file");
                return;
            }
            saveFileName = fileChooser.getSelectedFile();
            if (saveFileName == null || saveFileName.getName().equals(""))
                JOptionPane.showMessageDialog(this, "Illegal file name ", "Illegal file name ",
                        JOptionPane.ERROR_MESSAGE);
            else {
                saveFile();
                isNewFile = false;
                currentFile = saveFileName;
                oldValue = Text.getText();
                this.setTitle(saveFileName.getName() + "  - Notepad");
                statusLabel1.setText("Current open file:" + saveFileName.getAbsoluteFile());
            }

        }// Save as operation complete

        // print
        else if (e.getSource() == mFile_Print || e.getSource() == printButton) {
            try {
                printjob = this.getToolkit().getPrintJob(this, "OK", null);
                graphics = printjob.getGraphics();// get a print Graphics object
                graphics.translate(200, 300);
                Text.printAll(graphics);// print
                printjob.end();// release
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "print cancel");
            }

        }
        // pageSetup
        else if (e.getSource() == mFile_pageSet) {
            SettingLogic.showSettingPage(this, Text);
        }
        // Character count
        else if (e.getSource() == mEdit_Character) {
            String text = Text.getText();
            if (text != null) {
                String chars = text.trim();
                chars = Notepad.replaceBlank(chars);
                TextFuncLogic.showCharCount(chars.length(),this);
            } else {
                TextFuncLogic.showCharCount(0,this);
            }
        }

        // exit
        else if (e.getSource() == mFile_Exit) {
            int exitChoose =
                    JOptionPane.showConfirmDialog(this, "Do you want to exit?", "Exit notice",
                            JOptionPane.OK_CANCEL_OPTION);
            if (exitChoose == JOptionPane.OK_OPTION) {
                checkText();
            } else {
                return;
            }
        }

        // undo
        else if (e.getSource() == mEdit_Undo || e.getSource() == popupMenu_Undo || e.getSource() == undoButton) {
            Text.requestFocus();
            if (undo.canUndo()) {
                try {
                    undo.undo();

                } catch (CannotUndoException ex) {
                    System.out.println("Unable to undo: " + ex);
                    ex.printStackTrace();
                }

                if (!undo.canUndo()) {
                    mEdit_Undo.setEnabled(false);
                    popupMenu_Undo.setEnabled(false);
                    undoButton.setEnabled(false);

                }
            }
        }
        // re do
        else if (e.getSource() == mEdit_Redo || e.getSource() == popupMenu_Redo || e.getSource() == redoButton) {
            Text.requestFocus();
            if (undo.canRedo()) {
                try {
                    undo.redo();

                } catch (CannotUndoException ex) {
                    System.out.println("Unable to redo: " + ex);
                    ex.printStackTrace();
                }

                if (!undo.canRedo()) {
                    mEdit_Redo.setEnabled(false);
                    popupMenu_Redo.setEnabled(false);
                    redoButton.setEnabled(false);

                }
            }
        }

        // cut
        else if (e.getSource() == mEdit_Cut || e.getSource() == popupMenu_Cut || e.getSource() == cutButton) {
            EditLogic.cut(this);
        }

        // copy
        else if (e.getSource() == mEdit_Copy || e.getSource() == popupMenu_Copy || e.getSource() == copyButton) {
            EditLogic.copy(this);
        }

        // paste
        else if (e.getSource() == mEdit_Paste || e.getSource() == popupMenu_Paste || e.getSource() == pasteButton) {
            EditLogic.paste(this);
        }

        // delete
        else if (e.getSource() == mEdit_Del || e.getSource() == popupMenu_Delete || e.getSource() == deleteButton) {
            Text.requestFocus();
            Text.replaceRange("", Text.getSelectionStart(), Text.getSelectionEnd());
            checkMenuItemEnabled(); // setting usable
        }

        // search
        else if (e.getSource() == mEdit_Search || e.getSource() == searchButton) {
            Text.requestFocus();
            if (e.getSource() == searchButton) {
                Text.requestFocus();
                Text.setCaretPosition(0);
            }
            SearchLogic.search(this, Text);
        }

        // setting next
        else if (e.getSource() == mEdit_SearchNext) {
            SearchLogic.search(this, Text);
        }

        // replace
        else if (e.getSource() == mEdit_Replace) {
            SearchLogic.search(this, Text);
        }

        // go to
        else if (e.getSource() == mEdit_Turnto) {
            final JDialog gotoDialog = new JDialog(this, "Go to this row");
            JLabel gotoLabel = new JLabel("Row number(L):");
            final JTextField linenum = new JTextField(5);
            linenum.setText("1");
            linenum.selectAll();

            JButton okButton = new JButton("Ok");
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    int totalLine = Text.getLineCount();
                    int[] lineNumber = new int[totalLine + 1];
                    String s = Text.getText();
                    int pos = 0, t = 0;

                    while (true) {
                        pos = s.indexOf('\12', pos);
                        // System.out.println("index pos:"+pos);
                        if (pos == -1)
                            break;
                        lineNumber[t++] = pos++;
                    }

                    int gt = 1;
                    try {
                        gt = Integer.parseInt(linenum.getText());
                    } catch (NumberFormatException efe) {
                        JOptionPane.showMessageDialog(null, "Input row number!", "Notice", JOptionPane.WARNING_MESSAGE);
                        linenum.requestFocus(true);
                        return;
                    }

                    if (gt < 2 || gt >= totalLine) {
                        if (gt < 2)
                            Text.setCaretPosition(0);
                        else
                            Text.setCaretPosition(s.length());
                    } else
                        Text.setCaretPosition(lineNumber[gt - 2] + 1);

                    gotoDialog.dispose();
                }

            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gotoDialog.dispose();
                }
            });

            Container con = gotoDialog.getContentPane();
            con.setLayout(new FlowLayout());
            con.add(gotoLabel);
            con.add(linenum);
            con.add(okButton);
            con.add(cancelButton);

            gotoDialog.setSize(200, 100);
            gotoDialog.setResizable(false);
            gotoDialog.setLocation(300, 280);
            gotoDialog.setVisible(true);

        }// go to operation complete

        // insert date and time
        else if (e.getSource() == mEdit_TimeDate || e.getSource() == timeButton) {
            Text.requestFocus();
            SimpleDateFormat currentDateTime = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            Text.insert(currentDateTime.format(new Date()), Text.getCaretPosition());
        }

        // select all
        else if (e.getSource() == popupMenu_SelectAll || e.getSource() == mEdit_SelectAll) {
            Text.selectAll();
        }

        // word wrap
        else if (e.getSource() == formatMenu_LineWrap) {
            if (formatMenu_LineWrap.getState()) {
                Text.setLineWrap(true);
            } else
                Text.setLineWrap(false);
        }

        // setting font style
        else if (e.getSource() == formatMenu_Font || e.getSource() == fontButton) {
            Text.requestFocus();
            new NotepadFont(this,Text);
        }

        // setting font color(front color)
        else if (e.getSource() == formatMenu_Color_FgColor || e.getSource() == fgcolorButton) {
            Text.requestFocus();
            Color color = JColorChooser.showDialog(this, "Change font color", Color.black);
            if (color != null) {
                Text.setForeground(color);
            } else
                return;
        }

        // setting back color for edit area
        else if (e.getSource() == formatMenu_Color_BgColor || e.getSource() == bgcolorButton) {
            Text.requestFocus();
            Color color = JColorChooser.showDialog(this, "Change back color", Color.white);
            if (color != null) {
                Text.setBackground(color);
            } else
                return;
        }

        // setting visible
        else if (e.getSource() == viewMenu_Status) {
            if (viewMenu_Status.getState())
                statusBar.setVisible(true);

            else
                statusBar.setVisible(false);

        }

        // help document
        else if (e.getSource() == mHelp_HelpTopics || e.getSource() == helpButton) {
            JOptionPane.showMessageDialog(this, "This demo support drag and drop\n", "Help Document",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // about
        else if (e.getSource() == mHelp_About) {
            JOptionPane.showMessageDialog(this, "     Notepad\n" + "     Aodan Xin 201271814\n"
                    + "     Xiaofei Wang 201294147\n" + "     Project Demo\n", "About Notepad",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // tool bar bold button
        else if (e.getSource() == boldButton) {
            Text.requestFocus();
            Font tempFont = Text.getFont();

            if (Text.getFont().getStyle() == Font.PLAIN) {
                tempFont = new Font(Text.getFont().getFontName(), Font.BOLD, Text.getFont().getSize());
            } else if (Text.getFont().getStyle() == Font.ITALIC) {
                tempFont = new Font(Text.getFont().getFontName(), Font.BOLD + Font.ITALIC, Text.getFont().getSize());
            } else if (Text.getFont().getStyle() == Font.BOLD) {
                tempFont = new Font(Text.getFont().getFontName(), Font.PLAIN, Text.getFont().getSize());
            } else if (Text.getFont().getStyle() == (Font.BOLD + Font.ITALIC)) {
                tempFont = new Font(Text.getFont().getFontName(), Font.ITALIC, Text.getFont().getSize());
            }

            Text.setFont(tempFont);
        }

        // tool bar italic button
        else if (e.getSource() == italicButton) {
            Text.requestFocus();
            Font tempFont = Text.getFont();

            if (Text.getFont().getStyle() == Font.PLAIN) {
                tempFont = new Font(Text.getFont().getFontName(), Font.ITALIC, Text.getFont().getSize());
            } else if (Text.getFont().getStyle() == Font.ITALIC) {
                tempFont = new Font(Text.getFont().getFontName(), Font.PLAIN, Text.getFont().getSize());
            } else if (Text.getFont().getStyle() == Font.BOLD) {
                tempFont = new Font(Text.getFont().getFontName(), Font.BOLD + Font.ITALIC, Text.getFont().getSize());
            } else if (Text.getFont().getStyle() == (Font.BOLD + Font.ITALIC)) {
                tempFont = new Font(Text.getFont().getFontName(), Font.BOLD, Text.getFont().getSize());
            }

            Text.setFont(tempFont);
        }

    }// actionPerformed() complete

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    class Clock extends Thread 
    { // simulation clock
        public void run() 
        {
            while (true) 
            {
                GregorianCalendar time = new GregorianCalendar();
                int hour = time.get(Calendar.HOUR_OF_DAY);
                int min = time.get(Calendar.MINUTE);
                int second = time.get(Calendar.SECOND);
                statusLabel2.setText("    Local time" + hour + ":" + min + ":" + second);
                try 
                {
                    Thread.sleep(950);
                } catch (InterruptedException exception) 
                {
                }

            }
        }
    }

    public void checkText() {
        Text.requestFocus();
        String currentValue = Text.getText();
        boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
        if (isTextChange) {

            int saveChoose =
                    JOptionPane.showConfirmDialog(this, "Do you want to save the changes?", "Notice",
                            JOptionPane.YES_NO_CANCEL_OPTION);

            if (saveChoose == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setApproveButtonText("Ok");
                fileChooser.setDialogTitle("Save As");

                int result = fileChooser.showSaveDialog(this);

                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel1.setText("Choose no file!");
                    return;
                }

                saveFileName = fileChooser.getSelectedFile();

                if (saveFileName == null || saveFileName.getName().equals(""))
                    JOptionPane.showMessageDialog(this, "Illegal file name ", "Illegal file name ",
                            JOptionPane.ERROR_MESSAGE);
                else {
                    saveFile();
                    Text.setText("");
                    this.setTitle("New Document");
                    statusLabel1.setText("New Document");
                }
            }

            else if (saveChoose == JOptionPane.NO_OPTION) {
                System.exit(0);
            }

            else if (saveChoose == JOptionPane.CANCEL_OPTION) {
                Text.requestFocus();
            }
        }

        else if (!isTextChange) {
            System.exit(0);
        }
    }
    


    public void removeUpdate(DocumentEvent e) {
        mEdit_Undo.setEnabled(true);
        popupMenu_Undo.setEnabled(true);
        undoButton.setEnabled(true);
        mEdit_Redo.setEnabled(true);
        popupMenu_Redo.setEnabled(true);
        redoButton.setEnabled(true);
    }

    public void insertUpdate(DocumentEvent e) {
        mEdit_Undo.setEnabled(true);
        popupMenu_Undo.setEnabled(true);
        undoButton.setEnabled(true);
        mEdit_Redo.setEnabled(true);
        popupMenu_Redo.setEnabled(true);
        redoButton.setEnabled(true);
    }

    public void changedUpdate(DocumentEvent e) {
        mEdit_Undo.setEnabled(true);
        popupMenu_Undo.setEnabled(true);
        undoButton.setEnabled(true);
        mEdit_Redo.setEnabled(true);
        popupMenu_Redo.setEnabled(true);
        redoButton.setEnabled(true);
    }

    // End of DocumentListener

    // implement interface UndoableListener: Class UndoHandler
    class UndoHandler implements UndoableEditListener 
    {
        public void undoableEditHappened(UndoableEditEvent uee) 
        {
            undo.addEdit(uee.getEdit());
        }
    }
  
    public static void main(String s[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException 
    {
        // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Text = new JTextArea();
        Text.setDragEnabled(true); // support drag and drop auto
        Text.setTransferHandler(new FileTransferHandler(Text));
        new Notepad();
        
    }

	private static void checkIfRunning(JFrame frame) 
    {
    	ServerSocket socket = null;
    	  try 
    	  {
    		  socket = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
    	  }
    	  catch (BindException e) 
    	  {
    	    System.err.println("Already running.");   	    
    	    JOptionPane.showMessageDialog(frame,
    	            "Already running");
            System.exit(1);
    	  }
    	  catch (IOException e) 
    	  {
    	    System.err.println("Unexpected error.");
    	    e.printStackTrace();
    	    System.exit(2);
    	  }
    }
}


