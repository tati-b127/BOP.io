package com.igatec.bop.core.report;

import lombok.extern.slf4j.Slf4j;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

@Slf4j
public class AcErrorListener implements ErrorListener {

    @Override
    public void warning(TransformerException e) throws TransformerException {
        log.warn( e.getMessageAndLocation() );
        //System.out.println( "WARNING: " + e.getMessageAndLocation() );
    }

    @Override
    public void error(TransformerException e) throws TransformerException {
        log.error( e.getMessageAndLocation() );
    }

    @Override
    public void fatalError(TransformerException e) throws TransformerException {
        log.error( "FATAL: " + e.getMessageAndLocation() );
    }

}

//===========================================================================================================
