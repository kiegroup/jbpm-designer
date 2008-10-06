/**
 * @author sven.wagner-boysen
 * 
 * contains all strings for german language (de)
 * 
 */


// namespace
if(window.Repository == undefined) Repository = {};
if(window.Repository.I18N == undefined) Repository.I18N = {};

Repository.I18N.Language = "de"; //Pattern <ISO language code>_<ISO country code> in lower case!

// Repository strings

if(!Repository.I18N.Repository) Repository.I18N.Repository = {};

Repository.I18N.Repository.openIdSample = "ihre.openid.de";
Repository.I18N.Repository.sayHello = "Hallo";
Repository.I18N.Repository.login = "Anmelden";
Repository.I18N.Repository.logout = "Abmelden";

Repository.I18N.Repository.leftPanelTitle = "Modelorganisation";
Repository.I18N.Repository.rightPanelTitle = "Modelinfo";
Repository.I18N.Repository.bottomPanelTitle = "Kommentare";

// Plugins here
 
// NewModel Plugin
if(!Repository.I18N.NewModel) Repository.I18N.NewModel = {};

Repository.I18N.NewModel.name = "Neues Model";

//TypeFilter Plugin

if(!Repository.I18N.TypeFilter) Repository.I18N.TypeFilter = {};

Repository.I18N.TypeFilter.name = "Typ Filter";
