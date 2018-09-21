package nostra.resourceed.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import nostra.resourceed.Editor;
import nostra.resourceed.Group;
import nostra.resourceed.Resource;
import nostra.resourceed.Type;

public class MainController
{
    public final static String CONFIG_KEY_RECENT = "recent";
    
    private ResourceED application;

    private JSONObject configFile;
    
    @FXML
    private TableView<Resource> tableViewResource;

    @FXML
    private TableColumn<Resource, Integer> tableColumnIdResource;

    @FXML
    private TableColumn<Resource, String> tableColumnPathResource;

    @FXML
    private TableColumn<Resource, String> tableColumnCachedResource;

    @FXML
    private TableColumn<Resource, String> tableColumnTypeNameResource;

    @FXML
    private TableColumn<Resource, Integer> tableColumnTypeIdResource;

    @FXML
    private TableView<Group> tableViewGroup;

    @FXML
    private TableColumn<Group, Integer> tableColumnIdGroup;

    @FXML
    private TableColumn<Group, String> tableColumnNameGroup;

    @FXML
    private TableView<Type> tableViewType;

    @FXML
    private TableColumn<Type, Integer> tableColumnIdType;

    @FXML
    private TableColumn<Type, String> tableColumnNameType;

    @FXML
    private Menu menuRecentlyOpened;

