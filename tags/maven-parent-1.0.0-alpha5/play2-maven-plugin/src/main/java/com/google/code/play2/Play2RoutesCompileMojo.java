/*
 * Copyright 2013 Grzegorz Slowikowski (gslowikowski at gmail dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.google.code.play2;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;

import com.google.code.play2.provider.Play2RoutesCompiler;
import com.google.code.play2.provider.RoutesCompilationException;

/**
 * Compile routes
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @since 1.0.0
 */
@Mojo( name = "routes-compile" )
public class Play2RoutesCompileMojo
    extends AbstractPlay2Mojo
{

    /**
     * Main language ("scala" or "java").
     * 
     * @since 1.0.0
     */
    @Parameter( property = "play.mainLang", required = true, defaultValue = "scala" )
    private String mainLang;

    private static final String confDirectoryName = "conf";

    private static final String targetDirectoryName = "src_managed/main";

    private static final String[] routesIncludes = new String[] { "*.routes", "routes" };

    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        if ( !"java".equals( mainLang ) && !"scala".equals( mainLang ) )
        {
            throw new MojoExecutionException(
                                              String.format( "Routes compilation failed  - unsupported <mainLang> configuration parameter value \"%s\"",
                                                             mainLang ) );
        }

        File basedir = project.getBasedir();
        File confDirectory = new File( basedir, confDirectoryName );
        if ( !confDirectory.isDirectory() )
        {
            getLog().info( "No routes to compile" );
            return;
        }

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( confDirectory );
        scanner.setIncludes( routesIncludes );
        scanner.addDefaultExcludes();
        scanner.scan();
        String[] files = scanner.getIncludedFiles();

        if ( files.length > 0 )
        {
            File targetDirectory = new File( project.getBuild().getDirectory() );
            File generatedDirectory = new File( targetDirectory, targetDirectoryName );

            Play2RoutesCompiler compiler = play2Provider.getRoutesCompiler();
            compiler.setMainLang( mainLang );
            compiler.setOutputDirectory( generatedDirectory );

            for ( String fileName : files )
            {
                File routesFile = new File( confDirectory, fileName );
                if ( routesFile.isFile() )
                {
                    try
                    {
                        compiler.compile( routesFile );
                    }
                    catch ( RoutesCompilationException e )
                    {
                        throw new MojoExecutionException( String.format( "Routes compilation failed (%s)",
                                                                         routesFile.getPath() ), e );
                    }
                }
            }

            if ( !project.getCompileSourceRoots().contains( generatedDirectory.getAbsolutePath() ) )
            {
                project.addCompileSourceRoot( generatedDirectory.getAbsolutePath() );
                getLog().debug( "Added source directory: " + generatedDirectory.getAbsolutePath() );
            }
        }
    }

}