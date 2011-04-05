package org.ant_vis.model;

import org.ant_vis.main.Activator;
import org.ant_vis.views.AntGraphViewer;
import org.ant_vis.views.MultiCompartmentFigure;
import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IFigureProvider;


public class AntFigureProvider extends BaseLabelProvider implements IFigureProvider, ILabelProvider {

    private final AntDepModel antDepModel;
    private final AntGraphViewer antGraphViewer;

    /**
     * Constructor.
     * 
     * @param antDepModel
     * @param antGraphViewer
     */
    public AntFigureProvider(final AntDepModel antDepModel, final AntGraphViewer antGraphViewer) {
        Assert.isNotNull(antDepModel);
        Assert.isNotNull(antGraphViewer);
        
        this.antDepModel = antDepModel;
        this.antGraphViewer = antGraphViewer;
    }

    @Override
    public IFigure getFigure(final Object paramObject) {
        if (paramObject instanceof AntTarget) {
            final AntTarget antTarget = (AntTarget) paramObject;
            Activator.debug("Constructing IFigure for node: " + antTarget);
            
            final Label nameLabel = new Label(antTarget.getName());
            nameLabel.setFont(JFaceResources.getBannerFont());
            final MultiCompartmentFigure mcFigure = new MultiCompartmentFigure(nameLabel);
            
            if (antDepModel.isShowingDescriptions()) {
                final String description = antTarget.getDescription(); 
                if (description != null && description.length() > 0) {
                    final Label descriptionLabel = new Label(antTarget.getDescription());
                    mcFigure.addCompartment(descriptionLabel);
                }
            }
            
            antTarget.setFigure(mcFigure);
            antGraphViewer.setFigureColors(mcFigure, antTarget.isDefault(), false);
            
            return mcFigure;
        } else if (paramObject instanceof AntDep) {
            final AntDep antDep = (AntDep) paramObject;
            Activator.debug("Constructing IFigure for edge: " + antDep);
            
            final Label titleLabel = new Label(antDep.getTitle());
            antDep.setFigure(titleLabel);
            
            return titleLabel;
        } else {
            throw new IllegalArgumentException("paramObject: " + paramObject);
        }
    }

    @Override
    public Image getImage(final Object element) {
        return null;
    }

    @Override
    public String getText(final Object element) {
        Activator.debug("getText: " + element);
        if (element instanceof EntityConnectionData) {
            final EntityConnectionData conn = (EntityConnectionData) element;
            Activator.debug(conn.source + " ---- " + conn.dest);
            final AntTarget targetSrc = (AntTarget) conn.source;
            final AntTarget targetDst = (AntTarget) conn.dest;
            if (targetSrc.getDependencies().size() > 1) {
                final int index = targetSrc.getDependencies().indexOf(targetDst);
                assert index >= 0;
                return Integer.toString(index + 1);
            }
        }
        return null;
    }

    
}
