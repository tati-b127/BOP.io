package com.igatec.bop.controllers;

import com.igatec.bop.core.dto.RouteDTO;
import com.igatec.bop.core.model.Route;
import com.igatec.bop.services.RouteService;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.igatec.bop.utils.Constants.DEFAULT_VAL;
import static com.igatec.bop.utils.Constants.PARAM_SELECTS;

@RestController
@RequestMapping(path = "api/routes")
@Api(tags = {"Route"})
@SwaggerDefinition(tags = {
        @Tag(name = "Route", description = "provides rest service to work with objects of type 'Route'")//todo description doesn't appear
})
public class RouteController {
    @Autowired
    RouteService routeService;

    @ApiOperation(value = "Create Route", response = Route.class)
    @PutMapping(path = "createRoute", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> createRoute(
            @RequestBody String pBody,
            @ApiParam(value = "comma separated lista with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return routeService.createRoute(pBody, selects);
    }

    @ApiOperation(value = "Add content", response = Route.class)
    @PutMapping(path = "addContent", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> addContent(
            @RequestBody String pBody,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return routeService.addContent(pBody, selects);
    }

    @ApiOperation(value = "Delete content", response = Route.class)
    @DeleteMapping(path = "deleteContent", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> deleteContent(
            @ApiParam(value = "DTO route content")
            @RequestBody List<RouteDTO> routeDTOs,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return routeService.deleteContent(routeDTOs, selects);
    }
}
