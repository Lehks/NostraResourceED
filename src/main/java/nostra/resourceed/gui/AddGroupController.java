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

public class AddGroupController
{
    ResourceED application;

    @FXML
    private TextField nameText;

    public static void show(ResourceED application)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.getUrl("AddGroup.fxml"));

            Parent parent = loader.load();
            AddGroupController controller = loader.getController();
            controller.lateInit(application);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle(Messages.get("AddGroupController.StageTitle"));
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void lateInit(ResourceED application)
    {
        this.application = application;
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
    }
}
