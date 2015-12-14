/*
 * Copyright (C) 2014 Brucus.com All Rights Reserved. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.brucus.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;
import android.widget.FrameLayout;

public class DrawBoard extends View {
	private Paint paint;
	private FrameLayout flView;
	private Board board;
	private int startX = 0, startY = 0, centerX = 0, centerY = 0,
			boardSize = 0, markerDistance = 0;

	public DrawBoard(Context context) {
		super(context);
		paint = new Paint();
	}

	public DrawBoard(Context context, Board br, FrameLayout fl) {
		super(context);
		board = br;
		paint = new Paint();
		flView = fl;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		calculateCanvas(flView);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(5);
		drawEmptyBoard(canvas, paint);
		drawBoard(canvas);
		int finish = board.checkColumns();
		if (finish!=0) {
			drawColumn(canvas, finish);
		}
		finish=board.checkRows();
		if 	(finish!=0) {
			drawRow(canvas, finish);
		}
		finish=board.checkDiagonals();
		if 	(finish!=0) {
			drawDiagonals(canvas, finish);
		}
		invalidate();
	}

	/**
	 * Calculate canvas boarders
	 * @param fl canvas
	 */
	private void calculateCanvas(FrameLayout fl) {
		int w, h;
		w = fl.getWidth();
		h = fl.getHeight();
		boardSize = (w < h ? w : h) - 50;
		markerDistance = (int) boardSize / 3;
		centerX = (int) w / 2;
		centerY = (int) h / 2;
		startX = centerX - (int) boardSize / 2;
		startY = centerY - (int) boardSize / 2;
	}

	/**
	 * Draw empty board
	 * @param cnv canvas
	 * @param p paint
	 */
	private void drawEmptyBoard(Canvas cnv, Paint p) {
		cnv.drawLine(startX, startY + markerDistance, 
				startX + boardSize,	startY + markerDistance, p);
		cnv.drawLine(startX, startY + 2 * markerDistance, 
				startX + boardSize, startY + 2 * markerDistance, p);
		cnv.drawLine(startX + markerDistance, startY, 
				startX + markerDistance, startY + boardSize, p);
		cnv.drawLine(startX + 2 * markerDistance, startY, 
				startX + 2 * markerDistance, startY + boardSize, p);
	}
	
	/**
	 * Draw markers on board
	 * @param cnv game canvas
	 */
	private void drawBoard(Canvas cnv) {
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				if (board.getPosition(row, column)==Marker.O) {
					drawMarkerO(cnv, row, column);
				}
				else if (board.getPosition(row, column)==Marker.X) {
					drawMarkerX(cnv, row, column);
				}
			}
		}
	}
	
	/**
	 * Draw marker X
	 * @param cnv canvas
	 * @param row
	 * @param column
	 */
	private void drawMarkerX(Canvas cnv, int row, int column) {
		int x = centerX+markerDistance*(row-1);
		int y = centerY+markerDistance*(column-1);
		int r = (int)(markerDistance*0.4);
		Paint p = new Paint();
		p.setColor(Color.DKGRAY);
		p.setStrokeWidth(5);
        cnv.drawLine(x-r, y-r, x+r, y+r, p);
        cnv.drawLine(x+r, y-r, x-r, y+r, p);
	}
	
	/**
	 * Draw marker O
	 * @param cnv canvas
	 * @param row
	 * @param column
	 */
	private void drawMarkerO(Canvas cnv, int row, int column) {
		int x = centerX+markerDistance*(row-1);
		int y = centerY+markerDistance*(column-1);
		int r = (int)(markerDistance*0.4);
		Paint p = new Paint();
		p.setColor(Color.CYAN);
		p.setStrokeWidth(5);
        p.setStyle(Style.STROKE);
		cnv.drawCircle(x, y, r, p);
	}

	private void drawColumn(Canvas cnv, int finish) {
		Paint p = new Paint();
		p.setColor(Color.YELLOW);
		p.setStrokeWidth(10);
        p.setStyle(Style.STROKE);
        p.setPathEffect(new DashPathEffect(new float[] {15,5}, 0));
		cnv.drawLine(startX, centerY+markerDistance*(Math.abs(finish)-2), startX+boardSize, centerY+markerDistance*(Math.abs(finish)-2), p);
	}

	private void drawRow(Canvas cnv, int finish) {
		Paint p = new Paint();
		p.setColor(Color.YELLOW);
		p.setStrokeWidth(10);
        p.setStyle(Style.STROKE);
        p.setPathEffect(new DashPathEffect(new float[] {15,5}, 0));
		cnv.drawLine(centerX+markerDistance*(Math.abs(finish)-2), startY, centerX+markerDistance*(Math.abs(finish)-2), startY+boardSize, p);
	}

	private void drawDiagonals(Canvas cnv, int finish) {
		Paint p = new Paint();
		p.setColor(Color.YELLOW);
		p.setStrokeWidth(10);
        p.setStyle(Style.STROKE);
        p.setPathEffect(new DashPathEffect(new float[] {10,5}, 0));
        if (Math.abs(finish) == 1) {
    		cnv.drawLine(startX, startY, startX+boardSize, startY+boardSize, p);
        }
        else {
    		cnv.drawLine(startX, startY+boardSize, startX+boardSize, startY, p);
        }
	}
	
	/**
	 * Get row,column from touch point
	 * @param x
	 * @param y
	 * @return 
	 */
	public int[] getTouchPosition(float x, float y) {
		if (x>startX && x<startX+boardSize && y>startY && y<startY+boardSize) {
			int[] retValue = new int[2];
			retValue[0] = (int)((x-startX)/markerDistance);
			retValue[1] = (int)((y-startY)/markerDistance);
			return retValue;
		}
		else {
			return null;
		}
	}

}
