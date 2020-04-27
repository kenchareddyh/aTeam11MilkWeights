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
  
  //Map that contains milkTable for corresponding year
  private Map<String, MilkTable> milkTableListYear;

  /**
   * Constructor to initialize map
   */
  public MilkManager() {
    milkTableListMonth = new HashMap<String, MilkTable>();
    milkTableListYear = new HashMap<String, MilkTable>();
  }
  
  /**
   * Returns corresponding milkTable for month/year
   * @param value - month/year
   * @return milkTable
   */
  public List<List<String>> getYearMonth(String value){
    List<List<String>> list = new ArrayList<List<String>>();
    
    if(milkTableListMonth.containsKey(value)) {
      MilkTable mt = milkTableListMonth.get(value);
      for(int i = 0; i < mt.numKeys(); i++) {
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

    MilkTable mt = new MilkTable();


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
        mt.insert(records.get(i));

      } catch (IllegalNullKeyException e) {
        e.printStackTrace();
      }
    }


    System.out.println(records.get(1).get(0));
    String[] arr = records.get(1).get(0).split("-");
    monthYearDate = arr[0] + "-" + arr[1];
    yearDate = arr[0];

    System.out.println(monthYearDate);
    System.out.println(yearDate);

    milkTableListMonth.put(monthYearDate, mt);
    milkTableListYear.put(yearDate, mt);

  }

  public static void main(String[] args) {
    MilkManager mm = new MilkManager();
    String file = "//Users/harshak/eclipse-workspace/milkWeigths/csv/small/2019-1.csv";
    mm.milkParser(file);
    mm.getYearMonth("2019-1");
  }


}
