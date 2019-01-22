//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2012 Mirko Nasato and contributors
//
// JODConverter is Open Source software, you can redistribute it and/or
// modify it under either (at your option) of the following licenses
//
// 1. The GNU Lesser General Public License v3 (or later)
//    -> http://www.gnu.org/licenses/lgpl-3.0.txt
// 2. The Apache License, Version 2.0
//    -> http://www.apache.org/licenses/LICENSE-2.0.txt
//
package org.artofsolving.jodconverter.document;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

public class JsonDocumentFormatRegistry extends SimpleDocumentFormatRegistry {

    public JsonDocumentFormatRegistry(InputStream input) throws IOException {
        readJsonArray(IOUtils.toString(input));
    }

    public JsonDocumentFormatRegistry(String source) throws IOException {
        readJsonArray(source);
    }

    private void readJsonArray(String source) throws IOException {
        List<DocumentFormat> list = new ObjectMapper().readValue(source, new TypeReference<List<DocumentFormat>>() {
        });
        for (DocumentFormat documentFormat : list) {
            addFormat(documentFormat);
        }
    }


}
