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

public class Board {
	public static Board board = null;
	
	private int[][] Board = new int[3][3];

	/**
	 * Constructor: create an empty board
	 */
	public Board() {
		clearBoard();
	}
	
	/**
	 * Returns Board Singleton, if object not exist create it
	 * @return Board object
	 */
	public static Board getInstance() {
		if (board==null) {
			board = new Board();
		}
		return board;
	}

	/**
	 * Clear board to create a new game.
	 */
	public void clearBoard() {
		for (int row = 0; row < 3; row++)
			for (int column = 0; column < 3; column++)
				Board[row][column] = Marker.EMTPY;
	}

	/**
	 * Return the marker for a specific position 
	 * @param attempt place of the marker [line, column]
	 * @return the marker 
	 *          0 : Marker.EMPTY
	 *          1 : Marker.O
	 *         -1 : Marker.X
	 */
	public int getPosition(int row, int column) {
		return Board[row][column];
	}

	/**
	 * Set the marker for a specific position
	 * @param attempt place of the marker [line, column]
	 * @param player 
	 * @return true if win the game
	 */
	public boolean setPosition(int row, int column , int player) {
		if (player == 1)
			Board[row][column] = Marker.X;
		else
			Board[row][column] = Marker.O;
		return (checkColumns()+checkRows()+checkDiagonals())!=0;
	}

	/**
	 * Check if there is a row that win
	 * @return Positive number if wins player O
	 *         Negative number if wins player X, 
	 *         number 1 means that wins at the first line, 
	 *         2 means that wins at the second line
	 *         and 3 means that wins at the third line.
	 *         zero if no player wins
	 */
	public int checkRows() {
		for (int row = 0; row < 3; row++) {
			if ((Board[row][0] + Board[row][1] + Board[row][2]) == -3)
				return -(row+1);
			if ((Board[row][0] + Board[row][1] + Board[row][2]) == 3)
				return row+1;
		}
		return 0;
	}

	/**
	 * Check if there is a column that win
	 * @return Positive number if wins player O
	 *         Negative number if wins player X, 
	 *         number 1 means that wins at the first column, 
	 *         2 means that wins at the second column
	 *         and 3 means that wins at the third column.
	 *         zero if no player wins
	 */
	public int checkColumns() {
		for (int column = 0; column < 3; column++) {
			if ((Board[0][column] + Board[1][column] + Board[2][column]) == -3)
				return -(column+1);
			if ((Board[0][column] + Board[1][column] + Board[2][column]) == 3)
				return column+1;
		}
		return 0;
	}

	/**
	 * Check if there is a diagonals that win
	 * @return Positive number if wins player O
	 *         Negative number if wins player X, 
	 *         number 1 means that wins at the left-to-right diagonal 
	 *         and 2 means that wins at the right-to-left diagonal.
	 *         zero if no player wins
	 */
	public int checkDiagonals() {
		if ((Board[0][0] + Board[1][1] + Board[2][2]) == -3)
			return -1;
		if ((Board[0][0] + Board[1][1] + Board[2][2]) == 3)
			return 1;
		if ((Board[0][2] + Board[1][1] + Board[2][0]) == -3)
			return -2;
		if ((Board[0][2] + Board[1][1] + Board[2][0]) == 3)
			return 2;
		return 0;
	}

	/**
	 * Checks if board is fully marked
	 * @return true if board is fully marked, otherwise false
	 */
	public boolean fullBoard() {
		for (int row = 0; row < 3; row++)
			for (int column = 0; column < 3; column++)
				if (Board[row][column] == 0)
					return false;
		return true;
	}
	
	/**
	 * Checks if game is completed
	 * @return true if game is completed, otherwise false
	 */
	public boolean finished() {
		return (checkColumns()+checkRows()+checkDiagonals())!=0 || fullBoard();
	}
}
