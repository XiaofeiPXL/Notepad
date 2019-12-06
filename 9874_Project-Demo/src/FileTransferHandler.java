import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;


public class FileTransferHandler extends TransferHandler {
    JTextArea Text;

    public FileTransferHandler(JTextArea Text) {
        this.Text = Text;
    }

    public boolean importData(JComponent c, Transferable t) {
        try {
            List files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
            addFilesToFilePathList(files);
            return true;
        } catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }

    private void addFilesToFilePathList(List files) {
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            File file = (File) iter.next();
            String str = null;
            try {
                FileReader fr = new FileReader(file);
                BufferedReader bfr = new BufferedReader(fr);
                Text.setText("");
                while ((str = bfr.readLine()) != null) {
                    Text.append(str + "\15\12");
                }
            } catch (Exception b) {
            }
        }
    }
}