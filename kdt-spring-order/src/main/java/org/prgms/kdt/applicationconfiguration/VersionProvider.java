package org.prgms.kdt.applicationconfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("version.properties")
public class VersionProvider {

	private final String version;

	public VersionProvider(@Value("${version:v1.0.0}") String version) {
		System.out.println(version);
		this.version = version;
	}

	public String getVersion() {
		return version;
	}
}
