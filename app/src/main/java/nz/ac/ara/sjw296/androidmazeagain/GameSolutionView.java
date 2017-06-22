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

public class GameSolutionView extends View {
    protected boolean showSolution = false;
    protected List<Direction> theSolution;
    protected String orientation;
    private CommunalImageTools imageTools = new CommunalImageTools();
    private int imageSize;
    private int imageSpace;

    public GameSolutionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        imageSize = 0; //// TODO: 23/06/2017
        imageSpace = 0; // TODO: 23/06/2017  
        final ViewTreeObserver obs = this.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (getMeasuredHeight() > getMeasuredWidth()) {
                    orientation = "Vertical";
                } else {
                    orientation = "Horizontal";
                }
                return true;
            }
        });
    }

    public void setSolution(List<Direction> solution) {
        theSolution = solution;
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
    
    protected Bitmap getImageForDirection(Direction d) {
        int resource;

        if (d == Direction.PASS) {
            resource = R.drawable.move_type_skip;
        } else {
            resource = R.drawable.move_type_right;
        }

        return imageTools.decodeSampledBitmapFromResource(getResources(), resource, imageSize, imageSize);
    }

    protected Matrix getImageRotationMatrix(Direction d) {
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
        matrix.setRotate(deg, imageSize / 2, imageSize / 2);
        return matrix;
    }

    private float getImageX(int count) {
        //// TODO: 23/06/2017
    }

    private float getImageY(int count) {
        //// TODO: 23/06/2017
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#AA3356"));

        if (showSolution && theSolution.size() > 0) {
            int count = 0;
            for (Direction d:
                    theSolution) {
                Bitmap image = getImageForDirection(d);
                Matrix matrix = getImageRotationMatrix(d);
                canvas.drawBitmap(image, matrix, null);
                count++;
            }
        }
    }
}
