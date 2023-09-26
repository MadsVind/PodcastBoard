import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Controller {

    private final Model model;
    private final UI ui;

    //Edited
    private int editIndex;

    // update thread
    private int updateIntervalSec = 30;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture updateThread;

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
        updateThread = executorService.scheduleAtFixedRate(this::updateThread, 0, updateIntervalSec, TimeUnit.SECONDS);
        closeThread();
    }

    private void loadInformation() {
        model.getPodcastListFromFile();
        updateApiKey();
        updatePodcastsShown();

        String[] settingsArr = model.settingFromFile();

        try {
            ui.setWindowScalableCheckbox(Boolean.parseBoolean(settingsArr[0]));
            ui.setWindowWidthInput(settingsArr[1]);
            ui.setWindowHeightInput(settingsArr[2]);
            ui.setUpdateSecInput(settingsArr[3]);
            updateIntervalSec = Integer.parseInt(settingsArr[3]);
        } catch (Exception err) {
            System.err.println(err);
        }

        ui.updateFrameSize();
    }

    private void updateThread() {
        updatePodcastsData();
        int tempInterval = Integer.parseInt(ui.getUpdateSecInput());
        if (updateIntervalSec == tempInterval) return;
        updateThread.cancel(false);
        updateThread = executorService.scheduleAtFixedRate(this::updateThread, 0, updateIntervalSec, TimeUnit.SECONDS);
    }

    private void closeThread() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                updateThread.cancel(false);
                System.out.println("Saving Data");
                String str = ui.getWindowScalableCheckbox() + " "
                        + ui.getWindowWidthInput() + " "
                        + ui.getWindowHeightInput() + " "
                        + ui.getUpdateSecInput();
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
