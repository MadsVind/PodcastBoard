import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.io.Serializable;

public class Podcast implements Serializable {

    @Serial
    private static final long serialVersionUID = -8256478671811426015L;

    //Search data
    private String name;
    private ArrayList<String> params = new ArrayList<>();

    //Result data
    private String newestPodcastThumbnailUrl  = null;
    private String newestPodcastTitle         = null;

    public Podcast(String name) {
        this.name = name;
    }

    public static String[][] getSeachDataAs2dArr(ArrayList<Podcast> podcasts, int paramAmount) {
        if (podcasts.isEmpty()) return new String[][]{};
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

    public void setParams(ArrayList<String> params) {
        this.params = params;
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
            if (i != 0) formatString += " ";
            formatString += params.get(i).replaceAll(" ", "%20");

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

    public String getNewestPodcastThumbnailUrl() {
        return newestPodcastThumbnailUrl;
    }

    public void setNewestPodcastThumbnailUrl(String newestPodcastThumbnailUrl) {
        this.newestPodcastThumbnailUrl = newestPodcastThumbnailUrl;
    }
}
