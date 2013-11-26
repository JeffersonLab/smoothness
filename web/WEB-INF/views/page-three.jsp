<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Page Three"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            .left-wrap-cell {
                width: 199px;
                vertical-align: top;
            }
            .left-nested-table {
                width: 199px;
            }
            .right-wrap-cell {
                width: 599px;
            }
            table {
                table-layout: fixed;
                border-collapse: collapse;
                border: 1px solid black;
            }
            th,
            td {
                font-size: 16px;
                border: 1px solid black;
                height: 24px;
                padding: 2px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            td.wrap-cell {
                padding: 0;
            }
            tr.left-header th {
                padding: 0;
                height: 60px;
                border-bottom: 2px solid black;
            }
            .decorator-panel {
                border: 1px solid gray;
                box-shadow: 0 -1px 1px #666666, -1px 0 2px #666666, 0 1px 1px rgba(255, 255, 255, 0.8) inset, -1px 0 1px rgba(0, 0, 0, 0.8) inset;
                margin: 3px;
            }
            .scroll-panel {
                overflow-x: scroll;
                width: 599px;
            }
            .right-nested-table .bottom-header th,
            .right-nested-table td {
                width: 200px;
                min-width: 200px;
                max-width: 200px;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
        <script type="text/javascript">
        </script>
    </jsp:attribute>        
    <jsp:body>
        <section class="scrollable-section">
            <h2><c:out value="${title}"/></h2>
            <table class="wrap-table">
                <tbody>
                    <tr>
                        <td class="wrap-cell left-wrap-cell">
                            <table class="left-nested-table">
                                <tbody>
                                    <tr class="left-header">
                                        <th>Username</th>
                                    </tr>
                                    <tr>
                                        <th>Smith, White, Thompson, Jenkins</th>
                                    </tr>
                                    <tr>
                                        <th>Brown</th>
                                    </tr>
                                    <tr>
                                        <th>Johnson</th>
                                    </tr>
                                    <tr>
                                        <th>Carter</th>
                                    </tr>
                                </tbody>
                            </table>
                        </td>
                        <td class="wrap-cell right-wrap-cell">
                            <div class="decorator-panel">
                                <div class="scroll-panel">
                                    <table class="right-nested-table">
                                        <thead>
                                            <tr>
                                                <th colspan="6">Workgroup</th>
                                            </tr>
                                            <tr class="bottom-header">
                                                <th>PD</th>
                                                <th>Operator</th>
                                                <th>Crew Chief</th>
                                                <th>Operability Manager</th>
                                                <th>LASO</th>
                                                <th>HLA Group Leader</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>Apple, Orange, Banana, Pear, Grape, Peach</td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td>B</td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td>C</td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td>D</td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </section>
    </jsp:body>         
</t:page>
