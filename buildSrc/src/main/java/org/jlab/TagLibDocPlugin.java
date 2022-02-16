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

        task.dependsOn("jar"); // This seems to trigger jar task, but too late.  Run tlddoc task twice works...

        File projectDir = project.getProjectDir();

        task.setOutdir(new File(projectDir, "build/docs/tlddoc").getAbsolutePath());

        File jarDir = new File(projectDir, "build/libs/");
        String jarpath = jarDir.getAbsolutePath();

        File[] jarFiles = jarDir.listFiles(new JarExtensionFilter());

        if(jarFiles != null && jarFiles.length > 0) {
            jarpath = jarFiles[0].getAbsolutePath();
        }

        task.setJarpath(jarpath);
    }

    class JarExtensionFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            int i = name.lastIndexOf('.');
            if (i > 0 && i < name.length() - 1) {
                String desiredExtension = name.substring(i + 1).
                        toLowerCase(Locale.ENGLISH);
                if (desiredExtension.equals("jar")) {
                    return true;
                }
            }

            return false;
        }
    }
}