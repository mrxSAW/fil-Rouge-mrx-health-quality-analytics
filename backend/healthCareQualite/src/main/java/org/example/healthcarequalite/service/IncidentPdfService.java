package org.example.healthcarequalite.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.example.healthcarequalite.entity.Incident;
import org.example.healthcarequalite.repository.IncidentRepository;
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
public class IncidentPdfService {

    private final IncidentRepository incidentRepository;

    public IncidentPdfService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generateReport(
            Long departmentId,
            LocalDate startDate,
            LocalDate endDate) throws IOException {

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
                "incidentDate",
                "id"
        );

        List<Incident> incidents = incidentRepository.findForReport(
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
        addText(lines, "Rapport des incidents", font);

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

        addText(
                lines,
                "Nombre d'incidents : " + incidents.size(),
                font
        );

        lines.add("");

        if (incidents.isEmpty()) {
            addText(lines, "Aucun incident pour les filtres demandes.", font);
        }

        for (Incident incident : incidents) {

            addText(lines, "Incident N° " + incident.getId(), font);
            addText(lines, "Titre : " + incident.getTitle(), font);

            String date = "Non renseignee";

            if (incident.getIncidentDate() != null) {
                date = incident.getIncidentDate().format(formatter);
            }

            addText(lines, "Date : " + date, font);
            addText(lines, "Type : " + incident.getType(), font);
            addText(lines, "Gravite : " + incident.getGravity(), font);
            addText(lines, "Statut : " + incident.getStatus(), font);

            String departmentName = "Non renseigne";

            if (incident.getDepartment() != null) {
                departmentName = incident.getDepartment().getName();
            }

            addText(lines, "Departement : " + departmentName, font);

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