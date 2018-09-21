package nostra.resourceed.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import nostra.resourceed.Editor;
import nostra.resourceed.Filter;
import nostra.resourceed.Group;
import nostra.resourceed.Resource;
import nostra.resourceed.Type;

public class MainController
{
    public final static String CONFIG_KEY_RECENT = "recent";
    public final static String CONFIG_KEY_FILTER = "filters";
    
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
    private ComboBox<FilterPreset> filterChoice;

    @FXML
    private ScrollPane filterOptionPane;
    
    @FXML
    private Button applyFilter;

    @FXML
    private Button addFilter;

    @FXML
    private Button removeFilter;
    
    private ObservableList<FilterPreset> loadedFilters;
    
    @FXML
    public void initialize()
    {
        loadedFilters = filterChoice.getItems();
        loadedFilters.add(new FilterPreset("<no filter>"));//create empty filter preset to allow removal
        filterChoice.getSelectionModel().select(0);
        
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
        
        filterChoice.disableProperty().bind(Bindings.isNull(application.editorProperty()));
        applyFilter.disableProperty().bind(Bindings.isNull(application.editorProperty()));
        addFilter.disableProperty().bind(Bindings.isNull(application.editorProperty()));
        removeFilter.disableProperty().bind(Bindings.isNull(application.editorProperty()));
        
        loadConfigFile();
        
        reloadRecentlyOpened();
        loadFilters();
    }

    private boolean createDefaultConfig() throws FileNotFoundException
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
        
        
        String path = getClass().getClassLoader().getResource("defaultconfig.json").getFile();
        InputStream istream = new BufferedInputStream(new FileInputStream(path));
        
        OutputStream ostream = new BufferedOutputStream(new FileOutputStream(application.getConfigFile()));
        
        int b;
        
        try
        {
            while((b = istream.read()) != -1)
            {
                System.out.println((char)b);
                ostream.write(b);
            }
            
            return true;
        } 
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        finally
        {
            try
            {
                istream.close();
                ostream.close();
            } 
            catch (IOException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        
        return false;
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
            
            try
            {
                configFile = new JSONObject(builder.toString());
                scanner.close();
            } 
            catch (JSONException e)
            {
                scanner.close();
                
                if(createDefaultConfig())
                {
                    loadConfigFile(); //TODO: avoid endless loop
                }
            }
        } 
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        
    }
    
    private void reloadRecentlyOpened()
    {
        menuRecentlyOpened.getItems().clear();
        
        if(!configFile.has(CONFIG_KEY_RECENT))
        {
            //fix the missing attribute
            configFile.put(CONFIG_KEY_RECENT, new JSONArray());
            return; //an empty array was just created -> there are no objects to put into the menu
        }
        
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
    
    private void loadFilters()
    {
        if(!configFile.has(CONFIG_KEY_FILTER))
        {
            //fix the missing attribute
            configFile.put(CONFIG_KEY_FILTER, new JSONArray());
            
            return;
        }
        
        JSONArray array = configFile.getJSONArray(CONFIG_KEY_FILTER);
        
        try
        {
            loadedFilters.addAll(FilterPreset.loadFilters(array));
        } 
        catch (FilterPresetException e)
        {
            System.err.println("Could not load filters: " + e.getMessage());
        }
    }
    
    @FXML
    void filterChoiceOnAction(ActionEvent event)
    {
        if(checkIfLoadedAndShow())
        {
            FilterPreset preset = filterChoice.getSelectionModel().getSelectedItem();
            
            if(preset != null)
            {
                filterOptionPane.setContent(preset.getFilterSettingsPane());
            }
        }
    }

    @FXML
    void applyFilterOnAction(ActionEvent event)
    {
        if(filterOptionPane.getContent() != null)
        {
            FilterSettingsPane filterSettings = (FilterSettingsPane) filterOptionPane.getContent();
            
            try
            {
                Filter filter = filterSettings.generateFilter();

                tableViewResource.getItems().clear();
                
                System.out.println(filter);
                
                if(filter == null)
                {
                    //get without filter, this is faster
                    tableViewResource.getItems().addAll(application.getEditor().getResources());
                }
                else
                {
                    System.out.println("filtered!");
                    tableViewResource.getItems().addAll(application.getEditor().getResources(filter));
                }
            } 
            catch (FilterSettingsException e)
            {
                Utils.showError("Invalid filter setting", e.getMessage(), 
                        application.getPrimaryStage());
            }
        }
    }

    @FXML
    void addFilterOnAction(ActionEvent event) 
    {
        AddFilterDialog dialog = new AddFilterDialog(application, loadedFilters);
        
        dialog.showAndWait();
        
        if(!(dialog.getFilter() == null))
        {
            loadedFilters.add(dialog.getFilter());
            
            //add to config file
            dialog.getFilter().addToJSON(configFile.getJSONArray(CONFIG_KEY_FILTER)); 
            
            //select last added filter preset
            filterChoice.getSelectionModel().select(loadedFilters.size() - 1);
        }
    }

    @FXML
    void removeFilterOnAction(ActionEvent event) 
    {
        if(filterChoice.getSelectionModel().getSelectedIndex() == 0)
        {
            Utils.showError("Can not remove filter", 
                    "This filter can not be removed.", 
                    application.getPrimaryStage());
            
            return;
        }
        
        FilterPreset filter = filterChoice.getSelectionModel().getSelectedItem();
        
        filterChoice.getItems().remove(filter);

        JSONArray filterArray = configFile.getJSONArray(CONFIG_KEY_FILTER);
        
        //remove filter by name (names are unique)
        for(Iterator<Object> i = filterArray.iterator(); i.hasNext();)
        {
            Object object = i.next();
            
            if(object instanceof JSONObject)
            {
                String name = ((JSONObject)object).getString(FilterPreset.FILTER_NAME);
                
                if(filter.getName().equals(name))
                {
                    i.remove();
                    return; //there can't be another filter, names are unique
                }
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
