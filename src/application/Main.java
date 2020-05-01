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

  // flag to see if data was added
  private boolean flag = false;

  private static final int WINDOW_WIDTH = 400;
  private static final int WINDOW_HEIGHT = 200;
  private static final String APP_TITLE = "Milk Weights";

  @Override
  public void start(Stage primaryStage) throws Exception {
    // save args example
    args = this.getParameters().getRaw();
    BorderPane root = new BorderPane();

    // hbox to have the three buttons horizontally placed beside each other
    HBox hbox = new HBox();
    // Hbox for error msg
    HBox hError = new HBox();
    Label label = new Label("MILK WEIGHTS");
    // sets the label to the top
    root.setTop(label);
    // sets the hbox with the buttons at the center
    root.setCenter(hbox);

    root.setBottom(hError);

    // button for adding add data
    Button addData = new Button("Add Data");

    // button for generating reports
    Button report = new Button("Generate Report");

    // button for displaying data
    Button readData = new Button("Display Data");


    /**
     * Add csv file when the button is selected
     */
    addData.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent e) {

        // creates a new Vbox for a new layout
        VBox vbox = new VBox();
        Label l = new Label("Display Data");
        Label l2 = new Label("Upload a file: ");

        // button for choosing a file
        Button b = new Button("Choose File:");

        b.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent arg0) {
            FileChooser fileChooser = new FileChooser();
            // get file
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            // parse file
            // if does not parse properly due to errors in the file will not return to the main
            // screen
            try {
              if (mm.milkParser(selectedFile.getPath())) {
                // returns to main screen
                primaryStage.setScene(previousScene.pop());

                // set flag to true if data has been succesfully added
                flag = true;
              }
            } catch (Exception e) {
              Label errorMSG = new Label("File wasn't Entered");
              vbox.getChildren().add(errorMSG);
            }


          }
        });

        // button to go to the previous screen
        Button back = new Button("Back");
        back.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent arg0) {
            // returns to the previous scene
            primaryStage.setScene(previousScene.pop());
          }
        });

        vbox.getChildren().addAll(l, l2, b, back);
        // creates a new scene
        Scene scene = new Scene(vbox, 200, 200);
        // adds the current scene to a stack
        previousScene.push(primaryStage.getScene());
        // changes the layout to a new scene
        primaryStage.setScene(scene);

      }
    });


    // error label to display if no data files have been entered
    Label noDataLabel = new Label("Can only access buttons after entering a valid file");

    /**
     * Does the read stuff
     */
    readData.setOnAction(new EventHandler<ActionEvent>() {


      ObservableList<String> data = FXCollections.observableArrayList();

      @Override
      public void handle(ActionEvent arg0) {
        // if data has been entered set action, otherwise don't set any action
        ListView<String> list = new ListView<>();

        if (flag == true) {
          hError.getChildren().remove(noDataLabel);

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
          list.getSelectionModel().selectedItemProperty().addListener(
              (ObservableValue<? extends String> ov, String old_val, String new_val) -> {

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

                //add the table into a vbox
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
              });

          //making a back button to go back one window
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
        } else if (hError.getChildren().contains(noDataLabel) == false) { // if no data has been
          // added create an error msg
          hError.getChildren().add(noDataLabel);
        }

      }

    });



    // when button is clicked on
    report.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        // new BorderPane for new Layout
        if (flag == true) {
          hError.getChildren().remove(noDataLabel);

          BorderPane bPane = new BorderPane();
          VBox vbox = new VBox();
          Label l = new Label("Select the report you would like to generate");

          // buttons for each type of report
          Button farm = new Button("Farm Report");
          Button annual = new Button("Annual Report");
          Button monthly = new Button("Monthly Report");
          Button dateRange = new Button("Date Range Report");

          // back button to return to previous scene
          Button back = new Button("Back");

          bPane.setCenter(vbox);
          bPane.setPadding(new Insets(20));
          vbox.getChildren().addAll(l, farm, annual, monthly, dateRange, back);

          // when the Farm Report button is pressed
          farm.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

              BorderPane bordPane = new BorderPane();
              VBox vbox1 = new VBox();
              Label l1 = new Label("Farmer id(*):");
              // holds the input for the Farmer ID
              TextField t1 = new TextField();
              Label l2 = new Label("Year:");
              // holds the input for the Year
              TextField t2 = new TextField();

              // Button to generate the report with given input
              Button generate = new Button("Generate Report");

              // Back button to return to previous screen
              Button back = new Button("Back");
              vbox1.getChildren().addAll(l1, t1, l2, t2, generate, back);


              Label l = new Label("Invalid data");

              // when the generate report button is pressed
              generate.setOnAction(new EventHandler<ActionEvent>() {


                @Override
                public void handle(ActionEvent arg0) {

                  try {
                    // to check if the Year input only had numbers in it or not
                    Integer.parseInt(t2.getText());


                    vbox1.getChildren().remove(l);
                    VBox v = new VBox();
                    Stage graph = new Stage();
                    primaryStage.setWidth(300);
                    primaryStage.setHeight(500);

                    // button to return to previous screen
                    Button back = new Button("Back");
                    back.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent arg0) {
                        // returns to the previous screen
                        primaryStage.setScene(previousScene.pop());
                      }
                    });


                    //create a new table
                    TableView<MilkStats> table = new TableView<MilkStats>();

                    //table headers and associating each column with a certain list
                    TableColumn dateCol = new TableColumn("Date");
                    TableColumn weightCol = new TableColumn("Total Weight");
                    TableColumn percentCol = new TableColumn("Percent Weight");
                    dateCol
                        .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item1"));
                    weightCol
                        .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item2"));
                    percentCol
                        .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item3"));
                    table.getColumns().addAll(dateCol, weightCol, percentCol);

                    
                    ObservableList<MilkStats> data = FXCollections.observableArrayList();

                    // get milkList with mm with given year and month

                    List<List<String>> milkList = mm.dataForAllMonths(t2.getText(), t1.getText());
                    
                    //sort milkList
                    for(int i = 1; i < milkList.size(); i++) {
                      String[] arr = milkList.get(i).get(0).split("-");
                      int ikey = Integer.parseInt(arr[0] + arr[1]);
                      List<String> keyString = milkList.get(i);
                      
                      int j = i - 1;
                      String[] arr2 = milkList.get(j).get(0).split("-");
                      int jkey = Integer.parseInt(arr2[0] + arr2[1]);
                      
                      while(j >= 0 && jkey > ikey) {
                        milkList.set((j + 1), milkList.get(j));
                        j = j - 1;
                      }
                      milkList.set((j+1), keyString);
                    }

                    // populate the observable list
                    for (int i = 0; i < milkList.size(); i++) {
                      data.add(new MilkStats(milkList.get(i)));
                    }


                    table.setItems(data);

                    //add table to a vbox
                    VBox vBox = new VBox();
                    vBox.setSpacing(5);
                    vBox.setPadding(new Insets(10, 0, 0, 10));
                    Label title = new Label(t2.getText() + " " + t1.getText() + " Milk Weights");

                    vBox.getChildren().addAll(title, table, back);

                    //adding a back button
                    Scene scene = new Scene(vBox);
                    previousScene.push(primaryStage.getScene());
                    primaryStage.setScene(scene);
                  } catch (Exception e) {
                    // If there was an error due to invalid input Displays a label saying input was
                    // invalid
                    try {
                      vbox1.getChildren().add(l);
                    }catch(Exception a) {
                      
                    }
                    
                  }



                }

              });

              // when back button is pressed
              back.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                  // returns to the previous screem
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

          // when the Annual Report button is pressed
          annual.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
              BorderPane bordPane = new BorderPane();
              VBox vbox1 = new VBox();

              Label l1 = new Label("Year(*):");
              // holds the input value for the Year
              TextField t1 = new TextField();
              // button to generate the report
              Button generate = new Button("Generate Report");
              // button to return to the previous screem
              Button back = new Button("Back");
              // when the back button is pressed
              back.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                  // returns to the previous screen
                  primaryStage.setScene(previousScene.pop());
                }
              });
              vbox1.getChildren().addAll(l1, t1, generate, back);

              Label l = new Label("Invalid data");
              // when the generate button is pressed
              generate.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent arg0) {
                  try {
                    // checks if it can be converted to an integer
                    Integer.parseInt(t1.getText());
                    vbox1.getChildren().remove(l);
                    VBox v = new VBox();
                    Stage graph = new Stage();

                    Stage tableScene = new Stage();
                    tableScene.setWidth(300);
                    tableScene.setHeight(500);

                    //make a new table with headers, then associate a header with a certain list
                    TableView<MilkStats> table = new TableView<MilkStats>();
                    TableColumn farmCol = new TableColumn("Farm ID");
                    TableColumn weightCol = new TableColumn("Total Weight");
                    TableColumn percentCol = new TableColumn("Percent Weight");
                    farmCol
                        .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item1"));
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
                    // button to return to the previous scene
                    Button back = new Button("Back");
                    // when the back button is pressed
                    back.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent arg0) {
                        // returns to the previous sceme
                        primaryStage.setScene(previousScene.pop());
                      }
                    });


                    //add the table to a vbox
                    final VBox vbox = new VBox();
                    vbox.setSpacing(5);
                    vbox.setPadding(new Insets(10, 0, 0, 10));

                    Label title = new Label(t1.getText() + " Milk Weights");
                    vbox.getChildren().addAll(title, table, back);

                    Scene scene = new Scene(vbox);
                    previousScene.push(primaryStage.getScene());
                    primaryStage.setScene(scene);

                  } catch (Exception e) {
                    // If there was an error with the input data adds a label to signify that the
                    // input was invalid
                    try {
                      vbox1.getChildren().add(l);
                    }catch(Exception a) {
                      
                    }
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

          // when the Monthly Report button is pressed
          monthly.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
              BorderPane bordPane = new BorderPane();
              VBox vbox1 = new VBox();

              Label l1 = new Label("Year(*):");
              // holds the input value for the year
              TextField t1 = new TextField();
              Label l2 = new Label("Month(*):");
              // holds the input value foe the Month
              TextField t2 = new TextField();

              // button to generate the report
              Button generate = new Button("Generate Report");
              // button to return to the previous screen
              Button back = new Button("Back");
              // when the back button is pressed
              back.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                  // returns to the previous screen
                  primaryStage.setScene(previousScene.pop());
                }
              });
              vbox1.getChildren().addAll(l1, t1, l2, t2, generate, back);
              bordPane.setCenter(vbox1);

              Label l = new Label("Invalid data");

              // when the generate button is pressed
              generate.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent arg0) {
                  try {
                    // checks if both year and month are in number format
                    Integer.parseInt(t1.getText());
                    Integer.parseInt(t2.getText());


                    vbox1.getChildren().remove(l);
                    VBox v = new VBox();
                    Stage graph = new Stage();

                    primaryStage.setWidth(300);
                    primaryStage.setHeight(500);
                    
                    //create a new table, add headers to the table, and associate each header with a certain list
                    TableView<MilkStats> table = new TableView<MilkStats>();
                    TableColumn farmCol = new TableColumn("Farm ID");
                    TableColumn weightCol = new TableColumn("Total Weight");
                    TableColumn percentCol = new TableColumn("Percent Weight");
                    farmCol
                        .setCellValueFactory(new PropertyValueFactory<MilkStats, String>("item1"));
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

                    // button to return to the previous screen
                    Button back = new Button("Back");
                    // when the back button is pressed
                    back.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent arg0) {
                        // returns to the previous screen
                        primaryStage.setScene(previousScene.pop());
                      }
                    });
                    
                    //add the table to the vbox
                    final VBox vbox = new VBox();
                    vbox.setSpacing(5);
                    vbox.setPadding(new Insets(10, 0, 0, 10));
                    Label title = new Label(t1.getText() + "-" + t2.getText() + " Milk Weights");
                    vbox.getChildren().addAll(title, table, back);

                    Scene scene = new Scene(vbox);
                    previousScene.push(primaryStage.getScene());
                    primaryStage.setScene(scene);
                  } catch (Exception e) {
                    // If there was an error due to the input values adds a label to signify that
                    // the input was invalid
                    try {
                      vbox1.getChildren().add(l);
                    }catch(Exception a) {
                      
                    }
                  }

                }

              });
              bordPane.setPadding(new Insets(20));
              Scene s1 = new Scene(bordPane, 200, 200);
              previousScene.push(primaryStage.getScene());
              primaryStage.setScene(s1);
            }

          });

          // when the Date Range Report button is pressed
          dateRange.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
              BorderPane bordPane = new BorderPane();
              VBox vbox1 = new VBox();

              Label l1 = new Label("Start Date(*) (Year/Month/Day):");
              // holds that input value for the start date
              TextField t1 = new TextField();
              Label l2 = new Label("End Date(*) :");
              // holds the input value for the end date
              TextField t2 = new TextField();
              // button to generate the report
              Button generate = new Button("Generate Report");
              // button to return to the previous screen
              Button back = new Button("Back");
              // when the back button is pressed
              back.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                  // returns to the previous screen
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

                  //create new table with headers, associate each header with a certain list
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
                  List<List<String>> milkList = new ArrayList<List<String>>();

                  try {
                    //parse and interpret the input text
                    String startDate = t1.getText();
                    String endDate = t2.getText();
                    String[] startArr = startDate.split("/");
                    String[] endArr = endDate.split("/");

                    if (Integer.parseInt(startArr[0] + startArr[1] + startArr[2]) > Integer
                        .parseInt(endArr[0] + endArr[1] + endArr[2])) {
                      throw new Exception();
                    }


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


                    //add a back button
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

                    Label title = new Label(t1.getText() + "-" + t2.getText() + " Milk Weights");
                    vbox.getChildren().addAll(title, table, back);

                    Scene scene = new Scene(vbox);
                    previousScene.push(primaryStage.getScene());
                    primaryStage.setScene(scene);

                  } catch (Exception e) {
                    // if there was an error due to the input data adds a label to signify that the
                    // input data was invalid
                    try {
                      vbox1.getChildren().add(l);
                    }catch(Exception a) {
                      
                    }
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
          // when the back button is pressed
          back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
              // returns to the previous screem
              primaryStage.setScene(previousScene.pop());
            }
          });

          Scene scene = new Scene(bPane, 200, 200);
          previousScene.push(primaryStage.getScene());
          primaryStage.setScene(scene);

        } else if (hError.getChildren().contains(noDataLabel) == false) { // if no data has been
                                                                          // added yet, create an
                                                                          // error msg
          hError.getChildren().add(noDataLabel);
        }


      }

    });



    hbox.getChildren().addAll(addData, readData, report);

    Scene mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);



    // Add the stuff and set the primary stage
    primaryStage.setTitle(APP_TITLE);
    primaryStage.setScene(mainScene);
    primaryStage.show();
  }

  public void setFlag() {
    flag = true;
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
   * The main method
   * 
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }


}
