package com.github.aikivinen.birtdemo.view;

import javax.annotation.PostConstruct;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringView(name = DefaultView.VIEW_NAME)
@SuppressWarnings("serial")
public class DefaultView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "";
	public static final FontAwesome ICON = FontAwesome.APPLE;

	@PostConstruct
	void init() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	      String name = auth.getName(); //get logged in username
	      
	      Label label = new Label("Hello, " + name);
	      label.setPrimaryStyleName(ValoTheme.LABEL_LARGE);
	      addComponent(label);
	      setComponentAlignment(label, Alignment.TOP_CENTER);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
