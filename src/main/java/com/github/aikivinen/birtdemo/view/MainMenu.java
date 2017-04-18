package com.github.aikivinen.birtdemo.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class MainMenu extends CssLayout {

	private final String MENU_TITLE = "Spring+Vaadin";

	public MainMenu() {
		setPrimaryStyleName(ValoTheme.MENU_ROOT);

		Label title = new Label(MENU_TITLE);
		title.setPrimaryStyleName(ValoTheme.MENU_TITLE);
		title.addStyleName(ValoTheme.LABEL_BOLD);
		addComponent(title);

		addComponent(new ValoMenuItemButton("Welcome", DefaultView.VIEW_NAME, DefaultView.ICON));
		addComponent(new ValoMenuItemButton("Users", UserEditView.VIEW_NAME, UserEditView.ICON));
		addComponent(new ValoMenuItemButton("Data", ValueEditView.VIEW_NAME, ValueEditView.ICON));
		addComponent(new ValoMenuItemButton("Reports", ReportsView.VIEW_NAME, ReportsView.ICON));
		addComponent(new ValoMenuItemButton("Audit Log", AuditLogView.VIEW_NAME, AuditLogView.ICON));

		Button logout = new Button("Log out", VaadinIcons.SIGN_OUT);
		logout.setPrimaryStyleName(ValoTheme.MENU_ITEM);

		logout.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Resource res = new ExternalResource("logout");
				final ResourceReference rr = ResourceReference.create(res, event.getButton().getParent(), "logout");
				Page.getCurrent().open(rr.getURL(), null);
			}
		});
		addComponents(new Label(), logout);
	}

	public class ValoMenuItemButton extends Button {

		// private static final String STYLE_SELECTED = "selected";

		public ValoMenuItemButton(String caption, String viewName, FontIcon icon) {
			setPrimaryStyleName(ValoTheme.MENU_ITEM);
			setIcon(icon);
			setCaption(caption);
			addClickListener(event -> getUI().getNavigator().navigateTo(viewName));
		}
	}
}