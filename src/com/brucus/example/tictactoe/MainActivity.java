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

import com.brucus.Brucus;
import com.brucus.BrucusException;
import com.brucus.IRTData;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String AppID = "YOUR_BRUCUS_API_KEY";
	private Board board;
	private DrawBoard drawBoard;
	private FrameLayout gameCanvas;
	private static String userId;
	private static String remUserId;
	private boolean isMyTurn = false;
	private static boolean isGameActive = false;
	private String channelName;
	private static int scoreHome = 0;
	private static int scoreGuest = 0;
	private TextView tvScoreHome;
	private TextView tvScoreGuest;
	public static ProgressDialog progress;
	

	public static void setIsGameActive(boolean active) {
		isGameActive = active;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		board = Board.getInstance();
		gameCanvas = (FrameLayout)findViewById(R.id.frmCanvas); 
		drawBoard = new DrawBoard(this, board, gameCanvas);
		gameCanvas.addView(drawBoard);
		gameCanvas.setOnTouchListener(boardTouch);
		tvScoreHome = (TextView)findViewById(R.id.tvHomeScore);
		tvScoreGuest = (TextView)findViewById(R.id.tvGuestScore);
		progress = new ProgressDialog(this);
		progress.setTitle(R.string.loading_title);
		progress.setMessage(getResources().getText(R.string.loading_body));
		
		Brucus.init(AppID);
		
		try {
			Brucus.enableRealTime();
		} catch (BrucusException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		Brucus.disableRealTime();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_connect:
			progress.show();
			userId = Brucus.getUserId();
			channelName = System.currentTimeMillis() + String.valueOf(Math.ceil(Math.random()*1000));
			Brucus.registerRealTimeCallback(Handshake.class, "iWantToPlay", new IRTData<Handshake>() {
				@Override
				public void incomingData(Handshake handshake) {
					switch (handshake.step) {
					case 1:  // SYN
						if (!isGameActive) {
							isGameActive = true;
							remUserId = handshake.sender;
							Brucus.createPrivateChannel(channelName, new String[] {userId, remUserId});
							Handshake hs = new Handshake();
							hs.sender = userId;
							hs.receiver = remUserId;
							hs.channelname = channelName;
							hs.step = 2;
							Brucus.broadcastToRecipients("iWantToPlay", hs, new String[] {remUserId});
						}
						break;
					case 2:  // ACK+SYN
						remUserId = handshake.sender;
						Brucus.joinChannel(handshake.channelname);
						Handshake hs = new Handshake();
						hs.sender = userId;
						hs.receiver = remUserId;
						hs.channelname = channelName;
						hs.step = 3;
						Brucus.broadcastToRecipients("iWantToPlay", hs, new String[] {remUserId});
						progress.dismiss();
						break;
					case 3:  // ACK
						progress.dismiss();
						isMyTurn = true;
						Toast.makeText(getApplicationContext(), R.string.start_playing ,Toast.LENGTH_SHORT).show();
						break;
					}
				}
			});
			Brucus.registerRealTimeCallback(Turn.class, "play", new IRTData<Turn>() {
				@Override
				public void incomingData(Turn turn) {
					if (turn.row == -1 && turn.column == -1) {
						board.clearBoard();
					}
					else if (!isMyTurn) {
						if (board.setPosition(turn.row, turn.column, Marker.O)) {
		        			scoreGuest++;
		        			tvScoreGuest.setText(scoreGuest+"");
						}
						isMyTurn=true;
					}
				}
			});
			Handshake hs = new Handshake();
			hs.sender = userId;
			hs.step = 1;
			Brucus.broadcast("iWantToPlay", hs);
			break;
		case R.id.action_play_again:
			if (board.finished()) {
        		Turn t = new Turn();
        		t.row = -1;
        		t.column = -1;
        		Brucus.broadcastToRecipients("play", t, new String[] {remUserId});
				board.clearBoard();
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.game_not_finished, Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

    private OnTouchListener boardTouch = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
        	if (!board.finished() & isMyTurn) {
	        	if (event.getAction() == MotionEvent.ACTION_UP) {
		        	int[] touch = drawBoard.getTouchPosition(event.getX(), event.getY());
		        	if (touch!=null && board.getPosition(touch[0], touch[1])==0) {
		        		if (board.setPosition(touch[0], touch[1], Marker.X)) {
		        			scoreHome++;
		        			tvScoreHome.setText(scoreHome+"");
		        		}
		        		Turn t = new Turn();
		        		t.row = touch[0];
		        		t.column = touch[1];
		        		Brucus.broadcastToRecipients("play", t, new String[] {remUserId});
		        		isMyTurn = false;
		        	}
	        	}
        	}
        	return true;
        }
    };
}