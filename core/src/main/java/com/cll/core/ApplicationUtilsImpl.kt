package com.cll.core

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.StatFs
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ApplicationUtilsImpl : ApplicationUtils {


    companion object {
        const val TAG = "ApplicationUtils"
    }

    override var mCurrentContext: Context? = null

    override fun createImageFile(pattern: String?): File? {
        mCurrentContext?.let { context ->
            val fPattern = pattern ?: context.resources?.getString(R.string.yyyyMMdd_HHmmss)
            val timeStamp: String = SimpleDateFormat(fPattern, Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir: File =
                mCurrentContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    ?: return null
            return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
        }
        Log.d(TAG, "---!!null context!!---")
        return null
    }

    override fun scaleImage(path: String, targetW: Int, targetH: Int): Bitmap? {
        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(path, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        return BitmapFactory.decodeFile(path, bmOptions)
    }

    override fun showDialog(
        title: String,
        message: String,
        onNegativeClick: (() -> Unit)?,
        onPositiveClick: (() -> Unit)?
    ) {
        mCurrentContext?.let { context ->

            val builder = MaterialAlertDialogBuilder(context).setTitle(title).setMessage(message)
            if (onNegativeClick != null) {
                builder.setNegativeButton(context.getText(R.string.decline)) { dialog, _ ->
                    dialog.dismiss()
                    onNegativeClick.invoke()
                }
            }
            if (onPositiveClick != null) {
                builder.setPositiveButton(context.getText(R.string.accept)) { dialog, _ ->
                    dialog.dismiss()
                    onPositiveClick.invoke()
                }
            }

            builder.show()
        }
        Log.d(TAG, "---!!null context!!---")
    }

    override fun appToast(message: String, duration: Int) {
        mCurrentContext?.let {
            Toast.makeText(it, message, duration).show()
        }
        Log.d(TAG, "---!!null context!!---")
    }

    override fun appSnackBar(contextView: View, message: String, duration: Int) {
        return Snackbar.make(contextView, message, duration).show()
    }

    override fun appSnackBarAction(
        contextView: View,
        message: String,
        duration: Int,
        actionTitle: String,
        onclickAction: View.OnClickListener
    ) {
        return Snackbar.make(contextView, message, duration).setAction(actionTitle, onclickAction)
            .show()
    }

    override fun closeKeyBoard(activity: Activity) {
        val imm:InputMethodManager =
            mCurrentContext?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun queryForFreeSpace():Long {
        val internalStorage = mCurrentContext!!.filesDir.path
        val stat = StatFs(mCurrentContext!!.filesDir.path)
        val availableBlocks: Long = stat.availableBlocksLong
        return stat.blockSizeLong * availableBlocks
    }

    //    private var resultLauncher =
    //        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    //            if (result.resultCode == Activity.RESULT_OK) {
    ////                val w = binding.imageV.width
    ////                val h = binding.imageV.height
    ////                scaleImage(pathv!!, w, h)?.let {
    ////                    binding.imageV.setImageBitmap(it)
    ////                }
    //            }
    //
    //        }
    //
    //    var pathv: String? = ""
    //
    //    private fun dispatchTakePictureIntent() {
    //        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
    //            // Ensure that there's a camera activity to handle the intent
    //
    //            takePictureIntent.resolveActivity(requireActivity().packageManager)
    //            // Create the File where the photo should go
    //            val photoFile: File? = try {
    //                createImageFile()
    //            } catch (ex: IOException) {
    //                // Error occurred while creating the File
    //                showDialog("Error", "message", {})
    //                null
    //            }
    //            // Continue only if the File was successfully created
    //            pathv = photoFile?.absolutePath
    //            photoFile?.also {
    //                val photoURI: Uri = FileProvider.getUriForFile(
    //                    requireContext(), "com.example.android.fileprovider", it
    //                )
    //                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    //                resultLauncher.launch(takePictureIntent)
    //            }
    //
    //        }
    //    }
}