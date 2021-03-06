package application;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * An instance of this class creates a MilkTable
 *
 */
public class MilkTable implements DataStructureADT {

  LinkedList<MilkData> milkList;
  private int count;

  private class MilkData {

    private String farm_id;
    private int milkWeight;
    private int month;
    private int year;
    private int day;

    /**
     * Instantiates a new MilkData with key and value
     * 
     * @param key   - String key
     * @param value - associated value
     */
    public MilkData(String farm_id, int milkWeight, String date) {
      this.farm_id = farm_id;
      this.milkWeight = milkWeight;

      String[] arr = date.split("-");
      this.month = Integer.parseInt(arr[1]);
      this.year = Integer.parseInt(arr[0]);
      this.day = Integer.parseInt(arr[2]);
    }

    /**
     * Returns the farmID
     * 
     * @return the farmID
     */
    private String getFarmId() {
      return farm_id;
    }

    /**
     * Returns the milkWeight
     * 
     * @return the milkWeight
     */
    private int getMilkWeight() {
      return milkWeight;
    }

    /**
     * Returns the Year
     * 
     * @return the year
     */
    private int getYear() {
      return year;
    }

    /**
     * Returns the Month
     * 
     * @return the month
     */
    private int getMonth() {
      return month;
    }

    /**
     * Returns the Day
     * 
     * @return the day
     */
    private int getDay() {
      return day;
    }


  }

  /**
   * Constructor for the MilkTable class
   */
  public MilkTable() {
    milkList = new LinkedList<MilkData>();
    count = 0;
  }



  /**
   * Insert the given data into milkList as a milkNode
   */
  @Override
  public void insert(List<String> data) throws IllegalNullKeyException {
    if (data == null) {
      throw new IllegalNullKeyException();
    }
    String date = data.get(0);
    String farm_id = data.get(1);
    int weight = Integer.parseInt(data.get(2));

    milkList.add(new MilkData(farm_id, weight, date));
    count++;
  }

  /**
   * Return a list of the date, farmID, and milkweight of the corresponding index
   */
  @Override
  public List<String> get(int index) throws KeyNotFoundException {
    // TODO Auto-generated method stub
    MilkData md = milkList.get(index);
    String date = md.getYear() + "-" + md.getMonth() + "-" + md.getDay();

    List<String> list = new ArrayList<String>();
    list.add(date);
    list.add(md.getFarmId());
    list.add(Integer.toString(md.getMilkWeight()));

    return list;


  }

  /**
   * Returns the FarmID at a certain index
   * 
   * @param index - index of the data
   * @return the FarmID at a certain index
   */
  public String getFarmId(int index) {
    return milkList.get(index).getFarmId();
  }


  public int size() {
    return milkList.size();
  }

  /**
   * Returns milkWeight of node with the given index
   * 
   * @param index
   * @return milkWeight
   */
  public int getMilkWeight(int index) {
    return milkList.get(index).getMilkWeight();
  }

  /**
   * Finds the first milkNode with given day
   * 
   * @param day
   * @return the index of the milkNode
   */
  public int findIndex(int day) {
    for (int i = 0; i < milkList.size(); i++) {
      if (milkList.get(i).getDay() == day) {
        return i;
      }
    }
    return 0;
  }

  /**
   * Finds the last milkNode with the given day
   * 
   * @param day
   * @return index of the milkNode
   */
  public int findLastIndex(int day) {
    int index = 0;
    boolean flag = true;
    for (int i = 0; i < milkList.size(); i++) {
      if (milkList.get(i).getDay() == day) {
        index = i;
      }
      if (flag == false && milkList.get(i).getDay() != day) {
        break;
      }
    }
    return index;
  }


}
