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


Repository.I18N.en_us = "English";
Repository.I18N.de = "Deutsch";
// Repository strings

if(!Repository.I18N.Repository) Repository.I18N.Repository = {};

Repository.I18N.Repository.openIdSample = "your.openid.net";
Repository.I18N.Repository.sayHello = "Hi";
Repository.I18N.Repository.login = "login";
Repository.I18N.Repository.logout = "logout";

Repository.I18N.Repository.viewMenu = "View";
Repository.I18N.Repository.viewMenuTooltip = "Changes View";

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
Repository.I18N.NewModel.tooltipText = "Create a new model of the selected type";

// TableView Plugin

if(!Repository.I18N.TableView) Repository.I18N.TableView = {};

Repository.I18N.TableView.name = "Table View";

if(!Repository.I18N.TableView.columns) Repository.I18N.TableView.columns = {};
Repository.I18N.TableView.columns.title = "Title";
Repository.I18N.TableView.columns.type = "Model Type";
Repository.I18N.TableView.columns.author = "Author";

if(!Repository.I18N.IconView) Repository.I18N.IconView = {};
Repository.I18N.IconView.name = "Icon View";


if(!Repository.I18N.FullView) Repository.I18N.FullView = {};
Repository.I18N.FullView.name = "Full View";

// TypeFilter Plugin

if(!Repository.I18N.TypeFilter) Repository.I18N.TypeFilter = {};
Repository.I18N.TypeFilter.name = "Type Filter";

if(!Repository.I18N.TagInfo) Repository.I18N.TagInfo = {};
Repository.I18N.TagInfo.name = "Tags"
Repository.I18N.TagInfo.deleteText = "Delete"
Repository.I18N.TagInfo.none = "none"
Repository.I18N.TagInfo.shared = "Shared tags:"
Repository.I18N.TagInfo.newTag = "New Tag"
Repository.I18N.TagInfo.addTag = "Add"
		
if(!Repository.I18N.ModelRangeSelection) Repository.I18N.ModelRangeSelection = {};
Repository.I18N.ModelRangeSelection.previous = "<< Previous Page"
Repository.I18N.ModelRangeSelection.next = "Next Page >>"
Repository.I18N.ModelRangeSelection.last = "Last"
Repository.I18N.ModelRangeSelection.first = "First"
Repository.I18N.ModelRangeSelection.modelsOf = "(#{number} of #{size} Models)" 

if(!Repository.I18N.AccessInfo) Repository.I18N.AccessInfo = {};
Repository.I18N.AccessInfo.name = "Access Rights"
Repository.I18N.AccessInfo.publicText = "Public";
Repository.I18N.AccessInfo.notPublicText  = "Not Public";
Repository.I18N.AccessInfo.noneIsSelected  = "None is selected";
Repository.I18N.AccessInfo.none  = "none";
Repository.I18N.AccessInfo.deleteText  = "Delete";
Repository.I18N.AccessInfo.publish  = "Publishing";
Repository.I18N.AccessInfo.unPublish  = "Stop Publishing";
Repository.I18N.AccessInfo.owner = "Owner:"
Repository.I18N.AccessInfo.contributer = "Contributers:"
Repository.I18N.AccessInfo.reader = "Readers:"
Repository.I18N.AccessInfo.openid = "OpenID"
Repository.I18N.AccessInfo.addReader = "Add as Reader"
Repository.I18N.AccessInfo.addContributer = "Add as Contributer"


if(!Repository.I18N.SortingSupport) Repository.I18N.SortingSupport = {};
Repository.I18N.SortingSupport.name = "Sorting";
Repository.I18N.SortingSupport.lastchange = "By last change"
Repository.I18N.SortingSupport.title = "By title"

if(!Repository.I18N.Export) Repository.I18N.Export = {};
Repository.I18N.Export.name = "Export";
Repository.I18N.Export.title = "Available export formats:"
Repository.I18N.Export.onlyOne = "Only one has to be selected!"

