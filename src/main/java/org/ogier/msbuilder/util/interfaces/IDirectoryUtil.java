package org.ogier.msbuilder.util.interfaces;

import java.util.Map;

public interface IDirectoryUtil {
    void createModules(Map<String, String> argMap);

    void createDirectory(String directoryPath);
}