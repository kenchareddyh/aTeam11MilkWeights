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
  
  private int maxYear = 0;
  private int minYear = 3000;
  
  public int getMaxYear() {
    return maxYear;
  }
  public int getMinYear() {
    return minYear;
  }

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
          // split table into rows and add each row to records
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


    System.out.println("records:" + records.get(1).get(0));


    // create two keys, year/month and month
    String[] arr = records.get(1).get(0).split("-");
    monthYearDate = arr[0] + "-" + arr[1];
    yearDate = arr[0];
    farmID = records.get(1).get(1);
    
    if(Integer.parseInt(yearDate) > maxYear)
      maxYear = Integer.parseInt(yearDate);
    if(Integer.parseInt(yearDate) < minYear) {
      minYear = Integer.parseInt(yearDate);
    }

    System.out.println(maxYear);
    System.out.println(minYear);
    System.out.println(monthYearDate);
    System.out.println(yearDate);

    // store the key and the milkTable as its value in two hashmaps
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
  public int getTotalMilkWeight(String value, String farmID) throws Exception {
    try {
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

    } catch (Exception e) {
      throw new Exception();
    }
  }


  // my method
  // returns total weight for a certain year
  public int getTotalMilkWeightYear(String value, String farmID) throws Exception {
    try {
      int totalMilk = 0;
      MilkTable mt = milkTableListYear.get(value);
      for (int i = 0; i < mt.size(); i++) {

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
      double total = (double) getTotalMilkWeight(value, farmID);
      double grandTotal = 0.0;
      MilkTable mt = milkTableListMonth.get(value);

      if (mt != null) {
        for (int i = 0; i < mt.size(); i++) {
          grandTotal = grandTotal + mt.getMilkWeight(i);
        }
      }
      return (total / grandTotal) * 100;
    } catch (Exception e) {
      throw new Exception();
    }

  }

  // my method
  // equivalent for year
  public double getPercentMilkWeightYear(String value, String farmID) throws Exception {
    try {
      double total = (double) getTotalMilkWeightYear(value, farmID);
      double grandTotal = 0.0;
      MilkTable mt = milkTableListYear.get(value);
      for (int i = 0; i < mt.size(); i++) {
        grandTotal = grandTotal + mt.getMilkWeight(i);
      }

      return (total / grandTotal) * 100;
    } catch (Exception e) {
      throw new Exception();
    }

  }

  // david
  public List<List<String>> dataForAllMonths(String year, String farmID) throws Exception {
    try {
      List<List<String>> list = new ArrayList<List<String>>();
      for (Map.Entry mapElement : milkTableListMonth.entrySet()) {
        String key = (String) mapElement.getKey();
        System.out.println("its true");
        List<String> subList = new ArrayList<String>();
        subList.add(key);
        subList.add(getTotalMilkWeight(key, farmID) + "");
        subList.add(Double.toString(getPercentMilkWeight(key, farmID)).substring(0, 5) + "%");
        System.out.println(subList);
        list.add(subList);
      }

      return list;
    } catch (Exception e) {
      throw new Exception();
    }


  }
  // my method
  // all farms

  public List<List<String>> dataForAllFarmsAnnual(String year) throws Exception {
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
  public List<List<String>> dataForAllFarmsMonthly(String year, String month) throws Exception {
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
      int endMonth, int endDay) {
    List<List<String>> dateRange = generateDateRange(year, month, day, endYear, endMonth, endDay);
    List<String> fList = new ArrayList<String>();
    List<List<String>> report = new ArrayList<List<String>>();
    double grandTotal = 0.0;

    for (int i = 0; i < dateRange.size(); i++) {
      if (fList.contains(dateRange.get(i).get(1)) == false) {
        fList.add(dateRange.get(i).get(1));
      }
    }
    for (int i = 0; i < fList.size(); i++) {
      grandTotal = grandTotal + getTotalMilkWeightRange(fList.get(i), dateRange);
    }

    for (int i = 0; i < fList.size(); i++) {
      List<String> list = new ArrayList<String>();
      int tWeight = getTotalMilkWeightRange(fList.get(i), dateRange);
      double pWeight = Math.round((tWeight / grandTotal) * 100);

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


  public static void main(String[] args) {
    MilkManager mm = new MilkManager();
    String file = "//Users/harshak/eclipse-workspace/milkWeigths/csv/small/2019-1.csv";
    String file2 = "//Users/harshak/eclipse-workspace/milkWeigths/csv/small/2019-2.csv";
    mm.milkParser(file);
    mm.milkParser(file2);
    mm.getYearMonth("2019-1");


    // mm.getTotalMilkWeight("2019-1", "Farm 0");
    System.out.println(mm.generateDateRange(2019, 1, 1, 2019, 1, 10));
    System.out.println(mm.generateDateRangeReport(2019, 1, 1, 2019, 1, 10));

    System.out.println(mm.generateDateRangeReport(2019, 1, 20, 2019, 2, 20));
  }


}
