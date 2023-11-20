package com.igatec.bop.core.dao;

import com.igatec.bop.core.model.GeneralSystem;
import com.igatec.bop.core.model.HeaderOperation;
import com.igatec.bop.core.model.PPRContext;
import com.igatec.bop.core.model.Product;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.common.dao.DBObjectDAO;
import com.igatec.utilsspring.utils.common.db.Attribute;
import com.igatec.utilsspring.utils.common.db.Basics;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import matrix.db.Context;
import matrix.util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.ATTRIBUTE_KIT_NUM_OPERATION;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_KIT_TRANSITION_NUMBER;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_CNX_TREE_ORDER;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_NAME;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_RESOURCES_QUANTITY;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_TREE_ORDER;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_VERSION_COMMENT;
import static com.igatec.bop.utils.Constants.DEFAULT_ID_REL;
import static com.igatec.bop.utils.Constants.DELIMITER_COMMA;
import static com.igatec.bop.utils.Constants.INTERFACE_KIT_TRANSITION;
import static com.igatec.bop.utils.Constants.INTERFACE_KIT_WORKER;
import static com.igatec.bop.utils.Constants.PARAM_COMPOSEE;
import static com.igatec.bop.utils.Constants.PARAM_INSTANCE_ID;
import static com.igatec.bop.utils.Constants.POLICY_VPLM_SMB_CX_DEFINITION;
import static com.igatec.bop.utils.Constants.POLICY_VPLM_SMB_DEFINITION;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_GENERAL_SYSTEM_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_HEADER_OPERATION_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_INSTRUCTION_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_PLM_CONNECTION;
import static com.igatec.bop.utils.Constants.ROLE_DEL_PCU_OWNER;
import static com.igatec.bop.utils.Constants.ROLE_DEL_PCU_RESULT;
import static com.igatec.bop.utils.Constants.ROLE_PLM_IMPLEMENTLINK_SOURCE;
import static com.igatec.bop.utils.Constants.ROLE_PLM_IMPLEMENTLINK_TARGET;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_KIT_NUM_OPERATION;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_VALUE;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_V_RESOURCES_QUANTITY;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_V_TREE_ORDER;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_V_VERSION_COMMENT;
import static com.igatec.bop.utils.Constants.SELECT_INTERFACE;
import static com.igatec.bop.utils.Constants.SELECT_PATH;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_EL_0_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_EL_0_TYPE;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_EL_1_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_TO_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_TYPE;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE_3;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE_4;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE_5;
import static com.igatec.bop.utils.Constants.TYPE_ERGO_HUMAN;
import static com.igatec.bop.utils.Constants.TYPE_GENERAL_SYSTEM;
import static com.igatec.bop.utils.Constants.TYPE_HEADER_OPERATION_REFERENCE;
import static com.igatec.bop.utils.Constants.TYPE_IMPLEMENT_LINKS_DISCIPLINE;
import static com.igatec.bop.utils.Constants.TYPE_INSTRUCTION_REFERENCE;
import static com.igatec.bop.utils.Constants.TYPE_KIT_FACTORY;
import static com.igatec.bop.utils.Constants.TYPE_MFG_PRODUCTION_PLANNING;
import static com.igatec.bop.utils.Constants.TYPE_PROCESS_CAN_USE_CNX;
import static com.igatec.bop.utils.Constants.TYPE_VPM_REFERENCE;

@Repository
@ObjectDAO("HeaderOperation")
@Slf4j
public class HeaderOperationDAO extends DBObjectDAO<HeaderOperation> {
    @Value("${ppr.id}")
    private String pprTemplateID;

    private PPRContextDAO pprContextDAO;
    private FunctionDAO functionDAO;
    private InstructionDAO instructionDAO;

    @Autowired
    public void setPprContextDAO(@ObjectDAO("PPRContext") PPRContextDAO pprContextDAO) {
        this.pprContextDAO = pprContextDAO;
    }

    @Autowired
    public void setFunctionDAO(@ObjectDAO("Function") FunctionDAO functionDAO) {
        this.functionDAO = functionDAO;
    }

