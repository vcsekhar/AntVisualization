package org.ant_vis.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;


/**
 * Provides capability to read Ant build files.
 * 
 */
public class AntDepModel {

    // instance variables
    private boolean showingDescriptions;
    private Map<String, AntTarget> targetMap;

    /**
     * Constructor.
     */
    public AntDepModel() {
        // create the maps
        showingDescriptions = true;
        targetMap = new HashMap<String, AntTarget>();
    }

    /**
     * Read an XML stream and build node graph.
     * 
     * @param xmlStream
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void parseXML(final InputStream xmlStream) throws ParserConfigurationException, SAXException, IOException {
        // prepare to read
        clear();
        final boolean validating = false;

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final Document document = builder.parse(xmlStream);
        final DocumentTraversal traversal = (DocumentTraversal) document;

        final NodeIterator iterator = traversal.createNodeIterator(document.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);

        // iterate over input stream
        while (true) {
            final Node node = iterator.nextNode();
            if (node == null) {
                break;
            }
            final Element element = (Element) node;
            final String tagName = element.getTagName().trim();
            // Activator.debug("Element: " + tagName);
            if (tagName.equalsIgnoreCase("project")) {
                handleProject(element);
            } else if (tagName.equalsIgnoreCase("target")) {
                handleTarget(element);
            }
        }
    }

    /**
     * Handle project element.
     * 
     * @param element
     */
    private void handleProject(Element element) {
        final String defaultName = element.getAttribute("default").trim();
        if (defaultName.length() == 0) {
            return;
        }
        final AntTarget defaultTarget = getTarget(defaultName);
        defaultTarget.setDefault();
    }
    
    /**
     * Handle target element.
     * 
     * @param element
     */
    private void handleTarget(Element element) {
        final String targetName = element.getAttribute("name").trim();
        if (targetName.length() == 0) {
            return;
        }
        final AntTarget targetNode = getTarget(targetName);
        final String description = element.getAttribute("description").trim();
        if (description.length() > 0) {
            targetNode.setDescription(description);
        }
        final String dependsNames = element.getAttribute("depends").trim();
        if (dependsNames.length() == 0) {
            return;
        }
        final String[] dependencyNames = dependsNames.split(",");
        for (int i = 0; i < dependencyNames.length; i++) {
            final String dependencyName = dependencyNames[i].trim();
            if (dependencyName.length() == 0) {
                continue;
            }
            final AntTarget dependencyNode = getTarget(dependencyName);
            targetNode.addDependency(dependencyNode);
        }
    }

    /**
     * Get named node, construct if not already seen.
     * 
     * @param targetName
     * @return
     */
    private AntTarget getTarget(final String targetName) {
        AntTarget result = targetMap.get(targetName);
        if (result == null) {
            result = new AntTarget(targetName);
            targetMap.put(targetName, result);
        }
        return result;
    }

    public void clear() {
        showingDescriptions = true;
        targetMap.clear();
    }

    public Collection<AntTarget> getAllTargets() {
        return targetMap.values();
    }

    public Collection<AntDep> getDependencies() {
        final List<AntDep> allDependencies = new ArrayList<AntDep>(); 
        for (final AntTarget antTarget : targetMap.values()) {
            int index = 0;
            final List<AntTarget> targetDependencies = antTarget.getDependencies();
            for (final AntTarget antDest : targetDependencies) {
                final String dependencyTitle = (targetDependencies.size() > 1) ? Integer.toString(index + 1) : "";
                final AntDep antDep = new AntDep(antTarget, antDest, dependencyTitle);
                allDependencies.add(antDep);
                index++;
            }
        }
        return allDependencies;
    }

//    public void dumpDependencies() {
//        Activator.debug("");
//        Activator.debug("Dependencies:");
//        for (final AntDep dependency : dependencies) {
//            Activator.debug(dependency.toString());
//        }
//    }

    public void toggleDescriptions() {
        showingDescriptions = !showingDescriptions;
    }
    
    public boolean isShowingDescriptions() {
        return showingDescriptions;
    }

    public void deleteSelection(final AntSelection selection) {
        for (final EntityConnectionData connection : selection.getDependencies()) {
            final AntTarget targetSrc = (AntTarget) connection.source;
            final AntTarget targetDst = (AntTarget) connection.dest;
            targetSrc.deleteDependency(targetDst);
        }
        
        final Set<AntTarget> deletingTargets = selection.getTargets();
        
        for (final AntTarget target : targetMap.values()) {
            target.deleteDependencies(deletingTargets);
        }
        
        for (final AntTarget deletingTarget : deletingTargets) {
            targetMap.remove(deletingTarget.getName());
        }
    }
}
