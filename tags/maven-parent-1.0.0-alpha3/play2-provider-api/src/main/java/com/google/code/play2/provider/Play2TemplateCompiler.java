/*
 * Copyright 2013 Grzegorz Slowikowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.play2.provider;

import java.io.File;
//?import java.io.IOException;

public interface Play2TemplateCompiler
{
    void setMainLang(String mainLang);
    
    void setAppDirectory( File appDirectory );
    
    void setOutputDirectory( File outputDirectory );
    
    void compile( File templateFile ) throws TemplateCompilationException;

//    void compile( File templateFile, File appDirectory, File generatedDirectory, //String resultType,
//                  /*String formatterType, String imports, */String mainLang );//TODO any exceptions?
}
