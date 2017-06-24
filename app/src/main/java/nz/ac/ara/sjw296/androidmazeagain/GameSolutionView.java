package nz.ac.ara.sjw296.androidmazeagain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.List;

import nz.ac.ara.sjw296.androidmazeagain.game.Direction;

/**
 * Created by Sim on 22/06/2017.
 */

public class GameSolutionView extends View implements SolutionView {
    protected boolean showSolution = false;
    protected List<Direction> theSolution;
    protected String orientation;
    private CommunalImageTools imageTools = new CommunalImageTools();
    private int imageSize = 84;
    private int imageSpace = 8;
    private final String VERTICAL = "Vertical";
    private final String HORIZONTAL = "Horizontal";
    float startingPoint = imageSpace + imageSize / 2;

    public GameSolutionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final ViewTreeObserver obs = this.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (getMeasuredHeight() > getMeasuredWidth()) {
                    orientation = VERTICAL;
                } else {
                    orientation = HORIZONTAL;
                }
                return true;
            }
        });
    }

    public void setSolution(List<Direction> solution) {
        theSolution = solution;
        showSolution = true;
    }

    public boolean haveSolution() {
        return (theSolution.size() > 0);
    }

    public Direction getNextMove() {
        return theSolution.get(0);
    }

    public void popAndDraw() {
        theSolution.remove(0);
        invalidate();
    }

    public void stopShowingSolution() {
        showSolution = false;
        invalidate();
    }
    
    protected Bitmap getImageForDirection(Direction d) {
        int resource;

        if (d == Direction.PASS) {
            resource = R.drawable.move_type_skip;
        } else {
            resource = R.drawable.move_type_right;
        }

        return imageTools.decodeSampledBitmapFromResource(getResources(), resource, imageSize, imageSize);
    }

    protected Matrix getImageRotationMatrix(Direction d, float x, float y) {
        float deg = 0;
        switch (d) {
            case DOWN:
                deg = 90;
                break;
            case LEFT:
                deg = 180;
                break;
            case UP:
                deg = 270;
                break;
        }

        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postTranslate(imageSize / 2, imageSize / 2);
        matrix.postRotate(deg);
        matrix.postTranslate(x, y);
        return matrix;
    }

    private float getImageX(int count) {
        if (orientation == VERTICAL) {
            return startingPoint;
        } else {
            return startingPoint + (imageSpace + imageSize) * count;
        }
    }

    private float getImageY(int count) {
        if (orientation == HORIZONTAL) {
            return startingPoint;
        } else {
            return startingPoint + (imageSpace + imageSize) * count;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#AA3356"));

        if (showSolution && theSolution.size() > 0) {
            int count = 0;
            for (Direction d:
                    theSolution) {
                Bitmap image = getImageForDirection(d);
                Matrix matrix = getImageRotationMatrix(d, getImageX(count), getImageY(count));
                canvas.drawBitmap(image, matrix, null);
                count++;
            }
        }
    }
}
