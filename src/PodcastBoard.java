
/* TODO
 * - Couple this shit together again
 * - figure out wtf to do with buttons
 *
 * - Add functionality add, delete and edit channels
 * - Add a way to enter user api_key
 * - Add way to lock and or change window size
 * - Make gui with which the user, ME!, can use.
 * - format into MVC
 * - comment
 */


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class PodcastBoard {
    static String API_KEY_RELATIVE_PATH = "\\resources\\API_KEY.txt";

    //"DB" file Path
    final static String PODCASTS_FILE_PATH  = "\\resources\\PODCASTS.ser";

    //Image Relative paths
    final static String ICON_PATH           = "resources/PodcastBoardIcon.png";
    final static String SETTINGS_IMAGE_PATH = "resources/settings_icon.png";

    //Api key
    final boolean USE_API_KEY = false;

    String apiKey = "";

    Model model;
    UI ui;

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

        Image frameIcon    = model.imgFromRelPath(ICON_PATH);
        Image settingImage = model.imgFromRelPath(SETTINGS_IMAGE_PATH);

        JButton toSettingsButton = ui.createImageButton(settingImage, 40, 40);
        JButton toPodcastsButton = ui.createImageButton(settingImage, 40, 40);

        ArrayList<JPanel> podcastThumbnails = new ArrayList<>();
        ArrayList<Podcast> podcasts         = new ArrayList<>();

        Podcast FFP = new Podcast("Forehead Fables Podcast");
        FFP.addParam("Ep.");

        podcasts.add(FFP);

        if (USE_API_KEY) {
            this.apiKey = model.getApiKey(model.getDirPath(API_KEY_RELATIVE_PATH));

            if (apiKey.isEmpty()) apiKey = ui.getApiKey();
            else                  ui.setApiKey(apiKey);

            model.updatePodcastInfo(FFP, apiKey);
            podcastThumbnails.add(ui.createPodcastFront(FFP));
        }

        int paramAmount = model.mostParams(podcasts);
        String[][] podcastsInfoLists = Podcast.getSeachDataAs2dArr(podcasts, paramAmount);

        JPanel podcastCard      = ui.createPodcastCard(toSettingsButton, podcastThumbnails);
        JPanel podcastListPanel = ui.createPodcastListPanel(podcastsInfoLists);
        JPanel settingsCard     = ui.createSettingsCard(toPodcastsButton, podcastListPanel);

        ui.createFrameWithCardLayout(1200, 800, frameIcon, podcastCard, settingsCard);

        model.setSettingsButtonBehaivior(toSettingsButton, ui.cl, ui.cards);
        model.setSettingsButtonBehaivior(toPodcastsButton, ui.cl, ui.cards);
    }
}