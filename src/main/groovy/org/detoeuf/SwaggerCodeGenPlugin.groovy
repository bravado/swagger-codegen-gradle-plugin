package org.detoeuf

import org.gradle.api.Plugin
import org.gradle.api.Project

class SwaggerCodeGenPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create('swagger', SwaggerPluginExtension)
        project.task('swagger', type: SwaggerCodeGenTask)
        project.task('compileJava').dependsOn('swagger')
        project.sourceSets {
            swagger {
                java.srcDir = file("${project.buildDir.path}/swagger/src/main/java")
            }
        }
    }
}
