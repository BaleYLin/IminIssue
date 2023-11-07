package com.example.iminissue

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.Log


/**
 ** © Copyright 2020. KUANGLI. All Rights Reserved.
 ** Project: poslib
 ** Package: com.kuangli.lib.pos.utility
 ** File: BitmapUtility
 ** Create Date: 2020-08-20 13:39
 ** Author: Bale Lin
 ** Description:
 **
 ** Revision History:
 ** Date     Author      Ref     Revision(Date in YYYY-MM-DD format)
 **
 **
 **/
class BitmapUtility {
    companion object {
        fun renewBitmap(bitmap: Bitmap): Bitmap {
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            // 定义输出的bitmap
            val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val canvas = Canvas(newBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(bitmap, rect, rect, null)
            return newBitmap
        }

        /**
         * 把两个位图覆盖合成为一个位图，左右拼接
         * @param leftBitmap
         * @param rightBitmap
         * @param isBaseMax 是否以宽度大的位图为准，true则小图等比拉伸，false则大图等比压缩
         * @return
         */
        fun mergeBitmap_LR(
            leftBitmap: Bitmap?,
            rightBitmap: Bitmap?,
            isBaseMax: Boolean,
            spacing: Int = 0
        ): Bitmap? {
            if (leftBitmap == null || leftBitmap.isRecycled
                || rightBitmap == null || rightBitmap.isRecycled
            ) {
                Log.w(
                    this.javaClass.name,
                    "leftBitmap=$leftBitmap;rightBitmap=$rightBitmap"
                )
                return null
            }
            var height = 0 // 拼接后的高度，按照参数取大或取小
            height = if (isBaseMax) {
                if (leftBitmap.height > rightBitmap.height) leftBitmap.height else rightBitmap.height
            } else {
                if (leftBitmap.height < rightBitmap.height) leftBitmap.height else rightBitmap.height
            }

            // 缩放之后的bitmap
            var tempBitmapL: Bitmap = leftBitmap
            var tempBitmapR: Bitmap = rightBitmap
            if (leftBitmap.height != height) {
                tempBitmapL = Bitmap.createScaledBitmap(
                    leftBitmap,
                    (leftBitmap.width * 1f / leftBitmap.height * height).toInt(),
                    height,
                    false
                )
            } else if (rightBitmap.height != height) {
                tempBitmapR = Bitmap.createScaledBitmap(
                    rightBitmap,
                    (rightBitmap.width * 1f / rightBitmap.height * height).toInt(),
                    height,
                    false
                )
            }

            // 拼接后的宽度
            val width = tempBitmapL.width + tempBitmapR.width

            // 定义输出的bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // 缩放后两个bitmap需要绘制的参数
            val leftRect = Rect(0, 0, tempBitmapL.width, tempBitmapL.height)
            val rightRect = Rect(0, 0, tempBitmapR.width, tempBitmapR.height)

            // 右边图需要绘制的位置，往右边偏移左边图的宽度，高度是相同的
            val rightRectT = Rect(tempBitmapL.width + spacing, 0, width, height)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(tempBitmapL, leftRect, leftRect, null)
            canvas.drawBitmap(tempBitmapR, rightRect, rightRectT, null)
            return bitmap
        }

        fun mergeBitmapTB(
            topBitmap: Bitmap,
            bottomBitmap: Bitmap,
            spacing: Int = 0
        ): Bitmap? {
            if (topBitmap.isRecycled || bottomBitmap.isRecycled
            ) {
                Log.e(
                    this.javaClass.name,
                    "leftBitmap=$topBitmap;rightBitmap=$bottomBitmap"
                )
                return null
            }
            val height = topBitmap.height + bottomBitmap.height // 拼接后的高度，按照参数取大或取小

            // 拼接后的宽度
            val width =
                if (topBitmap.width > bottomBitmap.width) topBitmap.width else bottomBitmap.width

            // 定义输出的bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // 缩放后两个bitmap需要绘制的参数
            val topRect = Rect(0, 0, topBitmap.width, topBitmap.height)
            val bottomRect = Rect(0, 0, bottomBitmap.width, bottomBitmap.height)

            // 右边图需要绘制的位置，往右边偏移左边图的宽度，高度是相同的
            val topLeft = (width - topBitmap.width) / 2
            val topRectT = Rect(topLeft, 0, topBitmap.width, topBitmap.height)
            val bottomLeft = (width - bottomBitmap.width) / 2
            val bottomRectT = Rect(bottomLeft, topBitmap.height + spacing, width, height)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(topBitmap, topRect, topRectT, null)
            canvas.drawBitmap(bottomBitmap, bottomRect, bottomRectT, null)
            return bitmap
        }

        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }

        fun pxTodp(px: Int): Int {
            return (px / Resources.getSystem().displayMetrics.density).toInt()
        }

        fun getBlackWhiteBitmap(bitmap: Bitmap): Bitmap? {
            val w = bitmap.width
            val h = bitmap.height
            val resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
            var color = 0
            var a: Int
            var r: Int
            var g: Int
            var b: Int
            var r1: Int
            var g1: Int
            var b1: Int
            val oldPx = IntArray(w * h)
            val newPx = IntArray(w * h)
            bitmap.getPixels(oldPx, 0, w, 0, 0, w, h)
            for (i in 0 until w * h) {
                color = oldPx[i]
                r = Color.red(color)
                g = Color.green(color)
                b = Color.blue(color)
                a = Color.alpha(color)
                //黑白矩阵
                r1 = (0.33 * r + 0.59 * g + 0.11 * b).toInt()
                g1 = (0.33 * r + 0.59 * g + 0.11 * b).toInt()
                b1 = (0.33 * r + 0.59 * g + 0.11 * b).toInt()
                //检查各像素值是否超出范围
                if (r1 > 255) {
                    r1 = 255
                }
                if (g1 > 255) {
                    g1 = 255
                }
                if (b1 > 255) {
                    b1 = 255
                }
                newPx[i] = Color.argb(a, r1, g1, b1)
            }
            resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h)
            return getGreyBitmap(resultBitmap)
        }

