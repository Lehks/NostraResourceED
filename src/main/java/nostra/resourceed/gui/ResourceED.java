package nostra.resourceed.gui;

import nostra.resourceed.Database;
import nostra.resourceed.Editor;
import nostra.resourceed.Group;
import nostra.resourceed.Resource;
import nostra.resourceed.Type;

public class ResourceED
{

    public static void main(String[] args)
    {
        Database db = Database.getInstance();
        
        Editor editor = new Editor(db);
        
        Group group = editor.getGroup(1);
        
        group.setName("Grp1");
        
        Resource resource = editor.getResource(2);
        
        resource.setPath("ololol");
        resource.setCached("rofl");
        
        resource.setType(2);
        
        Type type = resource.getType();
        
        type.setName("Typetypetype");
        
        System.out.println(type.getId());
        
        System.out.println(editor.getResources());
        System.out.println(editor.getTypes());
        System.out.println(editor.getGroups());
    }

}
