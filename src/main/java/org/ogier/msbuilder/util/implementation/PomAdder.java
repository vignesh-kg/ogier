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
    public void addPom(String directoryPath, String msName, String module, String groupId) {
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
            writeToPom(directoryPath, model);
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
        if(ogierConfiguration.getIntermoduledependency().containsKey(module))
        {
            List<String> moduleDependencies = ogierConfiguration.getIntermoduledependency().get(module);
            for(String moduleDependency : moduleDependencies)
            {
                Dependency dependency = new Dependency();
                dependency.setGroupId("${project.groupId}");
                dependency.setArtifactId(msName+"-"+moduleDependency);
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