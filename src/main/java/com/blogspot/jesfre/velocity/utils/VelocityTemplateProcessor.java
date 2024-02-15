package com.blogspot.jesfre.velocity.utils;

import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class VelocityTemplateProcessor {
	private final String templateFolder;

	private VelocityTemplateProcessor(String templateFolderPath) {
		templateFolder = templateFolderPath;
	}

	public static VelocityTemplateProcessor getProcessor(String templateFolderPath) {
		return new VelocityTemplateProcessor(templateFolderPath);
	}

	public String process(String template, Map<String, Object> contextParams) {
		Properties props = new Properties();
		props.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, templateFolder);

		VelocityEngine ve = new VelocityEngine();
		ve.init(props);

		Template vtemplate = ve.getTemplate(template);
		VelocityContext context = new VelocityContext();
		for (Entry<String, Object> param : contextParams.entrySet()) {
			context.put(param.getKey(), param.getValue());
		}

		StringWriter writer = new StringWriter();
		vtemplate.merge(context, writer);
		return writer.toString();
	}

}
