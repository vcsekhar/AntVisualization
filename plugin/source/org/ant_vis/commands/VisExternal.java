package org.ant_vis.commands;

import java.io.IOException;

import org.ant_vis.main.Activator;
import org.ant_vis.preferences.PreferenceConstants;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;


public class VisExternal extends FileHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        // get the file
        final IFile selectedFile = getSelectedFile(event);
        
        // get the preferences
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        final String executable = store.getString(PreferenceConstants.USER_EXECUTABLE_PATH);
        final String argTemplate = store.getString(PreferenceConstants.USER_EXECUTABLE_ARG);
        final String argSubstituted = argTemplate.replace("%f", selectedFile.getRawLocation().toOSString());

        // start the process
        try {
            Runtime.getRuntime().exec(new String[] { executable, argSubstituted } );
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
