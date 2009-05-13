pref("toolkit.defaultChromeURI", "chrome://Oryx-Editor/content/main.xul");
pref("browser.chromeURL", "chrome://Oryx-Editor/content/editor.xul");

pref("general.useragent.extra.firefox", "Firefox/3.0.8");
/* debugging prefs
pref("browser.dom.window.dump.enabled", true);
pref("javascript.options.showInConsole", true);
pref("javascript.options.strict", true);
pref("nglayout.debug.disable_xul_cache", true);
pref("nglayout.debug.disable_xul_fastload", true);*/

/* autocomplete */
pref("browser.formfill.enable", true);

/* spellcheck */
pref("layout.spellcheckDefault", 1);
pref("oryx.start.url", "http://www.oryx-editor.org/backend/poem/repository");

/* suppress external-load warning for standard browser schemes*/
pref("network.protocol-handler.warn-external.http", false);
pref("network.protocol-handler.warn-external.https", false);
pref("network.protocol-handler.warn-external.ftp", false);
