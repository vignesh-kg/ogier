package org.ogier.msbuilder.util.implementation;

import org.ogier.msbuilder.util.interfaces.IDirectoryUtil;
import org.springframework.stereotype.Component;

@Component
public class DirectoryUtil implements IDirectoryUtil
{
    @Override
    public boolean createDirectory()
    {
        return true;
    }
}