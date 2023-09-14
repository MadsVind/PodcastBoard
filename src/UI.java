import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class UI {

    //Frame settings
    int frameWidth;
    int frameHeight;

    JFrame frame  = null;

    //Cards objects
    CardLayout cl           = null;
    JPanel  cards           = null;
    JPasswordField apiInput = null;

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
            card.setBackground(Color.WHITE);
            i++;
        }

        cl.show(cards, "1");
    }

    public JPanel createPodcastFront(Podcast podcast) {
        JLabel thumbnail  = makeImgLabel(500, 300, podcast.getNewestPodcastThumbnail());
        JLabel videoTitle = new JLabel(podcast.getNewestPodcastTitle());
        videoTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel podcastFront  = new JPanel();
        podcastFront.setBackground(Color.WHITE);
        podcastFront.setLayout(new BoxLayout(podcastFront, BoxLayout.Y_AXIS));
        podcastFront.add(thumbnail);
        podcastFront.add(videoTitle);
        return podcastFront;
    }

    public JPanel createPodcastCard(JButton settingsButton, ArrayList<JPanel> podcastPanels)  {
        JPanel podcastPanel  = new JPanel(new BorderLayout());
        JPanel podcastPanelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel podcastPanelBot = new JPanel(new FlowLayout());

        podcastPanelTop.setBackground(Color.ORANGE);
        podcastPanelBot.setBackground(Color.WHITE);

        podcastPanel.add(podcastPanelTop, BorderLayout.NORTH);
        podcastPanel.add(podcastPanelBot, BorderLayout.CENTER);

        podcastPanelTop.add(settingsButton);
        podcastPanels.forEach(podcastPanelBot::add);

        return podcastPanel;
    }

    public JPanel createSettingsCard(JButton settingsButton, JPanel podcastPanel) {
        JPanel settingsPanel = new JPanel(new BorderLayout());
        JPanel settingsPanelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel settingsPanelBot = new JPanel();
        settingsPanelBot.setLayout(new  BoxLayout(settingsPanelBot, BoxLayout.Y_AXIS));

        settingsPanelTop.setBackground(Color.ORANGE);
        settingsPanelBot.setBackground(Color.WHITE);

        settingsPanel.add(settingsPanelTop, BorderLayout.NORTH);
        settingsPanel.add(settingsPanelBot, BorderLayout.WEST);

        settingsPanelTop.add(settingsButton);


        JPanel apiKeyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        apiKeyPanel.setBackground(Color.WHITE);

        apiInput = new JPasswordField(18);
        JLabel apikeyText       = new JLabel("Apikey: ");

        apiKeyPanel.add(apikeyText);
        apiKeyPanel.add(apiInput);

        settingsPanelBot.add(apiKeyPanel);
        settingsPanelBot.add(podcastPanel);

        return settingsPanel;
    }

    public String getApiKey() {
        return apiInput.getPassword().toString();
    }

    public void setApiKey(String apiKey) {
        apiInput.setText(apiKey);
    }

    public JPanel createPodcastListPanel(String[][] podcastsInfoLists) {
        JPanel addPodcastPanel = new JPanel(new BorderLayout());
        JPanel addDelPanel     = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel podcastsPanel   = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addPodcastPanel.add(addDelPanel, BorderLayout.NORTH);
        addPodcastPanel.add(podcastsPanel, BorderLayout.WEST);

        JButton addPodcastButton = new JButton("+");
        JButton delPodcastButton = new JButton("-");

        // get less assaulting colors
        addPodcastButton.setBackground(Color.GREEN);
        delPodcastButton.setBackground(Color.RED);

        addPodcastButton.setPreferredSize(new Dimension(40, 40));
        delPodcastButton.setPreferredSize(new Dimension(40, 40));

        addPodcastButton.setFont(new Font("Arial", Font.BOLD, 10 ));
        delPodcastButton.setFont(new Font("Arial", Font.BOLD, 10 ));

        addDelPanel.add(addPodcastButton);
        addDelPanel.add(delPodcastButton);;

        //Add table titles
        int paramAmount = podcastsInfoLists[0].length-1;

        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Channel");
        for (int i = 1; i <= paramAmount; i++) {
            columnNames.add("param " + i);
        }

        JTable podcastTable                = new JTable(podcastsInfoLists, columnNames.toArray());
        podcastTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPanePodcastTable = new JScrollPane(podcastTable);
        podcastsPanel.add(scrollPanePodcastTable);

        podcastTable.setBackground(Color.WHITE);
        scrollPanePodcastTable.setBackground(Color.WHITE);
        scrollPanePodcastTable.getViewport().setBackground(Color.WHITE);
        addDelPanel.setBackground(Color.WHITE);
        addPodcastPanel.setBackground(Color.WHITE);
        podcastsPanel.setBackground(Color.WHITE);

        podcastTable.setFont(new Font("Arial", Font.PLAIN, 11));

        podcastTable.getTableHeader().setBackground(Color.ORANGE);
        podcastTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        podcastTable.getTableHeader().setReorderingAllowed(false);

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
