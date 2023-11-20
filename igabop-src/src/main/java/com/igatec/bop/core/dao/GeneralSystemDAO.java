package com.igatec.bop.core.dao;

import com.dassault_systemes.platform.model.CommonWebException;
import com.dassault_systemes.platform.model.implement.TreeOrderServices;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igatec.bop.core.model.GeneralSystem;
import com.igatec.bop.core.model.HeaderOperation;
import com.igatec.bop.core.model.Instruction;
import com.igatec.bop.core.model.PPRContext;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.annotations.MatrixTransactional;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.common.dao.DBObjectDAO;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import matrix.db.Context;
import matrix.util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.*;
import static com.matrixone.apps.domain.DomainConstants.SELECT_LEVEL;

@Repository
@MatrixTransactional
@ObjectDAO("GeneralSystem")
@Slf4j
public class GeneralSystemDAO extends DBObjectDAO<GeneralSystem> {
    @Value("${ppr.id}")
    private String pprTemplateID;

    private HeaderOperationDAO headerOperationDAO;
    private PPRContextDAO pprContextDAO;
    private InstructionDAO instructionDAO;

    @Autowired
    public void setHeaderOperationDAO(@ObjectDAO("HeaderOperation") HeaderOperationDAO headerOperationDAO) {
        this.headerOperationDAO = headerOperationDAO;
    }

    @Autowired
    public void setPprContextDAO(@ObjectDAO("PPRContext") PPRContextDAO pprContextDAO) {
        this.pprContextDAO = pprContextDAO;
    }

    @Autowired
    public void setInstructionDAO(@ObjectDAO("Instruction") InstructionDAO instructionDAO) {
        this.instructionDAO = instructionDAO;
    }

