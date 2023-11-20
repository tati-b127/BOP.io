package com.igatec.bop.controllers;

import com.igatec.bop.services.ExportService;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.igatec.bop.utils.Constants.DEFAULT_VAL;
import static com.igatec.bop.utils.Constants.PARAM_SELECTS;

@RestController
@RequestMapping(path = "api/export")
@Transactional
@Api(tags = {"PdfExport"})
@SwaggerDefinition(tags = {
        @Tag(name = "PdfExport", description = "provides rest service to get XML representation of PPR")//todo description doesn't appear
})
public class ExportController {

    @Autowired
     ExportService exportService;

    @GetMapping(path = "{pprcId}/pdf/{selId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getPdf(
            @ApiParam(value = "PPRContext's id")
            @PathVariable("pprcId") String pprcId,
            @ApiParam(value = "Selectd object's id")
            @PathVariable("selId") String selId,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects
    ) throws Exception {
        return exportService.getPdf(pprcId, selId, selects);
    }
}
