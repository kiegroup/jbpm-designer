/**
 * 
 * 
 * contains all strings for spanish language (es)
 *  Translated by Anna Lübbe
 */


// namespace
if(window.Repository == undefined) Repository = {};
if(window.Repository.I18N == undefined) Repository.I18N = {};

Repository.I18N.Language = "es"; //Pattern <ISO language code>_<ISO country code> in lower case!

// Repository strings

if(!Repository.I18N.Repository) Repository.I18N.Repository = {};

Repository.I18N.Repository.openIdSample = "su.openid.de"
Repository.I18N.Repository.sayHello = "¡Hola!"
Repository.I18N.Repository.login = "iniciar sesión"
Repository.I18N.Repository.logout = "cerrar sesión"

Repository.I18N.Repository.viewMenu = "vista"
Repository.I18N.Repository.viewMenuTooltip = "cambiar vista"

Repository.I18N.Repository.windowTimeoutMessage = "Parece que no se ha iniciado el editor oryx. Por favor, compruebe si no esta puesto un bloqueador de pop up." 
Repository.I18N.Repository.windowTitle = "Oryx"

Repository.I18N.Repository.noSaveTitle = "Oryx"
Repository.I18N.Repository.noSaveMessage = "Como usuario publico no tiene derecho a crear o guardar un modelo. ¿Quiere seguir abriendo el editor oryx?" 
Repository.I18N.Repository.yes = "Sí"
Repository.I18N.Repository.no = "No"

Repository.I18N.Repository.leftPanelTitle = "organización de modelos"
Repository.I18N.Repository.rightPanelTitle = "información de modelos"
Repository.I18N.Repository.bottomPanelTitle = "comentarios"

Repository.I18N.Repository.loadingText = "Está cargando repository..."

Repository.I18N.Repository.errorText = "Sólo se puede editar diagramas <b style='font-size:13px'>Upps!</b><br/> en <a href='http://www.mozilla.com/en-US/products/firefox/' target='_blank'>Firefox Browser</a> ."
Repository.I18N.Repository.errorAuthor = "autor:"
Repository.I18N.Repository.errorTitle = "título:"

// Plugins here
 
// NewModel Plugin
if(!Repository.I18N.NewModel) Repository.I18N.NewModel = {};
Repository.I18N.NewModel.name = "nuevo modelo"
Repository.I18N.NewModel.tooltipText = "crear un modelo nuevo con juego de plantillas seloccionado."

// VIEW PLUGINS

if(!Repository.I18N.TableView) Repository.I18N.TableView = {};
Repository.I18N.TableView.name = "vista de tabla"

if(!Repository.I18N.TableView.columns) Repository.I18N.TableView.columns = {};
Repository.I18N.TableView.columns.title = "título"
Repository.I18N.TableView.columns.type = "típo de modelo"
Repository.I18N.TableView.columns.author = "autor"
Repository.I18N.TableView.columns.summary = "descripción"
Repository.I18N.TableView.columns.creationDate = "creado el (datum)"
Repository.I18N.TableView.columns.lastUpdate = "modificado el (datum)"
Repository.I18N.TableView.columns.id = "ID"

if(!Repository.I18N.IconView) Repository.I18N.IconView = {};
Repository.I18N.IconView.name = "vista en azulejos"

if(!Repository.I18N.FullView) Repository.I18N.FullView = {};
Repository.I18N.FullView.name = "vista individual"

Repository.I18N.FullView.createdLabel = "creado"
Repository.I18N.FullView.fromLabel = "de";
Repository.I18N.FullView.changeLabel = "última modificación";
Repository.I18N.FullView.descriptionLabel = "descripción";
Repository.I18N.FullView.editorLabel = "abrir editor";

// TypeFilter Plugin

if(!Repository.I18N.TypeFilter) Repository.I18N.TypeFilter = {};
Repository.I18N.TypeFilter.name = "típo de filtro";

if(!Repository.I18N.TagInfo) Repository.I18N.TagInfo = {};
Repository.I18N.TagInfo.name = "etiquetas"
Repository.I18N.TagInfo.deleteText = "borrar"
Repository.I18N.TagInfo.none = "ningún"
Repository.I18N.TagInfo.shared = "etiquetas comunes:"
Repository.I18N.TagInfo.newTag = "etiqueta nueva"
Repository.I18N.TagInfo.addTag = "añadir"
		
