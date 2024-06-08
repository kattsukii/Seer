package gg.firmament.seer.core.util


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import gg.firmament.seer.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object helper {
	/**
	 * Formats a number into a more readable format with a suffix representing its magnitude.
	 * For example, 1000 becomes "1k", 1000000 becomes "1M", etc.
	 *
	 * @param number The number to format.
	 * @return A string representation of the number with a magnitude suffix.
	 */
	fun prettyCount(number: Number): String {
		val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
		val numValue = number.toLong()
		val value = floor(log10(numValue.toDouble())).toInt()
		val base = value / 3
		return if (value >= 3 && base < suffix.size) {
			DecimalFormat("#0.0").format(
				numValue / 10.0.pow((base * 3).toDouble())
			) + suffix[base]
		} else {
			DecimalFormat("#,##0").format(numValue)
		}
	}

	/**
	 * Opens a web link in the default browser.
	 *
	 * @param context The context to use.
	 * @param url The URL to open.
	 */
	fun openWebLink(context: Context, url: String) {
		val uri: Uri = Uri.parse(url)
		val intent = Intent(Intent.ACTION_VIEW, uri)
		try {
			context.startActivity(intent)
		} catch (exc: ActivityNotFoundException) {
			exc.printStackTrace()
			context.getString(R.string.error).toToast(context)
		}
	}

	/**
	 * Check if the device is running on MIUI.
	 *
	 * By default, HyperOS is excluded from the check.
	 * If you want to include HyperOS in the check, set excludeHyperOS to false.
	 *
	 * @param excludeHyperOS Whether to exclude HyperOS
	 * @return True if the device is running on MIUI, false otherwise
	 */
	fun isMiui(excludeHyperOS: Boolean = true): Boolean {
		// Return false if the device is not from Xiaomi, Redmi, or POCO.
		val brand = Build.BRAND.lowercase()
		if (!setOf("xiaomi", "redmi", "poco").contains(brand)) return false
		// Check if the device is running on MIUI and not HyperOS.
		val isMiui = !getProperty("ro.miui.ui.version.name").isNullOrBlank()
		val isHyperOS = !getProperty("ro.mi.os.version.name").isNullOrBlank()
		return isMiui && (!excludeHyperOS || !isHyperOS)
	}

	// Private function to get the property value from build.prop.
	private fun getProperty(property: String): String? {
		return try {
			Runtime.getRuntime().exec("getprop $property").inputStream.use { input ->
				BufferedReader(InputStreamReader(input), 1024).readLine()
			}
		} catch (e: IOException) {
			e.printStackTrace()
			null
		}
	}
}
