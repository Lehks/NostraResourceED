package nostra.resourceed.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nostra.resourceed.Group;
import nostra.resourceed.Resource;

/**
 * The controller for the dialog to add a resource to a new group.
 * 
 * @author Mahan Karimi, Dennis Franz
 */
public class AddNewGroupController
{
    ResourceED application;

    @FXML
    private TextField nameText;

    /**
     * The resource to add to the group.
     */
    private Resource resource;

    /**
     * Opens the dialog.
     * 
     * @param application The application that this dialog was opened.
     * @param resource    The resource to add.
     */
    public static void show(ResourceED application, Resource resource)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.getUrl("AddNewGroup.fxml"));

            Parent parent = loader.load();
            AddNewGroupController controller = loader.getController();
            controller.lateInit(application, resource);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle(Messages.get("AddNewGroupController.StageTitle"));
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void lateInit(ResourceED application, Resource resource)
    {
        this.application = application;
        this.resource = resource;
    }

    @FXML
    void addAndCloseOnAction(ActionEvent event)
    {
        add();
        close();
    }

    @FXML
    void addOnAction(ActionEvent event)
    {
        add();
    }

    @FXML
    void cancelOnAction(ActionEvent event)
    {
        close();
    }

    private void close()
    {
        Stage stage = (Stage) nameText.getScene().getWindow();

        stage.close();
    }

    private void add()
    {
        String name = Utils.nullIfEmpty(nameText.getText());

        Group group = application.getEditor().addGroup(name);

        if (group == null)
            Utils.showError(Messages.get("Msg.Error.Group.CanNotAdd.Header"),
                    Messages.get("Msg.Error.Group.CanNotAdd.Body"), nameText.getScene().getWindow());
        else
        {
            group.addMember(resource);
        }
    }
}
