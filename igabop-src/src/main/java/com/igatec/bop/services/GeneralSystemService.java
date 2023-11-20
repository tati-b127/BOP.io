package com.igatec.bop.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igatec.bop.core.dao.GeneralSystemDAO;
import com.igatec.bop.core.dao.PPRContextDAO;
import com.igatec.bop.core.model.GeneralSystem;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.util.FrameworkException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.PARAM_IDS;
import static com.igatec.bop.utils.Constants.PARAM_INSTANCE_ID;
import static com.igatec.bop.utils.Constants.PARAM_SELECTED;
import static com.igatec.bop.utils.Constants.SELECT_PHYSICALID;
import static com.igatec.bop.utils.Constants.SELECT_TYPE;
import static com.igatec.bop.utils.Constants.TYPE_KIT_FACTORY;

/**
 *
 */
@Service
public class GeneralSystemService {

    @Autowired
    @ObjectDAO("GeneralSystem")
    private GeneralSystemDAO systemDAO;
    @Autowired
    @ObjectDAO("PPRContext")
    PPRContextDAO pprContextDAO;
    @Autowired
    private PPRContextService pprContextService;

    /**
     * @param id      id document
     * @param selects selector
     */
    public GeneralSystem getByGeneralSystem(String id, Selector selects) throws FrameworkException {
        return systemDAO.get(id, selects);
    }

    /**
     * @param ids     ids of document
     * @param selects selector to get information
     * @return kist of DBObject
     */
    public List<DBObject> getInfo(String[] ids, Selector selects) throws FrameworkException {
        return systemDAO.getInfo(ids, selects);
    }

