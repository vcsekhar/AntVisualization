package org.ant_vis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AntTarget extends AntItem {

    private final String name;
    private boolean isDefault;
    private String description;
    private List<AntTarget> dependencies;

    public AntTarget(final String name) {
        this.name = name;
        isDefault = false;
        dependencies = new ArrayList<AntTarget>();
    }

    public void setDefault() {
        isDefault = true;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addDependency(final AntTarget dependency) {
        dependencies.add(dependency);
    }

    public List<AntTarget> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        String result = name + " -> [";
        for (final AntTarget antTarget : dependencies) {
            result += " " + antTarget.getName();
        }
        result += " ]";
        return result;
    }

    public void deleteDependency(final AntTarget deletingTarget) {
        dependencies.remove(deletingTarget);
    }

    public void deleteDependencies(final Collection<AntTarget> deletingTargets) {
        for (final AntTarget deletingTarget : deletingTargets) {
            dependencies.remove(deletingTarget);
        }
    }
}
