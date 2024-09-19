plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)

}

android {
    namespace = "com.imcys.shiquzkarticledetaildemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.imcys.shiquzkarticledetaildemo"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding { enable = true }
}

dependencies {
    // brv
    implementation("com.github.liangjingkanji:BRV:1.6.0")
    // flex
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    // 媒体播放
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    // 图片
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // 依赖注入
    implementation(platform("io.insert-koin:koin-bom:3.5.6"))
    implementation("io.insert-koin:koin-android")
    // 动画
    implementation("com.airbnb.android:lottie:3.4.0")
    // 空祖对话框
    implementation("com.kongzue.dialogx:DialogX:0.0.49")
    implementation("com.github.kongzue.DialogXSample:DatePicker:0.0.14")

    // 网络请求
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // 序列化
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.2")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}