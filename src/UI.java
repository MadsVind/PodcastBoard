import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UI {

    //Frame settings
    int frameWidth;
    int frameHeight;

    JFrame frame  = null;

    //Cards objects
    CardLayout cl = null;
    JPanel  cards = null;

    private static UI ui = null;

    public static synchronized UI getInstance()
    {
        if (ui == null)
            ui = new UI();

        return ui;
    }

    public void createFrameWithCardLayout(int width, int height, Image frameIcon, JPanel ... cardList) {
        frameWidth  = width;
        frameHeight = height;

        frame = new JFrame("Podcast Board");
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new BorderLayout());

        initCardLayout(cardList);
        frame.add(cards);

        frame.setIconImage(frameIcon);
        frame.setVisible(true);
    }

    public void initCardLayout(JPanel ... panels) {
        cards = new JPanel();
        cl = new CardLayout();

        cards.setLayout(cl);

        int i = 1;
        for (JPanel card : panels) {
            cards.add(card, Integer.toString(i));
            i++;
        }

        cl.show(cards, "1");
    }


    public JPanel createPodcastCard(JButton settingsButton, ArrayList<JPanel> podcastPanels)  {
        JPanel podcastPanel  = new JPanel(new BorderLayout());
        JPanel podcastPanelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel podcastPanelBot = new JPanel(new FlowLayout());

        podcastPanel.add(podcastPanelTop, BorderLayout.NORTH);
        podcastPanel.add(podcastPanelBot, BorderLayout.CENTER);

        podcastPanelTop.add(settingsButton);
        podcastPanels.forEach(podcastPanelBot::add);

        return podcastPanel;
    }

    public JPanel createSettingsCard(JButton settingsButton, JPanel podcastPanel) {
        JPanel settingsPanel = new JPanel(new BorderLayout());
        JPanel settingsPanelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel settingsPanelBot = new JPanel(new FlowLayout());

        settingsPanel.add(settingsPanelTop, BorderLayout.NORTH);
        settingsPanel.add(settingsPanelBot, BorderLayout.WEST);

        settingsPanelTop.add(settingsButton);

        JPanel addPodcastPanel = podcastPanel;

        settingsPanelBot.add(addPodcastPanel);

        return settingsPanel;
    }

    public JPanel createPodcastListPanel(ArrayList<Podcast> podcasts, int paramAmount) {
        JPanel addPodcastPanel = new JPanel();
        JPanel addDelPanel     = new JPanel();
        JPanel podcastsPanel   = new JPanel();

        addDelPanel.setBackground(Color.GRAY);

        addPodcastPanel.add(addDelPanel);
        addPodcastPanel.add(podcastsPanel);

        JButton addPodcast = new JButton("+");
        JButton delPodcast = new JButton("-");

        addDelPanel.add(addPodcast);
        addDelPanel.add(delPodcast);;

        //Add table titles
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Channel");
        for (int i = 1; i <= paramAmount; i++) {
            columnNames.add("param " + i);
        }


        String[][] podcastsInfoLists = new String[podcasts.size()][paramAmount+1];

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

        JTable table = new JTable(podcastsInfoLists, columnNames.toArray());
        podcastsPanel.add(table);

        return addPodcastPanel;
    }

    public JButton createImageButton(Image img, int width, int heigth) {
        Image image = img.getScaledInstance(width, heigth, Image.SCALE_DEFAULT);
        JButton toPodcastsButton = new JButton(new ImageIcon(image));
        toPodcastsButton.setBorder(BorderFactory.createEmptyBorder());
        toPodcastsButton.setContentAreaFilled(false);

        return toPodcastsButton;
    }

    public void removeLabel(JLabel label) {
        Container parent = label.getParent();
        parent.remove(label);
        parent.validate();
        parent.repaint();
    }

    public JLabel makeImgLabel(int width, int height, Image img) {
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_DEFAULT)));
        return label;
    }

}
