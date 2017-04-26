package com.github.aikivinen.birtdemo.view;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import com.github.aikivinen.birtdemo.domain.User;
import com.github.aikivinen.birtdemo.jpa.repository.UserRepository;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontIcon;
import com.vaadin.shared.ui.grid.ColumnResizeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.MenuBar.MenuItem;

@SpringView(name = UserEditView.VIEW_NAME)
@SuppressWarnings("serial")
public class UserEditView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "users";
	public static final FontIcon ICON = VaadinIcons.USERS;

	private Logger logger = LoggerFactory.getLogger(UserEditView.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private UserRepository userRepository;

	private Grid<User> table;

	// bar
	private HorizontalLayout upperBar;
	private MenuBar menuBar;
	private MenuItem menuButtonRemove;
	private TextField filter;
	private Button clearFilter;

	@PostConstruct
	void init() {
		setMargin(false);
		setupTable();
		buildBar();

		addComponent(upperBar);
		addComponent(table);

		setSizeFull();
		setExpandRatio(table, 1);
	}

	private void buildBar() {
		upperBar = new HorizontalLayout();
		upperBar.setWidth("100%");

		filter = new TextField();

		filter.setPlaceholder(messageSource.getMessage("caption.filterdesc", null, getLocale()));
		filter.addValueChangeListener(e -> {
			// TODO: filter stuff...
		});

		clearFilter = new Button(VaadinIcons.FILE_REMOVE);
		clearFilter.setDescription(messageSource.getMessage("caption.filterdesc", null, getLocale()));
		clearFilter.setEnabled(false);
		clearFilter.addClickListener(event -> {
			filter.setValue("");
			clearFilter.setEnabled(false);
		});

		// menuBar
		menuBar = new MenuBar();
		menuBar.setWidth("100.0%");
		menuBar.addItem(messageSource.getMessage("caption.add", null, getLocale()), VaadinIcons.PLUS, selectedItem -> {
			UserEditWindow win = new UserEditWindow();
			win.setFieldDataSource(new User());
			UI.getCurrent().addWindow(win);
		});

		menuBar.addItem(messageSource.getMessage("caption.edit", null, getLocale()), VaadinIcons.EDIT, selectedItem -> {
			UserEditWindow win = new UserEditWindow();
			win.setFieldDataSource(table.getSelectedItems().stream().findFirst().orElse(new User()));
			UI.getCurrent().addWindow(win);

		});

		menuButtonRemove = menuBar.addItem(messageSource.getMessage("caption.delete", null, getLocale()),
				VaadinIcons.MINUS, selectedItem -> {
					userRepository.delete(table.getSelectedItems());
					menuButtonRemove.setEnabled(false);
					table.setItems(userRepository.findAll());
				});

		menuButtonRemove.setEnabled(false);

		upperBar.addComponent(menuBar);
		upperBar.addComponent(filter);
		upperBar.addComponent(clearFilter);
		upperBar.setExpandRatio(menuBar, 1.0f);

	}

	/**
	 * Setup table
	 * 
	 * @return the table
	 */

	private void setupTable() {
		table = new Grid<>();
		table.setSizeFull();

		table.setSelectionMode(SelectionMode.SINGLE);
		table.setColumnResizeMode(ColumnResizeMode.ANIMATED);
		table.setColumnReorderingAllowed(true);


		table.addColumn(User::getUsername).setCaption(messageSource.getMessage("caption.username", null, getLocale()));
		table.addColumn(User::getEmail).setCaption(messageSource.getMessage("caption.email", null, getLocale()));
		table.addColumn(User::getPhone).setCaption(messageSource.getMessage("caption.phone", null, getLocale()));

		table.addColumn(User::isRightToPrintReport)
				.setCaption(messageSource.getMessage("caption.rightprint", null, getLocale()));

		table.addColumn(User::isRightToEditReport)
				.setCaption(messageSource.getMessage("caption.rightedit", null, getLocale()));

		table.addColumn(User::isRightToAddReport)
				.setCaption(messageSource.getMessage("caption.rightadd", null, getLocale()));

		table.addColumn(User::isRightToRemoveReport)
				.setCaption(messageSource.getMessage("caption.rightremove", null, getLocale()));

		table.addSelectionListener(u -> {
			menuButtonRemove.setEnabled(!u.getAllSelectedItems().isEmpty());

		});

		List<User> users = userRepository.findAll();
		table.setItems(users);

	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	public class UserEditWindow extends Window {

		private Logger logger = LoggerFactory.getLogger(UserEditWindow.class);

		private FormLayout winContent;

		private TextField userid;
		private TextField password;
		private TextField email;
		private TextField phone;

		private CheckBox rightPrint;
		private CheckBox rightEdit;
		private CheckBox rightAdd;
		private CheckBox rightRemove;

		private HorizontalLayout buttons;
		private Button cancel;
		private Button ok;

		private Binder<User> binder;

		private User bean;

		public UserEditWindow() {
			setModal(true);
			setClosable(false);
			setCaption("Edit user");
			center();

			winContent = new FormLayout();
			buttons = new HorizontalLayout();

			winContent.setMargin(true);
			buttons.setMargin(true);
			setContent(winContent);
			setWidth("500px");

			userid = new TextField(messageSource.getMessage("caption.username", null, getLocale()));
			email = new TextField(messageSource.getMessage("caption.email", null, getLocale()));
			phone = new TextField(messageSource.getMessage("caption.phone", null, getLocale()));
			password = new TextField(messageSource.getMessage("caption.password", null, getLocale()));

			rightPrint = new CheckBox(messageSource.getMessage("caption.rightprint", null, getLocale()));
			rightEdit = new CheckBox(messageSource.getMessage("caption.rightedit", null, getLocale()));
			rightAdd = new CheckBox(messageSource.getMessage("caption.rightadd", null, getLocale()));
			rightRemove = new CheckBox(messageSource.getMessage("caption.rightremove", null, getLocale()));

			Arrays.stream(new AbstractField[] { userid, email, phone, password, rightPrint, rightEdit, rightAdd,
					rightRemove }).forEach(e -> e.setWidth("100%"));

			setUpBinder();

			cancel = new Button(messageSource.getMessage("caption.cancel", null, getLocale()));
			cancel.addClickListener(e -> {
				close();
				table.setItems(userRepository.findAll());
			});

			ok = new Button(messageSource.getMessage("caption.ok", null, getLocale()));
			ok.addClickListener(e -> {

				try {
					this.bean = Optional.ofNullable(bean).orElse(new User());
					binder.writeBean(bean);
					userRepository.save(bean);
					Notification.show("User saved");
					close();
					table.setItems(userRepository.findAll());
				} catch (ValidationException e1) {
					Notification.show("Bad values. Go fix them... please?");
					logger.info(e1.getMessage(), e1);
				}
			});

			buttons = new HorizontalLayout();
			buttons.setSpacing(true);
			buttons.addComponent(ok);
			buttons.addComponent(cancel);

			winContent.addComponents(userid, password, email, phone, rightPrint, rightEdit, rightAdd, rightRemove,
					buttons);

		}

		/**
		 * Set the user to be edited in this Window, e.i. bind the given User to
		 * fields
		 * 
		 * @param user
		 */
		public void setFieldDataSource(User user) {
			this.bean = user;
			this.binder.setBean(user);
		}

		/**
		 * Set the fields on the window as read only
		 */
		public void setReadOnly(boolean readonly) {
			this.binder.setReadOnly(readonly);
			ok.setEnabled(!readonly);
		}

		private void setUpBinder() {
			this.binder = new Binder<>(User.class);
			binder.forField(userid).bind(User::getUsername, User::setUsername);

			// TODO: redo this
			binder.forField(password).bind(User::getPassword, User::setPassword);

			binder.forField(email).bind(User::getEmail, User::setEmail);
			binder.forField(phone).bind(User::getPhone, User::setPhone);

			// rights management
			binder.forField(rightAdd).bind(User::isRightToAddReport, User::setRightToAddReport);
			binder.forField(rightEdit).bind(User::isRightToEditReport, User::setRightToEditReport);
			binder.forField(rightPrint).bind(User::isRightToPrintReport, User::setRightToPrintReport);
			binder.forField(rightRemove).bind(User::isRightToRemoveReport, User::setRightToRemoveReport);

		}
	}

}
