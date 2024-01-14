package org.ogier.msbuilder.util.implementation;

import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.ogier.msbuilder.constants.OgierConstants;
import org.ogier.msbuilder.records.PomModel;
import org.ogier.msbuilder.util.interfaces.IPomAdder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.ogier.msbuilder.constants.OgierConstants.*;

@Component
public class PomAdder implements IPomAdder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PomAdder.class);

    @Autowired
    private OgierConfiguration ogierConfiguration;

    @Override
    public void createReactorPOM(String directoryPath, String msName, List<String> modules, String groupId) {
        // Create a new Model object
        Model model = createModel(new PomModel(groupId, msName + "-reactor",
                OgierConstants.SNAPSHOT_VERSION, "pom", "${project.groupId}:${project.artifactId}", "4.0.0"));

        List<String> modulesList = new ArrayList<>();

        for (String module : modules) {
            modulesList.add(msName + "-" + module);
        }
        Collections.sort(modulesList);
        modulesList.remove(msName + "-parent");
        modulesList.remove(msName + "-exe");
        modulesList.add(0, msName + "-parent");
        modulesList.add((modulesList.size() - 1), msName + "-exe");
        model.setModules(modulesList);

        Build build = new Build();
        Plugin plugin = new Plugin();
        Xpp3Dom configurationElement = new Xpp3Dom("configuration");
        Xpp3Dom skip = new Xpp3Dom("skip");
        skip.setValue("true");
        skip.setParent(configurationElement);
        configurationElement.addChild(skip);
        plugin.setConfiguration(configurationElement);
        String pluginGroupId = "org.apache.maven.plugins";
        plugin.setGroupId(pluginGroupId);
        plugin.setArtifactId("maven-deploy-plugin");
        List<Plugin> plugins = new ArrayList<>();
        plugins.add(plugin);
        build.setPlugins(plugins);

        model.setBuild(build);

        writeToPom(directoryPath, model);
    }

    @Override
    public void addPom(String directoryPath, String msName, String module, String groupId, String apiYamlFileName, String asyncYamlFileName) {
        if ("parent".equalsIgnoreCase(module)) {
            Model model = createModel(new PomModel(groupId, msName + "-" + module,
                    OgierConstants.SNAPSHOT_VERSION, "pom", "${project.groupId}:${project.artifactId}", "4.0.0"));

            model.setParent(createParent(module, groupId, msName));
            model.setProperties(createProperties());
            model.setDependencies(createDependencies(module, msName, groupId));
            writeToPom(directoryPath, model);
        } else {
            Model model = createModel(new PomModel(groupId, msName + "-" + module,
                    OgierConstants.SNAPSHOT_VERSION, null, "${project.groupId}:${project.artifactId}", "4.0.0"));
            model.setParent(createParent(module, groupId, msName));
            model.setDependencies(createDependencies(module, msName, groupId));
            if ("api".equalsIgnoreCase(module) || "async".equalsIgnoreCase(module)) {
                addAdditionalPropertiesForApiOrAsyncModules(module, model.getProperties(), groupId, apiYamlFileName, asyncYamlFileName);
                addBuildToPOM(module, model, StringUtils.hasText(apiYamlFileName), StringUtils.hasText(asyncYamlFileName));
            }
            writeToPom(directoryPath, model);
        }
    }

    private void addBuildToPOM(String module, Model model, boolean isApiYamlPresent, boolean isAsyncYamlPresent)
    {
        Build build = new Build();
        List<Plugin> plugins = new ArrayList<>();
        if("api".equalsIgnoreCase(module) && isApiYamlPresent)
        {

            Plugin plugin = new Plugin();
            plugin.setGroupId("org.openapitools");
            plugin.setArtifactId("openapi-generator-maven-plugin");
            plugin.setVersion("${"+OPENAPI_GENERATOR_PLUGIN_TAG+"}");
            List<PluginExecution> executions = new ArrayList<>();
            PluginExecution execution = new PluginExecution();

            Xpp3Dom configurationElement = new Xpp3Dom("configuration");
            Xpp3Dom inputSpec = new Xpp3Dom("inputSpec");
            inputSpec.setValue("${"+OgierConstants.SWAGGER_SRC_PROPERTY+"}");
            inputSpec.setParent(configurationElement);
            configurationElement.addChild(inputSpec);
            Xpp3Dom language = new Xpp3Dom("generatorName");
            language.setValue("spring");
            language.setParent(configurationElement);
            configurationElement.addChild(language);
            Xpp3Dom configOptions = new Xpp3Dom("configOptions");
            Xpp3Dom interfaceOnly = new Xpp3Dom("interfaceOnly");
            interfaceOnly.setValue("true");
            interfaceOnly.setParent(configOptions);
            configOptions.addChild(interfaceOnly);
            Xpp3Dom versionedApiConfiguration = new Xpp3Dom("dateLibrary");
            versionedApiConfiguration.setValue("java8");
            versionedApiConfiguration.setParent(configOptions);
            configOptions.addChild(versionedApiConfiguration);
            Xpp3Dom useOneOfInterfaces = new Xpp3Dom("useOneOfInterfaces");
            useOneOfInterfaces.setValue("true");
            useOneOfInterfaces.setParent(configOptions);
            configOptions.addChild(useOneOfInterfaces);
            Xpp3Dom useJakartaEe = new Xpp3Dom("useJakartaEe");
            useJakartaEe.setValue("true");
            useJakartaEe.setParent(configOptions);
            configOptions.addChild(useJakartaEe);
            Xpp3Dom delegatePattern = new Xpp3Dom("delegatePattern");
            delegatePattern.setValue("true");
            delegatePattern.setParent(configOptions);
            configOptions.addChild(delegatePattern);
            Xpp3Dom library = new Xpp3Dom("library");
            library.setValue("spring-cloud");
            library.setParent(configOptions);
            configOptions.addChild(library);

            configurationElement.addChild(configOptions);
            Xpp3Dom modelPackage = new Xpp3Dom("modelPackage");
            modelPackage.setValue("${"+OgierConstants.MODEL_PACKAGE+"}");
            modelPackage.setParent(configurationElement);
            configurationElement.addChild(modelPackage);
            Xpp3Dom apiPackage = new Xpp3Dom("apiPackage");
            apiPackage.setValue("${"+OgierConstants.API_PACKAGE+"}");
            apiPackage.setParent(configurationElement);
            configurationElement.addChild(apiPackage);
            execution.setConfiguration(configurationElement);
            List<String> goals = new ArrayList<>();
            goals.add("generate");
            execution.setGoals(goals);
            executions.add(execution);
            plugin.setExecutions(executions);
            plugins.add(plugin);
            build.setPlugins(plugins);

            model.setBuild(build);
        }

        if("async".equalsIgnoreCase(module) && isAsyncYamlPresent)
        {

        }
    }

    private void addAdditionalPropertiesForApiOrAsyncModules(String module, Properties properties, String groupId, String apiYamlFileName, String asyncYamlFileName) {
        if ("api".equalsIgnoreCase(module) && StringUtils.hasText(apiYamlFileName)) {
            properties.setProperty(OgierConstants.SWAGGER_SRC_PROPERTY, "${project.basedir}/" + OgierConstants.SWAGGER_PATH+"/"+apiYamlFileName);
            properties.setProperty(OgierConstants.API_PACKAGE, groupId + ".resources.interfaces");
            properties.setProperty(OgierConstants.MODEL_PACKAGE, groupId + ".resources.models");
        }

        if ("async".equalsIgnoreCase(module) && StringUtils.hasText(asyncYamlFileName)) {
            properties.setProperty(OgierConstants.SWAGGER_SRC_PROPERTY, "${project.basedir}/" + OgierConstants.SWAGGER_PATH+"/"+asyncYamlFileName);
            properties.setProperty(OgierConstants.MODEL_PACKAGE, groupId + ".asyncmessages.models");
        }
    }

    private Properties createProperties() {
        Properties properties = new Properties();
        properties.setProperty(MAPSTRUCT_VERSION_TAG, MAPSTRUCT_VERSION);
        properties.setProperty(OPENAPI_GENERATOR_PLUGIN_TAG, OPENAPI_GENERATOR_PLUGIN_VERSION);
        properties.setProperty(SPRINGBOOT_STARTER_WEB_TAG, SPRINGBOOT_VERSION);
        properties.setProperty(JACKSON_DATABIND_NULLABLE_TAG, JACKSON_DATABIND_NULLABLE_VERSION);
        properties.setProperty(SWAGGER_ANNOTATIONS_TAG, SWAGGER_ANNOTATIONS_VERSION);
        properties.setProperty(JACKSON_ANNOTATIONS_TAG, JACKSON_ANNOTATIONS_VERSION);
        properties.setProperty(JAKARTA_VALIDATION_TAG, JAKARTA_VALIDATION_VERSION);
        properties.setProperty(JAKARTA_ANNOTATION_TAG, JAKARTA_ANNOTATION_VERSION);
        properties.setProperty(SPRING_KAFKA_TAG, SPRING_KAFKA_VERSION);
        return properties;
    }

    private List<Dependency> createDependencies(String module, String msName, String groupId) {
        List<Dependency> dependencyList = new ArrayList<>();

        if (!ogierConfiguration.getNonbusinessmodules().contains(module)) {
            Dependency mapStructDependency = new Dependency();
            mapStructDependency.setGroupId("org.mapstruct");
            mapStructDependency.setArtifactId("mapstruct");
            mapStructDependency.setVersion("${" + MAPSTRUCT_VERSION_TAG + "}");
            if (!"parent".equalsIgnoreCase(module)) {
                mapStructDependency.setScope(OgierConstants.SCOPE_PROVIDED);
            }
            dependencyList.add(mapStructDependency);
        }

        if("api".equalsIgnoreCase(module))
        {
            Dependency jakartaAnnotationDependency = new Dependency();
            jakartaAnnotationDependency.setGroupId("jakarta.annotation");
            jakartaAnnotationDependency.setArtifactId("jakarta.annotation-api");
            jakartaAnnotationDependency.setVersion("${" + JAKARTA_ANNOTATION_TAG + "}");
            dependencyList.add(jakartaAnnotationDependency);

            Dependency jakartaValidationDependency = new Dependency();
            jakartaValidationDependency.setGroupId("jakarta.validation");
            jakartaValidationDependency.setArtifactId("jakarta.validation-api");
            jakartaValidationDependency.setVersion("${" + JAKARTA_VALIDATION_TAG + "}");
            dependencyList.add(jakartaValidationDependency);

            Dependency jacksonAnnotationsDependency = new Dependency();
            jacksonAnnotationsDependency.setGroupId("com.fasterxml.jackson.core");
            jacksonAnnotationsDependency.setArtifactId("jackson-annotations");
            jacksonAnnotationsDependency.setVersion("${" + JACKSON_ANNOTATIONS_TAG + "}");
            dependencyList.add(jacksonAnnotationsDependency);

            Dependency swaggerAnnotationsDependency = new Dependency();
            swaggerAnnotationsDependency.setGroupId("io.swagger.core.v3");
            swaggerAnnotationsDependency.setArtifactId("swagger-annotations");
            swaggerAnnotationsDependency.setVersion("${" + SWAGGER_ANNOTATIONS_TAG + "}");
            dependencyList.add(swaggerAnnotationsDependency);

            Dependency jacksonDatabindNullable = new Dependency();
            jacksonDatabindNullable.setGroupId("org.openapitools");
            jacksonDatabindNullable.setArtifactId("jackson-databind-nullable");
            jacksonDatabindNullable.setVersion("${" + JACKSON_DATABIND_NULLABLE_TAG + "}");
            dependencyList.add(jacksonDatabindNullable);

            Dependency springBootStarterWebDependency = new Dependency();
            springBootStarterWebDependency.setGroupId("org.springframework.boot");
            springBootStarterWebDependency.setArtifactId("spring-boot-starter-web");
            springBootStarterWebDependency.setVersion("${" + SPRINGBOOT_STARTER_WEB_TAG + "}");
            dependencyList.add(springBootStarterWebDependency);
        }

        if("async".equalsIgnoreCase(module))
        {
            Dependency jakartaAnnotationDependency = new Dependency();
            jakartaAnnotationDependency.setGroupId("org.springframework.kafka");
            jakartaAnnotationDependency.setArtifactId("spring-kafka");
            jakartaAnnotationDependency.setVersion("${" + SPRING_KAFKA_TAG + "}");
            dependencyList.add(jakartaAnnotationDependency);
        }

        createInterModuleDependency(dependencyList, module, msName);
        return dependencyList;
    }

    private void createInterModuleDependency(List<Dependency> dependencyList, String module, String msName) {
        if (ogierConfiguration.getIntermoduledependency().containsKey(module)) {
            List<String> moduleDependencies = ogierConfiguration.getIntermoduledependency().get(module);
            for (String moduleDependency : moduleDependencies) {
                Dependency dependency = new Dependency();
                dependency.setGroupId("${project.groupId}");
                dependency.setArtifactId(msName + "-" + moduleDependency);
                dependency.setVersion("${project.version}");
                dependencyList.add(dependency);
            }
        }
    }

    private Parent createParent(String module, String groupId, String msName) {
        Parent parent = new Parent();
        if (module.equalsIgnoreCase("parent")) {
            parent.setGroupId("org.springframework.boot");
            parent.setArtifactId("spring-boot-starter-parent");
            parent.setVersion(OgierConstants.SPRINGBOOT_VERSION);
        } else {
            parent.setGroupId(groupId);
            parent.setArtifactId(msName + "-parent");
            parent.setVersion("1.0.0-SNAPSHOT");
            parent.setRelativePath("../" + msName + "-parent");
        }
        return parent;
    }

    private Model createModel(PomModel pomModel) {
        // Create a new Model object
        Model model = new Model();
        // Set the groupId, artifactId, and version
        model.setGroupId(pomModel.groupId());
        model.setArtifactId(pomModel.artifactId());
        model.setVersion(pomModel.version());
        model.setPackaging(pomModel.packaging());
        model.setName(pomModel.name());
        model.setModelVersion(pomModel.modelVersion());

        return model;
    }

    private void writeToPom(String directoryPath, Model model) {
        File pomXmlFile = new File(directoryPath + "/pom.xml");
        LOGGER.info("Writing to pom in {}", directoryPath + "/pom.xml");
        // Write the document to the file
        try (FileOutputStream fos = new FileOutputStream(pomXmlFile)) {
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(fos, model);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}