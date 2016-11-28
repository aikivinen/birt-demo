package com.github.aikivinen.birtdemo.view;

import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.OptimisticLockException;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.data.Container.Filter;


@SpringView(name = ValueEditView.VIEW_NAME)
@SuppressWarnings("serial")
public class ValueEditView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "data";
	public static final FontAwesome ICON = FontAwesome.DATABASE;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private MessageSource messageSource;

	private Logger logger = LoggerFactory.getLogger(ValueEditView.class);

	// bar
	private HorizontalLayout upperBar;
	private MenuBar menuBar;
	private MenuItem menuButtonRemove;
	private TextField filter;
	private Button clearFilter;
	private ComboBox tableSelect;
	private MenuItem editButton;

	private Table table;
	private SQLContainer sqlContainer;

	@PostConstruct
	void init() {
		sqlContainer = (SQLContainer) context.getBean("sqlContainer",
				"values1", true);

		addComponent(buildBar());
		addComponent(setupTable());

		setExpandRatio(table, 1.0f);
		setSizeFull();
	}

	private HorizontalLayout buildBar() {

		upperBar = new HorizontalLayout();
		upperBar.setWidth("100%");

		clearFilter = new Button(FontAwesome.TIMES);
		clearFilter.setDescription(messageSource.getMessage(
				"caption.filterdesc", null, getLocale()));
		clearFilter.setEnabled(false);
		clearFilter.addClickListener(e -> {
			filter.setValue("");
			sqlContainer.removeAllContainerFilters();
			clearFilter.setEnabled(false);
		});

		filter = new TextField();
		filter.setInputPrompt(messageSource.getMessage("caption.filterdesc",
				null, getLocale()));
		filter.addTextChangeListener(event -> {
			sqlContainer.removeAllContainerFilters();
			sqlContainer.addContainerFilter(new ValueFilter(event.getText()));
		});

		menuBar = new MenuBar();
		menuBar.setWidth("100.0%");
		menuBar.addItem(
				messageSource.getMessage("caption.add", null, getLocale()),
				FontAwesome.PLUS_SQUARE, selectedItem -> {
					sqlContainer.addItem();
					try {
						sqlContainer.commit();
					} catch (Exception e) {
						logger.error("Exception caught: ", e);
					}
				});

		menuButtonRemove = menuBar.addItem(messageSource.getMessage(
				"caption.delete", null, getLocale()), FontAwesome.MINUS_SQUARE,
				selectedItem -> {

					sqlContainer.removeItem(table.getValue());
					try {
						sqlContainer.commit();
					} catch (UnsupportedOperationException | SQLException e) {
						logger.error("Caught exception: ", e);
					} catch (OptimisticLockException e) {

						Notification.show(messageSource
								.getMessage("caption.optlockerrordelete", null,
										getLocale()),
								Notification.Type.ERROR_MESSAGE);
						logger.error("Caught exception: ", e);
					}

				});

		editButton = menuBar.addItem(
				messageSource.getMessage("caption.edit", null, getLocale()),
				FontAwesome.EDIT, new Command() {

					@Override
					public void menuSelected(MenuItem selectedItem) {

						if (table.isEditable()) {
							// disable table editing
							table.setEditable(false);

							// save changes
							try {
								sqlContainer.commit();
							} catch (UnsupportedOperationException
									| SQLException e) {
								logger.error("Caught exception: ", e);
								e.printStackTrace();
							} catch (OptimisticLockException e) {
								Notification.show(messageSource.getMessage(
										"caption.optlockerroredit", null,
										getLocale()),
										Notification.Type.ERROR_MESSAGE);
								logger.error("Caught exception: ", e);
							}

							// change button caption to "edit"
							editButton.setText(messageSource.getMessage(
									"caption.edit", null, getLocale()));
							editButton.setIcon(FontAwesome.EDIT);
						} else {
							// make table editable
							table.setEditable(true);

							// change button caption to "save"
							editButton.setText(messageSource.getMessage(
									"caption.save", null, getLocale()));
							editButton.setIcon(FontAwesome.SAVE);

						}
					}
				});

		menuButtonRemove.setEnabled(false);

		upperBar.addComponent(menuBar);

		tableSelect = new ComboBox();
		for (String str : new String[] { "Values 1", "Values 2", "Values 3" }) {
			tableSelect.addItem(str);
		}

		tableSelect.setNullSelectionAllowed(false);
		tableSelect.setValue("Values 1");
		tableSelect.setInputPrompt(messageSource.getMessage("caption.table",
				null, getLocale()));

		tableSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				String table = null;
				switch (tableSelect.getValue().toString()) {
				case "Values 1":
					table = "values1";
					break;
				case "Values 2":
					table = "values2";
					break;
				case "Values 3":
					table = "values3";
					break;
				default:
					table = "values1";
					break;
				}

				sqlContainer = (SQLContainer) context.getBean("sqlContainer",
						table, true);

				setTableDataSource();
				menuButtonRemove.setEnabled(false);

			}
		});
		upperBar.addComponent(tableSelect);
		upperBar.addComponent(filter);
		upperBar.addComponent(clearFilter);
		upperBar.setExpandRatio(menuBar, 1.0f);

		return upperBar;
	}

	/**
	 * Table setup
	 */
	private Table setupTable() {
		table = new Table();
		table.setSizeFull();
		table.setSelectable(true);
		table.setImmediate(true);
		table.setEditable(false);

		setTableDataSource();

		table.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				menuButtonRemove.setEnabled(true);

			}
		});
		return table;
	}

	/**
	 * Update table data source
	 */

	private void setTableDataSource() {
		table.setContainerDataSource(sqlContainer);

		table.setNullSelectionAllowed(false);
		table.setColumnHeader("text_val1", messageSource.getMessage(
				"caption.text_val1", null, getLocale()));
		table.setColumnHeader("text_val2", messageSource.getMessage(
				"caption.text_val2", null, getLocale()));
		table.setColumnHeader("text_val3", messageSource.getMessage(
				"caption.text_val3", null, getLocale()));

		table.setColumnHeader("int_val1", messageSource.getMessage(
				"caption.integer_val1", null, getLocale()));
		table.setColumnHeader("int_val2", messageSource.getMessage(
				"caption.integer_val2", null, getLocale()));
		table.setColumnHeader("int_val3", messageSource.getMessage(
				"caption.integer_val3", null, getLocale()));

		table.setVisibleColumns(new Object[] { "text_val1", "text_val2",
				"text_val3", "int_val1", "int_val2", "int_val3" });
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	public class ValueFilter implements Filter {

		String needle;

		public ValueFilter(String needle) {
			this.needle = needle;
		}

		@Override
		public boolean passesFilter(Object itemId, Item item)
				throws UnsupportedOperationException {
			String haystack = (""
					+ item.getItemProperty("text_val1").getValue()
					+ item.getItemProperty("text_val2").getValue()
					+ item.getItemProperty("text_val3").getValue()
					+ item.getItemProperty("int_val1").getValue().toString()
					+ item.getItemProperty("int_val2").getValue().toString() + item
					.getItemProperty("int_val3").getValue().toString())
					.toLowerCase();
			return haystack.contains(needle);
		}

		@Override
		public boolean appliesToProperty(Object propertyId) {
			return true;
		}
	}
}
