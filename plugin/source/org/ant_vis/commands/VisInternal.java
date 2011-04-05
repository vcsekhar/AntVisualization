package org.ant_vis.commands;

import org.ant_vis.views.AntView;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;


public class VisInternal extends FileHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        
        // locate the view and perform action
        final AntView antView = AntView.getInstance(event);
        if (antView == null) {
            return null;
        }
        final IFile antFile = getSelectedFile(event);
        antView.setFocus();
        antView.showFile(antFile);

        return null;
    }

}
