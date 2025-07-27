plugins {
    id("com.android.library")
    id("maven-publish")
    id("signing")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.nova.mvvmtoolkit"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildToolsVersion = "34.0.0"

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    api("com.squareup.okhttp3:okhttp:5.1.0")
    api("com.google.code.gson:gson:2.13.1")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
    api("androidx.activity:activity-ktx:1.10.1")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "io.github.ankit295-ops"
                artifactId = "mvvmtoolkit"
                version = "1.0.0"

                pom {
                    name.set("MVVMToolkit")
                    description.set("MVVMToolkit is a lightweight Android library to simplify MVVM structure.")
                    url.set("https://github.com/ankit295-ops/MVVMToolkit")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("ankit295")
                            name.set("Ankit Maurya")
                            email.set("ankitmaurya295@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/ankit295-ops/MVVMToolkit.git")
                        developerConnection.set("scm:git:ssh://github.com:ankit295-ops/MVVMToolkit.git")
                        url.set("https://github.com/ankit295-ops/MVVMToolkit")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "OSSRH"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = project.findProperty("ossrhUsername") as String? ?: ""
                    password = project.findProperty("ossrhPassword") as String? ?: ""
                }
            }
        }
    }

    signing {
        useInMemoryPgpKeys(
            project.findProperty("signing.keyId") as String?,
            project.findProperty("signing.key") as String?,
            project.findProperty("signing.password") as String?
        )
        sign(publishing.publications["release"])
    }
}
