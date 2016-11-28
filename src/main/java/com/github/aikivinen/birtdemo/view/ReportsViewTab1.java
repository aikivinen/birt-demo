package com.github.aikivinen.birtdemo.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.github.aikivinen.birtdemo.ReportEngineHandlerImpl;
import com.github.aikivinen.birtdemo.ReportEngineHandlerImpl.Format;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Scope("prototype")
@Component
public class ReportsViewTab1 extends VerticalLayout {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private MessageSource messageSource;

	private String reportPath;

	@Autowired
	private ReportEngineHandlerImpl reHandler;

	private ComboBox cBox;
	private Button download;
	private OptionGroup optionGroup;
	private ByteArrayOutputStream bos;
	private Logger logger = LoggerFactory.getLogger(ReportsViewTab1.class);

	private FileDownloader fileDownloader;

	private	Authentication auth;
	private String userName;

	@PostConstruct
	void init() {
		auth = SecurityContextHolder.getContext().getAuthentication();
		userName = auth.getName();
		SQLContainer users = (SQLContainer) context.getBean("sqlContainer",
				"users", false);
		
		Item user = users.getItem(new RowId(userName));
		Property prop = user.getItemProperty("right_print_report");
		boolean hasRight = (boolean) prop.getValue();

		if (hasRight) {

			reportPath = VaadinService.getCurrent().getBaseDirectory()
					.getAbsolutePath()
					+ "/report-designs/";

			setSpacing(true);
			addComponent(new Label(messageSource.getMessage(
					"caption.viewreports", null, getLocale())));
			addComponent(buildCombobox());
			addComponent(new Label("Select file format"));
			addComponent(buildOptionGroup());

			download = new Button("Download");
			download.setIcon(FontAwesome.DOWNLOAD);
			download.setVisible(false);
			download.addClickListener(event -> {
				SQLContainer logContainer = (SQLContainer) context.getBean(
						"sqlContainer", "audit_log", false);
				Date date = new Date();
				Object id = logContainer.addItem();
				Item item = logContainer.getItem(id);
				item.getItemProperty("date").setValue(date);
				item.getItemProperty("log")
						.setValue(
								String.format(
										"User '%s' downloaded a report. Design file: '%s'",
										userName, cBox.getValue()));
				try {
					logContainer.commit();
				} catch (Exception e) {
					logger.error("Exception committing audit log: ", e);
				}
			});
			addComponent(download);
		} else {
			addComponent(new Label("You are not allowed to generate reports"));
		}
	}

	/**
	 * Builds an OptionGroup which can be used to select file format for report.
	 * The built OptionGroup is initially invisible.
	 * 
	 * @return built OptionGroup
	 */
	private OptionGroup buildOptionGroup() {
		optionGroup = new OptionGroup();
		optionGroup.addItem(Format.HTML);
		optionGroup.addItem(Format.PDF);
		optionGroup.setVisible(false);

		optionGroup.addValueChangeListener(event -> {

			download.setVisible(true);
			setFileDownloader();
		});
		return optionGroup;
	}

	private StreamResource getStream(Format format) {
		bos = reHandler.getFormattedReport(format);
		StreamResource res = null;
		if (bos != null) {
			res = new StreamResource(new StreamSource() {
				@Override
				public InputStream getStream() {
					return new ByteArrayInputStream(bos.toByteArray());
				}
			}, new Date().toString().replace(' ', '-') + "report."
					+ format.toString().toLowerCase());
		}
		return res;
	}

	private ComboBox buildCombobox() {
		cBox = new ComboBox();
		cBox.setNullSelectionAllowed(false);
		cBox.setInputPrompt(messageSource.getMessage("caption.reportbox", null,
				getLocale()));

		// get file listing
		try {
			Files.walk(Paths.get(reportPath)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					cBox.addItem(filePath.getFileName().toString());
				}
			});
		} catch (IOException e) {
			logger.error("Caught exception: ", e);
		}
		cBox.addValueChangeListener(event -> {
			if (event.getProperty().getValue() != null) {
				reHandler.setDesing(reportPath + cBox.getValue());
				optionGroup.setVisible(true);
				setFileDownloader();
			}

		});
		return cBox;
	}

	private FileDownloader setFileDownloader() {
		if (optionGroup.getValue() == null)
			return null;

		StreamResource stream = getStream((Format) optionGroup.getValue());
		if (fileDownloader == null) {
			fileDownloader = new FileDownloader(stream);
			fileDownloader.extend(download);
		} else {
			fileDownloader.setFileDownloadResource(stream);
		}
		return fileDownloader;
	}

}