    @Autowired
    public void setInstructionDAO(@ObjectDAO("Instruction") InstructionDAO instructionDAO) {
        this.instructionDAO = instructionDAO;
    }

    /**
     * Get all connected to given Header Operation Work Instructions
     *
     * @param op      Header Operation object id
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> getInstructions(HeaderOperation op, String parentId, Selector selects) {
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        StringList relSelects = Util.getDefaultRelSelects();
        relSelects.add(SELECT_ATTRIBUTE_VALUE);
        relSelects.add(SELECT_INTERFACE);
        long l = System.currentTimeMillis();
        List<DBObject> list = getRelatedObjects(op, RELATIONSHIP_INSTRUCTION_INSTANCE, TYPE_INSTRUCTION_REFERENCE,
                objSelects, relSelects, false, (short) 1, null, null, 0);
        log.info(String.format("%s\t%s\t%s", "HeaderOperationDAO.getInstructions", "getRelatedObjects", System.currentTimeMillis() - l));

        list = Util.extendData(getContext(), list, parentId, RELATIONSHIP_INSTRUCTION_INSTANCE, selects);

        String sTreeOrder = String.format("from[%s].attribute[%s]", RELATIONSHIP_INSTRUCTION_INSTANCE, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Set Work Instructions transition numbers for given Header Operation in order to TreeOrder
     *
     * @param op       Header Operation object id
     * @param parentId client id of parent object
     * @param selects  Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> setTransitionNumbers(HeaderOperation op, String parentId, Selector selects) {
        List<DBObject> instructions = getInstructions(op, parentId, selects);

        Context context = getContext();
        for (int i = 0; i < instructions.size(); i++) {
            DBObject obj = instructions.get(i);
            MqlUtil.mqlCommand(context, "mod bus $1 $2 $3", obj.getPhysicalid(),
                    ATTRIBUTE_KIT_TRANSITION_NUMBER, Integer.toString(i + 1));
            Map<String, Attribute> attrMap = obj.getAttributes().getAttributes();
            Attribute attr = attrMap.get(ATTRIBUTE_KIT_TRANSITION_NUMBER);
            attr.setValue(Integer.toString(i + 1));
            attrMap.put(ATTRIBUTE_KIT_TRANSITION_NUMBER, attr);
        }

        return instructions;
    }

    /**
     * Get all connected to given Header Operation (or Instruction) Resource objects
     *
     * @param op Header Operation object id
     */
    public List<DBObject> getResources(DBObject op, Selector selects) throws Exception {
        Selector selector = new Selector.Builder()
                .withoutDefaults()
                .basic(SELECT_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_TYPE)
                .attribute(ATTRIBUTE_V_RESOURCES_QUANTITY)
                .attribute(ATTRIBUTE_V_CNX_TREE_ORDER)
                .build();
        StringList relSelects = new StringList();
        List<DBObject> composee = getRelatedObjects(op, RELATIONSHIP_PLM_CONNECTION, TYPE_PROCESS_CAN_USE_CNX,
                selector.toStringList(), relSelects, false, (short) 1, null, null, 0);

        List<DBObject> sortedList = Util.sortByCnxTreeOrder(composee, ATTRIBUTE_V_CNX_TREE_ORDER);

        List<String> sResIds = new ArrayList<>();//collect Resource id for further getting info
        Map<String, String> mIdToComposee = new HashMap<>();//serve to match Res pid against its composee pid (for edit/delete)
        Map<String, String> mIdToQty = new HashMap<>();//serve to match Res pid against its composee attribute quantity
        for (DBObject obj : sortedList) {
            List<String> slTypes = Util.normalizeToList(obj.getInfo(SELECT_PATHS_SR_EL_0_TYPE));
            List<String> slPids = Util.normalizeToList(obj.getInfo(SELECT_PATHS_SR_EL_0_PHYSICALID));
            List<String> slQuantities = Util.normalizeToList(obj.getInfo(SELECT_ATTRIBUTE_V_RESOURCES_QUANTITY));

            //remove self pointed path
            slTypes.removeIf(el -> el.equals(op.getBasicFieldName(SELECT_TYPE)));
            slPids.removeIf(el -> el.equals(op.getPhysicalid()));

            String sResPID = slPids.get(0);
            mIdToComposee.put(sResPID, (String) obj.getInfo(SELECT_PHYSICALID));
            mIdToQty.put(sResPID, slQuantities.get(0));
            sResIds.add(sResPID);
        }

        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        List<Map> results = DomainObject.getInfo(this.getContext(), sResIds.toArray(new String[]{}), objSelects);
        List<DBObject> resources = results.stream().map(DBObject::fromMap).collect(Collectors.toList());

        List<DBObject> resWithoutNulls = resources.stream()
                .filter(Objects::nonNull)//todo resources can hold null object because of there are paths having non-existent bus as element =(
                .collect(Collectors.toList());

        resWithoutNulls.forEach(obj -> {
            Map<String, String> attrs = new HashMap<>();
            attrs.put(ATTRIBUTE_V_RESOURCES_QUANTITY, mIdToQty.get(obj.getPhysicalid()));
            obj.getAttributes().addAttributesToMap(attrs);

            Basics b = obj.getBasics();
            Map info = obj.getInfo();
            info.put(PARAM_COMPOSEE, mIdToComposee.get(obj.getPhysicalid()));
            obj.setInfo(info);
            obj.setBasics(b);
        });

        return resWithoutNulls;
    }

