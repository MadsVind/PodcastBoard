import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Model {
    //EP (Endpoints) for different used parts of the YouTube v3 api
    private final String YOUTUBE_EP           = "https://youtube.googleapis.com/youtube/v3/";
    private final String SEARCH_EP            = YOUTUBE_EP + "search?";
    private final String CHANNEL_SEARCH_EP    = SEARCH_EP + "type=channel";
    private final String BY_CHANNEL_SEARCH_EP = SEARCH_EP + "channelId=";
    private final String PART_SNIPPET_EP      = "&part=snippet";
    private final String MAX_RESULTS_EP       = "&maxResults=";
    private final String ORDER_EP             = "&order=";
    private final String SEARCH_PARAM_EP      = "&q=";
    private final String API_KEY_EP           = "&key=";

    //Path to storage files
    private final String API_KEY_RELATIVE_PATH = "resources\\saveFiles\\API_KEY.txt";
    private final String PODCASTS_FILE_PATH    = "resources\\saveFiles\\PODCASTS.ser";
    private final String SETTINGS_PATH      = "resources\\saveFiles\\SETTINGS.txt";


    private final HttpClient httpClient = HttpClient.newHttpClient();

    //Apikey
    private String apiKey = "";

    //Podcasts
    private ArrayList<Podcast> podcasts = new ArrayList<>();
    private int maxPodcastParams;

    private static Model model = null;

    public static synchronized Model getInstance()
    {
        if (model == null)
            model = new Model();


        return model;


    }

    public String getDirPath(String path) {
        File file = new File(PodcastBoard.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        String projectPath = file.getPath();


        Path p = Paths.get(projectPath);

        if (!file.isDirectory()) p = p.getParent();

        while (!p.getName(p.getNameCount()-1).toString().equals("PodcastBoard")) {
            p = p.getParent();
        }

        return p + "\\" + path;
    }

    public String initApiKeyFromFile() {
        apiKey = fileToStr(getDirPath(API_KEY_RELATIVE_PATH));
        if (apiKey == null) {
            System.out.println("api key didn't exist");
            return null;
        }
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        if (apiKey.equals("")) return;
        this.apiKey = apiKey;
    }

    public void saveData(String resolution) {
        strToFile(getDirPath(API_KEY_RELATIVE_PATH), apiKey);
        writePodcastListToFile();
        strToFile(getDirPath(SETTINGS_PATH ), resolution);
    }

    public String[] settingFromFile() {
        String str = fileToStr(getDirPath(SETTINGS_PATH));

        if (str == null) {
            System.err.println("ERR: settings file was null");
            return new String[0];
        }
        return str.split(" ");
    }

    public ArrayList<String> getPodcastTitles() {
        ArrayList<String> titles = new ArrayList<>();
        podcasts.forEach(podcast -> titles.add(podcast.getNewestPodcastTitle()));
        return titles;
    }

    public ArrayList<Image> getPodcastThumbnails() {
        ArrayList<Image> thumbnails = new ArrayList<>();
        podcasts.forEach(podcast -> thumbnails.add(imgFromWebPath(podcast.getNewestPodcastThumbnailUrl())));
        return thumbnails;
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public void addPodcast(Podcast podcast) {
        podcasts.add(podcast);
    }

    public void addPodcast(Podcast podcast, int index) {
        podcasts.add(index, podcast);
    }

    public void removePodcast(int index) {
        System.out.println("deleted: " + podcasts.get(index).getName());
        podcasts.remove(index);
    }

    public int getPodcastsSize() {
        return podcasts.size();
    }

    private void mostParams() {
        int params = 0;
        for (Podcast podcast : podcasts) {
            int tempAmount = podcast.getParamAmount();
            if (tempAmount > params) params = tempAmount;
        }
        maxPodcastParams = params;
    }

    public int getMaxPodcastParams() {
        return maxPodcastParams;
    }

    public void updatePodcastInfo(Podcast podcast) {
        try {
            String searchChannelJson = searchChannelJson(podcast.getName().replaceAll(" ", "%20"), apiKey);
            if (jsonByHitIndex("code", 0, searchChannelJson).equals("403"))
                System.err.println("ERR: The request cannot be completed because you have exceeded your apikey quota");
            String channelId         = jsonByHitIndex("channelId", 0, searchChannelJson);
            String formatedParams    = podcast.getYoutubeFormatParams().replaceAll(" ", "%20");
            String videoJson         = searchVideoJson(formatedParams, channelId, apiKey);
            String thumbnailUrl      = jsonByHitIndex("url", 2, videoJson);

            podcast.setNewestPodcastThumbnailUrl(thumbnailUrl);
            podcast.setNewestPodcastTitle(jsonByHitIndex("title", 0, videoJson));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePodcasts() {
        podcasts.forEach(podcast -> {
            updatePodcastInfo(podcast);
            mostParams();
        });
    }

    // this needs to be generalized

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

    private boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private void writePodcastListToFile() {
        String filePath = getDirPath(PODCASTS_FILE_PATH);
        File file = new File(filePath);
        if (file.exists()) file.delete();
        try  {
            file.createNewFile();
            FileOutputStream fileoutputStream = new FileOutputStream(filePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileoutputStream);
            objectOutputStream.writeObject(podcasts);

            objectOutputStream.close();
            fileoutputStream.close();

            podcasts.forEach(podcast -> System.out.println("To file: " + podcast.getName()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getPodcastListFromFile() {
        try {
            String filePath = getDirPath(PODCASTS_FILE_PATH);
            if (!fileExists(filePath)) {
                System.err.println("ERR: There was no file containing the podcast objects");
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            podcasts = (ArrayList<Podcast>) objectInputStream.readObject();
            podcasts.forEach(podcast -> System.out.println("From file: " + podcast.getName()));
            fileInputStream.close();
            objectInputStream.close();

            mostParams();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String jsonByHitIndex(String key, int index, String json) {
        java.util.List<String> list = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        check(key, jsonParser.parse(json), list);
        if (list.isEmpty()) return "";
        return list.get(index).replaceAll("\"", "");
    }

    private String getRequestJson(String uriStr) throws Exception {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI( uriStr))
                .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        return getResponse.body();
    }

    private String searchVideoJson(String searchParam, String channelId, String apiKey) throws Exception {
        if (channelId.isEmpty() || apiKey.isEmpty()) {
            System.err.println("ERR: Channelid or apikey was empty, in video search");
            return "";
        }

        String maxResults = "1";
        String order = "date";

        String str = BY_CHANNEL_SEARCH_EP + channelId + MAX_RESULTS_EP + maxResults + ORDER_EP + order;
        if (!searchParam.isEmpty()) str += SEARCH_PARAM_EP + searchParam;
        str += PART_SNIPPET_EP + API_KEY_EP + apiKey;

        return getRequestJson(str);
    }

    private String searchChannelJson(String channelName, String apiKey) throws Exception {
        if (channelName.isEmpty() || apiKey.isEmpty()) {
            System.err.println("ERR: Channel name or apikey was empty, in channel search");
            return "";
        }
        String maxResults = "1";
        return getRequestJson(CHANNEL_SEARCH_EP +
                MAX_RESULTS_EP + maxResults +
                SEARCH_PARAM_EP + channelName +
                API_KEY_EP + apiKey);
    }

    // Taken from https://www.codejava.net/java-se/file-io/how-to-read-and-write-text-file-in-java
    private String fileToStr(String fileName) {
        try {
            FileReader reader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String lineStr;
            StringBuilder fileStr = new StringBuilder();

            while ((lineStr = bufferedReader.readLine()) != null) {
                fileStr.append(lineStr);
            }
            reader.close();
            return fileStr.toString();

        } catch (IOException e) {
            System.err.println("ERR: Runtime error in fileToStr");
            return null;
        }
    }

    public void strToFile(String path, String str) {
        File file = new File(path);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file, false);
            writer.write(str);
            writer.close();

        } catch (IOException e) {
            throw  new RuntimeException();
        }
    }

    // Taken from Safwan Hijazi on stack overflow
    // https://stackoverflow.com/a/31158468
    private void check(String key, JsonElement jsonElement, List<String> list) {

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
}
