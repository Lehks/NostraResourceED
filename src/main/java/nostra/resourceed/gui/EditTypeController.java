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

/**
 * The controller for the dialog to edit an existing type.
 * 
 * @author Mahan Karimi, Dennis Franz
 */
public class EditTypeController
{
    @FXML
    private TextField nameText;

    @FXML
    private TextField descriptionText;

    private Type type;

    /**
     * Opens the dialog.
     * 
     * @param application The application that this dialog was opened.
     * @param type The type to edit.
     */
    public static void show(ResourceED application, Type type)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.getUrl("EditType.fxml"));

            Parent parent = loader.load();
            EditTypeController controller = loader.getController();
            controller.lateInit(application, type);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle(Messages.get("EditTypeController.StageTitle"));
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void lateInit(ResourceED application, Type type)
    {
        this.type = type;

        nameText.setText(type.getName());
        descriptionText.setText(type.getDescription());
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

    private boolean edit()
    {
        String name = Utils.nullIfEmpty(nameText.getText());
        String description = Utils.nullIfEmpty(descriptionText.getText());

        if (!type.setName(name))
        {
            Utils.showError(Messages.get("Msg.Error.Group.CanNotEdit.Header"),
                    Messages.get("Msg.Error.Group.CanNotEdit.Name.Body"), nameText.getScene().getWindow());

            return false;
        }

        if (!type.setDescription(description))
        {
            Utils.showError(Messages.get("Msg.Error.Group.CanNotEdit.Header"),
                    Messages.get("Msg.Error.Group.CanNotEdit.Description.Body"),
                    nameText.getScene().getWindow());

            return false;
        }

        return true;
    }
}
