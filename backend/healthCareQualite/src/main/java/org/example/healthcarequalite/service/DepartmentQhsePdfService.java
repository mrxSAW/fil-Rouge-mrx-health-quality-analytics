package org.example.healthcarequalite.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.example.healthcarequalite.dto.statistics.DepartmentQhseReportDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class DepartmentQhsePdfService {

    private final StatisticsService statisticsService;

    public DepartmentQhsePdfService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public byte[] generateReport(Long departmentId) throws IOException {

        DepartmentQhseReportDTO report = statisticsService.getDepartmentQhseReport(departmentId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String generationDate = LocalDate.now().format(formatter);

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            PDType1Font textFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {

                content.beginText();
                content.setFont(titleFont, 18);
                content.newLineAtOffset(50, 780);
                content.showText("Rapport QHSE par département");
                content.endText();

                content.beginText();
                content.setFont(textFont, 12);
                content.setLeading(25);
                content.newLineAtOffset(50, 735);

                content.showText("Health Quality Analytics");
                content.newLine();

                content.showText("Identifiant du département : " + report.getDepartmentId());
                content.newLine();

                content.showText("Date de génération : " + generationDate);
                content.newLine();

                content.showText("Périmètre : toutes les données du département");
                content.newLine();
                content.newLine();

                content.showText("Incidents critiques : " + report.getCriticalIncidents());
                content.newLine();

                content.showText("Critères non conformes : " + report.getNonCompliantCriteria());
                content.newLine();

                content.showText("Actions en retard : " + report.getOverdueActions());
                content.newLine();

                content.showText("Nombre total de critères : " + report.getTotalCriteria());
                content.newLine();

                content.showText("Critères conformes : " + report.getCompliantCriteria());
                content.newLine();

                content.showText("Taux de conformité : " + formatConformityRate(report.getConformityRate()));
                content.newLine();

                content.showText("Score de risque : " + String.format(
                                Locale.FRANCE, "%.2f",
                                report.getRiskScore()
                        )
                );
                content.newLine();
                content.newLine();

                content.showText("Les actions terminées ou annulées sont exclues des retards.");
                content.newLine();

                content.showText("Le score de risque n'est pas un pourcentage.");

                content.endText();
            }

            document.save(output);

            return output.toByteArray();
        }
    }

    private String formatConformityRate(Double conformityRate) {

        if (conformityRate == null) {
            return "Non calculable";
        }

        return String.format(Locale.FRANCE, "%.2f %%", conformityRate);
    }
}