if(!Repository.I18N.ModelRangeSelection) Repository.I18N.ModelRangeSelection = {};
Repository.I18N.ModelRangeSelection.previous = "<< pagina anterior"
Repository.I18N.ModelRangeSelection.next = "proxima pagina >>"
Repository.I18N.ModelRangeSelection.last = "última"
Repository.I18N.ModelRangeSelection.first = "primera"
Repository.I18N.ModelRangeSelection.modelsOfZero = "(0 modelos)" 
Repository.I18N.ModelRangeSelection.modelsOfOne = "(#{from} de #{size} modelos)" 
Repository.I18N.ModelRangeSelection.modelsOfMore = "(#{from}-#{to} de #{size} modelos)" 

if(!Repository.I18N.AccessInfo) Repository.I18N.AccessInfo = {};
Repository.I18N.AccessInfo.name = "administración de derechos"
Repository.I18N.AccessInfo.publicText = "público";
Repository.I18N.AccessInfo.notPublicText  = "accesso restringido";
Repository.I18N.AccessInfo.noneIsSelected  = "no ha seleccionado un modelo";
Repository.I18N.AccessInfo.none  = "ningún";
Repository.I18N.AccessInfo.deleteText  = "borrar";
Repository.I18N.AccessInfo.publish  = "publicar";
Repository.I18N.AccessInfo.unPublish  = "cancelar publicación";
Repository.I18N.AccessInfo.owner = "dueño:"
Repository.I18N.AccessInfo.contributer = "derecho a editar:"
Repository.I18N.AccessInfo.reader = "derecho a leer:"
Repository.I18N.AccessInfo.openid = "OpenID"
Repository.I18N.AccessInfo.addReader = "añadir derecho a leer"
Repository.I18N.AccessInfo.addContributer = "añadir derecho a editar"
Repository.I18N.AccessInfo.several = "varios"
Repository.I18N.AccessInfo.noWritePermission = "no tiene derecho a editar"

if(!Repository.I18N.SortingSupport) Repository.I18N.SortingSupport = {};
Repository.I18N.SortingSupport.name = "órden";
Repository.I18N.SortingSupport.lastchange = "por modificación mas reciente"
Repository.I18N.SortingSupport.title = "por título"
Repository.I18N.SortingSupport.rating = "por votos"

if(!Repository.I18N.Export) Repository.I18N.Export = {};
Repository.I18N.Export.name = "exporte";
Repository.I18N.Export.title = "formatos disponibles para exportar:"
Repository.I18N.Export.onlyOne = "sólo se puede seleccionar un modelo"

if(!Repository.I18N.UpdateButton) Repository.I18N.UpdateButton = {};
Repository.I18N.UpdateButton.name = "actualizar"

if(!Repository.I18N.Edit) Repository.I18N.Edit = {};
Repository.I18N.Edit.name = "editar"
Repository.I18N.Edit.editSummary = "editar descripción"
Repository.I18N.Edit.editName = "editar nombre"
Repository.I18N.Edit.nameText = "nombre"
Repository.I18N.Edit.summaryText = "descripción"
Repository.I18N.Edit.editText = "guardar cambios"
Repository.I18N.Edit.deleteText = "borrar"
Repository.I18N.Edit.noWriteAccess = "solo el dueño del modelo lo puede borrar"
Repository.I18N.Edit.deleteOneText = "'#{title}'" 
Repository.I18N.Edit.deleteMoreText = "todos #{size} modelos seleccionados" 

if(!Repository.I18N.Rating) Repository.I18N.Rating = {};
Repository.I18N.Rating.name = "valuación"
Repository.I18N.Rating.total = "valuación total:"
Repository.I18N.Rating.my = "mi valuación:"
Repository.I18N.Rating.totalNoneText = "no tiene votos" 
Repository.I18N.Rating.totalOneText = "#{totalScore} (#{totalVotes})" 
Repository.I18N.Rating.totalMoreText = "de #{modelCount} modelos #{voteCount} tienen una valuación promedia de #{totalScore} (#{totalVotes})"

if(!Repository.I18N.RatingFilter) Repository.I18N.RatingFilter = {};
Repository.I18N.RatingFilter.name = "filtro de valuación"

if(!Repository.I18N.AccessFilter) Repository.I18N.AccessFilter = {};
Repository.I18N.AccessFilter.name = "filtro de derechos"
Repository.I18N.AccessFilter.mine = "mis"
Repository.I18N.AccessFilter.reader = "todos con derecho a leer"
Repository.I18N.AccessFilter.writer = "todos con derecho a editar"
Repository.I18N.AccessFilter.publicText = "público"
