package org.ant_vis.commands;

import org.ant_vis.main.Activator;
import org.ant_vis.views.AntView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


public class MoveCanvas {

    public static class Base extends AbstractHandler {
        
        private final int x;
        private final int y;

        public Base(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Object execute(final ExecutionEvent event) throws ExecutionException {
            Activator.debug("Move");
            
            // locate the view and perform action
            AntView.getInstance(event).moveCanvas(x, y);
            return null;
        }

    }

    public static class U extends Base {
        public U() {
            super(0, +1);
        }
    }
        
    public static class D extends Base {
        public D() {
            super(0, -1);
        }
    }
    
    public static class L extends Base {
        public L() {
            super(-1, 0);
        }
    }
    
    public static class R extends Base {
        public R() {
            super(+1, 0);
        }
    }
    
}
