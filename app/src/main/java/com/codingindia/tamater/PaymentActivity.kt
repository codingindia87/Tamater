package com.codingindia.tamater

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codingindia.tamater.utils.Constents.APP_ICON_URL
import com.codingindia.tamater.utils.Constents.KEY
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Checkout.preload(applicationContext)
        val co = Checkout()
        co.setKeyID(KEY)

        initPayment()
    }

    private fun initPayment() {
        val activity: Activity = this
        val co = Checkout()

        val email = Firebase.auth.currentUser?.email

        try {
            val options = JSONObject()
            options.put("name", "Tamater")
            options.put("description", "Order foods")
            options.put("image", APP_ICON_URL)
            options.put("theme.color", "#F04B4B")
            options.put("currency", "INR")
//            options.put("order_id", "order_f${System.currentTimeMillis()}")
            options.put(
                "amount",
                intent.getDoubleExtra("price", 0.0).times(100)
            )//pass amount in currency subunits

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            val prefill = JSONObject()
            prefill.put("email", email)
            prefill.put("contact", "9876543210")

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            Log.d("payment-error", e.message.toString())
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        sendResult(true, p0!!)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        sendResult(false, "", p1!!)
    }

    private fun sendResult(status: Boolean, paymentId: String, error: String = "") {
        val resultIntent = Intent()
        resultIntent.putExtra("status", status)
        resultIntent.putExtra("id", paymentId)
        resultIntent.putExtra("error", error)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}