package com.example.stsonepaydemo

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.edesign.paymentsdk.Inquiry.InquiryRequest
import com.edesign.paymentsdk.Inquiry.InquiryResponse
import com.edesign.paymentsdk.Refund.RefundRequest
import com.edesign.paymentsdk.Refund.RefundResponse
import com.edesign.paymentsdk.version2.completion.CompletionCallback
import com.edesign.paymentsdk.version2.completion.CompletionRequest
import com.edesign.paymentsdk.version2.completion.CompletionResponse
import com.edesign.paymentsdk.version2.completion.SmartRouteCompletionService
import com.edesign.paymentsdk.version2.inquiry.InquiryCallback
import com.edesign.paymentsdk.version2.inquiry.SmartRouteInquiryService
import com.edesign.paymentsdk.version2.refund.RefundCallback
import com.edesign.paymentsdk.version2.refund.SmartRouteRefundService

import com.example.stsonepaydemo.databinding.ActivityOtherApiBinding
import com.google.gson.Gson

class OtherApiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtherApiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtherApiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnInquiry.setOnClickListener {
            inquiry()
        }


        binding.btnCompletion.setOnClickListener {
            completion()
        }

        binding.btnRefund.setOnClickListener {

            refund()
        }


    }


    fun refund(){
        var request= RefundRequest()
        request.setPaymentAuthenticationToken("AuthenticationToken","MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh")
        request.add("MessageID","4")
        request.add("MerchantID","AirrchipMerchant")
        request.add("TransactionID",System.currentTimeMillis().toString())
        request.add("CurrencyISOCode",binding.tilCurrency.editText?.text.toString())
        request.add("Amount",binding.tilAmount.editText?.text.toString())
        request.add("OriginalTransactionID",binding.tilOriginalTransactionId.editText?.text.toString())
        request.add("Version","1.0")
        var paymentService = SmartRouteRefundService(this)
        paymentService.process(request,object : RefundCallback {
            override fun onResponse(response: RefundResponse) {
                binding.textResponse.setText("Response: \n"+ Gson().toJson(response))
            }
        })
    }

    fun completion(){

        var request= CompletionRequest()
        request.setPaymentAuthenticationToken("AuthenticationToken","MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh")
        request.add("MessageID","5")
        request.add("MerchantID","AirrchipMerchant")
        request.add("TransactionID",System.currentTimeMillis().toString())
        request.add("CurrencyISOCode",binding.tilCurrency.editText?.text.toString())
        request.add("Amount",binding.tilAmount.editText?.text.toString())
        request.add("OriginalTransactionID",binding.tilOriginalTransactionId.editText?.text.toString())
//        request.add("Version","1.0")
        var paymentService = SmartRouteCompletionService(this)
        paymentService.process(request,object : CompletionCallback {
            override fun onResponse(response: CompletionResponse) {
                binding.textResponse.setText("Response: \n"+ Gson().toJson(response))
            }
        })
    }

    fun inquiry(){
        var request= InquiryRequest()
        request.setPaymentAuthenticationToken("AuthenticationToken","MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh")
        request.add("MessageID","2")
        request.add("MerchantID","AirrchipMerchant")
        request.add("OriginalTransactionID",binding.tilOriginalTransactionId.editText?.text.toString())
        request.add("Version","1.0")
        var paymentService = SmartRouteInquiryService(this)
        paymentService.process(request,object : InquiryCallback {
            override fun onResponse(response: InquiryResponse) {
                binding.textResponse.setText("Response: \n"+ Gson().toJson(response))
            }
        })
    }


}