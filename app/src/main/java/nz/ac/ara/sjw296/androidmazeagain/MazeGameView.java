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
 * Created by Sim on 22/06/2017.
 */

public class MazeGameView extends View implements MazeView {
    private int lineLength;
    private int imageSize;
    private int imageSpacing;
    private List<Point> topWalls;
    private List<Point> leftWalls;
    private Point theseus;
    private Point minotaur;
    private Mood theseusMood = Mood.NORMAL;
    private Paint linePaint = new Paint();
    private int paddingLeft;
    private int paddingTop;
    private int moves = -1;
    private Paint movesPaint = new Paint();
    private CommunalImageTools imageTools = new CommunalImageTools();
    private int rows;
    private int cols;

    public MazeGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(6F);
        movesPaint.setStyle(Paint.Style.FILL);
        movesPaint.setColor(Color.BLACK);
        movesPaint.setTextSize(60);
        movesPaint.setTypeface(Typeface.DEFAULT_BOLD);

//        final ViewTreeObserver obs = this.getViewTreeObserver();
//        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                lineLength = getMeasuredHeight() / 11;
//                imageSize = (int)(lineLength * .9);
//                imageSpacing = (int)(lineLength * .1);
//                return true;
//            }
//        });
    }

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

    public void newGameSetup(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        lineLength = (int)((getMeasuredWidth() < getMeasuredHeight() ? getMeasuredWidth() : getMeasuredHeight() * .9) / rows);
        setImageNumbers();
        paddingTop = (getMeasuredHeight() - rows * lineLength) / 2 + 50;
        paddingLeft = (getMeasuredWidth() - cols * lineLength) / 2;
        leftWalls = new ArrayList<>();
        topWalls = new ArrayList<>();
    }

    public void addLeftWall(Point p) {
        leftWalls.add(p);
    }

    public void addTopWall(Point p) {
        topWalls.add(p);
    }

    public void setTheseusPosition(Point p) {
        theseus = new MazePoint(p);
    }

    public void setTheseusMood(Mood theseusMood) {
        this.theseusMood = theseusMood;
    }

    public void setMinotaur(Point p) {
        minotaur = new MazePoint(p);
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    private int setCorrectTheseusImage() {
        int resource;
        switch (theseusMood) {
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
        return xy * lineLength + imageSpacing / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFBBBBBB);
        if (moves >= 0) {
            Bitmap bitmapSrcTheseus = imageTools.decodeSampledBitmapFromResource(getResources(), setCorrectTheseusImage(), imageSize, imageSize);
            float theseusX = getImageLocation(theseus.getCol()) + paddingLeft;
            float theseusY = getImageLocation(theseus.getRow()) + paddingTop;
            //canvas.drawBitmap(bitmapSrcTheseus, theseusX, theseusY, null);
            canvas.drawBitmap(bitmapSrcTheseus, null, getImageRectF(theseusX, theseusY), null);
            Bitmap bitmapSrcMinotaur = imageTools.decodeSampledBitmapFromResource(getResources(), R.drawable.minotaur, imageSize, imageSize);
            float minotaurX = getImageLocation(minotaur.getCol()) + paddingLeft;
            float minotaurY = getImageLocation(minotaur.getRow()) + paddingTop;
            //canvas.drawBitmap(bitmapSrcMinotaur, minotaurX, minotaurY, null);
            canvas.drawBitmap(bitmapSrcMinotaur, null, getImageRectF(minotaurX, minotaurY), null);
            canvas.drawText("Moves: " + String.valueOf(moves), 30, 60, movesPaint);

            for (Point p:
                    topWalls) {
                int x = getSquarePosition(p.getCol()) + paddingLeft;
                int y = getSquarePosition(p.getRow()) + paddingTop;
                canvas.drawLine(x, y, x + lineLength, y, linePaint);
            }

            for (Point p:
                    leftWalls) {
                int x = getSquarePosition(p.getCol()) + paddingLeft;
                int y = getSquarePosition(p.getRow()) + paddingTop;
                canvas.drawLine(x, y, x, y + lineLength, linePaint);
            }
        }
    }

    private RectF getImageRectF(float x, float y) {
        return new RectF(x, y, x + imageSize, y + imageSize);
    }

    private int getSquarePosition(int xy) {
        return xy * lineLength;
    }

    private void setTopWalls(List<Point> walls) {
        topWalls = walls;
    }

    private void setLeftWalls(List<Point> walls) {
        leftWalls = walls;
    }

    private void setImageNumbers() {
        imageSize = (int)(lineLength * .9);
        imageSpacing = (int)(lineLength * .1);
    }

    private void setLineLength(int l) {
        lineLength = l;
    }

    private void setPaddingTop(int pt) {
        paddingTop = pt;
    }

    private void setPaddingLeft(int pl) {
        paddingLeft = pl;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.lineLength = lineLength;
        ss.paddingTop = paddingTop;
        ss.paddingLeft = paddingLeft;
        ss.moves = moves;
        ss.theseus = theseus.toString();
        ss.minotaur = minotaur.toString();
        ss.leftWalls = leftWalls;
        ss.topWalls = topWalls;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setLineLength(ss.lineLength);
        setPaddingTop(ss.paddingTop);
        setPaddingLeft(ss.paddingLeft);
        setImageNumbers();
        setMoves(ss.moves);
        setTheseusPosition(new MazePoint(ss.theseus));
        setMinotaur(new MazePoint(ss.minotaur));
        setLeftWalls(ss.leftWalls);
        setTopWalls(ss.topWalls);
        invalidate();
    }

    private static class SavedState extends BaseSavedState {
        int lineLength;
        int paddingTop;
        int paddingLeft;
        int moves;
        String theseus;
        String minotaur;
        List<Point> leftWalls;
        List<Point> topWalls;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            lineLength = in.readInt();
            paddingTop = in.readInt();
            paddingLeft = in.readInt();
            moves = in.readInt();
            theseus = in.readString();
            minotaur = in.readString();
            in.readList(leftWalls, Point.class.getClassLoader());
            in.readList(topWalls, Point.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(lineLength);
            out.writeInt(paddingTop);
            out.writeInt(paddingLeft);
            out.writeInt(moves);
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
