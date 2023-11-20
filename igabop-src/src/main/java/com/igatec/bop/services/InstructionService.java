package com.igatec.bop.services;

import com.igatec.bop.core.dao.InstructionDAO;
import com.igatec.bop.core.dao.PPRContextDAO;
import com.igatec.bop.core.model.Instruction;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.util.FrameworkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.DELIMITER_COMMA;
import static com.igatec.bop.utils.Constants.PARAM_INSTANCE_ID;
import static com.igatec.bop.utils.Constants.SELECT_TYPE;
import static com.igatec.bop.utils.Constants.TYPE_KIT_MFG_ASSEMBLY;
import static com.igatec.bop.utils.Constants.TYPE_KIT_MFG_PRODUCED_PART;
import static com.igatec.bop.utils.Constants.TYPE_MFG_PRODUCTION_PLANNING;
import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT_PROCESS;
import static com.igatec.bop.utils.Constants.TYPE_PPR_CONTEXT_SYSTEM;

@Service
public class InstructionService {

    @Autowired
    @ObjectDAO("Instruction")
    InstructionDAO instructionDAO;

    @Autowired
    @ObjectDAO("PPRContext")
    PPRContextDAO pprContextDAO;

    @Autowired
    PPRContextService pprContextService;

    /**
     * @param id      object id
     * @param selects selector
     * @return DBObject instruction
     */
    public Instruction getInfo(String id, Selector selects) throws FrameworkException {
        return instructionDAO.get(id, selects);
    }

    /**
     * Modify Work Instruction attributes values
     *
     * @param instr   Work Instruction object id
     * @param selects comma separated list with MQL object selects for response data
     * @param pBody   body
     * @return list of DBObject
     */
    public List<DBObject> modify(DBObject instr, String pBody, Selector selects) throws Exception {
        return instructionDAO.modify(instr, pBody, selects);
    }

    /**
     * Creates Document and checks given file in it
     *
     * @param obj     Work Instruction or Operation object id
     * @param file    File
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> createDocument(DBObject obj, MultipartFile file, Selector selects) throws Exception {
        return instructionDAO.createDocument(obj, file, selects);
    }

    /**
     * Get all connected to given Header Operation (or Instruction) Document objects
     *
     * @param obj     Header Operation (Instruction) object id
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> getDocuments(DBObject obj, Selector selects) {
        return instructionDAO.getDocuments(obj, selects);
    }

    /**
     * Deletes Instruction and/or its Composee. In case of composee it means deleting link
     *
     * @param instr   Instruction object
     * @param ids     comma separated ids to delete
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> delete(DBObject instr, String ids, Selector selects, String bopPath, String contextId) throws FrameworkException {
        instructionDAO.delete(instr, ids, selects, bopPath);
        List<DBObject> contextMbom = pprContextDAO.getComposeeStructureForOperations(pprContextDAO.get(contextId, selects), contextId, selects,
                String.format("%s,%s", TYPE_PPR_CONTEXT_SYSTEM, TYPE_PPR_CONTEXT_PROCESS), (short) 0);
        return pprContextService.checkSetBop(contextId, contextMbom, selects);
    }
}
