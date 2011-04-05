package org.ant_vis.model;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

public class AntProvider implements IGraphEntityContentProvider {

    private AntDepModel antModel;

    public AntProvider() {
    }
    
    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub
    }

    @Override
    public Object[] getElements(Object paramObject) {
        antModel = (AntDepModel) paramObject;
        return antModel.getAllTargets().toArray();
    }

    @Override
    public Object[] getConnectedTo(Object paramObject) {
        final AntTarget source = (AntTarget) paramObject;
        return source.getDependencies().toArray();
    }

}
