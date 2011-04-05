package org.ant_vis.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.zest.core.viewers.EntityConnectionData;

/**
 * Provides a model of selected entities. Unfortunately somewhat inelegant due
 * to Zest design choices.
 */
public class AntSelection {
    final private Set<AntTarget> targets;
    final private Set<EntityConnectionData> dependencies;

    public AntSelection() {
        targets = new HashSet<AntTarget>();
        dependencies = new HashSet<EntityConnectionData>();
    }

    public Set<AntTarget> getTargets() {
        return targets;
    }

    public Set<EntityConnectionData> getDependencies() {
        return dependencies;
    }

    public void clear() {
        targets.clear();
        dependencies.clear();
    }
}
