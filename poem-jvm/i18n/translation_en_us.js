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
Repository.I18N.ru = "Русский";
Repository.I18N.es = "español";

// Repository strings

if(!Repository.I18N.Repository) Repository.I18N.Repository = {};

Repository.I18N.Repository.openIdSample = "your.openid.net";
Repository.I18N.Repository.sayHello = "Hi";
Repository.I18N.Repository.login = "login";
Repository.I18N.Repository.logout = "logout";

Repository.I18N.Repository.viewMenu = "View";
Repository.I18N.Repository.viewMenuTooltip = "Changes View";

Repository.I18N.Repository.windowTimeoutMessage = "The editor does not seem to be started yet. Please check, whether you have a popup blocker enabled and disable it or allow popups for this site. We will never display any commercials on this site.";
Repository.I18N.Repository.windowTitle = "Oryx";

Repository.I18N.Repository.noSaveTitle = "Message";
Repository.I18N.Repository.noSaveMessage = "As a public user, you can not save a model. Do you want to model anyway?";
Repository.I18N.Repository.yes = "Yes";
Repository.I18N.Repository.no = "No";

Repository.I18N.Repository.leftPanelTitle = "Organize Models";
Repository.I18N.Repository.rightPanelTitle = "Model Info";
Repository.I18N.Repository.bottomPanelTitle = "Comments";

Repository.I18N.Repository.loadingText = "Repository is loading..."

Repository.I18N.Repository.errorText = "<b style='font-size:13px'>Upps!</b><br/>Diagrams can only be edited with a <a href='http://www.mozilla.com/en-US/products/firefox/' target='_blank'>Firefox Browser</a>.";
Repository.I18N.Repository.errorAuthor = "Author:"
Repository.I18N.Repository.errorTitle = "Title:"
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
Repository.I18N.TableView.columns.summary = "Summary";
Repository.I18N.TableView.columns.creationDate = "Creation Date";
Repository.I18N.TableView.columns.lastUpdate = "Last Update";
Repository.I18N.TableView.columns.id = "ID";

if(!Repository.I18N.IconView) Repository.I18N.IconView = {};
Repository.I18N.IconView.name = "Icon View";


if(!Repository.I18N.FullView) Repository.I18N.FullView = {};
Repository.I18N.FullView.name = "Full View";

Repository.I18N.FullView.createdLabel = "Created";
Repository.I18N.FullView.fromLabel = "From";
Repository.I18N.FullView.changeLabel = "Last Change";
Repository.I18N.FullView.descriptionLabel = "Description";
Repository.I18N.FullView.editorLabel = "Open Editor";
;
// TypeFilter Plugin

if(!Repository.I18N.TypeFilter) Repository.I18N.TypeFilter = {};
Repository.I18N.TypeFilter.name = "Type Filter";

// TagFilter Plugin

if(!Repository.I18N.TagFilter) Repository.I18N.TagFilter = {};
Repository.I18N.TagFilter.name = "Tag Filter";

// Friend Filter Plugin

if(!Repository.I18N.FriendFilter) Repository.I18N.FriendFilter = {};
Repository.I18N.FriendFilter.name = "Friend Filter";

if(!Repository.I18N.TagInfo) Repository.I18N.TagInfo = {};
Repository.I18N.TagInfo.name = "Tags"
Repository.I18N.TagInfo.deleteText = "Delete"
Repository.I18N.TagInfo.none = "none"
Repository.I18N.TagInfo.shared = "Shared tags:"
Repository.I18N.TagInfo.newTag = "New Tag"
Repository.I18N.TagInfo.addTag = "Add"
		
if(!Repository.I18N.ModelRangeSelection) Repository.I18N.ModelRangeSelection = {};
Repository.I18N.ModelRangeSelection.previous = "« Previous Page"
Repository.I18N.ModelRangeSelection.next = "Next Page »"
Repository.I18N.ModelRangeSelection.nextSmall = "»"
Repository.I18N.ModelRangeSelection.previousSmall = "«"
Repository.I18N.ModelRangeSelection.last = "Last"
Repository.I18N.ModelRangeSelection.first = "First"
Repository.I18N.ModelRangeSelection.modelsOfZero = "(0 models)" 
Repository.I18N.ModelRangeSelection.modelsOfOne = "(#{from} from #{size} models)" 
Repository.I18N.ModelRangeSelection.modelsOfMore = "(#{from}-#{to} from #{size} models)" 

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
Repository.I18N.AccessInfo.several = "several"
Repository.I18N.AccessInfo.noWritePermission = "No write permissions"
 

if(!Repository.I18N.SortingSupport) Repository.I18N.SortingSupport = {};
Repository.I18N.SortingSupport.name = "Sorting";
Repository.I18N.SortingSupport.lastchange = "By last change"
Repository.I18N.SortingSupport.title = "By title"
Repository.I18N.SortingSupport.rating = "By rating"

if(!Repository.I18N.Export) Repository.I18N.Export = {};
Repository.I18N.Export.name = "Export";
Repository.I18N.Export.title = "Available export formats:"
Repository.I18N.Export.onlyOne = "Only one has to be selected!"

if(!Repository.I18N.UpdateButton) Repository.I18N.UpdateButton = {};
Repository.I18N.UpdateButton.name = "Refresh"

if(!Repository.I18N.Edit) Repository.I18N.Edit = {};
Repository.I18N.Edit.name = "Edit"
Repository.I18N.Edit.editSummary = "Edit summary"
Repository.I18N.Edit.editName = "Edit name"
Repository.I18N.Edit.nameText = "Name"
Repository.I18N.Edit.summaryText = "Summary"
Repository.I18N.Edit.editText = "Store changes"
Repository.I18N.Edit.deleteText = "Delete"
Repository.I18N.Edit.noWriteAccess = "Only the owner has the permission to delete"
Repository.I18N.Edit.deleteOneText = "'#{title}'" 
Repository.I18N.Edit.deleteMoreText = "All #{size} selected models" 


if(!Repository.I18N.Rating) Repository.I18N.Rating = {};
Repository.I18N.Rating.name = "Rating"
Repository.I18N.Rating.total = "Total Rating:"
Repository.I18N.Rating.my = "My Rating:"
Repository.I18N.Rating.totalNoneText = "no votes" 
Repository.I18N.Rating.totalOneText = "#{totalScore} (#{totalVotes})" 
Repository.I18N.Rating.totalMoreText = "From  #{modelCount} are #{voteCount} voted in average with #{totalScore} (#{totalVotes})"

if(!Repository.I18N.RatingFilter) Repository.I18N.RatingFilter = {};
Repository.I18N.RatingFilter.name = "Rating Filter"

if(!Repository.I18N.AccessFilter) Repository.I18N.AccessFilter = {};
Repository.I18N.AccessFilter.name = "Access Filter"
Repository.I18N.AccessFilter.mine = "Mine"
Repository.I18N.AccessFilter.reader = "Me as a reader"
Repository.I18N.AccessFilter.writer = "Me as a writer"
Repository.I18N.AccessFilter.publicText = "Public"