    /**
     * Get all connected to given Header Operation (or Instruction) MBOM objects (Implement Links)
     *
     * @param op         Header Operation object id
     * @param instanceId Header Operation instance id
     */
    @SneakyThrows
    public List<DBObject> getMBOM(DBObject op, String instanceId, Selector selects) {
        return Util.getImplementedLinks(getContext(), instanceId, selects, TYPE_MFG_PRODUCTION_PLANNING);
    }

    /**
     * Get all connected to the given Header Operation template Resource objects
     *
     * @param op      Header Operation object id
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> getCandidates(DBObject op, Selector selects) {
        List<DBObject> list = getCandidates2(op, selects, 1);
        if (list.isEmpty()) //suppose to find old data
            list = getCandidates2(op, selects, 2);
        return list;
    }

    /**
     * Get all connected to the given Header Operation template Resource objects
     *
     * @param op      Header Operation object id
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    private List<DBObject> getCandidates2(DBObject op, Selector selects, int mode) {
        Context context = getContext();

        //calculate duplicated Operation
        String sTempOpInstPID = null;
        if (mode == 1) {//new version
            String clonedPID = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", op.getPhysicalid(), SELECT_ATTRIBUTE_V_VERSION_COMMENT);
            if (Util.isNullOrEmpty(clonedPID))
                return new ArrayList<>();
            sTempOpInstPID = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", clonedPID, SELECT_TO_PHYSICALID);//suppose only one instance id
        } else if (mode == 2) {//old version
            DBObject cloned = getClonedPID(op);
            String relPattern = String.format("%s,%s", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
            String sInstanceIdKey = String.format("from[%s].%s", relPattern, SELECT_PHYSICALID);
            sTempOpInstPID = (String) cloned.getInfo().get(sInstanceIdKey);
        }
        String finalSTempOpInstPID = sTempOpInstPID;

        //find all path owners pointing to instance pid
        List<String> pathOwners = Util.findSROwners(context, sTempOpInstPID, TYPE_IMPLEMENT_LINKS_DISCIPLINE);// 1 request to DB

        //collect all candidates pid
        List<String> sResIds = new ArrayList<>();
        for (String sDisciplinePID : pathOwners) {//m times
            DomainObject obj = DomainObject.newInstance(context, sDisciplinePID);
            List<String> sPortPIDs = Util.normalizeToList(obj.getInfoList(context, SELECT_PATHS_SR_EL_0_PHYSICALID));// 1 request to DB
            sPortPIDs.removeIf(s -> s.equals(finalSTempOpInstPID));
            String sPortPID = sPortPIDs.get(0);

            obj.setId(sPortPID);
            String sBehaviorPID = obj.getInfo(context, SELECT_PATHS_SR_EL_1_PHYSICALID);// 1 request to DB

            obj.setId(sBehaviorPID);
            String sInstPID = obj.getInfo(context, SELECT_PATHS_SR_EL_0_PHYSICALID);// 1 request to DB

            DomainRelationship dr = DomainRelationship.newInstance(context, sInstPID);
            Map m = dr.getRelationshipData(context, new StringList(SELECT_TO_PHYSICALID));// 1 request to DB
            StringList slResPIDs = (StringList) m.get(SELECT_TO_PHYSICALID);
            sResIds.add(slResPIDs.get(0));
        }

        //this case collect all resources as implemented links (in 'classic' way) for cloned operation
        List implLinks = Util.getImplementedLinks(context, sTempOpInstPID, selects, TYPE_IMPLEMENT_LINKS_DISCIPLINE);// k requests

        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        List<Map> data = DomainObject.getInfo(context, sResIds.toArray(new String[]{}), objSelects);// 1 request to DB
        List<DBObject> results = data.stream().map(DBObject::fromMap).collect(Collectors.toList());
        //totally this algorithm call n+6+4m+k requests to DB, n - number of top level systems, m - number of candidates, k - total in getImplementedLinks

        results.addAll(implLinks);

        //define already connected Resources to current Operation
        List<DBObject> connectedResources = getResources(op, selects);
        for (DBObject obj : results) {
            DBObject matchedRes = connectedResources.stream()
                    .filter(o -> obj.getPhysicalid().equals(o.getPhysicalid()))
                    .findFirst()
                    .orElse(null);

            String sQty = "";
            if (matchedRes != null) {
                sQty = matchedRes.getAttributeValue(ATTRIBUTE_V_RESOURCES_QUANTITY);

                Map m = obj.getInfo();
                m.put(PARAM_COMPOSEE, matchedRes.getInfo(PARAM_COMPOSEE));
                obj.setInfo(m);
            }

            Map<String, String> attrs = new HashMap<>();
            attrs.put(ATTRIBUTE_V_RESOURCES_QUANTITY, sQty);
            obj.getAttributes().addAttributesToMap(attrs);
        }

        return Util.sortByAttribute(results, ATTRIBUTE_V_NAME);
    }

    /**
     * Calculate pid of duplicated Operation
     *
     * @param op HeaderOperation object
     */
    @SneakyThrows
    private DBObject getClonedPID(DBObject op) {
        Context context = getContext();
        DomainObject doOp = op.getObject().get();
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.add(SELECT_ATTRIBUTE_V_VERSION_COMMENT);
        Map info = doOp.getInfo(context, objSelects);// 1 request to DB
        String sSelectedFrom = (String) info.get(SELECT_ATTRIBUTE_V_VERSION_COMMENT);//store pid of cloned object

        Selector selector = new Selector.Builder()
                .withoutDefaults()
                .basic(SELECT_PHYSICALID)
                .attribute(ATTRIBUTE_V_VERSION_COMMENT)
                .build();
        PPRContext pprTemplate = new PPRContext(pprTemplateID);
        List<DBObject> allOps = pprContextDAO.getOperations(pprTemplate, selector);// n+2 request to DB

        DBObject tempOp = allOps.stream()
                .filter(obj -> sSelectedFrom.equals(obj.getPhysicalid()))
                .findAny()
                .orElse(null);

        return Objects.requireNonNull(tempOp);
    }

