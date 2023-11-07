package com.example.iminissue

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 ** © Copyright 2020. KUANGLI. All Rights Reserved.
 ** Project: poslib
 ** Package: com.kuangli.lib.pos.model.service.shoppingCar
 ** File: TaiwanInvoiceModel
 ** Create Date: 2020-08-09 13:52
 ** Author: Bale Lin
 ** Description:
 **
 ** Revision History:
 ** Date     Author      Ref     Revision(Date in YYYY-MM-DD format)
 **
 **
 **/
class TaiwanInvoiceModel(
    @SerializedName("StoreName")
    val StoreName: String,
    @SerializedName("InvoiceDateTitle")
    val InvoiceDateTitle: String,
    @SerializedName("InvoiceNumber")
    val InvoiceNumber: String,
    @SerializedName("InvoiceDate")
    val InvoiceDate: String,
    @SerializedName("RandomCode")
    val RandomCode: String,
    @SerializedName("TotalAmount")
    val TotalAmount: String,
    @SerializedName("CompanyID")
    val CompanyID: String,
    @SerializedName("BarcodeData")
    val BarcodeData: String,
    @SerializedName("QRCode_Left")
    val QRCode_Left: String,
    @SerializedName("QRCode_Right")
    val QRCode_Right: String,
    @SerializedName("BuyerID")
    val BuyerID: String
) : Serializable