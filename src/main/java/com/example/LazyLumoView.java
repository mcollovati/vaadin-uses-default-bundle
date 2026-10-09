package com.example;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;

/**
 * Same as LazyView, plus a reference to LumoIcon. LumoIcon is not a
 * Component, so it cannot be used with @Uses; a class reference is enough
 * for the build-time scanner.
 */
@Route("lazy-lumo")
@Uses(Icon.class)
public class LazyLumoView extends VerticalLayout {

    @SuppressWarnings("unused")
    private static final Class<?> LUMO_ICONS = LumoIcon.class;

    public LazyLumoView() {
        add(MainView.createGrid());
    }
}
