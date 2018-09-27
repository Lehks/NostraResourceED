package nostra.resourceed;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EditorTest
{
    private Editor editor;
    private File file;
    private Type predefType;

    @Before
    public void init() throws AccessDeniedException, FileAlreadyExistsException, SQLException, IOException
    {
        file = new File("EditorTestDB.db");
        editor = Editor.newDatabase(file);
        predefType = editor.addType("Type", null);
    }

    @After
    public void terminate() throws IOException
    {
        if(editor != null)
            editor.close();
        
        if(file != null)
            file.delete();
    }

    @Test
    public void resourceTest()
    {
        Resource res;
        
        {
            Resource resource = editor.addResource("path", null, predefType);
            res = resource;
             
            Assert.assertEquals(resource.getPath(), "path");
            Assert.assertEquals(resource.getCache(), null);
            Assert.assertEquals(resource.getTypeId(), predefType.getId());
        }

        {
            Resource resource = editor.addResource("other path", "cache", predefType);

            Assert.assertEquals(resource.getPath(), "other path");
            Assert.assertEquals(resource.getCache(), "cache");
            Assert.assertEquals(resource.getTypeId(), predefType.getId());
        }
        
        {
            Resource resource = editor.addResource("other path", null, 1);
            
            Assert.assertEquals(resource, null);
        }
        
        {
            Resource resource = editor.getResource(res.getId());

            Assert.assertEquals(resource.getPath(), res.getPath());
            Assert.assertEquals(resource.getCache(), res.getCache());
            Assert.assertEquals(resource.getTypeId(), res.getTypeId());
        }

        {
            Resource resource = editor.getResource(5000);
            
            Assert.assertEquals(resource, null);
        }
        
        {
            Assert.assertTrue(editor.removeResource(res.getId()));
            Assert.assertFalse(editor.removeResource(5000));
        }
    }

    @Test
    public void typeTest()
    {
        Type t;
        
        {
            Type type = editor.addType("other type", null);
            t = type;
            
            Assert.assertEquals(type.getName(), "other type");
            Assert.assertEquals(type.getDescription(), null);
        }
        
        {
            Type type = editor.addType("yet another type", "description");

            Assert.assertEquals(type.getName(), "yet another type");
            Assert.assertEquals(type.getDescription(), "description");
        }
        
        {
            Type type = editor.addType("other type", null);
            
            Assert.assertEquals(type, null);
        }

        {
            Type type = editor.getType(t.getId());

            Assert.assertEquals(type.getName(), t.getName());
            Assert.assertEquals(type.getDescription(), t.getDescription());
        }

        {
            Type type = editor.getType(5000);
            
            Assert.assertEquals(type, null);
        }
        
        {
            Assert.assertTrue(editor.removeType(t.getId()));
            Assert.assertFalse(editor.removeType(5000));
        }
    }

    @Test
    public void groupTest()
    {
        Group g;
        
        {
            Group group = editor.addGroup("group");
            g = group;
            
            Assert.assertEquals(group.getName(), "group");
        }
        
        {
            Group group = editor.addGroup("other group");

            Assert.assertEquals(group.getName(), "other group");
        }
        
        {
            Group group = editor.addGroup("group");

            Assert.assertEquals(group, null);
        }

        {
            Group group = editor.getGroup(g.getId());

            Assert.assertEquals(group.getName(), g.getName());
        }

        {
            Group group = editor.getGroup(5000);
            
            Assert.assertEquals(group, null);
        }
        
        {
            Assert.assertTrue(editor.removeGroup(g.getId()));
            Assert.assertFalse(editor.removeGroup(5000));
        }
    }

    @Test
    public void groupResourceTest()
    {
        Resource resource = editor.addResource("path", null, predefType);
        Group group = editor.addGroup("group");
        
        group.addMember(resource);
        
        Assert.assertTrue(group.getMembers().size() == 1);
        Assert.assertTrue(group.isMember(resource));
        Assert.assertTrue(group.removeMember(resource));
        
        resource.addToGroup(group);

        Assert.assertTrue(resource.getGroups().size() == 1);
        Assert.assertTrue(resource.isMemberOf(group));
        Assert.assertTrue(resource.removeFromGroup(group));
        
    }
}
