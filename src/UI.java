import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class UI extends JFrame {
    //Fonts
    final Font smallFont = new Font("Arial", Font.BOLD, 10 );
    final Font mediumFont = new Font("Arial", Font.BOLD, 16 );

    //Frame settings
    private int frameWidth;
    private int frameHeight;
    private final JFrame frame = new JFrame("PodcastBoard");

    //Cards objects
    private CardLayout cl = null;
    private JPanel  cards = null;

    //PodcastCard
    private final JPanel podcastCard         = new JPanel(new BorderLayout());
    private final JPanel settingsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JPanel podcastPanel        = new JPanel(new FlowLayout());

    // Podcast Thumbnail
    private final ArrayList<JPanel> podcastThumbnailPanels = new ArrayList<>();

    private final int thumbnailWidth  = 500;
    private final int thumbnailHeigth = 300;

    //SettingsCard
    private final JPanel settingsCard     = createColorPanel(new BorderLayout(), Color.WHITE);
    private final JPanel settingsPanelTop = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.ORANGE);
    private final JPanel settingsPanelBot = new JPanel();

    private final JPanel apiKeyPanel = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, settingsPanelBot);
    private final JLabel apikeyText  = new JLabel("Apikey: ");

    private JButton podcastsButton;

    //Podcast table panel
    private final JPanel addPodcastPanel    = createColorPanel(new BorderLayout(), Color.WHITE);
    private final JPanel addDelPanel        = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addPodcastPanel, BorderLayout.NORTH);
    private final JPanel tablePodcastsPanel = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addPodcastPanel, BorderLayout.WEST);

    private JButton addPodcastButton;
    private JButton delPodcastButton;

    private JButton settingsButton;

    //Podcast add dialog
    private CustomDialog addDialog = null;

    private final JPanel addDialogPanel    = createColorPanel(new BorderLayout(), Color.WHITE);
    private final JPanel channelNamePanel  = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addDialogPanel, BorderLayout.NORTH);
    private final JPanel addDelParamsPanel = createColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addDialogPanel, BorderLayout.WEST);
    private final JPanel paramsPanel       = new JPanel();
    private final JPanel acceptPanel       = createColorPanel(new FlowLayout(FlowLayout.CENTER), Color.WHITE, addDialogPanel, BorderLayout.SOUTH);

    private final JLabel channelNameLabel = createLabel("Podcast channel name:", smallFont, channelNamePanel);
    private final JTextField channelNameInput  = createTextField("Insert channel name", smallFont, channelNamePanel);

    private JButton addParamButton;
    private JButton delParamButton;
    private JButton acceptButton;

    private final ArrayList<JTextField> paramTextFields = new ArrayList<>();


    //Api key
    JPasswordField apiInput = new JPasswordField(18);

    //Podcast table
    private DefaultTableModel podcastModel;
    private JTable            podcastTable;

    //Images //this could be done with an action listner

    private final Image settingImage;
    private final Image addImage;
    private final Image delImage;
    private final Image editImage;
    private final Image acceptImage;

    private final Image settingImageDark;
    private final Image addImageDark;
    private final Image delImageDark;
    private final Image editImageDark;
    private final Image acceptImageDark;

    private String[][] podcastsTableData = null;

    public UI(int frameHeight, int frameWidth,
              Image frameIcon, Image settingImage, Image addImage, Image delImage, Image editImage, Image acceptImage,
              Image settingImageDark, Image addImageDark, Image delImageDark, Image editImageDark, Image acceptImageDark) {

        this.frameHeight = frameHeight;
        this.frameWidth  = frameWidth;

        this.settingImage = settingImage;
        this.addImage     = addImage;

        this.delImage     = delImage;
        this.editImage    = editImage;
        this.acceptImage  = acceptImage;

        this.settingImageDark = settingImageDark;
        this.addImageDark     = addImageDark;

        this.delImageDark     = delImageDark;
        this.editImageDark    = editImageDark;
        this.acceptImageDark  = acceptImageDark;

        this.setSize(frameWidth, frameHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        initAddDialog();

        initPodcastCard();
        initSettingsCard();

        initPodcastListPanel();

        initCardLayout(podcastCard, settingsCard );

        this.add(cards);

        this.setIconImage(frameIcon);
    }

//######################################################################################################################
//CARD CHANGER
//######################################################################################################################

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

    public void changeCard() {
        cl.next(cards);
    }
//######################################################################################################################
//PODCAST CARD
//######################################################################################################################

    public void initPodcastCard()  {
        settingsButtonPanel.setBackground(Color.ORANGE);
        podcastPanel.setBackground(Color.WHITE);

        podcastCard.add(settingsButtonPanel, BorderLayout.NORTH);
        podcastCard.add(podcastPanel, BorderLayout.CENTER);

        //add functionality
        settingsButton = new ImageButton(settingImage, settingImageDark,40, 40);
        settingsButtonPanel.add(settingsButton);
    }

    public void updateThumbnails(ArrayList<Image> podcastThumbnails, ArrayList<String> podcastTitles) {
        int podcastAmount = podcastThumbnails.size();

        if (podcastAmount != podcastTitles.size()) {
            System.err.println("ERR: not same amount of Thumbnails and titles given");
            return;
        }

        podcastThumbnailPanels.clear();
        for (int i = 0; i < podcastAmount; i++) {
            JPanel thumbnail = createThumbnailPanel(podcastThumbnails.get(i), podcastTitles.get(i));
            if (thumbnail == null) {
                System.err.println("ERR: thumbnail wan null");
                continue;
            }
            podcastThumbnailPanels.add(thumbnail);
        }
        podcastPanel.removeAll();
        podcastThumbnailPanels.forEach(podcastPanel::add);
    }

    private JPanel createThumbnailPanel(Image podcastThumbnail, String podcastTitle) {
        if (podcastThumbnail == null || podcastTitle == null) {
            System.err.println("ERR: no Thumbnail or title given");
            return null;
        }
        JPanel podcastFront = new JPanel();
        podcastFront.setBackground(Color.WHITE);
        podcastFront.setLayout(new BoxLayout(podcastFront, BoxLayout.Y_AXIS));

        JLabel thumbnail  = new ImageLabel(podcastThumbnail, thumbnailWidth, thumbnailHeigth);
        podcastFront.add(thumbnail);

        createLabel(podcastTitle, mediumFont, podcastFront);
        return podcastFront;
    }


//######################################################################################################################
//SETTINGS
//######################################################################################################################

    // maybe make class
    public void initSettingsCard() {
        settingsPanelBot.setLayout(new BoxLayout(settingsPanelBot, BoxLayout.Y_AXIS));
        settingsPanelBot.setBackground(Color.WHITE);

        settingsCard.add(settingsPanelTop, BorderLayout.NORTH);
        settingsCard.add(settingsPanelBot, BorderLayout.WEST);

        //add functionality
        podcastsButton = new ImageButton(settingImage, settingImageDark,40, 40);
        settingsPanelTop.add(podcastsButton);

        apiKeyPanel.add(apikeyText);
        apiKeyPanel.add(apiInput);

        settingsPanelBot.add(apiKeyPanel);
        settingsPanelBot.add(addPodcastPanel);
    }

    public void addPodcast() {
        clearDialogParams();
        addDialog = new CustomDialog(frame, "Add podcast", addDialogPanel, acceptButton);
        addDialog.setVisible(true);
    }

    public int delPodcast() {
        int selectedRow = podcastTable.getSelectedRow();
        if(selectedRow != -1) {
            podcastModel.removeRow(selectedRow);
            return selectedRow;
        }
        return -1;
    }

//######################################################################################################################
//PODCAST TABLE IN SETTINGS
//######################################################################################################################


    private void initPodcastListPanel() {
        addPodcastButton  = new ImageButton(addImageDark, addImage, 40, 40);
        delPodcastButton  = new ImageButton(delImageDark, delImage, 40, 40);
        //JButton editPodcastButton = createImageButton("editPodcastButton", pb.getImage("editImage"), 40, 40);

        addDelPanel.add(addPodcastButton);
        addDelPanel.add(delPodcastButton);
        //addDelPanel.add(editPodcastButton);

        podcastTable = new JTable();
        podcastTable.setDefaultEditor(Object.class, null);

        podcastTableStyling(tablePodcastsPanel);
    }

    public void updatePodcastTable(String[][] podcastsTableData) {
        this.podcastsTableData = podcastsTableData;
        int paramAmount = 0;
        if (podcastsTableData.length != 0) paramAmount = podcastsTableData[0].length - 1;

        getTableHeader(podcastsTableData, paramAmount);
        podcastTable.setModel(podcastModel);

        podcastTable.revalidate();
        podcastTable.repaint();
    }

    private void getTableHeader(String[][] podcastsInfoLists, int paramAmount) {
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Channel");

        for (int i = 1; i <= paramAmount; i++) {
            columnNames.add("param " + i);
        }
        podcastModel = new DefaultTableModel(podcastsInfoLists, columnNames.toArray());
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

//######################################################################################################################
//DIALOG FOR ADDING PODCASTS IM SETTINGS
//######################################################################################################################

    private void initAddDialog() {
        paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.Y_AXIS));
        paramsPanel.setBackground(Color.WHITE);
        addDialogPanel.add(paramsPanel);

        this.addParamButton = new ImageButton(addImageDark, addImage, 40, 40);
        this.delParamButton = new ImageButton(delImageDark, delImage, 40, 40);
        this.acceptButton   = new ImageButton(acceptImageDark, acceptImage, 40, 40);

        addDelParamsPanel.add(addParamButton);
        addDelParamsPanel.add(delParamButton);
        acceptPanel.add(acceptButton);
    }

    private void clearDialogParams() {
        paramsPanel.removeAll();
        if (paramTextFields == null) return;
        paramTextFields.clear();
        channelNameInput.setText("Insert channel name");
    }

    public void addParam() {
        paramTextFields.add(createParamPanel(paramsPanel));
        packCustDialog(addDialog);
        paramsPanel.revalidate();
        paramsPanel.repaint();
    }

    private JTextField createParamPanel(JPanel parent) {
        JPanel panel = createColorPanel(new FlowLayout(), Color.WHITE, parent);
        createLabel("Search param:", smallFont, panel);
        return createTextField("Insert Search param", smallFont, panel);
    }

    public void delParam() {
        int paramsCount = paramsPanel.getComponentCount();
        if (paramsPanel.getComponentCount() != 0) {
            paramsPanel.remove(paramsCount-1);
            paramTextFields.remove(paramsCount-1);
            packCustDialog(addDialog);
            paramsPanel.revalidate();
            paramsPanel.repaint();
        }
    }

    public Podcast acceptPodcast() {
        Podcast newPodcast = new Podcast(channelNameInput.getText());
        newPodcast.setParams(textFieldArrToStringArr(paramTextFields));
        return  newPodcast;
    }

