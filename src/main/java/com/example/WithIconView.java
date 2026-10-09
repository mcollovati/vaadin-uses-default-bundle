package com.example;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Same grid, plus a real Icon component. Attaching the Icon makes Flow load
 * the chunk that holds the iconsets.
 */
@Route("with-icon")
public class WithIconView extends VerticalLayout {

    public WithIconView() {
        add(MainView.createGrid());
        add(VaadinIcon.PLUS.create());
    }
}
