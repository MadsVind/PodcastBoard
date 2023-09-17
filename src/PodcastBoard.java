
/* TODO
 * - figure out wtf to do with buttons - PLS this is still a problem )':
 *
 * - Add functionality edit channels
 * - Add way to lock and or change window size
 * - format into MVC
 * - comment
 */


import javax.swing.*;
import java.awt.*;
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
    Hashtable<String, Image> imageHashtable = new Hashtable<>();

    //Api key
    final boolean USE_API_KEY = true;

    String apiKey = "";

    Model model;
    UI ui;

    //podcasts
    private ArrayList<Podcast> podcasts;

    private static PodcastBoard pb = null;

    public static synchronized PodcastBoard getInstance()
    {
        if (pb == null)
            pb = new PodcastBoard ();

        return pb;
    }

    public static void main(String[] args) throws Exception {
        PodcastBoard pb = PodcastBoard.getInstance();
        pb.run();
    }

    public void run() throws Exception {
        model = Model.getInstance();
        ui    = UI.getInstance();

        imageHashtable.put("frameIcon",    model.imgFromRelPath(ICON_PATH));
        imageHashtable.put("settingImage", model.imgFromRelPath(SETTINGS_IMAGE_PATH));
        imageHashtable.put("addImage",     model.imgFromRelPath(ADD_IMAGE_PATH));
        imageHashtable.put("delImage",     model.imgFromRelPath(DEL_IMAGE_PATH));
        imageHashtable.put("editImage",    model.imgFromRelPath(EDIT_IMAGE_PATH));
        imageHashtable.put("acceptImage",  model.imgFromRelPath(ACCEPT_IMAGE_PATH));

        ArrayList<JPanel> podcastThumbnails = new ArrayList<>();
        podcasts                            = new ArrayList<>();

        if (model.fileExists(PODCASTS_FILE_PATH)) {
            podcasts = model.readPodcastListFromFile(PODCASTS_FILE_PATH);
        }

        getUpdatedThumbnails();

        int paramAmount = ui.mostParams(podcasts);
        String[][] podcastsInfoLists = Podcast.getSeachDataAs2dArr(podcasts, paramAmount);


        JPanel podcastListPanel = ui.createPodcastListPanel(podcastsInfoLists);
        JPanel podcastCard      = ui.createPodcastCard(imageHashtable.get("settingImage"), podcastThumbnails);
        JPanel settingsCard     = ui.createSettingsCard(imageHashtable.get("settingImage"), podcastListPanel);


        ui.createFrameWithCardLayout("PodcastBoard",1200, 800, imageHashtable.get("frameIcon"), podcastCard, settingsCard);

        model.setSettingsButtonBehaivior(ui.getButtonByName("settingsButton"), ui.cl, ui.cards);
        model.setSettingsButtonBehaivior(ui.getButtonByName("podcastsButton"), ui.cl, ui.cards);
        ui.updatePodcastPanel(getUpdatedThumbnails());
        podcastCard.revalidate();
        podcastCard.repaint();

    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public Image getImage(String imageName) {
        return imageHashtable.get(imageName);
    }

    public Image urlToImage(String url) {
        return model.imgFromWebPath(url);
    }

    public void updatePodcastsFile() throws Exception {
        System.out.println("write podcast to file");
        model.writePodcastListToFile(podcasts, PODCASTS_FILE_PATH);
    }

    public ArrayList<JPanel> getUpdatedThumbnails() {
        if (USE_API_KEY) {
            this.apiKey = model.getApiKey(model.getDirPath(API_KEY_RELATIVE_PATH));
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
}