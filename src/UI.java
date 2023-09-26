import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class UI extends JFrame {
    //Fonts
    final Font smallFont = new Font("Arial", Font.BOLD, 10 );
    final Font mediumFont = new Font("Arial", Font.BOLD, 16 );

    //Frame size
    private int podcastFrameWidth  = 0;
    private int podcastFrameHeight = 0;

    private final int settingsFrameWidth = 500;
    private final int settingsFrameHeight = 700;

    //Cards objects
    private CardLayout cl   = null;
    private JPanel  cards   = null;
    private int currentCard = 1;

    //PodcastCard
    private final JPanel podcastCard         = new JPanel(new BorderLayout());
    private final JPanel settingsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JPanel podcastPanel        = new JPanel(new FlowLayout());

    // Podcast Thumbnail
    private final ArrayList<JPanel> podcastThumbnailPanels = new ArrayList<>();

    private final int thumbnailWidth  = 500;
    private final int thumbnailHeigth = 300;

    //SettingsCard
    private final JPanel settingsCard     = new ColorPanel(new BorderLayout(), Color.WHITE);
    private final JPanel settingsPanelTop = new ColorPanel(new FlowLayout(FlowLayout.LEFT), Color.ORANGE);
    private final JPanel settingsPanelBot = new JPanel();

    private final JPanel apiKeyPanel      = new ColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, settingsPanelBot);
    private final JLabel apikeyText       = new Label("Apikey: ", smallFont, apiKeyPanel);
    private final JPasswordField apiInput = new JPasswordField(18);

    private final JPanel updateSecPanel     = new ColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, settingsPanelBot);
    private final JTextField updateSecInput = new TextField("30", smallFont, updateSecPanel);
    private final JLabel updateSecText      = new Label("Seconds Between Updating Podcast", smallFont, updateSecPanel);

    private final JPanel    windowScalablePanel    = new ColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, settingsPanelBot);
    private final JCheckBox windowScalableCheckbox = new CheckBox(windowScalablePanel, Color.WHITE);
    private final JLabel    windowScalableLabel    = new Label("Window Scalable", smallFont, windowScalablePanel);

    private final JPanel windowScalePanel      = new ColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, settingsPanelBot);
    private final JTextField windowWidthInput  = new TextField("1200",         smallFont, windowScalePanel);
    private final JLabel windowScaleX          = new Label(" X ",               smallFont, windowScalePanel);
    private final JTextField windowHeightInput = new TextField("800",        smallFont, windowScalePanel);
    private final JLabel windowScaleText       = new Label("Window resolution", smallFont, windowScalePanel);

    private ImageButton podcastsButton;

    //Podcast table panel
    private final JPanel addPodcastPanel    = new ColorPanel(new BorderLayout(), Color.WHITE, settingsPanelBot);
    private final JPanel addDelPanel        = new ColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addPodcastPanel, BorderLayout.NORTH);
    private final JPanel tablePodcastsPanel = new ColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addPodcastPanel, BorderLayout.WEST);

    private ImageButton addPodcastButton;
    private ImageButton delPodcastButton;
    private ImageButton editPodcastButton;

    private ImageButton settingsButton;

    //Podcast add dialog
    private CustomDialog addDialog = null;

    private final JPanel addDialogPanel    = new ColorPanel(new BorderLayout(), Color.WHITE);
    private final JPanel channelNamePanel  = new ColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addDialogPanel, BorderLayout.NORTH);
    private final JPanel addDelParamsPanel = new ColorPanel(new FlowLayout(FlowLayout.LEFT), Color.WHITE, addDialogPanel, BorderLayout.WEST);
    private final JPanel paramsPanel       = new JPanel();
    private final JPanel acceptPanel       = new ColorPanel(new FlowLayout(FlowLayout.CENTER), Color.WHITE, addDialogPanel, BorderLayout.SOUTH);

    private final JLabel channelNameLabel      = new Label("Podcast channel name:", smallFont, channelNamePanel);
    private final JTextField channelNameInput  = new TextField("Insert channel name", smallFont, channelNamePanel);

    private ImageButton addParamButton;
    private ImageButton delParamButton;

    // accept buttons
    private ImageButton acceptButton;


    private final ArrayList<JTextField> paramTextFields = new ArrayList<>();


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

        this.podcastFrameWidth  = frameWidth;
        this.podcastFrameHeight = frameHeight;

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

        this.setTitle("PodcastBoard");
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

        cl.show(cards, Integer.toString(currentCard));
    }

    public void changeCard() {

        int width = settingsFrameWidth;
        int height = settingsFrameHeight;

        boolean checkedBox = windowScalableCheckbox.isSelected();

        if (currentCard == 2) {
            currentCard = 1;
            if (!checkedBox) {
                width = Integer.parseInt(windowWidthInput.getText());
                height = Integer.parseInt(windowHeightInput.getText());
            } else {
                width = podcastFrameWidth;
                height = podcastFrameHeight;
            }
        } else {
            currentCard = 2;
            podcastFrameWidth = getWidth();
            podcastFrameHeight = getHeight();
        }

        setResizable(currentCard == 1 && checkedBox);

        this.setSize(width, height);

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
                System.err.println("ERR: thumbnail was null");
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

        JLabel thumbnail  = new Label(podcastThumbnail, thumbnailWidth, thumbnailHeigth);
        podcastFront.add(thumbnail);

        new Label(podcastTitle, mediumFont, podcastFront);
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

        AbstractDocument updateSecInputDoc = (AbstractDocument) updateSecInput.getDocument();
        updateSecInputDoc.setDocumentFilter(new NumberFilter());

        AbstractDocument windowWidthInputDoc = (AbstractDocument) windowWidthInput.getDocument();
        AbstractDocument windowHeightInputDoc = (AbstractDocument) windowHeightInput.getDocument();

        windowWidthInputDoc.setDocumentFilter(new NumberFilter());
        windowHeightInputDoc.setDocumentFilter(new NumberFilter());

        apiKeyPanel.add(apiInput);

    }

    public void addPodcast() {
        clearDialogParams();
        addDialog = new CustomDialog(this, "Add podcast", addDialogPanel, acceptButton);
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

    public void editPodcast(Podcast podcast) {
        clearDialogParams();
        addDialog = new CustomDialog(this, "Edit podcast", addDialogPanel, acceptButton);
        channelNameInput.setText(podcast.getName());
        podcast.getParams().forEach(this::addParam);
        addDialog.setVisible(true);
    }

    public String getUpdateSecInput() {
        return updateSecInput.getText();
    }

    public void setUpdateSecInput(String updateSec) {
        this.updateSecInput.setText(updateSec);
    }

    public void windowBoxChecked() {
        windowScalePanel.setVisible(!windowScalableCheckbox.isSelected());
    }

    //GETTERS AND SETTERS SETTINGS

    public void setWindowScalableCheckbox(boolean selected) {
        windowScalableCheckbox.setSelected(selected);
        setResizable(selected);
        windowScalePanel.setVisible(!selected);
    }

    public void setWindowWidthInput(String Width) {
        windowWidthInput.setText(Width);
    }

    public void setWindowHeightInput(String height) {
        windowHeightInput.setText(height);
    }

    public boolean getWindowScalableCheckbox() {
        return windowScalableCheckbox.isSelected();
    }

    public String getWindowWidthInput() {
        return windowWidthInput.getText();
    }

    public String getWindowHeightInput() {
        return windowHeightInput.getText();
    }

    public void updateFrameSize() {
        this.setSize(Integer.parseInt(windowWidthInput.getText()), Integer.parseInt(windowHeightInput.getText()));
    }


//######################################################################################################################
//PODCAST TABLE IN SETTINGS
//######################################################################################################################


    private void initPodcastListPanel() {
        addPodcastButton  = new ImageButton(addImageDark, addImage, 40, 40);
        delPodcastButton  = new ImageButton(delImageDark, delImage, 40, 40);
        editPodcastButton = new ImageButton(editImageDark, editImage, 40, 40);

        addDelPanel.add(addPodcastButton);
        addDelPanel.add(delPodcastButton);
        addDelPanel.add(editPodcastButton);

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
        podcastTable.setSelectionBackground(Color.ORANGE);
        podcastTable.setFont(new Font("Arial", Font.PLAIN, 11));
        podcastTable.setShowGrid(false);

        scrollPanePodcastTable.setBackground(Color.WHITE);
        scrollPanePodcastTable.getViewport().setBackground(Color.WHITE);
        scrollPanePodcastTable.setBorder(BorderFactory.createEmptyBorder());
        scrollPanePodcastTable.setViewportBorder(BorderFactory.createEmptyBorder());

        podcastTable.getTableHeader().setBackground(Color.ORANGE);
        podcastTable.getTableHeader().setBorder(new LineBorder(Color.ORANGE, 5, true));
        podcastTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        podcastTable.getTableHeader().setReorderingAllowed(false);
        podcastTable.getTableHeader().setResizingAllowed(false);
    }

//######################################################################################################################
//DIALOG FOR ADDING PODCASTS IM SETTINGS
//######################################################################################################################

    private void initAddDialog() {
        paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.Y_AXIS));
        paramsPanel.setBackground(Color.WHITE);
        addDialogPanel.add(paramsPanel);

        this.addParamButton  = new ImageButton(addImageDark, addImage, 40, 40);
        this.delParamButton  = new ImageButton(delImageDark, delImage, 40, 40);

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

    public void addParam(String paramValue) {
        paramTextFields.add(createParamPanel(paramsPanel, paramValue));
        packCustDialog(addDialog);
        paramsPanel.revalidate();
        paramsPanel.repaint();
    }

    private JTextField createParamPanel(JPanel parent) {
        JPanel panel = new ColorPanel(new FlowLayout(), Color.WHITE, parent);
        new Label("Search param:", smallFont, panel);
        return new TextField("Insert Search param", smallFont, panel);
    }

    private JTextField createParamPanel(JPanel parent, String paramValue) {
        JPanel panel = new ColorPanel(new FlowLayout(), Color.WHITE, parent);
        new Label("Search param:", smallFont, panel);
        return new TextField(paramValue, smallFont, panel);
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

    public void podcastChangeButtonListeners(ActionListener listenForAddPodcastButton,
                                             ActionListener listenForDelPodcastButton,
                                             ActionListener listenForEditPodcastButton) {
        addPodcastButton.addActionListener(listenForAddPodcastButton);
        delPodcastButton.addActionListener(listenForDelPodcastButton);
        editPodcastButton.addActionListener(listenForEditPodcastButton);
    }

    public void settingsButtonListeners(ActionListener listenForWindowCheckBox) {
        windowScalableCheckbox.addActionListener(listenForWindowCheckBox);
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

    class Label extends JLabel {
        Icon icon;

        public Label(Image image, int width, int heigth) {
            this.icon = new ImageIcon(image.getScaledInstance(width, heigth, Image.SCALE_DEFAULT));
            this.setIcon(icon);
        }

        public Label(String text, Font font, JPanel parent) {
            this.setText(text);
            this.setFont(font);
            parent.add(this);
        }
    }

    class ColorPanel extends JPanel {

        public ColorPanel(Object layout, Color color, JPanel parent, String position) {
            this.setLayout((LayoutManager) layout);
            this.setBackground(color);
            parent.add(this, position);
        }

        public ColorPanel(Object layout, Color color, JPanel parent) {
            this.setLayout((LayoutManager) layout);
            this.setBackground(color);
            parent.add(this);
        }

        public ColorPanel(Object layout, Color color) {
            this.setLayout((LayoutManager) layout);
            this.setBackground(color);
        }
    }

    static class TextField extends JTextField {
    JTextField textField;

        public TextField(String text, Font font, JPanel parent) {
            textField = this;
            this.setText(text);
            this.addMouseListener(new ListenForMouseClickTextField());
            this.setFont(font);
            parent.add(this);
        }

        public TextField(String text, Font font) {
            textField = this;
            this.setText(text);
            this.addMouseListener(new ListenForMouseClickTextField());
            this.setFont(font);
        }

        public TextField(String text) {
            textField = this;
            this.setText(text);
            this.addMouseListener(new ListenForMouseClickTextField());
        }

        class ListenForMouseClickTextField extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {
                textField.selectAll();
            }
        }
    }

    class NumberFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            // Check if the inserted text contains only allowed characters (e.g., letters and digits)
            if (text.matches("[0-9]*")) {
                super.insertString(fb, offset, text, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            // Check if the replacement text contains only allowed characters (e.g., letters and digits)
            if (text.matches("[0-9]*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    static class CheckBox extends JCheckBox {
        public CheckBox(JPanel parent, Color color) {
            this.setBackground(color);
            parent.add(this);
        }
    }
}
