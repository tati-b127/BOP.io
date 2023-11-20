package com.igatec.bop.services;

import com.igatec.bop.core.dao.ProductDAO;
import com.igatec.bop.core.model.Product;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.domain.util.FrameworkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class represents methods to work with object Product
 */
@Service
public class ProductService {

    @Autowired
    @ObjectDAO("Product")
    ProductDAO productDAO;

    /**
     * Gets information
     *
     * @param id      object id
     * @param selects selector
     * @return list of DBObject
     */
    public Product getInfo(String id, Selector selects) throws FrameworkException {
        return productDAO.get(id, selects);
    }

    /**
     * Gets information
     *
     * @param ids     array of object id
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> getInfo(String[] ids,
                                  Selector selects
    ) throws FrameworkException {
        return productDAO.getInfo(ids, selects);
    }

    /**
     * Get all connected to given Product child Physical Products
     *
     * @param product        Product object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     * @return list of DBObject
     */
    public List<DBObject> getStructure(Product product,
                                       String parentId,
                                       Selector selects,
                                       short recurseToLevel) {
        return productDAO.getStructure(product, parentId, selects, recurseToLevel);
    }
}
