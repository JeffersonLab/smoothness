<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Page Four"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            .flyout-table td a:first-child {
                cursor: pointer;
                color: blue;
            }
            .flyout-table td a:first-child:hover {
                color: red;
            }
            .flyout-panel {
                border: 1px solid black;
                width: 400px;
                height: 300px;
                position: absolute;
                right: -448px;
                top: -177px;
                z-index: 2;
                background-color: white;
                border-radius: 0.5em;
                box-shadow: 0.5em 0.5em 0.5em #979797;
                padding: 16px;
            }
            .flyout-handle {
                position: relative;
            }
            .flyout-panel:after {
                content: '';
                width: 0; 
                height: 0; 
                border-top: 20px solid transparent;
                border-bottom: 20px solid transparent; 
                border-right: 20px solid white; 
                top: 50%;
                margin-top: -20px;
                position: absolute;
                left: -20px;
            }
            .flyout-panel:before {
                content: '';
                width: 0; 
                height: 0; 
                border-top: 21px solid transparent;
                border-bottom: 21px solid transparent; 
                border-right: 21px solid black; 
                top: 50%;
                margin-top: -21px;
                position: absolute;
                left: -21px;
            }   
            .close-bubble {
                float: right;
            }
            .flyout-panel .key-value-list {
                clear: both;
                margin-top: 3em;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript">
            $(document).on("click", ".flyout-table td:first-child a", function() {
                $(".flyout-handle").remove();
                $(this).parent().append('<div class="flyout-handle"><div class="flyout-panel"><button class="close-bubble">X</button><ul class="key-value-list"></ul></div></div>');
                var headers = $(this).closest("table").find("thead th");
                $(this).parent().siblings().each(function(index, td) {
                    $(".flyout-panel .key-value-list").append('<li><div class="li-key"><span>' + $(headers[index + 1]).text() + '</span></div><div class="li-value">' + $(td).text() + '</div></li>');
                });
                return false;
            });
            $(document).on("click", ".close-bubble", function(){
                $(".flyout-handle").remove();
                return false;
            });
            $(function() {
                $(".flyout-table").find("td, th").not(":first-child").hide();
            });
        </script>
    </jsp:attribute>        
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <table class="record-table flyout-table">
                <thead>
                    <tr>
                        <th>Username</th>
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
                        <td><a href="#">jsmith</a></td>
                        <td>✔</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td><a href="#">kbrown</a></td>
                        <td></td>
                        <td>✔</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td><a href="#">pjohnson</a></td>
                        <td></td>
                        <td></td>
                        <td>✔</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td><a href="#">mcarter</a></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>✔</td>
                        <td></td>
                        <td></td>
                    </tr>
                </tbody>
            </table>
        </section>
    </jsp:body>         
</t:page>
