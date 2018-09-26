package nostra.resourceed.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import nostra.resourceed.Filter;
import nostra.resourceed.filter.GroupIDFilter;
import nostra.resourceed.filter.GroupNameFilter;
import nostra.resourceed.filter.ResourceCachedFileExtensionFilter;
import nostra.resourceed.filter.ResourceCachedFilter;
import nostra.resourceed.filter.ResourceIDFilter;
import nostra.resourceed.filter.ResourceIsCachedFilter;
import nostra.resourceed.filter.ResourcePathFileExtensionFilter;
import nostra.resourceed.filter.ResourcePathFilter;
import nostra.resourceed.filter.TypeDescriptionFilter;
import nostra.resourceed.filter.TypeIDFilter;
import nostra.resourceed.filter.TypeNameFilter;

/**
 * A custom window component that allows it to enter values for filters.
 * 
 * When added to a window, this pane allows it to enter the values that are
 * required by a specific filter preset.
 * 
 * @author Mahan Karimi, Dennis Franz
 */
public class FilterSettingsPane extends VBox
{
    private List<FilterOptionPane> filterOptions;

    /**
     * Creates a new instance.
     * 
     * @param preset The preset to construct the pane for.
     */
    public FilterSettingsPane(FilterPreset preset)
    {
        setSpacing(10.0);

        filterOptions = new ArrayList<>();

        int i = 0;

        for (FilterPreset.FilterType filterType : preset.getFilterTypes())
        {
            FilterOptionPane pane = makeFilterOptionPane(filterType);

            filterOptions.add(pane);
            getChildren().add(pane);

            // insert separator if not the last pane
            if (i < (preset.getFilterTypes().size() - 1))
            {
                getChildren().add(new Separator());
            }

            i++;
        }
    }

    /**
     * Returns the component for a particular FilterType.
     * 
     * This component allows it to enter the value for the filter of that
     * FilterType.
     * 
     * @param filterType The filter type to return the component for.
     * @return The component.
     */
    private FilterOptionPane makeFilterOptionPane(FilterPreset.FilterType filterType)
    {
        switch (filterType)
        {
        case GROUP_ID:
            return new ReadGroupIdPane();
        case GROUP_NAME:
            return new ReadGroupNamePane();
        case RESOURCE_CACHED_FILE_EXTENSION:
            return new ReadResourceCachedFileExtensionPane();
        case RESOURCE_CACHED:
            return new ReadResourceCachedPane();
        case RESOURCE_PATH_FILE_EXTENSION:
            return new ReadResourcePathFileExtensionPane();
        case RESOURCE_ID:
            return new ReadResourceIdPane();
        case RESOURCE_IS_CACHED:
            return new ReadIsCachedPane();
        case RESOURCE_PATH:
            return new ReadResourcePathPane();
        case TYPE_ID:
            return new ReadTypeIdPane();
        case TYPE_DESCRIPTION:
            return new ReadTypeDescriptionPane();
        case TYPE_NAME:
            return new ReadTypeNamePane();

        default:
            return null;
        }
    }

    /**
     * Generates a concrete filter from the filter preset and the values that are
     * currently entered in the pane.
     * 
     * @return The generated filter; or null if there are no filters in the preset.
     * @throws FilterSettingsException If the filter could not be created.
     */
    public Filter generateFilter() throws FilterSettingsException
    {
        if (filterOptions.size() == 0)
            return null; // null indicates that there should be no filter
        else
        {
            Filter ret = filterOptions.get(0).getFilter();

            Iterator<FilterOptionPane> i = filterOptions.iterator();
            i.next(); // start at index 1

            while (i.hasNext())
            {
                ret.and(i.next().getFilter());
            }

            return ret;
        }
    }

    /**
     * The base class of all components that allow it to enter a single value for a filter.
     * 
     * Each of these components allow it to enter the required values for a single filter.
     */
    private abstract class FilterOptionPane extends VBox
    {
        private Label label;

        public FilterOptionPane(String title)
        {
            setPadding(new Insets(10, 10, 10, 10));
            setSpacing(10.0);
            label = new Label(title);

            getChildren().add(label);
        }
        
        /**
         * Returns the concrete filter (with value).
         * 
         * @return The filter.
         * @throws FilterSettingsException If an exception occurred while making the filter.
         */
        public abstract Filter getFilter() throws FilterSettingsException;
    }

    /**
     * A FilterOptionPane that reads a boolean.
     */
    private abstract class ReadBooleanPane extends FilterOptionPane
    {
        private ChoiceBox<Boolean> choice;

