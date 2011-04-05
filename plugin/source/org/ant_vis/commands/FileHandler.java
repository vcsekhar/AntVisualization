package org.ant_vis.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Provides a simple file retrieval capability. 
 */
public abstract class FileHandler extends AbstractHandler {

    protected IFile getSelectedFile(final ExecutionEvent event) {
        // get the file from the selection
        final IStructuredSelection activeMenuSelection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
        assert activeMenuSelection.size() == 1;
        final Object firstElement = activeMenuSelection.getFirstElement();
        Assert.isNotNull(firstElement);
        final IFile selectedFile = (IFile) firstElement;
        // Activator.debug("selected: " + xmlFile.getName());
        return selectedFile;
    }
}
