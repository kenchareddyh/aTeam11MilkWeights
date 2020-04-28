package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Main extends Application {
  // store any command-line arguments that were entered.
  // NOTE: this.getParameters().getRaw() will get these also
  private List<String> args;
  
  private MilkManager mm = new MilkManager();// create an instance of milkmanager

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
    
    /**
     * Add csv file 
     */
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
            // get file
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            // parse file
            mm.milkParser(selectedFile.getPath());
            popup.hide();
          }
          
        });
        vbox.getChildren().addAll(l,l2,b);
        Scene scene = new Scene(vbox, 200,200);
        popup.setScene(scene);
        popup.show();

      }
      
    });

    Button readData = new Button("Read Data");
    
    /**
     * Does readData stuff
     */
    readData.setOnAction(new EventHandler<ActionEvent>() {

      int maxYear = 2019;
      int minYear = 2019;
      
      VBox cb = new VBox();

      //Scene scene = new Scene(cb, 200, 200);
      Stage popup = new Stage();

      ListView<String> list = new ListView<>();
      ObservableList<String> data = FXCollections.observableArrayList();

      @Override
      public void handle(ActionEvent arg0) {
        for (int i = minYear; i <= maxYear; i++) {
          for (int j = 1; j < 13; j++) {
            String value = i + "-" + j;
            data.add(value);
          }
        }
        
        // creates graph popup for the date thats selected
        list.getSelectionModel().selectedItemProperty()
            .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {

              TableView<MilkNode> table = new TableView<MilkNode>();

              Stage tableScene = new Stage();
              tableScene.setTitle("Table view");
              tableScene.setWidth(300);
              tableScene.setHeight(500);

              final Label label = new Label(new_val + " table");
              label.setFont(new Font("Arial", 20));

              TableColumn dateCol = new TableColumn("Date");
              TableColumn idCol = new TableColumn("Farmer_ID");
              TableColumn weightCol = new TableColumn("Weight");

              
              // create a new observable list to store data
              ObservableList<MilkNode> data = FXCollections.observableArrayList();
              
              // get milkList with mm with given year and month
              List<List<String>> milkList = mm.getYearMonth(new_val);
              
              
              //populate the observable list
              for(int i = 0; i < milkList.size(); i++) {    
                data.add(new MilkNode(milkList.get(i)));
              }
              
              
              // Associate data with columns
              dateCol.setCellValueFactory(new PropertyValueFactory<MilkNode, String>("date"));
              idCol.setCellValueFactory(new PropertyValueFactory<MilkNode, String>("farmID"));
              weightCol.setCellValueFactory(new PropertyValueFactory<MilkNode, String>("milkWeight"));
              
              // set data
              table.setItems(data);
              table.getColumns().addAll(dateCol, idCol, weightCol);
              

              final VBox vbox = new VBox();
              vbox.setSpacing(5);
              vbox.setPadding(new Insets(10, 0, 0, 10));
              vbox.getChildren().addAll(label, table);

              Scene scene = new Scene(vbox);
              tableScene.setScene(scene);


              tableScene.show();
            });
    
        list.setItems(data);
        cb.getChildren().add(list);
        Scene scene = new Scene(cb, 400, 400);
        popup.setScene(scene);
        popup.setTitle("Select the year and month you would like to read");
        popup.show();
      }
    });

    
    
    Button report = new Button("Generate Report");
    report.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        Stage popup = new Stage();
        popup.setTitle("Generate Report");
        BorderPane bPane = new BorderPane();
        VBox vbox = new VBox();
        Label l = new Label("Select the report you would like to generate");
        Button farm = new Button("Farm Report");
        Button annual = new Button("Annual Report");
        Button monthly = new Button("Monthly Report");
        Button dateRange = new Button("Date Range Report");
        
        bPane.setCenter(vbox);
        bPane.setPadding(new Insets(20));
        vbox.getChildren().addAll(l, farm, annual, monthly, dateRange);
        
        farm.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent arg0) {
            Stage popup1 = new Stage();
            BorderPane bordPane = new BorderPane();
            VBox vbox1 = new VBox();
            popup1.setTitle("Generate Farmer Report");
            Label l1 = new Label("Farmer id:");
            TextField t1 = new TextField();
            Label l2 = new Label("Year:");
            TextField t2 = new TextField();
            Button generate = new Button("Generate Report");
            vbox1.getChildren().addAll(l1,t1,l2,t2,generate);
            
            
            
          
            
            generate.setOnAction(new EventHandler<ActionEvent>() {
              
              

              @Override
              public void handle(ActionEvent arg0) {
                VBox v = new VBox();
                Stage graph = new Stage();
                
                
                
                
                  
                Stage tableScene = new Stage();
                tableScene.setTitle("Table view");
                tableScene.setWidth(300);
                tableScene.setHeight(500);
              
                TableView<MilkStats> table = new TableView<MilkStats>();
                TableColumn dateCol = new TableColumn("Date");
                TableColumn weightCol = new TableColumn("Total Weight");
                TableColumn percentCol = new TableColumn("Percent Weight");
                dateCol.setCellValueFactory(new PropertyValueFactory<MilkStats, String>("date"));
                weightCol.setCellValueFactory(new PropertyValueFactory<MilkStats, String>("totalWeight"));
                percentCol.setCellValueFactory(new PropertyValueFactory<MilkStats, String>("totalPercentage"));
                table.getColumns().addAll(dateCol, weightCol, percentCol);
                
                ObservableList<MilkStats> data = FXCollections.observableArrayList();
                
                // get milkList with mm with given year and month
                List<List<String>> milkList = mm.dataForAllMonths(t2.getText(), t1.getText());
                
                
                
                
              //populate the observable list
                for(int i = 0; i < milkList.size(); i++) {    
                  data.add(new MilkStats(milkList.get(i)));
                }
                
                
                table.setItems(data);
                
                
                
                final VBox vbox = new VBox();
                vbox.setSpacing(5);
                vbox.setPadding(new Insets(10, 0, 0, 10));
                vbox.getChildren().addAll(label, table);

                Scene scene = new Scene(vbox);
                tableScene.setScene(scene);


                tableScene.show();
           
                
                
                
                
              }
              
            });
            
            bordPane.setCenter(vbox1);
            bordPane.setPadding(new Insets(20));
            Scene s1 = new Scene(bordPane , 200,200);
            popup1.setScene(s1);
            popup1.show();
          }
          
        });
        
        annual.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent arg0) {
            Stage popup1 = new Stage();
            BorderPane bordPane = new BorderPane();
            VBox vbox1 = new VBox();
            popup1.setTitle("Generate Annual Report");
            
            Label l1 = new Label("Year:");
            TextField t1 = new TextField();
            Button generate = new Button("Generate Report");
            vbox1.getChildren().addAll(l1,t1,generate);
            generate.setOnAction(new EventHandler<ActionEvent>() {

              @Override
              public void handle(ActionEvent arg0) {
                Stage graph = new Stage();
                BorderPane bP = new BorderPane();
                Scene s2 = new Scene(bP , 600,400);
                graph.setTitle("Farm Report");
                graph.setScene(s2);
                graph.show();
              }
              
            });
            bordPane.setCenter(vbox1);
            bordPane.setPadding(new Insets(20));
            Scene s1 = new Scene(bordPane , 200,200);
            popup1.setScene(s1);
            popup1.show();
          }
          
        });
        
        monthly.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent arg0) {
            Stage popup1 = new Stage();
            BorderPane bordPane = new BorderPane();
            VBox vbox1 = new VBox();
            popup1.setTitle("Generate Monthly Report");
            
            Label l1 = new Label("Year:");
            TextField t1 = new TextField();
            Label l2 = new Label("Month:");
            TextField t2 = new TextField();
            Button generate = new Button("Generate Report");
            vbox1.getChildren().addAll(l1,t1,l2,t2,generate);
            bordPane.setCenter(vbox1);
            generate.setOnAction(new EventHandler<ActionEvent>() {

              @Override
              public void handle(ActionEvent arg0) {
                Stage graph = new Stage();
                BorderPane bP = new BorderPane();
                Scene s2 = new Scene(bP , 600,400);
                graph.setTitle("Farm Report");
                graph.setScene(s2);
                graph.show();
              }
              
            });
            bordPane.setPadding(new Insets(20));
            Scene s1 = new Scene(bordPane , 200,200);
            popup1.setScene(s1);
            popup1.show();
          }
          
        });
        
        dateRange.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent arg0) {
            Stage popup1 = new Stage();
            BorderPane bordPane = new BorderPane();
            VBox vbox1 = new VBox();
            popup1.setTitle("Generate Date Range Report");
            
            Label l1 = new Label("Start Date (Year/Month/Day):");
            TextField t1 = new TextField();
            Label l2 = new Label("End Date:");
            TextField t2 = new TextField();
            Button generate = new Button("Generate Report");
            vbox1.getChildren().addAll(l1,t1,l2,t2,generate);
            generate.setOnAction(new EventHandler<ActionEvent>() {

              @Override
              public void handle(ActionEvent arg0) {
                Stage graph = new Stage();
                BorderPane bP = new BorderPane();
                Scene s2 = new Scene(bP , 600,450);
                graph.setTitle("Farm Report");
                graph.setScene(s2);
                graph.show();
              }
              
            });
            bordPane.setCenter(vbox1);
            bordPane.setPadding(new Insets(20));
            Scene s1 = new Scene(bordPane , 200,200);
            popup1.setScene(s1);
            popup1.show();
          }
          
        });
        
        Scene scene = new Scene(bPane, 200,200);
        popup.setScene(scene);
        popup.show();
        
      }
      
    });
    
    hbox.getChildren().addAll(addData, readData, report);
    
    Scene mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);



    // Add the stuff and set the primary stage
    primaryStage.setTitle(APP_TITLE);
    primaryStage.setScene(mainScene);
    primaryStage.show();
  }
  
  
  
  /**
   * Milk node to store into data frame nodes
   * @author harshak
   *
   */
  public class MilkNode {
    private SimpleStringProperty date;
    private SimpleStringProperty farmID;
    private SimpleStringProperty milkWeight;
 
    MilkNode(List<String> list){
      this.date = new SimpleStringProperty(list.get(0));
      this.farmID = new SimpleStringProperty(list.get(1));
      this.milkWeight = new SimpleStringProperty(list.get(2));
      
    }
    
    public String getDate() {
      return date.get();
    }
    
    public String getFarmID() {
      return farmID.get();
    }
    
    public String getMilkWeight() {
      return milkWeight.get();
    }
    
    
  }
  
  /**
   * Milk stats for generating data
   *
   */
  public class MilkStats {
    private SimpleStringProperty date;
    private SimpleStringProperty totalWeight;
    private SimpleStringProperty totalPercentage;
        
    MilkStats(List<String> list){
      this.date = new SimpleStringProperty(list.get(0));
      this.totalWeight = new SimpleStringProperty(list.get(1));
      this.totalPercentage = new SimpleStringProperty(list.get(2));
      
    }
    
    public String getDate() {
      return date.get();
    }
    
    public String getTotalWeight() {
      return totalWeight.get();
    }
    
    public String getTotalPercentage() {
      return totalPercentage.get();
    }
    
    
  }
  

  /**
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }

  
}


