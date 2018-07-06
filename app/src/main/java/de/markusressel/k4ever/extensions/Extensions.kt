package de.markusressel.k4ever.extensions

import android.content.Context
import android.os.AsyncTask
import android.support.v4.app.Fragment
import de.markusressel.k4ever.R
import java.util.*

/**
 * Created by Markus on 15.02.2018.
 */
fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start

/**
 * Returns true if the current device is considered a tablet
 */
fun Context.isTablet(): Boolean {
    return resources
            .getBoolean(R.bool.is_tablet)
}

fun Any.doAsync(handler: () -> Unit) {
    object : AsyncTask<Void, Void, Void?>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            handler()
            return null
        }
    }
            .execute()
}

fun Throwable.prettyPrint(): String {
    val message = "${this.message}:\n" + "${this.stackTrace.joinToString(separator = "\n")}}"

    return message
}

fun Fragment.context(): Context {
    return this.context as Context
}

fun Float.pxToSp(context: Context): Float {
    return this / context.resources.displayMetrics.scaledDensity
}