package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MilkManager {

  // Map that contains milkTable for corresponding year/month
  private Map<String, MilkTable> milkTableListMonth;

  private MilkTable milkTable;

  // addition
  private List<String> farmList;


  // Map that contains milkTable for corresponding year
  private Map<String, MilkTable> milkTableListYear;

  /**
   * Constructor to initialize map
   */
  public MilkManager() {

    milkTableListMonth = new HashMap<String, MilkTable>();
    milkTableListYear = new HashMap<String, MilkTable>();
    // addition
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

    if (milkTableListMonth.containsKey(value)) {
      MilkTable mt = milkTableListMonth.get(value);
      for (int i = 0; i < mt.numKeys(); i++) {
        try {
          list.add(mt.get(i));
        } catch (KeyNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    System.out.println(list);
    return list;

  }



  /**
   * Parse through given csv file
   * 
   * @param file
   */
  public void milkParser(String file) {
    String monthYearDate;
    String yearDate;
    String farmID;

    milkTable = new MilkTable();


    // parse through file a create a list of each row in data table
    List<List<String>> records = new ArrayList<>();
    try {
      BufferedReader csvReader = new BufferedReader(new FileReader(file));
      String line;
      try {
        while ((line = csvReader.readLine()) != null) {
          String[] values = line.split(",");
          records.add(Arrays.asList(values));
        }
        csvReader.close();
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
        milkTable.insert(records.get(i));
        if (!farmList.contains(records.get(i).get(1))) {
          farmList.add(records.get(i).get(1));
        }

      } catch (IllegalNullKeyException e) {
        e.printStackTrace();
      }
    }


    System.out.println("records:" + records.get(1).get(0));


    String[] arr = records.get(1).get(0).split("-");
    monthYearDate = arr[0] + "-" + arr[1];
    yearDate = arr[0];
    farmID = records.get(1).get(1);


    System.out.println(monthYearDate);
    System.out.println(yearDate);

    milkTableListMonth.put(monthYearDate, milkTable);
    milkTableListYear.put(yearDate, milkTable);


  }

  /**
   * get the total milk weight of a certain farm in a certain month
   * 
   * @param value  the month
   * @param farmID the farm
   * @return total milk weight of the farm in a month
   */
  public int getTotalMilkWeight(String value, String farmID) {
    int totalMilk = 0;
    MilkTable mt = milkTableListMonth.get(value);
    if (mt != null) {
      for (int i = 0; i < mt.size(); i++) {

        if (mt.getFarmId(i).equals(farmID)) {

          totalMilk = totalMilk + mt.getMilkWeight(i);
        }
      }
    }


    return totalMilk;
  }

  // my method
  // returns total weight for a certain year
  public int getTotalMilkWeightYear(String value, String farmID) {
    int totalMilk = 0;
    MilkTable mt = milkTableListYear.get(value);
    for (int i = 0; i < mt.size(); i++) {

      if (mt.getFarmId(i).equals(farmID)) {

        totalMilk = totalMilk + mt.getMilkWeight(i);
      }
    }

    return totalMilk;
  }


  /**
   * gets the percent of the total milk weight that a certain farm produced
   * 
   * @param value  the month
   * @param farmID the farm id
   * @return the percentage
   */
  public double getPercentMilkWeight(String value, String farmID) {
    double total = (double) getTotalMilkWeight(value, farmID);
    double grandTotal = 0.0;
    MilkTable mt = milkTableListMonth.get(value);

    if (mt != null) {
      for (int i = 0; i < mt.size(); i++) {
        grandTotal = grandTotal + mt.getMilkWeight(i);
      }
    }
    return (total / grandTotal) * 100;
  }

  // my method
  // equivalent for year
  public double getPercentMilkWeightYear(String value, String farmID) {
    double total = (double) getTotalMilkWeightYear(value, farmID);
    double grandTotal = 0.0;
    MilkTable mt = milkTableListYear.get(value);
    for (int i = 0; i < mt.size(); i++) {
      grandTotal = grandTotal + mt.getMilkWeight(i);
    }

    return (total / grandTotal) * 100;
  }

  // david
  public List<List<String>> dataForAllMonths(String year, String farmID) {
    List<List<String>> list = new ArrayList<List<String>>();
    for (Map.Entry mapElement : milkTableListMonth.entrySet()) {
      String key = (String) mapElement.getKey();
      System.out.println("its true");
      List<String> subList = new ArrayList<String>();
      subList.add(key);
      subList.add(getTotalMilkWeight(key, farmID) + "");
      subList
          .add(Double.toString(getPercentMilkWeight(key, farmID)).substring(0, 5)
              + "%");
      System.out.println(subList);
      list.add(subList);
    }

  return list;

  }
  // my method
  // all farms

  public List<List<String>> dataForAllFarmsAnnual(String year) {
    List<List<String>> list = new ArrayList<List<String>>();
    for (int i = 0; i < farmList.size(); i++) {
      List<String> subList = new ArrayList<String>();

      subList.add(farmList.get(i));
      subList.add(getTotalMilkWeightYear(year, farmList.get(i)) + "");
      subList.add(
          Double.toString(getPercentMilkWeightYear(year, farmList.get(i))).substring(0, 5) + "%");
      System.out.println(subList);
      list.add(subList);
    }
    return list;
  }

  // my method 2
  public List<List<String>> dataForAllFarmsMonthly(String year, String month) {
    List<List<String>> list = new ArrayList<List<String>>();
    for (int i = 0; i < farmList.size(); i++) {
      List<String> subList = new ArrayList<String>();

      subList.add(farmList.get(i));
      subList.add(getTotalMilkWeight(year + "-" + month, farmList.get(i)) + "");
      subList.add(
          Double.toString(getPercentMilkWeight(year + "-" + month, farmList.get(i))).substring(0, 5)
              + "%");
      System.out.println(subList);
      list.add(subList);
    }
    return list;
  }


  public static void main(String[] args) {
    MilkManager mm = new MilkManager();
    String file = "//Users/harshak/eclipse-workspace/milkWeigths/csv/small/2019-1.csv";
    mm.milkParser(file);
    mm.getYearMonth("2019-1");


    mm.getTotalMilkWeight("2019-1", "Farm 0");
  }


}
