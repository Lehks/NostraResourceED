package nostra.resourceed;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

public class FilterTest
{
    private Editor editor;
    private File file;

    private Type type1;
    private Type type2;
    
    private Resource resource1;
    private Resource resource2;
    
    private Group group1;
    private Group group2;
    
    @Before
    public void init() throws AccessDeniedException, FileAlreadyExistsException, SQLException, IOException
    {
        file = new File("FilterTestDB.db");
        editor = Editor.newDatabase(file);
        
        type1 = editor.addType("type1", null);
        type2 = editor.addType("type2", "description");
        
        resource1 = editor.addResource("file.txt", null, type1);
        resource2 = editor.addResource("file.png", "cache.cache", type2);
    
        group1 = editor.addGroup("group1");
        group2 = editor.addGroup("group2");
        
        group1.addMember(resource1);
        group2.addMember(resource2);
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
    public void testGroupIDFilter()
    {
        List<Resource> resources = editor.getResources(new GroupIDFilter(group1.getId()));
        
        Assert.assertTrue(resources.size() == 1);
        Assert.assertTrue(resources.get(0).equals(resource1));
    }

    @Test
    public void testGroupNameFilter()
    {
        List<Resource> resources = editor.getResources(new GroupNameFilter(group1.getName()));
        
        Assert.assertTrue(resources.size() == 1);
        Assert.assertTrue(resources.get(0).equals(resource1));
    }

    @Test
    public void testTypeIDFilter()
    {
        List<Resource> resources = editor.getResources(new TypeIDFilter(type1.getId()));
        
        Assert.assertTrue(resources.size() == 1);
        Assert.assertTrue(resources.get(0).equals(resource1));
    }

    @Test
    public void testTypeNameFilter()
    {
        List<Resource> resources = editor.getResources(new TypeNameFilter(type1.getName()));
        
        Assert.assertTrue(resources.size() == 1);
        Assert.assertTrue(resources.get(0).equals(resource1));
    }

    @Test
    public void testTypeDescriptionFilter()
    {
        {
            List<Resource> resources = editor.getResources(new TypeDescriptionFilter(type1.getDescription()));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource1));
        }
        
        {
            List<Resource> resources = editor.getResources(new TypeDescriptionFilter(type2.getDescription()));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource2));
        }
    }

    @Test
    public void testResourceCachedFileExtensionFilter()
    {
        List<Resource> resources = editor.getResources(new ResourceCachedFileExtensionFilter("cache"));
        
        Assert.assertTrue(resources.size() == 1);
        Assert.assertTrue(resources.get(0).equals(resource2));
    }

    @Test
    public void testResourceCachedFilter()
    {
        {
            List<Resource> resources = editor.getResources(new ResourceCachedFilter(null));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource1));
        }
        
        {
            List<Resource> resources = editor.getResources(new ResourceCachedFilter("cache.cache"));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource2));
        }
    }

    @Test
    public void testResourceIsCachedFilter()
    {
        {
            List<Resource> resources = editor.getResources(new ResourceIsCachedFilter(false));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource1));
        }
        
        {
            List<Resource> resources = editor.getResources(new ResourceIsCachedFilter(true));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource2));
        }
    }

    @Test
    public void testResourceIDFilter()
    {
        {
            List<Resource> resources = editor.getResources(new ResourceIDFilter(resource1.getId()));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource1));
        }
        
        {
            List<Resource> resources = editor.getResources(new ResourceIDFilter(resource2.getId()));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource2));
        }
    }

    @Test
    public void testResourcePathFileExtensionFilter()
    {
        {
            List<Resource> resources = editor.getResources(new ResourcePathFileExtensionFilter("txt"));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource1));
        }

        {
            List<Resource> resources = editor.getResources(new ResourcePathFileExtensionFilter("png"));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource2));
        }
    }

    @Test
    public void testResourcePathFilter()
    {
        {
            List<Resource> resources = editor.getResources(new ResourcePathFilter("file.txt"));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource1));
        }
        
        {
            List<Resource> resources = editor.getResources(new ResourcePathFilter("file.png"));
            
            Assert.assertTrue(resources.size() == 1);
            Assert.assertTrue(resources.get(0).equals(resource2));
        }
    }

}
