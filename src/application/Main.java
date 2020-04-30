package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
  private Stack<Scene> previousScene = new Stack<Scene>();// keeps track of the last page visited

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

        VBox vbox = new VBox();
        Label l = new Label("Display Data");
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
            primaryStage.setScene(previousScene.pop());
          }
        });
        Button back = new Button("Back");
        back.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent arg0) {
            primaryStage.setScene(previousScene.pop());
          }
        });
        vbox.getChildren().addAll(l, l2, b, back);
        Scene scene = new Scene(vbox, 200, 200);
        previousScene.push(primaryStage.getScene());
        primaryStage.setScene(scene);


      }

    });

    Button readData = new Button("Display Data");

    /**
     * Does readData stuff
     */
    readData.setOnAction(new EventHandler<ActionEvent>() {

      ListView<String> list = new ListView<>();
      ObservableList<String> data = FXCollections.observableArrayList();

      @Override
      public void handle(ActionEvent arg0) {
        VBox cb = new VBox();
        int maxYear = mm.getMaxYear();
        int minYear = mm.getMinYear();
        data.clear();
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

              primaryStage.setWidth(300);
              primaryStage.setHeight(500);

              final Label label = new Label(new_val + " table");
              label.setFont(new Font("Arial", 20));

              TableColumn dateCol = new TableColumn("Date");
              TableColumn idCol = new TableColumn("Farmer_ID");
              TableColumn weightCol = new TableColumn("Weight");


              // create a new observable list to store data
              ObservableList<MilkNode> data = FXCollections.observableArrayList();

              // get milkList with mm with given year and month
              List<List<String>> milkList = mm.getYearMonth(new_val);


              // populate the observable list
              for (int i = 0; i < milkList.size(); i++) {
                data.add(new MilkNode(milkList.get(i)));
              }


              // Associate data with columns
              dateCol.setCellValueFactory(new PropertyValueFactory<MilkNode, String>("date"));
              idCol.setCellValueFactory(new PropertyValueFactory<MilkNode, String>("farmID"));
              weightCol
                  .setCellValueFactory(new PropertyValueFactory<MilkNode, String>("milkWeight"));

              // set data
              table.setItems(data);
              table.getColumns().addAll(dateCol, idCol, weightCol);


              final VBox vbox = new VBox();
              vbox.setSpacing(5);
              vbox.setPadding(new Insets(10, 0, 0, 10));
              Button back = new Button("Back");
              back.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                  primaryStage.setScene(previousScene.pop());
                  // previousScene.pop();
                }
              });
              vbox.getChildren().addAll(label, table, back);

              Scene scene = new Scene(vbox);
              previousScene.push(primaryStage.getScene());
              primaryStage.setScene(scene);
            });

        list.setItems(data);
        Button back = new Button("Back");
        back.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent arg0) {
            primaryStage.setScene(previousScene.pop());
          }
        });
        cb.getChildren().addAll(list, back);
        Scene scene = new Scene(cb, 400, 400);
        previousScene.push(primaryStage.getScene());
        primaryStage.setScene(scene);
      }
    });



    Button report = new Button("Generate Report");
    report.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        BorderPane bPane = new BorderPane();
        VBox vbox = new VBox();
        Label l = new Label("Select the report you would like to generate");
        Button farm = new Button("Farm Report");
        Button annual = new Button("Annual Report");
        Button monthly = new Button("Monthly Report");
        Button dateRange = new Button("Date Range Report");
        Button back = new Button("Back");

        bPane.setCenter(vbox);
        bPane.setPadding(new Insets(20));
        vbox.getChildren().addAll(l, farm, annual, monthly, dateRange, back);

        farm.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent arg0) {
            BorderPane bordPane = new BorderPane();
            VBox vbox1 = new VBox();
            Label l1 = new Label("Farmer id(*):");
            TextField t1 = new TextField();
            Label l2 = new Label("Year:");
            TextField t2 = new TextField();
            Button generate = new Button("Generate Report");
            Button back = new Button("Back");
            vbox1.getChildren().addAll(l1, t1, l2, t2, generate, back);



            Label l = new Label("Invalid data");
            generate.setOnAction(new EventHandler<ActionEvent>() {



              @Override
              public void handle(ActionEvent arg0) {
                try {
                  vbox1.getChildren().remove(l);
                  VBox v = new VBox();
                  Stage graph = new Stage();



                  primaryStage.setWidth(300);
                  primaryStage.setHeight(500);

                  Button back = new Button("Back");
                  back.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent arg0) {
                      primaryStage.setScene(previousScene.pop());
                    }
                  });

                  TableView<MilkStats> table = new TableView<MilkStats>();
                  TableColumn dateCol = new TableColumn("Date");
                  TableColumn weightCol = new TableColumn("Total Weight");
                  TableColumn percentCol = new TableColumn("Percent Weight");
                  dateCol.setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item1"));
                  weightCol
                      .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item2"));
                  percentCol
                      .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item3"));
                  table.getColumns().addAll(dateCol, weightCol, percentCol);

                  ObservableList<MilkStats> data = FXCollections.observableArrayList();

                  // get milkList with mm with given year and month

                  List<List<String>> milkList = mm.dataForAllMonths(t2.getText(), t1.getText());



                  // populate the observable list
                  for (int i = 0; i < milkList.size(); i++) {
                    data.add(new MilkStats(milkList.get(i)));
                  }


                  table.setItems(data);

                  VBox vBox = new VBox();
                  vBox.setSpacing(5);
                  vBox.setPadding(new Insets(10, 0, 0, 10));
                  vBox.getChildren().addAll(label, table, back);

                  Scene scene = new Scene(vBox);
                  previousScene.push(primaryStage.getScene());
                  primaryStage.setScene(scene);
                } catch (Exception e) {

                  vbox1.getChildren().add(l);
                }



              }

            });

            back.setOnAction(new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent arg0) {
                primaryStage.setScene(previousScene.pop());
              }
            });

            bordPane.setCenter(vbox1);
            bordPane.setPadding(new Insets(20));
            Scene s1 = new Scene(bordPane, 200, 200);
            previousScene.push(primaryStage.getScene());
            primaryStage.setScene(s1);
          }

        });

        annual.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent arg0) {
            BorderPane bordPane = new BorderPane();
            VBox vbox1 = new VBox();

            Label l1 = new Label("Year(*):");
            TextField t1 = new TextField();
            Button generate = new Button("Generate Report");
            Button back = new Button("Back");
            back.setOnAction(new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent arg0) {
                primaryStage.setScene(previousScene.pop());
              }
            });
            vbox1.getChildren().addAll(l1, t1, generate, back);

            Label l = new Label("Invalid data");
            generate.setOnAction(new EventHandler<ActionEvent>() {

              @Override
              public void handle(ActionEvent arg0) {
                try {
                  vbox1.getChildren().remove(l);
                  VBox v = new VBox();
                  Stage graph = new Stage();



                  Stage tableScene = new Stage();
                  tableScene.setWidth(300);
                  tableScene.setHeight(500);

                  TableView<MilkStats> table = new TableView<MilkStats>();
                  TableColumn farmCol = new TableColumn("Farm ID");
                  TableColumn weightCol = new TableColumn("Total Weight");
                  TableColumn percentCol = new TableColumn("Percent Weight");
                  farmCol.setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item1"));
                  weightCol
                      .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item2"));
                  percentCol
                      .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item3"));
                  table.getColumns().addAll(farmCol, weightCol, percentCol);

                  ObservableList<MilkStats> data = FXCollections.observableArrayList();

                  // get milkList with mm with given year and month
                  List<List<String>> milkList = mm.dataForAllFarmsAnnual(t1.getText());



                  // populate the observable list
                  for (int i = 0; i < milkList.size(); i++) {
                    data.add(new MilkStats(milkList.get(i)));
                  }


                  table.setItems(data);

                  Button back = new Button("Back");
                  back.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent arg0) {
                      primaryStage.setScene(previousScene.pop());
                    }
                  });



                  final VBox vbox = new VBox();
                  vbox.setSpacing(5);
                  vbox.setPadding(new Insets(10, 0, 0, 10));
                  vbox.getChildren().addAll(label, table, back);

                  Scene scene = new Scene(vbox);
                  previousScene.push(primaryStage.getScene());
                  primaryStage.setScene(scene);

                } catch (Exception e) {
                  vbox1.getChildren().add(l);
                }



              }

            });
            bordPane.setCenter(vbox1);
            bordPane.setPadding(new Insets(20));
            Scene s1 = new Scene(bordPane, 200, 200);
            previousScene.push(primaryStage.getScene());
            primaryStage.setScene(s1);
          }

        });

        monthly.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent arg0) {
            BorderPane bordPane = new BorderPane();
            VBox vbox1 = new VBox();

            Label l1 = new Label("Year(*):");
            TextField t1 = new TextField();
            Label l2 = new Label("Month(*):");
            TextField t2 = new TextField();
            Button generate = new Button("Generate Report");
            Button back = new Button("Back");
            back.setOnAction(new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent arg0) {
                primaryStage.setScene(previousScene.pop());
              }
            });
            vbox1.getChildren().addAll(l1, t1, l2, t2, generate, back);
            bordPane.setCenter(vbox1);

            Label l = new Label("Invalid data");
            generate.setOnAction(new EventHandler<ActionEvent>() {

              @Override
              public void handle(ActionEvent arg0) {
                try {
                  vbox1.getChildren().remove(l);
                  VBox v = new VBox();
                  Stage graph = new Stage();

                  primaryStage.setWidth(300);
                  primaryStage.setHeight(500);

                  TableView<MilkStats> table = new TableView<MilkStats>();
                  TableColumn farmCol = new TableColumn("Farm ID");
                  TableColumn weightCol = new TableColumn("Total Weight");
                  TableColumn percentCol = new TableColumn("Percent Weight");
                  farmCol.setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item1"));
                  weightCol
                      .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item2"));
                  percentCol
                      .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item3"));
                  table.getColumns().addAll(farmCol, weightCol, percentCol);

                  ObservableList<MilkStats> data = FXCollections.observableArrayList();

                  // get milkList with mm with given year and month
                  List<List<String>> milkList =
                      mm.dataForAllFarmsMonthly(t1.getText(), t2.getText());



                  // populate the observable list
                  for (int i = 0; i < milkList.size(); i++) {
                    data.add(new MilkStats(milkList.get(i)));
                  }


                  table.setItems(data);



                  Button back = new Button("Back");
                  back.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent arg0) {
                      primaryStage.setScene(previousScene.pop());
                    }
                  });
                  final VBox vbox = new VBox();
                  vbox.setSpacing(5);
                  vbox.setPadding(new Insets(10, 0, 0, 10));
                  vbox.getChildren().addAll(label, table, back);

                  Scene scene = new Scene(vbox);
                  previousScene.push(primaryStage.getScene());
                  primaryStage.setScene(scene);
                } catch (Exception e) {
                  vbox1.getChildren().add(l);
                }

              }

            });
            bordPane.setPadding(new Insets(20));
            Scene s1 = new Scene(bordPane, 200, 200);
            previousScene.push(primaryStage.getScene());
            primaryStage.setScene(s1);
          }

        });

        dateRange.setOnAction(new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent arg0) {
            BorderPane bordPane = new BorderPane();
            VBox vbox1 = new VBox();

            Label l1 = new Label("Start Date(*) (Year/Month/Day):");
            TextField t1 = new TextField();
            Label l2 = new Label("End Date(*) :");
            TextField t2 = new TextField();
            Button generate = new Button("Generate Report");
            Button back = new Button("Back");
            back.setOnAction(new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent arg0) {
                primaryStage.setScene(previousScene.pop());
              }
            });
            vbox1.getChildren().addAll(l1, t1, l2, t2, generate, back);

            Label l = new Label("Invalid data");
            generate.setOnAction(new EventHandler<ActionEvent>() {

              @Override
              public void handle(ActionEvent arg0) {

                vbox1.getChildren().remove(l);

                primaryStage.setWidth(300);
                primaryStage.setHeight(500);

                TableView<MilkStats> table = new TableView<MilkStats>();
                TableColumn farmCol = new TableColumn("Farm ID");
                TableColumn weightCol = new TableColumn("Total Weight");
                TableColumn percentCol = new TableColumn("Percent Weight");
                farmCol.setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item1"));
                weightCol.setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item2"));
                percentCol
                    .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item3"));
                table.getColumns().addAll(farmCol, weightCol, percentCol);

                ObservableList<MilkStats> data = FXCollections.observableArrayList();
                List<List<String>> milkList = new ArrayList<List<String>>();

                try {
                  String startDate = t1.getText();
                  String endDate = t2.getText();
                  String[] startArr = startDate.split("/");
                  String[] endArr = endDate.split("/");

                  // get milkList with mm with given year and month
                  milkList = mm.generateDateRangeReport(Integer.parseInt(startArr[0]),
                      Integer.parseInt(startArr[1]), Integer.parseInt(startArr[2]),
                      Integer.parseInt(endArr[0]), Integer.parseInt(endArr[1]),
                      Integer.parseInt(endArr[2]));
                  
                  

                  // populate the observable list
                  for (int i = 0; i < milkList.size(); i++) {
                    data.add(new MilkStats(milkList.get(i)));
                  }


                  table.setItems(data);



                  final VBox vbox = new VBox();
                  vbox.setSpacing(5);
                  vbox.setPadding(new Insets(10, 0, 0, 10));
                  Button back = new Button("Back");
                  back.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent arg0) {
                      primaryStage.setScene(previousScene.pop());
                    }
                  });
                  vbox.getChildren().addAll(label, table, back);

                  Scene scene = new Scene(vbox);
                  previousScene.push(primaryStage.getScene());
                  primaryStage.setScene(scene);
                  
                } catch (Exception e) {
                  vbox1.getChildren().add(l);
                }



              }

            });
            bordPane.setCenter(vbox1);
            bordPane.setPadding(new Insets(20));
            Scene s1 = new Scene(bordPane, 200, 200);
            previousScene.push(primaryStage.getScene());
            primaryStage.setScene(s1);
          }

        });
        back.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent arg0) {
            primaryStage.setScene(previousScene.pop());
          }
        });

        Scene scene = new Scene(bPane, 200, 200);
        previousScene.push(primaryStage.getScene());
        primaryStage.setScene(scene);

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
   * 
   * @author harshak
   *
   */
  public class MilkNode {
    private SimpleStringProperty date;
    private SimpleStringProperty farmID;
    private SimpleStringProperty milkWeight;

    MilkNode(List<String> list) {
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

  // my edit
  public class MilkStats {
    private SimpleStringProperty item1;
    private SimpleStringProperty item2;
    private SimpleStringProperty item3;

    MilkStats(List<String> list) {
      this.item1 = new SimpleStringProperty(list.get(0));
      this.item2 = new SimpleStringProperty(list.get(1));
      this.item3 = new SimpleStringProperty(list.get(2));

    }

    public String getItem1() {
      return item1.get();
    }

    public String getItem2() {
      return item2.get();
    }

    public String getItem3() {
      return item3.get();
    }


  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }


}



