package com.j2trp.maven.plugin.copy.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.IOUtil;

/**
 */
@Mojo(name = "copy", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class CopyResourcesMojo extends AbstractMojo {
  /**
   * Location of the file.
   */
  @Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = true)
  private File outputDirectory;

  @Parameter(defaultValue = "${project.basedir}", property = "inputDirectory", required = true)
  private File inputDirectory;
  
  @Parameter(defaultValue = "${project.build.sourceEncoding}", property = "outputEncoding", required = true)
  private String outputEncoding;
  
  @Parameter(defaultValue = "${project.build.sourceEncoding}", property = "inputEncoding", required = true)
  private String inputEncoding;

  public void execute() throws MojoExecutionException {
    
    getLog().info("Input dir: " + inputDirectory);
    getLog().info("Output dir: " + outputDirectory);
    
    if (!inputDirectory.exists()) {
      throw new MojoExecutionException(String.format("The input directory %s doesn't exist", inputDirectory.getAbsolutePath()));
    }
    
    if (!inputDirectory.isDirectory()) {
      throw new MojoExecutionException(String.format("The input directory %s is not a directory", inputDirectory.getAbsolutePath())); 
    }
    
    File f = outputDirectory;

    if (!f.exists()) {
        f.mkdirs();
    }
    
    Charset inputCharset = Charset.forName(inputEncoding);
    Charset outputCharset = Charset.forName(outputEncoding);
    int counter = 0;
    for (File inputFile : inputDirectory.listFiles()) {
    
      if (inputFile.isDirectory()) {
        continue;
      }
      File outputFile = new File(outputDirectory, inputFile.getName());
      try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), inputCharset)); 
           BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), outputCharset));){
        IOUtil.copy(br, bw);
        getLog().info(String.format("Copied file %s to %s using %s/%s", inputFile, outputFile, inputCharset, outputCharset));
        counter++;
      }
      catch (IOException e) {
        throw new MojoExecutionException("Error while performing file operations.", e);
      }
    }
    
    getLog().info(String.format("Copied %d files.", counter));
  }
}
