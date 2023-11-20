package com.igatec.bop.controllers;

import com.igatec.bop.core.model.PPRContext;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.igatec.bop.utils.Constants.DEFAULT_VAL;
import static com.igatec.bop.utils.Constants.PARAM_DUPLICATE;
import static com.igatec.bop.utils.Constants.PARAM_GSYS;
import static com.igatec.bop.utils.Constants.PARAM_RECURSE_TO_LEVEL;
import static com.igatec.bop.utils.Constants.PARAM_SELECTS;
import static com.igatec.bop.utils.Constants.PARAM_TYPE;
import static com.igatec.bop.utils.Constants.TYPE_GENERAL_SYSTEM;

@RestController
@RequestMapping(path = "api/pprcontexts")
@Transactional
@Api(tags = {"PPRContext"})
@SwaggerDefinition(tags = {
        @Tag(name = "PPRContext", description = "provides rest service for 3DE objects of type 'PPRContext' and its derivatives")//todo description doesn't appear
})
public class PPRContextController {

    @Autowired
    PPRContextService pprContextService;

    @ApiOperation(value = "Provides information regarding existing PPRContext objects", response = PPRContext.class)
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> get(
            @ApiParam(value = "where expression (MQL valid)")
            @RequestParam(name = "where", required = false) String where,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return pprContextService.get(where, selects);
    }

    @ApiOperation(value = "Provides information regarding PPRContext with given id", response = PPRContext.class)
    @GetMapping(path = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getInfo(
            @ApiParam(value = "PPRContext id")
            @PathVariable("id") String id,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return pprContextService.getInfo(id, selects);
    }

    @ApiOperation(value = "Get all connected to given PPRContext General System objects", response = List.class)
    @GetMapping(path = "{id}/systems", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getSystems(
            @ApiParam(value = "PPRContext id")
            @PathVariable("id") PPRContext ppr,
            @ApiParam(value = "Parent id in the tree")
            @RequestParam("parentId") String parentId,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return pprContextService.getSystems(ppr, parentId, selects);
    }

    @ApiOperation(value = "Expand structure tree including General Systems, MBOM and EBOM", response = List.class)
    @GetMapping(path = "{id}/structure", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getStructure(
            @ApiParam(value = "PPRContext id")
            @PathVariable("id") PPRContext ppr,
            @ApiParam(value = "Parent id in the tree")
            @RequestParam("parentId") String parentId,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects,
            @ApiParam(value = "level to expand. 0 - stands for all")
            @RequestParam(name = PARAM_RECURSE_TO_LEVEL, defaultValue = "1") short recurseToLevel) {
        return pprContextService.getStructure(ppr, parentId, selects, recurseToLevel);
    }

    @ApiOperation(value = "Get possible for creation kit_Factory objects based on factory digital mockup", response = List.class)
    @GetMapping(path = "{id}/candidates", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getCandidates(
            @ApiParam(value = "PPRContext id")
            @PathVariable("id") PPRContext ppr,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return pprContextService.getCandidates(ppr, selects);
    }

    @ApiOperation(value = "Create General System and connect to the given id", response = List.class)
    @PostMapping(path = "{id}/insertGeneralSystem", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> insertGeneralSystem(
            @ApiParam(value = "PPRContext id")
            @PathVariable("id") DBObject ppr,
            @ApiParam(value = "id of General System object (used for 'add existing' function)")
            @RequestParam(name = PARAM_GSYS, required = false) String gsysId,
            @ApiParam(value = "true if duplicate from given gsys need to be created")
            @RequestParam(name = PARAM_DUPLICATE, required = false) String isDuplicate,
            @ApiParam(value = "specified type of General System")
            @RequestParam(name = PARAM_TYPE, defaultValue = TYPE_GENERAL_SYSTEM) String type,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return pprContextService.insertGeneralSystem(ppr, gsysId, isDuplicate, type, selects);
    }
}
