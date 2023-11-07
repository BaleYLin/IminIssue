package com.example.iminissue

import android.graphics.Bitmap
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

/**
 ** © Copyright 2021. KUANGLI. All Rights Reserved.
 ** Project: lib
 ** Package: com.kuangli.lib.pos.utility
 ** File: BarcodeUtility
 ** Create Date: 4/10/21 12:38 PM
 ** Author: Bale Lin
 ** Description:
 **
 ** Revision History:
 ** Date     Author      Ref     Revision(Date in YYYY-MM-DD format)
 **
 **
 **/
class BarcodeUtility {
    companion object {
        fun createBarCode39Bitmap(data: String, width: Int, height: Int): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val multi = MultiFormatWriter()
                val bitMatrix =
                    multi.encode(data, BarcodeFormat.CODE_39, width, height)
                val barcodeEncoder = BarcodeEncoder()
                bitmap = barcodeEncoder.createBitmap(bitMatrix)
            } catch (e: WriterException) {
                Log.e(this.javaClass.name, e.stackTraceToString())
            } catch (e: IllegalArgumentException) {
                Log.e(this.javaClass.name, e.stackTraceToString())
            }
            return bitmap
        }

        fun createBarCode128Bitmap(data: String, width: Int, height: Int): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val multi = MultiFormatWriter()
                val bitMatrix =
                    multi.encode(data, BarcodeFormat.CODE_128, width, height)
                val barcodeEncoder = BarcodeEncoder()
                bitmap = barcodeEncoder.createBitmap(bitMatrix)
            } catch (e: WriterException) {
                Log.e(this.javaClass.name, e.stackTraceToString())
            } catch (e: IllegalArgumentException) {
                Log.e(this.javaClass.name, e.stackTraceToString())
            }
            return bitmap
        }

        fun createQrCodeBitmap(
            data: String, width: Int, height: Int, isChinese: Boolean = false
        ): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val hitMap = Hashtable<EncodeHintType, Any>()
                hitMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
                hitMap[EncodeHintType.CHARACTER_SET] = if (isChinese) "GB2312" else "UTF-8"
                val qrCode = QRCodeWriter()
                val bitMatrix = qrCode.encode(data, BarcodeFormat.QR_CODE, width, height, hitMap)
                val barcodeEncoder = BarcodeEncoder()
                bitmap = barcodeEncoder.createBitmap(bitMatrix)
            } catch (e: WriterException) {
                Log.e(this.javaClass.name, e.stackTraceToString())
            }
            return bitmap
        }

        fun createTaiwanInvoiceQRCodeBitmap(leftData: String, rightData: String): Bitmap? {
            var bitmapLeft = createQrCodeBitmap(leftData, 200, 200) ?: return null
            val bitmapRight = createQrCodeBitmap(rightData, 200, 200) ?: return null
            if (bitmapRight.height > bitmapLeft.height) {
                bitmapLeft = createQrCodeBitmap(leftData, bitmapRight.width, bitmapRight.height)
                    ?: return bitmapLeft
            }
            return BitmapUtility.mergeBitmap_LR(bitmapLeft, bitmapRight, false, 6)
        }
    }
}