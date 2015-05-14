package org.jbpm.designer.web.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sevlet for color themes.
 * 
 * @author Tihomir Surdilovic
 */
public class ThemeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ACTION_GETTHEMENAMES = "getThemeNames";
	private static final String ACTION_GETTHEMEJSON = "getThemeJSON";
	private static final String THEME_NAME = "themes";
    public static final String THEME_EXT = ".json";
	private static final String DEFAULT_THEME = "jBPM";
	private static final Logger _logger = LoggerFactory.getLogger(ThemeServlet.class);
	private ServletConfig config;
	private String themeInfo;

//    @Inject
    private static IDiagramProfileService _profileService = ProfileServiceImpl.getInstance();
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String profileName = req.getParameter("profile");
		String action = req.getParameter("action");
        String uuid = Utils.getUUID(req);

		IDiagramProfile profile = _profileService.findProfile(req, profileName);

		if(action != null && action.equals(ACTION_GETTHEMENAMES)) {
			String themeStr = getThemeNames(profile, getServletContext(), uuid);
			PrintWriter pw = resp.getWriter();
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			pw.write(themeStr);
		} else if(action != null && action.equals(ACTION_GETTHEMEJSON)) {
			String themeJSON = getThemeJson(profile, getServletContext(), uuid);
			PrintWriter pw = resp.getWriter();
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			pw.write(themeJSON);
		}
	}
	
	private String getThemeJson(IDiagramProfile profile, ServletContext servletContext, String uuid) {

		String retStr = "";

    	try {

            retStr = FileUtils.readFileToString(new File(servletContext.getRealPath(ConfigurationProvider.getInstance().getDesignerContext() + "defaults/" + THEME_NAME+THEME_EXT)));

        } catch (Exception e) {
			_logger.error("Error retriving color theme info: " + e.getMessage());
		}
        return retStr;
	}
	
	private String getThemeNames(IDiagramProfile profile, ServletContext servletContext, String uuid) {

		String themesStr = "";
    	
        try {

            JSONObject themesObject =  new JSONObject(FileUtils.readFileToString(new File(servletContext.getRealPath(ConfigurationProvider.getInstance().getDesignerContext() + "defaults/" + THEME_NAME+THEME_EXT))));
            JSONObject themes = (JSONObject) themesObject.get("themes");
            for (int i = 0; i < themes.names().length(); i++) {
                themesStr += themes.names().getString(i) + ",";
            }
            if(themesStr.endsWith(",")) {
                themesStr = themesStr.substring(0, themesStr.length() - 1);
            }

		} catch (Exception e) {
			themesStr = DEFAULT_THEME;
		} 
        return themesStr;
	}

}
