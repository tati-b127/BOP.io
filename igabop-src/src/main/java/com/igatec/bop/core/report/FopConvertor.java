package com.igatec.bop.core.report;

import lombok.extern.slf4j.Slf4j;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;

@Slf4j
public class FopConvertor {

    //===========================================================================================================
/*
    public FormattingResults convert( String xslDirPath, String imageDirUrl, String xmlText, OutputStream output ) throws Exception {

        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();

        String cfgText = prepareConfigText();

        ByteArrayInputStream cfgStream = new ByteArrayInputStream( cfgText.getBytes("UTF-8"));
        Configuration cfg = cfgBuilder.build( cfgStream );

        URI baseURI = new File(".").toURI();
        FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(baseURI).setConfiguration(cfg);


        FopFactory fopFactory = fopFactoryBuilder.build();
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, output);

        Source xslSource = new StreamSource( prepareXslStream(xslDirPath,imageDirUrl) );

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer( xslSource );

        Source src = new StreamSource( new StringReader(xmlText) );

        Result res = new SAXResult(fop.getDefaultHandler());

        transformer.setErrorListener( new AcErrorListener() );

        transformer.transform(src, res);

        FormattingResults fres = fop.getResults();

        return fres;
    }
*/
    //===========================================================================================================

    public FormattingResults convert( ConfigurableSource xslConfSource, String xmlText, OutputStream output ) throws Exception {

        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();

        ConfigurableSource cfgConfSource = new ConfigurableSource( "espd/fop.xconf" );
        cfgConfSource.setConfig( "FONT_DIR", getFontDirURI() );

        Configuration cfg = cfgBuilder.build( cfgConfSource.getAsStream() );

        URI baseURI = new File(".").toURI();
        FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(baseURI).setConfiguration(cfg);

        FopFactory fopFactory = fopFactoryBuilder.build();
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, output);

        Source xslSource = new StreamSource( xslConfSource.getAsStream() );

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer( xslSource );

        Source src = new StreamSource( new StringReader(xmlText) );

        Result res = new SAXResult(fop.getDefaultHandler());

        transformer.setErrorListener( new AcErrorListener() );

        transformer.transform(src, res);

        FormattingResults fres = fop.getResults();

        return fres;
    }

    //===========================================================================================================

    private static void AssertNotNull( Object obj ) throws Exception {
        if( obj == null ) {
            throw new Exception("AssertNotNull: failed");
        }
    }

    //===========================================================================================================

    private String getFontDirURI() {
        String fontURI = getResourcePath("espd/fonts");
        if( fontURI.endsWith("/") ) {
            fontURI = fontURI.substring( 0, fontURI.length()-1 );
        }
        return fontURI;
    }

    //===========================================================================================================

    public String prepareXslText( String xslDir, String imageDirUrl ) throws IOException {
        String text;

        InputStream inputStream = getResourceStream("espd/_espd_compiled.xsl");
        log.info( "prepareXslText.inputStream = " + inputStream );
        text = IOUtils.toString(inputStream, "UTF-8");
        log.info( "prepareXslText.text = " + text.substring(0,300) + "..." );

        text = text.replaceAll( "XSL_DIR", xslDir.replace('\\','/') );
        text = text.replaceAll( "IMAGE_DIR_URL", imageDirUrl.replace('\\','/') );

        return text;
    }

    //===========================================================================================================

    public String getResourcePath( String resource ) {
        return getClass().getClassLoader().getResource(resource).toString();
    }

    //===========================================================================================================

    public InputStream getResourceStream( String resource ) {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

    //===========================================================================================================
}
