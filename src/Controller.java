import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Controller {

    private Model model;
    private UI ui;

    //Edited
    private int editIndex;

    public Controller(UI ui, Model model) {
        this.model = model;
        this.ui    = ui;

        this.ui.dialogButtonListeners(new listenForAddParamButton(),
                                      new listenForDelParamButton(),
                                      new listenForAcceptButton());
        this.ui.cardChangeButtonListener(new listenForCardChange());
        this.ui.podcastChangeButtonListeners(new listenForAddPodcastButton(),
                                             new listenForDelPodcastButton(),
                                             new listenForEditPodcastButton());

        this.ui.updatePodcastTable(Podcast.getSeachDataAs2dArr(model.getPodcasts(), model.getMaxPodcastParams()));

        model.getPodcastListFromFile();
        updateApiKey();
        updatePodcastsShown();
    }

    private boolean updateApiKey() {
        String apiKey = ui.getApiKey();
        if (!apiKey.isEmpty()) {
            model.setApiKey(apiKey);
            return true;
        }
        else {
            apiKey = model.initApiKeyFromFile();
            if (!apiKey.isEmpty()) {
                ui.setApiKey(apiKey);
                return true;
            }
        }
        return false;
    }


    private void updatePodcastsData() {
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

    class listenForAddPodcastButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editIndex = model.getPodcastsSize();
            ui.addPodcast();
        }
    }

    class listenForDelPodcastButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = ui.delPodcast();
            if (index == -1) return;
            model.removePodcast(index);
            updatePodcastsShown();
        }
    }

    class listenForEditPodcastButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editIndex = ui.delPodcast();
            if (editIndex == -1) return;
            Podcast beforePodcast = model.getPodcasts().get(editIndex);
            model.removePodcast(editIndex);
            ui.editPodcast(beforePodcast);
        }
    }

    class listenForAddParamButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            editIndex = model.getPodcastsSize();
            ui.addParam();
        }
    }

    class listenForDelParamButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ui.delParam();
        }
    }

    class listenForAcceptButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Podcast podcast = ui.acceptPodcast();
            model.addPodcast(podcast, editIndex);
            updatePodcastsData();
        }
    }

    class listenForCardChange implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ui.changeCard();
        }
    }
}
