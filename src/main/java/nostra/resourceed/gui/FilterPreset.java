package nostra.resourceed.gui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FilterPreset
{
    public static final String FILTER_NAME = "name";
    public static final String FILTER_DATA = "data";
    
    private String name;
    private List<FilterType> filterTypes;
    
    public static ObservableList<FilterPreset> loadFilters(JSONArray filterRoot) throws FilterPresetException
    {
        ObservableList<FilterPreset> ret = FXCollections.observableArrayList();
        
        for(Object object: filterRoot)
        {
            if(!(object instanceof JSONObject))
                throw new FilterPresetException("Invalid object in filter array.");
            
            ret.add(new FilterPreset((JSONObject)object));
        }
        
        return ret;
    }
    
    public FilterPreset(String name, List<FilterType> filterTypes)
    {
        this.name = name;
        this.filterTypes = filterTypes;
    }

    public FilterPreset(String name)
    {
        this.name = name;
        this.filterTypes = new ArrayList<>();;
    }
    
    private FilterPreset(JSONObject filter) throws FilterPresetException
    {
        filterTypes = new ArrayList<>();
        
        if(!filter.has(FILTER_NAME))
            throw new FilterPresetException("JSON object does not contain the attribute \"name\".");
        
        if(!filter.has(FILTER_DATA))
            throw new FilterPresetException("JSON object does not contain the attribute \"data\".");
        
        name = filter.getString(FILTER_NAME);
        
        JSONArray array = filter.getJSONArray(FILTER_DATA);
        
        for(Object object: array)
        {
            if(!(object instanceof String))
                throw new FilterPresetException("JSON array \"data\" may only contain strings.");
            
            filterTypes.add(toFilterType((String)object));
        }
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    private FilterType toFilterType(String str) throws FilterPresetException
    {
        try
        {
            return FilterType.valueOf(str);
        } 
        catch (IllegalArgumentException e)
        {
            throw new FilterPresetException("Unknown filter type.", e);
        }
    }
    
    public FilterSettingsPane getFilterSettingsPane()
    {
        return new FilterSettingsPane(this);
    }
    
    public List<FilterType> getFilterTypes()
    {
        return filterTypes;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        else if(!(obj instanceof FilterPreset))
            return false;
        
        return name.equals(((FilterPreset)obj).getName());
    }
    
    public void addToJSON(JSONArray root)
    {
        JSONObject filter = new JSONObject();
        
        filter.put(FILTER_NAME, name);
        filter.put(FILTER_DATA, filterTypes);
        
        root.put(filter);
    }
    
    public enum FilterType
    {
        GROUP_ID("GROUP_ID"),
        GROUP_NAME("GROUP_NAME"),
        RESOURCE_CACHED_FILE_EXTENSION("RESOURCE_CACHED_FILE_EXTENSION"),
        RESOURCE_CACHED("RESOURCE_CACHED"),
        RESOURCE_PATH_FILE_EXTENSION("RESOURCE_PATH_FILE_EXTENSION"),
        RESOURCE_ID("RESOURCE_ID"),
        RESOURCE_IS_CACHED("RESOURCE_IS_CACHED"),
        RESOURCE_PATH("RESOURCE_PATH"),
        TYPE_ID("TYPE_ID"),
        TYPE_DESCRIPTION("TYPE_DESCRIPTION"),
        TYPE_NAME("TYPE_NAME");
        
        private String name;
        
        private FilterType(String name)
        {
            this.name = name;
        }
        
        public String getName()
        {
            return name;
        }
    }
}
