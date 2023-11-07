package com.example.iminissue

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.imin.printerlib.IminPrintUtils
import com.google.gson.Gson
import com.imin.printerlib.interfaces.PrintResultCallback
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 ** © Copyright 2023. KUANGLI. All Rights Reserved.
 ** Project: IminIssue
 ** Package: com.example.iminissue
 ** File: PrintBusiness
 ** Create Date: 2023/8/9 18:07
 ** Author: Bale Lin
 ** Description:
 **
 ** Revision History:
 ** Date     Author      Ref     Revision(Date in YYYY-MM-DD format)
 **
 **
 **/
class PrintBusiness(private val context: Context) {
    private lateinit var pos: IminPrintUtils
    private val DEFAULT_TEXT_SIZE = 24
    private val MIDDLE_TEXT_SIZE = 50
    private val LARGE_TEXT_SIZE = 60

    init {
        openPrinter()
    }

    private var printingResult = false
    private var printingLatch: CountDownLatch? = null

    fun printTaiwanInvoice(isBuffer: Boolean): Boolean {
        pos.setPageFormat(1) //58mm
        printingResult = false
        if (isBuffer) {
            printingLatch = CountDownLatch(1)
            pos.enterPrinterBuffer(true)
        }
        layoutCompany()
        layoutInvoice()
        layoutContent()
        layoutBarCode()
        layoutTaiwanQRCode()
        layoutFooted()
        pos.printAndFeedPaper(100)
        pos.partialCut()
        if (isBuffer) {
            pos.exitPrinterBuffer(true, printingBufferCallback)
            printingLatch?.await(5, TimeUnit.SECONDS)
        } else {
            printingResult = true
        }
        return printingResult
    }

    private fun layoutCompany() {
        pos.setAlignment(1)
        pos.setTextSize(40)
        pos.setTextStyle(1)
        pos.printText("紅茶巴士總店\n")
    }

    private fun layoutInvoice() {
        pos.setAlignment(1)
        pos.setTextSize(45)
        pos.setTextStyle(1)
        val invoiceTitle = "電子發票證明聯"
        pos.printText("$invoiceTitle\n")
        pos.setAlignment(1)
        pos.setTextSize(50)
        pos.setTextStyle(1)
        pos.printText("112年1-12月\n")
        pos.printText("VP-97873565\n")
    }


    private fun layoutContent() {
        pos.setAlignment(0)
        pos.setTextSize(DEFAULT_TEXT_SIZE)
        pos.setTextStyle(0)
        pos.printText("2023-11-07 11:47:25\n")
        pos.printText("隨機碼:0817        總計:3000\n")
        pos.printText("賣方:93368593   買方:93364075\n")
    }


    private fun layoutBarCode() {
        val bitmap =
            BarcodeUtility.createBarCode39Bitmap("10404UZ176908720122", 384, 50)
                ?: return
        val blackWhiteBitmap = BitmapUtility.getBlackWhiteBitmap(bitmap)
        pos.printSingleBitmap(blackWhiteBitmap, 1)
//        saveToFile(taiwanInvoiceModel.BarcodeData, blackWhiteBitmap)
//        pos.setBarCodeHeight(40)
//        pos.setBarCodeContentPrintPos(0)
//        pos.setBarCodeWidth(3)
//        pos.printBarCode(4, taiwanInvoiceModel.BarcodeData, 1)
    }

    private fun layoutFooted() {
        pos.setAlignment(1)
        pos.setTextSize(DEFAULT_TEXT_SIZE)
        pos.setTextStyle(0)
        pos.printText("2023045A5999\n")
    }

    fun printQRCode(mm: Int): Boolean {
        if (mm == 80)
            pos.setPageFormat(0) //80mm
        else
            pos.setPageFormat(1) //58mm
        printingResult = false
        printingLatch = CountDownLatch(1)
        pos.enterPrinterBuffer(true)
        layoutLogo()
        layoutLine("*", 56)
        layoutReceiptName()
        layoutLine("*", 56)
        layoutCustomer()
        layoutLine("-")
        layoutNumber()
        layoutQRCode()
        layoutTaiwanQRCode()
        layoutFootRemark()
        pos.printAndLineFeed()
        pos.printAndFeedPaper(150)
        pos.partialCut()
        pos.exitPrinterBuffer(true, printingBufferCallback)
        printingLatch?.await(5, TimeUnit.SECONDS)
        return printingResult
    }

