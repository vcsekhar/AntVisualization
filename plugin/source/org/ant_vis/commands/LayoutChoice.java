package org.ant_vis.commands;

import org.ant_vis.main.Activator;
import org.ant_vis.views.AntView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

public class LayoutChoice extends AbstractHandler {

    public LayoutChoice() {
        Activator.debug("LayoutChoice()");
    }
    
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        Activator.debug("LayoutChoice.execute");

        // locate the view and perform action
        final AntView antView = AntView.getInstance(event);
        if (antView == null) {
            return null;
        }

        if (HandlerUtil.matchesRadioState(event)) {
            Activator.debug("LayoutChoice.execute: no action");
            return null; // we are already in the updated state - do nothing
        }

        // update layout state
        final String layoutName = event.getParameter(RadioState.PARAMETER_ID);
        Activator.debug("LayoutChoice.execute: layoutName: " + layoutName);
        final Command command = event.getCommand();
        HandlerUtil.updateRadioState(command, layoutName);
        
        antView.setLayoutName(layoutName);

        return null;
    }

}
