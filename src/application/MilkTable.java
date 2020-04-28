package application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * An instance of this class creates a MilkTable
 * 
 * @author harshak
 *
 * @param <K>
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
    
    private String getFarmId() {
      return farm_id;
    }
    
    private int getMilkWeight() {
      return milkWeight;
    }
    
    private int getYear() {
      return year;
    }
    
    private int getMonth() {
      return month;
    }
    
    private int getDay() {
      return day;
    }
    
    
  }
  
  public MilkTable() {
    milkList = new LinkedList<MilkData>();
    count = 0;
  }
  
  @Override
  public int numKeys() {
    // TODO Auto-generated method stub
    return count;
  }

  @Override
  public void insert(List<String> data) throws IllegalNullKeyException {
    if(data == null) {
      throw new IllegalNullKeyException();
    }   
    String date = data.get(0);
    String farm_id = data.get(1);
    int weight = Integer.parseInt(data.get(2));
    
    milkList.add(new MilkData(farm_id, weight, date));
    count++;
  }

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
  
  @Override
  public List<String> getDataWithID(String farmID){
    return null;
  }

  @Override
  public boolean remove(String node) throws IllegalNullKeyException {
    // TODO Auto-generated method stub
    if(node == null) {
      throw new IllegalNullKeyException();
    }
    
    return false;
  }
}

  