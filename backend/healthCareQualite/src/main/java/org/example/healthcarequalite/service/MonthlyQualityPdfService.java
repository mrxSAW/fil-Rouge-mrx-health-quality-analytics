package org.example.healthcarequalite.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.example.healthcarequalite.dto.statistics.MonthlyQualityReportDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class MonthlyQualityPdfService {

    private final StatisticsService statisticsService;

    public MonthlyQualityPdfService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public byte[] generateReport(Integer year, Integer month) throws IOException {

        MonthlyQualityReportDTO report = statisticsService.getMonthlyQualityReport(year, month);

        String period = String.format(Locale.FRANCE, "%02d/%04d", month, year);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String generationDate = LocalDate.now().format(formatter);

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            PDType1Font textFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {

                content.beginText();
                content.setFont(titleFont, 18);
                content.newLineAtOffset(50, 780);
                content.showText("Rapport qualité mensuel");
                content.endText();

                content.beginText();
                content.setFont(textFont, 12);
                content.setLeading(25);
                content.newLineAtOffset(50, 735);

                content.showText("Health Quality Analytics");
                content.newLine();

                content.showText("Période : " + period);
                content.newLine();

                content.showText("Date de génération : " + generationDate);
                content.newLine();

                content.showText("Périmètre : tous les départements");
                content.newLine();
                content.newLine();

                content.showText("Nombre d'incidents : " + report.getTotalIncidents());
                content.newLine();

                content.showText("Incidents critiques : " + report.getCriticalIncidents());
                content.newLine();

                content.showText(
                        "Nombre d'audits : " + report.getTotalAudits()
                );
                content.newLine();

                content.showText(
                        "Taux de conformité : "
                                + formatValue(report.getConformityRate(), " %")
                );
                content.newLine();

                content.showText(
                        "Score qualité : "
                                + formatValue(report.getQualityScore(), " / 100")
                );
                content.newLine();
                content.newLine();

                content.showText("Les indicateurs portent uniquement sur le mois sélectionné.");
                content.newLine();

                content.showText("Non calculable : données insuffisantes pour cet indicateur.");

                content.endText();
            }

            document.save(output);

            return output.toByteArray();
        }
    }

    private String formatValue(Double value, String suffix) {

        if (value == null) {
            return "Non calculable";
        }

        return String.format(Locale.FRANCE, "%.2f", value) + suffix;
    }






}