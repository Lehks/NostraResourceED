package nostra.resourceed.gui;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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

    public static boolean askConfirmation(String head, String body, Window owner)
    {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setHeaderText(head);
        alert.setContentText(body);
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    public static String nullIfEmpty(String string)
    {
        if (string != null && !string.trim().isEmpty())
            return string;
        else
            return null;
    }
}
