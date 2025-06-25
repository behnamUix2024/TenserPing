package com.behnamuix.metremajazi.Ads
//Created by BehnamUix @1404
import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.behnamuix.metremajazi.Model.MainModel
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
//add this:
// implementation("ir.tapsell.plus:tapsell-plus-sdk-android:2.3.2")
// to gradle
class TapsellApi(private val activity: Activity) {

    private var initResponseID = ""

    private var tapsell_key = MainModel().TAPSELL_KEY

    private var ads_zone_id = MainModel().ZONE_ID

    fun TapsellConfig() {
        TapsellPlus.initialize(activity, tapsell_key, object : TapsellPlusInitListener {
            override fun onInitializeSuccess(p0: AdNetworks?) {
                if (p0 != null) {
                    requestAds()
                    Log.d("onInitializeSuccess", p0.name);
                }

            }

            override fun onInitializeFailed(p0: AdNetworks?, p1: AdNetworkError?) {
                if (p0 != null) {
                    Log.e(
                        "onInitializeFailed",
                        "ad network: " + p0.name + ", error: " + p1?.errorMessage.toString()
                    )
                };
            }

        })
        TapsellPlus.setDebugMode(Log.DEBUG)

    }

    private fun requestAds() {
        TapsellPlus.requestInterstitialAd(
            activity,
            ads_zone_id,
            object : AdRequestCallback() {
                override fun response(p0: TapsellPlusAdModel?) {
                    super.response(p0)
                    if (p0 != null) {
                        initResponseID = p0.responseId
                        Toast.makeText(activity, "Ok+${initResponseID}+Now show ads!", Toast.LENGTH_SHORT).show()
                        showAds(initResponseID)
                    }

                }

                override fun error(p0: String?) {
                    super.error(p0)
                    Toast.makeText(activity, "Error+${p0.toString()}", Toast.LENGTH_SHORT).show()
                }
            }

        )
    }

    private fun showAds(InterstitialID: String) {
        TapsellPlus.showInterstitialAd(
            activity, InterstitialID,
            object : AdShowListener() {
                override fun onOpened(p0: TapsellPlusAdModel?) {
                   // Toast.makeText(activity, "تبلیغات باز شد", Toast.LENGTH_SHORT).show()
                    super.onOpened(p0)
                }

                override fun onClosed(p0: TapsellPlusAdModel?) {
                    //Toast.makeText(activity, "تبلیغات بسته  شد", Toast.LENGTH_SHORT).show()

                    super.onClosed(p0)
                }

                override fun onRewarded(p0: TapsellPlusAdModel?) {
                    super.onRewarded(p0)
                }

                override fun onError(p0: TapsellPlusErrorModel?) {
                    Toast.makeText(activity, "ارور وجود دارد!", Toast.LENGTH_SHORT).show()


                    super.onError(p0)
                }
            }

        )

    }
}