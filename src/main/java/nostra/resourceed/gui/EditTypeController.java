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

public class EditTypeController
{
    ResourceED application;

    @FXML
    private TextField nameText;
    
    private Type type;

    public static void show(ResourceED application, Type type)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(EditTypeController.class.getClassLoader().getResource("EditType.fxml"));
            
            Parent parent = loader.load();
            EditTypeController controller = loader.getController();
            controller.lateInit(application, type);
            
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle("Edit Type");
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
        this.application = application;
        this.type = type;
        
        nameText.setText(type.getName());
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
        String name = nameText.getText();
        
        if (!type.setName(name))
            Utils.showError("Error editing type", "Typename could not be edited.",
                    nameText.getScene().getWindow());
    }
}
