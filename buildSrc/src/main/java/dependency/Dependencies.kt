package dependency

object Dependencies {

  object AndroidX {
    const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    const val CONSTRAINT_LAYOUT =
      "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    const val LEGACY_SUPPORT = "androidx.legacy:legacy-support-v4:${Versions.LEGACY_SUPPORT}"
    const val CARDVIEW = "androidx.cardview:cardview:${Versions.CARDVIEW}"
    const val RECYCLERVIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLERVIEW}"
  }

  object Misc {
    const val PICASSO = "com.squareup.picasso:picasso:${Versions.PICASSO}"
    const val GSON_CONVERTER = "com.squareup.retrofit2:converter-gson:${Versions.GSON_CONVERTER}"
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
    const val CAROUSELVIEW = "com.github.sayyam:carouselview:${Versions.CAROUSELVIEW}"
  }
}
