import java.awt.*;
import java.util.ArrayList;

public class Podcast {
    //Search data
    private String name;
    private ArrayList<String> params = new ArrayList<>();

    //Result data
    private Image  newestPodcastThumbnail  = null;
    private String newestPodcastTitle      = null;

    public Podcast(String name) {
        this.name = name;
    }

    public static String[][] getSeachDataAs2dArr(ArrayList<Podcast> podcasts, int paramAmount) {
        String[][] podcastsSearchData2dArr = new String[podcasts.size()][paramAmount+1];

        for (int i = 0; i < podcasts.size(); i++) {
            for (int j = 0; j <= paramAmount; j++) {
                Podcast podcast = podcasts.get(i);

                if (j == 0) {
                    podcastsSearchData2dArr[i][0] = podcast.getName();
                }
                else if (j <= podcast.getParamAmount()) {
                    podcastsSearchData2dArr[i][j] = podcast.getParams().get(j-1);
                }
                else podcastsSearchData2dArr[i][j] = "";
            }
        }
        return podcastsSearchData2dArr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addParam(String param) {
        this.params.add(param);
    }

    public void delParam(int paramIndex) {
        this.params.remove(paramIndex);
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public String getYoutubeFormatParams() {
        String formatString = "";
        for (int i = 0; i < params.size(); i++) {
            if (i != 0) formatString += "|";
            formatString += params.get(i);

        }
        return formatString;
    }

    public int getParamAmount() {
        return params.size();
    }

    public String getNewestPodcastTitle() {
        return newestPodcastTitle;
    }

    public void setNewestPodcastTitle(String newestPodcastTitle) {
        this.newestPodcastTitle = newestPodcastTitle;
    }

    public Image getNewestPodcastThumbnail() {
        return newestPodcastThumbnail;
    }

    public void setNewestPodcastThumbnail(Image newestPodcastThumbnail) {
        this.newestPodcastThumbnail = newestPodcastThumbnail;
    }
}
