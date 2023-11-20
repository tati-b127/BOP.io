package com.igatec.bop.controllers;

import com.igatec.bop.core.model.HeaderOperation;
import com.igatec.bop.services.HeaderOperationService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.igatec.bop.utils.Constants.DEFAULT_VAL;
import static com.igatec.bop.utils.Constants.INTERFACE_KIT_INDUSTRIAL_MACHINE;
import static com.igatec.bop.utils.Constants.PARAM_BOP_PATH;
import static com.igatec.bop.utils.Constants.PARAM_BOP_ROOT_PID;
import static com.igatec.bop.utils.Constants.PARAM_DUPLICATE;
import static com.igatec.bop.utils.Constants.PARAM_ID;
import static com.igatec.bop.utils.Constants.PARAM_IDS;
import static com.igatec.bop.utils.Constants.PARAM_INSTANCE_ID;
import static com.igatec.bop.utils.Constants.PARAM_MBOM_PATH;
import static com.igatec.bop.utils.Constants.PARAM_PARENT_ID;
import static com.igatec.bop.utils.Constants.PARAM_RESOURCE;
import static com.igatec.bop.utils.Constants.PARAM_SELECTS;
import static com.igatec.bop.utils.Constants.PARAM_TYPE;
import static com.igatec.bop.utils.Constants.PARAM_WI;

@RestController
@RequestMapping(path = "api/operations")
@Api(tags = {"HeaderOperation"})
@SwaggerDefinition(tags = {
        @Tag(name = "HeaderOperation", description = "provides rest service to get information about objects of type 'DELLmiHeaderOperationReference'")//todo description doesn't appear
})
public class HeaderOperationController {
    @Autowired
    HeaderOperationService headerOperationService;
    @Autowired
    PPRContextService pprContextService;

    @ApiOperation(value = "Provides information regarding Header Operation with given id", response = HeaderOperation.class)
    @GetMapping(path = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public HeaderOperation getInfo(
            @ApiParam(value = "Header Operation's id")
            @PathVariable("id") String id,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return headerOperationService.getInfo(id, selects);
    }

    @ApiOperation(value = "Get all connected to given Header Operation Work Instruction objects", response = List.class)
    @GetMapping(path = "{id}/instructions", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getInstructions(
            @ApiParam(value = "Header Operation's id")
            @PathVariable("id") HeaderOperation op,
            @ApiParam(value = "Parent id in the tree")
            @RequestParam("parentId") String parentId,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return headerOperationService.getInstructions(op, parentId, selects);
    }

    @ApiOperation(value = "Get all connected to given Header Operation (or Instruction) Resource objects", response = List.class)
    @GetMapping(path = "{id}/resources", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getResources(
            @ApiParam(value = "Header Operation's id")
            @PathVariable("id") DBObject op,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return headerOperationService.getResources(op, selects);
    }

    @ApiOperation(value = "Get all connected to given Header Operation (or Instruction) MBOM objects (Implement Links)", response = List.class)
    @GetMapping(path = "{id}/mbom", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getMBOM(
            @ApiParam(value = "Header Operation's id")
            @PathVariable("id") DBObject op,
            @ApiParam(value = "instance id")
            @RequestParam(name = PARAM_INSTANCE_ID) String instanceId,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return headerOperationService.getMBOM(op, instanceId, selects);
    }

    @ApiOperation(value = "Get possible for creation Resource objects based on factory digital mockup", response = List.class)
    @GetMapping(path = "{id}/candidates", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getCandidates(
            @ApiParam(value = "Header Operation's id")
            @PathVariable("id") DBObject op,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return headerOperationService.getCandidates(op, selects);
    }

    @ApiOperation(value = "Create Work Instruction and/or connect", response = List.class)
    @PostMapping(path = "{id}/createInstruction", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> createInstruction(
            @ApiParam(value = "Header Operation's id")
            @PathVariable("id") HeaderOperation op,
            @ApiParam(value = "id of Resource object (used for 'add existing' function)")
            @RequestParam(name = PARAM_WI, required = false) String wiId,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return headerOperationService.createInstruction(op, wiId, selects);
    }

    @ApiOperation(value = "Creates Resource and/or connect to the given id", response = List.class)
    @PostMapping(path = "{id}/createResource", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> createResource(
            @ApiParam(value = "Header Operation's id")
            @PathVariable("id") DBObject op,
            @ApiParam(value = "id of Resource object (used for 'add existing' function)")
            @RequestParam(name = PARAM_RESOURCE, required = false) String resId,
            @ApiParam(value = "true if duplicate from given gsys need to be created")
            @RequestParam(name = PARAM_DUPLICATE, required = false) String isDuplicate,
            @ApiParam(value = "specified Interface of Resource to connect")
            @RequestParam(name = PARAM_TYPE, defaultValue = INTERFACE_KIT_INDUSTRIAL_MACHINE) String type,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws Exception {
        return headerOperationService.createResource(op, resId, isDuplicate, type, selects);
    }

    @ApiOperation(value = "Creates Implement Link with MBOM", response = List.class)
    @PutMapping(path = "{id}/createImplementLink", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> createImplementLink(
            @ApiParam(value = "Header Operation's id")
            @PathVariable("id") HeaderOperation op,
            @ApiParam(value = "id of BOP root")
            @RequestParam(PARAM_BOP_ROOT_PID) String bopRootPID,
            @ApiParam(value = "full path instance ids of BOP")
            @RequestParam(name = PARAM_BOP_PATH) String bopPath,
            @ApiParam(value = "full path instance ids of dropped MBOM")
            @RequestParam(name = PARAM_MBOM_PATH) String mbomPath,
            @ApiParam(value = "Context id in the tree")
            @RequestParam("pprContextId") String pprContextId,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return headerOperationService.createImplementLink(op, bopRootPID, bopPath, mbomPath, selects, pprContextId);
    }

    @ApiOperation(value = "Deletes Header Operation and|or its connections")
    @DeleteMapping(path = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> delete(
            @ApiParam(value = "Header Operation's id")
            @PathVariable("id") DBObject op,
            @ApiParam(value = "comma separated ids to delete")
            @RequestParam(PARAM_IDS) String ids,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return headerOperationService.delete(op, ids, selects);
    }

    @ApiOperation(value = "Set Work Instructions transition numbers for given Header Operation in order to TreeOrder", response = List.class)
    @PutMapping(path = "{id}/setTransitionNumbers", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> setTransitionNumbers(
            @ApiParam(value = " General System's id")
            @PathVariable(PARAM_ID) HeaderOperation op,
            @ApiParam(value = "id of parent object in the structure (used to correctly prepare response)")
            @RequestParam(name = PARAM_PARENT_ID) String parentId,
            @ApiParam(value = "comma separated list with MQL object selects for response data")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return headerOperationService.setTransitionNumbers(op, parentId, selects);
    }
}