    /**
     * Creates link between Header Operation and given Product object
     * (copy of 'Create link Operation-Capable Product' function in Planning Structure app)
     *
     * @param op    HeaderOperation object
     * @param resId id of Product object
     */
    public String createLinkToResource(DBObject op, String resId) throws Exception {
        Context context = getContext();
        String sComposeePLMID = Util.createComposee(context, TYPE_PROCESS_CAN_USE_CNX, POLICY_VPLM_SMB_CX_DEFINITION, op.getPhysicalid(), RELATIONSHIP_PLM_CONNECTION);

        Util.createSR(context, sComposeePLMID, new String[]{op.getPhysicalid()}, SEMANTICS_REFERENCE_3, ROLE_DEL_PCU_OWNER, "2");
        Util.createSR(context, sComposeePLMID, new String[]{resId}, SEMANTICS_REFERENCE_4, ROLE_DEL_PCU_RESULT, "1");

        return resId;
    }

    /**
     * Creates new Resource objects (or get existing) and connect to given Header Operation as capable resource
     * (copy of Create capable resource link function in Planning Structure app)
     *
     * @param op          Header Operation object
     * @param resIds      ids of Resource objects (used for 'add existing' function or/and to be copied from)
     * @param isDuplicate true if duplicate from given gsys need to be created
     * @param type        specified Interface of Resource to connect
     * @param selects     comma separated list with MQL object selects for response data
     */
    @SneakyThrows
    public List<DBObject> createResource(DBObject op, String resIds, String isDuplicate, String type, Selector selects) {
        //remove already added Resources
        List<DBObject> resources = getResources(op, selects);
        List<String> excludePIDs = resources.stream()
                .map(DBObject::getPhysicalid)
                .collect(Collectors.toList());

        Set<String> opPIDs = Arrays.stream(resIds.split(DELIMITER_COMMA))
                .filter(id -> !excludePIDs.contains(id))
                .map(id -> createRes(op, id, isDuplicate, type))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return Util.extendDataPro(getContext(), new ArrayList<>(opPIDs), null, null, selects);
//        return getCandidates(op, selects);
    }

