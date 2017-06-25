package nz.ac.ara.sjw296.androidmazeagain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import nz.ac.ara.sjw296.androidmazeagain.communal.MazePoint;
import nz.ac.ara.sjw296.androidmazeagain.communal.Point;

/**
 * View for displaying game representation
 * @author Simon Winder
 * @since 23/06/2017
 */
public class MazeGameView extends View implements MazeView {
    private int mLineLength;
    private int mImageSize;
    private int mImageSpacing;
    private List<Point> mTopWalls;
    private List<Point> mLeftWalls;
    private Point mTheseus;
    private Point mMinotaur;
    private Mood mTheseusMood = Mood.NORMAL;
    private Paint mLinePaint = new Paint();
    private int mPaddingLeft = 30;
    private int mPaddingTop = (getResources().getConfiguration().orientation == 1) ? 150 : 30;
    private Paint mMovesPaint = new Paint();
    private CommunalImageTools mImageTools = new CommunalImageTools();
    private int mRows;
    private int mCols;

    /**
     * Constructor
     * Sets paints used to display the game
     * @param context app context
     * @param attrs -
     */
    public MazeGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(6F);
        mMovesPaint.setStyle(Paint.Style.FILL);
        mMovesPaint.setColor(Color.BLACK);
        mMovesPaint.setTextSize(60);
        mMovesPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    /**
     * Override of the onMeasure method
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = 0;

        if (widthMeasureSpec >= heightMeasureSpec) {
            size = heightMeasureSpec;
        } else if (heightMeasureSpec > widthMeasureSpec) {
            size = widthMeasureSpec;
        }
        setMeasuredDimension(size, size);
    }

    /**
     * Sets up for game information to be input and drawn
     * @param rows
     * @param cols
     */
    public void newGameSetup(int rows, int cols) {
        this.mRows = rows;
        this.mCols = cols;
        mLeftWalls = new ArrayList<>();
        mTopWalls = new ArrayList<>();
    }

    private void setLineLength() {
        int size;
        if (getResources().getConfiguration().orientation == 1) {
            size = getMeasuredWidth();
        } else {
            size = getMeasuredHeight();
        }
        mLineLength = (int)(size * .9 / mRows);
    }

    /**
     * Adds a left wall
     * Representing a something wall that can't be moved through
     * @param p the point on the maze the wall is left of
     */
    public void addLeftWall(Point p) {
        mLeftWalls.add(p);
    }

    /**
     * Adds a top wall
     * Representing a something wall that can't be moved through
     * @param p the point on the maze the wall is above
     */
    public void addTopWall(Point p) {
        mTopWalls.add(p);
    }

    /**
     * Sets where Theseus will be drawn on the game
     * @param p The point on the game
     */
    public void setTheseusPosition(Point p) {
        mTheseus = new MazePoint(p);
    }

    /**
     * Sets the mood representing Theseus' game state
     * The mood is responsible for which face is drawn for Theseus
     * @param mTheseusMood HAPPY for out, NORMAL for playing
     */
    public void setTheseusMood(Mood mTheseusMood) {
        this.mTheseusMood = mTheseusMood;
    }

    /**
     * Sets where the Minotaur will be drawn on the game
     * @param p The point on the game
     */
    public void setMinotaur(Point p) {
        mMinotaur = new MazePoint(p);
    }

    private int setCorrectTheseusImage() {
        int resource;
        switch (mTheseusMood) {
            case SAD:
                resource = R.drawable.theseus_fail;
                break;
            case HAPPY:
                resource = R.drawable.theseus_succeed;
                break;
            default:
                resource = R.drawable.theseus;
        }
        return resource;
    }

    private int getImageLocation(int xy) {
        return xy * mLineLength + mImageSpacing / 2;
    }

    /**
     * onDraw gets called when invalidate() is called for the View
     * Draws the game to the view
     * @param canvas - What the game is drawn on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFBBBBBB);
        setLineLength();
        setImageNumbers();
        if (mLeftWalls != null) {
            Bitmap bitmapSrcTheseus = mImageTools.decodeSampledBitmapFromResource(getResources(), setCorrectTheseusImage(), mImageSize, mImageSize);
            float theseusX = getImageLocation(mTheseus.getCol()) + mPaddingLeft;
            float theseusY = getImageLocation(mTheseus.getRow()) + mPaddingTop;
            //canvas.drawBitmap(bitmapSrcTheseus, theseusX, theseusY, null);
            canvas.drawBitmap(bitmapSrcTheseus, null, getImageRectF(theseusX, theseusY), null);
            Bitmap bitmapSrcMinotaur = mImageTools.decodeSampledBitmapFromResource(getResources(), R.drawable.minotaur, mImageSize, mImageSize);
            float minotaurX = getImageLocation(mMinotaur.getCol()) + mPaddingLeft;
            float minotaurY = getImageLocation(mMinotaur.getRow()) + mPaddingTop;
            //canvas.drawBitmap(bitmapSrcMinotaur, minotaurX, minotaurY, null);
            canvas.drawBitmap(bitmapSrcMinotaur, null, getImageRectF(minotaurX, minotaurY), null);
            //canvas.drawText("Moves: " + String.valueOf(moves), 30, 60, mMovesPaint);

            for (Point p:
                    mTopWalls) {
                int x = getSquarePosition(p.getCol()) + mPaddingLeft;
                int y = getSquarePosition(p.getRow()) + mPaddingTop;
                canvas.drawLine(x, y, x + mLineLength, y, mLinePaint);
            }

            for (Point p:
                    mLeftWalls) {
                int x = getSquarePosition(p.getCol()) + mPaddingLeft;
                int y = getSquarePosition(p.getRow()) + mPaddingTop;
                canvas.drawLine(x, y, x, y + mLineLength, mLinePaint);
            }
        }
    }

    private RectF getImageRectF(float x, float y) {
        return new RectF(x, y, x + mImageSize, y + mImageSize);
    }

    private int getSquarePosition(int xy) {
        return xy * mLineLength;
    }

    private void setImageNumbers() {
        mImageSize = (int)(mLineLength * .9);
        mImageSpacing = (int)(mLineLength * .1);
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
        if (mTheseus != null) {
            ss.rows = mRows;
            ss.cols = mCols;
            ss.theseus = mTheseus.toString();
            ss.minotaur = mMinotaur.toString();
            ss.leftWalls = mLeftWalls;
            ss.topWalls = mTopWalls;
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
        try {
            newGameSetup(ss.rows, ss.cols);
            setTheseusPosition(new MazePoint(ss.theseus));
            setMinotaur(new MazePoint(ss.minotaur));
            mTopWalls = ss.topWalls;
            mLeftWalls = ss.leftWalls;
            invalidate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class SavedState extends BaseSavedState {
        int rows;
        int cols;
        String theseus;
        String minotaur;
        List<Point> leftWalls;
        List<Point> topWalls;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            rows = in.readInt();
            cols = in.readInt();
            theseus = in.readString();
            minotaur = in.readString();
            in.readList(leftWalls, Point.class.getClassLoader());
            in.readList(topWalls, Point.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(rows);
            out.writeInt(cols);
            out.writeString(theseus);
            out.writeString(minotaur);
            out.writeList(leftWalls);
            out.writeList(topWalls);
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
