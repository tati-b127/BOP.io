package com.igatec.bop.services;

import com.igatec.bop.core.dao.HeaderOperationDAO;
import com.igatec.bop.core.dao.PPRContextDAO;
import com.igatec.bop.core.model.HeaderOperation;
import com.igatec.bop.core.model.PPRContext;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.annotations.MatrixTransactional;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.util.FrameworkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT_PROCESS;
import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT_SYSTEM;

/**
 * Class represents wethods to work with Header Operation
 */
@Service
public class HeaderOperationService {

    @Autowired
    @ObjectDAO("HeaderOperation")
    HeaderOperationDAO headerOperationDAO;

    @Autowired
    @ObjectDAO("PPRContext")
    PPRContextDAO pprContextDAO;

    @Autowired
    PPRContextService pprContextService;

    /**
     * Returns informstion of object
     *
     * @param id      object id
     * @param selects selector
     * @return object HeaderOperation
     */
    public HeaderOperation getInfo(String id, Selector selects) throws FrameworkException {
        return headerOperationDAO.get(id, selects);
    }

    /**
     * Get all connected to given Header Operation Work Instructions
     *
     * @param op       Header Operation object id
     * @param selects  Comma separated list with object selects
     * @param parentId id parent object
     * @return list of DBObject
     */
    public List<DBObject> getInstructions(HeaderOperation op,
                                          String parentId,
                                          Selector selects
    ) throws Exception {
        return headerOperationDAO.getInstructions(op, parentId, selects);
    }

    /**
     * Get all connected to given Header Operation (or Instruction) Resource objects
     *
     * @param op      Header Operation object id
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> getResources(DBObject op, Selector selects) throws Exception {
        return headerOperationDAO.getResources(op, selects);
    }

    /**
     * Get all connected to given Header Operation (or Instruction) MBOM objects (Implement Links)
     *
     * @param op         Header Operation object id
     * @param instanceId Header Operation instance id
     * @param selects    selector
     * @return list of DBObject
     */
    public List<DBObject> getMBOM(DBObject op, String instanceId, Selector selects) {
        return headerOperationDAO.getMBOM(op, instanceId, selects);
    }

    /**
     * Get all connected to the given Header Operation template Resource objects
     *
     * @param op      Header Operation object id
     * @param selects Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> getCandidates(DBObject op, Selector selects) {
        return headerOperationDAO.getCandidates(op, selects);
    }

    /**
     * Creates Instruction and connects to Header Operation
     * (copy of 'Create Text Instruction' function in Work Instructions app)
     *
     * @param op      Header Operation object
     * @param wiId    id of Instruction object (used for 'add existing' function)
     * @param selects comma separated list with MQL object selects for response data
     * @return list of DBObject
     */
    @MatrixTransactional
    public List<DBObject> createInstruction(HeaderOperation op,
                                            String wiId,
                                            Selector selects
    ) throws Exception {
        return headerOperationDAO.createInstruction(op, wiId, 0, selects);
    }

    /**
     * Creates new Resource objects (or get existing) and connect to given Header Operation as capable resource
     * (copy of Create capable resource link function in Planning Structure app)
     *
     * @param op          Header Operation object
     * @param resId       ids of Resource objects (used for 'add existing' function or/and to be copied from)
     * @param isDuplicate true if duplicate from given gsys need to be created
     * @param type        specified Interface of Resource to connect
     * @param selects     comma separated list with MQL object selects for response data
     * @return list of DBObject
     */
    @MatrixTransactional
    public List<DBObject> createResource(DBObject op,
                                         String resId,
                                         String isDuplicate,
                                         String type,
                                         Selector selects
    ) throws Exception {
        return headerOperationDAO.createResource(op, resId, isDuplicate, type, selects);
    }

    /**
     * Creates Implement Link with MBOM
     *
     * @param op         Header Operation object id
     * @param bopRootPID id of BOP root
     * @param bopPath    full path instance ids of BOP
     * @param mbomPath   full path instance ids of dropped MBOM
     * @param selects    selector
     * @return object of DBObject
     */
    @MatrixTransactional
    public List<DBObject> createImplementLink(HeaderOperation op,
                                              String bopRootPID,
                                              String bopPath,
                                              String mbomPath,
                                              Selector selects,
                                              String pprContextId) throws FrameworkException {
        headerOperationDAO.createImplementLink(op, bopRootPID, bopPath, mbomPath, selects);
        List<DBObject> contextMbom = pprContextDAO.getComposeeStructureForOperations(pprContextDAO.get(pprContextId, selects), pprContextId, selects,
                String.format("%s,%s", TYPE_PPR_CONTEXT_SYSTEM, TYPE_PPR_CONTEXT_PROCESS), (short) 0);
        return pprContextService.checkSetBop(pprContextId, contextMbom, selects);
    }

    /**
     * Deletes Header Operation and/or its Composee. In case of composee it means deleting link
     *
     * @param op  Header Operation object
     * @param ids comma separated ids to delete
     * @return list of DBObject
     */
    @MatrixTransactional
    public List<DBObject> delete(DBObject op, String ids, Selector selects) {
        return headerOperationDAO.delete(op, ids, selects);
    }

    /**
     * Set Work Instructions transition numbers for given Header Operation in order to TreeOrder
     *
     * @param op       Header Operation object id
     * @param parentId client id of parent object
     * @param selects  Comma separated list with object selects
     * @return list of DBObject
     */
    @MatrixTransactional
    public List<DBObject> setTransitionNumbers(HeaderOperation op, String parentId, Selector selects) {
        return headerOperationDAO.setTransitionNumbers(op, parentId, selects);
    }
}
