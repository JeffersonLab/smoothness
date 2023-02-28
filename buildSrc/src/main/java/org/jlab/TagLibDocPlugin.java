package org.jlab;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

public class TagLibDocPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        TagLibDocTask task = project.getTasks().create("tlddoc", TagLibDocTask.class);

        File projectDir = project.getProjectDir();

        task.setOutdir(new File(projectDir, "build/docs/tlddoc").getAbsolutePath());

        File tldDir = new File(projectDir, "src/main/resources/META-INF");
        String tldpath = tldDir.getAbsolutePath();

        File[] tldFiles = tldDir.listFiles(new TldExtensionFilter());

        if(tldFiles != null && tldFiles.length > 0) {
            tldpath = tldFiles[0].getAbsolutePath();
        }

        task.setTldpath(tldpath);
    }

    class TldExtensionFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            int i = name.lastIndexOf('.');
            if (i > 0 && i < name.length() - 1) {
                String desiredExtension = name.substring(i + 1).
                        toLowerCase(Locale.ENGLISH);
                return desiredExtension.equals("tld");
            }

            return false;
        }
    }
}