package com.cll.core

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import java.io.File
import java.io.IOException

interface ApplicationUtils {

    var mCurrentContext: Context?

    @Throws(IOException::class)
    fun createImageFile(pattern: String? = null): File?

    fun scaleImage(
        path: String, targetW: Int, targetH: Int
    ): Bitmap?

    fun showDialog(
        title: String,
        message: String,
        onNegativeClick: (() -> Unit)? = null,
        onPositiveClick: (() -> Unit)? = null,
    )

    fun appToast(message: String, duration: Int)

    fun appSnackBar(contextView: View, message: String, duration: Int)

    fun appSnackBarAction(
        contextView: View,
        message: String,
        duration: Int,
        actionTitle: String,
        onclickAction: View.OnClickListener
    )

    fun closeKeyBoard(activity: Activity)

    fun queryForFreeSpace():Long

}