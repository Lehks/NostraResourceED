package nostra.resourceed.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nostra.resourceed.Group;
import nostra.resourceed.Resource;

public class AddExistingGroupController
{
    @FXML
    private ComboBox<Group> groupChoice;

    private Resource resource;

    public static void show(ResourceED application, Resource resource)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.getUrl("AddExistingGroup.fxml"));

            Parent parent = loader.load();
            AddExistingGroupController controller = loader.getController();
            controller.lateInit(application, resource);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle(Messages.get("AddExistingGroupController.StageTitle"));
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
        this.resource = resource;

        groupChoice.getItems().addAll(application.getEditor().getGroups());
    }

    @FXML
    void addOnAction(ActionEvent event)
    {
        add();
        close();
    }

    @FXML
    void cancelOnAction(ActionEvent event)
    {
        close();
    }

    private void close()
    {
        Stage stage = (Stage) groupChoice.getScene().getWindow();

        stage.close();
    }

    private void add()
    {
        Group group = groupChoice.getValue();

        if (group == null)
            Utils.showError(Messages.get("Msg.Error.Group.NoGroupSelected.Header"),
                    Messages.get("Msg.Error.Group.NoGroupSelected.Body"), groupChoice.getScene().getWindow());
        else if (!group.addMember(resource))
            Utils.showError(Messages.get("Msg.Error.Resource.CanNotAddToGroup.Header"),
                    Messages.get("Msg.Error.Resource.CanNotAddToGroup.Body"), groupChoice.getScene().getWindow());
    }
}
