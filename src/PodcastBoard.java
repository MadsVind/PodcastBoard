import java.awt.*;

import java.awt.event.ActionListener;
import java.io.*;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import javax.swing.*;

/* TODO
 * - Add functionality add, delete and edit channels
 * - Add a way to enter user api_key
 * - Add way to lock and or change window size
 * - Make gui with which the user, ME!, can use.
 * - format into MVC
 * - comment
 */

public class PodcastBoard {
    //EP (Endpoints) for different used parts of the YouTube v3 api
    static String YOUTUBE_EP           = "https://youtube.googleapis.com/youtube/v3/";
    static String SEARCH_EP            = YOUTUBE_EP + "search?";
    static String CHANNEL_SEARCH_EP    = SEARCH_EP + "type=channel";
    static String BY_CHANNEL_SEARCH_EP = SEARCH_EP + "channelId=";
    static String PART_SNIPPET_EP      = "&part=snippet";
    static String MAX_RESULTS_EP       = "&maxResults=";
    static String ORDER_EP             = "&order=";
    static String SEARCH_PARAM_EP      = "&q=";
    static String API_KEY_EP           = "&key=";

    //Paths
    static String DIR_PATH            = "";
    static String PODCASTS_FILE_PATH  = "\\resources\\PODCASTS.ser";
    //Image Relative paths
    static String ICON_PATH           = "resources/PodcastBoardIcon.png";
    static String SETTINGS_IMAGE_PATH = "resources/settings_icon.png";

    //Frame settings
    static int FRAME_WIDTH  = 1200;
    static int FRAME_HEIGHT = 800;

    static HttpClient httpClient = HttpClient.newHttpClient();

    //Api settings
    static boolean USE_API = false;
    static String apiKey = "";

    public static void main(String[] args) throws Exception {
        PodcastBoard pb = new PodcastBoard();
        pb.run();
    }

    private void initDirPath() {
        DIR_PATH = new File("").getAbsolutePath() + "\\src";
    }

    private void initApiKey() {
        apiKey = fileToStr(DIR_PATH + "\\resources\\API_KEY.txt");
        if (apiKey == null) System.exit(0);
    }

    public void removeLabel(JLabel label) {
        Container parent = label.getParent();
        parent.remove(label);
        parent.validate();
        parent.repaint();
    }

    private JPanel createPodcastPanel(String channelName, String searchParam) throws Exception {
        String searchChannelJson = searchChannelJson(channelName.replaceAll(" ", "%20"));
        println(searchChannelJson);
        String channelId         = jsonByHitIndex("channelId", 0, searchChannelJson);
        println(channelId);
        String videoJson         = searchVideoJson(searchParam.replaceAll(" ", "%20"), channelId);
        String thumbnailUrl      = jsonByHitIndex("url", 2, videoJson);

        Image image = imgFromWebPath(thumbnailUrl);
        JLabel thumbnail  = makeImgLabel(500, 300, image);
        JLabel videoTitle = new JLabel(jsonByHitIndex("title", 0, videoJson));
        videoTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel podcastEp  = new JPanel();
        podcastEp.setLayout(new BoxLayout(podcastEp, BoxLayout.Y_AXIS));
        podcastEp.add(thumbnail);
        podcastEp.add(videoTitle);
        return podcastEp;
    }

