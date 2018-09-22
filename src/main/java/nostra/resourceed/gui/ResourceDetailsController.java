package nostra.resourceed.gui;

import java.io.IOException;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nostra.resourceed.Group;
import nostra.resourceed.Resource;
import nostra.resourceed.Type;

public class ResourceDetailsController
{
    ResourceED application;

    @FXML
    private TextField pathResourceText;

    @FXML
    private TextField cachedResourceText;

    @FXML
    private ComboBox<Type> typeResourceChoice;

    @FXML
    private TableView<Group> groupsTable;

    @FXML
    private TableColumn<Group, String> groupsTableNameColumn;

    @FXML
    private TableColumn<Group, Integer> groupsTableIdColumn;

    private Resource resource;

    public static void show(ResourceED application, Resource resource)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.getUrl("ResourceDetails.fxml"));

            Parent parent = loader.load();
            ResourceDetailsController controller = loader.getController();
            controller.lateInit(application, resource);

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(application.getPrimaryStage());
            stage.setTitle(Messages.get("ResourceDetailsController.StageTitle"));
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize()
    {
        groupsTableIdColumn
                .setCellValueFactory(value -> new SimpleIntegerProperty(value.getValue().getId()).asObject());
        groupsTableNameColumn
                .setCellValueFactory(value -> new SimpleStringProperty(value.getValue().getName()));
    }

    public void lateInit(ResourceED application, Resource resource)
    {
        this.application = application;
        this.resource = resource;

        application.getEditor().getGroupEditEvents().add(group -> groupsTable.refresh());

        groupsTable.getItems().addAll(resource.getGroups());

        pathResourceText.setText(resource.getPath());
        cachedResourceText.setText(resource.getCache());
        typeResourceChoice.getItems().addAll(resource.getEditor().getTypes());
        typeResourceChoice.getSelectionModel().select(resource.getType());

        application.getEditor().getResourceAddToGroupEvents()
                .add((group, res) -> groupsTable.getItems().add(group));
        application.getEditor().getResourceRemoveFromGroupEvents()
                .add((group, res) -> groupsTable.getItems().remove(group));
    }

    @FXML
    void addToExGroupOnAction(ActionEvent event)
    {
        AddExistingGroupController.show(application, resource);
    }

    @FXML
    void addToNewGroupOnAction(ActionEvent event)
    {
        AddNewGroupController.show(application, resource);
    }

    @FXML
    void editGroupOnAction(ActionEvent event)
    {
        Group group = groupsTable.getSelectionModel().getSelectedItem();

        if (group != null)
            EditGroupController.show(application, group);
    }

    @FXML
    void removeFromGroupOnAction(ActionEvent event)
    {
        Group group = groupsTable.getSelectionModel().getSelectedItem();

        if (group != null)
            resource.removeFromGroup(group);
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
        Stage stage = (Stage) pathResourceText.getScene().getWindow();

        stage.close();
    }

    private void edit()
    {
        String path = Utils.nullIfEmpty(pathResourceText.getText());
        String cached = Utils.nullIfEmpty(cachedResourceText.getText());

        if (typeResourceChoice.getValue() == null)
            Utils.showError(Messages.get("Msg.Error.Type.NothingChosen.Header"),
                    Messages.get("Msg.Error.Type.NothingChosen.Body"),
                    pathResourceText.getScene().getWindow());
        else
        {
            if (!resource.setPath(path))
                Utils.showError(Messages.get("Msg.Error.Resource.CanNotEdit.Header"),
                        Messages.get("Msg.Error.Resource.CanNotEdit.Path.Body"),
                        pathResourceText.getScene().getWindow());

            if (!resource.setCached(cached))
                Utils.showError(Messages.get("Msg.Error.Resource.CanNotEdit.Header"),
                        Messages.get("Msg.Error.Resource.CanNotEdit.Cached.Body"),
                        pathResourceText.getScene().getWindow());

            if (!resource.setType(typeResourceChoice.getValue()))
                Utils.showError(Messages.get("Msg.Error.Type.NothingChosen.Header"),
                        Messages.get("Msg.Error.Type.NothingChosen.Body"),
                        pathResourceText.getScene().getWindow());
        }
    }
}
