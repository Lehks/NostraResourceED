package nostra.resourceed.gui;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The main class of the dialog to add a new filter.
 * 
 * @author Mahan Karimi, Dennis Franz
 */
public class AddFilterDialog extends Stage
{
    private FilterPreset filter;

    /**
     * Opens the dialog.
     * 
     * @param application The application that this dialog was opened.
     * @param filters     The list that the selected filters will be added to.
     */
    public AddFilterDialog(ResourceED owner, List<FilterPreset> filters)
    {
        FXMLLoader loader = new FXMLLoader(ResourceLoader.getUrl("AddFilter.fxml"));

        try
        {
            Pane root = loader.load();
            AddFilterController controller = loader.getController();
            controller.lateInit(this, filters);

            Scene scene = new Scene(root);

            setScene(scene);
            setTitle(Messages.get("AddFilterDialog.StageTitle"));
            initModality(Modality.APPLICATION_MODAL);
            initOwner(owner.getPrimaryStage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setFilter(FilterPreset filter)
    {
        this.filter = filter;
    }

    public FilterPreset getFilter()
    {
        return filter;
    }
}
