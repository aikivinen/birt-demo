package com.github.aikivinen.birtdemo.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.github.aikivinen.birtdemo.domain.AuditLogEntry;
import com.github.aikivinen.birtdemo.jpa.repository.AuditLogRepository;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = AuditLogView.VIEW_NAME)
public class AuditLogView extends VerticalLayout implements View {

	public final static String VIEW_NAME = "audit-log";
	public final static FontIcon ICON = VaadinIcons.LIST;

	@Autowired
	AuditLogRepository logRepository;

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@PostConstruct
	void init() {
		setSizeFull();
		Grid<AuditLogEntry> grid = new Grid<AuditLogEntry>();
		grid.setSizeFull();

		addComponent(grid);
		grid.addColumn(AuditLogEntry::getDate);
		grid.addColumn(AuditLogEntry::getText);

		grid.setItems(logRepository.findAll());

		addComponent(new Button("refresh", event -> {
			grid.setItems(logRepository.findAll());
		}));

		setExpandRatio(grid, 1);
	}
}
