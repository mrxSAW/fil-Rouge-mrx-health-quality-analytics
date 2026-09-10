package org.example.healthcarequalite.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.healthcarequalite.entity.Incident;
import org.example.healthcarequalite.repository.IncidentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class IncidentExcelService {

    private final IncidentRepository incidentRepository;

    public IncidentExcelService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generateReport( Long departmentId, LocalDate startDate, LocalDate endDate) throws IOException {

        if (departmentId != null && departmentId <= 0) {
            throw new IllegalArgumentException( "L'identifiant du département doit être positif" );
        }

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) { throw new IllegalArgumentException(
                        "La date de début doit être antérieure ou égale à la date de fin" );
            }
        }

        Sort sort = Sort.by( Sort.Direction.DESC, "incidentDate", "id" );

        List<Incident> incidents = incidentRepository.findForReport(departmentId, startDate, endDate, sort );

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Incidents");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor( IndexedColors.DARK_BLUE.getIndex() );
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setWrapText(true);
            textStyle.setVerticalAlignment(VerticalAlignment.TOP);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat( workbook.createDataFormat().getFormat("dd/mm/yyyy") );

            String[] headers = {"Identifiant", "Titre", "Description", "Date", "Type", "Gravité", "Statut", "Département"};

            Row headerRow = sheet.createRow(0);

            for (int column = 0; column < headers.length; column++) {
                Cell cell = headerRow.createCell(column);
                cell.setCellValue(headers[column]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;

            for (Incident incident : incidents) {

                Row row = sheet.createRow(rowIndex);
                row.setHeightInPoints(45);

                // Texte pour conserver exactement les identifiants longs.
                row.createCell(0).setCellValue(
                        String.valueOf(incident.getId())
                );

                Cell titleCell = row.createCell(1);
                titleCell.setCellValue(incident.getTitle());
                titleCell.setCellStyle(textStyle);

                Cell descriptionCell = row.createCell(2);
                descriptionCell.setCellValue(incident.getDescription());
                descriptionCell.setCellStyle(textStyle);

                Cell dateCell = row.createCell(3);

                if (incident.getIncidentDate() != null) {
                    dateCell.setCellValue(incident.getIncidentDate().atStartOfDay());
                    dateCell.setCellStyle(dateStyle);
                }

                if (incident.getType() != null) {
                    row.createCell(4).setCellValue(incident.getType().name());
                }

                if (incident.getGravity() != null) {
                    row.createCell(5).setCellValue(incident.getGravity().name());
                }

                if (incident.getStatus() != null) {
                    row.createCell(6).setCellValue(incident.getStatus().name());
                }

                if (incident.getDepartment() != null) {
                    Cell departmentCell = row.createCell(7);
                    departmentCell.setCellValue(incident.getDepartment().getName());
                    departmentCell.setCellStyle(textStyle);
                }

                rowIndex++;
            }

            sheet.setColumnWidth(0, 15 * 256);
            sheet.setColumnWidth(1, 35 * 256);
            sheet.setColumnWidth(2, 60 * 256);
            sheet.setColumnWidth(3, 15 * 256);
            sheet.setColumnWidth(4, 22 * 256);
            sheet.setColumnWidth(5, 15 * 256);
            sheet.setColumnWidth(6, 18 * 256);
            sheet.setColumnWidth(7, 30 * 256);

            sheet.createFreezePane(0, 1);

            sheet.setAutoFilter(
                    new CellRangeAddress(0, rowIndex - 1, 0, 7)
            );

            workbook.write(output);

            return output.toByteArray();
        }
    }
}