    /**
     * Get all connected to given General System Operation objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    public List<DBObject> getOperations(GeneralSystem gsys, String parentId, Selector selects) throws Exception {
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        StringList relSelects = Util.getDefaultRelSelects();

        long l = System.currentTimeMillis();
        List<DBObject> list = getRelatedObjects(gsys, RELATIONSHIP_HEADER_OPERATION_INSTANCE, TYPE_HEADER_OPERATION_REFERENCE,
                objSelects, relSelects, false, (short) 1, null, null, 0);
        log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.getOperations", "getRelatedObjects", System.currentTimeMillis() - l));

        list = Util.extendData(getContext(), list, parentId, RELATIONSHIP_HEADER_OPERATION_INSTANCE, selects);

        String sTreeOrder = String.format("from[%s].attribute[%s]", RELATIONSHIP_HEADER_OPERATION_INSTANCE, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Get all connected to given General System and all its subsystem (in the tree) Operation objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> getAllOperations(GeneralSystem gsys, Selector selects) {
        String relPattern = String.join(DELIMITER_COMMA, RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
        String typePattern = String.join(DELIMITER_COMMA, TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE);
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        Util.appendByScopedSelectForBOP(objSelects);
        StringList relSelects = Util.getDefaultRelSelects();

        long l = System.currentTimeMillis();
        List<DBObject> list = getRelatedObjects(gsys, relPattern, typePattern,
                objSelects, relSelects, false, (short)0, null, null, 0);
        log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.getAllOperations", "getRelatedObjects", System.currentTimeMillis() - l));

        list = Util.extendData(getContext(), list, null, relPattern, selects);

        String sTreeOrder = String.format("from[%s].attribute[%s]", relPattern, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Set Kit_NumOperation attr value for every Operation in General System according to Tree Order attr value
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> setNumOperation(GeneralSystem gsys, Selector selects) {
        List<DBObject> list = getAllOperations(gsys, selects);

        String relPattern = String.join(DELIMITER_COMMA, RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
        String parentPIDSelect = String.format("from[%s].from.%s", relPattern, SELECT_PHYSICALID);
        String instancePIDSelect = String.format("from[%s].%s", relPattern, SELECT_PHYSICALID);

        Map<String, List<DBObject>> idToChildren = new HashMap<>();
        List<DBObject> firstLevelObjects = new ArrayList<>();
        for(DBObject obj : list) {
            String parentPID = (String) obj.getInfo(parentPIDSelect);

            //collect first level children
            String level = (String) obj.getInfo(SELECT_LEVEL);
            if("1".equals(level))
                firstLevelObjects.add(obj);

            //generate id to children map
            if(idToChildren.containsKey(parentPID)) {
                List<DBObject> children = idToChildren.get(parentPID);
                children.add(obj);
            } else {
                List<DBObject> children = new ArrayList<>();
                children.add(obj);
                idToChildren.put(parentPID, children);
            }
        }

        //firstLevelObjects must be already sorted previously
        List<DBObject> leafs = new ArrayList<>();
        sortChildren(leafs, firstLevelObjects, idToChildren);

        int number = 0;
        DecimalFormat df = new DecimalFormat("000");
        Map<String, String> productFlowMap = new HashMap<>();
        String sFromInstanceId = "";
        String sToInstanceId = "";

        Context context = getContext();
        try {
            ContextUtil.pushContext(context);
            for (DBObject op : leafs) {
                //increment for all states
                number += 5;

                //modify only not RELEASED
                if(!STATE_RELEASED.equals(op.getBasicFieldName(SELECT_CURRENT))) {
                    DomainObject domObj = DomainObject.newInstance(context, op.getPhysicalid());
                    domObj.setAttributeValue(context, ATTRIBUTE_KIT_NUM_OPERATION, df.format(number));

                    sToInstanceId = (String) op.getInfo(instancePIDSelect);
                    if (!"".equals(sFromInstanceId))
                        productFlowMap.put(sFromInstanceId, sToInstanceId);

                    sFromInstanceId = sToInstanceId;
                }
            }
        } finally {
            ContextUtil.popContext(context);
        }

        //delete product flow links into gsys structure
        //todo make modify process not delete and create composition
        deleteProductFlow(gsys);

        //create product flow link
        productFlowMap.forEach((key, value) -> createProductFlow(context, key, value));

        return getAllOperations(gsys, selects);
    }

    private void sortChildren(List<DBObject> leafs, List<DBObject> firstLevelObjects, Map<String, List<DBObject>> idToChildren) {
        for(DBObject obj : firstLevelObjects) {
            List<DBObject> children = idToChildren.get(obj.getPhysicalid());//all children list must be already sorted previously
            if(children != null)
                sortChildren(leafs, children, idToChildren);
            else
                leafs.add(obj);
        }
    }

    /**
     * Set Kit_NumOperation attr value for every Operation in General System according to Tree Order attr value
     *
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> setNumOperation(Selector selects) {
        PPRContext pprTemplate = new PPRContext(pprTemplateID);
        List<DBObject> topSystemsList = pprContextDAO.getSystems(pprTemplate, null, selects);

        //exclude all except for Kit_Factory
        topSystemsList = topSystemsList.stream()
                .filter(obj -> TYPE_KIT_FACTORY.equals(obj.getBasicFieldName(SELECT_TYPE)))
                .collect(Collectors.toList());

        List<DBObject> result = new ArrayList<>();
        topSystemsList.forEach(factory -> result.addAll(setNumOperation((GeneralSystem) factory, selects)));

        return result;
    }

    /**
     * Delete all product flow links into gsys structure
     *
     * @param gsys General System
     */
    @SneakyThrows
    private void deleteProductFlow(GeneralSystem gsys) {
        String relPattern = String.format("%s,%s", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_PLM_CONNECTION);
        String typePattern = String.format("%s,%s", TYPE_GENERAL_SYSTEM, TYPE_TIME_CONSTRAINT_CNX);
        StringList objSelects = Util.getDefaultObjSelects();
        StringList relSelects = new StringList();

        List<DBObject> composeeList = getRelatedObjects(gsys, relPattern, typePattern,
                objSelects, relSelects, false, (short) 0, null, null, 0);

        for (DBObject composee : composeeList) {
            if (TYPE_TIME_CONSTRAINT_CNX.equals(composee.getBasicFieldName(SELECT_TYPE)))
                Util.delete(getContext(), composee.getPhysicalid(), true);
        }
    }

