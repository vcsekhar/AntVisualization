package org.ant_vis.commands;

import org.ant_vis.main.Activator;
import org.ant_vis.views.AntView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


public class ZoomOut extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        Activator.debug("ZoomOut");
        
        // locate the view and perform action
        AntView.getInstance(event).zoomOut();
        
        return null;
    }

}

