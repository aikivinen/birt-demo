package com.github.aikivinen.birtdemo.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.ui.ComboBox;

public class ViewComponents {

	private static final Logger logger = LoggerFactory.getLogger(ViewComponents.class);

	public static ComboBox<Path> buildFileListingComboBox(String caption, String placeholder, Path reportPath,
			ValueChangeListener<Path> listener) {

		ComboBox<Path> cBox = new ComboBox<>();
		cBox.setEmptySelectionAllowed(false);
		cBox.setPlaceholder(placeholder);

		// get file listing
		try {
			cBox.setItems(Files.walk(reportPath));
			
		} catch (IOException e) {
			logger.error("Caught exception: ", e);
		}

		Optional.ofNullable(listener).ifPresent(l -> {
			cBox.addValueChangeListener(l);
		});

		return cBox;
	}

}
