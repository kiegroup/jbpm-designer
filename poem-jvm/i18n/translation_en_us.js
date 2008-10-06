/**
 * @author sven.wagner-boysen
 * 
 * contains all strings for default language (en_us)
 * 
 */



// namespace
if(window.Repository == undefined) Repository = {};
if(window.Repository.I18N == undefined) Repository.I18N = {};

Repository.I18N.Language = "en_us"; //Pattern <ISO language code>_<ISO country code> in lower case!

// Repository strings

if(!Repository.I18N.Repository) Repository.I18N.Repository = {};

Repository.I18N.Repository.openIdSample = "your.openid.net";
Repository.I18N.Repository.sayHello = "Hi";
Repository.I18N.Repository.login = "login";
Repository.I18N.Repository.logout = "logout";

Repository.I18N.Repository.viewMenu = "View";

Repository.I18N.Repository.windowTimeoutMessage = "The editor does not seem to be started yet. Please check, whether you have a popup blocker enabled and disable it or allow popups for this site. We will never display any commercials on this site.";
Repository.I18N.Repository.windowTitle = "Editor not started.";

Repository.I18N.Repository.noSaveTitle = "Message";
Repository.I18N.Repository.noSaveMessage = "As a public user, you can not save a model. Do you want to model anyway?";
Repository.I18N.Repository.yes = "yes";

Repository.I18N.Repository.leftPanelTitle = "Organize Models";
Repository.I18N.Repository.rightPanelTitle = "Model Info";
Repository.I18N.Repository.bottomPanelTitle = "Comments";


// Plugin strings
 
// NewModel Plugin
if(!Repository.I18N.NewModel) Repository.I18N.NewModel = {};

Repository.I18N.NewModel.name = "Create New Model";

// TableView Plugin

if(!Repository.I18N.TableView) Repository.I18N.TableView = {};

Repository.I18N.TableView.name = "Table View";

// TypeFilter Plugin

if(!Repository.I18N.TypeFilter) Repository.I18N.TypeFilter = {};

Repository.I18N.TypeFilter.name = "Type Filter";

if(!Repository.I18N.TagInfo) Repository.I18N.TagInfo = {};
Repository.I18N.TagInfo.name = "Tags"





