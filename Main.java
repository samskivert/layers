import view.*;

import controller.Control;

import java.awt.EventQueue;
import javax.swing.UIManager;

class Main {
    public static void main(String[]s) {
        EventQueue.invokeLater(new Runnable() {
                public void run() {
                    LayerEditor.bootEditor();
                    LayerEditor.getInstance().addLayerListener(new EditorListener());
                    Control.addBaseLayer();
                }
            });
    }
}
