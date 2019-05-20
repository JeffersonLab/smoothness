package org.jlab.smoothness.presentation.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UrlParamHandler<E> {
    public E convert();
    
    public void validate(E params);
 
    public void store(E params);
    
    public E defaults();
    
    public E materialize();
    
    public boolean qualified();
    
    public String message(E params);
    
    public void redirect(HttpServletResponse response, E params) throws IOException;
}
