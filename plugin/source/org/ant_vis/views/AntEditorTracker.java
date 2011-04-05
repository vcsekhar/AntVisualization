package org.ant_vis.views;

import org.ant_vis.main.Activator;
import org.eclipse.ant.internal.ui.editor.AntEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;


/**
 * Provides a facility to track Ant editor operations.
 */
public class AntEditorTracker implements IPartListener2 {
    
    // constants
    private static final String LINKED_STATE_KEY = "LINKED_STATE";

    // instance variables
    private final AntView antView;
    private boolean linkedState;

    /**
     * Constructor.
     * 
     * @param antView
     * @param memento 
     * @param linkedState
     */
    public AntEditorTracker(final AntView antView, final IMemento memento) {
        Activator.debug("AntEditorTracker()");
        this.antView = antView;
        
        final Boolean linkedStateSaved = (memento != null) ? memento.getBoolean(LINKED_STATE_KEY) : null; 
        setLinkedState(linkedStateSaved != null ? linkedStateSaved : true);

        // final IWorkbenchPage page = antView.getSite(). getPage();
        antView.getSite().getWorkbenchWindow().getPartService().addPartListener(this);
    }

    public void close() {
        antView.getSite().getWorkbenchWindow().getPartService().removePartListener(this);
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        Activator.debug("AntEditorTracker.partActivated");
        checkIfTrackable(partRef);
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        Activator.debug("AntEditorTracker.partBroughtToTop");
        checkIfTrackable(partRef);
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        Activator.debug("AntEditorTracker.partClosed");
        final IFile antFile = getAntFile(partRef);
        if (antFile != null && linkedState) {
            antView.untrackFile(antFile);
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
        Activator.debug("AntEditorTracker.partDeactivated");
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
        Activator.debug("AntEditorTracker.partOpened");
        checkIfTrackable(partRef);
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        Activator.debug("AntEditorTracker.partHidden");
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
        Activator.debug("AntEditorTracker.partVisible");
        checkIfTrackable(partRef);
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
        Activator.debug("AntEditorTracker.partInputChanged");
    }

    private void checkIfTrackable(final IWorkbenchPartReference partRef) {
        final IFile antFile = getAntFile(partRef);
        if (antFile != null && linkedState) {
            antView.trackFile(antFile);
        }
    }

    @SuppressWarnings("restriction")
    private IFile getAntFile(final IWorkbenchPartReference partRef) {
        IFile antFile = null;
        final IWorkbenchPart part = partRef.getPart(true);
        final boolean isAntEditor = part instanceof AntEditor;
        Activator.debug("AntEditorTracker.partActivated isAntEditor: " + isAntEditor);
        if (isAntEditor) {
            final AntEditor antEditor = (AntEditor) part;
            antFile = antEditor.getAntModel().getFile();
        }
        return antFile;
    }
    
    public void setLinkedState(final boolean linkedState) {
        Activator.debug("AntEditorTracker.setLinkedState: " + linkedState);
        this.linkedState = linkedState;
    }

    public void saveState(final IMemento memento) {
        memento.putBoolean(LINKED_STATE_KEY, linkedState);
    }
    
}
