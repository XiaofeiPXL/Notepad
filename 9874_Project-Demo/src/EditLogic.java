import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JFrame;
import javax.swing.JTextArea;


public class EditLogic {

    static Toolkit toolKit = Toolkit.getDefaultToolkit();
    static Clipboard clipBoard = toolKit.getSystemClipboard();
    
    public static void cut(Notepad pad){
        Notepad.Text.requestFocus();
        String text = Notepad.Text.getSelectedText();
        StringSelection selection = new StringSelection(text);
        clipBoard.setContents(selection, null);
        Notepad.Text.replaceRange("", Notepad.Text.getSelectionStart(), Notepad.Text.getSelectionEnd());
        pad.checkMenuItemEnabled(); // checking usable
    }
    
    public static void copy(Notepad pad){
        Notepad.Text.requestFocus();
        String text = Notepad.Text.getSelectedText();
        StringSelection selection = new StringSelection(text);
        clipBoard.setContents(selection, null);
        pad.checkMenuItemEnabled(); // checking usable
    }
    
    public static void paste(Notepad pad){
        Notepad.Text.requestFocus();
        Transferable contents = clipBoard.getContents(pad);
        if (contents == null)
            return;
        String text;
        text = "";

        try {
            text = (String) contents.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception exception) {
        }

        Notepad.Text.replaceRange(text, Notepad.Text.getSelectionStart(), Notepad.Text.getSelectionEnd());
        pad.checkMenuItemEnabled(); // setting usable
    }
}
