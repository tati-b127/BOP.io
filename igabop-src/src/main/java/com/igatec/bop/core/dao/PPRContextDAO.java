package com.igatec.bop.core.dao;

import com.dassault_systemes.catrgn.connector.plm.services.PLMConnectorServices;
import com.dassault_systemes.platform.model.services.MdIDBaseServices;
import com.igatec.bop.core.model.Function;
import com.igatec.bop.core.model.GeneralSystem;
import com.igatec.bop.core.model.HeaderOperation;
import com.igatec.bop.core.model.PPRContext;
import com.igatec.bop.core.model.Product;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.annotations.MatrixTransactional;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.common.dao.DBObjectDAO;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.AccessUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import matrix.db.Access;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.ATTRIBUTE_STREAM;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_CNX_TREE_ORDER;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_NAME;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_TREE_ORDER;
import static com.igatec.bop.utils.Constants.DEFAULT_ID_REL;
import static com.igatec.bop.utils.Constants.DEFAULT_STREAM;
import static com.igatec.bop.utils.Constants.DELIMITER_COMMA;
import static com.igatec.bop.utils.Constants.INTERFACE_PLM_CORE_STREAM_STORAGE;
import static com.igatec.bop.utils.Constants.POLICY_VPLM_SMB_CX_DEFINITION;
import static com.igatec.bop.utils.Constants.POLICY_VPLM_SMB_DEFINITION;
import static com.igatec.bop.utils.Constants.POLICY_VPLM_SMB_PORT_DEFINITION;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_GENERAL_SYSTEM_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_HEADER_OPERATION_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_INSTRUCTION_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_PLM_CONNECTION;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_PLM_PORT;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_VPM_INSTANCE;
import static com.igatec.bop.utils.Constants.ROLE_PLM_PPRCONTEXTLINK_SYSTEM;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_KIT_NUM_OPERATION;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_VALUE;
import static com.igatec.bop.utils.Constants.SELECT_ATTRIBUTE_V_NAME;
import static com.igatec.bop.utils.Constants.SELECT_INTERFACE;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_EL_0_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_EL_0_TYPE;
import static com.igatec.bop.utils.Constants.SELECT_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_TYPE;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE_4;
import static com.igatec.bop.utils.Constants.TYPE_GENERAL_SYSTEM;
import static com.igatec.bop.utils.Constants.TYPE_HEADER_OPERATION_REFERENCE;
import static com.igatec.bop.utils.Constants.TYPE_INSTRUCTION_REFERENCE;
import static com.igatec.bop.utils.Constants.TYPE_KIT_FACTORY;
import static com.igatec.bop.utils.Constants.TYPE_KIT_MFG_ASSEMBLY;
import static com.igatec.bop.utils.Constants.TYPE_KIT_MFG_PRODUCED_PART;
import static com.igatec.bop.utils.Constants.TYPE_KIT_SHOP_FLOOR;
import static com.igatec.bop.utils.Constants.TYPE_KIT_WORK_CELL;
import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT;
import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT_PROCESS;
import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT_SYSTEM;
import static com.igatec.bop.utils.Constants.TYPE_PROD_SYSTEM_IO_PORT;
import static com.igatec.bop.utils.Constants.TYPE_VPM_REFERENCE;

@Repository
@ObjectDAO("PPRContext")
@Slf4j
public class PPRContextDAO extends DBObjectDAO<PPRContext> {
    @Value("${ppr.id}")
    private String pprTemplateID;

    private InstructionDAO instructionDAO;
    private FunctionDAO functionDAO;
    private ProductDAO productDAO;

    @Autowired
    public void setInstructionDAO(@ObjectDAO("Instruction") InstructionDAO instructionDAO) {
        this.instructionDAO = instructionDAO;
    }

    @Autowired
    public void setFunctionDAO(@ObjectDAO("Function") FunctionDAO functionDAO) {
        this.functionDAO = functionDAO;
    }

