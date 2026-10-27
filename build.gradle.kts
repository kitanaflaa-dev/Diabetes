// Top-level build file where you can add configuration options common to all sub-projects/modules.

// No longer needed here with modern Gradle
// allprojects {
//     repositories {
//         google()
//         mavenCentral()
//     }
// }

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