        public ReadBooleanPane(String title)
        {
            super(title);
            choice = new ChoiceBox<>();
            choice.getItems().addAll(true, false);
            choice.setMaxWidth(Double.POSITIVE_INFINITY);

            getChildren().add(choice);
        }

        public boolean getValue() throws FilterSettingsException
        {
            if (choice.getValue() != null)
                return choice.getValue();
            else
                throw new FilterSettingsException("No item selected");
        }
    }

    /**
     * A FilterOptionPane that reads text.
     */
    private abstract class ReadTextValuePaneBase extends FilterOptionPane
    {
        private TextField textField;

        public ReadTextValuePaneBase(String title)
        {
            super(title);

            textField = new TextField();
            textField.setPromptText(title);
            // textField.setAlignment(Pos.CENTER_RIGHT);

            getChildren().add(textField);
        }

        public String getText()
        {
            return textField.getText();
        }
    }

    /**
     * A FilterOptionPane that reads a string.
     */
    private abstract class ReadStringPane extends ReadTextValuePaneBase
    {
        public ReadStringPane(String title)
        {
            super(title);
        }

        public String getValue()
        {
            return Utils.nullIfEmpty(getText());
        }
    }

    /**
     * A FilterOptionPane that reads in an integer.
     */
    private abstract class ReadIntegerPane extends ReadTextValuePaneBase
    {
        public ReadIntegerPane(String title)
        {
            super(title);
        }

        public Integer getValue() throws FilterSettingsException
        {
            try
            {
                String str = Utils.nullIfEmpty(getText());

                if (str == null)
                    return null;
                else
                    return Integer.parseInt(str);
            }
            catch (NumberFormatException e)
            {
                throw new FilterSettingsException(e);
            }
        }
    }

    /**
     * A FilterOptionPane for the filter GroupIDFilter.
     */
    private class ReadGroupIdPane extends ReadIntegerPane
    {
        public ReadGroupIdPane()
        {
            super(Messages.get("FilterSettingsPane.Titles.GroupID"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new GroupIDFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter GroupNameFilter.
     */
    private class ReadGroupNamePane extends ReadStringPane
    {
        public ReadGroupNamePane()
        {
            super(Messages.get("FilterSettingsPane.Titles.GroupName"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new GroupNameFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter ResourceCachedFileExtensionFilter.
     */
    private class ReadResourceCachedFileExtensionPane extends ReadStringPane
    {
        public ReadResourceCachedFileExtensionPane()
        {
            super(Messages.get("FilterSettingsPane.Titles.CachePathExtension"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourceCachedFileExtensionFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter ResourceCachedFilter.
     */
    private class ReadResourceCachedPane extends ReadStringPane
    {
        public ReadResourceCachedPane()
        {
            super(Messages.get("FilterSettingsPane.Titles.CachePath"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourceCachedFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter ResourceIsCachedFilter.
     */
    private class ReadIsCachedPane extends ReadBooleanPane
    {
        public ReadIsCachedPane()
        {
            super(Messages.get("FilterSettingsPane.Titles.IsCached"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourceIsCachedFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter ResourcePathFileExtensionFilter.
     */
    private class ReadResourcePathFileExtensionPane extends ReadStringPane
    {
        public ReadResourcePathFileExtensionPane()
        {
            super(Messages.get("FilterSettingsPane.Titles.PathExtension"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourcePathFileExtensionFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter ResourcePathFilter.
     */
    private class ReadResourcePathPane extends ReadStringPane
    {
        public ReadResourcePathPane()
        {
            super(Messages.get("FilterSettingsPane.Titles.Path"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourcePathFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter ResourceIDFilter.
     */
    private class ReadResourceIdPane extends ReadIntegerPane
    {
        public ReadResourceIdPane()
        {
            super(Messages.get("FilterSettingsPane.Titles.ResourceID"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourceIDFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter TypeIDFilter.
     */
    private class ReadTypeIdPane extends ReadIntegerPane
    {
        public ReadTypeIdPane()
        {
            super(Messages.get("FilterSettingsPane.Titles.TypeID"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new TypeIDFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter TypeDescriptionFilter.
     */
    private class ReadTypeDescriptionPane extends ReadStringPane
    {
        public ReadTypeDescriptionPane()
        {
            super(Messages.get("FilterSettingsPane.Titles.TypeDescription"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new TypeDescriptionFilter(getValue());
        }
    }

    /**
     * A FilterOptionPane for the filter TypeNameFilter.
     */
    private class ReadTypeNamePane extends ReadStringPane
    {
        public ReadTypeNamePane()
        {
            super(Messages.get("FilterSettingsPane.Titles.TypeName"));
        }

        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new TypeNameFilter(getValue());
        }
    }
}
