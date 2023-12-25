package org.ogier.msbuilder.util;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.ogier.msbuilder.util.interfaces.IPomAdder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PomAdder implements IPomAdder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PomAdder.class);

    @Override
    public void createReactorPOM(String directoryPath, String msName, List<String> modules, String groupId) {
        // Create a new Model object
        Model model = new Model();
        // Set the groupId, artifactId, and version
        model.setGroupId(groupId);
        model.setArtifactId(msName+"-reactor");
        model.setVersion("1.0-SNAPSHOT");
        model.setPackaging("pom");
        model.setName("${project.groupId}:${project.artifactId}");
        model.setModelVersion("4.0.0");

        List<String> modulesList = new ArrayList<>();

        for(String module : modules)
        {
            modulesList.add(msName+"-"+module);
        }
        Collections.sort(modulesList);
        modulesList.remove(msName+"-parent");
        modulesList.remove(msName+"-exe");
        modulesList.add(0,msName+"-parent");
        modulesList.add((modulesList.size()-1), msName+"-exe");
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

    private void writeToPom(String directoryPath, Model model)
    {
        File pomXmlFile = new File(directoryPath+"/pom.xml");
        LOGGER.info("Writing to pom in {}", directoryPath+"/pom.xml");
        // Write the document to the file
        try (FileOutputStream fos = new FileOutputStream(pomXmlFile)) {
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(fos, model);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}