    public JLabel makeImgLabel(int width, int height, Image img) {
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_DEFAULT)));
        return label;
    }

    public Image imgFromWebPath(String path) {
        try {
            URL url = new URL(path);
            return ImageIO.read(url);
        } catch (IOException e) {
            return null;
        }
    }

    public Image imgFromRelPath(String path) {
        URL url = PodcastBoard.class.getResource(path);
        return Toolkit.getDefaultToolkit().getImage(url);
    }

    private void run() throws Exception {
        initDirPath();
        initApiKey();

        JFrame frame = new JFrame("Podcast Board");
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new BorderLayout());

        JPanel cards = createCardLayout();

        frame.add(cards);

        Image icon = imgFromRelPath(ICON_PATH);
        frame.setIconImage(icon);
        frame.setVisible(true);
    }

    public JPanel createCardLayout() throws Exception {
        JPanel cards = new JPanel();

        CardLayout cl = new CardLayout();

        cards.setLayout(cl);

        JPanel podcastPanel = createPodcastPanel(cl, cards);
        JPanel settingsPanel = createSettingsPanel(cl, cards);

        cards.add(podcastPanel, "1");
        cards.add(settingsPanel, "2");
        cl.show(cards, "1");
        return cards;
    }

    // make function for splitpanel
    public JPanel createPodcastPanel(CardLayout cl, JPanel cards) throws Exception {
        JPanel podcastPanel  = new JPanel(new BorderLayout());
        JPanel podcastPanelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel podcastPanelBot = new JPanel(new FlowLayout());

        podcastPanel.add(podcastPanelTop, BorderLayout.NORTH);
        podcastPanel.add(podcastPanelBot, BorderLayout.CENTER);

        JButton toSettingsButton = createImageButton("resources/settings_icon.png");

        ActionListener switchButtonListener = e -> cl.next(cards);

        toSettingsButton.addActionListener(switchButtonListener);
        podcastPanelTop.add(toSettingsButton);

        ArrayList<JPanel> podcastPanels = new ArrayList<>();

        if (USE_API) {
            podcastPanels.add(createPodcastPanel("Forehead Fables", "- Ep."));
            podcastPanels.add(createPodcastPanel("Linus Tech Tips", "WAN Show"));
        }

        podcastPanels.forEach(podcastPanelBot::add);

        return podcastPanel;
    }

    public JPanel createSettingsPanel(CardLayout cl, JPanel cards) throws Exception {
        JPanel settingsPanel = new JPanel(new BorderLayout());
        JPanel settingsPanelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel settingsPanelBot = new JPanel(new FlowLayout());

        settingsPanel.add(settingsPanelTop, BorderLayout.NORTH);
        settingsPanel.add(settingsPanelBot, BorderLayout.WEST);

        JButton toPodcastsButton = createImageButton(SETTINGS_IMAGE_PATH);

        ActionListener switchButtonListner = e -> cl.next(cards);

        toPodcastsButton.addActionListener(switchButtonListner);
        settingsPanelTop.add(toPodcastsButton);

        JPanel addPodcastPanel = createPodcastPanel();

        settingsPanelBot.add(addPodcastPanel);

        return settingsPanel;
    }

    public JPanel createPodcastPanel() throws Exception {
        JPanel addPodcastPanel = new JPanel();
        JPanel addDelPanel     = new JPanel();
        JPanel podcastsPanel   = new JPanel();

        addDelPanel.setBackground(Color.GRAY);

        addPodcastPanel.add(addDelPanel);
        addPodcastPanel.add(podcastsPanel);

        JButton addPodcast = new JButton("+");
        JButton delPodcast = new JButton("-");

        addDelPanel.add(addPodcast);
        addDelPanel.add(delPodcast);

        ArrayList<Podcast> podcasts;
        if (fileExists(PODCASTS_FILE_PATH)) {
            podcasts = readPodcastListFromFile(PODCASTS_FILE_PATH);
        } else {
            podcasts = new ArrayList<>();
        }

        Podcast FF = new Podcast("Forehead Fables Podcast");
        FF.addParam("Ep.");


        podcasts.add(FF);

        //Add table titles
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Channel");
        int paramAmount = mostParams(podcasts);
        for (int i = 1; i <= paramAmount; i++) {
            columnNames.add("param " + i);
        }


        String[][] podcastsInfoLists = new String[podcasts.size()][paramAmount+1];

        System.out.println();
        for (int i = 0; i < podcasts.size(); i++) {
            for (int j = 0; j <= paramAmount; j++) {
                Podcast podcast = podcasts.get(i);

                if (j == 0) {
                    podcastsInfoLists[i][0] = podcast.getName();
                }
                else if (j <= podcast.getParamAmount()) {
                    podcastsInfoLists[i][j] = podcast.getParams().get(j-1);
                }
                else podcastsInfoLists[i][j] = "";
            }
        }

        JTable table      = new JTable(podcastsInfoLists, columnNames.toArray());
        podcastsPanel.add(table);

        return addPodcastPanel;
    }

    private int mostParams(ArrayList<Podcast> podcasts) {
        int mostParams = 0;
        for (Podcast podcast : podcasts) {
            int tempAmount = podcast.getParamAmount();
            if (tempAmount > mostParams) mostParams = tempAmount;
        }
        return mostParams;
    }

    public boolean fileExists(String fileRelativePath) {
        String filePath = DIR_PATH + "\\" + fileRelativePath;
        File file = new File(filePath);
        return file.exists();
    }

    public void writePodcastListToFile(ArrayList<Podcast> obj, String fileRelativePath) throws Exception {
        String filePath = DIR_PATH + "\\" + fileRelativePath;
        File file = new File(filePath);
        if (file.exists()) file.delete();
        try  {
            FileOutputStream fileoutputStream = new FileOutputStream(DIR_PATH + "\\" + fileRelativePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileoutputStream);
            objectOutputStream.writeObject(obj);

            println("");
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    public ArrayList<Podcast> readPodcastListFromFile(String fileName) throws Exception {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            ArrayList<Podcast> podcastList = (ArrayList<Podcast>) objectInputStream.readObject();

            fileInputStream.close();
            objectInputStream.close();

            return podcastList;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    //public int mostParams()

    public JButton createImageButton(String imagePath) {
        Image settingImage = imgFromRelPath(imagePath).getScaledInstance(40, 40, Image.SCALE_DEFAULT);
        JButton toPodcastsButton = new JButton(new ImageIcon(settingImage));
        toPodcastsButton.setBorder(BorderFactory.createEmptyBorder());
        toPodcastsButton.setContentAreaFilled(false);

        return toPodcastsButton;
    }

    public String jsonByHitIndex(String key, int index, String json) {
        List<String> list = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        check(key, jsonParser.parse(json), list);
        return list.get(index).replaceAll("\"", "");
    }

    public String getRequestJson(String uriStr) throws Exception {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI( uriStr))
                .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, BodyHandlers.ofString());

        return getResponse.body();
    }

    public String searchVideoJson(String searchParam, String channelId) throws Exception {
        String maxResults = "1";
        String order = "date";
        return getRequestJson(BY_CHANNEL_SEARCH_EP + channelId +
                        MAX_RESULTS_EP + maxResults +
                        ORDER_EP + order +
                        SEARCH_PARAM_EP + searchParam +
                        PART_SNIPPET_EP +
                        API_KEY_EP + apiKey);
    }

    public String searchChannelJson(String channelName) throws Exception {
        String maxResults = "1";
        return getRequestJson(CHANNEL_SEARCH_EP +
                        MAX_RESULTS_EP + maxResults +
                        SEARCH_PARAM_EP + channelName +
                        API_KEY_EP + apiKey);
    }

    // Taken from https://www.codejava.net/java-se/file-io/how-to-read-and-write-text-file-in-java
    public static String fileToStr(String fileName) {
        try {
            FileReader reader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String lineStr;
            String fileStr = "";

            while ((lineStr = bufferedReader.readLine()) != null) {
                fileStr += lineStr;
            }
            reader.close();
            return fileStr;

        } catch (IOException e) {
            return null;
        }
    }

    // Taken from Safwan Hijazi on stack overflow
    // https://stackoverflow.com/a/31158468
    private static void check(String key, JsonElement jsonElement, List<String> list) {

        if (jsonElement.isJsonArray()) {
            for (JsonElement jsonElement1 : jsonElement.getAsJsonArray()) {
                check(key, jsonElement1, list);
            }
        } else {
            if (jsonElement.isJsonObject()) {
                Set<Map.Entry<String, JsonElement>> entrySet = jsonElement
                        .getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    String key1 = entry.getKey();
                    if (key1.equals(key)) {
                        list.add(entry.getValue().toString());
                    }
                    check(key, entry.getValue(), list);
                }
            } else {
                if (jsonElement.toString().equals(key)) {
                    list.add(jsonElement.toString());
                }
            }
        }
    }

    public static void println(Object e) {
        System.out.println(e.toString());
    }

    public static void print(Object e) {
        System.out.print(e.toString());
    }

}