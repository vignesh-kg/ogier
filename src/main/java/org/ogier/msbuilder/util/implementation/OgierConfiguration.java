package org.ogier.msbuilder.util.implementation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "org.ogier")
@Component
public class OgierConfiguration {
    private List<String> modules = new ArrayList<>();

    private List<String> mandatorymodules = new ArrayList<>();

    private List<String> nonbusinessmodules = new ArrayList<>();

    private List<String> excludesubmodules = new ArrayList<>();

    private Map<String, List<String>> intermoduledependency = new HashMap<>();

    public Map<String, List<String>> getIntermoduledependency() {
        return intermoduledependency;
    }

    public void setIntermoduledependency(Map<String, List<String>> intermoduledependency) {
        this.intermoduledependency = intermoduledependency;
    }

    public List<String> getExcludesubmodules() {
        return excludesubmodules;
    }

    public void setExcludesubmodules(List<String> excludesubmodules) {
        this.excludesubmodules = excludesubmodules;
    }

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