    /**
     * Calculates closest common General System object's id for two Header Operations and
     * creates 'Product Flow' link between them
     * (copy of 'Create Product Flow' function in Planning Structure app)
     *
     * @param sFromPID Header Operation instance's (connection's!) id which is followed by sToPID
     * @param sToPID   Header Operation instance's (connection's!) id which follows for sFromPID
     */
    @SneakyThrows
    private void createProductFlow(Context context, String sFromPID, String sToPID) {
        String relPattern = String.format("%s,%s", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
        String typePattern = String.format("%s,%s", TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE);
        StringList objSelects = new StringList();
        objSelects.add(SELECT_PHYSICALID);
        StringList relSelects = new StringList();
        relSelects.add(SELECT_PHYSICALID);

        String sFromPIDRef = MqlUtil.mqlCommand(context, "print connection $1 select $2 dump", sFromPID, SELECT_TO_PHYSICALID);
        String sToPIDRef = MqlUtil.mqlCommand(context, "print connection $1 select $2 dump", sToPID, SELECT_TO_PHYSICALID);
        HeaderOperation fromOp = new HeaderOperation(sFromPIDRef);
        HeaderOperation toOp = new HeaderOperation(sToPIDRef);

        List<DBObject> fromList = getRelatedObjects(fromOp, relPattern, typePattern,
                objSelects, relSelects, true, (short) 0, null, null, 0);

        List<DBObject> toList = getRelatedObjects(toOp, relPattern, typePattern,
                objSelects, relSelects, true, (short) 0, null, null, 0);

        //calculate common closest parent
        List<String> fromIds = fromList.stream().map(DBObject::getPhysicalid).collect(Collectors.toList());
        List<String> toIds = toList.stream().map(DBObject::getPhysicalid).collect(Collectors.toList());
        String sParentPID = "";
        for (String pid : fromIds) {
            if (toIds.contains(pid)) {
                sParentPID = pid;
                break;
            }
        }

        //calculate instances PID chain to the closest parent
        List<String> fromInstPIDs = new ArrayList<>();
        int fromIndex = fromIds.indexOf(sParentPID);
        for (int i = 0; i < fromIndex + 1; i++)
            fromInstPIDs.add((String) fromList.get(i).getInfo(String.format("to[%s].%s", relPattern, SELECT_PHYSICALID)));

        List<String> toInstPIDs = new ArrayList<>();
        int toIndex = toIds.indexOf(sParentPID);
        for (int i = 0; i < toIndex + 1; i++)
            toInstPIDs.add((String) toList.get(i).getInfo(String.format("to[%s].%s", relPattern, SELECT_PHYSICALID)));

        //reverse chains as path elements arranged from root to leaf instances
        Collections.reverse(fromInstPIDs);
        Collections.reverse(toInstPIDs);

        //immediate creation
        if (!"".equals(sParentPID)) {
            String sComposeePID = Util.createProductFlow(context, sParentPID, fromInstPIDs.toArray(new String[]{}), toInstPIDs.toArray(new String[]{}));
            //todo make tests to know when we have to set any attrs value
            DomainObject composee = DomainObject.newInstance(context, sComposeePID);
            composee.setAttributeValue(context, ATTRIBUTE_V_MATERIAL_NEED, "TRUE");
        }
    }

    /**
     * Set V_TreeOrder attr value for every Operation instance according to direction
     *
     * @param instId    comma separated list with Header Operation instances ids
     * @param parentPID  pid of parent obj in the tree
     * @param parentId  id of parent obj in the tree
     * @param direction direction 'up' or 'down' in which Operation is moved
     * @param selects   Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> setTreeOrder_v2(String instId, String parentPID, String parentId, String direction, Selector selects) {
        Context context = getContext();

        GeneralSystem gsysParent = new GeneralSystem(parentPID);

        //get sorted by Tree Order children for given parent General System
        List<DBObject> children = getStructure(gsysParent, parentId, selects, (short) 1);
        List<String> sNewOrderedPIDs = children.stream()
                .map(child -> (String) child.getInfo().get(PARAM_INSTANCE_ID))
                .collect(Collectors.toList());

        int index = sNewOrderedPIDs.indexOf(instId);
        // There are three cases of index value for such array: "A", "B", "C"
        // if input id is first, inside or last
        if (index == 0 && "up".equals(direction) || index == sNewOrderedPIDs.size() - 1 && "down".equals(direction))
            return children;

        //slice id accordingly
        sNewOrderedPIDs.remove(instId);
        if ("up".equals(direction))
            sNewOrderedPIDs.add(index - 1, instId);
        else if ("down".equals(direction))
            sNewOrderedPIDs.add(index + 1, instId);

        for (String sNewOrderedPID : sNewOrderedPIDs)
            MqlUtil.mqlCommand(context, "mod connection $1 $2 $3", sNewOrderedPID, ATTRIBUTE_V_TREE_ORDER, Double.toString(TreeOrderServices.createDefaultTreeOrderValue()));

        //only for Instructions. Automatically update numbers
        String sParentType = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", parentPID, SELECT_TYPE);
        HeaderOperation opParent = new HeaderOperation(parentPID);
        if(TYPE_KIT_MAIN_OP.equals(sParentType))
            return headerOperationDAO.setTransitionNumbers(opParent, parentId, selects);

        return getStructure(gsysParent, parentId, selects, (short) 1);
    }

    /**
     * Get first level connected to given General System children General System objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    public List<DBObject> getSystems(GeneralSystem gsys, String parentId, Selector selects) throws Exception {
        return getSystems(gsys, parentId, selects, (short) 1);
    }

    /**
     * Get connected to given General System children General System objects
     *
     * @param gsys           General System object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     */
    public List<DBObject> getSystems(GeneralSystem gsys, String parentId, Selector selects, short recurseToLevel) throws Exception {
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        StringList relSelects = Util.getDefaultRelSelects();
        List<DBObject> list = getRelatedObjects(gsys, RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, TYPE_GENERAL_SYSTEM,
                objSelects, relSelects, false, recurseToLevel, null, null, 0);

        list = Util.extendData(getContext(), list, parentId, RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, selects);

        String sTreeOrder = String.format("from[%s].attribute[%s]", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Get given General System's structure including General System, Header Operation and Instruction objects
     *
     * @param gsys           General System object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     */
    @SneakyThrows
    public List<DBObject> getStructure(DBObject gsys, String parentId, Selector selects, short recurseToLevel) {
        String relPattern = String.join(DELIMITER_COMMA, RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE, RELATIONSHIP_INSTRUCTION_INSTANCE);
        String typePattern = String.join(DELIMITER_COMMA, TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE, TYPE_INSTRUCTION_REFERENCE);
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        Util.appendByScopedSelectForBOP(objSelects);
        StringList relSelects = Util.getDefaultRelSelects();
        List<DBObject> list = getRelatedObjects(gsys, relPattern, typePattern,
                objSelects, relSelects, false, recurseToLevel, null, null, 0);

        list = Util.extendData(getContext(), list, parentId, relPattern, selects);

        String sTreeOrder = String.format("from[%s].attribute[%s]", relPattern, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Get all connected to given General System children General System objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> getAllSystems(GeneralSystem gsys, String parentId, Selector selects) {
        return getSystems(gsys, parentId, selects, (short) 0);
    }

    /**
     * Get all connected to the given General System template General System objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> getCandidates(GeneralSystem gsys, Selector selects) {
        List<DBObject> candidates = getCandidates2(gsys, selects);//new version

        if(candidates.isEmpty()){//old version
            List<DBObject> selected = getInfo(new String[]{gsys.getPhysicalid()}, selects);
            String sSelectedType = gsys.getBasicFieldName(SELECT_TYPE);
            String sSelectedVName = selected.get(0).getAttributeValue(ATTRIBUTE_V_NAME);

            PPRContext pprTemplate = new PPRContext(pprTemplateID);// 1
            List<DBObject> topSystemsList = pprContextDAO.getSystems(pprTemplate, null, selects);// 2 or 3

            List<DBObject> allSystems = new ArrayList<>();
            topSystemsList.forEach(obj -> allSystems.addAll(getAllSystems((GeneralSystem) obj, null, selects)));// n
            allSystems.addAll(topSystemsList);

            DBObject tempSystem = allSystems.stream()
                    .filter(obj -> sSelectedType.equals(obj.getBasicFieldName(SELECT_TYPE)) &&
                            (TYPE_KIT_FACTORY.equals(sSelectedType) || sSelectedVName.equals(obj.getAttributeValue(ATTRIBUTE_V_NAME))))
                    .findFirst()
                    .orElse(null);

            //1 more request, total n + 5 or 6 requests(!) to DB
            if (tempSystem != null)
                if (TYPE_KIT_FACTORY.equals(sSelectedType) || TYPE_KIT_SHOP_FLOOR.equals(sSelectedType))
                    candidates = getSystems((GeneralSystem) tempSystem, null, selects, (short) 1);
                else if (TYPE_KIT_WORK_CELL.equals(sSelectedType))
                    candidates = getOperations((GeneralSystem) tempSystem, null, selects);
        }

        return candidates;
    }

    /**
     * Get all connected to the given General System template General System objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> getCandidates2(GeneralSystem gsys, Selector selects) {
        String sSelectedType = MqlUtil.mqlCommand(getContext(), "print bus $1 select $2 dump", gsys.getPhysicalid(), SELECT_TYPE);
        String sClonedPID = MqlUtil.mqlCommand(getContext(), "print bus $1 select $2 dump", gsys.getPhysicalid(), SELECT_ATTRIBUTE_V_VERSION_COMMENT);

        if(Util.isNullOrEmpty(sClonedPID))
            return new ArrayList<>();

        GeneralSystem tempSystem = new GeneralSystem(sClonedPID);
        List<DBObject> candidates = new ArrayList<>();
        if (TYPE_KIT_FACTORY.equals(sSelectedType) || TYPE_KIT_SHOP_FLOOR.equals(sSelectedType))
            candidates = getSystems(tempSystem, null, selects, (short) 1);
        else if (TYPE_KIT_WORK_CELL.equals(sSelectedType))
            candidates = getOperations(tempSystem, null, selects);

        return candidates;
    }

    /**
     * Creates Scope link between General System and given MBOM object
     * (copy of 'Create a Scope' function in Planning Structure app)
     *
     * @param gsys   GeneralSystem object
     * @param mbomId id of MBOM object
     */
    public List<DBObject> createScope(GeneralSystem gsys, String mbomId) throws Exception {
        String sComposeePLMID = Util.createComposee(getContext(), TYPE_MFG_PRODUCTION_PLANNING, POLICY_VPLM_SMB_CX_DEFINITION, gsys.getPhysicalid(), RELATIONSHIP_PLM_CONNECTION);

        Util.createSR(getContext(), sComposeePLMID, new String[]{gsys.getPhysicalid()}, SEMANTICS_REFERENCE_5, ROLE_PLM_IMPLEMENTLINK_SOURCE, DEFAULT_ID_REL);
        Util.createSR(getContext(), sComposeePLMID, new String[]{mbomId}, SEMANTICS_REFERENCE_3, ROLE_PLM_IMPLEMENTLINK_TARGET, DEFAULT_ID_REL);

        Selector selector = (new Selector.Builder()).build();
        return this.getInfo(new String[]{gsys.getPhysicalid()}, selector);
    }

    /**
     * Creates new Header Operation object (or get existing) and connect to given General System
     * (copy of 'Insert Header Operation' function in Planning Structure app)
     *
     * @param gsys        General System object
     * @param opId        id of Header Operation object
     * @param isDuplicate true if duplicate from given gsys need to be created
     * @param type        specified type of Header Operation
     */
    @SneakyThrows
    private List<DBObject> createHeaderOperation(GeneralSystem gsys, String opId, String isDuplicate, String type, Selector selects) {
        Context context = getContext();

        //if need to be created at first
        if (Util.isNullOrEmpty(opId))
            opId = Util.create(context, type, POLICY_VPLM_SMB_DEFINITION);
        else if ("true".equalsIgnoreCase(isDuplicate))
            opId = Util.cloneOperation(context, opId);

        //POST PROCESS
        //connect to General System
        String sInstPID = Util.createInstance(context, gsys.getPhysicalid(), opId, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
        //setNumOperations
        long l = System.currentTimeMillis();
        HeaderOperation op = new HeaderOperation(opId);
        op.setPhysicalid(opId);
        log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.createHeaderOperation", "new HeaderOperation", System.currentTimeMillis() - l));

        //create Instruction
        List<DBObject> instructions = headerOperationDAO.createInstruction(op, null, 1, selects);
        Util.extendDataByParentID(instructions, sInstPID);

        List<DBObject> list = getInfo(new String[]{opId}, selects);
        Util.extendDataByInstancePID(list, sInstPID);

        list.addAll(instructions);

        return list;
    }

    @SneakyThrows
    public List<DBObject> insertHeaderOperation(GeneralSystem gsys, String opIds, String isDuplicate, String type, Selector selects) {
        long l = System.currentTimeMillis();
        List<DBObject> list = new ArrayList<>();
                Arrays.stream(opIds.split(DELIMITER_COMMA))
                .forEach(id -> list.addAll(createHeaderOperation(gsys, id, isDuplicate, type, selects)));
        log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.insertHeaderOperation", "loop", System.currentTimeMillis() - l));
        return list;
    }

    /**
     * Creates new General System object (or get existing) and connect to given General System as subsystem
     * (copy of Insert System function in Planning Structure app)
     *
     * @param gsys        General System object
     * @param gsysId      id of General System to be connected
     * @param isDuplicate true if duplicate from given gsys need to be created
     * @param type        specified type of General System
     */
    @SneakyThrows
    private DBObject createGeneralSystem(DBObject gsys, String gsysId, String isDuplicate, String type, Selector selects) {
        Context context = getContext();

        if (Util.isNullOrEmpty(gsysId))
            gsysId = Util.create(context, type, POLICY_VPLM_SMB_DEFINITION);
        else if ("true".equalsIgnoreCase(isDuplicate))
            gsysId = Util.clone(context, gsysId, true);

        //POST PROCESS
        //connect to General System
        String sInstPID = Util.createInstance(context, gsys.getPhysicalid(), gsysId, RELATIONSHIP_GENERAL_SYSTEM_INSTANCE);

        List<DBObject> list = getInfo(new String[]{gsysId}, selects);
        Util.extendDataByInstancePID(list, sInstPID);

        return list.get(0);
    }

    @SneakyThrows
    public List<DBObject> insertGeneralSystem(DBObject ppr, String gsysIds, String isDuplicate, String type, Selector selects) {
        long l = System.currentTimeMillis();
        List<DBObject> list = Arrays.stream(gsysIds.split(DELIMITER_COMMA))
                .map(id -> createGeneralSystem(ppr, id, isDuplicate, type, selects))
                .collect(Collectors.toList());
        log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.insertGeneralSystem", "total", System.currentTimeMillis() - l));
        return list;
    }

    public List<DBObject> getInfo(String[] objectIds, Selector selector) throws FrameworkException {
        List<Map> results = DomainObject.getInfo(getContext(), objectIds, selector.toStringList());
        return results.stream().map(DBObject::fromMap).collect(Collectors.toList());
    }

    public void deleteObject(List<String> Ids) throws Exception {
        Util.delete(getContext(), String.join(DELIMITER_COMMA, Ids), true);
    }

    public void deleteRelationship(List<String> Ids) throws Exception {
        Util.delete(getContext(), String.join(DELIMITER_COMMA, Ids), false);
    }

    public List<DBObject> systemExtendDataPro(List<String> ids, Selector selects) throws FrameworkException {
        return Util.extendDataPro(getContext(), ids, null, null, selects);
    }


    /**
     * Promotes selected objects
     *
     * @param pBody BOP entity
     */
    @SneakyThrows
    public List<DBObject> promote(String pBody, Selector selects) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> bodyMap = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        List<String> states = (List<String>) bodyMap.get(PARAM_STATES);
        String lowestState = getLowestState(states);
        String sSign = PROMOTE_STATE_SIGN.get(lowestState);

        List<Map> selected = (List<Map>) bodyMap.get(PARAM_SELECTED);
        List<String> ids = selected.stream()
                .filter(map -> filterByState(map, lowestState))
                .map(map -> (String) map.get(PARAM_PHYSICALID))
                .collect(Collectors.toList());

        long l = 0L;
        for (String id : ids) {
            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "unsign bus $1 signature '$2' comment '$3'", id, "all", MSG_OK);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.promote", "unsign", System.currentTimeMillis() - l));

            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "approve bus $1 signature '$2' comment '$3'", id, sSign, MSG_OK);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.promote", "approve", System.currentTimeMillis() - l));

            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "promote bus $1", id);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.promote", "promote", System.currentTimeMillis() - l));
        }

        return Util.extendDataPro(getContext(), ids, null, null, selects);
    }

    private String getLowestState(List<String> states) {
        int min = STATES_ALL.indexOf(states.get(0)); //by default
        for (String state : states) {
            int i = STATES_ALL.indexOf(state);
            if(i < min)
                min = i;
        }
        return STATES_ALL.get(min);
    }

    private String getHighestState(List<String> states) {
        int max = STATES_ALL.indexOf(states.get(0)); //by default
        for (String state : states) {
            int i = STATES_ALL.indexOf(state);
            if(i > max)
                max = i;
        }
        return STATES_ALL.get(max);
    }

    private boolean filterByState(Map map, String state) {
        Map stateMap = (Map) map.get(PARAM_CURRENT);
        String stateName = (String) stateMap.get(PARAM_NAME);
        return state.equals(stateName);
    }

    /**
     * Demotes selected objects
     *
     * @param pBody BOP entity
     */
    @SneakyThrows
    public List<DBObject> demote(String pBody, Selector selects) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> bodyMap = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        List<String> states = (List<String>) bodyMap.get(PARAM_STATES);
        String highestState = getHighestState(states);
        String sSign = DEMOTE_STATE_SIGN.get(highestState);

        List<Map> selected = (List<Map>) bodyMap.get(PARAM_SELECTED);
        List<String> ids = selected.stream()
                .filter(map -> filterByState(map, highestState))
                .map(map -> (String) map.get(PARAM_PHYSICALID))
                .collect(Collectors.toList());

        long l = 0L;
        for (String id : ids) {
            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "unsign bus $1 signature '$2' comment '$3'", id, "all", MSG_OK);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.demote", "unsign", System.currentTimeMillis() - l));

            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "approve bus $1 signature '$2' comment '$3'", id, sSign, MSG_OK);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.demote", "approve", System.currentTimeMillis() - l));

            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "promote bus $1", id);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.demote", "promote", System.currentTimeMillis() - l));
        }

        return Util.extendDataPro(getContext(), ids, null, null, selects);
    }

    /**
     * Reserves selected objects
     *
     * @param pBody BOP entity
     */
    @SneakyThrows
    public List<DBObject> reserve(String pBody, Selector selects) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> bodyMap = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        List<Map> selected = (List<Map>) bodyMap.get(PARAM_SELECTED);
        List<String> ids = selected.stream()
                .map(map -> (String) map.get(PARAM_PHYSICALID))
                .collect(Collectors.toList());

        long l = 0L;
        for (String id : ids) {
            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "mod bus $1 reserve", id);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.reserve", "reserve", System.currentTimeMillis() - l));
        }

        return Util.extendDataPro(getContext(), ids, null, null, selects);
    }

    /**
     * Unreserves selected objects
     *
     * @param pBody BOP entity
     */
    @SneakyThrows
    public List<DBObject> unreserve(String pBody, Selector selects) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> bodyMap = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        List<Map> selected = (List<Map>) bodyMap.get(PARAM_SELECTED);
        List<String> ids = selected.stream()
                .map(map -> (String) map.get(PARAM_PHYSICALID))
                .collect(Collectors.toList());

        long l = 0L;
        for (String id : ids) {
            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "mod bus $1 unreserve", id);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.unreserve", "unreserve", System.currentTimeMillis() - l));
        }

        return Util.extendDataPro(getContext(), ids, null, null, selects);
    }

    /**
     * Prepare to approve reserved by context user objects
     *
     * @param pBody BOP entity
     */
    @SneakyThrows
    public List<DBObject> prepare(String pBody, Selector selects) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> bodyMap = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        String sContextUser = getContext().getUser();
        List<Map> selected = (List<Map>) bodyMap.get(PARAM_SELECTED);
        List<String> ids = selected.stream()
                .filter(map -> sContextUser.equals(map.get(PARAM_RESERVED_BY)))
                .map(map -> (String) map.get(PARAM_PHYSICALID))
                .collect(Collectors.toList());

        String sSign = PROMOTE_STATE_SIGN.get(STATE_IN_WORK);


        long l = 0L;
        for (String id : ids) {
            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "mod bus $1 unreserve", id);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.prepare", "unreserve", System.currentTimeMillis() - l));

            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "unsign bus $1 signature '$2' comment '$3'", id, "all", MSG_OK);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.prepare", "unsign", System.currentTimeMillis() - l));

            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "approve bus $1 signature '$2' comment '$3'", id, sSign, MSG_OK);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.prepare", "approve", System.currentTimeMillis() - l));

            l = System.currentTimeMillis();
            MqlUtil.mqlCommand(getContext(), "promote bus $1", id);
            log.info(String.format("%s\t%s\t%s", "GeneralSystemDAO.prepare", "promote", System.currentTimeMillis() - l));
        }

        return Util.extendDataPro(getContext(), ids, null, null, selects);
    }

    /**
     * Clone structure
     *
     * @param obj       dropped object
     * @param parentPID physicalid of parent object for target object (on which is dropped)
     * @param instId    instance physicalid of target object
     */
    @SneakyThrows
    public List<DBObject> cloneStructure(DBObject obj, String parentPID, String instId, Selector selects) {
        Context context = getContext();

        boolean isInstruction = obj instanceof Instruction;
        if(!Util.validateCloneOperation(context, obj.getPhysicalid(), parentPID) && !isInstruction)
            return new ArrayList<>();

        String newPID = cloneStructure(obj, selects);

        //connect to parent
        double order = getNextTreeOrder(parentPID, selects, instId);
        createInstance(context, parentPID, newPID, obj, Double.toString(order));

        GeneralSystem parentGsys = new GeneralSystem(parentPID);
        return getStructure(parentGsys, null, selects, (short)0);
    }

    private double getNextTreeOrder(String parentPID, Selector selects, String instId) throws CommonWebException {
        GeneralSystem parentGsys = new GeneralSystem(parentPID);
        String relPattern = String.join(DELIMITER_COMMA, RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE, RELATIONSHIP_INSTRUCTION_INSTANCE);
        String selectOrder = String.format("from[%s].attribute[%s]", relPattern, ATTRIBUTE_V_TREE_ORDER);
        String sTreeOrder1 = Double.toString(TreeOrderServices.createDefaultTreeOrderValue());
        String sTreeOrder2 = sTreeOrder1;

        if(!Util.isNullOrEmpty(instId)) {
            List<DBObject> children = getStructure(parentGsys, null, selects, (short)1);
            int index = 0;
            for (int i = 0; i < children.size(); i++) {
                DBObject child = children.get(i);
                String sChildInstPID = (String) child.getInfo(PARAM_INSTANCE_ID);
                if(instId.equals(sChildInstPID)) {
                    sTreeOrder1 = (String) child.getInfo(selectOrder);
                    index = i;
                    break;
                }
            }

            if(children.size() - 1 > index) {
                DBObject child2 = children.get(index + 1);
                sTreeOrder2 = (String) child2.getInfo(selectOrder);
            }
        }

        double d1 = Double.parseDouble(sTreeOrder1);
        double d2 = Double.parseDouble(sTreeOrder2);
        return ( d1 + d2 ) / 2;
    }

    /**
     * Clone structure process
     * @param obj dropped object
     */
    @SneakyThrows
    public String cloneStructure(DBObject obj, Selector selects) {
        Context context = getContext();

        String newPID = "";
        if(obj instanceof GeneralSystem) {
            newPID = Util.clone(context, obj.getPhysicalid(), false);

            List<DBObject> children = getStructure(obj, null, selects, (short)1);
            for (DBObject child : children) {
                String newChildPID = "";

                if(child instanceof GeneralSystem)
                    newChildPID = cloneStructure(child, selects);
                else if(child instanceof HeaderOperation)
                    newChildPID = headerOperationDAO.cloneStructure((HeaderOperation)child, selects);
                else if(child instanceof Instruction)
                    newChildPID = instructionDAO.cloneStructure(child);

                if(!Util.isNullOrEmpty(newChildPID))
                    createInstance(context, newPID, newChildPID, child);
            }
        }
        else if(obj instanceof HeaderOperation)
            newPID = headerOperationDAO.cloneStructure((HeaderOperation)obj, selects);
        else if(obj instanceof Instruction)
            newPID = instructionDAO.cloneStructure(obj);

        return newPID;
    }

    private void createInstance(Context context, String newPID, String newChildPID, DBObject obj) throws FrameworkException, CommonWebException {
        createInstance(context, newPID, newChildPID, obj, Double.toString(TreeOrderServices.createDefaultTreeOrderValue()));
    }

    private void createInstance(Context context, String newPID, String newChildPID, DBObject obj, String sTreeOrder) throws FrameworkException {
        String type = RELATIONSHIP_GENERAL_SYSTEM_INSTANCE; // by default

        if(obj instanceof GeneralSystem)
            type = RELATIONSHIP_GENERAL_SYSTEM_INSTANCE;
        else if(obj instanceof HeaderOperation)
            type = RELATIONSHIP_HEADER_OPERATION_INSTANCE;
        else if(obj instanceof Instruction)
            type = RELATIONSHIP_INSTRUCTION_INSTANCE;

        Util.createInstance(context, newPID, newChildPID, type, sTreeOrder);
    }


}
