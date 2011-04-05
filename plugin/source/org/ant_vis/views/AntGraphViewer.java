package org.ant_vis.views;

import java.util.Iterator;

import org.ant_vis.main.Activator;
import org.ant_vis.model.AntSelection;
import org.ant_vis.model.AntTarget;
import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;


/**
 * Provide some convenience methods on GraphViewer, and track selection state.
 * 
 */
public class AntGraphViewer extends GraphViewer {
    
    // instance variables
    private AntSelection lastSelection;

    private static enum LayoutAlgorithmOption { htree, vtree, spring, radial };
    
    final public static String DEFAULT_LAYOUT_NAME = LayoutAlgorithmOption.htree.toString();
    
    /**
     * Constructor.
     * 
     * @param parent
     * @param style
     */
    public AntGraphViewer(final Composite parent, final int style) {
        super(parent, style);

        lastSelection = new AntSelection();
        addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final AntSelection newSelection = new AntSelection();
                final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                for (final Iterator<?> iterator = selection.iterator() ; iterator.hasNext() ; ) {
                    final Object selected = iterator.next();
                    Activator.debug("selectionChanged: " + selected.getClass() + " = " + selected);
                    if (selected instanceof AntTarget) {
                        newSelection.getTargets().add((AntTarget) selected);
                    } else if (selected instanceof EntityConnectionData) {
                        newSelection.getDependencies().add((EntityConnectionData) selected);
                    }
                }
                setNewSelection(newSelection);
            }
        });
        
    }

    private void setNewSelection(final AntSelection newSelection) {
//      Activator.debug("new selection: " + newSelection);
        lastSelection.getTargets().removeAll(newSelection.getTargets());
        
        for (final AntTarget unselected : lastSelection.getTargets()) {
            setFigureColors(unselected.getFigure(), unselected.isDefault(), false);
        }
//        final List<IFigure> widgets = new ArrayList<IFigure>();
        for (final AntTarget selected : newSelection.getTargets()) {
//            widgets.add(selected.getIFigure());
            setFigureColors(selected.getFigure(), selected.isDefault(), true);
        }
        lastSelection = newSelection;
    }

//    public boolean isSelectionEmpty() {
//        return getSelection().isEmpty();
//    }

//    public GraphItem getFirstSelectedItem() {
//        final IStructuredSelection selection = (IStructuredSelection) getSelection();
//        final GraphItem graphItem = findGraphItem(selection.getFirstElement());
//        return graphItem;
//    }

    public void deleteSelection() {
        graph.setSelection(null);
        
        for (final EntityConnectionData selected : lastSelection.getDependencies()) {
            removeRelationship(selected);
        }
        for (final AntTarget selected : lastSelection.getTargets()) {
            removeNode(selected);
        }
        lastSelection.clear();
    }

    public AntSelection getLastSelection() {
        return lastSelection;
    }
    
    public void setFigureColors(final IFigure figure, final boolean isDefault, final boolean isSelected) {
        figure.setForegroundColor(graph.DARK_BLUE);
        figure.setBackgroundColor(isSelected ? graph.LIGHT_YELLOW : (isDefault ? graph.GREY_BLUE : graph.LIGHT_BLUE_CYAN));
    }

    public void setDescriptionsVisible(final boolean state) {
        for (final Object element : getNodeElements()) {
            final AntTarget antTarget = (AntTarget) element;
            Activator.debug("setting: " + antTarget.getName() + " to: " + state);
            final MultiCompartmentFigure figure = (MultiCompartmentFigure) antTarget.getFigure();
            final Label descriptionLabel = (Label) figure.getChildren().get(1);
            descriptionLabel.setText(state ? antTarget.getDescription() : "");
            
            figure.setPreferredSize(-1, -1);
            for (final Object childObject : figure.getChildren()) {
                final IFigure childFigure = (IFigure) childObject;
                childFigure.setSize(-1, -1);
            }
        }
    }

    public void setLayoutName(final String layoutName) {
        LayoutAlgorithm layoutAlgorithm = null;
        switch (LayoutAlgorithmOption.valueOf(layoutName)) {
            case htree:
                layoutAlgorithm = new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
                break;
            case vtree:
                layoutAlgorithm = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
                break;
            case spring:
                layoutAlgorithm = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
                break;
            case radial:
                layoutAlgorithm = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
                break;
            default:
                Assert.isLegal(false, layoutName);
        }
                    
        setLayoutAlgorithm(layoutAlgorithm, true);
    }
}
