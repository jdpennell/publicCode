package com.example.app;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.example.app.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Generic adapter to be paired with custom ViewHolder and two cell list layouts
 *
 * @author Jessica Pennell &lt;jessicadharmapennell@gmail.com&gt;
 */
public abstract class TwoCellListAdapter
  extends SimpleAdapter
{
  /**
   * Constructor
   *
   * @param context  The context where the View associated with this
   *                 SimpleAdapter is running
   * @param data     A List of Maps.
   *                 Each entry in the List corresponds to one row in the
   *                 list. The Maps contain the data for each row,
   *                 and should include all the entries specified in "from"
   * @param resource The R.layout.resource id of the view to inflate
   * @param from     A list of column names that will be added to the Map
   *                 associated with each item.
   * @param to       The views that should display column in the "from"
   *                 parameter. These should all be TextViews.
   *                 The first N views in this list are given the values of
   *                 the first N columns
   */
  public TwoCellListAdapter(
      Context context, List<? extends Map<String, ?>> data
      , int resource, String[] from, int[] to
      )
  {
    super(context, data, resource, from, to);
    ViewHolder.populateData(data);
  }

  /**
   * Get a View that displays the data at the specified position in the
   * data set. You can either create a View manually or inflate it from an
   * XML layout file. When the View is inflated, the parent
   * View (GridView, ListView...) will apply default layout parameters unless
   * you use inflate(int, android.view.ViewGroup, boolean) to specify a root
   * view and to prevent attachment to the root.
   *
   * @param position The position of the item within the adapter's data set
   *                 of the item whose view we want.
   * @param convertView The old view to reuse, if possible.
   *                    Note: You should check that this view is non-null
   *                    and of an appropriate type before using.
   *                    If it is not possible to convert this view to
   *                    display the correct data, this method can create a
   *                    new view. Heterogeneous lists can specify their
   *                    number of view types, so that this View is always of
   *                    the right type
   *                    (see getViewTypeCount() and getItemViewType(int)).
   * @param parent The parent that this view will eventually be attached to
   * @return A View corresponding to the data at the specified position.
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    // create new views as needed, recycle otherwise
    ViewHolder.get(position);
    if ( (null == ViewHolder.cell1) )
    {
      ViewHolder.addVisualRow(position);
    }

    // failed create / recycle nullifies all fields
    if ( (null == ViewHolder.data) )
    {
      throw new NullPointerException("missing call to ViewHolder.populateData");
    }

    style();

    // we're done
    return ViewHolder.row;
  }

  /** populate, configure, and style row cells */
  public abstract void style();
}
