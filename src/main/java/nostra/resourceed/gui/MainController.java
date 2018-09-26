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
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
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
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import nostra.resourceed.Editor;
import nostra.resourceed.Filter;
import nostra.resourceed.Group;
import nostra.resourceed.Resource;
import nostra.resourceed.Type;

/**
 * The controller of the main window.
 * 
 * @author Mahan Karimi, Dennis Franz
 */
public class MainController
{
    public final static String CONFIG_KEY_RECENT = "recent"; //$NON-NLS-1$
    public final static String CONFIG_KEY_FILTER = "filters"; //$NON-NLS-1$

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
    private TableColumn<Type, String> tableColumnDescriptionType;

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

    private StringProperty makeProperNull(String string)
    {
        if (string == null)
            return new SimpleStringProperty("<NULL>");
        else
            return new SimpleStringProperty(string);
    }

    @FXML
    public void initialize()
    {
        loadedFilters = filterChoice.getItems();

        // create empty filter preset to
        loadedFilters.add(new FilterPreset(Messages.get("MainController.Filter.NoFilter.Name"))); //$NON-NLS-1$
        // allow removal
        filterChoice.getSelectionModel().select(0);

        // set cell factories for the tables
        tableColumnIdResource
                .setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());
        tableColumnPathResource.setCellValueFactory(param -> makeProperNull(param.getValue().getPath()));
        tableColumnCachedResource.setCellValueFactory(param -> makeProperNull(param.getValue().getCache()));
        tableColumnTypeNameResource
                .setCellValueFactory(param -> makeProperNull(param.getValue().getType().getName()));
        tableColumnTypeIdResource.setCellValueFactory(
                param -> new SimpleIntegerProperty(param.getValue().getType().getId()).asObject());