    @Autowired
    public void setProductDAO(@ObjectDAO("Product") ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<DBObject> contextExtendData(List<DBObject> list, Selector selector) {
        return Util.extendData(getContext(), list, null, null, selector);
    }

    /**
     * Get info about PPRContext objects
     *
     * @param where    filter MQL valid
     * @param selector list of selects
     */
    public List<DBObject> findObjects(String where, Selector selector) throws FrameworkException {
        return this.findObjects(TYPE_PPR_CONTEXT, "*", where, selector);
    }

    /**
     * Get all connected to given PPRContext General System objects
     *
     * @param ppr PPRContext object id
     */
    @SneakyThrows
    public List<DBObject> getSystems(PPRContext ppr, String parentId, Selector selects) {
        return getComposeeStructure(ppr, parentId, selects, TYPE_PPR_CONTEXT_SYSTEM, (short) 1);
    }

    @SneakyThrows
    public List<DBObject> getBOMOperations(PPRContext pprContext, String parentId, Selector selects, short recurseToLevel) {
        String sComposeeType = String.format("%s,%s", TYPE_PPR_CONTEXT_SYSTEM, TYPE_PPR_CONTEXT_PROCESS);

        Selector selector = new Selector.Builder()
                .withoutDefaults()
                .basic(SELECT_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_TYPE)
                .attribute(ATTRIBUTE_V_CNX_TREE_ORDER)
                .build();
        List<DBObject> composee = getRelatedObjects(pprContext, RELATIONSHIP_PLM_CONNECTION, sComposeeType,
                selector.toStringList(), null, false, (short) 1, null, null, 0);

        List<DBObject> sortedList = Util.sortByCnxTreeOrder(composee, ATTRIBUTE_V_CNX_TREE_ORDER);

        List<String> sSysIds = sortedList.stream()
                .map(obj -> (String) obj.getInfo(SELECT_PATHS_SR_EL_0_PHYSICALID))
                .collect(Collectors.toList());

        //todo systems can hold null object because of there are paths having non-existent bus as element =(
        sSysIds.removeIf(Objects::isNull);
        sSysIds.removeIf(this::filterSystems);

        List<DBObject> treeObject = Util.extendDataPro(getContext(), sSysIds, parentId, null, selects);

        List<DBObject> firstLevelSystems = treeObject.stream()
                .filter(obj -> TYPE_KIT_FACTORY.equals(obj.getBasicFieldName(SELECT_TYPE)))
                .collect(Collectors.toList());

        List<DBObject> results = new ArrayList<>();
        //expand to next levels if recurseToLevel != 1
        if (recurseToLevel != 1) {
            if (recurseToLevel > 0)
                recurseToLevel = (short) (recurseToLevel - 1);

            short finalRecurseToLevel = recurseToLevel;
            firstLevelSystems.forEach(obj -> results.addAll(getSystemStructure((GeneralSystem) obj, obj.getPhysicalid(), selects, finalRecurseToLevel)));
        }

        return results.stream().filter(obj -> obj instanceof HeaderOperation).collect(Collectors.toList());
    }


    /**
     * Get all connected to given PPRContext objects through composee object of predefined Type
     *
     * @param ppr           PPRContext object id
     * @param selects       Comma separated list with object selects
     * @param sComposeeType type name for expand criterion
     */
    @SneakyThrows
    public List<DBObject> getComposeeStructure(PPRContext ppr, String parentId, Selector selects, String sComposeeType, short recurseToLevel) {
        Selector selector = new Selector.Builder()
                .withoutDefaults()
                .basic(SELECT_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_TYPE)
                .attribute(ATTRIBUTE_V_CNX_TREE_ORDER)
                .build();
        List<DBObject> composee = getRelatedObjects(ppr, RELATIONSHIP_PLM_CONNECTION, sComposeeType,
                selector.toStringList(), null, false, (short) 1, null, null, 0);

        List<DBObject> sortedList = Util.sortByCnxTreeOrder(composee, ATTRIBUTE_V_CNX_TREE_ORDER);

        List<String> sSysIds = sortedList.stream()
                .map(obj -> (String) obj.getInfo(SELECT_PATHS_SR_EL_0_PHYSICALID))
                .collect(Collectors.toList());

        //todo systems can hold null object because of there are paths having non-existent bus as element =(
        sSysIds.removeIf(Objects::isNull);
        sSysIds.removeIf(this::filterSystems);

        List<DBObject> results = Util.extendDataPro(getContext(), sSysIds, parentId, null, selects);

        List<DBObject> firstLevelSystems = results.stream()
                .filter(obj -> TYPE_KIT_FACTORY.equals(obj.getBasicFieldName(SELECT_TYPE)))
                .collect(Collectors.toList());
        List<DBObject> firstLevelMBOM = results.stream()
                .filter(obj -> TYPE_KIT_MFG_PRODUCED_PART.equals(obj.getBasicFieldName(SELECT_TYPE)) ||
                        TYPE_KIT_MFG_ASSEMBLY.equals(obj.getBasicFieldName(SELECT_TYPE)))
                .collect(Collectors.toList());

        //expand to next levels if recurseToLevel != 1
        if (recurseToLevel != 1) {
            if (recurseToLevel > 0)
                recurseToLevel = (short) (recurseToLevel - 1);

            short finalRecurseToLevel = recurseToLevel;
            firstLevelMBOM.forEach(obj -> results.addAll(functionDAO.getStructure((Function) obj, obj.getPhysicalid(), selects, finalRecurseToLevel)));
            firstLevelSystems.forEach(obj -> results.addAll(getSystemStructure((GeneralSystem) obj, obj.getPhysicalid(), selects, finalRecurseToLevel)));
        }

        return results;
    }

    /**
     * Get all connected to given PPRContext objects through composee object of predefined Type
     *
     * @param ppr           PPRContext object id
     * @param selects       Comma separated list with object selects
     * @param sComposeeType type name for expand criterion
     */
    @SneakyThrows
    public List<DBObject> getComposeeStructureForOperations(PPRContext ppr, String parentId, Selector selects, String sComposeeType, short recurseToLevel) {
        Selector selector = new Selector.Builder()
                .withoutDefaults()
                .basic(SELECT_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_PHYSICALID)
                .basic(SELECT_PATHS_SR_EL_0_TYPE)
                .attribute(ATTRIBUTE_V_CNX_TREE_ORDER)
                .build();
        List<DBObject> composee = getRelatedObjects(ppr, RELATIONSHIP_PLM_CONNECTION, sComposeeType,
                selector.toStringList(), null, false, (short) 1, null, null, 0);
        List<DBObject> sortedList = Util.sortByCnxTreeOrder(composee, ATTRIBUTE_V_CNX_TREE_ORDER);
        List<String> sSysIds = sortedList.stream()
                .map(obj -> (String) obj.getInfo(SELECT_PATHS_SR_EL_0_PHYSICALID))
                .collect(Collectors.toList());

        //todo systems can hold null object because of there are paths having non-existent bus as element =(
        sSysIds.removeIf(Objects::isNull);
        sSysIds.removeIf(this::filterSystems);
        List<DBObject> results = Util.extendDataPro(getContext(), sSysIds, parentId, null, selects);
        List<DBObject> firstLevelMBOM = results.stream()
                .filter(obj -> TYPE_KIT_MFG_PRODUCED_PART.equals(obj.getBasicFieldName(SELECT_TYPE)) ||
                        TYPE_KIT_MFG_ASSEMBLY.equals(obj.getBasicFieldName(SELECT_TYPE)))
                .collect(Collectors.toList());
        firstLevelMBOM.forEach(obj -> results.addAll(functionDAO.getStructure((Function) obj, obj.getPhysicalid(), selects, (short) 0)));
        return results;
    }

    //todo remove it as it exact copy of systemDAO getStructure

    /**
     * Get given General System's structure including General System, Header Operation and Instruction objects
     *
     * @param gsys           General System object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     */
    @SneakyThrows
    public List<DBObject> getSystemStructure(GeneralSystem gsys, String parentId, Selector selects, short recurseToLevel) {
        String[] rels = new String[]{RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE, RELATIONSHIP_INSTRUCTION_INSTANCE};
        String[] types = new String[]{TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE, TYPE_INSTRUCTION_REFERENCE};
        String relPattern = String.join(DELIMITER_COMMA, rels);
        String typePattern = String.join(DELIMITER_COMMA, types);
        StringList relSelects = Util.getDefaultRelSelects();
        relSelects.add(SELECT_ATTRIBUTE_VALUE);
        relSelects.add(SELECT_INTERFACE);
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        Util.appendByScopedSelectForBOP(objSelects);
        List<DBObject> list = getRelatedObjects(gsys, relPattern, typePattern,
                objSelects, relSelects, false, recurseToLevel, null, null, 0);

        list = Util.extendData(getContext(), list, parentId, relPattern, selects);

        String sTreeOrder = String.format("from[%s].attribute[%s]", relPattern, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Get all connected to given PPRContext Physical Products
     *
     * @param ppr            PPRContext object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     */
    @SneakyThrows
    public List<DBObject> getEBOM(PPRContext ppr, String parentId, Selector selects, short recurseToLevel) {
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        StringList relSelects = Util.getDefaultRelSelects();

        //get only Kit_Product
        List<DBObject> list = getRelatedObjects(ppr, RELATIONSHIP_VPM_INSTANCE, TYPE_VPM_REFERENCE,
                objSelects, relSelects, false, recurseToLevel, null, null, 0);

        list = Util.extendData(getContext(), list, parentId, RELATIONSHIP_VPM_INSTANCE, selects);
        String sTreeOrder = String.format("from[%s].attribute[%s]", RELATIONSHIP_VPM_INSTANCE, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Get all Operations in the PPRContext structure
     *
     * @param ppr PPRContext object id
     */
    @SneakyThrows
    public List<DBObject> getOperations(PPRContext ppr, Selector selects) {
        List<DBObject> topSystemsList = getSystems(ppr, null, selects);//2
        List<DBObject> allOps = new ArrayList<>();
        topSystemsList.forEach(obj -> allOps.addAll(getAllOperations((GeneralSystem) obj, selects)));//n
        return allOps;
    }

    /**
     * Get all connected to given General System and all its subsystem (in the tree) Operation objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> getAllOperations(GeneralSystem gsys, Selector selects) {
        //todo need to be removed, copy from GeneralSystemDAO
        String relPattern = String.format("%s,%s", RELATIONSHIP_GENERAL_SYSTEM_INSTANCE, RELATIONSHIP_HEADER_OPERATION_INSTANCE);
        String typePattern = String.format("%s,%s", TYPE_GENERAL_SYSTEM, TYPE_HEADER_OPERATION_REFERENCE);
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        objSelects.add(SELECT_ATTRIBUTE_KIT_NUM_OPERATION);
        StringList relSelects = Util.getDefaultRelSelects();

        List<DBObject> list = getRelatedObjects(gsys, relPattern, typePattern,
                selects.toStringList(), relSelects, false, (short) 0, null, null, 0);

        List<String> excludeTypes = Arrays.asList(TYPE_KIT_FACTORY, TYPE_KIT_WORK_CELL, TYPE_KIT_SHOP_FLOOR);
        list.removeIf(obj -> excludeTypes.contains(obj.getBasicFieldName(SELECT_TYPE)));

        String sTreeOrder = String.format("from[%s].attribute[%s]", relPattern, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Creates new General System object
     *
     * @param type specified type of General System
     */
    private static String createGenSys(Context context, String type) throws Exception {
        String sGSPID = Util.create(context, type, POLICY_VPLM_SMB_DEFINITION);
        return PLMConnectorServices.getPLMID(context, sGSPID, false);
    }

    @SneakyThrows
    public boolean filterSystems(String id) {
        BusinessObject bo = new BusinessObject(id);
        Access access = bo.getAccessMask(getContext());
        return !AccessUtil.hasReadAccess(access);
    }

    /**
     * Get all connected to the given PPRContext template General System objects
     *
     * @param ppr PPRContext object id
     */
    @SneakyThrows
    public List<DBObject> getCandidates(PPRContext ppr, Selector selects) {
        PPRContext pprTemplate = new PPRContext(pprTemplateID);
        return getSystems(pprTemplate, null, selects);
    }

    /**
     * Creates new General System object (or get existing) and connect to given PPRContext
     * (copy of Insert System function in Planning Structure app)
     *
     * @param ppr         PPRContext object
     * @param gsysId      id of General System to be connected
     * @param isDuplicate true if duplicate from given gsys need to be created
     * @param type        specified type of General System
     */
    @SneakyThrows
    @MatrixTransactional
    public String createGeneralSystem(DBObject ppr, String gsysId, String isDuplicate, String type) {
        Context context = getContext();
        String sComposeePLMID = Util.createComposee(context, TYPE_PPR_CONTEXT_SYSTEM, POLICY_VPLM_SMB_CX_DEFINITION, ppr.getPhysicalid(), RELATIONSHIP_PLM_CONNECTION);
        String sComposeePID = MdIDBaseServices.getPhysicalIDfromPLMId(sComposeePLMID);

        //post process
        //add interface
        MqlUtil.mqlCommand(context, "mod bus $1 add interface $2", sComposeePID, INTERFACE_PLM_CORE_STREAM_STORAGE);
        MqlUtil.mqlCommand(context, "mod bus $1 $2 $3", sComposeePID, ATTRIBUTE_STREAM, DEFAULT_STREAM);

        //if need to be created at first
        if (Util.isNullOrEmpty(gsysId)) {
            String sGenSysPLMID = createGenSys(context, type);
            gsysId = MdIDBaseServices.getPhysicalIDfromPLMId(sGenSysPLMID);

            //don't know why but DELMIA also creates them
            String sPortPLMID1 = Util.createComposee(context, TYPE_PROD_SYSTEM_IO_PORT, POLICY_VPLM_SMB_PORT_DEFINITION, gsysId, RELATIONSHIP_PLM_PORT);
            String sPortPLMID2 = Util.createComposee(context, TYPE_PROD_SYSTEM_IO_PORT, POLICY_VPLM_SMB_PORT_DEFINITION, gsysId, RELATIONSHIP_PLM_PORT);
        } else if ("true".equalsIgnoreCase(isDuplicate)) {
            gsysId = Util.clone(context, gsysId, true);
            Selector selector = new Selector.Builder().build();
            List<DBObject> list = getSystems((PPRContext) ppr, null, selector);
            list = list.stream().filter(o -> TYPE_KIT_FACTORY.equals(o.getBasicFieldName(SELECT_TYPE))).collect(Collectors.toList());
            String pprVName = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", ppr.getPhysicalid(), SELECT_ATTRIBUTE_V_NAME);
            String vName = String.format("%s_%s", pprVName, list.size() + 1);
            MqlUtil.mqlCommand(context, "mod bus $1 $2 '$3'", gsysId, ATTRIBUTE_V_NAME, vName);
        }


        Util.createSR(context, sComposeePLMID, new String[]{gsysId}, SEMANTICS_REFERENCE_4, ROLE_PLM_PPRCONTEXTLINK_SYSTEM, DEFAULT_ID_REL);

        return gsysId;
    }

    @SneakyThrows
    @MatrixTransactional
    public List<DBObject> insertGeneralSystem(DBObject ppr, String gsysIds, String isDuplicate, String type, Selector selects) {
        List<String> pids = Arrays.stream(gsysIds.split(DELIMITER_COMMA))
                .map(id -> createGeneralSystem(ppr, id, isDuplicate, type))
                .collect(Collectors.toList());

        return this.getInfo(pids.toArray(new String[]{}), selects);
    }

    public List<DBObject> getInfo(String[] objectIds, Selector selector) throws FrameworkException {
        List<Map> results = DomainObject.getInfo(getContext(), objectIds, selector.toStringList());
        return results.stream().map(DBObject::fromMap).collect(Collectors.toList());
    }
}
