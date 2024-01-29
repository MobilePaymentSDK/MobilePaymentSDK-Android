package com.example.stsonepaydemo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.edesign.paymentsdk.Utils.Parameters
import com.edesign.paymentsdk.version2.CardType
import com.edesign.paymentsdk.version2.Checkout
import com.edesign.paymentsdk.version2.OpenPaymentRequest
import com.edesign.paymentsdk.version2.PaymentMethod
import com.edesign.paymentsdk.version2.PaymentResultListener
import com.edesign.paymentsdk.version2.PaymentType
import com.edesign.paymentsdk.version2.viewmodel.Language


import com.example.stsonepaydemo.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.ArrayList
import java.util.StringJoiner
import java.util.UUID

class MainActivity : AppCompatActivity() , PaymentResultListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        sharedPref = getSharedPreferences("MainApp", Context.MODE_PRIVATE)

        var request = OpenPaymentRequest()

        var data = sharedPref.getString("token", null)
        val type: Type = object : TypeToken<ArrayList<String>>() {}.type
        var text: ArrayList<String>? = null
        if (!data.isNullOrEmpty()) {
            text = Gson().fromJson(data, type)
            if (!text.isNullOrEmpty()) {
                val sj = StringJoiner(",")
                for (s in text) {
                    sj.add(s)
                }
                if (sj != null) {

                    binding.tilToken.editText?.setText(sj.toString())
                }
            }
        }


        binding.typeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2){

                    0-> {
                        request.paymentType = PaymentType.SALES.name
                    }
                    1->{
                        request.paymentType = PaymentType.PREAUTH.name
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.languageSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                when(p2){

                    0-> {
                        request.add(
                            "Language", Language.AR)
                    }
                    1->{
                        request.add(
                            "Language",Language.EN)
                    }
                    2->{
                        request.add(
                            "Language",Language.TR)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


        binding.OtherAPI.setOnClickListener{

            var intent = Intent(this, OtherApiActivity::class.java)
            startActivity(intent)
        }


        binding.materialButton.setOnClickListener {


            request.add("AuthenticationToken", "MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh")
            request.add(
                "TransactionID",
                binding.tilTransactionId.editText?.text.toString().ifBlank { System.currentTimeMillis().toString() }
            )
            request.add("MerchantID", "AirrchipMerchant")
            request.add("ClientIPaddress", "3.7.21.24")
            request.add("Amount", binding.tilAmount.editText?.text.toString())
            request.add("Currency", binding.tilCurrency.editText?.text.toString())
            request.add("PaymentDescription", "Sample Payment")
            request.add("AgreementID", "") //1699652979986
            request.add("AgreementType", "")
            request.add("ThreeDSEnable", binding.is3dsSecure.isChecked)
            request.add("TokenizeCard", binding.shouldTokenizeCard.isChecked)
            request.add("CardScanningEnable", binding.isCardScanEnabled.isChecked)
            request.add("SaveCard",  binding.isSaveCardEnabled.isChecked)
            request.add("PaymentMethod", arrayListOf<String>(PaymentMethod.Cards.name))
            request.add(
                "CardType",
                arrayListOf<String>(CardType.VISA.name, CardType.MASTERCARD.name,CardType.MADA.name)
            )
//optional param
           /* request.addOptional("ItemID", "Item1")
            request.addOptional("Quantity", "1")
            request.addOptional("Version", "1.0")*/
            request.addOptional("FrameworkInfo", "Android 7.0")
            request.add("Tokens", binding.tilToken.editText?.text.toString())
            var checkout = Checkout(this, this)
            checkout.open(request)
        }


    }

    override fun onDeleteCardResponse(token: String, deleted: Boolean) {

    }

    override fun onPaymentFailed(a: MutableMap<String, String>) {
       Toast.makeText(this, Gson().toJson(a),Toast.LENGTH_LONG).show()
    }

    override fun onResponse(a: MutableMap<String, String>) {
        Toast.makeText(this, Gson().toJson(a),Toast.LENGTH_LONG).show()

        if (a.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == Parameters.SMART_ROUTE_SUCCESS) {
            if (binding.shouldTokenizeCard.isChecked && !a[Parameters.RESPONSE_TOKEN].isNullOrEmpty() && !a[Parameters.SAVE_CARD].isNullOrEmpty()
                && a[Parameters.SAVE_CARD].equals("true", true)
            ) {
                var data = sharedPref.getString("token", null)
                val type: Type = object : TypeToken<ArrayList<String>>() {}.type
                var text: ArrayList<String>? = null
                if (!data.isNullOrEmpty()) {
                    text = Gson().fromJson(data, type)
                }



                if (text.isNullOrEmpty()) {
                    var arraylist = ArrayList<String>()
                    arraylist.add(a.get(Parameters.RESPONSE_TOKEN)!!)
                    sharedPref.edit().putString("token", Gson().toJson(arraylist)).commit()
                } else {
                    var cardpresent = false
                    for (i in 0 until text!!.size) {
                        if (text[i] == a[Parameters.RESPONSE_TOKEN]) {
                            cardpresent = true
                            break
                        }
                    }
                    if (!cardpresent) {
                        text.add(a[Parameters.RESPONSE_TOKEN]!!)
                        sharedPref.edit().putString("token", Gson().toJson(text)).commit()
                    }
                }

                var data1 = sharedPref.getString("token", null)
                val type1: Type = object : TypeToken<ArrayList<String>>() {}.type
                var text1: ArrayList<String>? = null
                if (!data1.isNullOrEmpty()) {
                    text1 = Gson().fromJson(data1, type1)
                    if (!text1.isNullOrEmpty()) {
                        val sj = StringJoiner(",")
                        for (s in text1) {
                            sj.add(s)
                        }
                        if (sj != null) {

                            binding.tilToken.editText?.setText(sj.toString())
                        }
                    }
                }

            }
        }


    }


}