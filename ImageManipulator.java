package com.example.app;
import com.example.app.MainActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

/**
 * Contains several functions for manipulating images in ImageViews
 *
 * @author Jessica Pennell &lt;jessicadharmapennell@gmail.com&gt;
<a href="http://www.wtfpl.net/">
  <img
    src="http://www.wtfpl.net/wp-content/uploads/2012/12/wtfpl-badge-4.png"
    width="80"
    height="15"
    alt="WTFPL"
    />
</a>
 */
public class ImageManipulator
{
    /**
     * Return a Bitmap inflated from a resource
     *
     * @param resourceID resource ID for the resource to inflate
     * @return inflated Bitmap
     */
    public static Bitmap inflate(int resourceID)
    {
        Bitmap out = null;
        Resources resources = getResources();

        // inflate from main activity's resources
        if (null != resources)
        {
            out = BitmapFactory.decodeResource(resources, resourceID);
        }

        return out;
    }

    /**
     * Creates a new Drawable surface using the passed in bitmap
     *
     * @param bitmap Bitmap which will be contained in the new Drawable surface
     * @return new Drawable surface
     */
    public static Drawable toDrawable(@Nullable Bitmap bitmap)
    {
        Resources resources = getResources();
        Drawable out = null;

        if ( (null != resources) && (null != bitmap) )
        {
            out = new BitmapDrawable(resources, bitmap);
        }

        return out;
    }

    /**
     * Returns a copy of source that has been cropped into a circle
     *
     * @param source source Bitmap to crop
     * @return cropped copy
     */
    public static Bitmap circleCrop(@Nullable Bitmap source)
    {
        Bitmap copy = null;
        int mindim = 0;
        Bitmap.Config storageScheme = Bitmap.Config.ARGB_8888;
        boolean doBilinearInterpolation = true, isMutable = true;

        // create a copy scaled into a square on the smaller of width or height
        if (null != source)
        {
            mindim =
                  source.getWidth() > source.getHeight()
                ? source.getHeight()
                : source.getWidth()
                ;
            if (mindim < 1)
            {
                return null;
            }
            copy = Bitmap.createScaledBitmap (
                source, mindim, mindim, doBilinearInterpolation
                );
        }

        // ensure we have a copy and not the original image
        if (null != copy)
        {
            copy = copy.copy(storageScheme, isMutable);
        }

        // if we fillet to half the square's width, we make a circle
        return filletCorners(copy, mindim / 2);
    }

    /**
     * Returns a copy of source that has been cropped so the corners are
     * filleted
     *
     * @param source source Bitmap to crop
     * @param radius how many pixels in to fillet the corners
     * @return cropped copy
     * @URL http://www.curious-creature.com/2012/12/11/android-recipe-1-image-with-rounded-corners/
     */
    public static Bitmap filletCorners(@Nullable Bitmap source, float radius)
    {
        Bitmap.Config storageScheme = Bitmap.Config.ARGB_8888;
        Bitmap copy = null;
        BitmapShader shader = null;
        Paint paint = new Paint();
        RectF rect = null;
        Canvas canvas = null;

        // make our blank canvas
        if (null != source)
        {
            copy = Bitmap.createBitmap (
                source.getWidth(), source.getHeight(), storageScheme
                );
        }

        // paint from the source onto a new blank canvas,
        // then return the newly created image
        if (null != copy)
        {
            // set up our shader and boundaries to color from the source
            shader = new BitmapShader (
                source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
                );
            paint.setAntiAlias(true);
            paint.setShader(shader);

            // our boundaries should match the source image's boundaries
            rect = new RectF (
                0.0f, 0.0f,
                (float) source.getWidth(), (float) source.getHeight()
                );

            // fill in our canvas, minus the filleted areas
            canvas = new Canvas(copy);
            canvas.drawRoundRect(rect, radius, radius, paint);
        }

        return copy;
    }

    /**
     * Returns a copy of source that has been scaled to the phone or tablet's
     * screen
     *
     * @param source source image to scale
     * @return scaled copy
     */
    public static Bitmap scaleToScreen(@Nullable Bitmap source)
    {
        Bitmap out = null;
        ViewGroup.LayoutParams params = getLayoutParams();
        boolean doBilinearInterpolation = true, isMutable = true;
        Bitmap.Config storageScheme = Bitmap.Config.ARGB_8888;

        // scale
        if ( (null != params) && (null != source) )
        {
            out = Bitmap.createScaledBitmap (
                source, params.width, params.height, doBilinearInterpolation
                );
        }

        // ensure we are returning a copy and not the original
        if (null != out)
        {
            out = out.copy(storageScheme, isMutable);
        }

        return out;
    }

    /**
     * Returns a copy of source that has been blurred
     *
     * @param degree (optional) a number between 0 and 1,
     *               which determines the amount of bluring to perform.
     *               Higher numbers for more blur.
     *               Any value higher than 1 is treated as 1.
     *               Default : 0.75
     * @param source source bitmap to blur
     * @return blurred copy
     */
    public static Bitmap blur(@Nullable Bitmap source, double degree)
    {
        Bitmap.Config storageScheme = Bitmap.Config.ARGB_8888;
        Bitmap copy = null;
        boolean doBilinearInterpolation = true, isMutable = true;
        int scaledWidth = 0, scaledHeight = 0;

        // ensure degree is in range
        if (degree < 0.0)
        {
            degree = 0.0;
        }
        if (degree > 1.0)
        {
            degree = 1.0;
        }

        // we're working with a nullable source
        if (null != source)
        {
            // get scaling parameters
            scaledWidth = (int) (source.getWidth() * (1.0 - degree));
            scaledHeight = (int) (source.getHeight() * (1.0 - degree));

            // enforce a minimum width and height
            if ( scaledWidth < MIN_SCALE_DIM )
            {
                scaledWidth = MIN_SCALE_DIM;
            }
            if ( scaledHeight < MIN_SCALE_DIM )
            {
                scaledHeight = MIN_SCALE_DIM;
            }

            // We are going to rely on Android's Bilinear Interpolation, which
            // it does as part of its scaling process.

            // So first, let's shrink the image...
            copy = Bitmap.createScaledBitmap (
                source, scaledWidth, scaledHeight, doBilinearInterpolation
                );

            // ...then let's grow the image back to full size...
            if (null != copy)
            {
                copy = Bitmap.createScaledBitmap (
                    copy, source.getWidth(), source.getHeight(), 
                    doBilinearInterpolation
                    );
            }

            // ... then let's ensure we are outputting a copy and not the original.
            if (null != copy)
            {
                copy = copy.copy(storageScheme, isMutable);
            }

        }

        return copy;
    }
    public static Bitmap blur(Bitmap source)
    {
        return blur(source, 0.75);
    }

    // TODO: weakref
    /** @return the resources from the main activity */
    protected static Resources getResources()
    {
        Resources out = null;

        if (null != MainActivity.getMainActivity())
        {
            out = MainActivity.getMainActivity().getResources();
        }

        return out;
    }

    // TODO: weakref
    /** @return the layoutparams from the main activity, or null on failure */
    protected static ViewGroup.LayoutParams getLayoutParams()
    {
        ViewGroup.LayoutParams out = null;

        if (null != MainActivity.getMainActivity())
        {
            out = MainActivity.getMainActivity().getWindow().getAttributes();
        }

        return out;
    }
}
