/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.poselandmarker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var pointPaint = Paint()
    private var dressBitmap: Bitmap? = null
    private var dressRect = RectF()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    init {
        initPaints()
        // Load the dress image
        dressBitmap = BitmapFactory.decodeResource(resources, R.drawable.dress)
    }

    fun clear() {
        results = null
        pointPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    private val offsetY = 80f // Atur nilai offset untuk menggeser gaun ke atas

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
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

                if (dressBitmap != null) {
                    val landmarks = poseLandmarkerResult.landmarks()[0]
                    if (landmarks.size > 1) {
                        val shoulderX = landmarks[11].x() * imageWidth * scaleFactor
                        val shoulderY = landmarks[11].y() * imageHeight * scaleFactor - offsetY // Geser ke atas
                        val hipX = landmarks[24].x() * imageWidth * scaleFactor
                        val hipY = landmarks[24].y() * imageHeight * scaleFactor

                        val distance = Math.sqrt(Math.pow(hipX.toDouble() - shoulderX.toDouble(), 2.0) + Math.pow(hipY.toDouble() - shoulderY.toDouble(), 2.0))

                        val dressWidth = (distance * 1.5).toFloat()
                        val dressHeight = dressBitmap!!.height * (dressWidth / dressBitmap!!.width)

                        val offsetX = 85f
                        dressRect.set(shoulderX - dressWidth / 2 - offsetX, shoulderY, shoulderX + dressWidth / 2 - offsetX, shoulderY + dressHeight)

                        canvas.drawBitmap(dressBitmap!!, null, dressRect, null)
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
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
    }
}
