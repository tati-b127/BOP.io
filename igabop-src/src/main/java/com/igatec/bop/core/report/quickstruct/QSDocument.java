package com.igatec.bop.core.report.quickstruct;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import com.sun.org.apache.xerces.internal.dom.CommentImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import lombok.extern.slf4j.Slf4j;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class QSDocument {

    public Context context;
    String pprcId;
    String rootSystemId;
    QSItem rootItem;

    String checkoutDirectory;

    List<QSItem> documentItems;
    Map<String,QSItem> documentItemsById;

    List<QSItemDocument> documentDocuments;
    Map<String,QSItemDocument> documentDocumentsMap;

    List<QSItemDataReqLink> documentDataReqLinks;

    List<QSItemSystemImplementLink> documentSils;
    List<String> documentSilInstanceIds;
    Map<String,String> documentSilInstanceIdsMap;
    Map<String,QSInstance> documentSilInstancesMap;

    public DocumentImpl xmldoc;
    Element rootNode;
    ElementImpl psNode;
    ElementImpl ptsNode;
    ElementImpl ppsNode;
    ElementImpl rsNode;
    ElementImpl msNode;

    //================================================

    public QSDocument(Context _context, String _pprcId ) {
        this( _context, _pprcId, null );
    }

    //================================================

    public QSDocument(Context _context, String _pprcId, String _rootSysId ) {
        context = _context;
        pprcId = _pprcId;
        rootSystemId = _rootSysId;
        rootItem = null;

        try {
            checkoutDirectory = context.createWorkspace();
            log.info( "checkoutDirectory = " + checkoutDirectory );

        } catch (MatrixException e) {
            e.printStackTrace();
        }

        documentItems = new ArrayList<QSItem>();
        documentItemsById = new HashMap<String,QSItem>();

        documentDocuments = new ArrayList<QSItemDocument>();
        documentDocumentsMap = new HashMap<String,QSItemDocument>();

        documentDataReqLinks = new ArrayList<QSItemDataReqLink>();

        documentSils = new ArrayList<QSItemSystemImplementLink>();
        documentSilInstanceIds = new ArrayList<String>();
        documentSilInstanceIdsMap = new HashMap<String,String>();
        documentSilInstancesMap = new HashMap<String,QSInstance>();

        xmldoc = new DocumentImpl();
    }

    //================================================

    public void traverseStructure() throws Exception {
        rootItem = new QSItemRoot( this, "ID_ROOT", "PPRContext", pprcId, rootSystemId );
        rootItem.expand();
        postprocessDocs();
        postprocessSils();
        postprocessItems();

        //System.out.println( convertToXML() );
    }

    //================================================

    public String getFactoryTitle() {
        QSInstance[] systems = rootItem.getSystems(); // Will be one root system
        return systems[0].itemTo.attributesMap.get("PLMEntity.V_Name");
    }

    //================================================

    public void registerSil(QSItemSystemImplementLink item) {

        documentSils.add( item );

        for( QSPath path : item.paths ) {
            for( QSPathElement element : path.elements ) {
                String instId = element.id;
                if( documentSilInstanceIdsMap.get(instId) == null ) {
                    documentSilInstanceIds.add(instId);
                    documentSilInstanceIdsMap.put(instId,instId);
                }
            }
        }
    }

    //================================================

    public void registerItem(QSItem item) {
        documentItems.add( item );
        documentItemsById.put( item.id, item );
    }

    //================================================

    public void registerDoc(QSItemDocument item) {

        documentDocuments.add( item );
        documentDocumentsMap.put( item.id, item );
    }

    //================================================

    public void registerDataReqLink( QSItemDataReqLink item) {

        documentDataReqLinks.add( item );
    }

    //================================================

    int xmlIdCount = 0;

    public String getNewXmlId() {
        String id = "ID_" + xmlIdCount;
        xmlIdCount ++;
        return id;
    }

    //================================================

    public QSItem createItem( Class<?extends QSItem> itemClass, String type, String id, QSPath[] paths ) throws Exception {
        QSItem item = documentItemsById.get(id);
        if( item != null ) {
            return item;
        }

        Constructor<?extends QSItem> constructor = itemClass.getConstructor( QSDocument.class, String.class, String.class, String.class, QSPath[].class );
        item = constructor.newInstance( this, getNewXmlId(), type, id, paths );

        return item;
    }

    //================================================

    Map<String,QSInstance> documentInstancesById = new HashMap<String,QSInstance>();

    public QSInstance createInstance( String relType, String relId, QSItem.RelAttrs relAttrs, QSItem from, QSItem to ) {
        QSInstance instance = documentInstancesById.get(relId);
        if( instance != null ) {
            return instance;
        }

        instance = new QSInstance( this, getNewXmlId(), relType, relId, relAttrs, from, to );

        documentInstancesById.put(relId,instance);

        return instance;
    }

    //================================================

    private void postprocessDocs() throws Exception {
        StringBuilder sb = new StringBuilder();

        for( QSItemDocument doc : documentDocuments) {
            if( sb.length() > 0 ) {
                sb.append( "," );
            }
            sb.append( doc.id );
        }

        String ids = sb.toString();

        log.debug( "temp query bus * * * where \"physicalid matchlist '" + ids + "' ','\" " +
					"select physicalid format[generic].file.fileid format[generic].file.name format[generic].file.size " +
					"dump / " +
					"recordseparator |"
        );

        String res = MqlUtil.mqlCommand( context, "temp query bus $1 $2 $3 where $4 " +
                        "select physicalid $5 $6 $7 " +
                        "dump $8 " +
                        "recordseparator $9",

                "*", "*", "*", "physicalid matchlist '" + ids + "' ','",
                "format[generic].file.fileid", "format[generic].file.name", "format[generic].file.size",
                "/", "|"
        );

        if( res.length() > 0 ) {

            String[] lines = res.split("\\|");

            for (String line : lines) {
                String[] fields = (line + "/dummy").split("\\/");

                String docId = fields[3];
                String fileId = fields[4];
                String fileName = fields[5];
                String fileSize = fields[6];

                QSItemDocument item = documentDocumentsMap.get(docId);
                if (item != null) {
                    item.setFileData(getNewXmlId(), fileId, fileName, fileSize);
                }
            }

        }

    }

    //================================================

    private void postprocessSils() throws Exception {

        StringBuilder sb = new StringBuilder();

        for( String id : documentSilInstanceIds) {
            if( sb.length() > 0 ) {
                sb.append( "," );
            }
            sb.append( id );
        }

        String ids = sb.toString();

        documentSilInstancesMap = loadInstances( ids );

        for (QSItemSystemImplementLink sil : documentSils) {
            sil.postFillInstances(documentSilInstancesMap);
        }

    }

    //================================================

    public Map<String,QSInstance> loadInstances( String ids ) throws Exception {
        Map<String,QSInstance> map = new HashMap<String,QSInstance>();

        log.debug( "query connection where \"physicalid matchlist '" + ids + "' ','\" select physicalid from.type from.physicalid to.type to.physicalid " +
                "attribute[PLMInstance.V_TreeOrder] attribute[DELAsmProcessCanUseCnx.V_ResourcesQuantity] attribute[ProcessInstanceContinuous.V_UsageContCoeff] dump"
        );

        String res = MqlUtil.mqlCommand( context, "query connection where $1 select physicalid $2 $3 $4 $5 $6 $7 $8 dump",
                "physicalid matchlist '" + ids + "' ','",
                "from.type", "from.physicalid", "to.type", "to.physicalid",
                "attribute[PLMInstance.V_TreeOrder]", "attribute[DELAsmProcessCanUseCnx.V_ResourcesQuantity]", "attribute[ProcessInstanceContinuous.V_UsageContCoeff]"
        );

        if( res.length() > 0 ) {

            String[] lines = res.split("\n");

            for (String line : lines) {
                String[] fields = (line + ",dummy").split(",");
                String relType = fields[0];
                String relId = fields[1];
                String fromType = fields[2];
                String fromId = fields[3];
                String toType = fields[4];
                String toId = fields[5];
                QSItem.RelAttrs relAttrs = new QSItem.RelAttrs();
                relAttrs.childOrder = fields[6];
                relAttrs.resourcesQuantity = fields[7];
                relAttrs.usageCoefficient = fields[8];

                Class<? extends QSItem> itemClass;
                if (relType.equals("DELLmiGeneralSystemInstance")) {
                    itemClass = QSItemSystem.class;
                } else if (relType.equals("DELLmiHeaderOperationInstance")) {
                    itemClass = QSItemOperation.class;
                } else {
                    itemClass = QSItemThing.class;
                }

                QSItem itemFrom = createItem(itemClass, fromType, fromId, new QSPath[0]);
                QSItem itemTo = createItem(itemClass, toType, toId, new QSPath[0]);
                QSInstance instance = createInstance(relType, relId, relAttrs, itemFrom, itemTo);

                itemTo.expand(); // Just to have ProductImplementScopeRef
                map.put(relId, instance);
            }
        }

        return map;
    }

    //================================================

    private void postprocessItems() throws Exception {

        StringBuilder sb = new StringBuilder();

        for( QSItem item : documentItems) {
            if( sb.length() > 0 ) {
                sb.append( "," );
            }
            sb.append( item.id );
        }

        String ids = sb.toString();

        Map<String,Map<String,String>> atrMap = new HashMap<String,Map<String,String>>();

        String[] attributesToLoad = attributeMap.keySet().toArray( new String[0] );

        int len = attributesToLoad.length;
        for( int i=0; i<len; i+=5 ) {
            fillFiveAttributes(atrMap, ids,
                    (i+0<len) ? attributesToLoad[i+0] : "dummy",
                    (i+1<len) ? attributesToLoad[i+1] : "dummy",
                    (i+2<len) ? attributesToLoad[i+2] : "dummy",
                    (i+3<len) ? attributesToLoad[i+3] : "dummy",
                    (i+4<len) ? attributesToLoad[i+4] : "dummy"
            );
        }

        for( QSItem item : documentItems) {

            //System.out.println( item.id );

            Map<String,String> itemMap = atrMap.get( item.id );

            if( itemMap != null ) {
                item.setAttributes( itemMap );

              /*  for (String atrName : itemMap.keySet()) {
                    String value = itemMap.get(atrName);
                    if (value != null && value.length() > 0) {
                        System.out.println("    " + atrName + " = " + value);
                    }
                }

               */
            }

        }

    }

    //================================================

    private void fillFiveAttributes( Map<String,Map<String,String>> map, String ids, String atr1, String atr2, String atr3, String atr4, String atr5 ) throws FrameworkException {
        String[] atrNames = { atr1, atr2, atr3, atr4, atr5 };

        String strange = "iresaf24389dirghfiueoemd45ut";
        String fldSep = "==FLD="+strange+"=FLD==";
        String recSep = "==REC="+strange+"=REC==";

		log.debug("temp query bus * * * " +
                "where \"physicalid matchlist '" + ids + "' ','\" " +
                "select physicalid attribute["+atr1+"] attribute["+atr2+"] attribute["+atr3+"] attribute["+atr4+"] attribute["+atr5+"] " +
                "dump " + fldSep +" " +
                "recordseparator "+recSep );

        String res = MqlUtil.mqlCommand(context, "temp query bus $1 $2 $3 " +
                "where $4 " +
                "select physicalid $5 $6 $7 $8 $9 " +
                "dump $10 " +
                "recordseparator $11 ",

                "*", "*", "*", "physicalid matchlist '" + ids + "' ','",

                "attribute["+atr1+"]",
                "attribute["+atr2+"]",
                "attribute["+atr3+"]",
                "attribute["+atr4+"]",
                "attribute["+atr5+"]",
                fldSep, recSep
        );

        if( res.length() > 0 ) {
            String[] lines = (res + "=").split(recSep);

            for (String line : lines) {
                String[] fields = line.split(fldSep);
                String name = fields[1];
                String revision = fields[2];
                String id = fields[3];

                Map<String, String> itemMap = map.get(id);
                if (itemMap == null) {
                    itemMap = new HashMap<String, String>();
                    map.put(id, itemMap);
                }

                itemMap.put( "_basic::name", name );
                itemMap.put( "_basic::revision", revision );

                for (int i = 0; i < atrNames.length && 4 + i < fields.length; i++) {
                    itemMap.put(atrNames[i], fields[4 + i]);
                }
            }
        }

    }

    //================================================

    protected Map<String,String> mbomTypeMap = fillMbomTypeMap();

    private Map<String,String> fillMbomTypeMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put( "Kit_CoolingMixture", "ContinuousProvidedMaterial" );
        map.put( "Kit_MfgAssembly", "ManufacturingAssembly" );
        map.put( "Kit_MfgBar", "Transform" );
        map.put( "Kit_MfgContinuousPrvd", "ContinuousProvidedMaterial" );
        map.put( "Kit_MfgProducedPart", "ManufacturedMaterial" );
        map.put( "Kit_MfgTransform", "Transform" );

        map.put( "Kit_MfgRawMaterial", "ProvidedPart" );
        map.put( "Kit_MfgStandardComponent", "ProvidedPart" );
        map.put( "Kit_MfgOEMComponent", "ProvidedPart" );
        map.put( "Kit_MfgKitPart", "ProvidedPart" );

        map.put( "Kit_MfgOEMKit", "Kit_MfgOEMKit" );
        map.put( "Kit_MfgKit", "Kit_MfgKit" );

        return map;
    }

    //================================================

    protected Map<String,String> resourceTypeMap = fillResourceTypeMap();

    private Map<String,String> fillResourceTypeMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put( "ErgoHuman", "Worker" );
        map.put( "IndustrialMachine", "IndustrialMachine" );
        map.put( "NCMachine", "NCMachine" );
        map.put( "ToolDevice", "ToolEquipment" );
        return map;
    }

    //================================================

    private Map<String,String> attributeMap = fillAttributeMap();

    private Map<String,String> fillAttributeMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put( "PLMEntity.V_Name", "=Name" );
        map.put( "Title", "=Name" );
        map.put( "PLMEntity.V_description", "=Description" );

        //map.put( "PLMInstance.V_discipline", "V_discipline" );
        map.put( "PLMEntity.V_discipline", "" );

        map.put( "DELAsmProcessCanUseCnx.V_ResourcesQuantity", "" );
        map.put( "DELWkiInstructionReference.V_WIInstruction_Text", "=Text" );

        map.put( "DELFmiContQuantity_Area.V_ContQuantity", "=AreaQuantity" );
        map.put( "DELFmiContQuantity_Length.V_ContQuantity", "=LengthQuantity" );
        map.put( "DELFmiContQuantity_Mass.V_ContQuantity", "=MassQuantity" );
        map.put( "DELFmiContQuantity_Volume.V_ContQuantity", "=VolumeQuantity" );

        map.put( "Kit_MfgBar.Kit_NumberOfParts", "Kit_NumberOfParts" );
        map.put( "Kit_MfgBar.Kit_StockLength", "Kit_StockLength" );
        map.put( "Kit_MfgBar.Kit_UsageCoeffic", "Kit_UsageCoeffic" );
        map.put( "Kit_MfgBar.Kit_MatConsumptionRate", "Kit_MatConsumptionRate" );
        map.put( "Kit_MfgBar.Kit_StockHightDiameter", "Kit_StockHightDiameter" );
        map.put( "Kit_MfgBar.Kit_StockWidth", "Kit_StockWidth" );
        map.put( "Kit_MfgBar.Kit_StockWeight", "Kit_StockWeight" );
        map.put( "Kit_MfgBar.Kit_StandardUnit", "Kit_StandardUnit" );
        map.put( "Kit_MfgBar.Kit_RawType", "Kit_RawType" );
        map.put( "Kit_MfgBar.Kit_BarCode", "Kit_BarCode" );
        map.put( "Kit_MfgBar.Kit_UnitValue", "Kit_UnitValue" );
        map.put( "Kit_MainOp.Kit_Kpcs", "Kit_Kpcs" );
        map.put( "Kit_MainOp.Kit_Tpcs", "Kit_Tpcs" );
        map.put( "Kit_MainOp.Kit_WorkConditionCode", "Kit_WorkConditionCode" );
        map.put( "Kit_MainOp.Kit_OperationCode", "Kit_OperationCode" );
        map.put( "Kit_MainOp.Kit_Tv", "Kit_Tv" );
        map.put( "Kit_MainOp.Kit_PrepTime", "Kit_PrepTime" );
        map.put( "Kit_MainOp.Kit_To", "Kit_To" );
        map.put( "Kit_MainOp.Kit_NumOperation", "Kit_NumOperation" );
        map.put( "Kit_MainOp.Kit_Percentage", "Kit_Percentage" );
        map.put( "Kit_MainOp.Kit_NumSimultaneousManufParts", "Kit_NumSimultaneousManufParts" );
        map.put( "Kit_MainOp.Kit_MechanizationCode", "Kit_MechanizationCode" );
        map.put( "Kit_MainOp.Kit_VolProdBatchPieces", "Kit_VolProdBatchPieces" );
        map.put( "Kit_MainOp.Kit_Control", "Kit_Control" );
        map.put( "Kit_MatExt.Kit_RSName", "Kit_RSName" );
        map.put( "Kit_Worker.Kit_ProfessionCode", "Kit_ProfessionCode" );
        map.put( "Kit_Worker.Kit_JobRank", "Kit_JobRank" );
        map.put( "Kit_PNExt.Kit_M_PartNumber", "Kit_M_PartNumber" );
        map.put( "Kit_WeightExt.Kit_MatCode", "Kit_MatCode" );
        map.put( "Kit_Transition.Kit_TransitionNumber", "Kit_TransitionNumber" );
        map.put( "Kit_FurtherDetails.Kit_Category", "Kit_Category" );
        map.put( "ProcessInstanceContinuous.V_UsageContCoeff", "=UsageCoefficient" );
        map.put( "WCGEquivalentComputedExt.V_WCG_Mass", "V_WCG_Mass" );
        map.put( "WCGEquivalentDeclaredWeightExt.V_WCG_Declared_Mass", "V_WCG_Declared_Mass" );
        map.put( "XP_VPMReference_Ext.Mass", "XP_Mass" );
        return map;
    }

    //================================================

    public String convertToXML() {
        rootNode = xmldoc.createElementNS( "http://www.3ds.com/xsd/XPDMXML", "XPDMXML" );
        xmldoc.appendChild( rootNode );

        buildDOM();

        String xmlText = xmldoc.saveXML( rootNode );
        xmlText = xmlText.replace( "UTF-16", "UTF-8" );

        return xmlText;
    }

    //================================================

    private void buildDOM() {

        appendComment( rootNode, rootItem.type+" "+rootItem.name+" "+rootItem.revision+" physicalid="+rootItem.id );

        psNode = appendChildNode( rootNode,"ProductStructure" );

        ptsNode = appendChildNode( rootNode,"ProductTransformationStructure" );

        ppsNode = appendChildNode( rootNode,"ProcessPlanningStructure" );

        rsNode = appendChildNode( rootNode,"ResourceStructure" );

        msNode = appendChildNode( rootNode,"MultiStructure" );

        QSInstance[] rootMbom = getRootMbom();
        String mbomRootIds = getSpacedIds( rootMbom );
        if( mbomRootIds != null ) {
            ptsNode.setAttribute( "rootRefs", mbomRootIds );
        }

        for( QSInstance inst : rootMbom ) {
            appendItemOnce( ptsNode, inst.itemTo.getMbomNodeName(), inst.itemTo );
        }

        QSInstance[] rootSystems = rootItem.getSystems();
        for( QSInstance inst : rootSystems ) {
            processSystemTree( inst, true );
        }

        for( QSItemSystemImplementLink sil : documentSils ) {
            processSil( sil );
        }

        for( QSItemDataReqLink drl : documentDataReqLinks ) {
            processDataReqLink( drl );
        }

    }

    //================================================

    private void processSystemTree( QSInstance inst ) {
        processSystemTree( inst, false );
    }

    //================================================

    private void processSystemTree( QSInstance inst, boolean isRoot ) {

        appendItemOnce( ppsNode, "GeneralSystem", inst.itemTo );
        if( ! isRoot ) {
            appendInstanceOnce( ppsNode, "SystemInst", inst );
        }

        QSInstance[] operations = inst.itemTo.getOperations();
        for( QSInstance opInst : operations ) {
            processOperation( opInst );
        }

        QSInstance[] subSystems = inst.itemTo.getSystems();
        for( QSInstance subInst : subSystems ) {
            processSystemTree( subInst );
        }
    }

    //================================================

    private void processOperation( QSInstance inst ) {

        appendItemOnce( ppsNode, "HeaderOperation", inst.itemTo );
        appendInstanceOnce( ppsNode, "OperationInst", inst );

        QSInstance[] instructions = inst.itemTo.getInstructions();
        for( QSInstance insInst : instructions ) {
            processInstruction( insInst );
        }

        QSInstance[] resources = inst.itemTo.getResources();
        for( QSInstance resInst : resources ) {
            processResource( resInst );
        }

        QSInstance[] documents = inst.itemTo.getDocuments();
        for( QSInstance docInst : documents ) {
            processDocument( docInst );
        }

    }

    //================================================

    private void processInstruction( QSInstance inst ) {

        appendItemOnce( ppsNode, "Instruction", inst.itemTo );
        appendInstanceOnce( ppsNode, "InstructionInst", inst );

        QSInstance[] resources = inst.itemTo.getResources();
        for( QSInstance resInst : resources ) {
            processResource( resInst );
        }

        QSInstance[] documents = inst.itemTo.getDocuments();
        for( QSInstance docInst : documents ) {
            processDocument( docInst );
        }

    }

    //================================================

    private void processResource( QSInstance inst ) {
        appendItemOnce( rsNode, inst.itemTo.getResourceNodeName(), inst.itemTo );

        if( ! inst.isProduced() ) {
            inst.setProduced();
            appendComment(ppsNode, inst.id);
            ppsNode.appendChild(inst.makeResourceInstNode());
        }
    }

    //================================================

    private void processDocument( QSInstance inst ) {

        QSItemDocument item = (QSItemDocument)inst.itemTo;

        if( ! item.isProduced() ) {
            item.setProduced();
            appendComment(msNode, item.id);
            msNode.appendChild( item.makeDocumentNode() );
            rootNode.appendChild( item.makeFileNode() );

            if( ! item.fileSize.equals("0")) {
                item.checkoutFile( getCheckoutDirectory() );
            }
        }

        if( ! inst.isProduced() ) {
            inst.setProduced();
            appendComment(msNode, inst.id);
            msNode.appendChild(inst.makeDocumentInstNode());
        }
    }

    //================================================

    private void processSil( QSItemSystemImplementLink sil  ) {

        if( !sil.isProduced() && sil.opRef.size()>0 && sil.tranRef.size()>0 ) {

            sil.setProduced();

            appendComment(ppsNode, sil.id);

            ElementImpl silNode = appendChildNode(ppsNode, "SystemImplementLink");
            ElementImpl opNode = appendChildNode(silNode, "OperationRef");
            ElementImpl tranNode = appendChildNode(silNode, "TransformationInstanceRef");

            for (QSInstance inst : sil.opRef) {
                appendComment(opNode, inst.itemTo.id);
                addPathItemNode(opNode, inst.xmlId);
            }

            for (QSInstance inst : sil.tranRef) {
                appendComment(tranNode  , inst.itemTo.id);
                addPathItemNode(tranNode, inst.xmlId);
                appendItemOnce( ptsNode, inst.itemTo.getMbomNodeName(), inst.itemTo );
                appendInstanceOnce( ptsNode, "TransformationInst", inst );
            }
        }
    }

    //================================================

    private void processDataReqLink( QSItemDataReqLink drl ) {
        drl.appendNodes( ptsNode );
    }

    //================================================

    public void appendItemOnce( ElementImpl parentNode, String nodeName, QSItem item ) {
        if( ! item.isProduced() ) {
            item.setProduced();
            appendComment(parentNode, item.id );
            parentNode.appendChild( item.makeRefNode(nodeName) );
        }
    }

    //================================================

    public void appendInstanceOnce( ElementImpl parentNode, String nodeName, QSInstance inst ) {
        if( ! inst.isProduced() ) {
            inst.setProduced();
            appendComment(parentNode, inst.id );
            parentNode.appendChild( inst.makeInstNode(nodeName) );
        }
    }

    //================================================

    public void addPathItemNode( ElementImpl pathNode, String value ) {
        ElementImpl piNode = appendChildNode( pathNode, "PathItem" );
        piNode.appendChild(new TextImpl(xmldoc, value));
    }

    //================================================

    protected void appendComment(Node node, String text ) {
        CommentImpl childNode = new CommentImpl(xmldoc,text);
        node.appendChild( childNode );
    }

    //================================================

    private String getSpacedIds(QSInstance[] instances ) {
        StringBuilder sb = new StringBuilder();
        for( QSInstance inst : instances ) {
            if( sb.length() > 0 )
                sb.append(" ");
            sb.append( inst.itemTo.xmlId );
        }
        return sb.toString();
    }

    //================================================

    protected ElementImpl appendChildNode(Node node, String childName ) {
        ElementImpl childNode = new ElementImpl(xmldoc,childName);
        node.appendChild( childNode );
        return childNode;
    }

    //================================================

    QSInstance[] getRootMbom() {

        QSInstance[] mbom = rootItem.getChildrenByRel("MbomMetaInst" );

        return mbom;
    }

    //================================================

    public void appendAttributes( ElementImpl node, QSItem item ) {

        for (String attrName : item.attributesMap.keySet() ) {
            String attrValue = item.attributesMap.get( attrName );

            if( attrValue!=null && attrValue.length()>0 ) {
                String newName = attributeMap.get(attrName);
                if (newName != null) {
                    if (newName.startsWith("="))
                        appendAttrNode(node, newName.substring(1), attrValue);
                    else if (newName.length() > 0) // Empty name means skip attribute
                        appendAttrProperty(node, newName, attrValue);
                }
            }
        }

    }

    //================================================

    public void appendAttrNode( ElementImpl node, String attrName, String attrValue ) {
        if( attrValue!=null && attrValue.length()>0 ) {
            ElementImpl attrNode = appendChildNode(node, attrName);
            attrNode.appendChild(new TextImpl(xmldoc, attrValue));
        }
    }

    //================================================

    public void appendAttrProperty( ElementImpl node, String attrName, String attrValue ) {

        ElementImpl attrNode = appendChildNode( node, "Property" );
        attrNode.setAttribute( "name", attrName);

        ElementImpl valueNode = appendChildNode( attrNode, "Value" );

        valueNode.appendChild(new TextImpl(xmldoc, attrValue));
    }

    //================================================

    public String getCheckoutDirectory() {
        return checkoutDirectory;
    }

    //================================================

    private String urlify( String path ) {
        String slashcorrectedDir = path.replace( '\\', '/');
        if( slashcorrectedDir.charAt(0) != '/' ) {
            slashcorrectedDir = '/'+slashcorrectedDir;
        }
        return "file://"+slashcorrectedDir;
    }

    //================================================

    public String getCheckoutURL() {
        return urlify( checkoutDirectory );
    }

    //================================================
}
