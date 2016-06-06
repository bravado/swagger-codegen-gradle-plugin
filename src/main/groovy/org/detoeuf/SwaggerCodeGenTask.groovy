package org.detoeuf

import config.Config
import config.ConfigParser
import io.swagger.codegen.CliOption
import io.swagger.codegen.ClientOptInput
import io.swagger.codegen.ClientOpts
import io.swagger.codegen.CodegenConfig
import io.swagger.codegen.DefaultGenerator
import io.swagger.models.Swagger
import io.swagger.parser.SwaggerParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SwaggerCodeGenTask extends DefaultTask {

    @TaskAction
    def swaggerCodeGen() {
        Swagger swagger = new SwaggerParser().read(project.file(project.swaggerInputSpec).absolutePath)
        CodegenConfig config = forName(project.swaggerLanguage)

        config.setOutputDir(project.file(project.swagger.output?:'build/generated-sources/swagger').absolutePath)
        project.delete(outputDir)

        def swaggerProperties = project.extensions.findByName('swagger').asType(SwaggerPluginExtension.class).getProperties()
        config.additionalProperties().putAll(swaggerProperties)

        ClientOptInput input = new ClientOptInput().opts(new ClientOpts()).swagger(swagger)
        input.setConfig(config)

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
