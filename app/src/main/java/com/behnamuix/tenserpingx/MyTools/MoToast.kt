package com.behnamuix.tenserpingx.MyTools

import android.app.Activity
import androidx.core.content.res.ResourcesCompat
import com.behnamuix.tenserpingx.R
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class MoToast(private val activity: Activity) {

    fun MoSuccess(msg: String, title: String = activity.getString(R.string.default_important_title)) {
        MotionToast.darkColorToast(
            activity,
            title,
            msg,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_CENTER,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(activity, R.font.vazir_fd_wol)
        )
    }

    fun MoWarning(msg: String, title: String = activity.getString(R.string.default_warning_title)) {
        MotionToast.darkColorToast(
            activity,
            title,
            msg,
            MotionToastStyle.WARNING,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(activity, R.font.vazir_fd_wol)
        )
    }

    fun MoInfo(msg: String, title: String = activity.getString(R.string.default_info_title)) {
        MotionToast.darkColorToast(
            activity,
            title,
            msg,
            MotionToastStyle.INFO,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(activity, R.font.vazir_fd_wol)
        )
    }

    fun MoError(msg: String, title: String = activity.getString(R.string.default_error_title)) {
        MotionToast.darkColorToast(
            activity,
            title,
            msg,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(activity, R.font.vazir_fd_wol)
        )
    }

    fun MoDelete(msg: String, title: String = activity.getString(R.string.default_delete_title)) {
        MotionToast.darkColorToast(
            activity,
            title,
            msg,
            MotionToastStyle.DELETE,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(activity, R.font.vazir_fd_wol)
        )
    }
}

