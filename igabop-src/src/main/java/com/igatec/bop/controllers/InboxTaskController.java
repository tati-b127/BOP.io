package com.igatec.bop.controllers;

import com.igatec.bop.core.dao.InboxTaskDAO;
import com.igatec.bop.core.model.InboxTask;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.util.FrameworkException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.igatec.bop.utils.Constants.DEFAULT_VAL;
import static com.igatec.bop.utils.Constants.PARAM_SELECTS;

@RestController
@RequestMapping(path = "api/tasks")
@Transactional
@Api(tags = {"InboxTask"})
@SwaggerDefinition(tags = {
        @Tag(name = "InboxTask", description = "provides rest service to get information about objects of type 'Inbox Task'")//todo description doesn't appear
})
public class InboxTaskController {
    @Autowired
    @ObjectDAO("InboxTask")
    InboxTaskDAO inboxTaskDAO;

    @ApiOperation(value = "Provides information regarding Inbox Task with given id", response = InboxTask.class)
    @GetMapping(path = "{id}", produces = {"application/json"})
    public InboxTask getInfo(
            @ApiParam(value = "Inbox Task's id")
            @PathVariable("id") String id,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return inboxTaskDAO.get(id, selects);
    }

    @ApiOperation(value = "Provides information regarding several objects of type Inbox Task by given ids", response = InboxTask.class)
    @GetMapping(produces = {"application/json"})
    public List<DBObject> getInfo(
            @ApiParam(value = "Inbox Tasks' ids")
            @RequestParam("ids") String[] ids,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return inboxTaskDAO.getInfo(ids, selects);
    }

    @ApiOperation(value = "Get all assigned to context user Inbox Tasks", response = List.class)
    @GetMapping(path = "my", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getMyTasks(
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return inboxTaskDAO.getMyTasks(selects);
    }

    @ApiOperation(value = "Get all Route Templates", response = List.class)
    @GetMapping(path = "routeTemplates", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getRouteTemplates(
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) {
        return inboxTaskDAO.getRouteTemplates(selects);
    }
}
