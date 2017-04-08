package com.github.aikivinen.birtdemo.view;

import java.sql.SQLException;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.MenuBar.MenuItem;


@SpringView(name = UserEditView.VIEW_NAME)
@SuppressWarnings("serial")
public class UserEditView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "users";
	public static final FontAwesome ICON = FontAwesome.USERS;

	private Logger logger = LoggerFactory.getLogger(UserEditView.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ApplicationContext context;

	private Table table;
	private SQLContainer sqlContainer;

	// bar
	private HorizontalLayout upperBar;
	private MenuBar menuBar;
	private MenuItem menuButtonRemove;
	private TextField filter;
	private Button clearFilter;

	@PostConstruct
	void init() {
		sqlContainer = (SQLContainer) context.getBean("sqlContainer", "users", true);
		addComponent(buildBar());
		addComponent(setupTable());
		setSizeFull();
		setExpandRatio(table, 1);
	}

	private Component buildBar() {
		upperBar = new HorizontalLayout();
		upperBar.setWidth("100%");

		filter = new TextField();
		filter.setInputPrompt(messageSource.getMessage("caption.filterdesc", null, getLocale()));
		filter.addTextChangeListener(event -> {
			sqlContainer.removeAllContainerFilters();
			sqlContainer.addContainerFilter(new UserFilter(event.getText()));
		});

		clearFilter = new Button(FontAwesome.TIMES);
		clearFilter.setDescription(messageSource.getMessage("caption.filterdesc", null, getLocale()));
		clearFilter.setEnabled(false);
		clearFilter.addClickListener(event -> {
			filter.setValue("");
			sqlContainer.removeAllContainerFilters();
			clearFilter.setEnabled(false);
		});

		// menuBar
		menuBar = new MenuBar();
		menuBar.setWidth("100.0%");
		menuBar.addItem(messageSource.getMessage("caption.add", null, getLocale()), FontAwesome.PLUS_SQUARE, selectedItem -> {
			Object itemId = sqlContainer.addItem();
			UserEditWindow win = new UserEditWindow(false) {

				@Override
				protected void okClick() {
					try {
						sqlContainer.commit();
						close();
					} catch (UnsupportedOperationException e) {
						logger.error("Exception caught: ", e);
					} catch (SQLException e) {
						logger.error("Exception caught: ", e);
					}
				}

				@Override
				protected void cancelClick() {
					sqlContainer.removeItem(itemId);
					close();
				}
			};
			win.setFieldDataSource(sqlContainer.getItem(itemId));
			UI.getCurrent().addWindow(win);
		});

		menuBar.addItem(messageSource.getMessage("caption.edit", null, getLocale()), FontAwesome.EDIT, selectedItem -> {

			UserEditWindow win = new UserEditWindow(true) {

				@Override
				protected void okClick() {
					try {
						sqlContainer.commit();
						close();
					} catch (UnsupportedOperationException e) {
						logger.error("Exception caught: ", e);
					} catch (SQLException e) {
						logger.error("Exception caught: ", e);
					}
				}

				@Override
				protected void cancelClick() {
					close();
				}
			};
			win.setFieldDataSource(sqlContainer.getItem(table.getValue()));
			UI.getCurrent().addWindow(win);

		});

		menuButtonRemove = menuBar.addItem(messageSource.getMessage("caption.delete", null, getLocale()), FontAwesome.MINUS_SQUARE, selectedItem -> {
			Object iid = table.getValue();
			sqlContainer.removeItem(iid);

			try {
				sqlContainer.commit();
			} catch (SQLException e) {
				logger.error("Exception caught: ", e);
			}
			menuButtonRemove.setEnabled(false);
		});

		menuButtonRemove.setEnabled(false);

		upperBar.addComponent(menuBar);
		upperBar.addComponent(filter);
		upperBar.addComponent(clearFilter);
		upperBar.setExpandRatio(menuBar, 1.0f);

		return upperBar;
	}

	/**
	 * Setup table
	 * 
	 * @return the table
	 */

	private Table setupTable() {
		table = new Table();
		table.setSizeFull();

		table.setSelectable(true);
		table.setImmediate(true);
		table.setNullSelectionAllowed(false);

		table.setContainerDataSource(sqlContainer);

		table.setColumnHeader("username", messageSource.getMessage("caption.username", null, getLocale()));
		table.setColumnHeader("email", messageSource.getMessage("caption.email", null, getLocale()));
		table.setColumnHeader("phone", messageSource.getMessage("caption.phone", null, getLocale()));
		table.setColumnHeader("right_print_report", messageSource.getMessage("caption.rightprint", null, getLocale()));
		table.setColumnHeader("right_edit_report", messageSource.getMessage("caption.rightedit", null, getLocale()));
		table.setColumnHeader("right_add_report", messageSource.getMessage("caption.rightadd", null, getLocale()));
		table.setColumnHeader("right_remove_report", messageSource.getMessage("caption.rightremove", null, getLocale()));

		table.setVisibleColumns(new Object[] { "username" ,"email", "phone", "right_print_report", "right_edit_report", "right_add_report", "right_remove_report","authority" ,"enabled" });

		table.addValueChangeListener(e -> {
			menuButtonRemove.setEnabled(true);
		});
		return table;
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	public class UserFilter implements Filter {
		private String needle;

		public UserFilter(String needle) {
			this.needle = needle.toLowerCase();
		}

		@Override
		public boolean passesFilter(Object itemId, Item item) {
			String haystack = ("" + item.getItemProperty("username").getValue()).toLowerCase();
			return haystack.contains(needle);
		}

		@Override
		public boolean appliesToProperty(Object id) {
			return true;
		}

	}
	
	public abstract class UserEditWindow extends Window {
			
		private FormLayout winContent;
		private HorizontalLayout buttons;
		private FieldGroup fields;
		private TextField userid;
		private TextField password;
		private TextField email;
		private TextField phone;
		private CheckBox rightPrint, rightEdit, rightAdd, rightRemove;
		private Button cancel;
		private Button ok;
		private SQLContainer sqlContainer;
		
		Logger logger = LoggerFactory.getLogger(UserEditWindow.class);

		private boolean editMode;

		public UserEditWindow(boolean editMode) {
				this.editMode = editMode;
		}
		
		@PostConstruct
		void init() {
			
			setModal(true);
			setClosable(false);
			center();
			
			winContent = new FormLayout();
			buttons = new HorizontalLayout();
			fields = new FieldGroup();
			
			winContent.setMargin(true);
			buttons.setMargin(true);
			setContent(winContent);
			setWidth("300px");
			
			sqlContainer = (SQLContainer) context.getBean("sqlContainer","users",true);
			
			userid = new TextField(messageSource.getMessage("caption.username", null, getLocale()));
			email = new TextField(messageSource.getMessage("caption.email", null, getLocale()));
			phone = new TextField(messageSource.getMessage("caption.phone", null, getLocale()));
			password = new TextField(messageSource.getMessage("caption.password", null, getLocale()));
			cancel = new Button(messageSource.getMessage("caption.cancel", null, getLocale()), event -> {cancelClick();});
			
			ok = new Button(messageSource.getMessage("caption.ok", null, getLocale()), event -> {
					try {
						fields.commit();
					} catch (CommitException e) {
						logger.error("CommitException at UserEditWindow", e);
					}
					okClick();
			});

			rightPrint = new CheckBox(messageSource.getMessage("caption.rightprint", null, getLocale()));
			rightEdit = new CheckBox(messageSource.getMessage("caption.rightedit", null, getLocale()));
			rightAdd = new CheckBox(messageSource.getMessage("caption.rightadd", null, getLocale()));
			rightRemove = new CheckBox(messageSource.getMessage("caption.rightremove", null, getLocale()));

			buttons = new HorizontalLayout();
			buttons.setSpacing(true);
			buttons.addComponent(ok);
			buttons.addComponent(cancel);

			userid.setNullRepresentation("");
			userid.setRequired(true);
			email.setNullRepresentation("");
			phone.setNullRepresentation("");

			fields.bind(userid, "username");
			fields.bind(email, "email");
			fields.bind(phone, "phone");
			fields.bind(password, password);
			fields.bind(rightPrint, "right_print_report");
			fields.bind(rightEdit, "right_edit_report");
			fields.bind(rightAdd, "right_add_report");
			fields.bind(rightRemove, "right_remove_report");

			winContent.addComponents(userid, password, email, phone, rightPrint, rightEdit, rightAdd, rightRemove, buttons);

			if (editMode) {
				userid.setReadOnly(true);
			} else {
				/*
				userid.addValidator(new Validator() {

					@Override
					public void validate(Object value) throws Exception {
						String val = (String) value;
						if (val.isEmpty() || val == null)
							throw new Exception(messageSource.getMessage("caption.usernamenotset", null, getLocale()));

						for (Iterator iterator = sqlContainer.getItemIds().iterator(); iterator.hasNext();) {
							String uname = (String) sqlContainer.getContainerProperty(iterator.next(), "username").getValue();
							if (uname.equals(val))
								throw new Exception(messageSource.getMessage("caption.usernametaken", null, getLocale()));
						}
						return;
					}
				});
				*/
			}

			
		}

		protected abstract void okClick();

		protected abstract void cancelClick();

		public void setFieldDataSource(Item itemDataSource) {
			fields.setItemDataSource(itemDataSource);
		}

	}

}
