import view.*;

import controller.Control;
import model.edittree.*;

import java.awt.EventQueue;
import javax.swing.UIManager;

class Main {
    static String subst1 = "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel";
    static String subst2 = "org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel";
    static String subst3 = "org.jvnet.substance.skin.SubstanceCremeLookAndFeel";
    static String subst4 = "org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel";
    static String subst5 = "org.jvnet.substance.skin.SubstanceSaharaLookAndFeel";
    static String subst6 = "org.jvnet.substance.skin.SubstanceModerateLookAndFeel";
    static String subst7 = "org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel";
    static String subst8 = "org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel";
    static String subst9 = "org.jvnet.substance.skin.SubstanceNebulaLookAndFeel";
    static String subst10 = "org.jvnet.substance.skin.SubstanceAutumnLookAndFeel";
    static String subst11 = "org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel";
    static String subst12 = "org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel";
    static String subst13 = "org.jvnet.substance.skin.SubstanceDustLookAndFeel";
    static String subst14 = "org.jvnet.substance.skin.SubstanceDustCoffeeLookAndFeel";
    static String subst15 = "org.jvnet.substance.skin.SubstanceGeminiLookAndFeel";

    public static void main(String[]s) {
        EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        //javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel");
                        //UIManager.setLookAndFeel(subst3);
                    } catch(Exception e) {
                        System.out.println("L&F failed");
                    }

                    LayerEditor.bootEditor();
                    LayerEditor.getInstance().addLayerListener(new EditorListener());
                    Control.addBaseLayer();
                }
            });
    }
}
