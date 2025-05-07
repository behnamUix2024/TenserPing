package com.behnamuix.tenserpingx.MyketRate

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.behnamuix.tenserpingx.R

class MyketRate(val ctx: Context){
     fun showRateDialog() {
        val builder = AlertDialog.Builder(ctx, R.style.cardAlertDialog)
        builder.setTitle("آیا به ما امتیاز می‌دهید؟")
        builder.setPositiveButton("بله، امتیاز می‌دهم") { _, _ ->
            openMyketForRating()
        }
        builder.setNegativeButton("بی‌خیال") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

     fun openMyketForRating() {
        val packageName = ctx.packageName
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("myket://comment?id=$packageName")
            setPackage("ir.mservices.market")
        }

        if (intent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(intent)
        } else {
            ctx.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://myket.ir/app/$packageName")
                )
            )
        }
    }
}