package nostra.resourceed.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import nostra.resourceed.gui.FilterPreset.FilterType;

public class AddFilterController
{
    private AddFilterDialog dialog;

    @FXML
    private TextField nameText;
    
    @FXML
    private CheckBox resourceIDBox;

    @FXML
    private CheckBox resourcePathBox;

    @FXML
    private CheckBox resourcePathExtensionBox;

    @FXML
    private CheckBox resourceIsCachedBox;

    @FXML
    private CheckBox resourceCachedBox;

    @FXML
    private CheckBox resourceCachedExtensionBox;

    @FXML
    private CheckBox typeIDBox;

    @FXML
    private CheckBox typeNameBox;

    @FXML
    private CheckBox groupIDBox;

    @FXML
    private CheckBox groupNameBox;

    private List<FilterPreset> filters;

    @FXML
    void addOnAction(ActionEvent event)
    {
        List<FilterType> filterTypes = new ArrayList<>();

        if (resourceIDBox.isSelected())
            filterTypes.add(FilterType.RESOURCE_ID);

        if (resourcePathBox.isSelected())
            filterTypes.add(FilterType.RESOURCE_PATH);

        if (resourcePathExtensionBox.isSelected())
            filterTypes.add(FilterType.RESOURCE_PATH_FILE_EXTENSION);

        if (resourceIsCachedBox.isSelected())
            filterTypes.add(FilterType.RESOURCE_IS_CACHED);

        if (resourceCachedBox.isSelected())
            filterTypes.add(FilterType.RESOURCE_CACHED);

        if (resourceCachedExtensionBox.isSelected())
            filterTypes.add(FilterType.RESOURCE_CACHED_FILE_EXTENSION);

        if (typeIDBox.isSelected())
            filterTypes.add(FilterType.TYPE_ID);

        if (typeNameBox.isSelected())
            filterTypes.add(FilterType.TYPE_NAME);

        if (groupIDBox.isSelected())
            filterTypes.add(FilterType.GROUP_ID);

        if (groupNameBox.isSelected())
            filterTypes.add(FilterType.GROUP_NAME);

        FilterPreset filter = new FilterPreset(nameText.getText(), filterTypes);

        if (!filters.contains(filter))
        {
            dialog.setFilter(filter);
            dialog.close();
        } 
        else
        {
            Utils.showError("Filter already exists", "A filter with that name already exists.", dialog);
        }
    }

    @FXML
    void cancelOnAction(ActionEvent event)
    {
        dialog.close();
    }

    public void lateInit(AddFilterDialog dialog, List<FilterPreset> filters)
    {
        this.dialog = dialog;
        this.filters = filters;
    }
}
