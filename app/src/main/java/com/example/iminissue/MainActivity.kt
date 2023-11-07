package com.example.iminissue

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var printing: PrintBusiness
    private lateinit var qrcode80Button: Button
    private lateinit var qrcode58Button: Button
    private lateinit var invoiceBufferButton: Button
    private lateinit var invoiceButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        printing = PrintBusiness(this)
    }

    override fun onStart() {
        super.onStart()
        qrcode80Button = findViewById(R.id.qrCode80Button)
        qrcode80Button.setOnClickListener(this)
        qrcode58Button = findViewById(R.id.qrCode58Button)
        qrcode58Button.setOnClickListener(this)
        invoiceBufferButton = findViewById(R.id.taiwanInvoiceBufferButton)
        invoiceBufferButton.setOnClickListener(this)
        invoiceButton = findViewById(R.id.taiwanInvoiceButton)
        invoiceButton.setOnClickListener(this)
        val productNameView = findViewById<TextView>(R.id.productName)
        val name = "${Build.MANUFACTURER} ${Build.PRODUCT}"
        productNameView.text = name
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.qrCode80Button -> {
                qrcode80Button.isEnabled = false
                onPrintingQRCode(80)
            }

            R.id.qrCode58Button -> {
                qrcode58Button.isEnabled = false
                onPrintingQRCode(58)
            }

            R.id.taiwanInvoiceBufferButton -> {
                invoiceBufferButton.isEnabled = false
                onPrintingInvoice(true)

            }

            R.id.taiwanInvoiceButton -> {
                invoiceButton.isEnabled = false
                onPrintingInvoice(true)

            }
        }
    }

    private fun onPrintingQRCode(mm: Int) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val isSuccess = printing.printQRCode(mm)
            this@MainActivity.runOnUiThread {
                qrcode80Button.isEnabled = true
                qrcode58Button.isEnabled = true
                Toast.makeText(this, "Printing -> $isSuccess", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPrintingInvoice(isBuffer: Boolean) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val isSuccess = printing.printTaiwanInvoice(isBuffer)
            this@MainActivity.runOnUiThread {
                invoiceButton.isEnabled = true
                invoiceBufferButton.isEnabled = true
                Toast.makeText(this, "Printing -> $isSuccess", Toast.LENGTH_SHORT).show()
            }
        }
    }
}