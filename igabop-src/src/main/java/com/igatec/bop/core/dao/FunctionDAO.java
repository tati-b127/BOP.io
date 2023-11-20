package com.igatec.bop.core.dao;

import com.dassault_systemes.platform.model.services.MdIDBaseServices;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igatec.bop.core.model.Function;
import com.igatec.bop.core.model.Product;
import com.igatec.bop.utils.Constants;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.annotations.MatrixTransactional;
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
import matrix.db.AttributeWithUnits;
import matrix.db.AttributeWithUnitsList;
import matrix.db.Context;
import matrix.db.Path;
import matrix.util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.igatec.bop.utils.Constants.ATTRIBUTE_APP_INDEX;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_KIT_RS_NAME;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_TYPES;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_IO_TYPE;
import static com.igatec.bop.utils.Constants.ATTRIBUTE_V_TREE_ORDER;
import static com.igatec.bop.utils.Constants.CALCULATION_METHOD_BY_TYPE;
import static com.igatec.bop.utils.Constants.DELIMITER_COMMA;
import static com.igatec.bop.utils.Constants.HIDE_RULES_MAP;
import static com.igatec.bop.utils.Constants.INTERFACE_DATA_REQUIREMENT_EXT;
import static com.igatec.bop.utils.Constants.MFG_BAR_ATTRIBUTES;
import static com.igatec.bop.utils.Constants.OEM_MATERIAL_TYPES_LST;
import static com.igatec.bop.utils.Constants.PARAM_PREDECESSORS;
import static com.igatec.bop.utils.Constants.PARAM_SCOPED;
import static com.igatec.bop.utils.Constants.POLICY_VPLM_SMB_CX_DEFINITION;
import static com.igatec.bop.utils.Constants.QUERY_WILDCARD;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_FUNCTION_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_IDENTIFIED_FUNCTION_INSTANCE;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_PLM_CONNECTION;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_PLM_PORT;
import static com.igatec.bop.utils.Constants.RELATIONSHIP_PROCESS_INSTANCE_CONTINUOUS;
import static com.igatec.bop.utils.Constants.ROLE_DELFMI_PREREQUISITECST_SOURCE;
import static com.igatec.bop.utils.Constants.ROLE_DELFMI_PREREQUISITECST_TARGET;
import static com.igatec.bop.utils.Constants.SELECT_FROM_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_PATHS_SR_EL_0_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_TO_PHYSICALID;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE_3;
import static com.igatec.bop.utils.Constants.SEMANTICS_REFERENCE_5;
import static com.igatec.bop.utils.Constants.TYPE_FUNCTION_REFERENCE;
import static com.igatec.bop.utils.Constants.TYPE_PROCESS_IMPLEMENT_CNX;
import static com.igatec.bop.utils.Constants.TYPE_PROCESS_PREREQUISITE_CNX_CUST;


@Repository
@MatrixTransactional
@ObjectDAO("Function")
@Slf4j
public class FunctionDAO extends DBObjectDAO<Function> {
    private InstructionDAO instructionDAO;

    @Autowired
    public void setInstructionDAO(@ObjectDAO("Instruction") InstructionDAO instructionDAO) {
        this.instructionDAO = instructionDAO;
    }

    /**
     * Get info about Function objects
     *
     * @param where    filter MQL valid
     * @param selector list of selects
     */
    public List<DBObject> findObjects(String where, Selector selector) throws FrameworkException {
        return this.findObjects(TYPE_FUNCTION_REFERENCE, "*", where, selector);
    }

    /**
     * Get info about Function
     *
     * @param func        Function object
     * @param instancePID instance pid to given MBOM object in the structure
     */
    @SneakyThrows
    public Function get(Function func, String instancePID) {
        Context context = getContext();

        long l = System.currentTimeMillis();
        Map<String, Object> objMap = extendAttributesInfo(context, func.getPhysicalid());
        //todo remove all loggers after successful fix
        log.info(String.format("%s\t%s\t%s", "FunctionDAO.get", "extendAttributesInfo", System.currentTimeMillis() - l));

        l = System.currentTimeMillis();
        appendByInstanceAttrs(context, instancePID, objMap);
        log.info(String.format("%s\t%s\t%s", "FunctionDAO.get", "appendByInstanceAttrs", System.currentTimeMillis() - l));

        Function funcFromDB = Function.fromMap(objMap);

        l = System.currentTimeMillis();
        List<DBObject> scopedList = getScoped(instancePID);
        log.info(String.format("%s\t%s\t%s", "FunctionDAO.get", "getScoped", System.currentTimeMillis() - l));

        l = System.currentTimeMillis();
        List<DBObject> preList = getPredecessors(func, instancePID);
        log.info(String.format("%s\t%s\t%s", "FunctionDAO.get", "getPredecessors", System.currentTimeMillis() - l));

        Map m = funcFromDB.getInfo();
        Basics b = funcFromDB.getBasics();
        m.put(PARAM_PREDECESSORS, preList);
        m.put(PARAM_SCOPED, scopedList);
        funcFromDB.setInfo(m);
        funcFromDB.setBasics(b);

        return funcFromDB;
    }

