package com.cloudeggtech.granite.pack.lite;

public class Options {
	private boolean help;
	private boolean update;
	private boolean cleanCache;
	private boolean cleanUpdate;
	private String version;
	private String[] modules;
	private String appName;
	private String targetDirPath;
	private String graniteProjectDirPath;
	private String projectDirPath;
	private Protocol protocol;
	
	public enum Protocol {
		LEP,
		STANDARD
	}
	
	public Options() {
		help = false;
		update = false;
		cleanCache = false;
		cleanUpdate = false;
		protocol = Protocol.LEP;
	}
	
	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public boolean isUpdate() {
		return update;
	}
	
	public void setUpdate(boolean update) {
		this.update = update;
	}
	
	public boolean isCleanCache() {
		return cleanCache;
	}
	
	public void setCleanCache(boolean cleanCache) {
		this.cleanCache = cleanCache;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}

	public String[] getModules() {
		return modules;
	}

	public void setModules(String[] bundles) {
		this.modules = bundles;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getTargetDirPath() {
		return targetDirPath;
	}

	public void setTargetDirPath(String targetDirPath) {
		this.targetDirPath = targetDirPath;
	}

	public String getGraniteProjectDirPath() {
		return graniteProjectDirPath;
	}

	public void setGraniteProjectDirPath(String graniteDir) {
		this.graniteProjectDirPath = graniteDir;
	}

	public String getProjectDirPath() {
		return projectDirPath;
	}

	public void setProjectDirPath(String projectDirPath) {
		this.projectDirPath = projectDirPath;
	}
	
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}

	public boolean isCleanUpdate() {
		return cleanUpdate;
	}

	public void setCleanUpdate(boolean cleanUpdate) {
		this.cleanUpdate = cleanUpdate;
	}
	
	public boolean isPack() {
		return !isUpdate() && !isCleanUpdate();
	}
	
}
