package asgarov.elchin.econvis.utils

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


object SnackbarUtils {
    fun showCustomSnackbar(view: View, message: String, backgroundColorResId: Int) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)

        // Customize the Snackbar layout
        val snackbarView = snackbar.view

        // Set the background color
        snackbarView.setBackgroundColor(ContextCompat.getColor(view.context, backgroundColorResId))

        // Center the text and move Snackbar to top
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER_HORIZONTAL

        // Move Snackbar to the top
        val layoutParams = snackbarView.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP
        snackbarView.layoutParams = layoutParams

        snackbar.show()
    }

    private fun convertDpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }
}