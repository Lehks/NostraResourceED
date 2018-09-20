package nostra.resourceed.gui;

import nostra.resourceed.Database;
import nostra.resourceed.Editor;
import nostra.resourceed.Filter;
import nostra.resourceed.GroupNameFilter;
import nostra.resourceed.ResourceFileExtensionFilter;
import nostra.resourceed.ResourcePathFilter;
import nostra.resourceed.TypeNameFilter;

public class ResourceED
{

    public static void main(String[] args)
    {
        Database db = Database.getInstance();
        
        Editor editor = new Editor(db);
        
        /*Filter filter = new TypeNameFilter("MyType")
            .and(Filter.not(new ResourcePathFilter("Test")))
            .andNot(new GroupNameFilter("Grp2").or(new GroupNameFilter("Grp3")));*/
        
        Filter filter = new ResourceFileExtensionFilter("txt");
        
        System.out.println(editor.getResources(filter));
        
//        System.out.println(editor.getResource(2).getGroups());
//        System.out.println(editor.getResources());
//        System.out.println(editor.getTypes());
//        System.out.println(editor.getGroups());
    }

}
