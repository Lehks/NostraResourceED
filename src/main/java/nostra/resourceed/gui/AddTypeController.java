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
import nostra.resourceed.Type;

public class AddTypeController
{
    ResourceED application;

    @FXML
    private TextField nameText;

    @FXML
    private TextField descriptionText;

    public static void show(ResourceED application)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.getUrl("AddType.fxml"));

            Parent parent = loader.load();
            AddTypeController controller = loader.getController();
            controller.lateInit(application);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle(Messages.get("AddTypeController.StageTitle"));
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
        if(add());
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

    private boolean add()
    {
        String name = Utils.nullIfEmpty(nameText.getText());
        String description = Utils.nullIfEmpty(descriptionText.getText());

        Type type = application.getEditor().addType(name, description);

        if (type == null)
        {
            Utils.showError(Messages.get("Msg.Error.Type.CanNotAdd.Header"),
                    Messages.get("Msg.Error.Type.CanNotAdd.Body"), nameText.getScene().getWindow());
            
            return false;
        }
        else
            return true;
    }
}
