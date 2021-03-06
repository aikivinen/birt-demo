package com.github.aikivinen.birtdemo.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

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
import org.springframework.util.StringUtils;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

@Scope("prototype")
@Component
public class ReportsViewTab2 extends VerticalLayout {

	private static final Logger logger = LoggerFactory.getLogger(ReportsViewTab2.class);

	@Autowired
	private MessageSource messageSource;

	private String reportPath;

	private Button removeReport;

	private ComboBox<Path> cBox;

	private Button addReport;

	private Button saveButton;

	private TextArea textArea;
	// private ObjectProperty<String> textAreaProperty;

	@PostConstruct
	void init() {

		// auth = SecurityContextHolder.getContext().getAuthentication();
		// userName = auth.getName();
		// SQLContainer users = (SQLContainer) context.getBean("sqlContainer",
		// "users", false);
		//
		// Item user = users.getItem(new RowId(userName));
		//
		// if ((boolean) user.getItemProperty("right_add_report").getValue()
		// || (boolean) user.getItemProperty("right_remove_report").getValue()
		// || (boolean) user.getItemProperty("right_edit_report").getValue()) {

		reportPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/report-designs/";
		setMargin(true);
		setSpacing(true);

		addComponent(new Label(messageSource.getMessage("caption.editreports", null, getLocale())));

		HorizontalLayout horizontal = new HorizontalLayout();

		cBox = ViewComponents.buildFileListingComboBox(messageSource.getMessage("caption.reportbox", null, getLocale()),
				"Select report", Paths.get(reportPath), event -> {
					Optional.ofNullable(event).map(ValueChangeEvent::getValue).ifPresent(ev -> {
						try {
							textArea.setValue(
									StringUtils.collectionToDelimitedString(Files.readAllLines(cBox.getValue()), "\n"));
						} catch (IOException e) {
							logger.error("Caught exception: ", e);
						}

					});
				});

		removeReport = new Button(messageSource.getMessage("caption.removereport", null, getLocale()), event -> {
			try {
				Files.deleteIfExists(cBox.getValue());
				cBox.setItems(Files.walk(Paths.get(reportPath)));
			} catch (IOException e) {
				logger.error("Caught exception: ", e);
			} 
		});

		addReport = new Button(messageSource.getMessage("caption.addreport", null, getLocale()), event -> {
			UI.getCurrent().addWindow(new UploadWindow());
		});

		saveButton = new Button(messageSource.getMessage("caption.save", null, getLocale()), event -> {
			try {
				Files.write(Paths.get(reportPath + cBox.getValue()), textArea.getValue().getBytes(),
						StandardOpenOption.WRITE);
			} catch (IOException e) {
				logger.error("Caught exception: ", e);
			}

		});

		textArea = new TextArea();
		textArea.setWordWrap(false);
		textArea.setSizeFull();

		horizontal.addComponent(cBox);
		horizontal.addComponent(removeReport);
		horizontal.addComponent(addReport);
		horizontal.setSpacing(true);

		addComponent(horizontal);
		addComponent(textArea);
		addComponent(saveButton);

		setExpandRatio(textArea, 1);
		setSizeFull();

		// if (!(boolean) user.getItemProperty("right_add_report").getValue()) {
		// addReport.setEnabled(false);
		// }
		//
		// if (!(boolean)
		// user.getItemProperty("right_remove_report").getValue()) {
		// removeReport.setEnabled(false);
		// }
		//
		// if (!(boolean) user.getItemProperty("right_edit_report").getValue())
		// {
		// saveButton.setEnabled(false);
		// textArea.setVisible(false);
		// }
		// }
		//
		// else {
		// addComponent(new Label("You are not allowed to edit, add or remove
		// report designs"));
		// }

	}

	private class UploadWindow extends Window {
		VerticalLayout content;
		Button closeButton;

		public UploadWindow() {
			setCaption(messageSource.getMessage("caption.uploadwindowtitle", null, getLocale()));

			setResizable(false);
			setDraggable(false);
			setClosable(true);
			setModal(true);
			center();

			content = new VerticalLayout();

			FileReceiver rec = new FileReceiver();
			Upload upload = new Upload(messageSource.getMessage("caption.selectfile", null, getLocale()), rec);
			upload.setButtonCaption(messageSource.getMessage("caption.startupload", null, getLocale()));
			upload.addSucceededListener(rec);

			closeButton = new Button(messageSource.getMessage("caption.close", null, getLocale()), e -> close());

			content.addComponent(upload);
			content.addComponent(closeButton);
			setContent(content);
			content.setSizeFull();
			setWidth("400px");
			setHeight("200px");

		}
	}

	public class FileReceiver implements Receiver, SucceededListener {

		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			File file = null;
			FileOutputStream fos = null;
			file = new File(reportPath + filename);

			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				logger.error("Caught exception: ", e);
				throw new RuntimeException("File not found");
			}
			return fos;
		}

		@Override
		public void uploadSucceeded(SucceededEvent event) {
			Notification.show(messageSource.getMessage("caption.uploadsuccess", null, getLocale()),
					Notification.Type.HUMANIZED_MESSAGE);
			logger.info("File uploaded successfully");
		}
	}

}
