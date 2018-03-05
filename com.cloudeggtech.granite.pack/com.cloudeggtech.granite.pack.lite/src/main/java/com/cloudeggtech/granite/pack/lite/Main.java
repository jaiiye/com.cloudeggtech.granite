package com.cloudeggtech.granite.pack.lite;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.util.Arrays;

public class Main {
	private static final String DEFAULT_VERSION = "0.2.1.RELEASE";
	private static final String NAME_PREFIX_APP = "granite-lite-";
	
	private Options options;
	
	public static void main(String[] args) {
		Main main = new Main();
		main.run(args);
	}
	
	public void run(String[] args) {
		try {
			options = parseOptions(args);
		} catch (IllegalArgumentException e) {
			if (e.getMessage() != null)
				System.out.println("Error: " + e.getMessage());
			printUsage();
			return;
		}
		
		if (options.isHelp()) {
			printUsage();
			return;
		}
		
		if (options.isPack()) {
			new Packer(options).pack();
		} else {
			Updater updater = new Updater(options);
			
			if (options.isCleanCache()) {
				updater.cleanCache();
			}
			
			if (options.isCleanUpdate()) {
				updater.update(true);
			} else {
				updater.update(false);
			}
		}
	}
	
	private Options parseOptions(String[] args) {
		Options options = new Options();
		
		if (args.length == 1 && args[0].equals("-help")) {
			options.setHelp(true);
			
			return options;
		}
		
		int i = 0;
		while (i < args.length) {
			if ("-update".equals(args[i])) {
				options.setUpdate(true);
				i++;
			} else if ("-cleanUpdate".equals(args[i])) {
				options.setCleanUpdate(true);
				i++;
			} else if ("-cleanCache".equals(args[i])) {
				options.setCleanCache(true);
				i++;
			} else if ("-version".equals(args[i])) {
				if (i == (args.length - 1)) {
					throw new IllegalArgumentException("-version should follow a [VERSION] option.");
				}
				i++;
				
				if (args[i].startsWith("-")) {
					throw new IllegalArgumentException("-version should follow a [VERSION] option.");
				}
				
				options.setVersion(args[i]);
				i++;
			} else if("-protocol".equals(args[i])) {
				if (i == (args.length - 1)) {
					throw new IllegalArgumentException("-protocol should follow a [PROTOCOL] option.");
				}
				i++;
				
				if (args[i].startsWith("-")) {
					throw new IllegalArgumentException("-protocol should follow a [PROTOCOL] option.");
				}
				
				if ("lep".equals(args[i])) {
					options.setProtocol(Options.Protocol.LEP);
				} else if ("standard".equals(args[i])) {
					options.setProtocol(Options.Protocol.STANDARD);
				} else {
					throw new IllegalArgumentException(String.format("Illegal protocol: %s. only 'lep' or 'standard' supported", args[i]));
				}
				i++;
			} else {
				options.setModules(Arrays.copyOfRange(args, i, args.length));
				break;
			}
		}
		
		if (options.isUpdate() && options.isCleanUpdate()) {
			throw new IllegalArgumentException("You can specify option -update or -cleanUpdate but not both.");
		}
		
		if (!options.isUpdate() && !options.isCleanUpdate() && options.getModules() != null) {
			throw new IllegalArgumentException("[BUNDLE_SYMBOLIC_NAME]... is only used in update mode. maybe you should add -update or -clean-update to options.");
		}
		
		if (options.getVersion() == null) {
			options.setVersion(DEFAULT_VERSION);
		}
		
		options.setAppName(NAME_PREFIX_APP + options.getVersion());
		
		if (!options.isUpdate() && !options.isCleanUpdate() && options.getProtocol() == null) {
			options.setProtocol(Options.Protocol.STANDARD);
		}
		
		options.setTargetDirPath(getTargetDirPath());
		options.setProjectDirPath(getProjectDirPath(options.getTargetDirPath()));
		options.setGraniteProjectDirPath(getGraniteProjectDirPath(options.getProjectDirPath()));
		
		return options;
	}
	
	private void printUsage() {
		System.out.println("Usage:");
		System.out.println("java -jar com.cloudeggtech.granite.pack.lite-${VERSION}.jar [OPTIONS] [Bundle-SymbolicNames or SubSystems]");
		System.out.println("OPTIONS:");
		System.out.println("-help                Display help information.");
		System.out.println("-update              Update specified modules.");
		System.out.println("-cleanUpdate         Clean and update specified modules.");
		System.out.println("-cleanCache          Clean the packing cache.");
		System.out.println("-version VERSION     Specify the version(Default is 0.2.1-RELEASE).");
		System.out.println("-prototol PROTOCOL   Specify the protocol('lep' or 'standard'. Default is 'standard').");
	}

	private String getTargetDirPath() {
		URL classPathRoot = this.getClass().getResource("");
		
		if (classPathRoot.getPath().indexOf("!") != -1) {
			int colonIndex =  classPathRoot.getFile().indexOf('!');
			String jarPath =  classPathRoot.getPath().substring(0, colonIndex);
			
			int lastSlashIndex = jarPath.lastIndexOf('/');
			String jarParentDirPath = jarPath.substring(5, lastSlashIndex);
			
			return jarParentDirPath;
		} else {
			int classesIndex = classPathRoot.getPath().lastIndexOf("/classes");	
			return classPathRoot.getPath().substring(0, classesIndex);
		}
	}
	
	private String getGraniteProjectDirPath(String projectDirPath) {
		return new File(projectDirPath).getParentFile().getParentFile().getPath();
	}
	
	private String getProjectDirPath(String targetDirPath) {
		return new File(targetDirPath).getParentFile().getPath();
	}
	
	public static void runMvn(File workingDir, String... args) {
		if (args == null || args.length == 0) {
			throw new IllegalArgumentException("Null mvn args.");
		}
		
		String[] cmdArray = new String[args.length + 1];
		cmdArray[0] = getMvnCmd();
		for (int i = 0; i < args.length; i++) {
			cmdArray[i + 1] = args[i];
		}
		
		try {
			Process process = new ProcessBuilder(cmdArray).
						redirectError(Redirect.INHERIT).
						redirectOutput(Redirect.INHERIT).
						directory(workingDir).
						start();
			
			process.waitFor();
		} catch (IOException e) {
			throw new RuntimeException("Can't execute maven.", e);
		} catch (InterruptedException e) {
			throw new RuntimeException("Maven execution error.", e);
		}
	}
	
	private static String getMvnCmd() {
		String osName = System.getProperty("os.name");
		if (osName.contains("Windows")) {
			return "mvn.cmd";
		}
		
		return "mvn";
	}
	
}