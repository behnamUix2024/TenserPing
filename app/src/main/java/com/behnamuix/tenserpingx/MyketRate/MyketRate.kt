package com.behnamuix.tenserpingx.MyketRate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.behnamuix.tenserpingx.R
import androidx.core.net.toUri

class MyketRate(val ctx: Context){
    fun showRateDialog() {
        val builder = AlertDialog.Builder(ctx, R.style.cardAlertDialog)
        builder.setTitle(ctx.getString(R.string.rate_us_title))
        builder.setPositiveButton(ctx.getString(R.string.rate_us_positive)) { _, _ ->
            openMyketForRating()
        }
        builder.setNegativeButton(ctx.getString(R.string.cancel_text)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }



    fun openMyketForRating() {
        val packageName = ctx.packageName
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "myket://comment?id=$packageName".toUri()
            setPackage("ir.mservices.market")
        }

        if (intent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(intent)
        } else {
            ctx.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://myket.ir/app/$packageName".toUri()
                )
            )
        }
    }
}