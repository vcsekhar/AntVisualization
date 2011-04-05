package org.ant_vis.commands;

import org.ant_vis.main.Activator;
import org.ant_vis.views.AntView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;


public class Linked extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {

        // locate the view and perform action
        // update toggled state
        final Command command = event.getCommand();
        final boolean state = !HandlerUtil.toggleCommandState(command);
        Activator.debug("Linked.execute: state: " + state);
        AntView.getInstance(event).setLinkedState(state);

        return null;
    }

}
