package org.ogier.msbuilder.util.implementation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "org.ogier")
@Component
public class OgierConfiguration
{
    private List<String> modules = new ArrayList<>();

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }
}