    @SneakyThrows
    public String createRes(DBObject op, String resId, String isDuplicate, String type) {
        Context context = getContext();

        //default
        String typeName = TYPE_VPM_REFERENCE;
        if (INTERFACE_KIT_WORKER.equals(type))
            typeName = TYPE_ERGO_HUMAN;

        if (Util.isNullOrEmpty(resId)) {
            resId = Util.create(context, typeName, POLICY_VPLM_SMB_DEFINITION);
            //POST PROCESS
            //add interface
            MqlUtil.mqlCommand(context, "mod bus $1 add interface $2", resId, type);
        } else if ("true".equalsIgnoreCase(isDuplicate)) {
            //resId = Util.clone(context, clonedResId);
            List<DBObject> docs = attachCommonDocs(op, resId);
        }

        createLinkToResource(op, resId);

        return op.getPhysicalid();
    }

    /**
     * find all docs for resId
     * find cloned Operation for this Operation
     * find all docs for cloned Operation
     * calculate common docs
     * find all Instructions for this Operation
     * if exist get first Instruction and attach common docs
     * else create new one and attach common docs
     *
     * @param op    Header Operation object
     * @param resId id of Resource object
     */
    @SneakyThrows
    private List<DBObject> attachCommonDocs(DBObject op, String resId) {
        Selector selector = new Selector.Builder()
                .withoutDefaults()
                .basic(SELECT_PHYSICALID)
                .basic(SELECT_TYPE)
                .attribute(ATTRIBUTE_V_NAME)
                .build();

        //find all docs for resId
        Product resource = new Product(resId);
        List<DBObject> docsResource = instructionDAO.getDocuments(resource, selector);

        //find cloned Operation for this Operation
        String sClonedPID = getClonedPID(op).getPhysicalid();

        //find all docs for cloned Operation
        HeaderOperation clonedOp = new HeaderOperation(sClonedPID);
        List<DBObject> docsOperation = instructionDAO.getDocuments(clonedOp, selector);

        //calculate common docs
        List<DBObject> docs = getIntersection(docsResource, docsOperation);

        //find all Instructions for this Operation
        List<DBObject> opDocs = instructionDAO.getDocuments(op, selector);

        //check if existing Instruction already has any of common docs
        docs = getDifference(docs, opDocs);

        //if still docs isn't empty take first instr in sorted by TreeOrder list and attach common docs
        if (docs.size() > 0)
            instructionDAO.attachDocuments(op, docs);

        return docs;
    }

    private List<DBObject> getIntersection(List<DBObject> list1, List<DBObject> list2) {
        List<DBObject> list = new ArrayList<>();
        List<String> ids = list1.stream().map(DBObject::getPhysicalid).collect(Collectors.toList());
        for (DBObject obj : list2) {
            if (ids.contains(obj.getPhysicalid()))
                list.add(obj);
        }
        return list;
    }

