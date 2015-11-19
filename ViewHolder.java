package com.example.app;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.MainActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Needed to work with listviews, contains every instantiated view in a row
 *
 * @author Jessica Pennell &lt;jessicadharmapennell@gmail.com&gt;
 */
public class ViewHolder
{
  /** visible references to each view */
  public static LinearLayout row;
  public static TextView     cell1, cell2;
  public static Map<String, String> data;

  /** allows switching between two column and two row list */
  public static int resource     = R.layout.two_column_list;
  public static int resourceKey1 = R.id.tcl_item1;
  public static int resourceKey2 = R.id.tcl_item2;

  /** actual references to each view */
  private static Map<Integer, TextView> cell1List, cell2List;
  private static Map<Integer, LinearLayout> rowList;

  /** reference to the backing data */
  private static Map<Integer, Map<String, String>> dataList;

  /** Switch to two row list */
  public void useTwoRowList()
  {
    resource = R.layout.two_row_list;
    resourceKey1 = R.id.trl_item1;
    resourceKey2 = R.id.trl_item2;
  }

  /** Switch to two column list */
  public void useTwoColumnList()
  {
    resource = R.layout.two_column_list;
    resourceKey1 = R.id.tcl_item1;
    resourceKey2 = R.id.tcl_item2;
  }

  /**
   * Set the backing data to be associated with each row
   *
   * @param data backing data
   */
  public static void populateData(List<? extends Map<String, ?>> data)
  {
    dataList = new HashMap<>();
    int i = 0;
    Map<String, String> outRow;

    for (Map inRow : data)
    {
      outRow = new HashMap<>();
      for (Object key : inRow.keySet())
      {
        outRow.put(key.toString(), inRow.get(key).toString());
      }
      dataList.put(i++, outRow);
    }
  }

  /**
   * Add a row to the View Holder.
   *
   * Note: this only adds views,
   * data must be added separately and all at once
   *
   * @param position row number to add
   */
  public static void addVisualRow(int position)
  {
    LinearLayout row;

    if (null == cell1List)
    {
      cell1List = new HashMap<>();
    }
    if (null == cell2List)
    {
      cell2List = new HashMap<>();
    }
    if (null == rowList)
    {
      rowList = new HashMap<>();
    }
    row = (LinearLayout) MainActivity.mainActivity
        .getLayoutInflater()
        .inflate(resource, null);
    cell1 = (TextView) row.findViewById(resourceKey1);
    cell2 = (TextView) row.findViewById(resourceKey2);
    rowList.put(position, row);
    cell1List.put(position, cell1);
    cell2List.put(position, cell2);
    get(position);
  }

  /**
   * Exposes the cells for the selected row
   *
   * Note: populateData(data containing row data) and addVisualRow(row)
   * must both be called for this to be successful,
   * otherwise all public members are nullified
   *
   * @param position selected row
   */
  public static void get(int position)
  {
    if (
           (null == cell1List) || (null == cell2List)
        || (null == rowList)   || (null == dataList)
        )
    {
      return;
    }
    cell1 = cell1List.get(position);
    cell2 = cell2List.get(position);
    row = rowList.get(position);
    data = dataList.get(position);
    if (
           (null == cell1) || (null == cell2)
        || (null == row)   || (null == data)
        )
    {
      cell1 = cell2 = null;
      row = null;
      data = null;
    }
  }
}
