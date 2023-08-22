package com.josejordan.tetris

import GameView
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import java.util.Random

class Board(private val gameView: GameView) {
    private val boardArray: Array<Array<TetrominoShape>>
    private val currentTetromino: Tetromino
    private val random: Random
    private var tileWidth: Float = 0f
    private var tileHeight: Float = 0f
    private var elapsedTime: Long = 0

    //private var gameOver: Boolean = false
    private var restartGame: Boolean = false
    private var restartGameConfirmed: Boolean = false
    private var restartGameTime: Long = 0


    init {
        boardArray = Array(HEIGHT) { Array(WIDTH) { TetrominoShape.Empty } }
        random = Random()
        currentTetromino = Tetromino()
        spawnNewTetromino()
    }


    private fun spawnNewTetromino() {
        currentTetromino.updateShape(TetrominoShape.values()[random.nextInt(7)])
        currentTetromino.position = Point(WIDTH / 2 - 1, 0)

        if (!isValidPosition(currentTetromino)) {
            gameView.gameOver()
            boardArray.forEach { row ->
                row.forEachIndexed { index, _ ->
                    row[index] = TetrominoShape.Empty
                }
            }

        }
    }


    fun restart() {
        boardArray.forEach { row ->
            row.forEachIndexed { index, _ ->
                row[index] = TetrominoShape.Empty
            }
        }
        spawnNewTetromino()
        //gameOver = false
        restartGame = false
        restartGameConfirmed = false
    }

    private fun isValidPosition(tetromino: Tetromino): Boolean {
        for (point in tetromino.coords) {
            val x = point.x + tetromino.position.x
            val y = point.y + tetromino.position.y

            if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || boardArray[y][x] != TetrominoShape.Empty) {
                return false
            }
        }
        return true
    }

    fun moveLeft() {
        val moved = currentTetromino.move(-1, 0)
        if (isValidPosition(moved)) {
            currentTetromino.position = moved.position
        }
    }

    fun moveRight() {
        val moved = currentTetromino.move(1, 0)
        if (isValidPosition(moved)) {
            currentTetromino.position = moved.position
        }
    }

    fun moveDown() {
        val moved = currentTetromino.move(0, 1)
        if (isValidPosition(moved)) {
            currentTetromino.position = moved.position
        } else {
            fixCurrentTetromino()
            spawnNewTetromino()
        }
    }

    private fun fixCurrentTetromino() {
        for (point in currentTetromino.coords) {
            val x = point.x + currentTetromino.position.x
            val y = point.y + currentTetromino.position.y
            boardArray[y][x] = currentTetromino.shape
        }
        clearFullRows()
    }

    fun rotate() {
        val rotated = currentTetromino.rotate()
        if (isValidPosition(rotated)) {
            currentTetromino.coords = rotated.coords
        }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        tileWidth = canvas.width.toFloat() / WIDTH
        tileHeight = canvas.height.toFloat() / HEIGHT

        /*        if (gameOver) {
                    val centerX = canvas.width / 2f
                    val centerY = canvas.height / 2f

                    paint.textSize = 100f
                    paint.color = Color.WHITE
                    paint.textAlign = Paint.Align.CENTER
                    canvas.drawText("GAME OVER", centerX, centerY, paint)
                    return  // Return early to prevent drawing the board and tetromino over the game over message
                }*/

        // Draw board
        for (y in boardArray.indices) {
            for (x in boardArray[y].indices) {
                if (boardArray[y][x] != TetrominoShape.Empty) {
                    paint.color = getColorForShape(boardArray[y][x])
                    canvas.drawRect(
                        x * tileWidth,
                        y * tileHeight,
                        (x + 1) * tileWidth,
                        (y + 1) * tileHeight,
                        paint
                    )
                }
            }
        }

        // Draw current tetromino
        paint.color = getColorForShape(currentTetromino.shape)
        for (point in currentTetromino.coords) {
            val x = point.x + currentTetromino.position.x
            val y = point.y + currentTetromino.position.y
            canvas.drawRect(
                (x * tileWidth),
                (y * tileHeight),
                ((x + 1) * tileWidth),
                ((y + 1) * tileHeight),
                paint
            )
        }
    }

    private fun getColorForShape(shape: TetrominoShape): Int {
        return when (shape) {
            TetrominoShape.I -> Color.CYAN
            TetrominoShape.O -> Color.YELLOW
            TetrominoShape.T -> Color.MAGENTA
            TetrominoShape.S -> Color.GREEN
            TetrominoShape.Z -> Color.RED
            TetrominoShape.J -> Color.BLUE
            TetrominoShape.L -> Color.parseColor("#FFA500") // Orange
            TetrominoShape.Empty -> Color.BLACK
        }
    }

    private fun clearFullRows() {
        for (y in boardArray.indices.reversed()) {
            if (boardArray[y].all { it != TetrominoShape.Empty }) {
                for (y2 in y downTo 1) {
                    boardArray[y2] = boardArray[y2 - 1].copyOf()
                }
                boardArray[0] = Array(WIDTH) { TetrominoShape.Empty }
            }
        }
    }


    companion object {
        const val WIDTH = 10
        const val HEIGHT = 20

    }
}
