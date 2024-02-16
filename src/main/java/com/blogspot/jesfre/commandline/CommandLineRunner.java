package com.blogspot.jesfre.commandline;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:jorge.ruiz.aquino@gmail.com">Jorge Ruiz Aquino</a>
 * Feb 13, 2024
 */
public class CommandLineRunner {

	public void run(String batFileLocation) throws IOException, InterruptedException {
		System.out.println("Running file " + batFileLocation);
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
			System.out.println(f.getName() + " executed.");
		} else {
			System.err.println("Error during execution of " + f.getName());
		}

	}

}