package com.igatec.bop.controllers;

import com.igatec.bop.core.model.GeneralSystem;
import com.igatec.bop.services.GeneralSystemService;
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

import java.util.List;

import static com.igatec.bop.utils.Constants.DEFAULT_VAL;
import static com.igatec.bop.utils.Constants.PARAM_DIRECTION;
import static com.igatec.bop.utils.Constants.PARAM_DUPLICATE;
import static com.igatec.bop.utils.Constants.PARAM_GSYS;
import static com.igatec.bop.utils.Constants.PARAM_ID;
import static com.igatec.bop.utils.Constants.PARAM_IDS;
import static com.igatec.bop.utils.Constants.PARAM_INSTANCE_ID;
import static com.igatec.bop.utils.Constants.PARAM_MBOM;
import static com.igatec.bop.utils.Constants.PARAM_OPERATION;
import static com.igatec.bop.utils.Constants.PARAM_PARENT_ID;
import static com.igatec.bop.utils.Constants.PARAM_PARENT_PID;
import static com.igatec.bop.utils.Constants.PARAM_RECURSE_TO_LEVEL;
import static com.igatec.bop.utils.Constants.PARAM_REL_ID;
import static com.igatec.bop.utils.Constants.PARAM_SELECTS;
import static com.igatec.bop.utils.Constants.PARAM_TYPE;
import static com.igatec.bop.utils.Constants.TYPE_GENERAL_SYSTEM;
import static com.igatec.bop.utils.Constants.TYPE_HEADER_OPERATION_REFERENCE;

@RestController
@RequestMapping(path = "api/systems")
@Transactional
@Api(tags = {"GeneralSystem"})
@SwaggerDefinition(tags = {
        @Tag(name = "GeneralSystem", description = "provides rest service to get information about objects of type 'DELLmiGeneralSystemReference'\"'")//todo description doesn't appear
})
public class GeneralSystemController {

    @Autowired
    GeneralSystemService generalSystemService;

