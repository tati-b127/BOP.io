package com.igatec.bop.utils;

import com.dassault_systemes.catrgn.connector.plm.services.PLMConnectorServices;
import com.dassault_systemes.platform.model.CommonWebException;
import com.dassault_systemes.platform.model.Oxid;
import com.dassault_systemes.platform.model.PathHolder;
import com.dassault_systemes.platform.model.SRHolder;
import com.dassault_systemes.platform.model.implement.TreeOrderServices;
import com.dassault_systemes.platform.model.itf.IOxidService;
import com.dassault_systemes.platform.model.services.IdentificationServicesProvider;
import com.dassault_systemes.platform.model.services.MdIDBaseServices;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.common.db.Basics;
import com.matrixone.apps.common.Document;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.Path;
import matrix.db.PathQuery;
import matrix.db.PathQueryIterator;
import matrix.db.PathWithSelect;
import matrix.db.PathWithSelectList;
import matrix.util.StringList;
import matrix.util.UUID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.ATTRIBUTE_APP_INDEX;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_C_UPDATESTAMP;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_ID_REL;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_PLM_EXTERNAL_ID;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_PRIVATE_DATA;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_REL_C_UPDATESTAMP;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_REL_V_DISCIPLINE;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_REL_V_SEC_LEVEL;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_STREAM;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_CNX_TREE_ORDER;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_DISCIPLINE;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_NAME;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_ORDER;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_PORT_TREE_ORDER;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_RESOURCES_QUANTITY;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_SEC_LEVEL;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_TREE_ORDER;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_VERSION_COMMENT;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_VERSION_ID;
import static com.igatec.bop.utils.Constants.DEFAULT_APP_INDEX;
import static com.igatec.bop.utils.Constants.DEFAULT_C_UPDATESTAMP;
import static com.igatec.bop.utils.Constants.DEFAULT_ID_REL;
import static com.igatec.bop.utils.Constants.DEFAULT_PRIVATE_DATA;
import static com.igatec.bop.utils.Constants.DEFAULT_REVISION;
import static com.igatec.bop.utils.Constants.DEFAULT_STREAM_DOC;
import static com.igatec.bop.utils.Constants.DEFAULT_V_ORDER;
import static com.igatec.bop.utils.Constants.DEFAULT_V_SEC_LEVEL;
import static com.igatec.bop.utils.Constants.DELIMITER_BELL;
import static com.igatec.bop.utils.Constants.DELIMITER_COMMA;
import static com.igatec.bop.utils.Constants.INTERFACE_PLM_CORE_STREAM_STORAGE;
import static com.igatec.bop.utils.Constants.IOT;
import static com.igatec.bop.utils.Constants.KIND_BUSINESSOBJECT;
import static com.igatec.bop.utils.Constants.KIND_CONNECTION;
import static com.igatec.bop.utils.Constants.PARAM_COMPOSEE;
import static com.igatec.bop.utils.Constants.PARAM_INSTANCE_ID;
import static com.igatec.bop.utils.Constants.PARAM_PARENT_ID;
import static com.igatec.bop.utils.Constants.PARAM_SCOPED;
import static com.igatec.bop.utils.Constants.POLICY_DOCUMENT_RELEASE;
import static com.igatec.bop.utils.Constants.POLICY_VPLM_SMB_CX_DEFINITION;
import static com.igatec.bop.utils.Constants.QUERY_WILDCARD;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_OBJECT_ROUTE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_PLM_CONNECTION;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_PLM_INSTANCE;
import static com.igatec.bop.utils.Constants.ROLE_DEL_LINK_TO_CONSTRAINED_OBJECT;
import static com.igatec.bop.utils.Constants.ROLE_DEL_LINK_TO_CONSTRAINING_OBJECT;
import static com.igatec.bop.utils.Constants.ROLE_DEL_PCU_OWNER;
import static com.igatec.bop.utils.Constants.ROLE_DEL_PCU_RESULT;
import static com.igatec.bop.utils.Constants.ROLE_DMT_DOCUMENT;
import static com.igatec.bop.utils.Constants.ROLE_PLM_IMPLEMENTLINK_SOURCE;
import static com.igatec.bop.utils.Constants.ROLE_PLM_IMPLEMENTLINK_TARGET;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_V_NAME;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_V_TREE_ORDER;
import static com.igatec.bop.utils.Constants.SELECT_DOCS;
import static com.igatec.bop.utils.Constants.SELECT_ELEMENT_KIND;
import static com.igatec.bop.utils.Constants.SELECT_ELEMENT_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_FROM_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_ICON_NAME;
import static com.igatec.bop.utils.Constants.SELECT_ID;
import static com.igatec.bop.utils.Constants.SELECT_INTERFACE;
import static com.igatec.bop.utils.Constants.SELECT_OWNER_NAME;
import static com.igatec.bop.utils.Constants.SELECT_OWNER_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_OWNER_TYPE;
import static com.igatec.bop.utils.Constants.SELECT_PATH;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_EL_0_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_EL_0_TYPE;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_EL_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_ID;
import static com.igatec.bop.utils.Constants.SELECT_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_POLICY;
import static com.igatec.bop.utils.Constants.SELECT_TO_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_TYPE;
import static com.igatec.bop.utils.Constants.SELECT_VERSIONID;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE_3;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE_4;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE_5;
import static com.igatec.bop.utils.Constants.SEMANTIC_RELATION;
import static com.igatec.bop.utils.Constants.STATE_FROZEN;
import static com.igatec.bop.utils.Constants.TYPE_DOCUMENT;
import static com.igatec.bop.utils.Constants.TYPE_KIT_FACTORY;
import static com.igatec.bop.utils.Constants.TYPE_KIT_MFG_ASSEMBLY;
import static com.igatec.bop.utils.Constants.TYPE_KIT_MFG_PRODUCED_PART;
import static com.igatec.bop.utils.Constants.TYPE_MFG_PRODUCTION_PLANNING;
import static com.igatec.bop.utils.Constants.TYPE_PLM_DOC_CONNECTION;
import static com.igatec.bop.utils.Constants.TYPE_PROCESS_CAN_USE_CNX;
import static com.igatec.bop.utils.Constants.TYPE_PROCESS_IMPLEMENT_CNX;
import static com.igatec.bop.utils.Constants.TYPE_PROCESS_PREREQUISITE_CNX_CUST;
import static com.igatec.bop.utils.Constants.TYPE_PROCESS_PREREQUISITE_PORT;
import static com.igatec.bop.utils.Constants.TYPE_ROUTE;
import static com.igatec.bop.utils.Constants.TYPE_TIME_CONSTRAINT_CNX;
import static com.igatec.bop.utils.Constants.VAULT_ESERVICE_PRODUCTION;
import static com.igatec.bop.utils.Constants.VAULT_VPLM;
import static com.matrixone.apps.domain.DomainConstants.ATTRIBUTE_TITLE;
import static com.matrixone.apps.domain.DomainConstants.FORMAT_GENERIC;
import static com.matrixone.apps.domain.DomainConstants.SELECT_ATTRIBUTE_TITLE;
import static com.matrixone.apps.domain.DomainConstants.SELECT_DESCRIPTION;

