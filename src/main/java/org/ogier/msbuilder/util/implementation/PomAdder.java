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
            plugin.setGroupId("io.swagger.codegen.v3");
            plugin.setArtifactId("swagger-codegen-maven-plugin");
            List<PluginExecution> executions = new ArrayList<>();
            PluginExecution execution = new PluginExecution();

            Xpp3Dom configurationElement = new Xpp3Dom("configuration");
            Xpp3Dom inputSpec = new Xpp3Dom("inputSpec");
            inputSpec.setValue("${"+OgierConstants.SWAGGER_SRC_PROPERTY+"}");
            inputSpec.setParent(configurationElement);
            configurationElement.addChild(inputSpec);
            Xpp3Dom language = new Xpp3Dom("language");
            language.setValue("java");
            language.setParent(configurationElement);
            configurationElement.addChild(language);
            Xpp3Dom configOptions = new Xpp3Dom("configOptions");
            Xpp3Dom interfaceOnly = new Xpp3Dom("interfaceOnly");
            interfaceOnly.setValue("true");
            interfaceOnly.setParent(configOptions);
            configOptions.addChild(interfaceOnly);
            Xpp3Dom versionedApiConfiguration = new Xpp3Dom("versionedApiConfiguration");
            versionedApiConfiguration.setValue("true");
            versionedApiConfiguration.setParent(configOptions);
            configOptions.addChild(versionedApiConfiguration);
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
        properties.setProperty(OgierConstants.MAPSTRUCT_VERSION, "1.5.5.Final");

        return properties;
    }

    private List<Dependency> createDependencies(String module, String msName, String groupId) {
        List<Dependency> dependencyList = new ArrayList<>();

        if (!ogierConfiguration.getNonbusinessmodules().contains(module)) {
            Dependency mapStructDependency = new Dependency();
            mapStructDependency.setGroupId("org.mapstruct");
            mapStructDependency.setArtifactId("mapstruct");
            mapStructDependency.setVersion("${" + OgierConstants.MAPSTRUCT_VERSION + "}");
            if (!"parent".equalsIgnoreCase(module)) {
                mapStructDependency.setScope(OgierConstants.SCOPE_PROVIDED);
            }
            dependencyList.add(mapStructDependency);
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