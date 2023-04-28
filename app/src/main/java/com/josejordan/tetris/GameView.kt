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
import java.lang.Thread.sleep


class GameView(context: Context) : SurfaceView(context), Runnable {
    private val thread: Thread
    private val paint: Paint = Paint()
    private val holder: SurfaceHolder = getHolder()
    private var isRunning = false
    private val board: Board = Board()

    private val handler = Handler(Looper.getMainLooper())

    init {
        thread = Thread(this)
        paint.isAntiAlias = true
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

            if (currentTime - lastUpdateTime >= updateInterval) {
                lastUpdateTime = currentTime
                handler.post(this::update)
            }
        }
    }



    private fun drawGame(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        board.draw(canvas, paint)
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
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

