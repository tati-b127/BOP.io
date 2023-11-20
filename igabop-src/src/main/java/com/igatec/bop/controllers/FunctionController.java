package com.igatec.bop.controllers;

import com.igatec.bop.core.model.Function;
import com.igatec.bop.services.FunctionService;
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
import static com.igatec.bop.utils.Constants.PARAM_ID;
import static com.igatec.bop.utils.Constants.PARAM_INSTANCE_ID;
import static com.igatec.bop.utils.Constants.PARAM_PARENT_ID;
import static com.igatec.bop.utils.Constants.PARAM_PREDECESSOR;
import static com.igatec.bop.utils.Constants.PARAM_RECURSE_TO_LEVEL;
import static com.igatec.bop.utils.Constants.PARAM_SELECTS;

@RestController
@RequestMapping(path = "api/functions")
@Transactional
@Api(tags = {"Function"})
@SwaggerDefinition(tags = {
        @Tag(name = "Function", description = "provides rest service for 3DE objects of type 'DELFmiFunctionReference' and its derivatives")//todo description doesn't appear
})
public class FunctionController {

    @Autowired
    FunctionService functionService;

    @ApiOperation(value = "Provides information regarding existing Function objects", response = Function.class)
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> get(
            @ApiParam(value = "where expression (MQL valid)")
            @RequestParam(name = "where", required = false) String where,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return functionService.get(where, selects);
    }

    @ApiOperation(value = "Provides information regarding Function with given id", response = Function.class)
    @GetMapping(path = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Function getInfo(
            @ApiParam(value = "Function id")
            @PathVariable("id") Function func,
            @ApiParam(value = "instance pid to given MBOM object in the structure")
            @RequestParam(name = PARAM_INSTANCE_ID) String instancePID) {
        return functionService.getInfo(func, instancePID);
    }

    @ApiOperation(value = "Modify MBOM's attributes' values", response = Function.class)
    @PutMapping(path = "{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DBObject modify(
            @RequestBody String pBody,
            @ApiParam(value = "MBOM's id")
            @PathVariable(PARAM_ID) Function func,
            @ApiParam(value = "instance pid to given MBOM object in the structure")
            @RequestParam(name = PARAM_INSTANCE_ID) String instancePID) throws Exception {
        return functionService.modify(func, pBody, instancePID);
    }

    @ApiOperation(value = "Modify MBOM instantce's attributes' values", response = Function.class)
    @PutMapping(path = "{id}/instance", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DBObject modifyInstance(
            @RequestBody String pBody,
            @ApiParam(value = "MBOM's id")
            @PathVariable(PARAM_ID) Function func,
            @ApiParam(value = "instance pid to given MBOM object in the structure")
            @RequestParam(name = PARAM_INSTANCE_ID) String instancePID) throws Exception {
        return functionService.modifyInstance(func, pBody, instancePID);
    }

    @ApiOperation(value = "Expand structure tree for Function", response = List.class)
    @GetMapping(path = "{id}/structure", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getStructure(
            @ApiParam(value = "Function id")
            @PathVariable("id") Function product,
            @ApiParam(value = "Parent id in the tree")
            @RequestParam("parentId") String parentId,
            @ApiParam(value = "PPRContex id in the tree")
            @RequestParam("pprContextId") String pprContextId,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects,
            @ApiParam(value = "level to expand. 0 - stands for all")
            @RequestParam(name = PARAM_RECURSE_TO_LEVEL, defaultValue = "1") short recurseToLevel) {
        return functionService.getStructure(product, parentId, pprContextId, selects, recurseToLevel);
    }

    @ApiOperation(value = "Insert Predecessor for MBOM object", response = List.class)
    @PostMapping(path = "{id}/insertPredecessor", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> insertPredecessor(
            @ApiParam(value = "MBOM id")
            @PathVariable("id") Function func,
            @ApiParam(value = "instance pid to given MBOM object in the structure")
            @RequestParam(name = PARAM_INSTANCE_ID) String instancePID,
            @ApiParam(value = "id of parent MBOM for given MBOM object in the structure (used to correctly prepare response)")
            @RequestParam(name = PARAM_PARENT_ID) String parentId,
            @ApiParam(value = "specified type of General System")
            @RequestParam(name = PARAM_PREDECESSOR) String predecessorPID,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return functionService.insertPredecessor(func, instancePID, parentId, predecessorPID, selects);
    }

    @ApiOperation(value = "Insert Predecessor for MBOM object in case of additional materials", response = List.class)
    @PostMapping(path = "{id}/insertPredecessor2", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> insertPredecessor2(
            @ApiParam(value = "MBOM id")
            @PathVariable("id") Function func,
            @ApiParam(value = "id of parent MBOM for given MBOM object in the structure (used to correctly prepare response)")
            @RequestParam(name = PARAM_PARENT_ID) String parentId,
            @ApiParam(value = "specified type of General System")
            @RequestParam(name = PARAM_PREDECESSOR) String predecessorPID,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return functionService.insertPredecessor2(func, parentId, predecessorPID, selects);
    }
}
