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
public class BirtEngineFactory implements FactoryBean<IReportEngine>,
		DisposableBean {

	private IReportEngine birtEngine;

	public boolean isSingleton() {
		return true;
	}

	public void destroy() throws Exception {
		birtEngine.destroy();
		Platform.shutdown();
	}

	public IReportEngine getObject() {

		EngineConfig config = new EngineConfig();

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
	public Class<IReportEngine> getObjectType() {
		return IReportEngine.class;
	}
}