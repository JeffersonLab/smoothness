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
    private String tldpath;

    @Option(option = "outdir", description = "Configures the output directory")
    public void setOutdir(String outdir) {
        this.outdir = outdir;
    }

    @Option(option = "tldpath", description = "Configures the input .tld file path")
    public void setTldpath(String tldpath) {
        this.tldpath = tldpath;
    }

    @Input
    public String getOutdir() {
        return outdir;
    }

    @Input
    public String getTldpath() {
        return tldpath;
    }

    @TaskAction
    public void run() throws GeneratorException, TransformerException {
        TLDDocGenerator generator = new TLDDocGenerator();

        generator.addTLD(new File(tldpath));

        generator.setOutputDirectory(new File(outdir));

        generator.generate();
    }
}