package gui.util;

import java.awt.event.ActionEvent;

import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) {
		return (Stage)((Node)event.getSource()).getScene().getWindow();
		
	}

	public static Stage currentStage(javafx.event.ActionEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

}