@Slf4j
public class Util {
    public static Path createSR(Context context, String sOwnerPLMID, String[] elementIds, String semantics, String role, String sIDRel) throws Exception {
        //ProcessPIDServices processPIDServices = new ProcessPIDServices();
        //processPIDServices.createSR(context, sOwnerPLMID, sElementPLMID, semantics, role);

        //alternate variant
        IOxidService iOxidService = IdentificationServicesProvider.getOxidService();
        Oxid oxid = iOxidService.getOxidFromPLMId(context, sOwnerPLMID);
        //String[] elementIds = new String[]{sElementPID};
        PathHolder pathHolder = new PathHolder(elementIds);
        List<PathHolder> lPH = new ArrayList<>();
        lPH.add(pathHolder);
        SRHolder srHolder = new SRHolder(lPH, semantics, role);
        SRService srService = new SRService();
        List<String> pathIds = srService.createSR(context, oxid, srHolder);//contains list of path ids

        //POST PROCESS
        AttributeList attrList = new AttributeList();
        attrList.addElement(new Attribute(new AttributeType(ATTRIBUTE_APP_INDEX), DEFAULT_APP_INDEX));
        attrList.addElement(new Attribute(new AttributeType(ATTRIBUTE_PRIVATE_DATA), DEFAULT_PRIVATE_DATA));
        attrList.addElement(new Attribute(new AttributeType(ATTRIBUTE_ID_REL), sIDRel));

        //expect working with only one path instance
        Path path = new Path(pathIds.get(0));
        path.setAttributeValues(context, attrList);

        return path;
    }

    /**
     * it's not working because of there are no "ODTEnoviaServer" and "ODTEnoviaRootURI" env vars in SemanticRelation.CreateSRs(context, list)
     * String var2 = Sys.getEnvEx("ODTEnoviaServer");
     * String var3 = Sys.getEnvEx("ODTEnoviaRootURI");
     */
    private static void createSR_v2(Context context, String sOwnerPLMID, String[] elementIds, String semantics, String role, String sIDRel) {
        /*        List<ENOVPathElement> pathElements = new ArrayList<>();
        for (String id : elementIds) {
            boolean isRelationship = true;
            String sPLMID = PLMConnectorServices.getPLMID(context, id, isRelationship);
            PLMID plmid = new PLMID(sPLMID);
            ENOVPathElement pathEl = new ENOVPathElement(plmid);
            pathElements.add(pathEl);
        }
        ENOVPathDescriptor pathDesc = new ENOVPathDescriptor(pathElements, DEFAULT_PRIVATE_DATA);
        SemanticRelationship sr = new SemanticRelationship(pathDesc, Integer.parseInt(sIDRel), semantics, role);
        SemanticRelationshipList srList = new SemanticRelationshipList(MultiValueAttribute.ADD_OR_OVERWRITE);
        srList.add(sr);

        List<SRLogEntry> list = new ArrayList<>();
        PLMID plmid = new PLMID(sOwnerPLMID);
        list.add(new SRLogEntry(plmid, null, srList));
        SemanticRelation.CreateSRs(context, list);*/
    }

    public static String createComposee(Context context, String sType, String sPolicy, String sPIDComposer, String sComposition) throws FrameworkException, CommonWebException {
        String sName = UUID.getNewUUIDHEXString();
        String sRev = "";
        String addCmd = "add bus $1 $2 $3 policy $4 composer $5 composition $6 select $7 dump";
        String sComposeePID = MqlUtil.mqlCommand(context, addCmd, sType, sName, sRev, sPolicy, sPIDComposer, sComposition, SELECT_PHYSICALID);

        //POST PROCESS
        //set pid as name
        MqlUtil.mqlCommand(context, "mod bus $1 name $2", sComposeePID, sComposeePID);
        //mod attributes
        DomainObject domObj = DomainObject.newInstance(context, sComposeePID);
        Map<String, String> attrMap = new HashMap<>();

        //tree order
        String treeOrderAttr = ATTRIBUTE_V_CNX_TREE_ORDER;//by default
        if (TYPE_PROCESS_PREREQUISITE_PORT.equals(sType))
            treeOrderAttr = ATTRIBUTE_V_PORT_TREE_ORDER;
        attrMap.put(treeOrderAttr, Double.toString(TreeOrderServices.createDefaultTreeOrderValue()));

        if (!TYPE_PROCESS_CAN_USE_CNX.equals(sType) && !TYPE_PLM_DOC_CONNECTION.equals(sType)) {
            attrMap.put(ATTRIBUTE_C_UPDATESTAMP, DEFAULT_C_UPDATESTAMP);
            attrMap.put(ATTRIBUTE_V_SEC_LEVEL, DEFAULT_V_SEC_LEVEL);
            if (!TYPE_PROCESS_PREREQUISITE_CNX_CUST.equals(sType) && !TYPE_PROCESS_PREREQUISITE_PORT.equals(sType))
                attrMap.put(ATTRIBUTE_V_DISCIPLINE, sType);//always equals type
        }
        domObj.setAttributeValues(context, attrMap);

        boolean isRelationship = false;
        return PLMConnectorServices.getPLMID(context, sComposeePID, isRelationship);
    }

    /**
     * Creates 'Product Flow' link between two Header Operations
     * (copy of 'Create Product Flow' function in Planning Structure app)
     *
     * @param sSysPID   General System object's id
     * @param sFromPIDs Header Operation instance's (connection's!) id which is followed by sToPID
     * @param sToPIDs   Header Operation instance's (connection's!) id which follows for sFromPID
     */
    @SneakyThrows
    public static String createProductFlow(Context context, String sSysPID, String[] sFromPIDs, String[] sToPIDs) {
        String sComposeePLMID = createComposee(context, TYPE_TIME_CONSTRAINT_CNX, POLICY_VPLM_SMB_CX_DEFINITION, sSysPID, RELATIONSHIP_PLM_CONNECTION);

        //todo here
        createSR(context, sComposeePLMID, sFromPIDs, SEMANTICS_REFERENCE, ROLE_DEL_LINK_TO_CONSTRAINING_OBJECT, "1");
        createSR(context, sComposeePLMID, sToPIDs, SEMANTICS_REFERENCE, ROLE_DEL_LINK_TO_CONSTRAINED_OBJECT, "2");

        return MdIDBaseServices.getPhysicalIDfromPLMId(sComposeePLMID);
    }

    public static void createLinkToResource(Context context, String sOperationPID, String sResourcePID) throws Exception {
        String sComposeePLMID = createComposee(context, TYPE_PROCESS_CAN_USE_CNX, POLICY_VPLM_SMB_CX_DEFINITION, sOperationPID, RELATIONSHIP_PLM_CONNECTION);

        createSR(context, sComposeePLMID, new String[]{sOperationPID}, SEMANTICS_REFERENCE_3, ROLE_DEL_PCU_OWNER, DEFAULT_ID_REL);
        createSR(context, sComposeePLMID, new String[]{sResourcePID}, SEMANTICS_REFERENCE_4, ROLE_DEL_PCU_RESULT, DEFAULT_ID_REL);
    }