    // list = list1 - list2
    private List<DBObject> getDifference(List<DBObject> list1, List<DBObject> list2) {
        List<DBObject> list = new ArrayList<>();
        List<String> ids = list2.stream().map(DBObject::getPhysicalid).collect(Collectors.toList());
        for (DBObject obj : list1) {
            if (!ids.contains(obj.getPhysicalid()))
                list.add(obj);
        }
        return list;
    }

    /**
     * Creates Instruction and connects to Header Operation
     * (copy of 'Create Text Instruction' function in Work Instructions app)
     *
     * @param op      Header Operation object
     * @param wiId    id of Instruction object (used for 'add existing' function)
     * @param number  number for Kit_TransitionNumber attr
     * @param selects comma separated list with MQL object selects for response data
     */
    public List<DBObject> createInstruction(HeaderOperation op, String wiId, int number, Selector selects) throws Exception {
        Context context = getContext();

        //if need to be created at first
        if (Util.isNullOrEmpty(wiId))
            wiId = Util.create(context, TYPE_INSTRUCTION_REFERENCE, POLICY_VPLM_SMB_DEFINITION);//2

        //POST PROCESS
        //connect to Operation
        String sInstPID = Util.createInstance(context, op.getPhysicalid(), wiId, RELATIONSHIP_INSTRUCTION_INSTANCE);//1
        //add custom interface, it's automatically added by property
        long l = System.currentTimeMillis();
        MqlUtil.mqlCommand(context, "mod bus $1 add interface $2", wiId, INTERFACE_KIT_TRANSITION);//1
        log.info(String.format("%s\t%s\t%s", "HeaderOperationDAO.createInstruction", "add interface", System.currentTimeMillis() - l));
        //mod attribute
        if (number == 0)
            number = getInstructions(op, null, selects).size();
        l = System.currentTimeMillis();
        MqlUtil.mqlCommand(context, "mod bus $1 '$2' '$3'", wiId, ATTRIBUTE_KIT_TRANSITION_NUMBER, Integer.toString(number));
        log.info(String.format("%s\t%s\t%s", "HeaderOperationDAO.createInstruction", "mod bus attr", System.currentTimeMillis() - l));

        l = System.currentTimeMillis();
        List<DBObject> list = getInfo(new String[]{wiId}, selects);
        log.info(String.format("%s\t%s\t%s", "HeaderOperationDAO.createInstruction", "return getInfo", System.currentTimeMillis() - l));

        Util.extendDataByInstancePID(list, sInstPID);

        return list;
    }

    public HeaderOperation getDbObject(@NotNull DomainObject object, @NotNull StringList selectsList) throws FrameworkException {
        if (!selectsList.contains("type")) {
            selectsList.add("type");
        }

        //todo must be replaced in com.igatec.utilsspring.utils.common.dao API
        //Map<String, Object> data = object.getInfo(this.getContext(), selectsList);
        Map<String, Object> data = Util.getInfo(this.getContext(), object, selectsList);
        HeaderOperation dbObject = HeaderOperation.fromMap(data);
        dbObject.setObject(object);
        return dbObject;
    }

