package com.github.aikivinen.birtdemo.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.aikivinen.birtdemo.ReportEngineHandlerImpl;
import com.github.aikivinen.birtdemo.ReportEngineHandlerImpl.Format;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
@Scope("prototype")
@Component
public class ReportsViewTab1 extends VerticalLayout {

	private static final Logger logger = LoggerFactory.getLogger(ReportsViewTab1.class);

	@Autowired
	private ApplicationContext context;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ReportEngineHandlerImpl reHandler;

	private ComboBox<Path> cBox;
	private Button download;
	private RadioButtonGroup<Format> optionGroup;
	private ByteArrayOutputStream bos;

	private FileDownloader fileDownloader;

	@PostConstruct
	void init() {
		boolean hasRight = checkUserPrintRight();

		if (hasRight) {

			setSpacing(true);
			addComponent(new Label(messageSource.getMessage("caption.viewreports", null, getLocale())));

			String placeholder = messageSource.getMessage("caption.reportbox", null, getLocale());

			Path reportPath = Paths
					.get(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/report-designs/");

			cBox = ViewComponents.buildFileListingComboBox("File", placeholder, reportPath, event -> {
				reHandler.setDesing(event.getValue().toString());
				optionGroup.setVisible(true);
				setFileDownloader();
			});

			addComponent(cBox);
			addComponent(new Label("Select file format"));
			addComponent(buildOptionGroup());

			download = new Button("Download");
			download.setIcon(VaadinIcons.DOWNLOAD);
			download.setVisible(false);

			download.addClickListener(event -> {
				addAuditLogEntry();
			});

			addComponent(download);
		} else {
			addComponent(new Label("You are not allowed to generate reports"));
		}
	}

	private void addAuditLogEntry() {
		/*
		 * SQLContainer logContainer = (SQLContainer)
		 * context.getBean("sqlContainer", "audit_log", false); Date date = new
		 * Date(); Object id = logContainer.addItem(); Item item =
		 * logContainer.getItem(id);
		 * item.getItemProperty("date").setValue(date);
		 * item.getItemProperty("log").setValue(
		 * String.format("User '%s' downloaded a report. Design file: '%s'",
		 * userName, cBox.getValue())); try { logContainer.commit(); } catch
		 * (Exception e) { logger.error("Exception committing audit log: ", e);
		 * }
		 */
	}

	private boolean checkUserPrintRight() {
		return true;

		/*
		 * auth = SecurityContextHolder.getContext().getAuthentication();
		 * userName = auth.getName(); SQLContainer users = (SQLContainer)
		 * context.getBean("sqlContainer", "users", false);
		 * 
		 * Item user = users.getItem(new RowId(userName)); Property prop =
		 * user.getItemProperty("right_print_report"); boolean hasRight =
		 * (boolean) prop.getValue(); return hasRight;
		 */
	}

	/**
	 * Builds an OptionGroup which can be used to select file format for report.
	 * The built OptionGroup is initially invisible.
	 * 
	 * @return built OptionGroup
	 */

	private RadioButtonGroup<Format> buildOptionGroup() {
		optionGroup = new RadioButtonGroup<>();
		optionGroup.setItems(Format.HTML, Format.PDF);

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
			}, new Date().toString().replace(' ', '-') + "report." + format.toString().toLowerCase());
		}
		return res;
	}

	private FileDownloader setFileDownloader() {
		if (optionGroup.getValue() == null)
			return null;

		StreamResource stream = getStream(optionGroup.getValue());
		if (fileDownloader == null) {
			fileDownloader = new FileDownloader(stream);
			fileDownloader.extend(download);
		} else {
			fileDownloader.setFileDownloadResource(stream);
		}
		return fileDownloader;
	}

}
