package pers.lee.common.rpc.ci.status;

import pers.lee.common.config.ApplicationConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * VersionStatus
 *
 * @author Drizzt Yang
 */
public class VersionStatus implements StatusAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(VersionStatus.class);

    private static final String VERSION = "version";
    private static final Set<String> KEYS = new HashSet<String>();

    static {
        KEYS.add("tomcat");
        KEYS.add("java");
        KEYS.add("jar.");
    }

    @Override
    public String getStatusPrefix() {
        return VERSION;
    }

    @Override
    public Set<String> getStatusKeys() {
        return KEYS;
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        properties.put("java", System.getProperty("java.version"));

        try {
            Properties tomcatProperties = new Properties();
            tomcatProperties.load(this.getClass().getClassLoader().getResourceAsStream("org/apache/catalina/util/ServerInfo.properties"));
            properties.put("tomcat", tomcatProperties.getProperty("server.number"));
        } catch (Exception e) {
            LOGGER.warn("not able to get tomcat version", e);
        }

        String jarDirectoryPath = ApplicationConfiguration.get().getApplicationDirectory() + "/WEB-INF/lib/";

        if (!new File(jarDirectoryPath).exists()) {
            return properties;
        }
        for (File file : new File(jarDirectoryPath).listFiles()) {
            if (!file.getName().endsWith(".jar")) {
                continue;
            }
            try {
                JarFile jarFile = new JarFile(file);
                Manifest manifest = jarFile.getManifest();
                if (manifest == null) {
                    continue;
                }
                Attributes mainAttributes = manifest.getMainAttributes();
                if (mainAttributes == null) {
                    continue;
                }
                String jarName = FilenameUtils.getBaseName(file.getName());

                String implVersion = mainAttributes.getValue("Implementation-Version");
                if (implVersion == null) {
                    implVersion = mainAttributes.getValue("Manifest-Version");
                }
                if (implVersion != null) {
                    properties.put("jar." + jarName + ".impl", implVersion);
                }

                String svnVersion = mainAttributes.getValue("SVN-Version");
                if (svnVersion != null) {
                    properties.put("jar." + jarName + ".svn", svnVersion);
                }
            } catch (IOException ignored) {
            }
        }
        return properties;
    }

    private static VersionStatus versionStatus = new VersionStatus();

    public static VersionStatus get() {
        return versionStatus;
    }
}
