package org.ogier.msbuilder.records;

public record PomModel(String groupId, String artifactId, String version, String packaging, String name,
                       String modelVersion) {
}