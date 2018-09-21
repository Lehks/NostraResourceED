package nostra.resourceed.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nostra.resourceed.Editor;

public class ResourceED extends Application
{
    private Editor editor;
    private MainController controller;
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.primaryStage = primaryStage;
        
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
    
    public static void main(String[] args)
    {
        launch(args);
    }

}