    public static void createScope(Context context, String sPIDComposer, String sMBOMPID) throws Exception {
        String sComposeePLMID = createComposee(context, TYPE_MFG_PRODUCTION_PLANNING, POLICY_VPLM_SMB_CX_DEFINITION, sPIDComposer, RELATIONSHIP_PLM_CONNECTION);

        createSR(context, sComposeePLMID, new String[]{sPIDComposer}, SEMANTICS_REFERENCE_5, ROLE_PLM_IMPLEMENTLINK_SOURCE, DEFAULT_ID_REL);
        createSR(context, sComposeePLMID, new String[]{sMBOMPID}, SEMANTICS_REFERENCE_3, ROLE_PLM_IMPLEMENTLINK_TARGET, DEFAULT_ID_REL);
    }

    public static String autoname() {
        return Long.toString(System.currentTimeMillis());
    }

    /**
     * Checks if a given string empty or null
     *
     * @param str string var to check
     */
    public static boolean isNullOrEmpty(String str) {
        return null == str || "".equals(str);
    }

    //obj - multiple value from map (for example, for key "from.to.id") it can be String or StringList
    public static List<String> normalizeToList(Object obj) {
        List<String> list = new ArrayList<>();

        // 1) DomainObject.findObjects() return String with "BELL" char as delimiter: "id1id2..."
        if (obj instanceof String) {
            String str = (String) obj;
            if (str.contains(DELIMITER_BELL))
                list = new ArrayList<>(Arrays.asList(str.split(DELIMITER_BELL)));
            else
                list.add(str);
        } else if (obj instanceof StringList) {
            StringList sl = (StringList) obj;
            list = sl.toList();
        }

        return list;
    }

    /* Override DomainObject.getInfo(Context var1, StringList var2, Collection var3, boolean var4) method
    because of bug with select = "from[VPLMrel/PLMConnection/V_Owner].to.paths[SemanticRelation].path.element[0]".
    This select(=key) always store only one value but db may have any number of values
    */
    public static Map getInfo(Context context, DomainObject domObj, StringList selects) throws FrameworkException {
        boolean var5 = domObj.openObject(context);

        try {
            BusinessObjectWithSelect data = domObj.select(context, selects, true);
            Hashtable htValues = data.getHashtable();
            Vector keys = data.getSelectKeys();

            for (Object o : keys) {
                String key = (String) o;
                List lValues = (List) htValues.get(key);
                if (lValues != null && lValues.size() == 1) {
                    String value = (String) lValues.get(0);
                    htValues.put(key, value);
                }
            }

            return htValues;
        } catch (Exception var16) {
            throw new FrameworkException(var16);
        } finally {
            domObj.closeObject(context, var5);
        }
    }

    public static List<DBObject> getInfo(Context context, String[] objectIds, StringList selects) throws FrameworkException {
        List<Map> results = DomainObject.getInfo(context, objectIds, selects);
        return results.stream().map(DBObject::fromMap).collect(Collectors.toList());
    }

    /**
     * Creates new 'BOP entity' object
     *
     * @param type   type name
     * @param policy policy name
     */
    public static String create(Context context, String type, String policy) throws Exception {
        long l = System.currentTimeMillis();
        String sName = Util.autoname();
        log.info(String.format("%s\t%s\t%s", "Util.create", "Util.autoname", System.currentTimeMillis() - l));
        l = System.currentTimeMillis();
        String sResult = MqlUtil.mqlCommand(context, "add bus '$1' '$2' '$3' policy '$4' vault '$5' select $6 $7 dump '$8'",
                type, sName, DEFAULT_REVISION, policy, VAULT_VPLM, SELECT_PHYSICALID, SELECT_VERSIONID, DELIMITER_COMMA);
        String[] aResult = sResult.split(DELIMITER_COMMA);
        String sNewPID = aResult[0];
        String sVID = aResult[1];
        log.info(String.format("%s\t%s\t%s", "Util.create", "add bus", System.currentTimeMillis() - l));
        l = System.currentTimeMillis();
        String[] attrs = {
                sNewPID,
                ATTRIBUTE_V_NAME, sName,
                ATTRIBUTE_C_UPDATESTAMP, DEFAULT_C_UPDATESTAMP,
                ATTRIBUTE_V_SEC_LEVEL, DEFAULT_V_SEC_LEVEL,
                ATTRIBUTE_PLM_EXTERNAL_ID, sName,
                ATTRIBUTE_V_ORDER, DEFAULT_V_ORDER,
                ATTRIBUTE_V_VERSION_ID, sVID};
        MqlUtil.mqlCommand(context, "mod bus $1 $2 $3 $4 $5 $6 $7 $8 $9 $10 $11 $12 $13", attrs);
        log.info(String.format("%s\t%s\t%s", "Util.create", "mod bus", System.currentTimeMillis() - l));
        return sNewPID;
    }

    /**
     * Creates duplicate of any DBObject
     *
     * @param pid physicalid to be copied from
     */
    public static String cloneOperation(Context context, String pid) throws Exception {
        String sName = Util.autoname();
        long l = System.currentTimeMillis();
        String sNewPID = MqlUtil.mqlCommand(context, "copy bus '$1' to '$2' '$3' select $4 dump '$5'",
                pid, sName, DEFAULT_REVISION, SELECT_PHYSICALID, DELIMITER_COMMA);
        log.info(String.format("%s\t%s\t%s", "Util.clone", "copy bus", System.currentTimeMillis() - l));
        MqlUtil.mqlCommand(context, "mod bus $1 $2 $3 $4 $5", sNewPID,
                ATTRIBUTE_V_VERSION_COMMENT, pid,
                ATTRIBUTE_PLM_EXTERNAL_ID, sName
        );
        return sNewPID;
    }

    /**
     * Creates duplicate of any DBObject
     *
     * @param pid physicalid to be copied from
     */
    public static String clone(Context context, String pid, boolean isCloneFromTwin) throws Exception {
        long l = System.currentTimeMillis();
        StringList selects = new StringList();
        selects.add(SELECT_TYPE);
        selects.add(SELECT_POLICY);
        selects.add(SELECT_INTERFACE);
        selects.add("attribute.value");
        String objWhere = String.format("%s match '%s'", SELECT_PHYSICALID, pid);
        MapList ml = DomainObject.findObjects(context, QUERY_WILDCARD, VAULT_VPLM, objWhere, selects);
        Map<String, Object> m = (Map) ml.get(0);
        String sType = (String) m.get(SELECT_TYPE);
        String sPolicy = (String) m.get(SELECT_POLICY);
        String sInterfaces = (String) m.get(SELECT_INTERFACE);
        String sNewPID = create(context, sType, sPolicy);
        Map<String, Object> attrMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            String attrName = entry.getKey();
            if (attrName.startsWith("attribute")) {
                attrName = attrName.substring(attrName.indexOf("[") + 1, attrName.indexOf("]"));

                //exclude PLM_ExternalID to be equal 'name' as set in create method
                if (!ATTRIBUTE_PLM_EXTERNAL_ID.equals(attrName))
                    attrMap.put(attrName, entry.getValue());
            }
        }

