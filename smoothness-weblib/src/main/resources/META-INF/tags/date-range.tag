<%@tag description="Date Range" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@attribute name="datetime" required="false" type="java.lang.Boolean"%>
<%@attribute name="sevenAmOffset" required="false" type="java.lang.Boolean"%>
<ul class="key-value-list">
    <li>
        <div class="li-key"><label class="required-field" for="date-range">Date Range</label></div>
        <div class="li-value">
            <select id="date-range" class="${datetime ? 'datetime-range' : 'date-range'} ${sevenAmOffset ? ' seven-am-offset' : ' midnight-offset'}">
                <option value="1fiscalyear">Previous Fiscal Year</option>
                <option value="1year">Previous Year</option>
                <option value="1month">Previous Month</option>
                <option value="1week">Previous Week (from Wed)</option>
                <c:if test="${datetime}">
                    <option value="1ccshift">Previous CC Shift</option>
                </c:if>
                <option value="0fiscalyear">Current Fiscal Year</option>
                <option value="0year">Current Year</option>
                <option value="0month">Current Month</option>
                <option value="0week">Current Week (from Wed)</option>
                <c:if test="${datetime}">
                    <option value="0ccshift">Current CC Shift</option>
                </c:if>
                <option value="past10days">Past 10 Days</option>
                <option value="past7days">Past 7 Days</option>
                <option value="past3days">Past 3 Days</option>
                <option value="1day">Yesterday</option>
                <option value="0day">Today</option>
                <option value="custom">Custom...</option>
            </select>
        </div>
    </li>
</ul>
<ul id="custom-date-range-list" class="key-value-list" style="display: none;">
    <li>
        <div class="li-key">
            <label class="required-field" for="start" title="Inclusive (Closed)">Start
                Date</label>
            <div class="date-note">(Inclusive)</div>
        </div>
        <div class="li-value">
            <input type="text" class="${datetime ? 'datetime' : 'date'}-input" id="start" name="start" autocomplete="off"
                   placeholder="DD-MMM-YYYY ${datetime ? 'hh:mm' : ''}" value="${param.start}"/>
        </div>
    </li>
    <li>
        <div class="li-key">
            <label class="required-field" for="end" title="Exclusive (Open)">End Date</label>
            <div class="date-note">(Exclusive)</div>
        </div>
        <div class="li-value">
            <input type="text" class="${datetime ? 'datetime' : 'date'}-input" id="end" name="end" autocomplete="off"
                   placeholder="DD-MMM-YYYY ${datetime ? 'hh:mm' : ''}" value="${param.end}"/>
        </div>
    </li>
</ul>