package com.behnamuix.tenserpingx.MyTools
import android.app.Activity
import androidx.core.content.res.ResourcesCompat
import com.behnamuix.tenserpingx.R
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class MoToast(private val activity: Activity) {

    fun MoSuccess(title: String="مهم", msg: String) {
        MotionToast.darkColorToast(
            activity,
            title,
            msg,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(activity, R.font.vazir_fd_wol)
        )
    }

    fun MoWarning(title: String="هشدار", msg: String) {
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

    fun MoInfo(title: String="توجه", msg: String) {
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

    fun MoError(title: String="خطا", msg: String) {
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
    fun MoDelete(title: String="هشدار از دست دادن داده", msg: String) {
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
