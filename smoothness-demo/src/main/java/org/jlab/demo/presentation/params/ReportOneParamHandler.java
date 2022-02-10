package org.jlab.demo.presentation.params;

import org.jlab.demo.business.params.ReportOneParams;
import org.jlab.smoothness.business.util.IOUtil;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.ParamBuilder;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ServletUtil;
import org.jlab.smoothness.presentation.util.UrlParamHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Parameter Handler for ReportOne.
 */
public class ReportOneParamHandler implements UrlParamHandler<ReportOneParams> {
    private final HttpServletRequest request;

    /**
     * Create a new ReportoneParamHandler.
     *
     * @param request The HttpServletRequest
     */
    public ReportOneParamHandler(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public ReportOneParams convert() {
        Date start = null;
        Date end = null;

        try {
            start = ParamConverter.convertFriendlyDateTime(request, "start");
            end = ParamConverter.convertFriendlyDateTime(request, "end");
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse date", e);
        }

        ReportOneParams params = new ReportOneParams();

        params.setStart(start);
        params.setEnd(end);

        return params;
    }

    @Override
    public void validate(ReportOneParams params) {
        if (params.getStart() == null) {
            throw new RuntimeException("start date must not be empty");
        }

        if (params.getEnd() == null) {
            throw new RuntimeException("end date must not be empty");
        }

        if (params.getStart().after(params.getEnd())) {
            throw new RuntimeException("start date must not come before end date");
        }
    }

    @Override
    public void store(ReportOneParams params) {
        /* Note: We store each field individually as we want to re-use among screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);

        session.setAttribute("start[]", new Date[]{params.getStart()});
        session.setAttribute("end[]", new Date[]{params.getEnd()});
    }

    @Override
    public ReportOneParams defaults() {
        ReportOneParams defaultParams = new ReportOneParams();

        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 7);
        Date today = c.getTime();
        c.add(Calendar.DATE, -7);
        Date sevenDaysAgo = c.getTime();

        defaultParams.setStart(sevenDaysAgo);
        defaultParams.setEnd(today);

        return defaultParams;
    }

    @Override
    public ReportOneParams materialize() {
        ReportOneParams defaultValues = defaults();

        /* Note: We store each field individually as we want to re-use among screens*/
        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        /* Note: We use an array regardless if the parameter is multi-valued because a null array means no page ever set this param before vs empty array or array with null elements means someone set it, but value is empty*/
        HttpSession session = request.getSession(true);
        Date[] startArray = (Date[]) session.getAttribute("start[]");
        Date[] endArray = (Date[]) session.getAttribute("end[]");

        Date start = defaultValues.getStart();
        Date end = defaultValues.getEnd();

        if (startArray != null && startArray.length > 0) {
            start = startArray[0];
        }

        if (endArray != null && endArray.length > 0) {
            end = endArray[0];
        }

        ReportOneParams params = new ReportOneParams();

        params.setStart(start);
        params.setEnd(end);

        return params;
    }

    @Override
    public boolean qualified() {
        return request.getParameter("qualified") != null;
    }

    @Override
    public String message(ReportOneParams params) {
        return TimeUtil.formatSmartRangeSeparateTime(params.getStart(), params.getEnd());
    }

    @Override
    public void redirect(HttpServletResponse response, ReportOneParams params) throws IOException {
        ParamBuilder builder = new ParamBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());

        builder.add("start", IOUtil.nullOrFormat(params.getStart(), dateFormat));
        builder.add("end", IOUtil.nullOrFormat(params.getEnd(), dateFormat));
        builder.add("qualified", "");

        String url = ServletUtil.getCurrentUrlAdvanced(request, builder.getParams());

        response.sendRedirect(response.encodeRedirectURL(url));
    }
}
