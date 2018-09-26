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

public class FilterSettingsPane extends VBox
{
    private List<FilterOptionPane> filterOptions;
    
    public FilterSettingsPane(FilterPreset preset)
    {
        setSpacing(10.0);
     
        filterOptions = new ArrayList<>();
        
        int i = 0;
        
        for(FilterPreset.FilterType filterType: preset.getFilterTypes())
        {
            FilterOptionPane pane = makeFilterOptionPane(filterType);
            
            filterOptions.add(pane);
            getChildren().add(pane);
            
            //insert separator if not the last pane
            if(i < (preset.getFilterTypes().size() - 1))
            {
                getChildren().add(new Separator());
            }
            
            i++;
        }
    }
    
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
    
    public Filter generateFilter() throws FilterSettingsException
    {
        if(filterOptions.size() == 0)
            return null; //null indicates that there should be no filter
        else
        {
            Filter ret = filterOptions.get(0).getFilter();
            
            Iterator<FilterOptionPane> i = filterOptions.iterator();
            i.next(); //start at index 1
            
            while(i.hasNext())
            {
                ret.and(i.next().getFilter());
            }
            
            return ret;
        }
    }
    
    private abstract class FilterOptionPane extends VBox
    {
        public abstract Filter getFilter() throws FilterSettingsException;
    }

    private abstract class ReadValuePaneBase extends FilterOptionPane
    {
        private Label label;
        
        public ReadValuePaneBase(String title)
        {
            setPadding(new Insets(10, 10, 10, 10));
            setSpacing(10.0);
            label = new Label(title);
            
            getChildren().add(label);
        }
    }
    
    private abstract class ReadBooleanPane extends ReadValuePaneBase
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
            if(choice.getValue() != null)
                return choice.getValue();
            else
                throw new FilterSettingsException("No item selected");
        }
    }
    
    private abstract class ReadTextValuePaneBase extends ReadValuePaneBase
    {
        private TextField textField;
        
        public ReadTextValuePaneBase(String title)
        {
            super(title);
            
            textField = new TextField();
            textField.setPromptText(title);
            //textField.setAlignment(Pos.CENTER_RIGHT);
            
            getChildren().add(textField);
        }
        
        public String getText()
        {
            return textField.getText();
        }
    }
    
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
                
                if(str == null)
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
    
    private class ReadGroupIdPane extends ReadIntegerPane
    {
        public ReadGroupIdPane()
        {
            super("Group ID");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new GroupIDFilter(getValue());
        }
    }
    
    private class ReadGroupNamePane extends ReadStringPane
    {
        public ReadGroupNamePane()
        {
            super("Group Name");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new GroupNameFilter(getValue());
        }
    }

    private class ReadResourceCachedFileExtensionPane extends ReadStringPane
    {
        public ReadResourceCachedFileExtensionPane()
        {
            super("Cached Extension");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourceCachedFileExtensionFilter(getValue());
        }
    }

    private class ReadResourceCachedPane extends ReadStringPane
    {
        public ReadResourceCachedPane()
        {
            super("Cached");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourceCachedFilter(getValue());
        }
    }
    
    private class ReadIsCachedPane extends ReadBooleanPane
    {
        public ReadIsCachedPane()
        {
            super("Is Cached");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourceIsCachedFilter(getValue());
        }
    }
    
    private class ReadResourcePathFileExtensionPane extends ReadStringPane
    {
        public ReadResourcePathFileExtensionPane()
        {
            super("Path Extension");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourcePathFileExtensionFilter(getValue());
        }
    }

    private class ReadResourcePathPane extends ReadStringPane
    {
        public ReadResourcePathPane()
        {
            super("Path");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourcePathFilter(getValue());
        }
    }

    private class ReadResourceIdPane extends ReadIntegerPane
    {
        public ReadResourceIdPane()
        {
            super("Resource ID");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new ResourceIDFilter(getValue());
        }
    }
    
    private class ReadTypeIdPane extends ReadIntegerPane
    {
        public ReadTypeIdPane()
        {
            super("Type ID");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new TypeIDFilter(getValue());
        }
    }
    
    private class ReadTypeDescriptionPane extends ReadStringPane
    {
        public ReadTypeDescriptionPane()
        {
            super("Type Description");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new TypeDescriptionFilter(getValue());
        }
    }
    
    private class ReadTypeNamePane extends ReadStringPane
    {
        public ReadTypeNamePane()
        {
            super("Type Name");
        }
        
        @Override
        public Filter getFilter() throws FilterSettingsException
        {
            return new TypeNameFilter(getValue());
        }
    }
}
