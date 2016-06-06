package org.detoeuf

import io.swagger.codegen.ClientOptInput
import io.swagger.codegen.ClientOpts
import io.swagger.codegen.CodegenConfig
import io.swagger.codegen.DefaultGenerator
import io.swagger.parser.SwaggerParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SwaggerCodeGenTask extends DefaultTask {

    @TaskAction
    def swaggerCodeGen() {
        def swaggerPlugin = project.extensions.findByName('swagger').asType(SwaggerPluginExtension.class)

        // Configuration for language
        CodegenConfig config = forName(swaggerPlugin.language)

        // Outputdir + clean
        config.setOutputDir(project.file(swaggerPlugin.output ?: 'build/generated-sources/swagger').absolutePath)
        project.delete(config.getOutputDir())

        // Add additional properties
        config.additionalProperties().putAll(swaggerPlugin.additionalProperties)

        // Client input
        ClientOptInput input = new ClientOptInput()
                .opts(new ClientOpts())
                .swagger(new SwaggerParser().read(project.file(swaggerPlugin.inputSpec).absolutePath))
                .config(config)

        new DefaultGenerator().opts(input).generate()
    }

    private static CodegenConfig forName(String name) {
        ServiceLoader<CodegenConfig> loader = ServiceLoader.load(CodegenConfig.class)
        for (CodegenConfig config : loader) {
            if (config.getName().equals(name)) {
                return config
            }
        }

        // else try to load directly
        try {
            return (CodegenConfig) Class.forName(name).newInstance()
        } catch (Exception e) {
            throw new RuntimeException("Can't load config class with name ".concat(name), e)
        }
    }

}
