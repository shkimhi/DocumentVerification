package com.sh.documentverification.services;


import com.spire.doc.*;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.ShapeLineStyle;
import com.spire.doc.documents.ShapeType;
import com.spire.doc.documents.WatermarkLayout;
import com.spire.doc.fields.ShapeObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
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
/*
        PictureWatermark pictureWatermark = new PictureWatermark();
        pictureWatermark.setPicture("tilon.png");
        pictureWatermark.setScaling(100);
        pictureWatermark.isWashout(false);
        doc.setWatermark(pictureWatermark);

        Section section = doc.getSections().get(0);
        TextWatermark textWatermark = new TextWatermark();
        textWatermark.setText("Tilon CenterCahin");
        textWatermark.setFontSize(50);
        textWatermark.setColor(Color.red);
        textWatermark.setLayout(WatermarkLayout.Diagonal);

        section.getDocument().setWatermark(textWatermark);
*/

        ShapeObject shape = new ShapeObject(doc, ShapeType.Text_Plain_Text);
        shape.setWidth(60);
        shape.setHeight(20);
        shape.setVerticalPosition(30);
        shape.setHorizontalPosition(20);
        shape.setRotation(315);
        shape.getWordArt().setText("Tilon");
        shape.setFillColor(new Color(255,53,20,50));
        shape.setLineStyle(ShapeLineStyle.Single);
        shape.setStrokeColor(new Color(255,53,20,50));
        shape.setStrokeWeight(1);

        Section section;
        HeaderFooter header;
        for (int n = 0; n < doc.getSections().getCount(); n++) {
            section = doc.getSections().get(n);
            header = section.getHeadersFooters().getHeader();
            Paragraph paragraph1;
            for (int i = 0; i < 4; i++) {
                paragraph1 = header.addParagraph();
                for (int j = 0; j < 3; j++) {
                    shape = (ShapeObject) shape.deepClone();
                    shape.setVerticalPosition(50 + 150 * i);
                    shape.setHorizontalPosition(20 + 160 * j);
                    paragraph1.getChildObjects().add(shape);
                }
            }
        }

        String originalFileName = file.getOriginalFilename();
        String pdfFileName = "/home/sh/Downloads/shatest/" + originalFileName.substring(0, originalFileName.lastIndexOf('.')) + ".pdf";

        doc.saveToFile(pdfFileName, ppl);



    }
}
