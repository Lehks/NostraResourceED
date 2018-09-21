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
    ResourceED application;

    @FXML
    private ComboBox<Group> groupChoice;

    private Resource resource;
    
    public static void show(ResourceED application, Resource resource)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(AddExistingGroupController.class.getClassLoader().getResource("AddExistingGroup.fxml"));
            
            Parent parent = loader.load();
            AddExistingGroupController controller = loader.getController();
            controller.lateInit(application, resource);
            
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle("Add Group");
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
        
        if(group == null)
            Utils.showError("No group", "No group was chosen.",
                    groupChoice.getScene().getWindow());
        else
            if(!group.addMember(resource))
                Utils.showError("Error adding resource to group", "The resource could not be added to the group.",
                        groupChoice.getScene().getWindow());
    }
}
