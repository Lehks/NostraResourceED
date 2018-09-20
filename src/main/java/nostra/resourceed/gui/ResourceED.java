package nostra.resourceed.gui;

import java.io.IOException;
import java.sql.SQLException;

import nostra.resourceed.Database;
import nostra.resourceed.Editor;
import nostra.resourceed.Filter;
import nostra.resourceed.ResourceFileExtensionFilter;

public class ResourceED
{

    public static void main(String[] args)
    {
        try
        {
            Editor editor = new Editor(new Database("nostra.db"));
            
            Filter filter = new ResourceFileExtensionFilter("txt");
            
            System.out.println(editor.getResources(filter));
            
            editor.close();
            
        } 
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        /*Filter filter = new TypeNameFilter("MyType")
            .and(Filter.not(new ResourcePathFilter("Test")))
            .andNot(new GroupNameFilter("Grp2").or(new GroupNameFilter("Grp3")));*/
        
//        System.out.println(editor.getResource(2).getGroups());
//        System.out.println(editor.getResources());
//        System.out.println(editor.getTypes());
//        System.out.println(editor.getGroups());
    }

}
