import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Controller {

    private Model model;
    private UI ui;

    public Controller(UI ui, Model model) {
        this.model = model;
        this.ui    = ui;

        this.ui.dialogButtonListeners(new listenForAddParamButton(), new listenForDelParamButton(), new listenForAcceptButton());
        this.ui.cardChangeButtonListener(new listenForCardChange());
        this.ui.podcastChangeButtonListeners(new listenForAddPodcastButton(), new listenForDelPodcastButton());

        this.ui.updatePodcastTable(Podcast.getSeachDataAs2dArr(model.getPodcasts(), model.getMaxPodcastParams()));

        model.getPodcastListFromFile();
        updatePodcasts();
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


    private void updatePodcasts() {
        if (!updateApiKey()) {
            System.err.println("ERR: No APIKEY");
            return;
        }
        model.updatePodcasts();

        System.out.println(model.getPodcasts().size());

        ArrayList<Image> podcastThumbnails = model.getPodcastThumbnails();
        ArrayList<String> podcastTitles    = model.getPodcastTitles();
        ui.updateThumbnails(podcastThumbnails, podcastTitles);

        String[][] podcastsTableData = Podcast.getSeachDataAs2dArr(model.getPodcasts(), model.getMaxPodcastParams());
        ui.updatePodcastTable(podcastsTableData);
    }

    class listenForAddPodcastButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ui.addPodcast();
        }
    }

    class listenForDelPodcastButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = ui.delPodcast();
            model.removePodcast(index);
            System.out.println(" delpodcast");
            updatePodcasts();
        }
    }

    class listenForAddParamButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
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
            model.addPodcast(podcast);
            updatePodcasts();
        }
    }

    class listenForCardChange implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ui.changeCard();
        }
    }
}