//######################################################################################################################
//ACTION LISTENER ASSIGNMENT
//######################################################################################################################

    public void dialogButtonListeners(ActionListener listenForAddParamButton,
                                      ActionListener listenForDelParamButton,
                                      ActionListener listenForAcceptButton) {
        addParamButton.addActionListener(listenForAddParamButton);
        delParamButton.addActionListener(listenForDelParamButton);
        acceptButton.addActionListener(listenForAcceptButton);
    }

    public void cardChangeButtonListener(ActionListener listenForCardChange) {
        settingsButton.addActionListener(listenForCardChange);
        podcastsButton.addActionListener(listenForCardChange);
    }

    public void podcastChangeButtonListeners(ActionListener listenForAddPodcastButton, ActionListener listenForDelPodcastButton) {
        addPodcastButton.addActionListener(listenForAddPodcastButton);
        delPodcastButton.addActionListener(listenForDelPodcastButton);
    }

//######################################################################################################################
//UTILITY
//######################################################################################################################

    public void packCustDialog(CustomDialog dialog) {
        dialog.pack();
    }

    private ArrayList<String> textFieldArrToStringArr(ArrayList<JTextField> textFields) {
        ArrayList<String> strings = new ArrayList<>();
        textFields.forEach(textField -> strings.add(textField.getText()));
        return strings;
    }

    public String getApiKey() {
        return new String(apiInput.getPassword());
    }

    public void setApiKey(String apiKey) {
        this.apiInput.setText(apiKey);
    }


    static class ImageButton extends JButton {
        Icon releasedIcon;
        Icon pressedIcon;
        ImageButton button;

        public ImageButton(Image pressedImage, Image releasedImage, int width, int heigth) {
            this.button = this;

            this.releasedIcon = new ImageIcon(releasedImage.getScaledInstance(width, heigth, Image.SCALE_DEFAULT));
            this.pressedIcon  = new ImageIcon(pressedImage.getScaledInstance(width, heigth, Image.SCALE_DEFAULT));

            this.setIcon(releasedIcon);

            this.setBorder(BorderFactory.createEmptyBorder());
            this.setContentAreaFilled(false);
            this.addMouseListener(new MyAdapter());
        }

        public ImageButton(Image image, int width, int heigth) {
            this.button = this;

            this.releasedIcon = new ImageIcon(image.getScaledInstance(width, heigth, Image.SCALE_DEFAULT));

            this.setIcon(releasedIcon);

            this.setBorder(BorderFactory.createEmptyBorder());
            this.setContentAreaFilled(false);
        }

        private class MyAdapter extends MouseAdapter {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                button.setIcon(pressedIcon);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                button.setIcon(releasedIcon);
            }
        }
    }

    static class ImageLabel extends JLabel {
        Icon icon;

        public ImageLabel(Image image, int width, int heigth) {
            this.icon = new ImageIcon(image.getScaledInstance(width, heigth, Image.SCALE_DEFAULT));
            this.setIcon(icon);
        }
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
