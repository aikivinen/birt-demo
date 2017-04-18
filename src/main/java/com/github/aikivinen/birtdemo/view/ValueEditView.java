package com.github.aikivinen.birtdemo.view;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import com.github.aikivinen.birtdemo.domain.ValuesOne;
import com.github.aikivinen.birtdemo.jpa.repository.ValuesOneRepository;
import com.github.aikivinen.birtdemo.jpa.repository.ValuesTwoRepository;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

@SpringView(name = ValueEditView.VIEW_NAME)
@SuppressWarnings("serial")
public class ValueEditView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "data";
	public static final FontIcon ICON = VaadinIcons.DATABASE;

	private static final Logger logger = LoggerFactory.getLogger(ValueEditView.class);

	@Autowired
	private ApplicationContext context;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ValuesOneRepository valuesOneRepository;
	
	@Autowired
	private ValuesTwoRepository valuesTwoRepository;
	
//	private JpaRepository<? extends CrudDemoObject, Integer> jpaRepoSelection;
	
	// bar
	private HorizontalLayout upperBar;
	private MenuBar menuBar;
	private MenuItem menuButtonRemove;
	private TextField filter;
	private Button clearFilter;
//	private ComboBox<JpaRepository<? extends CrudDemoObject, Integer>> tableSelect;
	private MenuItem editButton;

	private Grid<ValuesOne> table;

	@PostConstruct
	void init() {

		buildBar();
		addComponent(upperBar);
		
		setupTable();
		addComponent(table);

		setExpandRatio(table, 1.0f);
		setSizeFull();
	}

	private HorizontalLayout buildBar() {

		upperBar = new HorizontalLayout();
		upperBar.setWidth("100%");

		clearFilter = new Button(VaadinIcons.RECYCLE);
		clearFilter.setDescription(messageSource.getMessage("caption.filterdesc", null, getLocale()));
		clearFilter.setEnabled(false);
		clearFilter.addClickListener(e -> {
			filter.setValue("");
			clearFilter.setEnabled(false);
		});

		filter = new TextField();
		filter.setPlaceholder(messageSource.getMessage("caption.filterdesc", null, getLocale()));
		filter.addValueChangeListener(event -> {
			clearFilter.setEnabled(true);
		});

		menuBar = new MenuBar();
		menuBar.setWidth("100%");
		menuBar.addItem(messageSource.getMessage("caption.add", null, getLocale()), VaadinIcons.PLUS,
				selectedItem -> {
					valuesOneRepository.save(new ValuesOne());
				});

		menuButtonRemove = menuBar.addItem(messageSource.getMessage("caption.delete", null, getLocale()),
				VaadinIcons.MINUS, selectedItem -> {
					valuesOneRepository.delete(table.getSelectedItems());

				});

		editButton = menuBar.addItem(messageSource.getMessage("caption.edit", null, getLocale()), VaadinIcons.EDIT,
				new Command() {

					@Override
					public void menuSelected(MenuItem selectedItem) {
/*
 *  Garbage
						if (table.isEditable()) {
							// disable table editing
							table.setEditable(false);

							// save changes
							try {
								sqlContainer.commit();
							} catch (UnsupportedOperationException | SQLException e) {
								logger.error("Caught exception: ", e);
								e.printStackTrace();
							} catch (OptimisticLockException e) {
								Notification.show(
										messageSource.getMessage("caption.optlockerroredit", null, getLocale()),
										Notification.Type.ERROR_MESSAGE);
								logger.error("Caught exception: ", e);
							}

							// change button caption to "edit"
							editButton.setText(messageSource.getMessage("caption.edit", null, getLocale()));
							editButton.setIcon(FontAwesome.EDIT);
						} else {
							// make table editable
							table.setEditable(true);

							// change button caption to "save"
							editButton.setText(messageSource.getMessage("caption.save", null, getLocale()));
							editButton.setIcon(FontAwesome.SAVE);

						}
 */
					}
				}
				);

		menuButtonRemove.setEnabled(false);

		upperBar.addComponent(menuBar);

//		tableSelect = new ComboBox<JpaRepository<? extends CrudDemoObject, Integer>>();
//		tableSelect.setItems(valuesOneRepository, valuesTwoRepository);
//		
//		tableSelect.addValueChangeListener(e -> {
//			this.jpaRepoSelection = e.getValue();
//		});

		
//		upperBar.addComponent(tableSelect);
		upperBar.addComponent(filter);
		upperBar.addComponent(clearFilter);
		upperBar.setExpandRatio(menuBar, 1.0f);

		return upperBar;
	}

	/**
	 * Table setup
	 */
	private void setupTable() {
		table = new Grid<>();
		table.setSizeFull();
		table.setSelectionMode(SelectionMode.MULTI);
		table.addSelectionListener(e -> {
			menuButtonRemove.setEnabled(!e.getAllSelectedItems().isEmpty());
		});

		setTableDataSource();

	}

	/**
	 * Update table data source
	 */

	private void setTableDataSource() {
		table.setItems(valuesOneRepository.findAll());
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	/*
	 * public class ValueFilter implements Filter {
	 * 
	 * String needle;
	 * 
	 * public ValueFilter(String needle) { this.needle = needle; }
	 * 
	 * @Override public boolean passesFilter(Object itemId, Item item) throws
	 * UnsupportedOperationException { String haystack = ("" +
	 * item.getItemProperty("text_val1").getValue() +
	 * item.getItemProperty("text_val2").getValue() +
	 * item.getItemProperty("text_val3").getValue() +
	 * item.getItemProperty("int_val1").getValue().toString() +
	 * item.getItemProperty("int_val2").getValue().toString() +
	 * item.getItemProperty("int_val3").getValue().toString()).toLowerCase();
	 * return haystack.contains(needle); }
	 * 
	 * @Override public boolean appliesToProperty(Object propertyId) { return
	 * true; } }
	 */
}
