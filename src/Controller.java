import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Controller {

    private Model model = Model.getInstance();
    private UI ui       = UI.getInstance();

    public Controller(UI ui, Model model) {
        this.model = model;
        this.ui    = ui;

        this.ui.
    }

    abstract class podcastListStateChangedListner implements ActionListener {
        public void actionPerfomed(ActionEvent e) {
            String apiKeyFromUi = ui.getApiKey();
            if (!apiKeyFromUi.isEmpty()) model.setApiKey(apiKeyFromUi);
            model.updatePodcasts();
            ui.setThumbnails(model.getPodcasts());
        }
    }
}
