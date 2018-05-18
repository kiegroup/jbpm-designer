package org.jbpm.designer.web.server;

import javax.servlet.http.HttpServlet;

/**
 * 
 * Creates/updates task forms for a specific process.
 * 
 * @author Tihomir Surdilovic
 */
public class TaskFormsServlet extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//    private static final Logger _logger = LoggerFactory
//            .getLogger(TaskFormsServlet.class);
//    private static final String TASKFORMS_PATH = "taskforms";
//    private static final String FORMTEMPLATE_FILE_EXTENSION = "ftl";
//    private static final String FORMMODELER_FILE_EXTENSION = "form";
//    public static final String DESIGNER_PATH = ConfigurationProvider.getInstance().getDesignerContext();
//
//    private IDiagramProfile profile;
//
//    public void setProfile(IDiagramProfile profile) {
//        this.profile = profile;
//    }
//
//    @Inject
//    private IDiagramProfileService _profileService = null;
//
//    @Inject
//    private BPMNFormBuilderService formModelerService;
//
//    @Inject
//    private VFSService vfsServices;
//
//    @Override
//    public void init(ServletConfig config) throws ServletException {
//        super.init(config);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//        req.setCharacterEncoding("UTF-8");
//        String json = req.getParameter("json");
//        String uuid = Utils.getUUID(req);
//        String profileName = req.getParameter("profile");
//        String preprocessingData = req.getParameter("ppdata");
//        String taskId = req.getParameter("taskid");
//
//        if (profile == null) {
//            profile = _profileService.findProfile(req, profileName);
//        }
//        Repository repository = profile.getRepository();
//
//        Asset<String> processAsset = null;
//
//        try {
//            processAsset = repository.loadAsset(uuid);
//
//            DroolsFactoryImpl.init();
//            BpsimFactoryImpl.init();
//
//            Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
//            Definitions def = ((Definitions) unmarshaller.unmarshall(json, preprocessingData).getContents().get(0));
//
//            Path myPath = vfsServices.get( uuid );
//
//            TaskFormTemplateManager templateManager = new TaskFormTemplateManager( myPath, formModelerService, profile, processAsset, getServletContext().getRealPath(DESIGNER_PATH + TASKFORMS_PATH), def, taskId );
//            templateManager.processTemplates();
//
//            //storeInRepository(templateManager, processAsset.getAssetLocation(), repository);
//            //displayResponse( templateManager, resp, profile );
//            resp.setContentType("application/json");
//            resp.getWriter().write(storeInRepository(templateManager, processAsset.getAssetLocation(), repository).toString());
//        } catch (Exception e) {
//            _logger.error(e.getMessage());
//            //displayErrorResponse(resp, e.getMessage());
//            resp.setContentType("text/plain");
//            resp.getWriter().write("fail");
//        }
//    }
//
////    public void displayResponse(TaskFormTemplateManager templateManager, HttpServletResponse resp, IDiagramProfile profile) {
////        try {
////            STGroup templates = new STGroup("resultsgroup", templateManager.getTemplatesPath());
////            ST resultsForm = templates.getInstanceOf("resultsform");
//////            resultsForm.add("manager", templateManager);
//////            resultsForm.add("profile", RepositoryInfo.getRepositoryProtocol(profile));
//////            resultsForm.add("host", RepositoryInfo.getRepositoryHost(profile));
//////            resultsForm.add("subdomain", RepositoryInfo.getRepositorySubdomain(profile).substring(0,
//////                RepositoryInfo.getRepositorySubdomain(profile).indexOf("/")));
////            ServletOutputStream outstr = resp.getOutputStream();
////            resp.setContentType("text/html");
////            outstr.write(resultsForm.toString().getBytes("UTF-8"));
////            outstr.flush();
////            outstr.close();
////        } catch (IOException e) {
////           _logger.error(e.getMessage());
////        }
////    }
//
////    public void displayErrorResponse(HttpServletResponse resp, String exceptionStr) {
////        try {
////            ServletOutputStream outstr = resp.getOutputStream();
////            resp.setContentType("text/html");
////            outstr.write(exceptionStr.getBytes("ASCII"));
////            outstr.flush();
////            outstr.close();
////        } catch (IOException e) {
////           _logger.error(e.getMessage());
////        }
////    }
//
//    public JSONArray storeInRepository(TaskFormTemplateManager templateManager, String location, Repository repository) throws Exception {
//        JSONArray retArray = new JSONArray();
//        List<TaskFormInfo> taskForms =  templateManager.getTaskFormInformationList();
//        for(TaskFormInfo taskForm : taskForms) {
//            retArray.put(storeTaskForm(taskForm, location, repository));
//        }
//
//        return retArray;
//    }
//
//    public JSONObject storeTaskForm(TaskFormInfo taskForm, String location, Repository repository) throws Exception {
//        try {
//            JSONObject retObj = new JSONObject();
//
//            repository.deleteAssetFromPath(taskForm.getPkgName() + "/" + taskForm.getId()+"." + FORMTEMPLATE_FILE_EXTENSION);
//
//            // create the form meta form asset
//            AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
//            builder.name(taskForm.getId())
//                   .location(location)
//                    .type(FORMTEMPLATE_FILE_EXTENSION)
//                    .content(taskForm.getMetaOutput().getBytes("UTF-8"));
//
//            repository.createAsset(builder.getAsset());
//
//            Asset newFormAsset =  repository.loadAssetFromPath(taskForm.getPkgName() + "/" + taskForm.getId()+"." + FORMTEMPLATE_FILE_EXTENSION);
//
//            String uniqueId = newFormAsset.getUniqueId();
//            if (Base64Backport.isBase64(uniqueId)) {
//                byte[] decoded = Base64.decodeBase64(uniqueId);
//                try {
//                    uniqueId =  new String(decoded, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//            retObj.put("ftluri", uniqueId);
//
//            // create the modeler form asset
//            repository.deleteAssetFromPath(taskForm.getPkgName() + "/" + taskForm.getId()+"." + FORMMODELER_FILE_EXTENSION);
//            AssetBuilder modelerBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
//            modelerBuilder.name(taskForm.getId())
//                    .location(location)
//                    .type(FORMMODELER_FILE_EXTENSION)
//                    .content(taskForm.getModelerOutput().getBytes("UTF-8"));
//
//            repository.createAsset(modelerBuilder.getAsset());
//
//            Asset newModelerFormAsset =  repository.loadAssetFromPath(taskForm.getPkgName() + "/" + taskForm.getId()+"." + FORMMODELER_FILE_EXTENSION);
//
//            String modelerUniqueId = newModelerFormAsset.getUniqueId();
//            if (Base64Backport.isBase64(modelerUniqueId)) {
//                byte[] decoded = Base64.decodeBase64(modelerUniqueId);
//                try {
//                    modelerUniqueId =  new String(decoded, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            retObj.put("formuri", modelerUniqueId);
//
//            return retObj;
//
//		} catch (Exception e) {
//			_logger.error(e.getMessage());
//            return new JSONObject();
//		}
//    }
}
