package com.igatec.bop.core.dao;

import com.igatec.bop.core.model.Product;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.annotations.MatrixTransactional;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.common.dao.DBObjectDAO;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import lombok.SneakyThrows;
import matrix.util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.igatec.bop.utils.Constants.*;

@Repository
@MatrixTransactional
@ObjectDAO("Product")
public class ProductDAO extends DBObjectDAO<Product> {
    private InstructionDAO instructionDAO;

    @Autowired
    public void setInstructionDAO(@ObjectDAO("Instruction") InstructionDAO instructionDAO) {
        this.instructionDAO = instructionDAO;
    }

    /**
     * Get all connected to given Product child Physical Products
     *
     * @param product        Product object id
     * @param selects        Comma separated list with object selects
     * @param recurseToLevel recurse level for expand
     */
    @SneakyThrows
    public List<DBObject> getStructure(Product product, String parentId, Selector selects, short recurseToLevel) {
        StringList objSelects = Util.getDefaultObjSelects();
        objSelects.addAll(selects.toStringList());
        StringList relSelects = Util.getDefaultRelSelects();

        List<DBObject> list = getRelatedObjects(product, RELATIONSHIP_VPM_INSTANCE, TYPE_VPM_REFERENCE,
                objSelects, relSelects, false, recurseToLevel, null, null, 0);

        Util.extendData(getContext(), list, parentId, RELATIONSHIP_VPM_INSTANCE, selects);

        String sTreeOrder = String.format("from[%s].attribute[%s]", RELATIONSHIP_VPM_INSTANCE, ATTRIBUTE_V_TREE_ORDER);
        return Util.sortByTreeOrder(list, sTreeOrder);
    }
}