    /**
     * Deletes General System and/or its Composee. In case of composee it means deleting link
     *
     * @param pBody contains comma separated ids to delete
     */
    @SneakyThrows
    public void delete(String pBody) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> bodyMap =
                mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
                });
        List<Map> objs = (List<Map>) bodyMap.get(PARAM_SELECTED);

        List<String> factoryPIDs = objs.stream()
                .filter(map -> TYPE_KIT_FACTORY.equals(map.get(SELECT_TYPE)))
                .map(map -> (String) map.get(SELECT_PHYSICALID))
                .collect(Collectors.toList());
        factoryPIDs.removeIf(Objects::isNull);

        List<String> instanceIds = objs.stream()
                .filter(map -> !TYPE_KIT_FACTORY.equals(map.get(SELECT_TYPE)))
                .map(map -> (String) map.get(PARAM_INSTANCE_ID))
                .collect(Collectors.toList());
        instanceIds.removeIf(Objects::isNull);

        if (!instanceIds.isEmpty()) {
            systemDAO.deleteRelationship(instanceIds);
        }
        if (!factoryPIDs.isEmpty()) {
            systemDAO.deleteObject(factoryPIDs);
        }
    }

    /**
     * Get all connected to given General System Operation objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> getOperations(GeneralSystem gsys,
                                        String parentId,
                                        Selector selects
    ) throws Exception {
        return systemDAO.getOperations(gsys, parentId, selects);
    }

    /**
     * Get first level connected to given General System children General System objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> getSystems(GeneralSystem gsys,
                                     String parentId,
                                     Selector selects
    ) throws Exception {
        return systemDAO.getSystems(gsys, parentId, selects);
    }

    /**
     * Get given General System's structure including General System, Header Operation and Instruction objects
     *
     * @param gsys           General System object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     * @return list of DBObject
     */
    public List<DBObject> getStructure(GeneralSystem gsys,
                                       String parentId,
                                       Selector selects,
                                       short recurseToLevel) {
        return systemDAO.getStructure(gsys, parentId, selects, recurseToLevel);
    }

    /**
     * Get all connected to the given General System template General System objects
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> getCandidates(GeneralSystem gsys,
                                        Selector selects) {
        return systemDAO.getCandidates(gsys, selects);
    }

    /**
     * Creates Scope link between General System and given MBOM object
     * (copy of 'Create a Scope' function in Planning Structure app)
     *
     * @param gsys   GeneralSystem object
     * @param mbomId id of MBOM object
     * @return list of DBObject
     */
    public List<DBObject> createScope(GeneralSystem gsys, String mbomId) throws Exception {
        return systemDAO.createScope(gsys, mbomId);
    }

    /**
     * @param gsys
     * @param opId
     * @param isDuplicate
     * @param type
     * @param selects
     * @return list of DBObject
     * @throws Exception
     */
    public List<DBObject> insertHeaderOperation(GeneralSystem gsys,
                                                String opId,
                                                String isDuplicate,
                                                String type,
                                                Selector selects
    ) throws Exception {
        return systemDAO.insertHeaderOperation(gsys, opId, isDuplicate, type, selects);
    }

    /**
     * @param gsys
     * @param gsysId
     * @param isDuplicate
     * @param type
     * @param selects
     * @return list of DBObject
     * @throws Exception
     */
    public List<DBObject> insertGeneralSystem(DBObject gsys,
                                              String gsysId,
                                              String isDuplicate,
                                              String type,
                                              Selector selects
    ) throws Exception {
        return systemDAO.insertGeneralSystem(gsys, gsysId, isDuplicate, type, selects);
    }

    /**
     * Set Kit_NumOperation attr value for every Operation in General System according to Tree Order attr value
     *
     * @param gsys    General System object id
     * @param selects Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> setNumOperation(GeneralSystem gsys, Selector selects) {
        return systemDAO.setNumOperation(gsys, selects);
    }

    /**
     * Set Kit_NumOperation attr value for every Operation in General System according to Tree Order attr value
     *
     * @param selects Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> setNumOperation(Selector selects) {
        return systemDAO.setNumOperation(selects);
    }

    /**
     * Set V_TreeOrder attr value for every Operation instance according to direction
     *
     * @param opInstanceId comma separated list with Header Operation instances ids
     * @param parentPID    pid of parent obj in the tree
     * @param parentId     id of parent obj in the tree
     * @param direction    direction 'up' or 'down' in which Operation is moved
     * @param selects      Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> setTreeOrder_v2(String opInstanceId,
                                          String parentPID,
                                          String parentId,
                                          String direction,
                                          Selector selects) {
        return systemDAO.setTreeOrder_v2(opInstanceId, parentPID, parentId, direction, selects);
    }

    /**
     * Refresh all expanded objects in the tree structure
     *
     * @param pBody contains comma separated ids to refresh
     */
    @SneakyThrows
    public List<DBObject> refresh(String pBody, Selector selects) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> bodyMap = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });
        List<String> ids = (List<String>) bodyMap.get(PARAM_IDS);
        //it's result do not contain rels only attributes
        /*List<DBObject> result = systemDAO.systemExtendDataPro(ids, selects);*/

        if (!ids.isEmpty()) {
            //here we get all structure with statuses
            return pprContextService.getRoot(ids.get(0), selects);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Promotes selected objects
     *
     * @param pBody   BOP entity
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> promote(String pBody, Selector selects) {
        return systemDAO.promote(pBody, selects);
    }

    /**
     * Demotes selected objects
     *
     * @param pBody   BOP entity
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> demote(String pBody, Selector selects) {
        return systemDAO.demote(pBody, selects);
    }

    /**
     * Reserves selected objects
     *
     * @param pBody   BOP entity
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> reserve(String pBody, Selector selects) {
        return systemDAO.reserve(pBody, selects);
    }

    /**
     * Unreserves selected objects
     *
     * @param pBody   BOP entity
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> unreserve(String pBody, Selector selects) {
        return systemDAO.unreserve(pBody, selects);
    }

    /**
     * Prepare to approve reserved by context user objects
     *
     * @param pBody   BOP entity
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> prepare(String pBody, Selector selects) {
        return systemDAO.prepare(pBody, selects);
    }

    /**
     * Clone structure
     *
     * @param obj       dropped object
     * @param parentPID physicalid of parent object for target object (on which is dropped)
     * @param instId    instance physicalid of target object
     * @return list of DBObject
     */
    public List<DBObject> cloneStructure(DBObject obj,
                                         String parentPID,
                                         String instId,
                                         Selector selects) {
        return systemDAO.cloneStructure(obj, parentPID, instId, selects);
    }
}
