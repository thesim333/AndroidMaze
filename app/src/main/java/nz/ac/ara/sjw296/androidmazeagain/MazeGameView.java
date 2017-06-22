package nz.ac.ara.sjw296.androidmazeagain;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

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
    private Paint nonLinePaint = new Paint();
    private int paddingLeft;
    private int paddingTop;
    private int moves = -1;
    private Paint movesPaint = new Paint();
    private int rows;
    private int cols;
    private CommunalImageTools imageTools = new CommunalImageTools();

    public MazeGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(6F);
        nonLinePaint.setStyle(Paint.Style.STROKE);
        nonLinePaint.setColor(Color.BLACK);
        nonLinePaint.setStrokeWidth(1F);
        nonLinePaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        movesPaint.setStyle(Paint.Style.FILL);
        movesPaint.setColor(Color.BLACK);
        movesPaint.setTextSize(60);
        movesPaint.setTypeface(Typeface.DEFAULT_BOLD);

        final ViewTreeObserver obs = this.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                lineLength = getMeasuredHeight() / 11;
                imageSize = (int)(lineLength * .9);
                imageSpacing = (int)(lineLength * .1);
                return true;
            }
        });
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
        paddingTop = (getMeasuredHeight() - cols * lineLength) / 2;
        paddingLeft = (getMeasuredHeight() - rows * lineLength) / 2;
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
            float theseusX = getImageLocation(theseus.getCol()) + paddingTop;
            float theseusY = getImageLocation(theseus.getRow()) + paddingLeft;
            //canvas.drawBitmap(bitmapSrcTheseus, theseusX, theseusY, null);
            canvas.drawBitmap(bitmapSrcTheseus, null, getImageRectF(theseusX, theseusY), null);
            Bitmap bitmapSrcMinotaur = imageTools.decodeSampledBitmapFromResource(getResources(), R.drawable.minotaur, imageSize, imageSize);
            float minotaurX = getImageLocation(minotaur.getCol()) + paddingTop;
            float minotaurY = getImageLocation(minotaur.getRow()) + paddingLeft;
            //canvas.drawBitmap(bitmapSrcMinotaur, minotaurX, minotaurY, null);
            canvas.drawBitmap(bitmapSrcMinotaur, null, getImageRectF(minotaurX, minotaurY), null);
            canvas.drawText("Moves: " + String.valueOf(moves), 30, 60, movesPaint);

            for (Point p:
                    topWalls) {
                int x = getSquarePosition(p.getCol()) + paddingTop;
                int y = getSquarePosition(p.getRow()) + paddingLeft;
                canvas.drawLine(x, y, x + lineLength, y, linePaint);
            }

            for (Point p:
                    leftWalls) {
                int x = getSquarePosition(p.getCol()) + paddingTop;
                int y = getSquarePosition(p.getRow()) + paddingLeft;
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
}
