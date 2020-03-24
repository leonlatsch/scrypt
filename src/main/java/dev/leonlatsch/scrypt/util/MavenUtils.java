package dev.leonlatsch.scrypt.util;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Leon Latsch
 * @since 2.1
 */
public class MavenUtils {

    public static String getVersion() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model;
            if ((new File("pom.xml")).exists())
                model = reader.read(new FileReader("pom.xml"));
            else
                model = reader.read(MavenUtils.class.getResourceAsStream("/META-INF/maven/dev.leonlatsch/scrypt/pom.xml"));
            return model.getVersion();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
            return "";
        }
    }
}
