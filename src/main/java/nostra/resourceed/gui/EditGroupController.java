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

public class EditGroupController
{
    ResourceED application;

    @FXML
    private TextField nameText;

    private Group group;

    public static void show(ResourceED application, Group group)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.getUrl("EditGroup.fxml"));

            Parent parent = loader.load();
            EditGroupController controller = loader.getController();
            controller.lateInit(application, group);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle(Messages.get("EditGroupController.StageTitle"));
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void lateInit(ResourceED application, Group group)
    {
        this.application = application;
        this.group = group;

        nameText.setText(group.getName());
    }

    @FXML
    void editOnAction(ActionEvent event)
    {
        edit();
        close();
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

    private void edit()
    {
        String name = Utils.nullIfEmpty(nameText.getText());

        if (!group.setName(name))
            Utils.showError(Messages.get("Msg.Error.Group.CanNotEdit.Header"),
                    Messages.get("Groupname could not be editied."), nameText.getScene().getWindow());
    }
}
