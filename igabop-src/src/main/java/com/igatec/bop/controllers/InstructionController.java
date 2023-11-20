package com.igatec.bop.controllers;

import com.igatec.bop.core.model.Instruction;
import com.igatec.bop.services.InstructionService;
import com.igatec.bop.services.PPRContextService;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.matrixone.apps.domain.util.FrameworkException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.igatec.bop.utils.Constants.DEFAULT_VAL;
import static com.igatec.bop.utils.Constants.PARAM_BOP_PATH;
import static com.igatec.bop.utils.Constants.PARAM_ID;
import static com.igatec.bop.utils.Constants.PARAM_IDS;
import static com.igatec.bop.utils.Constants.PARAM_SELECTS;

@RestController
@RequestMapping(path = "api/instructions")
@Transactional
@Api(tags = {"Instruction"})
@SwaggerDefinition(tags = {
        @Tag(name = "Instruction", description = "provides rest service to get information about objects of type 'DELWkiInstructionReference'")//todo description doesn't appear
})
public class InstructionController {
    @Autowired
    InstructionService instructionService;
    @Autowired
    PPRContextService pprContextService;

    @ApiOperation(value = "Provides information regarding Instruction with given id", response = Instruction.class)
    @GetMapping(path = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Instruction getInfo(
            @ApiParam(value = "Instruction's id")
            @PathVariable(PARAM_ID) String id,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return instructionService.getInfo(id, selects);
    }

    @ApiOperation(value = "Modify Instruction's metadata", response = Instruction.class)
    @PutMapping(path = "{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> modify(
            @RequestBody String pBody,
            @ApiParam(value = "Instruction's id")
            @PathVariable(PARAM_ID) DBObject instr,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return instructionService.modify(instr, pBody, selects);
    }

    @ApiOperation(value = "Creates Document and checks given file in it", response = DBObject.class)
    @PostMapping(path = "{id}/createDocument")
    public List<DBObject> createDocument(
            @ApiParam(value = "Instruction's (Header Operation's) id")
            @PathVariable(PARAM_ID) DBObject obj,
            @ApiParam(value = "File to attach")
            @RequestParam(name = "file") MultipartFile file,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return instructionService.createDocument(obj, file, selects);
    }

    @ApiOperation(value = "Get all connected to given Instruction (or Header Operation) Document objects", response = List.class)
    @GetMapping(path = "{id}/documents", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getDocuments(
            @ApiParam(value = "Instruction's (Header Operation's) id")
            @PathVariable(PARAM_ID) DBObject obj,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return instructionService.getDocuments(obj, selects);
    }

    @ApiOperation(value = "Deletes Instruction and|or its connections")
    @DeleteMapping(path = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> delete(
            @ApiParam(value = "Instruction's id")
            @PathVariable(PARAM_ID) DBObject instr,
            @ApiParam(value = "comma separated ids to delete")
            @RequestParam(PARAM_IDS) String ids,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects,
            @ApiParam(value = "Context id in the tree")
            @RequestParam("pprContextId") String pprContextId,
            @ApiParam(value = "full path instance ids of BOP")
            @RequestParam(name = PARAM_BOP_PATH) String bopPath) throws FrameworkException {
        return instructionService.delete(instr, ids, selects, bopPath, pprContextId);
    }
}
