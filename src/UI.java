import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class UI {

    //PodcastBoard
    PodcastBoard pb = PodcastBoard.getInstance();

    //Frame settings
    int frameWidth;
    int frameHeight;
    JFrame frame  = null;

    //Cards objects
    CardLayout cl           = null;
    JPanel  cards           = null;

    //Dialogs
    private CustomDialog addDialog = null;

    //Api key
    JPasswordField apiInput = new JPasswordField(18);

    //Podcast Swing
    private JPanel            podcastPanel;
    private DefaultTableModel podcastModel;
    private JTable            podcastTable;

    //Font
    final Font smallFont = new Font("Arial", Font.BOLD, 10 );
    final Font mediumFont = new Font("Arial", Font.BOLD, 16 );

    private final Hashtable<String, JButton> buttonHashtable = new Hashtable<>();

    public JButton getButtonByName(String name) {
        return  buttonHashtable.get(name);
    }

    private static UI ui = null;

    public static synchronized UI getInstance()
    {
        if (ui == null)
            ui = new UI();

        return ui;
    }

    public void packCustDialog(CustomDialog dialog) {
        dialog.pack();
    }

    public void addPodcast(ArrayList<Podcast> podcasts) {
        Image acceptImage = pb.getImage("acceptImage");
        JButton acceptButton = createImageButton("acceptPodcastButton", acceptImage, 40, 40);
        JPanel dialogPanel = createDialogPanel(podcasts, acceptButton);

        addDialog = new CustomDialog(frame, "Add podcast", dialogPanel, acceptButton);
        addDialog.setVisible(true);
    }

    public void delPodcast(ArrayList<Podcast> podcasts) {
        int selectedRow = podcastTable.getSelectedRow();
        if(selectedRow  != -1) {
            podcastModel.removeRow(selectedRow);
            podcasts.remove(selectedRow);

            int paramAmount = mostParams(podcasts);
            String[][] podcastsInfoLists = Podcast.getSeachDataAs2dArr(podcasts, paramAmount);

            try {
                pb.updatePodcastsFile();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            updatePodcastTable(podcastsInfoLists);
        }
    }


    private JPanel createDialogPanel(ArrayList<Podcast> podcasts, JButton acceptButton) {
        JPanel addDialogPanel    = createColorPanel(new BorderLayout(), Color.WHITE);
        JPanel channelNamePanel  = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addDialogPanel, BorderLayout.NORTH);
        JPanel addDelParamsPanel = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addDialogPanel, BorderLayout.WEST);

        JPanel paramsPanel = new JPanel();
        paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.Y_AXIS));
        paramsPanel.setBackground(Color.WHITE);
        addDialogPanel.add(paramsPanel);

        JPanel acceptPanel = createColorPanel(new FlowLayout(FlowLayout.CENTER), Color.WHITE, addDialogPanel, BorderLayout.SOUTH);

        createLabel("Podcast channel name:", smallFont, channelNamePanel);
        JTextField channelNameInput = createTextField("Insert channel name", smallFont, channelNamePanel);

        Image addImage = pb.getImage("addImage");
        Image delImage = pb.getImage("delImage");
        JButton addPodcastButton = createImageButton("addParamButton", addImage, 40, 40);
        JButton delPodcastButton = createImageButton("delParamButton", delImage, 40, 40);

        ArrayList<JTextField> paramTextFields = new ArrayList<>();

        addDelParamsPanel.add(addPodcastButton);
        addDelParamsPanel.add(delPodcastButton);
        acceptPanel.add(acceptButton);

        addPodcastButton.addActionListener(e -> {
            paramTextFields.add(createParamPanel(paramsPanel));
            packCustDialog(addDialog);
            paramsPanel.revalidate();
            paramsPanel.repaint();

        });

        delPodcastButton.addActionListener(e -> {
            int paramsCount = paramsPanel.getComponentCount();
            if (paramsPanel.getComponentCount() != 0) {
                paramsPanel.remove(paramsCount-1);
                paramTextFields.remove(paramsCount-1);
                packCustDialog(addDialog);
                paramsPanel.revalidate();
                paramsPanel.repaint();
            }
        });

        acceptButton.addActionListener(e -> {
            Podcast newPodcast = new Podcast(channelNameInput.getText());
            newPodcast.setParams(textFieldArrToStringArr(paramTextFields));
            podcasts.add(newPodcast);
            int paramAmount = mostParams(podcasts);

            String[][] podcastsInfoLists = Podcast.getSeachDataAs2dArr(podcasts, paramAmount);
            updatePodcastTable(podcastsInfoLists);
            try {
                pb.updatePodcastsFile();
                updatePodcastPanel(pb.getUpdatedThumbnails());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        return addDialogPanel;
    }

    public int mostParams(ArrayList<Podcast> podcasts) {
        int mostParams = 0;
        for (Podcast podcast : podcasts) {
            int tempAmount = podcast.getParamAmount();
            if (tempAmount > mostParams) mostParams = tempAmount;
        }
        return mostParams;
    }
    private ArrayList<String> textFieldArrToStringArr(ArrayList<JTextField> textFields) {
        ArrayList<String> strings = new ArrayList<>();
        textFields.forEach(textField -> strings.add(textField.getText()));
        return strings;
    }

    private JTextField createParamPanel(JPanel parent) {
        JPanel panel = createColorPanel(new FlowLayout(), Color.WHITE, parent);
        createLabel("Search param:", smallFont, panel);
        return createTextField("Insert Search param", smallFont, panel);
    }

    public JPanel createPodcastCard(Image settingsButtonImage, ArrayList<JPanel> podcastPanels)  {
        JPanel podcastCard         = new JPanel(new BorderLayout());
        JPanel settingsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        podcastPanel               = new JPanel(new FlowLayout());

        settingsButtonPanel.setBackground(Color.ORANGE);
        podcastPanel.setBackground(Color.WHITE);

        podcastCard.add(settingsButtonPanel, BorderLayout.NORTH);
        podcastCard.add(podcastPanel, BorderLayout.CENTER);

        JButton podcastsButton = createImageButton("podcastsButton", settingsButtonImage, 40, 40);

        settingsButtonPanel.add(podcastsButton);
        podcastPanels.forEach(podcastPanel::add);

        return podcastCard;
    }

    public void updatePodcastPanel(ArrayList<JPanel> podcastPanels) {
        podcastPanel.removeAll();
        podcastPanels.forEach(podcastPanel::add);
    }

    public JPanel createSettingsCard(Image settingsButtonImage, JPanel podcastPanel) {
        JPanel settingsPanel    = createColorPanel(new BorderLayout(), Color.WHITE);
        JPanel settingsPanelTop = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.ORANGE);
        JPanel settingsPanelBot = new JPanel();
        settingsPanelBot.setLayout(new BoxLayout(settingsPanelBot, BoxLayout.Y_AXIS));
        settingsPanelBot.setBackground(Color.WHITE);

        settingsPanel.add(settingsPanelTop, BorderLayout.NORTH);
        settingsPanel.add(settingsPanelBot, BorderLayout.WEST);

        JButton settingsButton = createImageButton("settingsButton", settingsButtonImage, 40, 40);
        settingsPanelTop.add(settingsButton);

        JPanel apiKeyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        apiKeyPanel.setBackground(Color.WHITE);

        JLabel apikeyText = new JLabel("Apikey: ");

        apiKeyPanel.add(apikeyText);
        apiKeyPanel.add(apiInput);

        settingsPanelBot.add(apiKeyPanel);
        settingsPanelBot.add(podcastPanel);

        return settingsPanel;
    }

    public JPanel createPodcastListPanel(String[][] podcastsInfoLists) {
        JPanel addPodcastPanel = createColorPanel(new BorderLayout(), Color.WHITE);
        JPanel addDelPanel     = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addPodcastPanel, BorderLayout.NORTH);
        JPanel podcastsPanel   = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addPodcastPanel, BorderLayout.WEST);

        JButton addPodcastButton  = createImageButton("addPodcastButton", pb.getImage("addImage"), 40, 40);
        JButton delPodcastButton  = createImageButton("delPodcastButton", pb.getImage("delImage"), 40, 40);
        //JButton editPodcastButton = createImageButton("editPodcastButton", pb.getImage("editImage"), 40, 40);

        getButtonByName("addPodcastButton").addActionListener(e -> addPodcast(pb.getPodcasts()));
        getButtonByName("delPodcastButton").addActionListener(e -> delPodcast(pb.getPodcasts()));

        addDelPanel.add(addPodcastButton);
        addDelPanel.add(delPodcastButton);
        //addDelPanel.add(editPodcastButton);

        initPodcastTable(podcastsInfoLists);
        podcastTable.setDefaultEditor(Object.class, null);

        podcastTableStyling(podcastsPanel);

        return addPodcastPanel;
    }

    private void initPodcastTable(String[][] podcastsInfoLists) {
        int paramAmount = 0;
        if (podcastsInfoLists.length != 0) paramAmount = podcastsInfoLists[0].length - 1;

        getTableHeader(podcastsInfoLists, paramAmount);
        podcastTable = new JTable(podcastModel);
    }

    private void getTableHeader(String[][] podcastsInfoLists, int paramAmount) {
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Channel");

        for (int i = 1; i <= paramAmount; i++) {
            columnNames.add("param " + i);
        }
        podcastModel = new DefaultTableModel(podcastsInfoLists, columnNames.toArray());
    }

    public void updatePodcastTable(String[][] podcastsInfoLists) {
        int paramAmount;
        if (podcastsInfoLists.length == 0) paramAmount = 0;
        else paramAmount = podcastsInfoLists[0].length-1;

        getTableHeader(podcastsInfoLists, paramAmount);
        podcastTable.setModel(podcastModel);

        podcastTable.revalidate();
        podcastTable.repaint();
    }

    private void podcastTableStyling(JPanel podcastsPanel) {
        JScrollPane scrollPanePodcastTable = new JScrollPane(podcastTable);
        podcastsPanel.add(scrollPanePodcastTable);

        podcastTable.setBackground(Color.WHITE);
        scrollPanePodcastTable.setBackground(Color.WHITE);
        scrollPanePodcastTable.getViewport().setBackground(Color.WHITE);

        podcastTable.setFont(new Font("Arial", Font.PLAIN, 11));

        podcastTable.getTableHeader().setBackground(Color.ORANGE);
        podcastTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        podcastTable.getTableHeader().setReorderingAllowed(false);
    }

    public void createFrameWithCardLayout(String frameTitle, int width, int height, Image frameIcon, JPanel ... cardList) {
        frameWidth  = width;
        frameHeight = height;

        frame = new JFrame(frameTitle);
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

    public JPanel createPodcastThumbnail(Podcast podcast) {
        if (podcast.getNewestPodcastThumbnailUrl().isEmpty()) {
            System.err.println("ERR: Url was empty in createPodcastThumbnail");
            return null;
        }
        JLabel thumbnail  = makeImgLabel(500, 300, pb.urlToImage(podcast.getNewestPodcastThumbnailUrl()));
        JLabel videoTitle = new JLabel(podcast.getNewestPodcastTitle());
        videoTitle.setFont(mediumFont);

        JPanel podcastFront = new JPanel();
        podcastFront.setBackground(Color.WHITE);
        podcastFront.setLayout(new BoxLayout(podcastFront, BoxLayout.Y_AXIS));
        podcastFront.add(thumbnail);
        podcastFront.add(videoTitle);
        return podcastFront;
    }

    public ArrayList<JPanel> PodcastToThumbnails(ArrayList<Podcast> podcasts) {
        ArrayList<JPanel> thumbnails = new ArrayList<>();
        podcasts.forEach(podcast -> {
            JPanel thumbnail = createPodcastThumbnail(podcast);
            if (thumbnail == null) return;
            thumbnails.add(createPodcastThumbnail(podcast));
        });
        return thumbnails;
    }

    public String getApiKey() {
        return new String(apiInput.getPassword());
    }

    public void setApiKey(String apiKey) {
        this.apiInput.setText(apiKey);
    }

    public JButton createImageButton(String name, Image img, int width, int hight) {
        Image image = img.getScaledInstance(width, hight, Image.SCALE_DEFAULT);
        JButton button = new JButton(new ImageIcon(image));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        buttonHashtable.put(name, button);
        return button;
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

    private JPanel createColorPanel(Object layout, Color color) {
        JPanel panel = new JPanel((LayoutManager) layout);
        panel.setBackground(color);
        return panel;
    }

    private JPanel createColorPanel(Object layout, Color color, JPanel parent) {
        JPanel panel = new JPanel((LayoutManager) layout);
        panel.setBackground(color);
        parent.add(panel);
        return panel;
    }

    private JPanel createColorPanel(Object layout, Color color, JPanel parent, String position) {
        JPanel panel = new JPanel((LayoutManager) layout);
        panel.setBackground(color);
        parent.add(panel, position);
        return panel;
    }

    private JTextField createTextField(String text, Font font, JPanel parent) {
        JTextField textField = new JTextField(text);
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textField.selectAll();
            }
        });
        textField.setFont(font);
        parent.add(textField);
        return textField;
    }

    private JLabel createLabel(String text, Font font, JPanel parent) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        parent.add(label);
        return label;
    }
}
