package application;

import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    // store any command-line arguments that were entered.
    // NOTE: this.getParameters().getRaw() will get these also
	private List<String> args;

	private static final int WINDOW_WIDTH = 300;
	private static final int WINDOW_HEIGHT = 200;
	private static final String APP_TITLE = "Milk Weights";

	@Override
	public void start(Stage primaryStage) throws Exception {
		// save args example
		args = this.getParameters().getRaw();
		BorderPane root = new BorderPane();
		VBox topBox = new VBox();
		VBox bottomBox = new VBox();
		Label label = new Label("MILK WEIGHTS");
		root.setTop(label);
		root.setLeft(topBox);
		root.setBottom(bottomBox);
		Button insertMilkButton = new Button("Insert Milk");
		Button removeMilkButton = new Button("Remove Milk");
        Button logIntoFarmButton = new Button("Log Into Farm");
        Button readFromFileButton = new Button("Read From File");
        Button createGraphButton = new Button("Create Graph");
        
        VBox insertMilkOptions = new VBox();
        TextField newLabel = new TextField("Label");
        TextField newWeight = new TextField("Weight");
        TextField newDate = new TextField("Date");
        Button insertMilkSubmit = new Button("Submit");
		insertMilkOptions.getChildren().addAll(newLabel, newWeight, newDate, insertMilkSubmit);
		
		VBox removeMilkOptions = new VBox();
        TextField removeLabel = new TextField("Label");
        Button removeMilkSubmit = new Button("Submit");
		removeMilkOptions.getChildren().addAll(removeLabel, removeMilkSubmit);
		
		VBox loginOptions = new VBox();
        TextField farmID = new TextField("Farm ID");
        Button loginSubmit = new Button("Submit");
		loginOptions.getChildren().addAll(farmID, loginSubmit);
		
		VBox readFromFileOptions = new VBox();
        TextField fileLocation = new TextField("File Location");
        Button readFromFileSubmit = new Button("Submit");
        readFromFileOptions.getChildren().addAll(fileLocation, readFromFileSubmit);
		
		
        topBox.getChildren().addAll(insertMilkButton, removeMilkButton, logIntoFarmButton, readFromFileButton);
        bottomBox.getChildren().add(createGraphButton);
        
		Scene mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		
		
		
		// Add the stuff and set the primary stage
		primaryStage.setTitle(APP_TITLE);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}