    private void appendByInstanceAttrs(Context context, String instancePID, Map<String, Object> objMap) throws Exception {
        Map<String, Object> refAttrs = (Map<String, Object>) objMap.get("attributes");
        DomainRelationship domRel = DomainRelationship.newInstance(context, instancePID);

        Map<String, Attribute> attrMap = new HashMap<>();
        AttributeWithUnitsList instAttrs = domRel.getAttributesWithUnits(context);
        for (AttributeWithUnits attrUOM : instAttrs) {
            Map<String, Object> data = new HashMap<>();
            Map<String, String> info = new HashMap<>();
            info.put("type", ATTRIBUTE_TYPES.get(attrUOM.getType()));
            info.put("inputvalue", attrUOM.getInputvalue());
            info.put("inputunit", attrUOM.getInputunit());
            data.put("name", attrUOM.getName());
            data.put("value", attrUOM.getValue());
            data.put("info", info);
            Attribute attr = Attribute.fromMap(data);
            attrMap.put(attrUOM.getName(), attr);
        }

        Map attrs = Stream.concat(refAttrs.entrySet().stream(), attrMap.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        objMap.put("attributes", attrs);
    }

    private Map<String, Object> extendAttributesInfo(Context context, String pid) throws FrameworkException {
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.add("attribute.value");
        objSelects.add("attribute.type");
        objSelects.add("attribute.inputvalue");
        objSelects.add("attribute.inputunit");

        DomainObject domObj = DomainObject.newInstance(context, pid);
        Map<String, String> map = domObj.getInfo(context, objSelects);
        Map<String, Object> objMap = new HashMap<>();
        Map<String, Attribute> attrMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (key.startsWith("attribute[") && key.contains("].value")) {
                String attrName = key.substring(10, key.indexOf("].value"));
                Map<String, Object> data = new HashMap<>();
                Map<String, String> info = new HashMap<>();
                info.put("type", map.get("attribute[" + attrName + "].type"));
                info.put("inputvalue", map.get("attribute[" + attrName + "].inputvalue"));
                info.put("inputunit", map.get("attribute[" + attrName + "].inputunit"));
                data.put("name", attrName);
                data.put("value", val);
                data.put("info", info);
                Attribute attr = Attribute.fromMap(data);
                attrMap.put(attrName, attr);
            } else if (!key.startsWith("attribute["))
                objMap.put(key, val);
        }
        objMap.put("attributes", attrMap);

        return objMap;
    }

    /**
     * Modify MBOM obj
     *
     * @param func MBOM object id
     * @param instancePID instance pid to given MBOM object in the structure
     */
    public DBObject modify(Function func, String pBody, String instancePID) throws Exception {
        Context context = getContext();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> attrMap = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        DomainObject domObj = DomainObject.newInstance(context, func.getPhysicalid());
        domObj.setAttributeValues(context, attrMap);

        return get(func, instancePID);
    }

    /**
     * Modify MBOM instance obj
     *
     * @param func MBOM object id
     * @param instancePID instance pid to given MBOM object in the structure
     */
    public DBObject modifyInstance(Function func, String pBody, String instancePID) throws Exception {
        Context context = getContext();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> attrMap = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        DomainRelationship domRel = DomainRelationship.newInstance(context, instancePID);
        domRel.setAttributeValues(context, attrMap);

        DomainObject domObj = DomainObject.newInstance(context, func.getPhysicalid());
        domObj.setAttributeValues(context, attrMap);

        return get(func, instancePID);
    }

