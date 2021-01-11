package org.jlab.demo.presentation.controller.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ryans
 */
@WebServlet(name = "ReportThree", urlPatterns = {"/reports/report-three"})
public class ReportThree extends HttpServlet {

    public class LegendData {
        private final String name;
        private final int count;
        private final String color;
        
        public LegendData(String name, int count, String color) {
            this.name = name;
            this.count = count;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        public String getColor() {
            return color;
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<LegendData> legendDataList = new ArrayList<>();

        legendDataList.add(new LegendData("Series 1", 1, "blue"));
        legendDataList.add(new LegendData("Series 2", 2, "red"));
        legendDataList.add(new LegendData("Series 3", 3, "green"));
        legendDataList.add(new LegendData("Series 4", 4, "yellow"));

        int totalCount = 10;

        List<String> footnoteList = new ArrayList<>();
        
        footnoteList.add("Excludes holidays");
        footnoteList.add("Includes first, second, third, and fourth series types");
        
        request.setAttribute("legendDataList", legendDataList);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("footnoteList", footnoteList);
        
        request.getRequestDispatcher("/WEB-INF/views/reports/report-three.jsp").forward(request,
                response);
    }
}