    @ApiOperation(value = "Provides information regarding General System with given id", response = GeneralSystem.class)
    @GetMapping(path = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public GeneralSystem getInfo(
            @ApiParam(value = "General System's id")
            @PathVariable(PARAM_ID) String id,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return generalSystemService.getByGeneralSystem(id, selects);
    }

    @ApiOperation(value = "Provides information regarding several objects of type General System by given ids", response = List.class)
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getInfo(
            @ApiParam(value = "General Systems' ids")
            @RequestParam(PARAM_IDS) String[] ids,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return generalSystemService.getInfo(ids, selects);
    }

    @ApiOperation(value = "Deletes General System")
    @DeleteMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public void delete(
            @ApiParam(value = "Body stores General Systems' ids comma separated")
            @RequestBody String pBody) {
        generalSystemService.delete(pBody);
    }

    @ApiOperation(value = "Get all connected to given General System Operation objects", response = List.class)
    @GetMapping(path = "{id}/operations", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getOperations(
            @ApiParam(value = "General System's id")
            @PathVariable(PARAM_ID) GeneralSystem gsys,
            @ApiParam(value = "Parent id in the tree")
            @RequestParam("parentId") String parentId,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return generalSystemService.getOperations(gsys, parentId, selects);
    }

    @ApiOperation(value = "Get all connected to given General System children General System objects", response = List.class)
    @GetMapping(path = "{id}/systems", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getSystems(
            @ApiParam(value = "General System's id")
            @PathVariable(PARAM_ID) GeneralSystem gsys,
            @ApiParam(value = "Parent id in the tree")
            @RequestParam("parentId") String parentId,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return generalSystemService.getSystems(gsys, parentId, selects);
    }

    @ApiOperation(value = "Get given General System's structure including General System, Header Operation and Instruction objects", response = List.class)
    @GetMapping(path = "{id}/structure", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getStructure(
            @ApiParam(value = "General System's id")
            @PathVariable(PARAM_ID) GeneralSystem gsys,
            @ApiParam(value = "Parent id in the tree")
            @RequestParam("parentId") String parentId,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects,
            @ApiParam(value = "level to expand. 0 - stands for all")
            @RequestParam(name = PARAM_RECURSE_TO_LEVEL, defaultValue = "4") short recurseToLevel) {
        return generalSystemService.getStructure(gsys, parentId, selects, recurseToLevel);
    }

    @ApiOperation(value = "Get possible for creation kit_ShopFloor objects based on factory digital mockup", response = List.class)
    @GetMapping(path = "{id}/candidates", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getCandidates(
            @ApiParam(value = "PPRContext id")
            @PathVariable(PARAM_ID) GeneralSystem gsys,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.getCandidates(gsys, selects);
    }

    @ApiOperation(value = "Creates Scope link with MBOM", response = List.class)
    @PostMapping(path = "{id}/createScope", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> createScope(
            @ApiParam(value = "General System's id")
            @PathVariable(PARAM_ID) GeneralSystem gsys,
            @ApiParam(value = "id of object from MBOM structure")
            @RequestParam(PARAM_MBOM) String mbomId) throws Exception {
        return generalSystemService.createScope(gsys, mbomId);
    }

    @ApiOperation(value = "Creates Header Operation and connect to the General System", response = List.class)
    @PostMapping(path = "{id}/insertHeaderOperation", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> insertHeaderOperation(
            @ApiParam(value = "General System's id")
            @PathVariable(PARAM_ID) GeneralSystem gsys,
            @ApiParam(value = "Header Operation's id (for 'add existing' function)")
            @RequestParam(name = PARAM_OPERATION, required = false) String opId,
            @ApiParam(value = "true if duplicate from given gsys need to be created")
            @RequestParam(name = PARAM_DUPLICATE, required = false) String isDuplicate,
            @ApiParam(value = "specified type of Header Operation")
            @RequestParam(name = PARAM_TYPE, defaultValue = TYPE_HEADER_OPERATION_REFERENCE) String type,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return generalSystemService.insertHeaderOperation(gsys, opId, isDuplicate, type, selects);
    }

    @ApiOperation(value = "Create General System and connect to the given id", response = List.class)
    @PostMapping(path = "{id}/insertGeneralSystem", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> insertGeneralSystem(
            @ApiParam(value = " General System's id")
            @PathVariable(PARAM_ID) DBObject gsys,
            @ApiParam(value = "id of General System object (used for 'add existing' function)")
            @RequestParam(name = PARAM_GSYS, required = false) String gsysId,
            @ApiParam(value = "true if duplicate from given gsys need to be created")
            @RequestParam(name = PARAM_DUPLICATE, required = false) String isDuplicate,
            @ApiParam(value = "specified type of General System to connect")
            @RequestParam(name = PARAM_TYPE, defaultValue = TYPE_GENERAL_SYSTEM) String type,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return generalSystemService.insertGeneralSystem(gsys, gsysId, isDuplicate, type, selects);
    }

    @ApiOperation(value = "Set Kit_NumOperation attribute value for all child Header Operations found in the tree structure", response = List.class)
    @PutMapping(path = "{id}/setNumOperation", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> setNumOperation(
            @ApiParam(value = " General System's id")
            @PathVariable(PARAM_ID) GeneralSystem gsys,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.setNumOperation(gsys, selects);
    }

    @ApiOperation(value = "Set Kit_NumOperation attribute value for all child Header Operations found in the tree structure", response = List.class)
    @PutMapping(path = "setNumOperation", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> setNumOperation(
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.setNumOperation(selects);
    }

    @ApiOperation(value = "Set V_TreeOrder attr value for every Operation instance according to order in input parameter v2", response = List.class)
    @PutMapping(path = "setTreeOrder_v2", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> setTreeOrder_v2(
            @ApiParam(value = "moved Operation instance id")
            @RequestParam(name = PARAM_REL_ID) String opInstanceId,
            @ApiParam(value = "pid of parent obj in the tree")
            @RequestParam(name = PARAM_PARENT_PID) String parentPID,
            @ApiParam(value = "id of parent obj in the tree")
            @RequestParam(name = PARAM_PARENT_ID) String parentId,
            @ApiParam(value = "direction 'up' or 'down' in which Operation is moved")
            @RequestParam(name = PARAM_DIRECTION) String direction,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.setTreeOrder_v2(opInstanceId, parentPID, parentId, direction, selects);
    }

    @ApiOperation(value = "Refresh all expanded objects in the tree structure", response = List.class)
    @PostMapping(path = "refresh", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> refresh(
            @ApiParam(value = "Body stores General Systems' ids comma separated")
            @RequestBody String pBody,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.refresh(pBody, selects);
    }
    
    @ApiOperation(value = "Promotes selected objects", response = List.class)
    @PutMapping(path = "promote", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> promote(
            @ApiParam(value = "Body stores General Systems' ids comma separated")
            @RequestBody String pBody,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.promote(pBody, selects);
    }

    @ApiOperation(value = "Demotes selected objects", response = List.class)
    @PutMapping(path = "demote", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> demote(
            @ApiParam(value = "Body stores General Systems' ids comma separated")
            @RequestBody String pBody,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.demote(pBody, selects);
    }

    @ApiOperation(value = "Reserves selected objects", response = List.class)
    @PutMapping(path = "reserve", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> reserve(
            @ApiParam(value = "Body stores General Systems' ids comma separated")
            @RequestBody String pBody,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.reserve(pBody, selects);
    }

    @ApiOperation(value = "Unreserves selected objects", response = List.class)
    @PutMapping(path = "unreserve", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> unreserve(
            @ApiParam(value = "Body stores General Systems' ids comma separated")
            @RequestBody String pBody,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.unreserve(pBody, selects);
    }

    @ApiOperation(value = "Prepare to approve reserved by context user objects", response = List.class)
    @PutMapping(path = "prepare", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> prepare(
            @ApiParam(value = "Body stores General Systems' ids comma separated")
            @RequestBody String pBody,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.prepare(pBody, selects);
    }


    @ApiOperation(value = "Clone whole structure", response = List.class)
    @PutMapping(path = "{id}/cloneStructure", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> cloneStructure(
            @ApiParam(value = "Header Operation's id")
            @PathVariable(PARAM_ID) DBObject obj,
            @ApiParam(value = "id of parent object in the structure (used to correctly prepare response)")
            @RequestParam(name = PARAM_PARENT_PID) String parentPID,
            @ApiParam(value = "id of object to be copied from")
            @RequestParam(name = PARAM_INSTANCE_ID, required = false) String instId,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return generalSystemService.cloneStructure(obj, parentPID, instId, selects);
    }
}
