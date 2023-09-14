import java.util.ArrayList;

public class Podcast {
    private String name;
    private ArrayList<String> params = new ArrayList<>();

    public Podcast(String name) {
        this.name = name;
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
}
