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
        params.add(param);
    }

    public void delParam(int paramIndex) {
        params.remove(paramIndex);
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public int getParamAmount() {
        return params.size();
    }
}
