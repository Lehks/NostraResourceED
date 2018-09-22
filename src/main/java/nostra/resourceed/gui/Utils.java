package nostra.resourceed.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;

public abstract class Utils
{
    public static void showError(String head, String body, Window owner)
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(owner);
        alert.setHeaderText(head);
        alert.setContentText(body);
        alert.showAndWait();
    }
    
    public static String nullIfEmpty(String string)
    {
        if(string != null && !string.trim().isEmpty())
            return string;
        else
            return null;
    }
}
