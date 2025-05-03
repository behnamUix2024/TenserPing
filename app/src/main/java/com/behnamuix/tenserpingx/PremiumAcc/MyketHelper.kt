package com.behnamuix.tenserpingx.PremiumAcc

import android.app.Activity
import android.content.Context
import android.util.Log
import ir.myket.billingclient.IabHelper
import ir.myket.billingclient.util.Purchase

class MyketHelper(private val ctx: Context) {
    private lateinit var iabHelper: IabHelper
    private var isServiceConnected = false

    companion object {
        //const val SKU_PREM_ACC = "Hist_chart"
        const val SKU_PREM_ACC = "android.test.purchased"

        const val BASE64_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwwsFIXlqefMxrOUl3//fNAvng3lKqfw4kCGbdeDXbp2oRg8z3PZ+Fvr0INk0mcZ3WMptSW/0a+rHv1PLB/zNxDn6vPbd1TR3bc4bCFi96xHEPVhlPCyss2u26yvBB+EMvEKzZZ96lANUFU4Y1mR7j7icF5XKYA99UVJO68cgPFQIDAQAB"
    }

    fun setupBiling(setupCompleted: (Boolean) -> Unit) {
        iabHelper = IabHelper(ctx, BASE64_KEY)
        iabHelper.enableDebugLogging(true)
        iabHelper.startSetup { result ->
            if (result.isSuccess) {
                isServiceConnected = true
                Log.d("Biling", "Service Activated!")
                setupCompleted(true)
            } else {
                Log.d("Biling", "Error in Service running! ")
                setupCompleted(false)

            }
        }
    }

    fun purchasedItem(activity: Activity, sku: String, callback: (Boolean) -> Unit) {
        if (!isServiceConnected) {
            Log.d("kir","error")
            callback(false)
            return
        }
        iabHelper.launchPurchaseFlow(
            activity,
            sku,
            "1001",
            { result, purchase ->
                if (result.isSuccess &&
                     purchase!= null

                ) {
                    Log.d("kir","ok")
                    //پرداخت موفق
                    verifyAndConsumePurchase(purchase, callback)
                } else {
                    //پرداخت ناموفق
                    Log.d("kir","error2")
                    callback(false)
                }
            }, "payload-${System.currentTimeMillis()}"
        )
    }

    fun verifyAndConsumePurchase(purchase: Purchase, callback: (Boolean) -> Unit) {
        iabHelper.consumeAsync(purchase) { result, _ ->
           try{
               val success=when{
                   result::class.java.getMethod("isSuccess").invoke(result) as Boolean->true
                   else->false
               }
               callback(success)
           }catch (e:Exception){
               Log.e("kir","error in data usage!")
               callback(false)
           }
        }
    }

    fun dispose() {
        if (::iabHelper.isInitialized) {
            iabHelper.dispose()
        }
    }

}
