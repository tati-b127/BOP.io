package com.igatec.bop.core.report.quickstruct;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class QSItem {
    //================================================

    protected QSDocument document;
    protected String xmlId;
    protected String type;
    protected String id;
    protected String name;
    protected String revision;
    public QSPath[] paths;
    protected List<QSInstance> children = new ArrayList<QSInstance>();
    protected Map<String, String> attributesMap;

    private boolean produced = false;

    //================================================

    public QSItem(QSDocument doc, String _type, String _id ) {
        this( doc, "NONE", _type, _id, new QSPath[0] );

    }

    //================================================

    public QSItem(QSDocument doc, String _xmlId, String _type, String _id, QSPath[] _paths ) {
        document = doc;
        xmlId = _xmlId;
        type = _type;
        id = _id;
        paths = _paths;
        document.registerItem( this );
    }

    //================================================

    protected String getValidChildTypes() {
        return null;
    }

    //================================================

    protected void expand() throws Exception {
        children = new ArrayList<QSInstance>();

        String reltypes = "DELLmiGeneralSystemInstance,DELLmiHeaderOperationInstance,DELWkiInstructionInstance,DELWkiAlertInstance,VPLMrel/PLMConnection/V_Owner,VPMInstance";
        String bustypes = getValidChildTypes();
        if( bustypes == null ) {
            return; // This object doesn't suppose expanding
        }

//        System.out.println( "Expand: "+id+" type:"+type );

        log.debug( "expand bus "+id+" from relationship "+reltypes+" type "+bustypes+" " +
                        "select rel physicalid attribute[PLMInstance.V_TreeOrder] attribute[DELAsmProcessCanUseCnx.V_ResourcesQuantity] attribute[ProcessInstanceContinuous.V_UsageContCoeff] " +
                        "select bus physicalid attribute[PLMInstance.V_TreeOrder] attribute[DELAsmProcessCanUseCnx.V_ResourcesQuantity] attribute[ProcessInstanceContinuous.V_UsageContCoeff] paths.path"
        );

        String res = null;
        try {
            res = MqlUtil.mqlCommand( document.context, "expand bus $1 from relationship $2 type $3 " +
                            "select rel physicalid $4 $5 $6 " +
                            "select bus physicalid $7 $8 $9 $10",
                    id, reltypes, bustypes,
                    "attribute[PLMInstance.V_TreeOrder]", "attribute[DELAsmProcessCanUseCnx.V_ResourcesQuantity]", "attribute[ProcessInstanceContinuous.V_UsageContCoeff]",
                    "attribute[PLMInstance.V_TreeOrder]", "attribute[DELAsmProcessCanUseCnx.V_ResourcesQuantity]", "attribute[ProcessInstanceContinuous.V_UsageContCoeff]", "paths.path"
            );
        } catch (FrameworkException e) {
            log.warn("Can't expand object. id="+id +" type="+type );
            e.printStackTrace();
            return; // Most probably non-existing object
        }

        log.debug( "expand res=" + res );

        if( res.length() > 0 ) {

            String[] lines = res.split("\n");

            for( int k=0; k<lines.length; ) {
                int maxk=k;
                for( int i=k+1; i<lines.length; i++ ) {
                    if( lines[i].charAt(0) == '1' ) {
                        maxk = i-1;
                        break;
                    }
                    maxk = i; // In case we don't find a '1'
                }

                if( maxk >= k+5 ) {
                    expand_instance(lines, k, maxk);
                }

                k = maxk+1;
            }
        }

    }

    //================================================

    public static class RelAttrs {
        String childOrder;
        String resourcesQuantity;
        String usageCoefficient;
    };

    private void expand_instance( String[] lines, int nFirst, int nLast ) throws Exception {
        //log.info( "expand_instance nFirst="+nFirst + " nLast="+nLast );

        String[] header = lines[nFirst+0].split(" +");
        String relType = header[1];
        String busType = header[3];

        RelAttrs busAttrs = new RelAttrs();
        String busId = cutValue( lines[nFirst+1] );
        busAttrs.childOrder = cutValue( lines[nFirst+2] );
        busAttrs.resourcesQuantity = cutValue( lines[nFirst+3] );
        busAttrs.usageCoefficient = cutValue( lines[nFirst+4] );

        RelAttrs relAttrs = new RelAttrs();
        String relId = cutValue( lines[nLast-3] );
        relAttrs.childOrder = cutValue( lines[nLast-2] );
        relAttrs.resourcesQuantity = cutValue( lines[nLast-1] );
        relAttrs.usageCoefficient = cutValue( lines[nLast-0] );

        int nFirstPathPos = nFirst+5;
        int nPaths = (nLast-3) - nFirstPathPos;
        QSPath[] paths = new QSPath[nPaths];
        for( int i=0; i<nPaths; i++ ) {
            paths[i] = new QSPath( cutValue( lines[nFirstPathPos+i] ) );
        }

        //System.out.println( type + " -> " + relType + " -> " + busType );

        QSItem item = addItemForData( relType,relId,relAttrs, busType,busId,busAttrs,paths );
        if( item != null ) {
            item.expand();
        }
    }

    //================================================

    public static String cutValue( String line ) {
        int pos = line.indexOf( " = " );
        if( pos > 0 ) {
            return line.substring( pos + 3 );
        }
        return null;
    }

    //================================================

    public QSItem getChildByType( String type ) {
        for( QSInstance inst : children ) {
            if( inst.itemTo.type.equals(type) ) {
                return inst.itemTo;
            }
        }
        return null;
    }

    //================================================

    private QSItem addItemForData(String relType, String relId, RelAttrs relAttrs, String busType, String busId, RelAttrs busAttrs, QSPath[] paths) throws Exception {
        log.debug( "addItemForData relType="+relType+" relId="+relId+" busType="+busType+" busId="+busId );

        if( relType.equals("DELLmiGeneralSystemInstance") ) {
            return addItemAndInstance( relType,relId,relAttrs, QSItemSystem.class, busType,busId,paths );
        }

        if( relType.equals("DELLmiHeaderOperationInstance") ) {
            return addItemAndInstance( relType,relId,relAttrs, QSItemOperation.class, busType,busId,paths );
        }

        if( relType.equals("DELWkiInstructionInstance") ) {
            return addItemAndInstance( relType,relId,relAttrs, QSItemInstruction.class, busType,busId,paths );
        }

        if( relType.equals("DELWkiAlertInstance") ) {
            return addItemAndInstance( relType,relId,relAttrs, QSItemInstruction.class, busType,busId,paths );
        }

        if( relType.equals("VPMInstance") ) {
            return addItemAndInstance( relType,relId,relAttrs, QSItemThing.class, busType,busId,paths );
        }

        //=== PPRContext root System
        if( busType.equals("PPRContextSystemCnxDisc") ) {
            QSPathElement pe = paths[0].elements[0];
            return addItemAndInstance( relType,relId,relAttrs, QSItemFactory.class, pe.type,pe.id,new QSPath[0] );
        }

        //=== PPRContext root MBOM
        if( busType.equals("PPRContextProcessCnxDisc") ) {
            QSPathElement pe = paths[0].elements[0];
            return addItemAndInstance( "MbomMetaInst",relId,relAttrs, QSItemThing.class, pe.type,pe.id,new QSPath[0] );
        }

        //=== CapableResource
        if( busType.equals("DELAsmProcessCanUseCnx") ) {
            //log.info( "CapableResource_1" );
            for( QSPath p: paths ) {
                //log.info( "CapableResource_2" );
                QSPathElement pe = p.elements[0];
                if( ! QSTypes.isOperationOrInstruction(pe.type) ) { // Not Operations here means Thing
                    //log.info( "CapableResource_3" );
                    return addItemAndInstance("CapableResource", busId, busAttrs, QSItemThing.class, pe.type, pe.id, new QSPath[0]);
                }
            }
        }

        //=== Attached Documents
        if( busType.equals("PLMDocConnection") ) {
            QSPathElement pe = paths[0].elements[0];
            return addItemAndInstance( "DocumentMetaInst",relId,relAttrs, QSItemDocument.class, pe.type, pe.id, new QSPath[0]  );
        }

        //=== SystemImplementLink
        if( busType.equals("MfgProductionPlanning") ) {
            return addItemAndInstance( "SilMetaInst",relId,relAttrs, QSItemSystemImplementLink.class, "SystemImplementLink",busId,paths );
        }


        //=== ProductImplementLinkRef
        if( busType.equals("DELFmiProcessImplementCnx") ) {
            for( QSPath p: paths ) {
                QSPathElement pe = p.elements[0];
                if( ! pe.id.equals(this.id) ) { // Not this, other end
                    //log.info( "CapableResource_3" );
                    return addItemAndInstance("ProductImplementMetaInst", busId, busAttrs, QSItemThing.class, pe.type, pe.id, new QSPath[0]);
                }
            }
        }

        //=== DataRequirementLink
        if( busType.equals("DELFmiProcessPrerequisiteCnxCust") ) {
            return addItemAndInstance( "LinkMetaInst",relId,relAttrs, QSItemDataReqLink.class, "DataRequirementLink",busId,paths );
        }

        //===

        return null;
    }

    //================================================

    private QSItem addItemAndInstance(String relType, String relId, RelAttrs relAttrs, Class<?extends QSItem> itemClass, String busType, String busId, QSPath[] paths) throws Exception {

        QSItem item = document.createItem( itemClass, busType, busId, paths );

        QSInstance inst = document.createInstance(relType,relId, relAttrs, this,item);
        children.add( inst );

        return item;
    }

    //================================================

    public void setAttributes( Map<String, String> attrMap ) {
        attributesMap = attrMap;
        name = attributesMap.get( "_basic::name" );
        revision = attributesMap.get( "_basic::revision" );
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

    public String getMbomNodeName() {
        String nodeName = document.mbomTypeMap.get(type);
        if( nodeName == null ) nodeName="DefaultMbom";
        return nodeName;
    }

    //================================================

    public String getResourceNodeName() {
        String discipline = attributesMap.get( "PLMEntity.V_discipline" );
        String nodeType = document.resourceTypeMap.get( discipline );
        String nodeName = (nodeType!=null) ? nodeType : "Resource";
        return nodeName;
    }

    //================================================

    public ElementImpl makeRefNode( String nodeName ) {

        ElementImpl node = new ElementImpl( document.xmldoc, nodeName );

        node.setAttribute( "id", xmlId );
        node.setAttribute( "mappingType", type );

        document.appendAttributes( node, this );

        return node;
    }

    //================================================

    public QSInstance[] getSystems() {
        return getChildrenByClass( QSItemSystem.class );
    }

    //================================================

    public QSInstance[] getOperations() {
        return getChildrenByClass( QSItemOperation.class );
    }

    //================================================

    public QSInstance[] getInstructions() {
        return getChildrenByClass( QSItemInstruction.class );
    }

    //================================================

    public QSInstance[] getDocuments() {
        return getChildrenByClass( QSItemDocument.class );
    }

    //================================================

    public QSInstance[] getChildrenByClass( Class<?extends QSItem> childClass ) {
        List<QSInstance> list = new ArrayList<QSInstance>();

        for( QSInstance inst : children ) {
            if( childClass.isInstance( inst.itemTo ) ) {
                list.add( inst );
            }
        }
        return list.toArray( new QSInstance[0] );
    }

    //================================================

    public QSInstance[] getChildrenById( String id ) {

        for( QSInstance inst : children ) {
            if( inst.itemTo.id.equals(id) ) {
                QSInstance[] res = { inst };
                return res;
            }
        }
        return new QSInstance[0];
    }

    //================================================

    public QSInstance[] getResources() {
        return getChildrenByRel( "CapableResource" );
    }

    //================================================

    public QSInstance[] getChildrenByRel( String relType ) {
        List<QSInstance> list = new ArrayList<QSInstance>();

        for( QSInstance inst : children ) {
            if( inst.type.equals(relType) ) {
                list.add( inst );
            }
        }
        return list.toArray( new QSInstance[0] );
    }

    //================================================
}