    private val printingBufferCallback = PrintResultCallback {
        printingResult = it == 0
        Log.e("Printing RESULT", "Code:$it")
        printingLatch?.countDown()
    }

    private fun layoutLogo() {
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
        pos.setAlignment(1)
        pos.setTextSize(50)
        pos.setTextStyle(1)
        pos.printSingleBitmap(BitmapUtility.getBlackWhiteBitmap(logoBitmap), 1)
    }

    private fun openPrinter() {
        pos = IminPrintUtils.getInstance(context)
        pos.resetDevice()
        pos.initPrinter(IminPrintUtils.PrintConnectType.USB)
    }

    private fun layoutLine(lineStr: String, length: Int = 80) {
        val newLength = length - 10
        var line = ""
        for (i in 0..newLength)
            line += lineStr
        pos.setAlignment(0)
        pos.setTextSize(DEFAULT_TEXT_SIZE)
        pos.setTextStyle(0)
        pos.printText("$line\r\n")
    }

    private fun layoutReceiptName() {
        pos.setAlignment(1)
        pos.setTextSize(LARGE_TEXT_SIZE)
        pos.setTextStyle(1)
        pos.printText("SCAN QR CODE\r\n")
    }

    private fun layoutCustomer() {
        pos.setAlignment(1)
        pos.setTextSize(MIDDLE_TEXT_SIZE)
        pos.setTextStyle(1)
        pos.printText("Table: 5\r\n")
        val dateTime = Calendar.getInstance(Locale.ENGLISH).time.toString(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH
        )
        pos.printText("$dateTime\r\n")
        pos.printText("Pax:6\r\n")
    }

    private fun layoutNumber() {
        pos.setAlignment(2)
        pos.setTextSize(DEFAULT_TEXT_SIZE)
        pos.setTextStyle(1)
        pos.printText("RM 5.00\r\n")
        pos.setAlignment(2)
        pos.setTextSize(MIDDLE_TEXT_SIZE)
        pos.setTextStyle(1)
        pos.printText("RM 5.00\r\n")
        pos.setAlignment(2)
        pos.setTextSize(LARGE_TEXT_SIZE)
        pos.setTextStyle(1)
        pos.printText("RM 5.00\r\n")
    }

    private fun layoutQRCode() {
        pos.setAlignment(1)
        pos.setTextSize(DEFAULT_TEXT_SIZE)
        pos.setTextStyle(1)
        pos.setQrCodeSize(6)
        pos.printQrCode("https://play.google.com/store/apps/details?id=com.kuangli.glm", 1)
        pos.printText(" \r\n")
    }

    private fun createTaiwanInvoice(): TaiwanInvoiceModel {
        val jsonInvoice =
            "{\"BarcodeData\":\"11002AA000001407572\",\"BuyerID\":\"88888888\",\"CompanyID\":\"12345678\",\"InvoiceDate\":\"2021-02-17 22:20:33\",\"InvoiceDateTitle\":\"110年01-02月\",\"InvoiceNumber\":\"AA-00000140\",\"QRCode_Left\":\"AA0000014011002177572000000390000003c12345678kkhIHms0mAOdyG4onNdRkw\\u003d\\u003d:**********:3:3:1:\",\"QRCode_Right\":\"原味炸雞-小:1:60:\",\"RandomCode\":\"7572\",\"StoreName\":\"鄺聯企業有限公司\",\"TotalAmount\":\"60\"}"
        return Gson().fromJson(jsonInvoice, TaiwanInvoiceModel::class.java)
    }

    private fun layoutTaiwanQRCode() {
        val taiwanInvoiceModel = createTaiwanInvoice()
        val qrCodeBitmap = BarcodeUtility.createTaiwanInvoiceQRCodeBitmap(
            taiwanInvoiceModel.QRCode_Left, taiwanInvoiceModel.QRCode_Right
        ) ?: return
        pos.printSingleBitmap(BitmapUtility.getBlackWhiteBitmap(qrCodeBitmap), 1)
    }

    private fun layoutFootRemark() {
        pos.setAlignment(0)
        pos.setTextSize(DEFAULT_TEXT_SIZE)
        pos.setTextStyle(0)
        pos.printText("请扫描QR Code 进行点餐，我们马上为您服务！Please scan the QR Code to place your order, thank you very much!")

    }

    private fun Date.toString(format: String, locale: Locale = Locale.ENGLISH): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
}