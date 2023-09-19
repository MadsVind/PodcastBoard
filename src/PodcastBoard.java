import java.awt.*;

public class PodcastBoard {
    static String API_KEY_RELATIVE_PATH = "\\resources\\API_KEY.txt";

    //"DB" file Path
    final static String PODCASTS_FILE_PATH  = "\\resources\\PODCASTS.ser";

    //Image Relative paths
    final static String ICON_PATH                = "resources/images/PodcastBoardIcon.png";

    final static String SETTINGS_DARK_IMAGE_PATH = "resources/images/darkImages/settingsIcon.png";
    final static String ADD_DARK_IMAGE_PATH      = "resources/images/darkImages/+IconDarken.png";
    final static String DEL_DARK_IMAGE_PATH      = "resources/images/darkImages/-IconDarken.png";
    final static String EDIT_DARK_IMAGE_PATH     = "resources/images/darkImages/editIconDarken.png";
    final static String ACCEPT_DARK_IMAGE_PATH   = "resources/images/darkImages/acceptIconDarken.png";

    final static String SETTINGS_IMAGE_PATH = "resources/images/brightImages/settingsIconGrey.png";
    final static String ADD_IMAGE_PATH      = "resources/images/brightImages/+Icon.png";
    final static String DEL_IMAGE_PATH      = "resources/images/brightImages/-Icon.png";
    final static String EDIT_IMAGE_PATH     = "resources/images/brightImages/editIcon.png";
    final static String ACCEPT_IMAGE_PATH   = "resources/images/brightImages/acceptIcon.png";


    public static void main(String[] args)  {
        Model model = Model.getInstance();
        
        Image frameIcon    = model.imgFromRelPath(ICON_PATH);

        Image settingImage = model.imgFromRelPath(SETTINGS_IMAGE_PATH);
        Image addImage     = model.imgFromRelPath(ADD_IMAGE_PATH);
        Image delImage     = model.imgFromRelPath(DEL_IMAGE_PATH);
        Image editImage    = model.imgFromRelPath(EDIT_IMAGE_PATH);
        Image acceptImage  = model.imgFromRelPath(ACCEPT_IMAGE_PATH);

        Image settingImageDark = model.imgFromRelPath(SETTINGS_DARK_IMAGE_PATH);
        Image addImageDark     = model.imgFromRelPath(ADD_DARK_IMAGE_PATH);
        Image delImageDark     = model.imgFromRelPath(DEL_DARK_IMAGE_PATH);
        Image editImageDark    = model.imgFromRelPath(EDIT_DARK_IMAGE_PATH);
        Image acceptImageDark  = model.imgFromRelPath(ACCEPT_DARK_IMAGE_PATH);

        UI ui       = new UI(800, 1200,
                             frameIcon, settingImage, addImage, delImage, editImage, acceptImage,
                             settingImageDark, addImageDark, delImageDark, editImageDark, acceptImageDark);
        Controller controller = new Controller(ui, model);

        ui.setVisible(true);
    }

}