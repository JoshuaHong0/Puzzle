package com.meitu.mypuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.RowSet;

public class PuzzleView extends View {

    private static final int ROWS = 3;
    private static final int COLS = 3;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private int chunkHeight, chunkWidth;
    private int emptyId = ROWS * COLS - 1;
    private boolean firstLaunch = true;
    private Bitmap bitmap;
    private float startX, startY, endX, endY;
    private ArrayList<Piece> pieces = new ArrayList<>(ROWS * COLS);
    private ArrayList<Rect> srcs = new ArrayList<>();
    private ArrayList<Rect> dsts = new ArrayList<>();
    private Map<Integer, Integer> posToIndedx = new HashMap<>();

    private class Piece {

        private Rect srcRect;
        private Rect dstRect;

        public Piece(Rect srcRect, Rect dstRect) {
            this.srcRect = srcRect;
            this.dstRect = dstRect;
        }

        public Rect getSrcRect() {
            return srcRect;
        }

        public void setSrcRect(Rect srcRect) {
            this.srcRect = srcRect;
        }

        public Rect getDstRect() {
            return dstRect;
        }

        public void setDstRect(Rect dstRect) {
            this.dstRect = dstRect;
        }

    }

    public PuzzleView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (firstLaunch) init(canvas);
        for (Piece piece : pieces)
            canvas.drawBitmap(bitmap, piece.getSrcRect(),
                    piece.getDstRect(), null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                int size = pieces.size();
                if (endX - startX > 200 && emptyId % ROWS != 0) {
                    Shift(emptyId - 1, RIGHT);
                } else if (startX - endX > 200 && (emptyId + 1) % ROWS != 0) {
                    Shift(emptyId + 1, LEFT);
                } else if (endY - startY > 200 && emptyId - ROWS >= 0) {
                    Shift(emptyId - ROWS, DOWN);
                } else if (startY - endY > 200 && emptyId + ROWS <= size) {
                    Shift(emptyId + ROWS, UP);
                }
                invalidate();
        }
        return true;
    }

    private void init(Canvas canvas) {

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dog);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int imageWidth, imageHeight;

        if (bitmapWidth >= bitmapHeight) { // 图像宽度大于高度
            imageWidth = canvasWidth; // 设置图像宽度等于canvas宽度
            imageHeight = (bitmapHeight * canvasWidth) / bitmapWidth; // 等比例调整图像高度
        } else {                          // 图像高度大于宽度
            imageHeight = canvasHeight;  // 设置图像高度等于canvas高度
            imageWidth = (bitmapWidth * canvasHeight) / bitmapHeight; // 等比例调整图像宽度
        }

        chunkHeight = imageHeight / ROWS;
        chunkWidth = imageWidth / COLS;

        int bitmapUnitWidth = bitmapWidth / ROWS;
        int bitmapUnitHeight = bitmapHeight / COLS;


        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (i == ROWS - 1 && j == COLS - 1) continue;
                Rect sRect = new Rect(j * bitmapUnitWidth, i * bitmapUnitHeight,
                        (j + 1) * bitmapUnitWidth, (i + 1) * bitmapUnitHeight);
                Rect dRect = new Rect(j * chunkWidth, i * chunkHeight,
                        (j + 1) * chunkWidth, (i + 1) * chunkHeight);
                srcs.add(sRect);
                dsts.add(dRect);
            }
        }

        Collections.shuffle(srcs);
        int cnt = 0;
        for (int i = 0; i < ROWS * COLS - 1; i++) {
            pieces.add(new Piece(srcs.get(i), dsts.get(i)));
            posToIndedx.put(cnt, cnt);
            cnt++;
        }
        firstLaunch = false;
    }

    private void Shift(int target_id, int orientation) {
        int index = posToIndedx.get(target_id);
        Piece target = pieces.get(index);
        Rect oldDst = target.getDstRect();
        Rect newDst = new Rect();
        if (orientation == RIGHT) {
            newDst = new Rect(oldDst.left + chunkWidth, oldDst.top,
                    oldDst.right + chunkWidth, oldDst.bottom);
        } else if (orientation == LEFT) {
            newDst = new Rect(oldDst.left - chunkWidth, oldDst.top,
                    oldDst.right - chunkWidth, oldDst.bottom);
        } else if (orientation == UP) {
            newDst = new Rect(oldDst.left, oldDst.top - chunkHeight,
                    oldDst.right, oldDst.bottom - chunkHeight);
        } else if (orientation == DOWN) {
            newDst = new Rect(oldDst.left, oldDst.top + chunkHeight,
                    oldDst.right, oldDst.bottom + chunkHeight);
        }
        target.setDstRect(newDst);
        posToIndedx.put(emptyId, index);
        emptyId = target_id;
    }

}
