import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {

    private final Model model;
    private final UI ui;

    //Edited
    private int editIndex;

    private final int updateIntervalSec = 600;
    private final ScheduledExecutorService updateInfoThread = Executors.newSingleThreadScheduledExecutor();

    public Controller(UI ui, Model model) {
        this.model = model;
        this.ui    = ui;

        this.ui.dialogButtonListeners(new ListenForAddParamButton(),
                                      new ListenForDelParamButton(),
                                      new ListenForAcceptButton());

        this.ui.cardChangeButtonListener(new ListenForCardChange());

        this.ui.podcastChangeButtonListeners(new ListenForAddPodcastButton(),
                                             new ListenForDelPodcastButton(),
                                             new ListenForEditPodcastButton());

        this.ui.settingsButtonListeners(new ListenForWindowBoxCheck());

        this.ui.updatePodcastTable(Podcast.getSeachDataAs2dArr(model.getPodcasts(), model.getMaxPodcastParams()));


        loadInformation();
        updateThread();
        saveDataOnClose();
    }

    private void loadInformation() {
        model.getPodcastListFromFile();
        updateApiKey();
        updatePodcastsShown();

        String[] settingsArr = model.windowSettingFromFile();

        if (!isEmptyStrArr(settingsArr)) {
            ui.setWindowScalableCheckbox(Boolean.parseBoolean(settingsArr[0]));
            ui.setWindowWidthInput(settingsArr[1]);
            ui.setWindowHeightInput(settingsArr[2]);
        }
        ui.updateFrameSize();
    }

    private void updateThread() {
        updateInfoThread.scheduleAtFixedRate(this::updatePodcastsData, 0, updateIntervalSec, TimeUnit.SECONDS);
    }

    private void saveDataOnClose() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                updateInfoThread.shutdown();
                System.out.println("Saving Data");
                String str = ui.getWindowScalableCheckbox() + " "
                        + ui.getWindowWidthInput() + " "
                        + ui.getWindowHeightInput();
                model.saveData(str);
                System.out.println("Saved");

            }
        }));
    }

    private Boolean isEmptyStrArr(String[] strArr) {
        if (strArr.length < 1) return true;

        for (String str : strArr) {
            if (Objects.equals(str, " ")) return true;
        }
        return false;
    }

    private boolean updateApiKey() {
        String apiKey = ui.getApiKey();
        if (!apiKey.isEmpty()) {
            model.setApiKey(apiKey);
            return true;
        }
        else {
            apiKey = model.initApiKeyFromFile();
            if (apiKey != null) {
                ui.setApiKey(apiKey);
                return true;
            }
        }
        return false;
    }


    private void updatePodcastsData() {
        System.out.println("update podcasts");
        if (!updateApiKey()) {
            System.err.println("ERR: No APIKEY");
            return;
        }
        model.updatePodcasts();
        updatePodcastsShown();
    }

    private void updatePodcastsShown() {
        ArrayList<Image> podcastThumbnails = model.getPodcastThumbnails();
        ArrayList<String> podcastTitles    = model.getPodcastTitles();
        ui.updateThumbnails(podcastThumbnails, podcastTitles);

        String[][] podcastsTableData = Podcast.getSeachDataAs2dArr(model.getPodcasts(), model.getMaxPodcastParams());
        ui.updatePodcastTable(podcastsTableData);
    }

    class ListenForAddPodcastButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editIndex = model.getPodcastsSize();
            ui.addPodcast();
        }
    }

    class ListenForDelPodcastButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = ui.delPodcast();
            if (index == -1) return;
            model.removePodcast(index);
            updatePodcastsShown();
        }
    }

    class ListenForEditPodcastButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editIndex = ui.delPodcast();
            if (editIndex == -1) return;
            Podcast beforePodcast = model.getPodcasts().get(editIndex);
            model.removePodcast(editIndex);
            ui.editPodcast(beforePodcast);
        }
    }

    class ListenForAddParamButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editIndex = model.getPodcastsSize();
            ui.addParam();
        }
    }

    class ListenForDelParamButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ui.delParam();
        }
    }

    class ListenForAcceptButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Podcast podcast = ui.acceptPodcast();
            model.addPodcast(podcast, editIndex);
            updatePodcastsData();
        }
    }

    class ListenForCardChange implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ui.changeCard();
        }
    }

    class ListenForWindowBoxCheck implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ui.windowBoxChecked();
        }
    }
}
