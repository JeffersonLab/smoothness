package org.jlab;

import com.sun.tlddoc.GeneratorException;
import com.sun.tlddoc.TLDDocGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import javax.xml.transform.TransformerException;
import java.io.File;

public class TagLibDocTask extends DefaultTask {
    private String outdir;
    private String jarpath;

    @Option(option = "outdir", description = "Configures the output directory")
    public void setOutdir(String outdir) {
        this.outdir = outdir;
    }

    @Option(option = "jarpath", description = "Configures the input jar path")
    public void setJarpath(String jarpath) {
        this.jarpath = jarpath;
    }

    @Input
    public String getOutdir() {
        return outdir;
    }

    @Input
    public String getJarpath() {
        return jarpath;
    }

    @TaskAction
    public void run() throws GeneratorException, TransformerException {
        System.out.println("Hello from task " + getPath() + "!");
        TLDDocGenerator generator = new TLDDocGenerator();

        //File f = new File("src/main/resources/META-INF/smoothness.tld");
        //generator.addTLD(f);

        generator.addJAR(new File(jarpath));

        generator.setOutputDirectory(new File(outdir));

        generator.generate();
    }
}