package com.github.aikivinen.birtdemo.view;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = ReportsView.VIEW_NAME)
@SuppressWarnings("serial")
public class ReportsView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "reports";
	public static final FontIcon ICON = VaadinIcons.FILE;
	private static final Logger logger = LoggerFactory.getLogger(ReportsView.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ReportsViewTab1 tab1;

	@Autowired
	private ReportsViewTab2 tab2;

	private TabSheet tabsheet;

	@Override
	public void enter(ViewChangeEvent event) {

	}

	@PostConstruct
	void init() {
		tabsheet = new TabSheet();
		tabsheet.addTab(tab1, messageSource.getMessage("caption.printreports", null, getLocale()));
		tabsheet.addTab(tab2, messageSource.getMessage("caption.editreports", null, getLocale()));
		tabsheet.setSizeFull();
		addComponent(tabsheet);
		setSizeFull();
	}
}
