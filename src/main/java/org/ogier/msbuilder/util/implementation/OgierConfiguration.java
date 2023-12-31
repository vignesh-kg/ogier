package org.ogier.msbuilder.util.implementation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "org.ogier")
@Component
public class OgierConfiguration {
    private List<String> modules = new ArrayList<>();

    private List<String> mandatorymodules = new ArrayList<>();

    private List<String> nonbusinessmodules = new ArrayList<>();

    public List<String> getNonbusinessmodules() {
        return nonbusinessmodules;
    }

    public void setNonbusinessmodules(List<String> nonbusinessmodules) {
        this.nonbusinessmodules = nonbusinessmodules;
    }

    public List<String> getMandatorymodules() {
        return mandatorymodules;
    }

    public void setMandatorymodules(List<String> mandatorymodules) {
        this.mandatorymodules = mandatorymodules;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }
}