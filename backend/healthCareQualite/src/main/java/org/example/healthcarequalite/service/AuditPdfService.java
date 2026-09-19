package org.example.healthcarequalite.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.example.healthcarequalite.entity.Audit;
import org.example.healthcarequalite.repository.AuditRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service

public class AuditPdfService {

    private final AuditRepository auditRepository;

    public AuditPdfService(AuditRepository auditRepository) {

        this.auditRepository = auditRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generateReport( Long departmentId, LocalDate startDate, LocalDate endDate) throws IOException {

        if (departmentId != null && departmentId <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du département doit être positif"
            );
        }

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException(
                        "La date de début doit être antérieure ou égale à la date de fin"
                );
            }
        }

        Sort sort = Sort.by(
                Sort.Direction.DESC,
                "auditDate",
                "id"
        );

        List<Audit> audits = auditRepository.findForReport(
                departmentId,
                startDate,
                endDate,
                sort
        );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        PDType1Font font = new PDType1Font(
                Standard14Fonts.FontName.HELVETICA
        );

        List<String> lines = new ArrayList<>();

        addText(lines, "Health Quality Analytics", font);
        addText(lines, "Rapport des audits", font);

        addText(
                lines,
                "Date de generation : " + LocalDate.now().format(formatter),
                font
        );

        if (departmentId != null) {
            addText(
                    lines,
                    "Departement filtre : " + departmentId,
                    font
            );
        }

        if (startDate != null) {
            addText(
                    lines,
                    "Date de debut : " + startDate.format(formatter),
                    font
            );
        }

        if (endDate != null) {
            addText(
                    lines,
                    "Date de fin : " + endDate.format(formatter),
                    font
            );
        }

        addText(lines, "Nombre d'audits : " + audits.size(), font);
        lines.add("");

        if (audits.isEmpty()) {
            addText(lines, "Aucun audit pour les filtres demandes.", font);
        }

        for (Audit audit : audits) {

            addText(lines, "Audit N° " + audit.getId(), font);
            addText(lines, "Titre : " + audit.getTitle(), font);

            String date = "Non renseignee";

            if (audit.getAuditDate() != null) {
                date = audit.getAuditDate().format(formatter);
            }

            addText(lines, "Date : " + date, font);
            addText(lines, "Score : " + displayValue(audit.getScore()), font);

            addText(
                    lines,
                    "Criteres evalues : " + displayValue(audit.getTotalCriteria()),
                    font
            );

            addText(
                    lines,
                    "Criteres conformes : "
                            + displayValue(audit.getCompliantCriteria()),
                    font
            );

            String conformityRate = "Non renseigne";

            if (audit.getConformityRate() != null) {
                conformityRate = audit.getConformityRate() + " %";
            }

            addText(lines, "Taux de conformite : " + conformityRate, font);

            String departmentName = "Non renseigne";

            if (audit.getDepartment() != null) {
                departmentName = audit.getDepartment().getName();
            }

            addText(lines, "Departement : " + departmentName, font);

            if (audit.getObservations() != null
                    && !audit.getObservations().isBlank()) {
                addText(
                        lines,
                        "Observations : " + audit.getObservations(),
                        font
                );
            }

            lines.add("");
        }

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            int linesPerPage = 42;
            int pageNumber = 1;

            for (int start = 0; start < lines.size(); start += linesPerPage) {

                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                int end = Math.min(start + linesPerPage, lines.size());

                try (PDPageContentStream content =
                             new PDPageContentStream(document, page)) {

                    content.beginText();
                    content.setFont(font, 11);
                    content.setLeading(17);
                    content.newLineAtOffset(50, 790);

                    for (int index = start; index < end; index++) {
                        content.showText(lines.get(index));
                        content.newLine();
                    }

                    content.endText();

                    content.beginText();
                    content.setFont(font, 10);
                    content.newLineAtOffset(50, 30);
                    content.showText("Page " + pageNumber);
                    content.endText();
                }

                pageNumber++;
            }

            document.save(output);

            return output.toByteArray();
        }
    }

    private String displayValue(Integer value) {

        if (value == null) {
            return "Non renseigne";
        }

        return value.toString();
    }

    private void addText(List<String> lines, String text, PDType1Font font) throws IOException {

        StringBuilder currentLine = new StringBuilder();

        for (int index = 0; index < text.length();) {

            int codePoint = text.codePointAt(index);
            index += Character.charCount(codePoint);

            String character = new String(Character.toChars(codePoint));

            if (Character.isWhitespace(codePoint)) {
                character = " ";
            }

            try {
                font.encode(character);
            } catch (IllegalArgumentException exception) {
                character = "?";
            }

            String candidate = currentLine.toString() + character;
            float width = font.getStringWidth(candidate) / 1000 * 11;

            if (width > 490 && currentLine.length() > 0) {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
            }

            currentLine.append(character);
        }

        lines.add(currentLine.toString());
    }
}