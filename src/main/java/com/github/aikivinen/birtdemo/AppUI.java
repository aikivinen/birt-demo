package com.github.aikivinen.birtdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import com.github.aikivinen.birtdemo.view.MainMenu;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Theme("valo")
public class AppUI extends UI {

	@Autowired
	private SpringViewProvider viewProvider;

	@Override
	protected void init(VaadinRequest request) {
		LocaleContextHolder.setLocale(request.getLocale());

		HorizontalLayout root = new HorizontalLayout();
		setContent(root);
		root.setSizeFull();

		VerticalLayout viewContainer = new VerticalLayout();

		root.addComponent(new MainMenu());
		root.addComponent(viewContainer);
		viewContainer.setSizeFull();
		root.setExpandRatio(viewContainer, 1);

		Navigator navigator = new Navigator(this, viewContainer);
		navigator.addProvider(viewProvider);
		
	}
}
