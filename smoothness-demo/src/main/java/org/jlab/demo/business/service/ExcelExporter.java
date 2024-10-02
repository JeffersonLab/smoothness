package org.jlab.demo.business.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jlab.demo.persistence.entity.Movie;

/**
 * @author ryans
 */
public class ExcelExporter {
  public void export(OutputStream out, List<Movie> movieList) throws IOException {
    Workbook wb = new XSSFWorkbook();
    Sheet sheet1 = wb.createSheet("Test Excel");

    Row row1 = sheet1.createRow((short) 0);
    row1.createCell(0).setCellValue("ID");
    row1.createCell(1).setCellValue("Release Date");
    row1.createCell(2).setCellValue("MPAA Rating");
    row1.createCell(3).setCellValue("Title");
    row1.createCell(4).setCellValue("Description");
    row1.createCell(5).setCellValue("Duration (Minutes)");

    CreationHelper createHelper = wb.getCreationHelper();
    CellStyle dateStyle = wb.createCellStyle();
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));

    for (int i = 1; i <= movieList.size(); i++) {
      Row row = sheet1.createRow((short) i);

      Movie movie = movieList.get(i - 1);

      row.createCell(0).setCellValue(movie.getMovieId().longValue());

      Cell c1 = row.createCell(1);
      c1.setCellValue(movie.getReleaseDate());
      c1.setCellStyle(dateStyle);

      row.createCell(2).setCellValue(movie.getMpaaRating());
      row.createCell(3).setCellValue(movie.getTitle());
      row.createCell(4).setCellValue(movie.getDescription());
      row.createCell(5).setCellValue(movie.getDurationMinutes());
    }

    sheet1.autoSizeColumn(0);
    sheet1.autoSizeColumn(1);
    sheet1.autoSizeColumn(2);
    sheet1.autoSizeColumn(3);
    sheet1.autoSizeColumn(4);
    sheet1.autoSizeColumn(5);

    wb.write(out);
  }
}
