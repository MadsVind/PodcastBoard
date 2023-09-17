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


    public static void main(String[] args)  {
        Model model = Model.getInstance();
        
        Image frameIcon    = model.imgFromRelPath(ICON_PATH);
        Image settingImage = model.imgFromRelPath(SETTINGS_IMAGE_PATH);
        Image addImage     = model.imgFromRelPath(ADD_IMAGE_PATH);
        Image delImage     = model.imgFromRelPath(DEL_IMAGE_PATH);
        Image editImage    = model.imgFromRelPath(EDIT_IMAGE_PATH);
        Image acceptImage  = model.imgFromRelPath(ACCEPT_IMAGE_PATH);

        UI ui       = new UI(800, 1200, frameIcon, settingImage, addImage, delImage, editImage, acceptImage);
        Controller controller = new Controller(ui, model);

        ui.setVisible(true);
    }

}