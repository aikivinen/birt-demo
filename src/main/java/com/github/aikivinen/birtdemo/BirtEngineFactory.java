package com.github.aikivinen.birtdemo;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Factory bean for the instance of the {@link IReportEngine report engine}.
 * 
 */
public class BirtEngineFactory implements FactoryBean, ApplicationContextAware,
		DisposableBean {

	private ApplicationContext context;
	private IReportEngine birtEngine;

	public void setApplicationContext(ApplicationContext ctx) {
		this.context = ctx;
	}

	public boolean isSingleton() {
		return true;
	}

	public void destroy() throws Exception {
		birtEngine.destroy();
		Platform.shutdown();
	}

	public IReportEngine getObject() {

		EngineConfig config = new EngineConfig();

		// This line injects the Spring Context into the BIRT Context
		// config.getAppContext().put("spring", this.context );

		try {
			Platform.startup(config);
		} catch (BirtException e) {
			throw new RuntimeException("Could not start the Birt engine!", e);
		}

		IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		IReportEngine be = factory.createReportEngine(config);
		this.birtEngine = be;
		return be;
	}

	@Override
	public Class getObjectType() {
		return IReportEngine.class;
	}
}