package com.josejordan.tetris

import android.graphics.Point

enum class TetrominoShape {
    I, O, T, S, Z, J, L, Empty
}

class Tetromino {
    var shape: TetrominoShape
        private set
    var coords: Array<Point>
    var position: Point

    init {
        shape = TetrominoShape.Empty
        coords = arrayOf(Point(), Point(), Point(), Point())
        position = Point(0, 0)
    }

    fun updateShape(newShape: TetrominoShape) {
        shape = newShape
        coords = when (shape) {
            TetrominoShape.I -> arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))
            TetrominoShape.O -> arrayOf(Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1))
            TetrominoShape.T -> arrayOf(Point(0, 1), Point(1, 0), Point(1, 1), Point(2, 1))
            TetrominoShape.S -> arrayOf(Point(0, 1), Point(1, 1), Point(1, 0), Point(2, 0))
            TetrominoShape.Z -> arrayOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(2, 1))
            TetrominoShape.J -> arrayOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(2, 1))
            TetrominoShape.L -> arrayOf(Point(2, 0), Point(0, 1), Point(1, 1), Point(2, 1))
            TetrominoShape.Empty -> arrayOf(Point(), Point(), Point(), Point())
        }
    }

    fun rotate(): Tetromino {
        if (shape == TetrominoShape.O) return this

        val rotated = Tetromino()
        rotated.updateShape(shape)
        rotated.position = position

        for (i in coords.indices) {
            rotated.coords[i] = Point(-coords[i].y, coords[i].x)
        }

        return rotated
    }

    fun move(offsetX: Int, offsetY: Int): Tetromino {
        val moved = Tetromino()
        moved.updateShape(shape)
        moved.coords = coords.copyOf()
        moved.position = Point(position.x + offsetX, position.y + offsetY)
        return moved
    }
}
