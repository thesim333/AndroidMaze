package nz.ac.ara.sjw296.androidmazeagain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.List;

import nz.ac.ara.sjw296.androidmazeagain.game.Direction;

/**
 * Displays the solution generated for the current game.
 * Solution is displayed as a series of images representing moves.
 * @author Simon Winder
 * @since 23/06/2017
 */
public class GameSolutionView extends View implements SolutionView {
    protected boolean mShowSolution = false;
    protected List<Direction> mSolution;
    protected String mOrientation;
    protected CommunalImageTools mImageTools = new CommunalImageTools();
    protected int mImageSize = 200;
    protected int mImageSpace = 8;
    protected int mImageSpaceToGameViewSide = 70;
    private final String VERTICAL = "Vertical";
    private final String HORIZONTAL = "Horizontal";

    /**
     * Standard View constructor.
     * Defines the direction the solution will be drawn.
     * @param context
     * @param attrs
     */
    public GameSolutionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final ViewTreeObserver obs = this.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (getMeasuredHeight() > getMeasuredWidth()) {
                    mOrientation = VERTICAL;
                } else {
                    mOrientation = HORIZONTAL;
                }
                return true;
            }
        });
    }

    /**
     * Inputs the current solution and allows the solution to be drawn
     * @param solution List of the solve for the current game
     */
    public void setSolution(List<Direction> solution) {
        mSolution = solution;
        mShowSolution = true;
    }

    /**
     * Test if this View currently has a solution for the current game.
     * @return solution list size is above 0
     */
    public boolean haveSolution() {
        return (mSolution.size() > 0);
    }

    /**
     * Get the Direction of the next move in the solution.
     * @return The Direction at the start 0f the list.
     */
    public Direction getNextMove() {
        return mSolution.get(0);
    }

    /**
     * Remove the next move from the list and redraw the solution.
     */
    public void popAndDraw() {
        mSolution.remove(0);
        invalidate();
    }

    /**
     * The View will be drawn blank and won't update solution to the game.
     */
    public void stopShowingSolution() {
        mShowSolution = false;
        invalidate();
    }

    private Bitmap getImageForDirection(Direction d) {
        int resource;
        switch(d) {
            case UP:
                resource = R.drawable.move_type_up;
                break;
            case DOWN:
                resource = R.drawable.move_type_down;
                break;
            case LEFT:
                resource = R.drawable.move_type_left;
                break;
            case RIGHT:
                resource = R.drawable.move_type_right;
                break;
            default:
                resource = R.drawable.move_type_skip;
        }

        return getImage(resource);
    }

    private Bitmap getImage(int resource) {
        return mImageTools.decodeSampledBitmapFromResource(getResources(), resource, mImageSize, mImageSize);
    }

    private RectF getImageRectF(float x, float y) {
        return new RectF(x, y, x + mImageSize, y + mImageSize);
    }

    private float getImageX(int count) {
        if (mOrientation.equals(VERTICAL)) {
            return mImageSpaceToGameViewSide;
        } else {
            return mImageSpace + (mImageSpace + mImageSize) * count;
        }
    }

    private float getImageY(int count) {
        if (mOrientation.equals(HORIZONTAL)) {
            return mImageSpaceToGameViewSide;
        } else {
            return mImageSpace + (mImageSpace + mImageSize) * count;
        }
    }

    /**
     * Override of the default onDraw method that will be called by invalidate()
     * Draws the solution if allowable and a solution exists to draw.
     * @param canvas To draw on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#AA3356"));

        if (mShowSolution && mSolution.size() > 0) {
            int count = 0;
            for (Direction d:
                    mSolution) {
                Bitmap image = getImageForDirection(d);
                canvas.drawBitmap(image, null, getImageRectF(getImageX(count), getImageY(count)), null);
                count++;
            }
        }
        else if (mShowSolution) {
            Bitmap image = getImage(R.drawable.danger);
            canvas.drawBitmap(image, null, getImageRectF(getImageX(0), getImageY(0)), null);
        }
    }

    /**
     * Saves the state of the view so it can be redrawn on rotate.
     * Don't call this method it is called automatically.
     * @return SavedState object containing variables of the View
     */
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.showSolution = mShowSolution;
        if (mShowSolution && mSolution != null) {
            ss.solution = mSolution;
        }
        return ss;
    }

    /**
     * Restores the view to it's state before a rotation.
     * Don't call this method it is called automatically.
     * @param state the object containing the variables to redraw the view.
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mShowSolution = ss.showSolution;
        if (mShowSolution && ss.solution != null) {
            mSolution = ss.solution;
        }
    }

    private static class SavedState extends BaseSavedState {
        boolean showSolution;
        List<Direction> solution;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            showSolution = in.readByte() != 0;
            in.readList(solution, Direction.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (showSolution ? 1 : 0));
            out.writeList(solution);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
