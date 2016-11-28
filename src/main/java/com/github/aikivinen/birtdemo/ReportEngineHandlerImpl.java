package com.github.aikivinen.birtdemo;

import java.io.ByteArrayOutputStream;

import javax.annotation.PostConstruct;
import javax.naming.OperationNotSupportedException;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.github.aikivinen.birtdemo.config.BirtConfig;

@Service
public class ReportEngineHandlerImpl {

	public enum Format {
		HTML, PDF
	}

	
	private IReportEngine engine;
	private Logger logger;
	private IReportRunnable design;

	@Autowired
	ApplicationContext context;

	@PostConstruct
	void init() {
		logger = LoggerFactory.getLogger(ReportEngineHandlerImpl.class);
		engine = (IReportEngine) context.getBean("engineFactory");
		logger.info("Loaded ReportEngine: " + engine.toString());
	}

	public void setDesing(String desingPath) {
		logger.info("Design path: " + desingPath);
		try {
			design = engine.openReportDesign(desingPath);
		} catch (EngineException e) {
			logger.error("Caught exception: ", e);
		}
	}

	
	
	public ByteArrayOutputStream getFormattedReport(Format format) {
		if (design == null)
			throw new IllegalStateException("Desing is null. Use setDesign(design) before getReport(format)");

		IRunAndRenderTask task = engine.createRunAndRenderTask(design);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IRenderOption options = new RenderOption();
		switch (format) {
			case HTML:
				HTMLRenderOption	htmlOptions = new HTMLRenderOption(options);
				htmlOptions.setOutputFormat("html");
			// htmlOptions.setEmbeddable(true);
			break;
			case PDF:
				PDFRenderOption pdfOptions = new PDFRenderOption(options);
				pdfOptions.setOutputFormat("pdf");
			break;
			default:
				throw new IllegalArgumentException("Format '"+format.toString()+"' not supported");
		}

		options.setOutputStream(bos);
		task.setRenderOption(options);

		try {
			task.run();
		} catch (EngineException e) {
			logger.error("Caught exception: ", e);
		} finally {
			task.close();
		}
		return bos;
	}
}
