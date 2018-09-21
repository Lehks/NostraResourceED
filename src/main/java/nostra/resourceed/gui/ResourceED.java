package nostra.resourceed.gui;

import java.io.File;
import java.io.FileWriter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nostra.resourceed.Editor;

public class ResourceED extends Application
{
    public final static String CONFIG_FILE_PATH = System.getProperty("user.home") + "/.nostraresourceed/config.json";
    
    private Editor editor;
    private MainController controller;
    private Stage primaryStage;
    private File configFile;
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.primaryStage = primaryStage;

        this.configFile = new File(CONFIG_FILE_PATH);
        
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Main.fxml"));
        Parent root = loader.load();
        
        controller = loader.getController();
        controller.lateInit(this);
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("NostraResourceED");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception
    {
        if(editor != null)
            editor.close();
        
        FileWriter writer = new FileWriter(configFile);
        
        writer.write(controller.getConfigFile().toString());
        writer.close();
    }
    
    public Stage getPrimaryStage()
    {
        return primaryStage;
    }
    
    public Editor getEditor()
    {
        return editor;
    }
    
    public void setEditor(Editor editor)
    {
        this.editor = editor;
    }
    
    public File getConfigFile()
    {
        return configFile;
    }
    
    public static void main(String[] args)
    {
        launch(args);
    }

}
