package nz.ac.ara.sjw296.androidmazeagain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
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
    private int imageSize = 200;
    private int imageSpace = 8;
    private final String VERTICAL = "Vertical";
    private final String HORIZONTAL = "Horizontal";

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

    protected Bitmap getImage(int resource) {
        return imageTools.decodeSampledBitmapFromResource(getResources(), resource, imageSize, imageSize);
    }

    private RectF getImageRectF(float x, float y) {
        return new RectF(x, y, x + imageSize, y + imageSize);
    }

    private float getImageX(int count) {
        if (orientation.equals(VERTICAL)) {
            return imageSpace;
        } else {
            return imageSpace + (imageSpace + imageSize) * count;
        }
    }

    private float getImageY(int count) {
        if (orientation.equals(HORIZONTAL)) {
            return imageSpace;
        } else {
            return imageSpace + (imageSpace + imageSize) * count;
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
                canvas.drawBitmap(image, null, getImageRectF(getImageX(count), getImageY(count)), null);
                count++;
            }
        }
        else if (showSolution) {
            Bitmap image = getImage(R.drawable.danger);
            canvas.drawBitmap(image, null, getImageRectF(getImageX(0), getImageY(0)), null);
        }
    }
}
