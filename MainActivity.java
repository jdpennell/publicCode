package com.app.example;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import com.app.example.FirstFragment;

/**
 * Main (and only) calling activity, designed to manage and swap fragments
 *
 * @author "Jessica Pennell" &lt;jessicadharmapennell@gmail.com&gt;
 */
public class MainActivity
    extends AppCompatActivity
    implements
        DialogInterface.OnClickListener
      , FirstFragment.OnFirstFragmentInteractionListener
{
    /** crappy compile time switch */
    protected static boolean runUnitTests = true;

    /** @return this app's main activity*/
    public static MainActivity getMainActivity()
    {
        return main == null ? null : main.get();
    }
    private static WeakReference<MainActivity> main = null;

    /** indices for requestpermission */
    static final int PERMISSION_REASON = 0;
    static final int PERMISSION_PERMISSION = 1;

    /**
     * Make the passed in fragment the visible fragment
     *
     * @param fragment passed in fragment
     */
    public void swapFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getFragmentManager()
          .beginTransaction();
        fragment.setArguments(getIntent().getExtras());

        if (firstSwap)
        {
            transaction.replace(R.id.body_fragment, fragment);
            transaction.addToBackStack(null);
        }
        else
        {
            transaction.add(R.id.body_fragment, fragment);
            firstSwap = false;
        }
        transaction.commit();
    }
    private boolean firstSwap = true; // TODO: this needs to be replaced with a backstack check

    /**
     * Request a permission considered dangerous
     *
     * @param permission permission to request
     */
    public static void requestPermission(String permission, String reason)
    {
        // base case
        if (

                // we need a main activity
                (null == getMainActivity())

                // we already have the permission we need
                || (
                           ContextCompat.checkSelfPermission (
                                   getMainActivity(), permission
                                   )
                        == PackageManager.PERMISSION_GRANTED
                        )
                )
        {
            return;
        }

        // we need to show a dialog first
        if (
                ActivityCompat.shouldShowRequestPermissionRationale (
                        getMainActivity(), permission
                        )
                )
        {
            new GetPermissionsThread().execute(reason, permission);
            return;
        }

        // we're ready to request the permission now
        ActivityCompat.requestPermissions (
                getMainActivity(), new String[]{permission},
                (++PERMCONSTANT) % 0x100
                );
    }
    private static int PERMCONSTANT = 0x00;

    /**
     * Initialize the app here
     *
     * @URL https://developer.android.com/reference/android/app/Activity.html#onCreate(android.os.Bundle)
     * @param savedInstanceState Bundle: If the activity is being
     *                           re-initialized after previously being shut down
     *                           then this Bundle contains the data it most
     *                           recently supplied in
     *                           onSaveInstanceState(Bundle)
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // set the main activity reference
        main = new WeakReference<>(this);

        // swap in the first fragment
        swapFragment(new FirstFragment());
    }

    /**
     * Main entry point into the app
     *
     * @URL https://developer.android.com/reference/android/app/Activity.html#onWindowFocusChanged(boolean)
     * @param hasFocus Whether the window of this activity has focus.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (! hasFocus)
        {
            return;
        }

        if (runUnitTests)
        {
            //new MainActivityTest().runAllTests();
        }
    }

    /**
     * @URL https://developer.android.com/reference/android/app/Activity.html#onCreateOptionsMenu(android.view.Menu)
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return
     *          false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        return true;
    }

    /**
     * @URL https://developer.android.com/reference/android/app/Activity.html#onOptionsItemSelected(android.view.MenuItem)
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @URL https://developer.android.com/training/basics/fragments/communicating.html
     *
     * @param uri placeholder for a user specified argument
     */
    @Override
    public void onFirstFragmentInteraction(Uri uri)
    {

    }

    /**
     * @URL https://developer.android.com/reference/android/content/DialogInterface.OnClickListener.html
     *
     * @param rawDialog The dialog that received the click
     * @param which  The button that was clicked (e.g. BUTTON1) or the position of the item clicked
     */
    @Override
    public void onClick(DialogInterface rawDialog, int which)
    {
        String[] permission = GetPermissionsThread.pop();

        if (null == permission)
        {
            return;
        }

        requestPermission(permission[PERMISSION_PERMISSION], permission[PERMISSION_REASON]);
    }

    /** Helper for requestPermissions */
    private static class GetPermissionsThread extends AsyncTask<String, Integer, Integer>
    {
        /** @return most recent passed in permission, or null */
        public static String[] pop()
        {
            if (null == stack)
            {
                stack = new SynchronousQueue<>();
            }
            return stack.poll();
        }

        /** @param permission new permission to add to the stack */
        private static void push(String[] permission)
        {
            if (null == stack)
            {
                stack = new SynchronousQueue<>();
            }
            stack.add(permission);
        }

        /** contains recently requested permissions */
        private static Queue<String[]> stack = null;

        /**
         * Pop up an informative dialog explaining why we need the permission
         *
         * @param reason reason we need the permission in question
         * @return 0 (always)
         */
        @Override
        protected Integer doInBackground(String... reason)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getMainActivity());
            DialogInterface.OnClickListener doNothing =
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                };

            // set up dialog which allows user to OK permission
            dialog.setTitle(reason[PERMISSION_REASON]);
            dialog.setMessage(reason[PERMISSION_PERMISSION]);
            dialog.setPositiveButton("OK", getMainActivity());
            dialog.setNegativeButton("Cancel", doNothing);

            // make reason and permission set available to handler
            push(reason);

            return 0;
        }
    }
}
