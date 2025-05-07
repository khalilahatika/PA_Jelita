package com.google.mediapipe.examples.poselandmarker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var pointPaint = Paint()
    private var dressBitmaps: List<Bitmap>
    private var currentDressIndex = -1
    private var dressRect = RectF()
    private var dressEnabled = false
    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    init {
        initPaints()
        // Tambah semua dress di sini
        dressBitmaps = listOf(
            BitmapFactory.decodeResource(resources, R.drawable.dress),
            BitmapFactory.decodeResource(resources, R.drawable.dress_2),
        )
    }


    fun clear() {
        results = null
        pointPaint.reset()
        invalidate()
        initPaints()
    }

    fun showDressA(index: Int) {
        if (currentDressIndex == 0 && dressEnabled) {
            currentDressIndex = -1
            dressEnabled = false
        } else {
            currentDressIndex = 0
            dressEnabled = true
        }
        invalidate()
    }

    fun showDressB(index: Int) {
        if (currentDressIndex == 1 && dressEnabled) {
            currentDressIndex = -1
            dressEnabled = false
        } else {
            currentDressIndex = 1
            dressEnabled = true
        }
        invalidate()
    }


    private fun initPaints() {
        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    fun setDressByIndex(index: Int) {
        if (index in dressBitmaps.indices) {
            Log.d("OverlayView", "setDressByIndex dipanggil. Index baru: $index")
            currentDressIndex = index
            invalidate()
        } else {
            Log.d("OverlayView", "Index $index di luar batas list dressBitmaps")
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        Log.d("OverlayView", "Draw dipanggil. Instance: $this, Index baju: $currentDressIndex, Results: ${results != null}")

        results?.let { poseLandmarkerResult ->
            if (poseLandmarkerResult.landmarks().isNotEmpty()) {
                for (landmark in poseLandmarkerResult.landmarks()) {
                    for (normalizedLandmark in landmark) {
                        canvas.drawPoint(
                            normalizedLandmark.x() * imageWidth * scaleFactor,
                            normalizedLandmark.y() * imageHeight * scaleFactor,
                            pointPaint
                        )
                    }
                }

                val dressBitmap = if (currentDressIndex >= 0) dressBitmaps.getOrNull(currentDressIndex) else null
                if (dressEnabled && dressBitmap != null) {
                    val landmarks = poseLandmarkerResult.landmarks()[0]
                    if (landmarks.size > 24) {
                        val shoulderX = landmarks[11].x() * imageWidth * scaleFactor
                        val shoulderY = landmarks[11].y() * imageHeight * scaleFactor
                        val hipX = landmarks[24].x() * imageWidth * scaleFactor
                        val hipY = landmarks[24].y() * imageHeight * scaleFactor

                        val distance = Math.sqrt(
                            Math.pow(hipX - shoulderX.toDouble(), 2.0) +
                                    Math.pow(hipY - shoulderY.toDouble(), 2.0)
                        )

                        val dressWidth = (distance * 1.5).toFloat()
                        val dressHeight = dressBitmap.height * (dressWidth / dressBitmap.width)

                        val offsetX = 50f
                        dressRect.set(
                            shoulderX - dressWidth / 2 - offsetX,
                            shoulderY,
                            shoulderX + dressWidth / 2 - offsetX,
                            shoulderY + dressHeight
                        )

                        canvas.drawBitmap(dressBitmap, null, dressRect, null)
                    }
                }
            }
        }
    }



    fun setResults(
        poseLandmarkerResults: PoseLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = poseLandmarkerResults
        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE, RunningMode.VIDEO ->
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            RunningMode.LIVE_STREAM ->
                max(width * 1f / imageWidth, height * 1f / imageHeight)
        }
        invalidate()
    }


    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
    }
}