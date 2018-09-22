package nostra.resourceed.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nostra.resourceed.Resource;
import nostra.resourceed.Type;

public class AddResourceController
{
    ResourceED application;

    @FXML
    private TextField pathResourceText;

    @FXML
    private TextField cachedResourceText;

    @FXML
    private ComboBox<Type> typeResourceChoice;

    public static void show(ResourceED application)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.getUrl("AddResource.fxml"));

            Parent parent = loader.load();
            AddResourceController controller = loader.getController();
            controller.lateInit(application);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle(Messages.get("AddResourceController.StageTitle"));
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

        typeResourceChoice.getItems().addAll(application.getEditor().getTypes());
    }

    @FXML
    void addAndCloseOnAction(ActionEvent event)
    {
        if (add())
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
        Stage stage = (Stage) pathResourceText.getScene().getWindow();

        stage.close();
    }

    private boolean add()
    {
        String path = Utils.nullIfEmpty(pathResourceText.getText());
        String cached = Utils.nullIfEmpty(cachedResourceText.getText());

        if (typeResourceChoice.getValue() == null)
        {
            Utils.showError(Messages.get("Msg.Error.Type.NothingChosen.Header"),
                    Messages.get("Msg.Error.Type.NothingChosen.Body"),
                    pathResourceText.getScene().getWindow());
            return false;
        }
        else
        {
            Resource resource = application.getEditor().addResource(path, cached,
                    typeResourceChoice.getValue().getId());

            if (resource == null)
            {
                Utils.showError(Messages.get("Msg.Error.Resource.CanNotAdd.Header"),
                        Messages.get("Msg.Error.Resource.CanNotAdd.Body"),
                        pathResourceText.getScene().getWindow());
                return false;
            }
            return true;
        }
    }
}
