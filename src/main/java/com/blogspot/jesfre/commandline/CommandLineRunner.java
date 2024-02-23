package com.blogspot.jesfre.commandline;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:jorge.ruiz.aquino@gmail.com">Jorge Ruiz Aquino</a>
 * Feb 13, 2024
 */
public class CommandLineRunner {

	private enum Mode {
		WINDOWS, UNIX
	}

	private static final String CMD_RUN_PREFIX = "cmd /c ";
	// TODO change mode to Unix
	private static final Mode DEFAULT_MODE = Mode.WINDOWS;
	
	private boolean verbose;
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void run(String batFileLocation) throws IOException, InterruptedException {
		if(verbose) {
			System.out.println("Running file " + batFileLocation);
		}
		ProcessBuilder processBuilder = new ProcessBuilder(batFileLocation);

		Process process = processBuilder.start();
		StringBuilder output = new StringBuilder();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}

		File f = new File(batFileLocation);
		int exitVal = process.waitFor();
		if (exitVal == 0) {
			if(verbose) {
				System.out.println(f.getName() + " executed.");
			}
		} else {
			System.err.println("Error during execution of " + f.getName());
		}
	}

	public String executeCommand(String command) {
		if(verbose) {
			System.out.println("Executing command>" + command);
		}
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(CMD_RUN_PREFIX + command);
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				try {
					return getStdInOut2(process);
				} catch (Exception e) {
					System.out.println("Error while trying to print out execution results.");
				}
			}
		}
		return "";
	}

	private String getStdInOut(Process proc) throws IOException {
		StringBuilder stds = new StringBuilder();

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			stds.append(s).append("\n");
		}
		if (stds.length() > 0) {
			return stds.toString();
		}

		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		while ((s = stdError.readLine()) != null) {
			stds.append(s).append("\n");
		}
		if (stds.length() > 0) {
			return stds.toString();
		}
		return "";
	}

	private String getStdInOut2(Process proc) throws IOException {
		String stdout = IOUtils.toString(proc.getInputStream(), Charset.defaultCharset().toString());
		if (StringUtils.isNotBlank(stdout)) {
			return stdout;
		}
		String stderr = IOUtils.toString(proc.getErrorStream(), Charset.defaultCharset().toString());
		if (StringUtils.isNotBlank(stderr)) {
			return stderr;
		}
		return "";
	}
}