        //POST PROCESS
        if (!isNullOrEmpty(sInterfaces))
            MqlUtil.mqlCommand(context, "mod bus $1 add interface '$2'", sNewPID, sInterfaces);
        //mod attributes
        if (isCloneFromTwin)
            attrMap.put(ATTRIBUTE_V_VERSION_COMMENT, pid);//store id of copied obj from digital twin
        //change for unique V_Name rule, only for type Kit_Factory
        if (TYPE_KIT_FACTORY.equals(sType)) {
            String sVName = String.format("%s_%s", attrMap.get(ATTRIBUTE_V_NAME), autoname());
            attrMap.put(ATTRIBUTE_V_NAME, sVName);
        }

        DomainObject domObjNew = DomainObject.newInstance(context, sNewPID);
        domObjNew.setAttributeValues(context, attrMap);
        log.info(String.format("%s\t%s\t%s", "Util.clone", "total", System.currentTimeMillis() - l));

        return sNewPID;
    }

    /**
     * Creates duplicate of any DBObject
     *
     * @param obj to be copied from
     */
    public static String clone(Context context, DBObject obj) throws Exception {
        long l = System.currentTimeMillis();
        String sInterfaces = (String) obj.getInfo(SELECT_INTERFACE);//comma separated list of interfaces
        String sNewPID = create(context, obj.getBasicFieldName(SELECT_TYPE), obj.getBasicFieldName(SELECT_POLICY));
        Map attrMap = obj.getAttributesMap();

        //POST PROCESS
        if (!isNullOrEmpty(sInterfaces))
            MqlUtil.mqlCommand(context, "mod bus $1 add interface '$2'", sNewPID, sInterfaces);
        //mod attributes
        attrMap.put(ATTRIBUTE_V_VERSION_COMMENT, obj.getPhysicalid());//store id of copied obj
        DomainObject domObjNew = DomainObject.newInstance(context, sNewPID);
        domObjNew.setAttributeValues(context, attrMap);
        log.info(String.format("%s\t%s\t%s", "Util.clone", "total", System.currentTimeMillis() - l));

        return sNewPID;
    }

    /**
     * Creates Document and connect to parent object
     *
     * @param obj      matrix object for which Document is created
     * @param fileName name of the file
     * @param filePath file path on server
     */
    public static String attachDocument(Context context, DBObject obj, String fileName, String filePath) throws Exception {
        String sDocPLMID = createDocument(context, fileName, filePath);
        createDocComposeeUnderUA(context, obj, sDocPLMID);
        return MdIDBaseServices.getPhysicalIDfromPLMId(sDocPLMID);
    }

    private static void createDocComposeeUnderUA(Context context, DBObject obj, String sDocPLMID) throws Exception {
        boolean isFrozenKitFactory = TYPE_KIT_FACTORY.equals(obj.getBasicFieldName(SELECT_TYPE)) && STATE_FROZEN.equals(obj.getBasicFieldName(Constants.SELECT_CURRENT));
        if (isFrozenKitFactory)
            ContextUtil.pushContext(context);
        try {
            String sComposeePLMID = createDocComposee(context, obj.getPhysicalid());
            createSR(context, sComposeePLMID, new String[]{sDocPLMID}, SEMANTICS_REFERENCE, ROLE_DMT_DOCUMENT, DEFAULT_ID_REL);
        } finally {
            if (isFrozenKitFactory)
                ContextUtil.popContext(context);
        }
    }

    /**
     * Creates Document and checks given file to it
     *
     * @param fileName name of the file
     * @param filePath file path on server
     */
    public static String createDocument(Context context, String fileName, String filePath) throws Exception {
        String policyItemAlias = FrameworkUtil.getAliasForAdmin(context, "policy", POLICY_DOCUMENT_RELEASE, true);
        String typeItemAlias = FrameworkUtil.getAliasForAdmin(context, "type", TYPE_DOCUMENT, true);
        String sID = FrameworkUtil.autoName(context, typeItemAlias, null, policyItemAlias, VAULT_ESERVICE_PRODUCTION);
        Document document = new Document(sID);
        String sPID = document.getInfo(context, SELECT_PHYSICALID);

        String[] attrs = {sPID, ATTRIBUTE_TITLE, fileName};
        MqlUtil.mqlCommand(context, "mod bus $1 $2 $3", attrs);

        document.lock(context);
        document.checkinFile(context, true, false, context.getClientHost(), FORMAT_GENERIC, fileName, filePath);

        boolean isRelationship = false;
        return PLMConnectorServices.getPLMID(context, sPID, isRelationship);
    }

    /**
     * Connect Document to the Instruction
     *
     * @param obj BOP entity
     * @param doc Document object
     */
    private static void attachDocument(Context context, DBObject obj, DBObject doc) throws Exception {
        String sDocPLMID = PLMConnectorServices.getPLMID(context, doc.getPhysicalid(), false);
        createDocComposeeUnderUA(context, obj, sDocPLMID);
    }

    /**
     * Connect Documents to the Instruction
     *
     * @param obj  BOP entity
     * @param docs list of docs
     */
    public static void attachDocuments(Context context, DBObject obj, List<DBObject> docs) throws Exception {
        for (DBObject doc : docs)
            attachDocument(context, obj, doc);
    }

    /**
     * Connect Document to the Instruction
     *
     * @param pid BOP entity physicalid
     */
    public static String createDocComposee(Context context, String pid) throws Exception {
        String sComposeePLMID = createComposee(context, TYPE_PLM_DOC_CONNECTION, POLICY_VPLM_SMB_CX_DEFINITION, pid, RELATIONSHIP_PLM_CONNECTION);
        String sComposeePID = MdIDBaseServices.getPhysicalIDfromPLMId(sComposeePLMID);
        //post process
        //add interface
        MqlUtil.mqlCommand(context, "mod bus $1 add interface $2", sComposeePID, INTERFACE_PLM_CORE_STREAM_STORAGE);
        MqlUtil.mqlCommand(context, "mod bus $1 $2 $3", sComposeePID, ATTRIBUTE_STREAM, DEFAULT_STREAM_DOC);

        return sComposeePLMID;
    }

    /**
     * Sorts list of DBObjects by given attribute's value
     *
     * @param list     applied object list
     * @param attrName attribute's name
     */
    public static List<DBObject> sortByCnxTreeOrder(List<DBObject> list, String attrName) {
        Comparator<DBObject> c = (o1, o2) -> {
            double d1 = Double.parseDouble(o1.getAttributeValue(attrName));
            double d2 = Double.parseDouble(o2.getAttributeValue(attrName));
            return Double.compare(d1, d2);
        };
        return list.stream().sorted(c).collect(Collectors.toList());
    }

    /**
     * Sorts list of DBObjects by given select (key in 'info' map of DBObject class)
     *
     * @param list   applied object list
     * @param select mql select
     */
    public static List<DBObject> sortByTreeOrder(List<DBObject> list, String select) {
        Comparator<DBObject> c = (o1, o2) -> {
            double d1 = Double.parseDouble((String) o1.getInfo(select));
            double d2 = Double.parseDouble((String) o2.getInfo(select));
            return Double.compare(d1, d2);
        };
        return list.stream().sorted(c).collect(Collectors.toList());
    }

    /**
     * Sorts list of DBObjects by given select (key in 'info' map of DBObject class)
     *
     * @param list     applied object list
     * @param attrName mql select
     */
    public static List<DBObject> sortByAttribute(List<DBObject> list, String attrName) {
        Comparator<DBObject> c = (o1, o2) -> {
            String s1 = o1.getAttributeValue(attrName);
            String s2 = o2.getAttributeValue(attrName);
            return s1.compareTo(s2);
        };
        return list.stream().sorted(c).collect(Collectors.toList());
    }

    /**
     * Creates an instance (connect references)
     *
     * @param context  matrix db context
     * @param parentId id of parent object
     * @param childId  id of child object
     * @param relName  instance type (relationship name to connect)
     */
    public static String createInstance(Context context, String parentId, String childId, String relName) throws FrameworkException, CommonWebException {
        return createInstance(context, parentId, childId, relName, Double.toString(TreeOrderServices.createDefaultTreeOrderValue()));
    }

    /**
     * Creates an instance (connect references)
     *
     * @param context  matrix db context
     * @param parentId id of parent object
     * @param childId  id of child object
     * @param relName  instance type (relationship name to connect)
     */
    public static String createInstance(Context context, String parentId, String childId, String relName, String treeOrder) throws FrameworkException {
        long l = System.currentTimeMillis();
        String sInstPID = MqlUtil.mqlCommand(context, "add connection '$1' from '$2' to '$3' '$4' '$5' '$6' '$7' '$8' '$9' '$10' '$11' select $12 dump",
                relName, parentId, childId,
                ATTRIBUTE_V_TREE_ORDER, treeOrder,
                ATTRIBUTE_REL_V_SEC_LEVEL, DEFAULT_V_SEC_LEVEL,
                ATTRIBUTE_REL_V_DISCIPLINE, relName,
                ATTRIBUTE_REL_C_UPDATESTAMP, DEFAULT_C_UPDATESTAMP,
                SELECT_PHYSICALID);
        log.info(String.format("%s\t%s\t%s", "Util.createInstance", "add connection", System.currentTimeMillis() - l));
        return sInstPID;
    }

    /**
     * Creates an instance (connect references), set interfaces and attributes
     *
     * @param context  matrix db context
     * @param parentId id of parent object
     * @param childId  id of child object
     * @param relMap   contains detailed connection info (attributes and interfaces)
     */
    public static void connect(Context context, String parentId, Map relMap, String childId) throws FrameworkException {
        if (relMap != null && !relMap.isEmpty()) {
            String type = (String) relMap.get("type");
            String interfaces = (String) relMap.get("interface");//comma delimited
            Map<String, String> attributes = (Map<String, String>) relMap.get("attributes");

            String[] args = new String[]{parentId, type, childId};
            String relPID = MqlUtil.mqlCommand(context, "connect bus $1 rel $2 to $3 select physicalid dump", args);
            if (!isNullOrEmpty(interfaces))
                MqlUtil.mqlCommand(context, "mod connection $1 interface '$2'", relPID, interfaces);
            DomainRelationship dr = new DomainRelationship(relPID);
            dr.setAttributeValues(context, attributes);
        }
    }

    /***
     * Find all composee BO's which points to given pid through path object.
     * Based on NewTypingMigration.updateSematicRelations method (yes, has typo in method name)
     *
     * @param pids list of BO's or RO's physicalids
     * @param ownerType composee BO's type to filter
     */
    public static List<String> findSROwners(Context context, String pids, String ownerType) throws Exception {
        List<String> result = new ArrayList<>();

        StringList pathSelects = new StringList();
        pathSelects.add(SELECT_ID);
        pathSelects.add(SELECT_OWNER_PHYSICALID);
        pathSelects.add(SELECT_OWNER_TYPE);
        PathQuery pQuery = new PathQuery();
        pQuery.setPathType(SEMANTIC_RELATION);
        pQuery.setVaultPattern(VAULT_VPLM);
        PathQuery.QueryKind qKind = PathQuery.CONTAINS;
        pQuery.setCriterion(qKind, Arrays.asList(pids.split(DELIMITER_COMMA)));
        short sPageSize = 10;
        PathQueryIterator pqItr = pQuery.getIterator(context, pathSelects, sPageSize, new StringList());

        while (pqItr.hasNext()) {
            PathWithSelect pathInfo = pqItr.next();
            String sPathId = pathInfo.getSelectData(SELECT_ID);
            StringList slOwnerPIDs = pathInfo.getSelectDataList(SELECT_OWNER_PHYSICALID);
            StringList slOwnerTypes = pathInfo.getSelectDataList(SELECT_OWNER_TYPE);
            if (ownerType == null || slOwnerTypes.contains(ownerType))
                result.addAll(slOwnerPIDs);
        }
        pqItr.close();

        return result;
    }

    /***
     * Find all composee BO's which points to given pid through path object.
     * This method is alternative to 'findSROwners' but performs too long when DB has a lot of BO of 'ownerType' type
     *
     * @param pids list of BO's or RO's physicalids
     * @param ownerType composee BO's type to filter
     * @Deprecated
     */
    public static List<String> findSROwners_v2(Context context, String[] pids, String ownerType) throws Exception {
        List<String> result = new ArrayList<>();

        StringList objSelects = new StringList();
        objSelects.add(SELECT_PHYSICALID);
        String objWhere = Arrays.stream(pids)
                .map(pid -> String.format("(%s match '%s')", SELECT_PATHS_SR_EL_PHYSICALID, pid))
                .collect(Collectors.joining("&&"));
        MapList mlComposees = DomainObject.findObjects(context, ownerType, VAULT_VPLM, objWhere, objSelects);

        for (Map map : (List<Map>) mlComposees)
            result.add((String) map.get(SELECT_PHYSICALID));
        log.info(String.format("There are %s composee objects %s of type %s", result.size(), result, ownerType));

        return result;
    }

    public static void delete(Context context, String id, boolean isBUS) throws Exception {
        String[] ids = id.split(DELIMITER_COMMA);
        List<String> objPIDS = new ArrayList<>();
        List<String> relPIDS = new ArrayList<>();

        //POST process
        //id must be exactly physicalid to find referencing paths
        for (String pid : ids) {
            List<String> sOwners = Util.findSROwners(context, pid, null);
            objPIDS.addAll(sOwners);
            if (isBUS)
                objPIDS.add(pid);
            else
                relPIDS.add(pid);
        }

        if (!objPIDS.isEmpty())
            DomainObject.deleteObjects(context, objPIDS.toArray(new String[]{}));

        if (!relPIDS.isEmpty())
            DomainRelationship.disconnect(context, relPIDS.toArray(new String[]{}));
    }

    /**
     * Get connected to given BOP Entity active Routes
     *
     * @param obj      BOP Entity object id
     * @param objWhere filter
     * @param selects  Comma separated list with object selects
     */
    @SneakyThrows
    public static List<DBObject> getRoutes(Context context, DBObject obj, Selector selects, String objWhere) {
        StringList objSelects = selects.toStringList();
        StringList relSelects = new StringList();
        long l = System.currentTimeMillis();
        DomainObject givenObj = obj.getObject().isPresent() ? obj.getObject().get() : DomainObject.newInstance(context, obj.getNotEmptyId());
        List<Map> mapList = givenObj.getRelatedObjects(context, RELATIONSHIP_OBJECT_ROUTE, TYPE_ROUTE,
                objSelects, relSelects, false, true, (short) 1, objWhere, null, 0);
        List<DBObject> list = convertListMapToDbObject(mapList);
        log.info(String.format("%s\t%s\t%s", "Util.getRoutes", "getRelatedObjects", System.currentTimeMillis() - l));
        return list;
    }

    private static List<DBObject> convertListMapToDbObject(List<Map> mapList) {
        List<DBObject> list = new ArrayList();
        Iterator var3 = mapList.iterator();

        while (var3.hasNext()) {
            Map map = (Map) var3.next();
            DBObject object = DBObject.fromMap(map);
            list.add(object);
        }

        return list;
    }

    @SneakyThrows
    public static List<DBObject> getRouteContents(Context context, DBObject obj, Selector selects, String objWhere) {
        StringList objSelects = selects.toStringList();
        StringList relSelects = new StringList();
        long l = System.currentTimeMillis();
        DomainObject givenObj = obj.getObject().isPresent() ? obj.getObject().get() : DomainObject.newInstance(context, obj.getNotEmptyId());
        List<Map> mapList = givenObj.getRelatedObjects(context, RELATIONSHIP_OBJECT_ROUTE, "*",
                objSelects, relSelects, true, false, (short) 1, objWhere, null, 0);
        List<DBObject> list = convertListMapToDbObject(mapList);
        log.info(String.format("%s\t%s\t%s", "Util.getRoutes", "getRelatedObjects", System.currentTimeMillis() - l));
        return list;
    }

    public static StringList getDefaultObjSelects() {
        StringList sl = new StringList();
        sl.add(SELECT_PHYSICALID);
        sl.add(SELECT_TYPE);
        sl.add(SELECT_ATTRIBUTE_V_NAME);
        return sl;
    }

    public static StringList getDefaultRelSelects() {
        StringList sl = new StringList();
        sl.add(SELECT_ATTRIBUTE_V_TREE_ORDER);//for sorting
        sl.add(SELECT_FROM_PHYSICALID);//for parentId
        sl.add(SELECT_PHYSICALID);//to add instance id to the row client model
        return sl;
    }

    public static List<DBObject> extendDataPro(Context context, List<String> ids, String parentId, String relPattern, Selector selects) throws FrameworkException {
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        Util.appendByScopedSelectForBOP(objSelects);
        Util.appendByScopedSelectForMBOM(objSelects);

        long l = System.currentTimeMillis();
        List<DBObject> list = getInfo(context, ids.toArray(new String[]{}), objSelects);
        log.info(String.format("%s\t%s\t%s", "Util.extendDataPro", "getInfo", System.currentTimeMillis() - l));

        return Util.extendData(context, list, parentId, relPattern, selects);
    }

    /**
     * Extend data by parentId relId routes and doc info
     *
     * @param list       list of extending obj
     * @param relPattern rel pattern to fit key
     * @param selects    Comma separated list with object selects
     */
    public static List<DBObject> extendData(Context context, List<DBObject> list, String parentId, String relPattern, Selector selects) {
        String sInstanceIdKey = String.format("from[%s].%s", relPattern, SELECT_PHYSICALID);
        String sScopedKey = String.format("from[%s].to.paths[%s].path.element[0].physicalid", RELATIONSHIP_PLM_CONNECTION, SEMANTIC_RELATION);
        return list.stream()
                .peek(obj -> {
                    Map m = obj.getInfo();
                    Basics b = obj.getBasics();

                    List<String> pathList = appendByParentInstanceId(new ArrayList<>(), list, relPattern, m);
                    pathList.removeIf(Objects::isNull);
                    Collections.reverse(pathList);

                    List<String> copyPathList = new ArrayList<>(pathList);
                    String sInstanceId = (String) m.get(sInstanceIdKey);
                    copyPathList.removeIf(s -> s.equals(sInstanceId));
//                    String sParentId = (String) m.get(sParentIdKey);
                    String sParentId = parentId;
                    if (!copyPathList.isEmpty())
                        sParentId = copyPathList.get(copyPathList.size() - 1);

                    //extendByScoped
                    Set<String> scopedList = new HashSet<>();
                    List<String> scopedWithBell = normalizeToList(m.get(sScopedKey));
                    for (String obj1 : scopedWithBell) {
                        String[] scoped = obj1.split(DELIMITER_BELL);
                        scopedList.addAll(new HashSet<>(Arrays.asList(scoped)));
                    }
                    scopedList.removeIf(s -> s.equals(obj.getPhysicalid()));

                    m.put(PARAM_SCOPED, scopedList);
                    m.put(PARAM_PARENT_ID, sParentId);
                    m.put(PARAM_INSTANCE_ID, m.get(sInstanceIdKey));
                    m.put(SELECT_DOCS, getDocuments(context, obj, selects));
                    m.put(SELECT_PATH, pathList);

                    obj.setInfo(m);
                    obj.setBasics(b);
                })
                .collect(Collectors.toList());
    }

    /**
     * Extend data by instancePID
     *
     * @param list list of extending obj
     */
    public static List<DBObject> extendDataByInstancePID(List<DBObject> list, String instancePID) {
        return list.stream()
                .peek(obj -> {
                    Map m = obj.getInfo();
                    Basics b = obj.getBasics();
                    m.put(PARAM_INSTANCE_ID, instancePID);
                    obj.setInfo(m);
                    obj.setBasics(b);
                })
                .collect(Collectors.toList());
    }

    /**
     * Extend data by parent ID
     *
     * @param list list of extending obj
     */
    public static List<DBObject> extendDataByParentID(List<DBObject> list, String parentId) {
        return list.stream()
                .peek(obj -> {
                    Map m = obj.getInfo();
                    Basics b = obj.getBasics();
                    m.put(PARAM_PARENT_ID, parentId);
                    obj.setInfo(m);
                    obj.setBasics(b);
                })
                .collect(Collectors.toList());
    }

    /**
     * Collects instances path for current obj in the list
     *
     * @param pathList   list of instances PIDs from current to root
     * @param list       list of extending obj
     * @param relPattern rel pattern to fit key
     * @param m          info map of current object
     */
    public static List<String> appendByParentInstanceId(List<String> pathList, List<DBObject> list, String relPattern, Map m) {
        String sParentIdKey = String.format("from[%s].from.%s", relPattern, SELECT_PHYSICALID);
        String sInstanceIdKey = String.format("from[%s].%s", relPattern, SELECT_PHYSICALID);
        pathList.add((String) m.get(sInstanceIdKey));

        String sParentPID = (String) m.get(sParentIdKey);
        if (!isNullOrEmpty(sParentPID)) {
            List<DBObject> filtered = list.stream().filter(obj -> obj.getPhysicalid().equals(sParentPID)).collect(Collectors.toList());
            if (filtered.size() > 0) {
                Map parentMap = filtered.get(0).getInfo();
                pathList = appendByParentInstanceId(pathList, list, relPattern, parentMap);
            }
        }

        return pathList;
    }

    /**
     * Get all connected to given Header Operation (or Instruction) Document objects
     *
     * @param dbObject Header Operation (Instruction) object id
     */
    @SneakyThrows
    public static List<DBObject> getDocuments(Context context, DBObject dbObject, Selector selects) {
        Selector selector = new Selector.Builder()
                .withoutDefaults()
                .basic(SELECT_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_TYPE)
                .attribute(ATTRIBUTE_V_RESOURCES_QUANTITY)
                .attribute(ATTRIBUTE_V_CNX_TREE_ORDER)
                .build();
        StringList relSelects = new StringList();

        DomainObject givenObj = dbObject.getObject().isPresent() ? dbObject.getObject().get() : DomainObject.newInstance(context, dbObject.getNotEmptyId());
        List<Map> mapList = givenObj.getRelatedObjects(context, RELATIONSHIP_PLM_CONNECTION, TYPE_PLM_DOC_CONNECTION,
                selector.toStringList(), relSelects, false, true, (short) 1, null, null, 0);
        List<DBObject> composee = convertListMapToDbObject(mapList);

        List<DBObject> sortedList = Util.sortByCnxTreeOrder(composee, ATTRIBUTE_V_CNX_TREE_ORDER);

        List<String> sResIds = new ArrayList<>();//collect Resource id for further getting info
        Map<String, String> mIdToComposee = new HashMap<>();//serve to match Res pid against its composee pid (for edit/delete)
        for (DBObject obj : sortedList) {
            List<String> slPids = Util.normalizeToList(obj.getInfo(SELECT_PATHS_SR_EL_0_PHYSICALID));
            String sResPID = slPids.get(0);
            mIdToComposee.put(sResPID, (String) obj.getInfo(SELECT_PHYSICALID));
            sResIds.add(sResPID);
        }

        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        objSelects.add(SELECT_ATTRIBUTE_TITLE);
        objSelects.add(SELECT_DESCRIPTION);
        objSelects.add(SELECT_ICON_NAME);
        List<Map> results = DomainObject.getInfo(context, sResIds.toArray(new String[]{}), objSelects);
        List<DBObject> docs = results.stream().map(DBObject::fromMap).collect(Collectors.toList());

        List<DBObject> docWithoutNulls = docs.stream()
                .filter(Objects::nonNull)//todo documents can hold null object because of there are paths having non-existent bus as element =(
                .collect(Collectors.toList());

        docWithoutNulls.forEach(obj -> {
            Basics b = obj.getBasics();
            Map m = obj.getInfo();
            m.put(PARAM_PARENT_ID, dbObject.getPhysicalid());
            m.put(PARAM_COMPOSEE, mIdToComposee.get(obj.getPhysicalid()));
            obj.setInfo(m);
            obj.setBasics(b);
        });

        return docWithoutNulls;
    }

    /**
     * Get all root objects for given BOP Entity
     *
     * @param obj         given BOP Entity
     * @param relPattern  relationship pattern
     * @param typePattern type pattern
     */
    @SneakyThrows
    public static List<DBObject> getRoots(Context context, DBObject obj, String relPattern, String typePattern) {
        StringList objSelects = new StringList();
        objSelects.add(SELECT_PHYSICALID);
        objSelects.add(SELECT_TYPE);
        objSelects.add(SELECT_ATTRIBUTE_V_NAME);
        objSelects.add(String.format("to[%s]", relPattern));
        String mbomTypes = String.join(DELIMITER_COMMA, TYPE_KIT_MFG_PRODUCED_PART, TYPE_KIT_MFG_ASSEMBLY);
        objSelects.add(String.format("from[%s|to.paths.path.element.type matchlist '%s' '%s'].to.%s",
                RELATIONSHIP_PLM_CONNECTION, mbomTypes, DELIMITER_COMMA, SELECT_PATHS_SR_EL_PHYSICALID));//it need only for BOP structure
        StringList relSelects = new StringList();

        long l = System.currentTimeMillis();
        DomainObject givenObj = obj.getObject().isPresent() ? obj.getObject().get() : DomainObject.newInstance(context, obj.getNotEmptyId());
        List<Map> mapList = givenObj.getRelatedObjects(context, relPattern, typePattern,
                objSelects, relSelects, true, false, (short) 0, null, null, 0);
//        List<Map> filtered = mapList.stream()
//                .filter(m -> "FALSE".equalsIgnoreCase((String) m.get(String.format("to[%s]", relPattern))))
//                .collect(Collectors.toList());
        List<DBObject> list = convertListMapToDbObject(mapList);
        log.info(String.format("%s\t%s\t%s", "HeaderOperationDAO.getRootSystem", "getRelatedObjects", System.currentTimeMillis() - l));

        return list;
    }

    /**
     * Get all connected to given object Implemented Links
     *
     * @param sInstPIDList comma separated list of BOP Object instance id
     * @param composeeType type of composee object
     */
    @SneakyThrows
    public static List<DBObject> getImplementedLinks(Context context, String sInstPIDList, Selector selects, String composeeType) {
        List<DBObject> mbomInstList = new ArrayList<>();

        long l = System.currentTimeMillis();
        //find all paths ant its owners (filtered by type) referencing to given instance IDs
        List<String> ownerPIDs = findSROwners(context, sInstPIDList, composeeType);
        log.info(String.format("%s\t%s\t%s", "Util.getImplementedLinks", "findSROwners", System.currentTimeMillis() - l));

        //find all paths for owner ids
        StringList objSelects = new StringList();
        objSelects.add(SELECT_PATHS_SR_ID);
        l = System.currentTimeMillis();
        BusinessObjectWithSelectList bowsl = BusinessObject.getSelectBusinessObjectData(context, ownerPIDs.toArray(new String[]{}), objSelects, false);
        log.info(String.format("%s\t%s\t%s", "Util.getImplementedLinks", "getSelectBusinessObjectData", System.currentTimeMillis() - l));
        List<String> pathPIDs = new ArrayList<>();
        bowsl.forEach(bows -> {
            String[] sIDs = bows.getSelectData(SELECT_PATHS_SR_ID).split(DELIMITER_BELL);
            pathPIDs.addAll(Arrays.asList(sIDs));
        });

        if (!pathPIDs.isEmpty()) {
            Map<String, String> elemToOwnerMap = new HashMap<>();
            Map<String, String> instToRefMap = new HashMap<>();
            //find last ordered element for given path ids, for each such element (connection) get reference id
            //suppose that last element in path description is mbom instance connected as Implement Link
            Set<String> mbomPIDs = new HashSet<>();
            StringList pSelects = new StringList();
            pSelects.add(SELECT_ELEMENT_PHYSICALID);
            pSelects.add(SELECT_ELEMENT_KIND);
            pSelects.add(SELECT_OWNER_NAME);
            l = System.currentTimeMillis();
            PathWithSelectList pwsl = Path.getSelectPathData(context, pathPIDs.toArray(new String[]{}), pSelects, false);
            log.info(String.format("%s\t%s\t%s", "Util.getImplementedLinks", "getSelectPathData", System.currentTimeMillis() - l));
            l = System.currentTimeMillis();
            for (PathWithSelect pws : pwsl) {
                StringList slPIDs = pws.getSelectDataList(SELECT_ELEMENT_PHYSICALID);
                StringList slKinds = pws.getSelectDataList(SELECT_ELEMENT_KIND);
                StringList slOwnerPIDs = pws.getSelectDataList(SELECT_OWNER_NAME);
                String sLastPID = slPIDs.get(slPIDs.size() - 1);
                String sLastKind = slKinds.get(slPIDs.size() - 1);

                log.info(String.format("Element pids %s, kinds %s, owner name is %s", slPIDs, slKinds, slOwnerPIDs));

                //exclude 'self' path element ids
                if (!sInstPIDList.contains(sLastPID)) {
                    boolean mustBeAdded = false;
                    String sRefPID = sLastPID;

                    if (KIND_CONNECTION.equals(sLastKind)) {
                        try {
                            sRefPID = MqlUtil.mqlCommand(context, "print connection $1 select $2 dump", sLastPID, SELECT_TO_PHYSICALID);
                            mustBeAdded = true;
                        } catch (FrameworkException e) {
                            log.info(String.format("Connection with id %s is not alive.", sLastPID));
                        }
                    }

                    //in case of root mbom object then bus kind is used and
                    // only if MfgProductionPlanning composee object to exclude 'port' buses calculating candidates
                    if ((TYPE_MFG_PRODUCTION_PLANNING.equals(composeeType) || TYPE_PROCESS_IMPLEMENT_CNX.equals(composeeType)) && KIND_BUSINESSOBJECT.equals(sLastKind)) {
                        mustBeAdded = true;
                    }

                    if (mustBeAdded) {
                        mbomPIDs.add(sRefPID);
                        elemToOwnerMap.put(sLastPID, slOwnerPIDs.get(0));
                        instToRefMap.put(sLastPID, sRefPID);
                    }
                }
            }
            log.info(String.format("%s\t%s\t%s", "Util.getImplementedLinks", "for loop", System.currentTimeMillis() - l));

            //get info about found objects
            objSelects = Util.getDefaultObjSelects();
            objSelects.addAll(selects.toStringList());
            l = System.currentTimeMillis();
            List<Map> mbomRefList = DomainObject.getInfo(context, mbomPIDs.toArray(new String[]{}), objSelects);
            log.info(String.format("%s\t%s\t%s", "Util.getImplementedLinks", "DomainObject.getInfo", System.currentTimeMillis() - l));

            l = System.currentTimeMillis();
            mbomInstList = instToRefMap.entrySet().stream()
                    .map(e -> {
                        String instancePID = e.getKey();
                        String referencePID = e.getValue();
                        Map refMap = mbomRefList.stream().filter(m -> referencePID.equals(m.get(SELECT_PHYSICALID))).findFirst().get();
                        DBObject ref = DBObject.fromMap(refMap);

                        //put composee physicalid and instance physicalid into obj
                        Map m = ref.getInfo();
                        Basics b = ref.getBasics();
                        //it's WA, client component SelectableTable can't redefine id field
                        // so we have to make unique ids for instances on server side
                        //but physicalid of reference still stored in 'physicalid' field
                        ref.setId(instancePID);
                        m.put(PARAM_INSTANCE_ID, instancePID);
                        m.put(PARAM_COMPOSEE, elemToOwnerMap.get(instancePID));
                        ref.setInfo(m);
                        ref.setBasics(b);
                        return ref;
                    })
                    .collect(Collectors.toList());
        }
        log.info(String.format("%s\t%s\t%s", "Util.getImplementedLinks", "instToRefMap.entrySet().stream().map", System.currentTimeMillis() - l));

        return mbomInstList;
    }

    public static void appendByScopedSelect(StringList objSelects, String composeeType) {
        objSelects.add(String.format("from[%s|to.type==%s].to.paths[%s].path.element[0].physicalid", RELATIONSHIP_PLM_CONNECTION, composeeType, SEMANTIC_RELATION));
    }

    public static void appendByScopedSelectForBOP(StringList objSelects) {
        appendByScopedSelect(objSelects, TYPE_MFG_PRODUCTION_PLANNING);
    }

    public static void appendByScopedSelectForMBOM(StringList objSelects) {
        appendByScopedSelect(objSelects, TYPE_PROCESS_IMPLEMENT_CNX);
    }

    public static void copyLinksToDocs(Context context, DBObject clonedObj, DBObject newObj) throws Exception {
        Selector selects = new Selector.Builder().build();
        List<DBObject> docs = getDocuments(context, clonedObj, selects);
        List<DBObject> filtered = docs.stream().filter(o -> {
            String title = o.getAttributeValue(ATTRIBUTE_TITLE);
            String desc = o.getBasicFieldName(SELECT_DESCRIPTION);
            boolean b1 = title.toLowerCase().contains(IOT);
            boolean b2 = desc.toLowerCase().contains(IOT);
            log.warn(String.format("Title '%s' contains '%s'? %s", title, IOT, b1));
            log.warn(String.format("Description '%s' contains '%s'? %s", desc, IOT, b2));
            return b1 || b2;
        }).collect(Collectors.toList());
        attachDocuments(context, newObj, filtered);
    }

    public static boolean validateCloneOperation(Context context, String pid, String parentPID) throws FrameworkException {
        boolean isValid = false;

        String clonedPIDSelect = String.format("attribute[%s].value", ATTRIBUTE_V_VERSION_COMMENT);
        String clonedPID = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", pid, clonedPIDSelect);
        String clonedParentPID = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", parentPID, clonedPIDSelect);

        if (isNullOrEmpty(clonedPID)) {
            String title = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", pid, SELECT_ATTRIBUTE_V_NAME);
            log.warn(String.format("Cloned object '%s' has no V_versionComment, pid=%s", title, pid));
        }
        if (isNullOrEmpty(clonedParentPID)) {
            String title = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", parentPID, SELECT_ATTRIBUTE_V_NAME);
            log.warn(String.format("Parent object '%s' has no V_versionComment, pid=%s", title, parentPID));
        }

        if (!isNullOrEmpty(clonedPID) && !isNullOrEmpty(clonedParentPID)) {
            String childPIDSelect = String.format("from[%s].to.physicalid", RELATIONSHIP_PLM_INSTANCE);
            String clonedChildPID = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", clonedParentPID, childPIDSelect);
            if (!isNullOrEmpty(clonedChildPID)) {
                List<String> clonedChildPIDs = Arrays.asList(clonedChildPID.split(DELIMITER_COMMA));
                isValid = clonedChildPIDs.contains(clonedPID);
            } else
                log.warn(String.format("Twin (%s) for dropped object (%s) isn't contained in twins (%s) for children of given parent (%s)", clonedPID, pid, clonedChildPID, parentPID));
        }

        return isValid;
    }

    public static boolean checkUser(Context context, String user) {
        return user != null && user.equalsIgnoreCase(context.getUser());
    }
}
