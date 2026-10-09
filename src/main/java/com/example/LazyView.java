package com.example;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Same as MainView, but on a route that is not eager, so in an
 * application-specific bundle its dependencies go into a lazy chunk.
 */
@Route("lazy")
@Uses(Icon.class)
public class LazyView extends VerticalLayout {

    public LazyView() {
        add(MainView.createGrid());
    }
}
