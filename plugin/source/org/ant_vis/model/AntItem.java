package org.ant_vis.model;

import org.eclipse.draw2d.IFigure;

/**
 * Provided to parent the Ant model class hierarchy.
 */
public abstract class AntItem {
    
//    private GraphItem graphItem;
//
//    public void setGraphItem(final GraphItem graphItem) {
//        this.graphItem = graphItem;
//    }
//
//    public GraphItem getGraphItem() {
//        return graphItem;
//    }
    
    private IFigure figure;

    public void setFigure(final IFigure figure) {
        this.figure = figure;
    }
    
    public IFigure getFigure() {
        return figure;
    }
}
