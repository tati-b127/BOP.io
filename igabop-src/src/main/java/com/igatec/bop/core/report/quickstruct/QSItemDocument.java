package com.igatec.bop.core.report.quickstruct;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QSItemDocument extends QSItem {

    public String fileXmlId;
    public String fileId;
    public String fileName;
    public String fileSize;

    //================================================

    public QSItemDocument(QSDocument doc, String _xmlId, String _type, String _id, QSPath[] _paths) {
        super(doc, _xmlId, _type, _id, _paths);
        document.registerDoc( this );
    }

    //================================================

    public void setFileData(String _fileXmlId, String _fileId, String _fileName, String _fileSize ) {
        fileXmlId = _fileXmlId;
        fileId = _fileId;
        fileName = _fileName;
        fileSize = _fileSize;
    }

    //================================================

    public ElementImpl makeDocumentNode() {

        ElementImpl node = new ElementImpl( document.xmldoc, "Document" );

        node.setAttribute( "id", xmlId );

        document.appendAttrNode( node, "FileRef", fileXmlId );

        document.appendAttributes( node, this );

        return node;
    }

    //================================================

    public ElementImpl makeFileNode() {

        ElementImpl node = new ElementImpl( document.xmldoc, "File" );

        node.setAttribute( "id", fileXmlId );

        document.appendAttrNode( node, "Name", fileName );
        document.appendAttrNode( node, "Location", fileName );

        return node;
    }

    //================================================

    public void checkoutFile( String directory ) {
        try {
			log.debug( "checkout bus "+id+" server file "+fileName+" "+directory );
			
            MqlUtil.mqlCommand( document.context, "checkout bus $1 server file $2 $3",
                    id,
                    fileName,
                    directory
            );
        } catch (FrameworkException e) {
            log.warn("Can't checkout. id="+id +" fileName="+fileName );
            e.printStackTrace();
        }
    }

    //================================================
}
