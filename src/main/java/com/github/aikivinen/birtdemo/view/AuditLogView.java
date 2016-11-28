package com.github.aikivinen.birtdemo.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = AuditLogView.VIEW_NAME)
public class AuditLogView extends VerticalLayout implements View {

	public final static String VIEW_NAME = "audit-log";
	public final static FontAwesome ICON = FontAwesome.LIST;
	
	@Autowired
	ApplicationContext context;
	
	@Override
	public void enter(ViewChangeEvent event) {	
	}
	
	@PostConstruct
	void init() {
		setSizeFull();
		Table table = new Table();	
		addComponent(table);
		table.setSizeFull();
		SQLContainer sqlContainer = (SQLContainer) context.getBean("sqlContainer","audit_log", false);
		table.setContainerDataSource(sqlContainer);
		addComponent(new Button("refresh", event -> {
			sqlContainer.refresh();
		}));
		setExpandRatio(table, 1);
		setSpacing(true);
	}
}
