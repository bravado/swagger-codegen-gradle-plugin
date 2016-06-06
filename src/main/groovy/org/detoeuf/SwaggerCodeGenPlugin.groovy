package org.detoeuf

import org.gradle.api.Plugin
import org.gradle.api.Project

class SwaggerCodeGenPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create('swagger', GreetingPluginExtension)
        project.task('swagger', type: SwaggerCodeGenTask)
    }
}
