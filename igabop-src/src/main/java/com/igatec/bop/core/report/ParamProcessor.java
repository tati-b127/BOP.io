package com.igatec.bop.core.report;

import com.igatec.bop.core.dao.PPRContextDAO;
import com.igatec.bop.core.model.GeneralSystem;
import com.igatec.bop.core.model.PPRContext;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.matrixone.apps.domain.util.FrameworkException;
import lombok.Getter;
import matrix.db.Context;

import java.util.List;

@Getter
public class ParamProcessor {

    String error = null;
    PPRContext pprc = null;
    GeneralSystem sys = null;

    //================================================

    public ParamProcessor(PPRContextDAO pprContextDAO, String pprcId, String selId ) throws FrameworkException {

        Context context = pprContextDAO.getContext();
        Selector selector = new Selector.Builder().attribute( "PLMEntity.V_Name" ).build();

        DBObject object = Attacher.getInfo( context, selId, selector);
        if( object == null ) {
            error = "Объект не найден - " + selId;
            return;
        }

        String type = object.getBasics().getType().getName();
        if( type.equals("PPRContext") ) {
            pprc = (PPRContext)object;
            List<DBObject> systems = pprContextDAO.getSystems(pprc, null, selector);
            if( systems.size() > 1 ) {
                error = "В этом PPRContext " + systems.size() + " предприятий. Выберите одно.";
                return;
            }
            if( systems.size() == 0 ) {
                error = "В этом PPRContext нет ни одного предприятия.";
                return;
            }
            sys = (GeneralSystem)systems.get(0);
        }
        else if( type.equals("Kit_Factory") ) {
            sys = (GeneralSystem)object;
            pprc = (PPRContext)Attacher.getInfo( context, pprcId, selector);
        }
        else {
            error = "Выберите предприятие";
            return;
        }
    }
}
