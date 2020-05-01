package application;

import javafx.scene.control.Label;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class MilkManager {

  // the maximum year
  private int maxYear = 0;

  // the minmum year
  private int minYear = 3000;

  // flag for if data is added
  private boolean flag = false;

  // a log of all activity in milk manager
  private String log = "";

  /**
   * method that returns the max year
   * 
   * @return the max year
   */
  public int getMaxYear() {
    return maxYear;
  }

  /**
   * method that returns the min year
   * 
   * @return the min year
   */
  public int getMinYear() {
    return minYear;
  }

  public boolean getFlag() {
    return flag;
  }

  // Map that contains milkTable for corresponding year/month
  private Map<String, MilkTable> milkTableListMonth;

  private MilkTable milkTable;

  // A list of FarmIDs
  private List<String> farmList;


  // Map that contains milkTable for corresponding year
  private Map<String, MilkTable> milkTableListYear;

  /**
   * Constructor to initialize map
   */
  public MilkManager() {

    // initializing the milkTableListMonth, milkTableListYear and the farmList
    milkTableListMonth = new HashMap<String, MilkTable>();
    milkTableListYear = new HashMap<String, MilkTable>();
    farmList = new ArrayList<>();
  }



  /**
   * Returns corresponding milkTable for month/year
   * 
   * @param value - month/year
   * @return milkTable
   */
  public List<List<String>> getYearMonth(String value) {
    List<List<String>> list = new ArrayList<List<String>>();

    // if the key exists in HashMap return the corresponding milktable for that key
    if (milkTableListMonth.containsKey(value)) {
      MilkTable mt = milkTableListMonth.get(value);
      for (int i = 0; i < mt.size(); i++) {
        try {
          list.add(mt.get(i));
        } catch (KeyNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    log = log.concat("\n" + list);
    System.out.println(list);
    return list;

  }



  /**
   * Parse through given csv file
   * 
   * @param file
   */
  public boolean milkParser(String file) {
    String monthYearDate;
    String yearDate;
    String farmID;

    milkTable = new MilkTable();


    // parse through file a create a list of each row in data table
    List<List<String>> records = new ArrayList<>();
    try {
      BufferedReader csvReader = new BufferedReader(new FileReader(file));
      String line;
      int count = 0;
      try {
        while ((line = csvReader.readLine()) != null) {
          // split table into rows and add each row to records
          String[] values = line.split(",");

          // start of checking for invalid file input
          // checks to make sure it is not the first row
          if (count != 0) {

            // checks if there are only 3 columns
            if (values.length < 3) {
              // create popup error saying missing data
              createPopup("File has missing data. Not been accepted");
              return false;
            }
            if (values.length > 3) {
              // create popup error saying excess data
              createPopup("File has excess data. Not been accepted");
              return false;
            }

            // to check the date
            String[] dateSplit = values[0].split("-");

            // might have to check if its null after split

            // checking if the format is right
            if (dateSplit.length != 3) {
              // create popup date format wrong
              createPopup("File has wrong date format. Not been accepted");
              return false;
            }

            // checks if each part of the date is in number format
            for (int i = 0; i < 3; i++) {
              try {
                // to check if it can be converted to an integer or not
                Integer.parseInt(dateSplit[i]);
              } catch (Exception e) {
                // create a popup coz date has characters other than numbers and dashes
              	log = log.concat("\n" + "length");
              	System.out.println("length");
                createPopup("File has wrong date format. Not been accepted");
                return false;
              }
            }

            // checks if the weight is in number format
            try {
              // to check if it can be converted into an integer or not
              Integer.parseInt(values[2]);
            } catch (Exception e2) {
              // not sure which exception to catch
              // create a popup coz weight has characters other than nurmbers
              createPopup("File has characters other than numbers for weight. Not been accepted");
              return false;
            }

            // error handling end
          }


          records.add(Arrays.asList(values));
          // incrementing counter to show which row it is at
          count++;
        }
        csvReader.close();
        log = log.concat("\n" + records);
        System.out.println(records);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    // create a milktable for the data
    for (int i = 1; i < records.size(); i++) {
      try {
        // insert each row into a milkTable
        milkTable.insert(records.get(i));
        if (!farmList.contains(records.get(i).get(1))) {
          // insert unique farm_id in farmList
          farmList.add(records.get(i).get(1));
        }

      } catch (IllegalNullKeyException e) {
        e.printStackTrace();
      }

    }


    log = log.concat("\n" + "records:" + records.get(1).get(0));
    System.out.println("records:" + records.get(1).get(0));


    // create two keys, year/month and month
    String[] arr = records.get(1).get(0).split("-");
    monthYearDate = arr[0] + "-" + arr[1];
    yearDate = arr[0];
    farmID = records.get(1).get(1);

    if (Integer.parseInt(yearDate) > maxYear)
      maxYear = Integer.parseInt(yearDate);
    if (Integer.parseInt(yearDate) < minYear) {
      minYear = Integer.parseInt(yearDate);
    }

    // i think we need to remove these
    log = log.concat("\n" + maxYear + "\n" + minYear + "\n" + monthYearDate + "\n" + yearDate);
    System.out.println(maxYear);
    System.out.println(minYear);
    System.out.println(monthYearDate);
    System.out.println(yearDate);

    // store the key and the milkTable as its value in two hashmaps
    milkTableListMonth.put(monthYearDate, milkTable);
    milkTableListYear.put(yearDate, milkTable);

    // set flag to true if file was succesfully parsed
    flag = true;
    // returns true if no errors occurred and parsed through the csv file succesfully
    return true;

  }

  /**
   * get the total milk weight of a certain farm in a certain month
   * 
   * @param value  the month
   * @param farmID the farm
   * @return total milk weight of the farm in a month
   */
  public int getTotalMilkWeight(String value, String farmID) throws Exception {
    try {
      int totalMilk = 0;
      // get a milktable from the hashtable of a month
      MilkTable mt = milkTableListMonth.get(value);
      if (mt != null) {
        // go through the milk table and find if the farm id of a certain index matches the farmID
        for (int i = 0; i < mt.size(); i++) {

          if (mt.getFarmId(i).equals(farmID)) {
            // add the milk weight to the total milk weight
            totalMilk = totalMilk + mt.getMilkWeight(i);
          }
        }
      }


      return totalMilk;

    } catch (Exception e) {
      throw new Exception();
    }
  }


  /**
   * get the total milk weight of a certain farm in a certain year
   * 
   * @param value  the year
   * @param farmID the farm
   * @return total milk weight of the farm in a year
   */
  public int getTotalMilkWeightYear(String value, String farmID) throws Exception {
    try {
      int totalMilk = 0;
      // get a milk table from the hashtable
      MilkTable mt = milkTableListYear.get(value);
      for (int i = 0; i < mt.size(); i++) {
        // if the id's match, then add the corresponding milk weight to the total
        if (mt.getFarmId(i).equals(farmID)) {

          totalMilk = totalMilk + mt.getMilkWeight(i);
        }
      }

      return totalMilk;
    } catch (Exception e) {
      throw new Exception();
    }

  }


  /**
   * gets the percent of the total milk weight that a certain farm produced
   * 
   * @param value  the month
   * @param farmID the farm id
   * @return the percentage
   */
  public double getPercentMilkWeight(String value, String farmID) throws Exception {
    try {
      // get the total milk weight of a certain month
      double total = (double) getTotalMilkWeight(value, farmID);
      double grandTotal = 0.0;
      MilkTable mt = milkTableListMonth.get(value);

      if (mt != null) {
        for (int i = 0; i < mt.size(); i++) {
          // add the corresponding farm's milk weight to the grand total
          grandTotal = grandTotal + mt.getMilkWeight(i);
        }
      }
      // get a percent by dividing total by grandTotal, then multiplying it by 100
      return (total / grandTotal) * 100;
    } catch (Exception e) {
      throw new Exception();
    }

  }


  /**
   * gets the percent of the total milk weight that a certain farm produced over a certain year
   * 
   * @param value  - the year
   * @param farmID - the farm id
   * @return the percentage
   * @throws Exception
   */
  public double getPercentMilkWeightYear(String value, String farmID) throws Exception {
    try {
      // get a total for a year, instead of just for a month
      double total = (double) getTotalMilkWeightYear(value, farmID);
      double grandTotal = 0.0;

      MilkTable mt = milkTableListYear.get(value);
      for (int i = 0; i < mt.size(); i++) {
        // add the corresponding milk weight to the grand total
        grandTotal = grandTotal + mt.getMilkWeight(i);
      }
      // compute the percent
      return (total / grandTotal) * 100;
    } catch (Exception e) {
      throw new Exception();
    }

  }

  /**
   * get a 2D arraylist for the data of all months, its purpose is to build the MilkStats class in
   * the main class
   * 
   * @param year the year
   * @param farmID the farm id
   * @return a 2D arraylist of data
   * @throws Exception
   */
  public List<List<String>> dataForAllMonths(String year, String farmID) throws Exception {
    try {
      List<List<String>> list = new ArrayList<List<String>>();
      //loop through the milk table for a month
      for (Map.Entry mapElement : milkTableListMonth.entrySet()) {
        String key = (String) mapElement.getKey();
        log = log.concat("\n" + "its true");
        System.out.println("its true");
        //the list inside the arrayList
        List<String> subList = new ArrayList<String>();
        //fill the array list with the date, then the total milk weight, then the milk weight percentage
        subList.add(key);
        subList.add(getTotalMilkWeight(key, farmID) + "");
        subList.add(Double.toString(getPercentMilkWeight(key, farmID)).substring(0, 5) + "%");
        log = log.concat("\n" + subList);
        System.out.println(subList);
        //add this sublit to the 2D arraylist
        list.add(subList);
      }

      return list;
    } catch (Exception e) {
      throw new Exception();
    }


  }


  /**
   * generate the list for milkStats class, but with annual data
   * @param year
   * @return a 2D arraylist filled with data
   * @throws Exception
   */
  public List<List<String>> dataForAllFarmsAnnual(String year) throws Exception {
    List<List<String>> list = new ArrayList<List<String>>();
    //loop through a milkTable to gather data
    for (int i = 0; i < farmList.size(); i++) {
      List<String> subList = new ArrayList<String>();
      //add the date, the total milk weight, and the percent milk weight to the sublist
      subList.add(farmList.get(i));
      subList.add(getTotalMilkWeightYear(year, farmList.get(i)) + "");
      subList.add(
          Double.toString(getPercentMilkWeightYear(year, farmList.get(i))).substring(0, 5) + "%");
      log = log.concat("\n" + list);
      System.out.println(subList);
      //add the list to the overall 2D list
      list.add(subList);
    }
    return list;
  }

  /**
   * generate the list for MilkStats class, but for each month
   * @param year the given year
   * @param month the given month
   * @return a 2D list of data for the given month
   * @throws Exception
   */
  public List<List<String>> dataForAllFarmsMonthly(String year, String month) throws Exception {
    List<List<String>> list = new ArrayList<List<String>>();
    //loop through the milk table
    for (int i = 0; i < farmList.size(); i++) {
      List<String> subList = new ArrayList<String>();
      //add the date, total milk weight, and percent milk weight to the sublist 
      subList.add(farmList.get(i));
      subList.add(getTotalMilkWeight(year + "-" + month, farmList.get(i)) + "");
      subList.add(
          Double.toString(getPercentMilkWeight(year + "-" + month, farmList.get(i))).substring(0, 5)
              + "%");
      log = log.concat("\n" + list);
      System.out.println(subList);
      list.add(subList);
    }
    return list;
  }

  /**
   * Generates DateRangeReport
   * 
   * @param year     - Start Year
   * @param month    - Start Month
   * @param day      - Start Day
   * @param endYear  - End Day
   * @param endMonth - End Month
   * @param endDay   - End Day
   * @return
   */
  public List<List<String>> generateDateRangeReport(int year, int month, int day, int endYear,
      int endMonth, int endDay) throws Exception {

    // check if input is valid
    String sdate = Integer.toString(year) + "-" + Integer.toString(month);
    String edate = Integer.toString(endYear) + "-" + Integer.toString(endMonth);
    if (milkTableListMonth.containsKey(sdate) == false
        || milkTableListMonth.containsKey(edate) == false) { // throw exceptions if input is invalid
      throw new Exception();
    }
    if (day < 0 || day > 31 || endDay < 0 || endDay > 31) {
      throw new Exception();
    }
    //2D list for the data
    List<List<String>> dateRange = generateDateRange(year, month, day, endYear, endMonth, endDay);
    List<String> fList = new ArrayList<String>();
    List<List<String>> report = new ArrayList<List<String>>();
    double grandTotal = 0.0;

    //loop through the list and add data from dateRange into fList
    for (int i = 0; i < dateRange.size(); i++) {
      if (fList.contains(dateRange.get(i).get(1)) == false) {
        fList.add(dateRange.get(i).get(1));
      }
    }
    //compute the grand total by getting the data from fList
    for (int i = 0; i < fList.size(); i++) {
      grandTotal = grandTotal + getTotalMilkWeightRange(fList.get(i), dateRange);
    }

    //loop through fList
    for (int i = 0; i < fList.size(); i++) {
      List<String> list = new ArrayList<String>();
      int tWeight = getTotalMilkWeightRange(fList.get(i), dateRange);
      double pWeight = Math.round((tWeight / grandTotal) * 100);
      //add to the list, the date, the total weight, and the percentage
      list.add(fList.get(i));
      list.add(Integer.toString(tWeight));
      list.add(pWeight + "%");
      report.add(list);
    }

    return report;
  }

  /**
   * Gets totalMilkWeight from farmID in a given range of data
   * 
   * @param farmID
   * @param data
   * @return
   */
  public int getTotalMilkWeightRange(String farmID, List<List<String>> data) {
    int totalMilk = 0;
    // MilkTable mt = milkTableListYear.get(month);
    for (int i = 0; i < data.size(); i++) {
      if (data.get(i).get(1).equals(farmID)) {
        totalMilk = totalMilk + Integer.parseInt(data.get(i).get(2));
      }
    }

    return totalMilk;
  }

  /**
   * Generates list with required data in a given dateRange
   * 
   * @param year
   * @param month
   * @param day
   * @param endYear
   * @param endMonth
   * @param endDay
   * @return
   */
  public List<List<String>> generateDateRange(int year, int month, int day, int endYear,
      int endMonth, int endDay) {

    List<List<String>> data = new ArrayList<List<String>>();
    int ecount = endMonth;
    int dif = endDay;
    int dIndex = day;
    // iterate from start to end year
    for (int yDif = (endYear - year); yDif >= 0; yDif--) {
      // if year != endyear set endMonth to 12 for current iteration
      if (yDif != 0) {
        ecount = endMonth;
        endMonth = 12;
      } else {
        endMonth = ecount;
      }
      // iterate from start month to endMonth
      for (int mDif = (endMonth - month); mDif >= 0; mDif--) {
        // get the milktable for the current yeat/month
        MilkTable temp = milkTableListMonth
            .get(Integer.toString(endYear - yDif) + "-" + Integer.toString(endMonth - mDif));

        if (temp != null) {
          dIndex = temp.findIndex(day);
          // if month != endMonth set the endMonth to size of the list
          if (mDif != 0) {
            dif = temp.size() - 1;
          } else
            dif = temp.findLastIndex(endDay);

          // iterate through milkTable and add entries to data
          for (int index = dIndex; index <= dif; index++) {
            try {
              data.add(temp.get(index));
            } catch (KeyNotFoundException e) {
              // e.printStackTrace();
            }
          }
          // reset to first day
          day = 1;
        }
      }
      // reset to first month
      month = 1;
    }
    return data;

  }

  /**
   * Method that generates a popup with only the message parameter as a message
   * 
   * @param message ' the text to be displayed on the popup
   */
  public void createPopup(String message) {
    Stage popup = new Stage();
    Label msg = new Label(message);
    Scene scene = new Scene(msg, 500, 100);
    popup.setScene(scene);
    popup.show();
  }
  
  /**
   * Method that gets the log for this milk manager
   * 
   * @return the log
   */
  public String getLog() {
  	return log;
  }


  public static void main(String[] args) {
    MilkManager mm = new MilkManager();
    String file = "//Users/harshak/eclipse-workspace/milkWeigths/csv/small/2019-1.csv";
    String file2 = "//Users/harshak/eclipse-workspace/milkWeigths/csv/small/2019-2.csv";
    mm.milkParser(file);
    mm.milkParser(file2);
    mm.getYearMonth("2019-1");


    // mm.getTotalMilkWeight("2019-1", "Farm 0");
    mm.log = mm.log.concat("\n" + mm.generateDateRange(2019, 1, 1, 2019, 1, 10));
    System.out.println(mm.generateDateRange(2019, 1, 1, 2019, 1, 10));
    // System.out.println(mm.generateDateRangeReport(2019, 1, 1, 2019, 1, 10));

    // System.out.println(mm.generateDateRangeReport(2019, 1, 20, 2019, 2, 20));
  }


}
