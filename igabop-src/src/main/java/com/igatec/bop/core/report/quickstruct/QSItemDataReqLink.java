package com.igatec.bop.core.report.quickstruct;

import com.matrixone.apps.domain.util.MqlUtil;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;

import java.util.Map;

public class QSItemDataReqLink extends QSItem {

    String xmlId;

    //================================================

    public QSItemDataReqLink(QSDocument doc, String _xmlId, String _type, String _id, QSPath[] _paths) {
        super(doc, _xmlId, _type, _id, _paths);
        document.registerDataReqLink( this );
        xmlId = document.getNewXmlId();
    }

    //================================================

    protected String getValidChildTypes() {
        return "DELAsmProcessCanUseCnx,PLMDocConnection";
    }

    //================================================

    private class LinkEnd {
        String connId;
        String busId;
        QSInstance instance;
        String portXmlId;
        String portInstXmlId;
        boolean isIn;
    }

    LinkEnd[] ends = new LinkEnd[2];

    public void expand() throws Exception {

        if( paths.length != 2 ) {
            return;
        }

        for( int i=0; i<2; i++ ) {
            QSPath p = paths[i];
            ends[i] = new LinkEnd();
            ends[i].portXmlId = document.getNewXmlId();
            ends[i].portInstXmlId = document.getNewXmlId();
            for( QSPathElement e: p.elements ) {
                if( e.kind.equals("connection") ) {
                    ends[i].connId = e.id;
                }
                else {
                    ends[i].busId = e.id;
                }
            }
        }

        Map<String,QSInstance> instanceMap = document.loadInstances( ends[0].connId + "," + ends[1].connId );

        for( int i=0; i<2; i++ ) {
            ends[i].instance = instanceMap.get( ends[i].connId );
        }

        String t0 = MqlUtil.mqlCommand( document.context, "print bus $1 select $2 dump",
                ends[0].busId,
                "attribute[V_IOType]"
                );

        ends[0].isIn = t0.equals("1");
        ends[1].isIn = ! ends[0].isIn;
    }

    //================================================

    public void appendNodes( ElementImpl ptsNode ) {

        appendPortBranch( ptsNode, ends[0] );
        appendPortBranch( ptsNode, ends[1] );

        ElementImpl linkNode = document.appendChildNode( ptsNode, "DataRequirementLink" );
        linkNode.setAttribute( "id", xmlId );

        document.appendAttrNode( linkNode, "Owned", ends[0].instance.itemFrom.xmlId );
        document.appendAttrNode( linkNode, "Owned1", ends[1].instance.itemFrom.xmlId );
        document.appendAttrNode( linkNode, "Quantity", "1" );

        appendPortBranchRef( linkNode, ends[0] );
        appendPortBranchRef( linkNode, ends[1] );
    }

    //================================================

    public void appendPortBranch( ElementImpl ptsNode, LinkEnd end ) {
        QSInstance inst = end.instance;
        document.appendItemOnce( ptsNode, inst.itemTo.getMbomNodeName(), inst.itemTo );
        document.appendInstanceOnce( ptsNode, "TransformationInst", inst );

        ElementImpl portNode = document.appendChildNode( ptsNode, end.isIn ? "DataRequirementPortIN" : "DataRequirementPortOUT" );
        portNode.setAttribute( "id", end.portXmlId );
        document.appendAttrNode( portNode, "Owned", inst.itemTo.xmlId );

        ElementImpl portInstNode = document.appendChildNode( ptsNode, "TransformationInstancePort" );
        portInstNode.setAttribute( "id", end.portInstXmlId );
        document.appendAttrNode( portInstNode, "Owned", inst.xmlId );
        document.appendAttrNode( portInstNode, "PortRef", end.portXmlId );
    }

    //================================================

    public void appendPortBranchRef( ElementImpl linkNode, LinkEnd end ) {
        document.appendAttrNode( linkNode, end.isIn ? "TransformationInstancePortINRef" : "TransformationInstancePortOUTRef", end.portInstXmlId );
    }

    //================================================
}
