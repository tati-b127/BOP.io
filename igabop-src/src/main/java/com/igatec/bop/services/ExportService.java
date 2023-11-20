package com.igatec.bop.services;

import com.igatec.bop.core.dao.HeaderOperationDAO;
import com.igatec.bop.core.dao.InstructionDAO;
import com.igatec.bop.core.dao.PPRContextDAO;
import com.igatec.bop.core.report.Attacher;
import com.igatec.bop.core.report.ConfigurableSource;
import com.igatec.bop.core.report.FopConvertor;
import com.igatec.bop.core.report.ParamProcessor;
import com.igatec.bop.core.report.quickstruct.QSDocument;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import lombok.extern.slf4j.Slf4j;
import matrix.db.Context;
import org.apache.fop.apps.FormattingResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class represents methods to export pdf report
 */
@Service
@Slf4j
public class ExportService {

    @Autowired
    @ObjectDAO("PPRContext")
    PPRContextDAO pprContextDAO;

    @Autowired
    @ObjectDAO("HeaderOperation")
    HeaderOperationDAO headerOperationDAO;

    @Autowired
    @ObjectDAO("Instruction")
    InstructionDAO instructionDAO;

    @Value("${report.organization}")
    private String organizationTitle;

    /**
     * @param pprcId  id pprContext
     * @param selId
     * @param selects selector
     * @return list of DBObject
     */
    public List<DBObject> getPdf(String pprcId,
                                 String selId,
                                 Selector selects
    ) throws Exception {
        Context context = pprContextDAO.getContext();
        String xmlText;

        log.info("Start getting pdf");

        ParamProcessor paramProcessor = new ParamProcessor(pprContextDAO, pprcId, selId);

        if (paramProcessor.getError() != null) {
            log.error(paramProcessor.getError());
            return new ArrayList<DBObject>();
        }

        //======= Make XML
        QSDocument qsDoc = new QSDocument(context, paramProcessor.getPprc().getPhysicalid(), paramProcessor.getSys().getPhysicalid());
        qsDoc.traverseStructure();
        xmlText = qsDoc.convertToXML();

        String xmlTextNoHeader = xmlText.substring(xmlText.indexOf("?>") + 2);

        //======= Store XML for debug
        storeToFile(System.getProperty("user.dir") + File.separator + "XPDMXML.xml", xmlText.getBytes("UTF-8"));

        //======= Make PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        FopConvertor fopConvertor = new FopConvertor();

        String imageDirUrl = qsDoc.getCheckoutURL();

        ConfigurableSource xslConfSource = new ConfigurableSource("espd/_espd_compiled.xsl");
        xslConfSource.setConfig("IMAGE_DIR_URL", imageDirUrl);
        xslConfSource.setConfig("ORGANIZATION", organizationTitle);

        FormattingResults fres = fopConvertor.convert(xslConfSource, xmlTextNoHeader, outputStream);

        byte[] pdfData = outputStream.toByteArray();

        storeToFile(System.getProperty("user.dir") + File.separator + "report.pdf", pdfData);

        String fileName = String.format("%s_TD.pdf", qsDoc.getFactoryTitle());

        String factoryPID = paramProcessor.getSys().getPhysicalid();

        //======= Remove old Reports
        Attacher.removeOldReports(context, factoryPID, fileName);

        //======= Attach PDF
        Attacher.attachFile(context, factoryPID, fileName, pdfData);

        log.info(String.format(
                "Report was attached to document with id %s. Report name is %s.", factoryPID, fileName));

        List<String> pids = new ArrayList<>();
        pids.add(factoryPID);
        return Util.extendDataPro(context, pids, null, null, selects);
    }

    private void storeToFile(String filePath, byte[] data) throws IOException {
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
        stream.write(data);
        stream.close();
    }
}
