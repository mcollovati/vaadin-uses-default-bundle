package com.example;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;

/**
 * The grid shows icons only through a LitRenderer template, so there is no
 * Java reference to Icon apart from the @Uses annotation.
 */
@Route("")
@Uses(Icon.class)
public class MainView extends VerticalLayout {

    public MainView() {
        add(createGrid());
    }

    static Grid<String> createGrid() {
        Grid<String> grid = new Grid<>();
        grid.addColumn(s -> s);
        grid.addColumn(LitRenderer.<String> of("""
                <vaadin-button><vaadin-icon icon="vaadin:phone"></vaadin-icon></vaadin-button>
                <vaadin-button><vaadin-icon icon="lumo:angle-up"></vaadin-icon></vaadin-button>
                """));
        grid.setItems("foo", "bar");
        return grid;
    }
}