    /**
     * Get root General System
     *
     * @param op      Header Operation object id
     * @param selects Comma separated list with object selects
     */
    public DBObject getRootSystem(HeaderOperation op, Selector selects) throws Exception {
        String relPattern = String.format("%s,%s", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
        String typePattern = String.format("%s,%s", TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE);
        StringList objSelects = selects.toStringList();
        objSelects.add(SELECT_TYPE);
        StringList relSelects = new StringList();
        relSelects.add(SELECT_ATTRIBUTE_V_TREE_ORDER);

        long l = System.currentTimeMillis();
        List<DBObject> list = getRelatedObjects(op, relPattern, typePattern,
                selects.toStringList(), relSelects, true, (short) 0, null, null, 0);
        log.info(String.format("%s\t%s\t%s", "HeaderOperationDAO.getRootSystem", "getRelatedObjects", System.currentTimeMillis() - l));

        return list.stream()
                .filter(obj -> TYPE_KIT_FACTORY.equals(obj.getBasicFieldName(SELECT_TYPE)))
                .findAny()
                .orElse(null);
    }

    /**
     * Get instances data from given Header Operation upto root General System
     *
     * @param op      Header Operation object id
     * @param selects Comma separated list with object selects
     */
    public List<DBObject> getInstancesFromRoot(HeaderOperation op, Selector selects) throws Exception {
        String relPattern = String.format("%s,%s", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
        String typePattern = String.format("%s,%s", TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE);
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        StringList relSelects = Util.getDefaultRelSelects();

        return getRelatedObjects(op, relPattern, typePattern,
                selects.toStringList(), relSelects, true, (short) 0, null, null, 0);
    }

    public List<DBObject> extendData(List<String> ids, Selector selector) throws FrameworkException {
        return Util.extendDataPro(getContext(), ids, null, null, selector);
    }

    /**
     * Creates Implement Link with MBOM
     *
     * @param op         Header Operation object id
     * @param bopRootPID id of BOP root
     * @param bopPath    full path instance ids of BOP
     * @param mbomPath   full path instance ids of dropped MBOM
     */
    @SneakyThrows
    public void createImplementLink(HeaderOperation op,
                                              String bopRootPID,
                                              String bopPath,
                                              String mbomPath,
                                              Selector selector) {
        Context context = getContext();

        //bopPath can be 'undefined' in case of bop structure creation without refreshing structure
        //it happens because creating bop structure we don't remember instance pid on the client side
        if ("undefined".equals(bopPath) || Util.isNullOrEmpty(bopPath)) {
            String relPattern = String.format("%s,%s", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
            String typePattern = String.format("%s,%s", TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE);
            List<DBObject> opRoots = Util.getRoots(context, op, relPattern, typePattern);
            List<DBObject> opFiltered = opRoots.stream()
                    .filter(sys -> "FALSE".equalsIgnoreCase((String) sys.getInfo(String.format("to[%s]", relPattern))))
                    .collect(Collectors.toList());
            GeneralSystem opRoot = (GeneralSystem) opFiltered.get(0);//suppose it always has 1 root object
            List<DBObject> ops = pprContextDAO.getSystemStructure(opRoot, null, selector, (short) 0);
            List<DBObject> filteredBOPs = ops.stream().filter(o -> op.getPhysicalid().equals(o.getPhysicalid())).collect(Collectors.toList());
            List<String> path = (List<String>) filteredBOPs.get(0).getInfo(SELECT_PATH);
            bopPath = String.join(DELIMITER_COMMA, path);
        }

        //validation
        String[] bopInstIds = bopPath.split(DELIMITER_COMMA);
        String bopInstId = bopInstIds[bopInstIds.length - 1];
        List<DBObject> mboms = getMBOM(op, bopInstId, selector);
        List<String> mbomInstPIDs = mboms.stream().map(o -> (String) o.getInfo(PARAM_INSTANCE_ID)).collect(Collectors.toList());
        String[] mbomInstIds = mbomPath.split(DELIMITER_COMMA);
        String mbomInstId = mbomInstIds[mbomInstIds.length - 1];

        if (!mbomInstPIDs.contains(mbomInstId)) {
            String sComposeePLMID = Util.createComposee(context, TYPE_MFG_PRODUCTION_PLANNING, POLICY_VPLM_SMB_CX_DEFINITION, bopRootPID, RELATIONSHIP_PLM_CONNECTION);
            Util.createSR(context, sComposeePLMID, bopPath.split(DELIMITER_COMMA), SEMANTICS_REFERENCE_5, ROLE_PLM_IMPLEMENTLINK_SOURCE, DEFAULT_ID_REL);
            Util.createSR(context, sComposeePLMID, mbomPath.split(DELIMITER_COMMA), SEMANTICS_REFERENCE_3, ROLE_PLM_IMPLEMENTLINK_TARGET, "2");
        } else {
            log.warn(String.format("MBOM (path=%s) already connected with BOP (path=%s) as implemented link", mbomPath, bopPath));
        }
    }

    public List<DBObject> getInfo(String[] objectIds, Selector selector) throws FrameworkException {
        List<Map> results = DomainObject.getInfo(this.getContext(), objectIds, selector.toStringList());
        return results.stream().map(DBObject::fromMap).collect(Collectors.toList());
    }

    /**
     * Deletes Header Operation and/or its Composee. In case of composee it means deleting link
     *
     * @param obj Header Operation object
     * @param ids comma separated ids to delete
     */
    @SneakyThrows
    public List<DBObject> delete(DBObject obj, @NotNull String ids, Selector selects) {
        Context context = getContext();
        Util.delete(context, ids, true);
        return new ArrayList<>();
    }

    /**
     * Get all connected to given General System and all its subsystem (in the tree) Operation objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    //todo need to be removed as it in GeneralSystemDAO class
    @SneakyThrows
    private List<DBObject> getAllOperations(GeneralSystem gsys, Selector selects) {
//        String relPattern = String.format("%s,%s", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
//        String typePattern = String.format("%s,%s", TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE);
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        objSelects.add(SELECT_ATTRIBUTE_KIT_NUM_OPERATION);
        StringList relSelects = Util.getDefaultRelSelects();

        List<DBObject> list = getRelatedObjects(gsys, RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, TYPE_GENERAL_SYSTEM,
                objSelects, relSelects, false, (short) 0, null, null, 0);

        //unfortunately we have to sort leaf systems by tree order and then for each sorted System get and sort operations
        //so we get n+1 requests
        String sTreeOrder = String.format("from[%s].attribute[%s]", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, ATTRIBUTE_V_TREE_ORDER);
        List<DBObject> opsAll = new ArrayList<>();
        for (DBObject obj : Util.sortByTreeOrder(list, sTreeOrder)) {
            List<DBObject> ops = getRelatedObjects(obj, RELATIONSHIP_HEADER_OPERATION_INSTANCE, TYPE_HEADER_OPERATION_REFERENCE,
                    objSelects, relSelects, false, (short) 0, null, null, 0);
            if (!ops.isEmpty()) {
                String sTreeOrderKey = String.format("from[%s].attribute[%s]", RELATIONSHIP_HEADER_OPERATION_INSTANCE, ATTRIBUTE_V_TREE_ORDER);
                opsAll.addAll(Util.sortByTreeOrder(ops, sTreeOrderKey));
            }
        }

        return opsAll;
    }

    /**
     * Set Kit_NumOperation attr value for every Operation in General System according to Tree Order attr value
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    //todo need to be removed as it in GeneralSystemDAO class
    @SneakyThrows
    public List<DBObject> setNumOperation(GeneralSystem gsys, Selector selects) {
        Context context = getContext();
        //todo make one request, postprocessing can be done on java side without additional requests to DB
        List<DBObject> ops = getAllOperations(gsys, selects);

        int number = 0;
        DecimalFormat df = new DecimalFormat("000");

        for (DBObject op : ops) {
            number += 5;
            DomainObject domObj = DomainObject.newInstance(context, op.getPhysicalid());
            domObj.setAttributeValue(context, ATTRIBUTE_KIT_NUM_OPERATION, df.format(number));
        }

        return getAllOperations(gsys, selects);
    }

    /**
     * Clone structure process
     */
    @SneakyThrows
    public String cloneStructure(HeaderOperation op, Selector selects) {
        Context context = getContext();

        String newPID = Util.clone(context, op.getPhysicalid(), false);

        //copy links to docs
        DBObject newObj = getInfo(new String[]{newPID}).get(0);
        Util.copyLinksToDocs(context, op, newObj);

        //copy links to resources
        List<DBObject> resources = getResources(op, selects);
        for (DBObject res : resources)
            createLinkToResource(newObj, res.getPhysicalid());

        List<DBObject> children = getInstructions(op, null, selects);
        for (DBObject child : children) {
            String newChildPID = instructionDAO.cloneStructure(child);
            Util.createInstance(context, newPID, newChildPID, RELATIONSHIP_INSTRUCTION_INSTANCE);
        }

        return newPID;
    }
}