        tableColumnNameType.setCellValueFactory(param -> makeProperNull(param.getValue().getName()));
        tableColumnIdType
                .setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());
        tableColumnDescriptionType
                .setCellValueFactory(param -> makeProperNull(param.getValue().getDescription()));

        tableColumnNameGroup.setCellValueFactory(param -> makeProperNull(param.getValue().getName()));
        tableColumnIdGroup
                .setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getId()).asObject());

        // set description tooltip on type rows
        tableViewType.setRowFactory(tv -> new TableRow<Type>()
        {
            Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(Type item, boolean empty)
            {
                super.updateItem(item, empty);

                if (item == null)
                    setTooltip(null);
                else
                {
                    tooltip.setText(item.getDescription());
                    setTooltip(tooltip);
                }
            }
        });
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
        if (!application.getConfigFile().exists())
        {
            try
            {
                if (!application.getConfigFile().getParentFile().exists())
                    application.getConfigFile().getParentFile().mkdirs();

                application.getConfigFile().createNewFile();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }

        InputStream istream = new BufferedInputStream(ResourceLoader.getStream("defaultconfig.json")); //$NON-NLS-1$

        OutputStream ostream = new BufferedOutputStream(new FileOutputStream(application.getConfigFile()));

        int b;

        try
        {
            while ((b = istream.read()) != -1)
            {
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
                e1.printStackTrace();
            }
        }

        return false;
    }

    private void loadConfigFile()
    {
        if (!application.getConfigFile().exists())
        {
            try
            {
                if (!application.getConfigFile().getParentFile().exists())
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

            while (scanner.hasNextLine())
                builder.append(scanner.nextLine());

            try
            {
                configFile = new JSONObject(builder.toString());
                scanner.close();
            }
            catch (JSONException e)
            {
                scanner.close();

                if (createDefaultConfig())
                {
                    loadConfigFile(); // retry
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

        if (!configFile.has(CONFIG_KEY_RECENT))
        {
            // fix the missing attribute
            configFile.put(CONFIG_KEY_RECENT, new JSONArray());
            return; // an empty array was just created -> there are no objects
                    // to put into the menu
        }

        JSONArray array = configFile.getJSONArray(CONFIG_KEY_RECENT);

        for (Object object : array)
        {
            // ignore bad entries; aka. entries with wrong type(s)
            if (object instanceof String)
            {
                MenuItem item = new MenuItem((String) object);

                // action to reload with appropriate database
                item.setOnAction(event ->
                {
                    String path = ((MenuItem) event.getSource()).getText();
                    File file = new File(path);

                    registerExistingEditor(file);
                    addRecentFilesEntry(file);
                });

                menuRecentlyOpened.getItems().add(0, item);
            }
        }
    }

    private void loadFilters()
    {
        if (!configFile.has(CONFIG_KEY_FILTER))
        {
            // fix the missing attribute
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
            System.err.println("Could not load filters: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    @FXML
    void addResourceToNewGroup(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            Resource resource = tableViewResource.getSelectionModel().getSelectedItem();

            if (resource != null)
                AddNewGroupController.show(application, resource);
        }
    }

    @FXML
    void addResourceToExistingGroup(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            Resource resource = tableViewResource.getSelectionModel().getSelectedItem();

            if (resource != null)
                AddExistingGroupController.show(application, resource);
        }
    }

    @FXML
    void filterChoiceOnAction(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            FilterPreset preset = filterChoice.getSelectionModel().getSelectedItem();

            if (preset != null)
            {
                filterOptionPane.setContent(preset.getFilterSettingsPane());
            }
        }
    }

    @FXML
    void applyFilterOnAction(ActionEvent event)
    {
        if (filterOptionPane.getContent() != null)
        {
            FilterSettingsPane filterSettings = (FilterSettingsPane) filterOptionPane.getContent();

            try
            {
                Filter filter = filterSettings.generateFilter();

                tableViewResource.getItems().clear();

                if (filter == null)
                {
                    // get without filter, this is faster
                    tableViewResource.getItems().addAll(application.getEditor().getResources());
                }
                else
                {
                    tableViewResource.getItems().addAll(application.getEditor().getResources(filter));
                }
            }
            catch (FilterSettingsException e)
            {
                Utils.showError(Messages.get("Msg.Error.Filter.InvalidSetting.Header"), //$NON-NLS-1$
                        e.getMessage(), application.getPrimaryStage());
            }
        }
    }

    @FXML
    void addFilterOnAction(ActionEvent event)
    {
        AddFilterDialog dialog = new AddFilterDialog(application, loadedFilters);

        dialog.showAndWait();

        if (!(dialog.getFilter() == null))
        {
            loadedFilters.add(dialog.getFilter());

            // add to config file
            dialog.getFilter().addToJSON(configFile.getJSONArray(CONFIG_KEY_FILTER));

            // select last added filter preset
            filterChoice.getSelectionModel().select(loadedFilters.size() - 1);
        }
    }

    @FXML
    void removeFilterOnAction(ActionEvent event)
    {
        if (filterChoice.getSelectionModel().getSelectedIndex() == 0)
        {
            Utils.showError(Messages.get("Msg.Error.Filter.CanNotRemoveDefault.Header"), //$NON-NLS-1$
                    Messages.get("Msg.Error.Filter.CanNotRemoveDefault.Body"), //$NON-NLS-1$
                    application.getPrimaryStage());

            return;
        }

        FilterPreset filter = filterChoice.getSelectionModel().getSelectedItem();

        filterChoice.getItems().remove(filter);

        JSONArray filterArray = configFile.getJSONArray(CONFIG_KEY_FILTER);

        // remove filter by name (names are unique)
        for (Iterator<Object> i = filterArray.iterator(); i.hasNext();)
        {
            Object object = i.next();

            if (object instanceof JSONObject)
            {
                String name = ((JSONObject) object).getString(FilterPreset.FILTER_NAME);

                if (filter.getName().equals(name))
                {
                    i.remove();
                    return; // there can't be another filter, names are unique
                }
            }
        }
    }

    @FXML
    void addResource(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            AddResourceController.show(application);
        }
    }

    @FXML
    void addType(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            AddTypeController.show(application);
        }
    }

    @FXML
    void addGroup(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            AddGroupController.show(application);
        }
    }

    @FXML
    void editResource(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            Resource resource = tableViewResource.getSelectionModel().getSelectedItem();

            if (resource != null)
                ResourceDetailsController.show(application, resource);
        }
    }

    @FXML
    void editType(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            Type type = tableViewType.getSelectionModel().getSelectedItem();

            if (type != null)
                EditTypeController.show(application, type);
        }
    }

    @FXML
    void editGroup(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            Group group = tableViewGroup.getSelectionModel().getSelectedItem();

            if (group != null)
                EditGroupController.show(application, group);
        }
    }

    @FXML
    void fileNewOnAction(ActionEvent event)
    {
        closeEditor();

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(Messages.get("MainController.FileChooser.New.Title")); //$NON-NLS-1$
        fileChooser.setInitialFileName(Messages.get("MainController.FileChooser.New.InitFilename")); //$NON-NLS-1$
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir"))); //$NON-NLS-1$

        File database = fileChooser.showSaveDialog(application.getPrimaryStage());

        if (database != null) // check if a file was actually chosen
        {
            addRecentFilesEntry(database);
            registerNewEditor(database);
        }
    }

    @FXML
    void fileOpenOnAction(ActionEvent event)
    {
        closeEditor();

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(Messages.get("MainController.FileChooser.Open.Title")); //$NON-NLS-1$
        fileChooser.setInitialFileName(Messages.get("MainController.FileChooser.Open.InitFilename")); //$NON-NLS-1$
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir"))); //$NON-NLS-1$

        File database = fileChooser.showOpenDialog(application.getPrimaryStage());

        if (database != null) // check if a file was actually chosen
        {
            addRecentFilesEntry(database);

            registerExistingEditor(database);
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
        if (checkIfLoadedAndShow())
        {
            Resource resource = tableViewResource.getSelectionModel().getSelectedItem();

            if (resource != null)
            {
                boolean success = application.getEditor().removeResource(resource);

                if (!success)
                    Utils.showError(Messages.get("Msg.Error.Resource.CanNotRemove.Header"), //$NON-NLS-1$
                            Messages.get("Msg.Error.Resource.CanNotRemove.Body"), //$NON-NLS-1$
                            application.getPrimaryStage());
            }
        }
    }

    @FXML
    void removeType(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            Type type = tableViewType.getSelectionModel().getSelectedItem();

            if (type != null)
            {
                boolean success = application.getEditor().removeType(type);

                if (!success)
                    Utils.showError(Messages.get("Msg.Error.Type.CanNotRemove.Header"), //$NON-NLS-1$
                            Messages.get("Msg.Error.Type.CanNotRemove.Body"), //$NON-NLS-1$
                            application.getPrimaryStage());
            }
        }
    }

    @FXML
    void removeGroup(ActionEvent event)
    {
        if (checkIfLoadedAndShow())
        {
            Group group = tableViewGroup.getSelectionModel().getSelectedItem();

            if (group != null)
            {
                boolean accept = true;

                // warn if there are members in the group left
                if (group.getMembers().size() > 0)
                {
                    accept = Utils.askConfirmation(Messages.get("Msg.Confirmation.Group.HasMembersLeft.Head"),
                            Messages.get("Msg.Confirmation.Group.HasMembersLeft.Body"),
                            application.getPrimaryStage());
                }

                if (accept)
                {
                    boolean success = application.getEditor().removeGroup(group);

                    if (!success)
                        Utils.showError(Messages.get("Msg.Error.Group.CanNotRemove.Header"), //$NON-NLS-1$
                                Messages.get("Msg.Error.Group.CanNotRemove.Body"), //$NON-NLS-1$
                                application.getPrimaryStage());
                }
            }
        }
    }

    private void save()
    {
        if (checkIfLoaded())
        {
            if (!application.saveConfigFile())
                Utils.showError(Messages.get("Msg.Error.Config.SaveError.Header"), //$NON-NLS-1$
                        Messages.get("Msg.Error.Config.SaveError.Body"), //$NON-NLS-1$
                        application.getPrimaryStage());
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
            Utils.showError(Messages.get("Msg.Error.Database.NotOpened.Header"), //$NON-NLS-1$
                    Messages.get("Msg.Error.Database.NotOpened.Body"), //$NON-NLS-1$
                    application.getPrimaryStage());

            return false;
        }

        return true;
    }

    private boolean closeEditor()
    {
        tableViewResource.getItems().clear();
        tableViewType.getItems().clear();
        tableViewGroup.getItems().clear();

        application.setTitle(Messages.get("ResourceED.StageTitle.NoDatabaseOpened"));

        if (application.getEditor() == null)
            return true;

        try
        {
            application.getEditor().close();
            application.setEditor(null);

            return true;
        }
        catch (IOException e)
        {
            Utils.showError(Messages.get("Msg.Error.Database.CanNotSave.Header"), //$NON-NLS-1$
                    e.getMessage(), application.getPrimaryStage());

            return false;
        }
    }

    private void addRecentFilesEntry(File file)
    {
        JSONArray recent = configFile.getJSONArray(CONFIG_KEY_RECENT);

        // remove entry if the file is already in it - this way it will be added
        // at the
        // top
        {
            for (Iterator<Object> i = recent.iterator(); i.hasNext();)
            {
                Object object = i.next();

                if (object instanceof String)
                {
                    if (((String) object).equals(file.getAbsolutePath()))
                    {
                        i.remove();
                    }
                }
            }
        }

        if (recent.length() >= 10)
        {
            recent.remove(0);
        }

        recent.put(recent.length(), file.getAbsolutePath());

        reloadRecentlyOpened();
    }

    private void registerNewEditor(File file)
    {
        try
        {
            registerEditor(Editor.newDatabase(file));
        }
        catch (AccessDeniedException e)
        {
            e.printStackTrace();
            Utils.showError(Messages.get("Msg.Error.Database.CanNotCreateFile.Header"), //$NON-NLS-1$
                    e.getMessage(), application.getPrimaryStage());
        }
        catch (FileAlreadyExistsException e)
        {
            e.printStackTrace();
            Utils.showError(Messages.get("Msg.Error.Database.CanNotLoad.Header"), //$NON-NLS-1$
                    e.getMessage(), application.getPrimaryStage());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Utils.showError(Messages.get("Msg.Error.Database.CanNotCreateDB.Header"), //$NON-NLS-1$
                    e.getMessage(), application.getPrimaryStage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Utils.showError(Messages.get("Msg.Error.Database.CanNotCreateFile.Header"), //$NON-NLS-1$
                    e.getMessage(), application.getPrimaryStage());
        }
    }

    private void registerExistingEditor(File file)
    {
        try
        {
            registerEditor(new Editor(file));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Utils.showError(Messages.get("Msg.Error.Database.CanNotLoad.Header"), //$NON-NLS-1$
                    e.getMessage(), application.getPrimaryStage());
            application.setEditor(null);
        }
    }

    private void registerEditor(Editor editor)
    {
        // true by default, because if the editor does not need to be closed
        // there is no
        // problem either way
        boolean couldClose = true;

        if (application.getEditor() != null)
            couldClose = closeEditor();

        if (!couldClose)
            return;

        application.setEditor(editor);

        application.setTitle(editor.getDatabase().getPath());

        // add the resources/groups/types
        tableViewResource.getItems().addAll(application.getEditor().getResources());
        tableViewGroup.getItems().addAll(application.getEditor().getGroups());
        tableViewType.getItems().addAll(application.getEditor().getTypes());

        // register add/edit/remove events
        application.getEditor().getResourceAddEvents()
                .add(resource -> tableViewResource.getItems().add(resource));
        application.getEditor().getResourceEditEvents().add(type -> tableViewResource.refresh());
        application.getEditor().getResourceRemoveEvents()
                .add(id -> tableViewResource.getItems().removeIf(resource -> resource.getId() == id));

        application.getEditor().getTypeAddEvents().add(type -> tableViewType.getItems().add(type));
        application.getEditor().getTypeEditEvents().add(type ->
        {
            tableViewType.refresh();
            tableViewResource.refresh();
        });
        
        application.getEditor().getTypeRemoveEvents()
                .add(id -> tableViewType.getItems().removeIf(type -> type.getId() == id));

        application.getEditor().getGroupAddEvents().add(group -> tableViewGroup.getItems().add(group));
        application.getEditor().getGroupEditEvents().add(type -> tableViewGroup.refresh());
        application.getEditor().getGroupRemoveEvents()
                .add(id -> tableViewGroup.getItems().removeIf(group -> group.getId() == id));
    }

    public JSONObject getConfigFile()
    {
        return configFile;
    }
}