    @FXML
    public void initialize()
    {
        //set cell factories for the tables
        tableColumnIdResource.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());
        tableColumnPathResource.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPath()));
        tableColumnCachedResource.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCache()));
        tableColumnTypeNameResource.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        tableColumnTypeIdResource.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getType().getId()).asObject());

        tableColumnNameType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        tableColumnIdType.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());

        tableColumnNameGroup.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        tableColumnIdGroup.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());
    }

    public void lateInit(ResourceED application)
    {
        this.application = application;
        
        loadConfigFile();
        
        reloadRecentlyOpened();
    }

    private void loadConfigFile()
    {

        if(!application.getConfigFile().exists())
        {
            try
            {
                if(!application.getConfigFile().getParentFile().exists())
                    application.getConfigFile().getParentFile().mkdirs();
                
                application.getConfigFile().createNewFile();
            } 
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        
        try
        {
            Scanner scanner = new Scanner(new FileInputStream(application.getConfigFile()));
            
            StringBuilder builder = new StringBuilder();
            
            while(scanner.hasNextLine())
                builder.append(scanner.nextLine());
            
            scanner.close();
            
            configFile = new JSONObject(builder.toString());
        } 
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        
    }
    
    private void reloadRecentlyOpened()
    {
        menuRecentlyOpened.getItems().clear();
        
        JSONArray array = configFile.getJSONArray(CONFIG_KEY_RECENT);

        for(Object object: array)
        {
            //ignore bad entries; aka. entries with wrong type(s)
            if(object instanceof String)
            {
                MenuItem item = new MenuItem((String)object);
                
                //action to reload with appropriate database
                item.setOnAction(event ->
                {
                    String path = ((MenuItem)event.getSource()).getText();
                    File file = new File(path);
                    
                    registerEditor(file);
                    addRecentFilesEntry(file);
                });
                
                menuRecentlyOpened.getItems().add(0, item);
            }
        }
    }
    
    @FXML
    void addResource(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            AddResourceController.show(application);
        }
    }

    @FXML
    void addType(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            AddTypeController.show(application);
        }
    }

    @FXML
    void addGroup(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            AddGroupController.show(application);
        }
    }

    @FXML
    void editResource(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            Resource resource = tableViewResource.getSelectionModel().getSelectedItem();
            
            if(resource != null)
                ResourceDetailsController.show(application, resource);
        }
    }

    @FXML
    void editType(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            Type type = tableViewType.getSelectionModel().getSelectedItem();
            
            if(type != null)
                EditTypeController.show(application, type);
        }
    }

    @FXML
    void editGroup(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            Group group = tableViewGroup.getSelectionModel().getSelectedItem();
            
            if(group != null)
                EditGroupController.show(application, group);
        }
    }
    
    @FXML
    void fileNewOnAction(ActionEvent event)
    {
        
    }

    @FXML
    void fileOpenOnAction(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Open database");
        fileChooser.setInitialFileName("NostraResourceDB.db");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        File database = fileChooser.showOpenDialog(application.getPrimaryStage());

        if (database != null) // check if a file was actually chosen
        {
            addRecentFilesEntry(database);
            registerEditor(database);
        }
    }

    @FXML
    void fileQuitOnAction(ActionEvent event)
    {
        closeEditor();

        Platform.exit();
    }

    @FXML
    void fileSaveOnAction(ActionEvent event)
    {
        save();
    }

    @FXML
    void removeResource(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            Resource resource = tableViewResource.getSelectionModel().getSelectedItem();
            
            if(resource != null)
            {
                boolean success = application.getEditor().removeResource(resource);
                
                if(!success)
                    Utils.showError("Error removing resource", "Could not remove type.", application.getPrimaryStage());
            }
        }
    }

    @FXML
    void removeType(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            Type type = tableViewType.getSelectionModel().getSelectedItem();
            
            if(type != null)
            {
                boolean success = application.getEditor().removeType(type);
                
                if(!success)
                    Utils.showError("Error removing type", "Could not remove type, is it still in use?", application.getPrimaryStage());
            }
        }
    }

    @FXML
    void removeGroup(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            Group group = tableViewGroup.getSelectionModel().getSelectedItem();
            
            if(group != null)
            {
                boolean success = application.getEditor().removeGroup(group);
                
                if(!success)
                    Utils.showError("Error removing group", "Could not remove group, is it still in use?", application.getPrimaryStage());
            }
        }
    }

    private void save()
    {
        if (checkIfLoaded())
        {
            registerEditor(new File(application.getEditor().getDatabase().getPath()));
        }
    }

    private boolean checkIfLoaded()
    {
        return application.getEditor() != null;
    }

    private boolean checkIfLoadedAndShow()
    {
        if (!checkIfLoaded())
        {
            Utils.showError("No Database opened", "No datbase is currently opened.", application.getPrimaryStage());

            return false;
        }

        return true;
    }

    private boolean closeEditor()
    {
        tableViewResource.getItems().clear();
        tableViewType.getItems().clear();
        tableViewGroup.getItems().clear();
        
        if(application.getEditor() == null)
            return true;
        
        try
        {
            application.getEditor().close();
            application.setEditor(null);
            
            return true;
        } 
        catch (IOException e)
        {
            Utils.showError("Database could not be saved", e.getMessage(), application.getPrimaryStage());

            return false;
        }
    }

    private void addRecentFilesEntry(File file)
    {
        JSONArray recent = configFile.getJSONArray(CONFIG_KEY_RECENT);
        
        //remove entry if the file is already in it - this way it will be added at the top
        {
            for(Iterator<Object> i = recent.iterator(); i.hasNext();)
            {
                Object object = i.next();
                
                if(object instanceof String)
                {
                    if(((String)object).equals(file.getAbsolutePath()))
                    {
                        i.remove();
                    }
                }
            }
        }
        
        if(recent.length() >= 10)
        {
            recent.remove(0);
        }
        
        recent.put(recent.length(), file.getAbsolutePath());
        
        reloadRecentlyOpened();
    }
    
    private void registerEditor(File database)
    {
        if(!database.exists())
        {
            Utils.showError("Database does not exist", 
                    String.format("The file \"%s\" does not exist.", 
                            database.getAbsolutePath()), application.getPrimaryStage());
            
            return;
        }
        
        // true by default, because if the editor does not need to be closed there is no
        // problem either way
        boolean couldClose = true;

        if (application.getEditor() != null)
            couldClose = closeEditor();

        if (!couldClose)
            return;

        try
        {
            application.setEditor(new Editor(database));
            
            //add the resources/groups/types
            tableViewResource.getItems().addAll(application.getEditor().getResources());
            tableViewGroup.getItems().addAll(application.getEditor().getGroups());
            tableViewType.getItems().addAll(application.getEditor().getTypes());
            
            //register add/edit/remove events
            application.getEditor().getResourceAddEvents().add(resource -> tableViewResource.getItems().add(resource));
            application.getEditor().getResourceEditEvents().add(type -> tableViewResource.refresh());
            application.getEditor().getResourceRemoveEvents().add(id -> tableViewResource.getItems().removeIf(resource -> resource.getId() == id));
            
            application.getEditor().getTypeAddEvents().add(type -> tableViewType.getItems().add(type));
            application.getEditor().getTypeEditEvents().add(type -> tableViewType.refresh());
            application.getEditor().getTypeRemoveEvents().add(id -> tableViewType.getItems().removeIf(type -> type.getId() == id));
            
            application.getEditor().getGroupAddEvents().add(group -> tableViewGroup.getItems().add(group));
            application.getEditor().getGroupEditEvents().add(type -> tableViewGroup.refresh());
            application.getEditor().getGroupRemoveEvents().add(id -> tableViewGroup.getItems().removeIf(group -> group.getId() == id));
        
        } 
        catch (SQLException e)
        {
            Utils.showError("Could not load database", e.getMessage(), application.getPrimaryStage());
            application.setEditor(null);
        }
    }
    
    public JSONObject getConfigFile()
    {
        return configFile;
    }
}
