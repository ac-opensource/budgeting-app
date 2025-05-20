package dev.pandesal.sbp.presentation.nav

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("FunctionName", "ExperimentalStdlibApi")
inline fun <reified T : Parcelable> parcelableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json
): NavType<T?> = object : NavType<T?>(isNullableAllowed) {
  override fun get(bundle: Bundle, key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      bundle.getParcelable(key, T::class.java)
    } else {
      @Suppress("DEPRECATION")
      bundle.getParcelable(key)
    }

  override fun parseValue(value: String): T? =
    json.decodeFromString(value)

  override fun serializeAsValue(value: T?): String =
    json.encodeToString(value)

  override fun put(bundle: Bundle, key: String, value: T?) =
    bundle.putParcelable(key, value)
}

/**
 * Build a Map<KType,NavType<*>> for both T and T? so you can just
 * pass this into any dialog<T> or composable<T> call.
 */
@Suppress("ExperimentalStdlibApi")
inline fun <reified T : Parcelable> parcelableTypeMap(
  isNullableAllowed: Boolean = false,
  json: Json = Json
): Map<KType, NavType<*>> {
  val navType = parcelableType<T>(isNullableAllowed, json)

  return mapOf(
    typeOf<T>()   to navType,
    typeOf<T?>()  to navType,

  )
}