    /**
     * Get all connected to given Function Functions objects
     *
     * @param func           Function object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     */
    @SneakyThrows
    public List<DBObject> getStructure(Function func, String parentId, Selector selects, short recurseToLevel) {
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        Util.appendByScopedSelectForMBOM(objSelects);
        StringList relSelects = Util.getDefaultRelSelects();

        List<DBObject> list = getRelatedObjects(func, RELATIONSHIP_FUNCTION_INSTANCE, QUERY_WILDCARD,
                objSelects, relSelects, false, recurseToLevel, null, null, 0);

        Util.extendData(getContext(), list, parentId, RELATIONSHIP_FUNCTION_INSTANCE, selects);

        String sTreeOrder = String.format("from[%s].attribute[%s]", RELATIONSHIP_FUNCTION_INSTANCE, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }

    /**
     * Insert Predecessor for MBOM object
     * (copy of 'Insert Predecessor' function in Manufactured Item Definition app)
     *
     * @param func           MBOM object
     * @param instancePID    instance pid to given MBOM object in the structure
     * @param parentId       id of parent MBOM for given MBOM object in the structure (used to correctly prepare response)
     * @param predecessorPID pid of MBOM object which stands for Material
     */
    @SneakyThrows
    public List<DBObject> insertPredecessor(Function func, String instancePID, String parentId, String predecessorPID, Selector selects) {
        Context context = getContext();

        List<DBObject> preList = getPredecessors(func, instancePID);
        if (!preList.isEmpty())
            return new ArrayList<>();

        //calculate parent MBOM PID
        String parentPID = MqlUtil.mqlCommand(context, "print connection $1 select $2 dump", instancePID, SELECT_FROM_PHYSICALID);

        //create instance to given Material MBOM
        String preInstancePID = Util.createInstance(context, parentPID, predecessorPID, RELATIONSHIP_IDENTIFIED_FUNCTION_INSTANCE);

        //get ports
        String portSelect = String.format("from[%s|to.attribute[%s].value==1].to.physicalid", RELATIONSHIP_PLM_PORT, ATTRIBUTE_V_IO_TYPE);
        String prePortSelect = String.format("from[%s|to.attribute[%s].value==2].to.physicalid", RELATIONSHIP_PLM_PORT, ATTRIBUTE_V_IO_TYPE);
        String portPID = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", func.getPhysicalid(), portSelect);
        String prePortPID = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", predecessorPID, prePortSelect);

        //create composee
        String composeePLMID = Util.createComposee(getContext(), TYPE_PROCESS_PREREQUISITE_CNX_CUST, POLICY_VPLM_SMB_CX_DEFINITION, parentPID, RELATIONSHIP_PLM_CONNECTION);
        String composeePID = MdIDBaseServices.getPhysicalIDfromPLMId(composeePLMID);
        MqlUtil.mqlCommand(context, "mod bus $1 add interface $2", composeePID, INTERFACE_DATA_REQUIREMENT_EXT);

        //create paths
        Path path = Util.createSR(context, composeePLMID, new String[]{instancePID, portPID}, SEMANTICS_REFERENCE_5, ROLE_DELFMI_PREREQUISITECST_SOURCE, "1");
        Path prePath = Util.createSR(context, composeePLMID, new String[]{preInstancePID, prePortPID}, SEMANTICS_REFERENCE_3, ROLE_DELFMI_PREREQUISITECST_TARGET, "2");

        //paths creation post process
        MqlUtil.mqlCommand(context, "mod path $1 $2 $3", path.getName(), ATTRIBUTE_APP_INDEX, "1");
        MqlUtil.mqlCommand(context, "mod path $1 $2 $3", prePath.getName(), ATTRIBUTE_APP_INDEX, "1");

        //get info
        List<DBObject> list = getInfo(new String[]{predecessorPID},
                new Selector.Builder()
                        .selector(selects)
                        .attribute(ATTRIBUTE_KIT_RS_NAME)
                        .build());
        Util.extendDataByInstancePID(list, preInstancePID);
        Util.extendDataByParentID(list, parentId);

        //set attributes to its default values
        if (!list.isEmpty()){
            DBObject materialDBObj = list.get(0);
            if (materialDBObj != null){
                String materialType = materialDBObj.getBasics().getType().getName();
                String rawType = materialDBObj.getAttributeValue(ATTRIBUTE_KIT_RS_NAME);
                if (OEM_MATERIAL_TYPES_LST.contains(materialType)){
                    rawType = "ПКИ";
                }
                if (CALCULATION_METHOD_BY_TYPE.containsKey(rawType)){
                    Constants.CALCULATE_METHOD method = CALCULATION_METHOD_BY_TYPE.get(rawType);
                    List<String> hiddedAttributes = HIDE_RULES_MAP.get(method);

                    Map<String, String> attributesToClearMap = MFG_BAR_ATTRIBUTES.entrySet()
                            .stream()
                            .filter(entry -> hiddedAttributes.contains(entry.getKey()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    if (attributesToClearMap.size() > 0){
                        DomainObject domObj = DomainObject.newInstance(context, func.getPhysicalid());
                        domObj.setAttributeValues(context, attributesToClearMap);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Insert Predecessor for MBOM object in case of additional materials
     * (copy of 'Insert Predecessor' function in Manufactured Item Definition app)
     *
     * @param func           MBOM object
     * @param parentId       id of parent MBOM for given MBOM object in the structure (used to correctly prepare response)
     * @param predecessorPID pid of MBOM object which stands for Material
     */
    @SneakyThrows
    public List<DBObject> insertPredecessor2(Function func, String parentId, String predecessorPID, Selector selects) {
        Context context = getContext();

        //create instance to given Material MBOM
        String preInstancePID = Util.createInstance(context, func.getPhysicalid(), predecessorPID, RELATIONSHIP_PROCESS_INSTANCE_CONTINUOUS);

        //get info
        List<DBObject> list = getInfo(new String[]{predecessorPID}, selects);
        Util.extendDataByInstancePID(list, preInstancePID);
        Util.extendDataByParentID(list, parentId);

        return list;
    }

    /**
     * Get Predecessors for MBOM object
     *
     * @param func        MBOM object
     * @param instancePID instance pid to given MBOM object in the structure
     */
    @SneakyThrows
    public List<DBObject> getPredecessors(Function func, String instancePID) {
        Context context = getContext();

        List<String> ownerPIDs = Util.findSROwners(context, instancePID, TYPE_PROCESS_PREREQUISITE_CNX_CUST);

        List<DBObject> list = new ArrayList<>();
        if (ownerPIDs.size() > 0) {
            if (ownerPIDs.size() > 1)
                log.warn(String.format("getPredecessors(). %s composee objects found for %s, only %s takes into account for further calculations", ownerPIDs, func.getPhysicalid(), ownerPIDs.get(0)));

            String elPIDs = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", ownerPIDs.get(0), SELECT_PATHS_SR_EL_0_PHYSICALID);
            List<String> instPIDs = new ArrayList<>(Arrays.asList(elPIDs.split(DELIMITER_COMMA)));
            instPIDs.removeIf(instancePID::equals);//remove instance pid itself
            if (instPIDs.size() > 1)
                log.warn(String.format("getPredecessors(). %s predecessors found for %s, only %s takes into account", instPIDs, func.getPhysicalid(), instPIDs.get(0)));
            String prePID = MqlUtil.mqlCommand(context, "print connection $1 select $2 dump", instPIDs.get(0), SELECT_TO_PHYSICALID);

            Map preMap = extendAttributesInfo(context, prePID);
            Function funcFromDB = Function.fromMap(preMap);
            list.add(funcFromDB);
        }

        return list;
    }


    /**
     * Get Implemented Links with extended attribute info
     *
     * @param instancePID instance pid to given MBOM object in the structure
     */
    @SneakyThrows
    private List<DBObject> getScoped(String instancePID) {
        Context context = getContext();

        //calculate First Upper Scope. In BOP business process only 1 level of MBOM expanding
        String parentPID = MqlUtil.mqlCommand(context, "print connection $1 select $2 dump", instancePID, SELECT_FROM_PHYSICALID);

        Selector selector = new Selector.Builder()
                .withoutDefaults()
                .basic(SELECT_PHYSICALID)
                .build();
        List<DBObject> scopedList = Util.getImplementedLinks(context, parentPID, selector, TYPE_PROCESS_IMPLEMENT_CNX);

        List<DBObject> list = new ArrayList<>();
        for (DBObject obj : scopedList) {
            Map map = extendAttributesInfo(context, obj.getPhysicalid());
            Product prd = Product.fromMap(map);
            list.add(prd);
        }

        return list;
    }
}
