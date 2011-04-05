package org.ant_vis.model;

public class AntDep extends AntItem {
    
    private final AntTarget src;
    private final AntTarget dst;
    private final String title;

    public AntDep(final AntTarget src, final AntTarget dst, final String title) {
        this.src = src;
        this.dst = dst;
        this.title = title;
    }
    
    public AntTarget getSrc() {
        return src;
    }
    
    public AntTarget getDst() {
        return dst;
    }

    public String getTitle() {
        return title;
    }
    
    @Override
    public String toString() {
        return src.getName() + " -> " + dst.getName();
    }
}
