package org.ogier.msbuilder.util.interfaces;

import java.io.FileNotFoundException;
import java.util.List;

public interface IPomAdder
{

    void createReactorPOM(String directoryPath, String msName, List<String> modules, String groupId);

    void addPom(String directoryPath, String msName, String module, String groupId);
}