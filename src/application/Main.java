package application;

import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
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
    HBox hbox = new HBox();
    Label label = new Label("MILK WEIGHTS");
    root.setTop(label);
    root.setCenter(hbox);
    
    Button addData = new Button("Add Data");
    
    addData.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent e) {
        
        Stage popup = new Stage();
        VBox vbox = new VBox();
        Label l = new Label("Add Data");
        Label l2 = new Label("Upload a file: ");
        Button b = new Button("Choose File:");
        b.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent arg0) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.showOpenDialog(primaryStage);
            popup.hide();
          }
          
        });
        vbox.getChildren().addAll(l,l2,b);
        Scene scene = new Scene(vbox, 100,100);
        popup.setScene(scene);
        popup.show();
        
        
      }
      
    });


    Button readData = new Button("Read Data");

    Button report = new Button("Generate Report");
    
    hbox.getChildren().addAll(addData, readData, report);
    
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

