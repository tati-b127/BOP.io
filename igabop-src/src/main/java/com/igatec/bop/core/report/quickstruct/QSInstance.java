package com.igatec.bop.core.report.quickstruct;

import com.sun.org.apache.xerces.internal.dom.ElementImpl;

public class QSInstance {

    QSDocument document;
    protected String xmlId;
    String type;
    String id;
    QSItem itemFrom;
    QSItem itemTo;

    String childOrder;
    String resourcesQuantity;
    String usageCoefficient;

    private boolean produced = false;

    //================================================

    public QSInstance(QSDocument doc, String _xmlId, String _type, String _id, QSItem.RelAttrs relAttrs, QSItem from, QSItem to ) {
        document = doc;
        xmlId = _xmlId;
        type = _type;
        id = _id;
        itemFrom = from;
        itemTo = to;

        if( relAttrs != null ) {
            childOrder = relAttrs.childOrder;
            resourcesQuantity = relAttrs.resourcesQuantity;
            usageCoefficient = relAttrs.usageCoefficient;
        }
    }

    //================================================

    public boolean isProduced() {
        return produced;
    }

    //================================================

    public void setProduced() {
        produced = true;
    }

    //================================================

    public ElementImpl makeInstNode(String nodeName ) {

        ElementImpl node = new ElementImpl( document.xmldoc, nodeName );

        node.setAttribute( "id", xmlId );

        document.appendAttrNode( node, "Owned", itemFrom.xmlId );
        document.appendAttrNode( node, "Instancing", itemTo.xmlId );
        if( childOrder != null ) {
            document.appendAttrNode(node, "ChildOrder", childOrder );
        }
        if( resourcesQuantity != null ) {
            document.appendAttrNode(node, "ResourcesQuantity", resourcesQuantity );
        }
        if( usageCoefficient != null ) {
            document.appendAttrNode(node, "UsageCoefficient", usageCoefficient );
        }

        return node;
    }

    //================================================

    public ElementImpl makeResourceInstNode() {

        ElementImpl node = new ElementImpl( document.xmldoc, "CapableReferenceResourceLink" );

        node.setAttribute( "id", xmlId );

        document.appendAttrNode( node, "Owned", itemFrom.xmlId );
        document.appendAttrNode( node, "ResourceRef", itemTo.xmlId );
        document.appendAttrNode( node, "ResourcesQuantity", resourcesQuantity );

        return node;
    }

    //================================================

    public ElementImpl makeDocumentInstNode() {

        ElementImpl node = new ElementImpl( document.xmldoc, "DocumentReferenceRelationship" );

        node.setAttribute( "id", xmlId );

        document.appendAttrNode( node, "Owned", itemFrom.xmlId );
        document.appendAttrNode( node, "Instancing", itemTo.xmlId );

        return node;
    }

    //================================================
}
