import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

public class PodcastBoard {
    static String API_KEY_RELATIVE_PATH = "\\resources\\API_KEY.txt";

    //"DB" file Path
    final static String PODCASTS_FILE_PATH  = "\\resources\\PODCASTS.ser";

    //Image Relative paths
    final static String ICON_PATH           = "resources/images/PodcastBoardIcon.png";
    final static String SETTINGS_IMAGE_PATH = "resources/images/settings_icon.png";
    final static String ADD_IMAGE_PATH      = "resources/images/+Icon.png";
    final static String DEL_IMAGE_PATH      = "resources/images/-Icon.png";
    final static String EDIT_IMAGE_PATH     = "resources/images/editIcon.png";
    final static String ACCEPT_IMAGE_PATH   = "resources/images/acceptIcon.png";

    //Image hashmap
    //Hashtable<String, Image> imageHashtable = new Hashtable<>();

    //Api key
    final boolean USE_API_KEY = true;

    public static void main(String[] args)  {
        Model model = Model.getInstance();
        UI    ui    = UI.getInstance();

        Image frameIcon    = model.imgFromRelPath(ICON_PATH);
        Image settingImage = model.imgFromRelPath(SETTINGS_IMAGE_PATH);
        Image addImage     = model.imgFromRelPath(ADD_IMAGE_PATH);
        Image delImage     = model.imgFromRelPath(DEL_IMAGE_PATH);
        Image editImage    = model.imgFromRelPath(EDIT_IMAGE_PATH);
        Image acceptImage  = model.imgFromRelPath(ACCEPT_IMAGE_PATH);
    }


    /*
    public ArrayList<JPanel> getUpdatedThumbnails() {
        if (USE_API_KEY) {
            this.apiKey = model.initApiKeyFromFile(model.getDirPath(API_KEY_RELATIVE_PATH));
            if (!ui.getApiKey().isEmpty() && apiKey.isEmpty()) {
                apiKey = ui.getApiKey();
                model.strToFile(model.getDirPath(API_KEY_RELATIVE_PATH), apiKey);
            }
            else ui.setApiKey(apiKey);
            model.updatePodcasts(podcasts, apiKey);
            return ui.PodcastToThumbnails(podcasts);
        }
        else System.out.println("Api usage turned off");
        return null;
    }

    public void setSettingsButtonBehavior(JButton settingsButton, CardLayout cl, JPanel cards) {
        ActionListener switchButtonListener = e -> cl.next(cards);
        settingsButton.addActionListener(switchButtonListener);
    }
    */


}