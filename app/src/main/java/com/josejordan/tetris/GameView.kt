import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.josejordan.tetris.Board


class GameView(context: Context) : SurfaceView(context), Runnable {
    private val thread: Thread
    private val paint: Paint = Paint()
    private val holder: SurfaceHolder = getHolder()
    private var isRunning = false
    private var gameOver = false
    private val board: Board = Board(this)

    private val handler = Handler(Looper.getMainLooper())

    init {
        thread = Thread(this)
        paint.isAntiAlias = true
    }
    fun gameOver() {
       gameOver = true
   }

    private val updateInterval: Long = 500 // Agrega esto al principio de la clase GameView

    override fun run() {
        var lastUpdateTime = System.currentTimeMillis()

        while (isRunning) {
            val currentTime = System.currentTimeMillis()

            if (holder.surface.isValid) {
                val canvas = holder.lockCanvas()
                drawGame(canvas)
                holder.unlockCanvasAndPost(canvas)
            }

            if (!gameOver && currentTime - lastUpdateTime >= updateInterval) {
                lastUpdateTime = currentTime
                handler.post(this::update)
            }
        }
    }


    private fun drawGame(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        board.draw(canvas, paint)

        if (gameOver) { // mostrar mensaje "GAME OVER" si el juego ha terminado
            val centerX = canvas.width / 2f
            val centerY = canvas.height / 2f

            paint.textSize = 100f
            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("GAME OVER", centerX, centerY, paint)
        }

    }

    private fun update() {
        board.moveDown()
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        board.draw(canvas, paint)
    }

/*    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x < width / 2) {
                    board.moveLeft()
                } else {
                    board.moveRight()
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                board.rotate()
                invalidate()
            }
        }
        return true
    }*/

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!gameOver) { // Agregado
                    if (event.x < width / 2) {
                        board.moveLeft()
                    } else {
                        board.moveRight()
                    }
                    invalidate()
                } else {
                    // Reiniciar juego
                    board.restart()
                    gameOver = false
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!gameOver) { // Agregado
                    board.rotate()
                    invalidate()
                }
            }
        }
        return true
    }


    fun resume() {
        isRunning = true
        thread.start()
    }

    fun pause() {
        isRunning = false
        thread.join()
    }

}

