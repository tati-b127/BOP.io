package com.igatec.bop.core.report;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class ConfigurableSource {
    String resourceRelativePath;
    String fullText;

    //===========================================================================================================

    public ConfigurableSource( String path ) throws IOException {
        InputStream inputStream = getResourceStream( path );
        fullText = IOUtils.toString( inputStream, "UTF-8" );
    }

    //===========================================================================================================

    public void setConfig( String name, String value ) {
        fullText = fullText.replace( name, value );
    }

    //===========================================================================================================

    public InputStream getAsStream() throws UnsupportedEncodingException {
        return new ByteArrayInputStream( fullText.getBytes("UTF-8") );
    }

    //===========================================================================================================

    private InputStream getResourceStream( String resource ) {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

    //===========================================================================================================
}
