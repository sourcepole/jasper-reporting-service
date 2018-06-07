package jasperapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.Map;


@RestController
@RequestMapping(value = "/reports")
public class JasperEndpointController {
    private static final String FILE_FORMAT = "format";

    private static final String DATASOURCE = "datasource";

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    /* Example http://localhost:8080/reports/rpt_example/?format=pdf&personid=0 */
    /* Example http://localhost:8080/reports/rpt_subfolder/rpt_example/?format=pdf&personid=0 */

    @RequestMapping(value = {"/{reportname}/", "/{reportfolder}/{reportname}/"}, method = RequestMethod.GET)
    public ModelAndView getRptByParam(final ModelMap modelMap, ModelAndView modelAndView, @PathVariable Map<String, String> pathVariables,  HttpServletRequest request) {

        Map<String, String[]> map = request.getParameterMap();
        String reportfolder = pathVariables.get("reportfolder");
        String reportname = pathVariables.get("reportname");

        String report = reportfolder != null ? reportfolder + "/" + reportname : reportname;

        // connecting to H2
        modelMap.put(DATASOURCE, dataSource);

        map.forEach((String k, String[] v) -> {
            if (v[0].matches("\\d+")) {
                //its a number:
                modelMap.put(k, Integer.parseInt(v[0]));
            } else {
                modelMap.put(k, v[0]);
            }
        });

        // This can be used in JasperReports to have absolute Image Paths
        modelMap.put("ROOT_DIR", System.getProperty("user.dir") + "/reports" + (reportfolder != null ? "/" + reportfolder : ""));

        //It is important that the underlying Jasper Report supports the Query parameters

        modelAndView = new ModelAndView(report, modelMap);
        return modelAndView;
    }
}

