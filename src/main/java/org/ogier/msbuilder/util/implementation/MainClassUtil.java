package org.ogier.msbuilder.util.implementation;

import org.springframework.stereotype.Component;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MainClassUtil {

    public void createMainClass(File mainClassFile, String packageName)
    {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();

        VelocityContext context = new VelocityContext();
        context.put("package", packageName);

        Template template = ve.getTemplate("//template//mainClassTemplate.vm");

        try (FileWriter writer = new FileWriter(mainClassFile)) {
            template.merge(context, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createConfigClass(File configClassFile, String basePackage, String packageName)
    {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();

        VelocityContext context = new VelocityContext();
        context.put("package", packageName);
        context.put("basePackage", basePackage);

        Template template = ve.getTemplate("//template//configClassTemplate.vm");

        try (FileWriter writer = new FileWriter(configClassFile)) {
            template.merge(context, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
