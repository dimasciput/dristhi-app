package org.ei.drishti.domain;

import org.ei.drishti.Context;
import org.ei.drishti.dto.Action;

public enum ChildActionRoute {
    DELETE("deleteChild") {
        @Override
        public void direct(Action action) {
            Context.getInstance().childService().delete(action);
        }
    };
    private String identifier;

    ChildActionRoute(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

    public abstract void direct(Action action);
}
