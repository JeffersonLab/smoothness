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
            table.horizontal-scroll-table,
            .horizontal-scroll-table table {
                table-layout: fixed;
                border-collapse: collapse;
                border: 1px solid black;
            }
            .horizontal-scroll-table th,
            .horizontal-scroll-table td {
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
                text-align: center;
            }
            .left-nested-table tbody tr:nth-child(2n),
            .right-nested-table tbody tr:nth-child(2n) {
                background-color: #E8E8E8;
            }
            .horizontal-scroll-table thead th {
                background-color: lightgray;
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
            <table class="horizontal-scroll-table">
                <tbody>
                    <tr>
                        <td class="wrap-cell left-wrap-cell">
                            <table class="left-nested-table">
                                <thead>
                                    <tr class="left-header">
                                        <th>Username</th>
                                    </tr>    
                                </thead>
                                <tbody>
                                    <tr>
                                        <th>jsmith</th>
                                    </tr>
                                    <tr>
                                        <th>kbrown</th>
                                    </tr>
                                    <tr>
                                        <th>pjohnson</th>
                                    </tr>
                                    <tr>
                                        <th>mcarter</th>
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
                                                <td></td>
                                                <td>✔</td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td>✔</td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td>✔</td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                                <td>✔</td>
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
