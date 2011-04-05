package org.ant_vis.views;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;

/**
 * Provides a Toolbar-layout Figure to contain multiple compartments. Note the
 * added child components have their borders set by this component: this doesn't
 * seem good practice exactly, but works in this application.
 * 
 */
public class MultiCompartmentFigure extends Figure {

    /**
     * Constructor.
     * 
     * @param first
     */
    public MultiCompartmentFigure(final IFigure first) {
        setLayoutManager(new ToolbarLayout());
        setBorder(new MultiCompartmentBorder());
        setOpaque(true);
        setSize(-1, -1);
        first.setBorder(new InsetBorder());
        add(first);
    }

    /**
     * Add subsequent compartment.
     * 
     * @param figure
     */
    public void addCompartment(final IFigure figure) {
        figure.setBorder(new SeparatedInsetBorder());
        add(figure);
    }

    /**
     * Provides this container's border.
     *
     */
    private static class MultiCompartmentBorder extends LineBorder {

        public MultiCompartmentBorder() {
            super(ColorConstants.darkBlue, 1);
        }
    }

    /**
     * Provides border for first container element.
     *
     */
    private static class InsetBorder extends AbstractBorder {

        @Override
        public Insets getInsets(IFigure figure) {
            return new Insets(1, 5, 1, 5);
        }

        @Override
        public void paint(IFigure figure, Graphics graphics, Insets insets) {
            // no-op for top element
        }
    }

    /**
     * Provides border for successive container elements.
     *
     */
    private static class SeparatedInsetBorder extends InsetBorder {

        @Override
        public void paint(IFigure figure, Graphics graphics, Insets insets) {
            graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(), tempRect.getTopRight());
        }
    }

    /**
     * Compartment accessor.
     * 
     * @param index
     * @return
     */
    public IFigure getCompartment(final int index) {
        return (IFigure) getChildren().get(index);
    }
}
