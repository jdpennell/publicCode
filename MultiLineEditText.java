package com.example.app;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * Extends the multi-line EditText to allow IME actions
 *
 * @author Jessica Pennell &lt;jessicadharmapennell@gmail.com&gt;
 */
public class MultiLineEditText extends EditText
{
  /** constructors */
  public MultiLineEditText(Context context) { super(context); }

  /**
   * Create a new InputConnection for an InputMethod to interact with the
   * view. The default implementation returns null, since it doesn't support
   * input methods. You can override this to implement such support. This is
   * only needed for views that take focus and text input.
   *
   * When implementing this, you probably also want to implement onCheckIsTextEditor()
   * to indicate you will return a non-null InputConnection.
   *
   * Also, take good care to fill in the EditorInfo object correctly and
   * in its entirety, so that the connected IME can rely on its values. For
   * example, initialSelStart and initialSelEnd members must be filled in with
   * the correct cursor position for IMEs to work correctly with your application.
   *
   * @param outAttrs Fill in with attribute information about the connection.
   * @return new InputConnection object
   */
  @Override
  public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    // grab our feature locked crappy version
    InputConnection connection = super.onCreateInputConnection(outAttrs);

    // I don't think you're happy enough
    // I'LL TEACH YOU TO BE HAPPY!
    outAttrs.imeOptions |= EditorInfo.IME_ACTION_DONE;
    outAttrs.imeOptions &= ~(EditorInfo.IME_FLAG_NO_ENTER_ACTION);

    // That's more like it
    return connection;
  }
}
