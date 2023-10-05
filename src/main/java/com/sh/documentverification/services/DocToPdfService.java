package com.sh.documentverification.services;


import com.spire.doc.Document;
import com.spire.doc.PictureWatermark;
import com.spire.doc.PrivateFontPath;
import com.spire.doc.ToPdfParameterList;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Service
public class DocToPdfService {


    public void DocToPdf(MultipartFile file) throws IOException {

        Document doc = new Document(file.getInputStream());
        doc.setEmbedFontsInFile(true);

        //폰트 적용
        PrivateFontPath fontPath = new PrivateFontPath("malgun", "malgun.ttf");
        ToPdfParameterList ppl = new ToPdfParameterList();
        List pathList = new LinkedList<>();
        pathList.add(fontPath);
        ppl.setPrivateFontPaths(pathList);
        ppl.isEmbeddedAllFonts(true);

        // 이미지 워터마크
        PictureWatermark pictureWatermark = new PictureWatermark();
        pictureWatermark.setPicture("tilon.png");
        pictureWatermark.setScaling(100);
        pictureWatermark.isWashout(false);
        doc.setWatermark(pictureWatermark);

        String originalFileName = file.getOriginalFilename();
        String pdfFileName = "/home/sh/Downloads/shatest/" + originalFileName.substring(0, originalFileName.lastIndexOf('.')) + ".pdf";

        doc.saveToFile(pdfFileName, ppl);



    }
}
