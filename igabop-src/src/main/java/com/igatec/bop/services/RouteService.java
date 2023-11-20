package com.igatec.bop.services;

import com.igatec.bop.core.dao.RouteDAO;
import com.igatec.bop.core.dto.RouteDTO;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.annotations.MatrixTransactional;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.common.core.StandardSelectors;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.igatec.bop.utils.Constants.RELATIONSHIP_OBJECT_ROUTE;
import static com.igatec.utilsspring.utils.common.core.StandardSelectors.DEFAULT;

/**
 * Class represents mwthods to work with object Route
 */
@Service
public class RouteService {

    @Autowired
    @ObjectDAO("Route")
    RouteDAO routeDAO;

    /**
     * Creates Route
     *
     * @param pBody   contains client info required to creation (Route Template id, my Inbox Task id and assignee)
     * @param selects Comma separated list with object selects
     * @return list of DBObject
     */
    public List<DBObject> createRoute(String pBody, Selector selects) {
        return routeDAO.createRoute(pBody, selects);
    }

    /**
     * Add Route content
     *
     * @param selects Comma separated list with object selects
     * @param pBody   contains client info required to content adding (selected Route and objects as content)
     * @return list of DBObject
     */
    public List<DBObject> addContent(String pBody, Selector selects) {
        return routeDAO.addContent(pBody, selects);
    }

    /**
     * Delete Route content
     *
     * @return list of DBObject
     */
    @SneakyThrows
    public List<DBObject> deleteContent(List<RouteDTO> routeDTOs, Selector selects) {
        List<String> content = new ArrayList<>();
        for (RouteDTO routeDto : routeDTOs) {
            routeDAO.disconnectContent(routeDto.getRoute(), routeDto.getContent(), getSelector());
            content.addAll(routeDto.getContent());
        }
        return Util.extendDataPro(routeDAO.getContext(), content, null, null, selects);
    }

    private Selector getSelector() {
        return new Selector.Builder()
                .fromRelObjectSelector(RELATIONSHIP_OBJECT_ROUTE, DEFAULT)
                .fromRelSelector(RELATIONSHIP_OBJECT_ROUTE, DEFAULT)
                .build();
    }
}
