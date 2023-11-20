package com.igatec.bop.controllers;

import com.igatec.bop.core.model.Product;
import com.igatec.bop.services.ProductService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.igatec.bop.utils.Constants.DEFAULT_VAL;
import static com.igatec.bop.utils.Constants.PARAM_RECURSE_TO_LEVEL;
import static com.igatec.bop.utils.Constants.PARAM_SELECTS;

@RestController
@RequestMapping(path = "api/resources")
@Transactional
@Api(tags = {"Product"})
@SwaggerDefinition(tags = {
        @Tag(name = "Product", description = "provides rest service to get information about objects of type 'VPMReference'")//todo description doesn't appear
})
public class ProductController {

    @Autowired
    ProductService productService;

    @ApiOperation(value = "Provides information regarding VPMReference with given id", response = Product.class)
    @GetMapping(path = "{id}", produces = {"application/json"})
    public Product getInfo(
            @ApiParam(value = "VPMReference's id")
            @PathVariable("id") String id,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return productService.getInfo(id, selects);
    }

    @ApiOperation(value = "Provides information regarding several objects of type VPMReference by given ids", response = Product.class)
    @GetMapping(produces = {"application/json"})
    public List<DBObject> getInfo(
            @ApiParam(value = "VPMReferences' ids")
            @RequestParam("ids") String[] ids,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects) throws FrameworkException {
        return productService.getInfo(ids, selects);
    }

    @ApiOperation(value = "Expand structure tree for Product", response = List.class)
    @GetMapping(path = "{id}/structure", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<DBObject> getStructure(
            @ApiParam(value = "Product id")
            @PathVariable("id") Product product,
            @ApiParam(value = "Parent id in the tree")
            @RequestParam("parentId") String parentId,
            @ApiParam(value = "comma separated list with MQL object selects")
            @RequestParam(name = PARAM_SELECTS, defaultValue = DEFAULT_VAL) Selector selects,
            @ApiParam(value = "level to expand. 0 - stands for all")
            @RequestParam(name = PARAM_RECURSE_TO_LEVEL, defaultValue = "1") short recurseToLevel) {
        return productService.getStructure(product, parentId, selects, recurseToLevel);
    }
}