        private fun getGreyBitmap(bitmap: Bitmap?): Bitmap? {
            if (bitmap == null) return null
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val gray = IntArray(height * width)
            var e: Int
            var i: Int
            var j: Int
            var g: Int
            e = 0
            while (e < height) {
                i = 0
                while (i < width) {
                    j = pixels[width * e + i]
                    g = j and 16711680 shr 16
                    gray[width * e + i] = g
                    ++i
                }
                ++e
            }
            i = 0
            while (i < height) {
                j = 0
                while (j < width) {
                    g = gray[width * i + j]
                    if (g >= 128) {
                        pixels[width * i + j] = -1
                        e = g - 255
                    } else {
                        pixels[width * i + j] = -16777216
                        e = g - 0
                    }
                    if (j < width - 1 && i < height - 1) {
                        gray[width * i + j + 1] += 3 * e / 8
                        gray[width * (i + 1) + j] += 3 * e / 8
                        gray[width * (i + 1) + j + 1] += e / 4
                    } else if (j == width - 1 && i < height - 1) {
                        gray[width * (i + 1) + j] += 3 * e / 8
                    } else if (j < width - 1 && i == height - 1) {
                        gray[width * i + j + 1] += e / 4
                    }
                    ++j
                }
                ++i
            }
            val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            mBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return mBitmap
        }

        fun PrintDiskImagefile(bitmap: Bitmap): ByteArray? {
            val bytes: ByteArray
            val width = bitmap.width
            val heigh = bitmap.height
            val iDataLen = width * heigh
            val pixels = IntArray(iDataLen)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, heigh)
            bytes = PrintDiskImagefile(pixels, width, heigh)
            return bytes
        }

        private fun PrintDiskImagefile(pixels: IntArray, iWidth: Int, iHeight: Int): ByteArray {
            var iBw = iWidth / 8
            val iMod = iWidth % 8
            if (iMod > 0) iBw = iBw + 1
            val iDataLen = iBw * iHeight
            val bCmd = ByteArray(iDataLen + 8)
            var iIndex = 0
            bCmd[iIndex++] = 0x1D
            bCmd[iIndex++] = 0x76
            bCmd[iIndex++] = 0x30
            bCmd[iIndex++] = 0x0
            bCmd[iIndex++] = iBw.toByte()
            bCmd[iIndex++] = (iBw shr 8).toByte()
            bCmd[iIndex++] = iHeight.toByte()
            bCmd[iIndex++] = (iHeight shr 8).toByte()
            var iValue1 = 0
            var iValue2 = 0
            var iRow = 0
            var iCol = 0
            var iW = 0
            var iValue3 = 0
            var iValue4 = 0
            iRow = 0
            while (iRow < iHeight) {
                iCol = 0
                while (iCol < iBw - 1) {
                    iValue2 = 0
                    iValue1 = pixels[iW++]
                    if (iValue1 < -1) iValue2 = iValue2 + 0x80
                    iValue1 = pixels[iW++]
                    if (iValue1 < -1) iValue2 = iValue2 + 0x40
                    iValue1 = pixels[iW++]
                    if (iValue1 < -1) iValue2 = iValue2 + 0x20
                    iValue1 = pixels[iW++]
                    if (iValue1 < -1) iValue2 = iValue2 + 0x10
                    iValue1 = pixels[iW++]
                    if (iValue1 < -1) iValue2 = iValue2 + 0x8
                    iValue1 = pixels[iW++]
                    if (iValue1 < -1) iValue2 = iValue2 + 0x4
                    iValue1 = pixels[iW++]
                    if (iValue1 < -1) iValue2 = iValue2 + 0x2
                    iValue1 = pixels[iW++]
                    if (iValue1 < -1) iValue2 = iValue2 + 0x1
                    if (iValue3 < -1) // w1
                        iValue4 = iValue4 + 0x10
                    bCmd[iIndex++] = iValue2.toByte()
                    iCol++
                }
                iValue2 = 0
                if (iValue4 > 0) // w2
                    iValue3 = 1
                if (iMod == 0) {
                    iCol = 8
                    while (iCol > iMod) {
                        iValue1 = pixels[iW++]
                        if (iValue1 < -1) iValue2 = iValue2 + (1 shl iCol)
                        iCol--
                    }
                } else {
                    iCol = 0
                    while (iCol < iMod) {
                        iValue1 = pixels[iW++]
                        if (iValue1 < -1) iValue2 = iValue2 + (1 shl 8 - iCol)
                        iCol++
                    }
                }
                bCmd[iIndex++] = iValue2.toByte()
                iRow++
            }
            return bCmd
        }

    }
}