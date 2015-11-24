package com.example.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.example.app.ImageManipulator;
import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.ServerIO;
import com.example.app.TwoCellListAdapter;
import com.example.app.ViewHolder;
import com.example.app.SampleGSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// IMPORTANT : This is untested, uncompiled code.
// A version of this class has been tested, but several project specific identifiers
// were changed afterward and before putting on github

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 *
 * @author Jessica Pennell &lt;jessicadharmapennell@gmail.com&gt;
 */
public class MyExampleFragment
    extends
      Fragment
    implements
      AbsListView.OnItemClickListener
      ,ViewTreeObserver.OnPreDrawListener
      ,ServerIO.ServerIOListener
{
  /** Main listener for events */
  private OnFragmentInteractionListener mListener;

  /** The fragment's ListView/GridView. */
  private AbsListView mListView;

  /** Adapter which will be used to populate the ListView with Views */
  private static MyExampleAdapter mAdapter;

  /** Dimensions for a listview */
  private ViewGroup.LayoutParams mListRowLayoutParams;

  /** mAdapter's data set */
  List<Map<String, String>> dataBacker = new ArrayList<>();

  /**
   * Base constructor
   */
  public MyExampleFragment()
  {
    Bundle args = new Bundle();

    // set arguments
    setArguments(args);
  }

  /** http://developer.android.com/reference/android/app/Fragment.html#onCreateView%28android.view.LayoutInflater,%20android.view.ViewGroup,%20android.os.Bundle%29 */
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container
      , Bundle savedInstanceState
      )
  {
    View view = inflater.inflate(R.layout.fragment_example, container, false);

    // create and populate the course list adapter
    String from[] = {"tcl_item1", "tcl_item2"};
    int    to[]   = {R.id.tcl_item1, R.id.tcl_item2};
    dataBacker = new ArrayList<>();
    mAdapter = new MyExampleAdapter(
        getActivity(), dataBacker, R.layout.two_column_list, from, to
    );
    mListView = (AbsListView) view.findViewById(R.id.example_list_view);
    mListView.setAdapter(mAdapter);
    mListView.setOnItemClickListener(this);
    ServerIO.getSampleData(this);

    // set our event handlers
    mListener = MainActivity.mainActivity;
    mListView.getViewTreeObserver().addOnPreDrawListener(this);

    // finish styling
    MainActivity.setTitle("My Title");

    // we're done
    return view;
  }

  /**
   * Sample exposed (to xml layout) method
   *
   * @param position selected item position
   */
  public static void onPress(int position)
  {
    if (! mAdapter.isPressed(position))
    {
      mAdapter.onPress(position);
    }
  }

  /**
   * Notify the active callbacks interface
   * (the activity, if the fragment is attached to one)
   * that an item has been selected.
   *
   * http://developer.android.com/reference/android/widget/AdapterView.OnItemClickListener.html
   **/
  @Override
  public void onItemClick (
      AdapterView<?> parent, View view, int position, long id
      )
  {
    Bundle args = new Bundle();

    args.putLong("id", id);
    args.putInt("position", position);
    view.setTag(args);

    mListener.onExampleFragmentInteraction(view);
  }

  /**
   * First event where measurements are available
   *
   * @return true (always)
   */
  @Override
  public boolean onPreDraw()
  {
    View cell =
          null == mListView
        ? null
        : mListView.findViewById(R.id.tcl_item1);

    // only move forward once, and only after initial populate
    if ( (null == mListRowLayoutParams) && (null != cell) )
    {
      // retrieve a local copy of the layout params we can modify safely
      mListRowLayoutParams = cell.getLayoutParams();
      mListRowLayoutParams = new ViewGroup.LayoutParams(mListRowLayoutParams);

      // modify the local copy
      mListRowLayoutParams.height = cell.getHeight();
      mListRowLayoutParams.width = cell.getWidth();
    }

    return true;
  }

  /** @return the URI we retrieve the listview data from */
  @Override
  public String uri()
  {
    return String.format("%s/%s", ServerIO.SERVER, "function");
  }

  /**
   * Populate the listview when we get data back from the server
   *
   * @param result data retrieved from the ReST server
   */
  @Override
  public void onRetrieved(String result)
  {
    Map<String, String> row;

    for (Map map : dataBacker)
    {
      dataBacker.remove(map);
    }

    for (
        SampleGSONObject course :
        new Gson().fromJson(result, SampleGSONObject[].class)
        )
    {
      row = new HashMap<>();
      row.put("tcl_item1", course.field);
      row.put("tcl_item2", "Example label");
      dataBacker.add(row);
    }

    ViewHolder.clear();
    ViewHolder.populateData(dataBacker);
    mAdapter.notifyDataSetChanged();
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p/>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener
  {
    /**
     * Event handler for when a screen element is clicked
     *
     * @param view clicked element
     */
    void onExampleFragmentInteraction(View view);
  }

  /**
   * Two column list adapter.
   *
   * First column : course name
   * Second column : button. Changes to reflect state.
   */
  protected class MyExampleAdapter extends TwoCellListAdapter
  {
    /** Call TwoCellListAdapter's constructor */
    public MyExampleAdapter (
      Context context, List<? extends Map<String, ?>> data
      , int resource, String[] from, int[] to
      )
    {
      super(context, data, resource, from, to);
    }

    /**
     * Determine whether a selected button was pressed
     *
     * @param position given class
     * @return whether the a selected button was pressed
     */
    public boolean isPressed(int position)
    {
      ViewHolder.get(position);
      return
             null != ViewHolder.data
          && ViewHolder.data.containsKey("state")
          && ViewHolder.data.get("state").matches("pressed");
    }

    /**
     * Toggle the state of a selected button, between pressed and not pressed
     *
     * @param position selected button
     */
    public void onPress(int position)
    {
      ViewHolder.get(position);
      ViewHolder.data.put (
          "state",
          isPressed(position) ? "not pressed" : "pressed"
          );
      notifyDataSetChanged();
    }

    /** populate, configure, and style row cells */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void style()
    {
      // not pressed
      if (
             (! ViewHolder.data.containsKey("state"))
          || (ViewHolder.data.get("state").matches("not pressed"))
          )
      {
        ViewHolder.cell1.setText(ViewHolder.data.get("tcl_item1"));
        ViewHolder.cell1.setTextColor(ColorInflater.Black());
        ViewHolder.cell2.setText(ViewHolder.data.get("tcl_item2"));
        ViewHolder.cell2.setTextColor(ColorInflater.PacificBlue());
      }

      // pressed
      else
      {
        // white font once pressed
        ViewHolder.cell1.setText(ViewHolder.data.get("tcl_item1"));
        ViewHolder.cell1.setTextColor(ColorInflater.White());

        // create and scale a background icon
        Bitmap icon = ImageManipulator.inflate(R.drawable.sample_picture);
        icon = ImageManipulator.scaleToSpec(icon, mListRowLayoutParams);

        // apply the background
        ViewHolder.cell2.setText(R.string.space);
        ViewHolder.cell2.setBackground(ImageManipulator.toDrawable(icon));
        ViewHolder.row.setBackgroundColor(ColorInflater.PineGreen());
      }
    }
  }
