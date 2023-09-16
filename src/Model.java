import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class Model {
    //EP (Endpoints) for different used parts of the YouTube v3 api
    final static String YOUTUBE_EP           = "https://youtube.googleapis.com/youtube/v3/";
    final static String SEARCH_EP            = YOUTUBE_EP + "search?";
    final static String CHANNEL_SEARCH_EP    = SEARCH_EP + "type=channel";
    final static String BY_CHANNEL_SEARCH_EP = SEARCH_EP + "channelId=";
    final static String PART_SNIPPET_EP      = "&part=snippet";
    final static String MAX_RESULTS_EP       = "&maxResults=";
    final static String ORDER_EP             = "&order=";
    final static String SEARCH_PARAM_EP      = "&q=";
    final static String API_KEY_EP           = "&key=";

    static HttpClient httpClient = HttpClient.newHttpClient();

    private static Model model = null;

    public static synchronized Model getInstance()
    {
        if (model == null)
            model = new Model();

        return model;
    }

    public String getDirPath(String path) {
        return new File("").getAbsolutePath() + "\\src" + path;
    }

    public String getApiKey(String apiKeyPath) {
        String apiKey = fileToStr(apiKeyPath);
        if (apiKey == null) System.out.println("api key didn't exist");
        return  apiKey;
    }

    public void updatePodcastInfo(Podcast podcast, String apiKey) {
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

    public void updatePodcasts(ArrayList<Podcast> podcasts, String apiKey) {
        podcasts.forEach(podcast -> {
            try                 {updatePodcastInfo(podcast, apiKey);}
            catch (Exception e) {throw new RuntimeException(e);}
        });
    }

    // this needs to be generalized
    public void setSettingsButtonBehaivior(JButton settingsButton, CardLayout cl, JPanel cards) {
        ActionListener switchButtonListener = e -> cl.next(cards);
        settingsButton.addActionListener(switchButtonListener);
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

    public boolean fileExists(String fileRelativePath) {
        String filePath = getDirPath(fileRelativePath);
        File file = new File(filePath);
        return file.exists();
    }

    public void writePodcastListToFile(ArrayList<Podcast> obj, String fileRelativePath) throws Exception {
        String filePath = getDirPath(fileRelativePath);
        File file = new File(filePath);
        if (file.exists()) file.delete();
        try  {
            FileOutputStream fileoutputStream = new FileOutputStream(filePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileoutputStream);
            objectOutputStream.writeObject(obj);

            objectOutputStream.close();
            fileoutputStream.close();

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ArrayList<Podcast> readPodcastListFromFile(String fileRelativePath) throws Exception {
        try {
            String filePath = getDirPath(fileRelativePath);
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            ArrayList<Podcast> podcastList =  (ArrayList<Podcast>) objectInputStream.readObject();
            fileInputStream.close();
            objectInputStream.close();

            return podcastList;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String jsonByHitIndex(String key, int index, String json) {
        java.util.List<String> list = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        check(key, jsonParser.parse(json), list);
        if (list.isEmpty()) return "";
        return list.get(index).replaceAll("\"", "");
    }

    public String getRequestJson(String uriStr) throws Exception {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI( uriStr))
                .build();
        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        return getResponse.body();
    }

    public String searchVideoJson(String searchParam, String channelId, String apiKey) throws Exception {
        if (searchParam.isEmpty() || channelId.isEmpty() || apiKey.isEmpty()) {
            System.err.println("ERR: searchParams, Channelid or apikeywas empty, in video search");
            return "";
        }
        String maxResults = "1";
        String order = "date";
        return getRequestJson(BY_CHANNEL_SEARCH_EP + channelId +
                MAX_RESULTS_EP + maxResults +
                ORDER_EP + order +
                SEARCH_PARAM_EP + searchParam +
                PART_SNIPPET_EP +
                API_KEY_EP + apiKey);
    }

    public String searchChannelJson(String channelName, String apiKey) throws Exception {
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
    public static String fileToStr(String fileName) {
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
            return null;
        }
    }

    public void strToFile(String path, String str) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(str);
            writer.close();

        } catch (IOException e) {
            throw  new RuntimeException();
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
}
