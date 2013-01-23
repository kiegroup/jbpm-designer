/**
 * @author Sergey Smirnov
 * 
 * Contains all strings for the default language (ru).
 * Version 1 - 08/29/08
 */
if(!ORYX) var ORYX = {};

if(!ORYX.I18N) ORYX.I18N = {};

ORYX.I18N.Language = "ru"; //Pattern <ISO language code>_<ISO country code> in lower case!

if(!ORYX.I18N.Oryx) ORYX.I18N.Oryx = {};

ORYX.I18N.Oryx.pleaseWait = "Oryx загружается. Пожалуйста, подождите...";
ORYX.I18N.Oryx.notLoggedOn = "Не прошел регистрацию";

if(!ORYX.I18N.AddDocker) ORYX.I18N.AddDocker = {};

//ORYX.I18N.AddDocker.group = "Docker";
ORYX.I18N.AddDocker.add = "Добавить докер";
ORYX.I18N.AddDocker.addDesc = "Добавить докер, кликом на ребре";
ORYX.I18N.AddDocker.del = "Удалить докер";
ORYX.I18N.AddDocker.delDesc = "Удалить докер";

if(!ORYX.I18N.SSExtensionLoader) ORYX.I18N.SSExtensionLoader = {};

//ORYX.I18N.SSExtensionLoader.group = "Stencil Set";
ORYX.I18N.SSExtensionLoader.add = "Добавить расширения набора шаблонов";
ORYX.I18N.SSExtensionLoader.addDesc = "Добавить расширение набора шаблонов";
ORYX.I18N.SSExtensionLoader.loading = "Загрузка расширения набора шаблонов";
ORYX.I18N.SSExtensionLoader.noExt = "Нет доступных расширений или все доступные расширения уже загружены.";
ORYX.I18N.SSExtensionLoader.failed1 = "Загрузка конфигурации расширений наборов шаблонов не удалась. Ответ не является правильным файлом конфигурации.";
ORYX.I18N.SSExtensionLoader.failed2 = "Загрузка конфигурации расширений наборов шаблонов не удалась. Запрос вызвал ошибку.";
ORYX.I18N.SSExtensionLoader.panelTitle = "Расширения";
ORYX.I18N.SSExtensionLoader.panelText = "Выберите расширения набора шаблонов, которые вы хотите использовать.";

if(!ORYX.I18N.AdHocCC) ORYX.I18N.AdHocCC = {};
//ORYX.I18N.AdHocCC.group = "Ad Hoc";
ORYX.I18N.AdHocCC.compl = "Изменить условие завершения";
ORYX.I18N.AdHocCC.complDesc = "Изменить условие завершения ad hoc Действия";
ORYX.I18N.AdHocCC.notOne = "Необходимо выбрать один элемент!";
ORYX.I18N.AdHocCC.nodAdHocCC = "Выбранный ad hoc элемент не имеет условия завершения!";
ORYX.I18N.AdHocCC.selectTask = "Выбрать задачу...";
ORYX.I18N.AdHocCC.selectState = "Выбрать состояние...";
ORYX.I18N.AdHocCC.addExp = "Добавить выражение";
ORYX.I18N.AdHocCC.selectDataField = "Выбрать поле данных...";
ORYX.I18N.AdHocCC.enterEqual = "Выбрать значение, равное...";
ORYX.I18N.AdHocCC.and = "и";
ORYX.I18N.AdHocCC.or = "или";
ORYX.I18N.AdHocCC.not = "не";
ORYX.I18N.AdHocCC.clearCC = "Очистить условие завершения";
ORYX.I18N.AdHocCC.editCC = "Редактировать условия завершения ad hoc элементов";
ORYX.I18N.AdHocCC.addExecState = "Добавить операторы состояний выполнения: ";
ORYX.I18N.AdHocCC.addDataExp = "Добавить операторы данных: ";
ORYX.I18N.AdHocCC.addLogOp = "Добавить логические операторы: ";
ORYX.I18N.AdHocCC.curCond = "Текущие условия завершения: ";

if(!ORYX.I18N.AMLSupport) ORYX.I18N.AMLSupport = {};

//ORYX.I18N.AMLSupport.group = "EPC";
ORYX.I18N.AMLSupport.imp = "Импортировать файл AML";
ORYX.I18N.AMLSupport.impDesc = "Импортировать файл в формате AML Aris 7";
ORYX.I18N.AMLSupport.failed = "Импорт файла AML не удался. Пожалуйста, проверьте соответствует ли выбранный файл формату AML. Сообщение об ошибке: ";
ORYX.I18N.AMLSupport.failed2 = "Импорт файла AML не удался: ";
ORYX.I18N.AMLSupport.noRights = "У вас нет прав для импорта нескольких диаграмм EPC (Необходимо войти в систему).";
ORYX.I18N.AMLSupport.panelText = "Выбрать файл AML (.xml) для импорта.";
ORYX.I18N.AMLSupport.file = "Файл";
ORYX.I18N.AMLSupport.importBtn = "Импортировать файл AML";
ORYX.I18N.AMLSupport.get = "Получить диаграммы...";
ORYX.I18N.AMLSupport.close = "Закрыть";
ORYX.I18N.AMLSupport.title = "Название";
ORYX.I18N.AMLSupport.selectDiagrams = "Выбрать диаграммы для импорта. <br/> Если выбрана одна модель, то она будет загружена в текущее окно. Если импортируется более одной модели, то они сохраняются в репозитории.";
ORYX.I18N.AMLSupport.impText = "Импорт";
ORYX.I18N.AMLSupport.impProgress = "Выполняется импорт...";
ORYX.I18N.AMLSupport.cancel = "Отменить";
ORYX.I18N.AMLSupport.name = "Имя";
ORYX.I18N.AMLSupport.allImported = "Все импортированные диаграммы.";
ORYX.I18N.AMLSupport.ok = "Ок";

if(!ORYX.I18N.Arrangement) ORYX.I18N.Arrangement = {};

ORYX.I18N.Arrangement.groupZ = "Z-Order";
ORYX.I18N.Arrangement.btf = "На передний план";
ORYX.I18N.Arrangement.btfDesc = "На передний план";
ORYX.I18N.Arrangement.btb = "На задний план";
ORYX.I18N.Arrangement.btbDesc = "На задний план";
ORYX.I18N.Arrangement.bf = "Переместить вперед";
ORYX.I18N.Arrangement.bfDesc = "Переместить вперед";
ORYX.I18N.Arrangement.bb = "Переместить назад";
ORYX.I18N.Arrangement.bbDesc = "Переместить назад";
ORYX.I18N.Arrangement.groupA = "Выравнивание";
ORYX.I18N.Arrangement.ab = "Выравнивание по нижнему краю";
ORYX.I18N.Arrangement.abDesc = "По нижнему краю";
ORYX.I18N.Arrangement.am = "Выравнивание по центру";
ORYX.I18N.Arrangement.amDesc = "По центру";
ORYX.I18N.Arrangement.at = "Выравнивание по верхнему краю";
ORYX.I18N.Arrangement.atDesc = "По верхнему краю";
ORYX.I18N.Arrangement.al = "Выравнивание по левому краю";
ORYX.I18N.Arrangement.alDesc = "По левому краю";
ORYX.I18N.Arrangement.ac = "Выравнивание по центру";
ORYX.I18N.Arrangement.acDesc = "По центру";
ORYX.I18N.Arrangement.ar = "Выравнивание по правому краю";
ORYX.I18N.Arrangement.arDesc = "По правому краю";

if(!ORYX.I18N.BPELSupport) ORYX.I18N.BPELSupport = {};

//ORYX.I18N.BPELSupport.group = "BPEL";
ORYX.I18N.BPELSupport.exp = "Экспортировать BPEL";
ORYX.I18N.BPELSupport.expDesc = "Экспортировать диаграмму в BPEL";
ORYX.I18N.BPELSupport.imp = "Импорт BPEL";
ORYX.I18N.BPELSupport.impDesc = "Импортировать файл BPEL";
ORYX.I18N.BPELSupport.selectFile = "Выбрать файл BPEL для импорта";
ORYX.I18N.BPELSupport.file = "файл";
ORYX.I18N.BPELSupport.impPanel = "Импортировать файл BPEL";
ORYX.I18N.BPELSupport.impBtn = "Импорт";
ORYX.I18N.BPELSupport.content = "содержимое";
ORYX.I18N.BPELSupport.close = "Закрыть";
ORYX.I18N.BPELSupport.error = "Ошибка";
ORYX.I18N.BPELSupport.progressImp = "Импорт...";
ORYX.I18N.BPELSupport.progressExp = "Экспорт...";
ORYX.I18N.BPELSupport.impFailed = "Произошла ошибка импорта! <br/>Пожалуйста, проверьте сообщение об ошибке: <br/><br/>";

if(!ORYX.I18N.BPEL4ChorSupport) ORYX.I18N.BPEL4ChorSupport = {};

//ORYX.I18N.BPEL4ChorSupport.group = "BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.exp = "Экспорт BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.expDesc = "Экспортировать диаграмму в BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.imp = "Импортировать BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.impDesc = "Импортировать файл BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.selectFile = "Выбрать файл BPEL4Chor для импорта";
ORYX.I18N.BPEL4ChorSupport.file = "файл";
ORYX.I18N.BPEL4ChorSupport.impPanel = "Импортировать файл BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.impBtn = "Импорт";
ORYX.I18N.BPEL4ChorSupport.content = "содержимое";
ORYX.I18N.BPEL4ChorSupport.close = "Закрыть";
ORYX.I18N.BPEL4ChorSupport.error = "Ошибка";
ORYX.I18N.BPEL4ChorSupport.progressImp = "Импорт...";
ORYX.I18N.BPEL4ChorSupport.progressExp = "Экспорт...";
ORYX.I18N.BPEL4ChorSupport.impFailed = "Произошла ошибка импорта! <br/>Пожалуйста, проверьте сообщение об ошибке: <br/><br/>";

if(!ORYX.I18N.Bpel4ChorTransformation) ORYX.I18N.Bpel4ChorTransformation = {};

//ORYX.I18N.Bpel4ChorTransformation.group = "BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportBPEL = "Экспорт BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportBPELDesc = "Экспортировать диаграмму в BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportXPDL = "Экспорт XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportXPDLDesc = "Экспортировать диаграмму в XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.warning = "Предупреждение";
ORYX.I18N.Bpel4ChorTransformation.wrongValue = 'Измененное имя должно иметь значение "1" во избежание ошибок трансформации в BPEL4Chor';
ORYX.I18N.Bpel4ChorTransformation.loopNone = 'Тип цикла задачи-получателя сообщения должен быть "None" для трансформации в BPEL4Chor';
ORYX.I18N.Bpel4ChorTransformation.error = "Ошибка";
ORYX.I18N.Bpel4ChorTransformation.noSource = "1 с идентификатором 2 не имеет объекта-источника.";
ORYX.I18N.Bpel4ChorTransformation.noTarget = "1 с идентификатором 2 не имеет объекта-цели.";
ORYX.I18N.Bpel4ChorTransformation.transCall = "Во время трансформации произогла ошибка. 1:2";
ORYX.I18N.Bpel4ChorTransformation.loadingXPDL4ChorExport = "Экспорт XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.loadingBPEL4ChorExport = "Экспорт to BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.noGen = "Данные для трансформации не могут быть сгенерированы: 1\n2\n";

if(!ORYX.I18N.TransformationDownloadDialog) ORYX.I18N.TransformationDownloadDialog = {};

ORYX.I18N.TransformationDownloadDialog.error = "Ошибка";
ORYX.I18N.TransformationDownloadDialog.noResult = "Веб сервис трансформации не отвечает.";
ORYX.I18N.TransformationDownloadDialog.errorParsing = "Ошибка во время анализа диаграммы.";
ORYX.I18N.TransformationDownloadDialog.transResult = "Результаты трансформации";
ORYX.I18N.TransformationDownloadDialog.showFile = "Показать результат трансформации";
ORYX.I18N.TransformationDownloadDialog.downloadFile = "Скачать файл с результатом";
ORYX.I18N.TransformationDownloadDialog.downloadAll = "Скачать все файлы с результатами";

if(!ORYX.I18N.DesynchronizabilityOverlay) ORYX.I18N.DesynchronizabilityOverlay = {};
//TODO desynchronizability is not a correct term
//ORYX.I18N.DesynchronizabilityOverlay.group = "Overlay";
ORYX.I18N.DesynchronizabilityOverlay.name = "Desynchronizability Checker";
ORYX.I18N.DesynchronizabilityOverlay.desc = "Desynchronizability Checker";
ORYX.I18N.DesynchronizabilityOverlay.sync = "Сеть десинхронизированаю";
ORYX.I18N.DesynchronizabilityOverlay.error = "Сеть содержит 1 синтаксических ошибок.";
ORYX.I18N.DesynchronizabilityOverlay.invalid = "Неверный ответ сервера.";

if(!ORYX.I18N.Edit) ORYX.I18N.Edit = {};

//ORYX.I18N.Edit.group = "Edit";
ORYX.I18N.Edit.cut = "Вырезать";
ORYX.I18N.Edit.cutDesc = "Вырезать в буфер Oryx";
ORYX.I18N.Edit.copy = "Копировать";
ORYX.I18N.Edit.copyDesc = "Скопировать в буфер Oryx";
ORYX.I18N.Edit.paste = "Вставить";
ORYX.I18N.Edit.pasteDesc = "Добавить содержимое буфера Oryx на холст";
ORYX.I18N.Edit.del = "Удалить";
ORYX.I18N.Edit.delDesc = "Удалить";

if(!ORYX.I18N.EPCSupport) ORYX.I18N.EPCSupport = {};

//ORYX.I18N.EPCSupport.group = "EPC";
ORYX.I18N.EPCSupport.exp = "Экспорт EPC";
ORYX.I18N.EPCSupport.expDesc = "Экспортировать диаграмму в EPML";
ORYX.I18N.EPCSupport.imp = "Импорт EPC";
ORYX.I18N.EPCSupport.impDesc = "Импортировать файл в EPML";
ORYX.I18N.EPCSupport.progressExp = "Экспорт модели";
ORYX.I18N.EPCSupport.selectFile = "Выбрать файл EPML (.empl) для экспорта.";
ORYX.I18N.EPCSupport.file = "Файл";
ORYX.I18N.EPCSupport.impPanel = "Импортировать файл EPML";
ORYX.I18N.EPCSupport.impBtn = "Импорт";
ORYX.I18N.EPCSupport.close = "Закрыть";
ORYX.I18N.EPCSupport.error = "Ошибка";
ORYX.I18N.EPCSupport.progressImp = "Импорт...";

if(!ORYX.I18N.ERDFSupport) ORYX.I18N.ERDFSupport = {};

ORYX.I18N.ERDFSupport.exp = "Экспорт в ERDF";
ORYX.I18N.ERDFSupport.expDesc = "Экспорт в ERDF";
ORYX.I18N.ERDFSupport.imp = "Имопрт  ERDF";
ORYX.I18N.ERDFSupport.impDesc = "Импорт ERDF";
ORYX.I18N.ERDFSupport.impFailed = "Импорт ERDF не удался.";
ORYX.I18N.ERDFSupport.impFailed2 = "Произошла ошибка во время импорта! <br/>Пожалуйста, ознакомьтесь с сообщением: <br/><br/>";
ORYX.I18N.ERDFSupport.error = "Ошибка";
ORYX.I18N.ERDFSupport.noCanvas = "XML документ не содержит Oryx холста!";
ORYX.I18N.ERDFSupport.noSS = "Oryx холст не содержит определения набора шаблонов!";
ORYX.I18N.ERDFSupport.wrongSS = "Данный набор шаблонов не подходит для данного редактора!";
ORYX.I18N.ERDFSupport.selectFile = "Выберите файл ERDF (.xml) или введите ERDF для импорта!";
ORYX.I18N.ERDFSupport.file = "Файл";
ORYX.I18N.ERDFSupport.impERDF = "Импорт ERDF";
ORYX.I18N.ERDFSupport.impBtn = "Импорт";
ORYX.I18N.ERDFSupport.impProgress = "Импорт...";
ORYX.I18N.ERDFSupport.close = "Закрыть";
//ORYX.I18N.ERDFSupport.deprTitle = "Do you really want to export to eRDF?";
//ORYX.I18N.ERDFSupport.deprText = "Exporting to eRDF is not recommended anymore because the support will be stopped in future versions of the Oryx editor. If possible, export the model to JSON.";

if(!ORYX.I18N.Save) ORYX.I18N.Save = {};

//ORYX.I18N.Save.group = "File";
ORYX.I18N.Save.save = "Сохранить";
ORYX.I18N.Save.saveDesc = "Сохранить";
ORYX.I18N.Save.saveAs = "Сохранить как...";
ORYX.I18N.Save.saveAsDesc = "Сохранить как...";
ORYX.I18N.Save.unsavedData = "Есть несохраненные изменения. Пожалуйста, сохраните изменения перед выходом, иначе они будут утеряны!";
ORYX.I18N.Save.newProcess = "Новый процесс";
ORYX.I18N.Save.saveAsTitle = "Сохранить как...";
ORYX.I18N.Save.saveBtn = "Сохранить";
ORYX.I18N.Save.close = "Закрыть";
ORYX.I18N.Save.savedAs = "Сохранен как";
ORYX.I18N.Save.saved = "Выполняется сохранение";
ORYX.I18N.Save.failed = "Сохранение не выполнено.";
ORYX.I18N.Save.noRights = "У Вас нет прав для сохранения изменений.";
ORYX.I18N.Save.saving = "Сохранение";

if(!ORYX.I18N.File) ORYX.I18N.File = {};

//ORYX.I18N.File.group = "File";
ORYX.I18N.File.print = "Печать";
ORYX.I18N.File.printDesc = "Печать модели";
ORYX.I18N.File.pdf = "Экспорт в PDF";
ORYX.I18N.File.pdfDesc = "Экспорт в PDF";
ORYX.I18N.File.info = "Инфо";
ORYX.I18N.File.infoDesc = "Инфо";
ORYX.I18N.File.genPDF = "Создается PDF";
ORYX.I18N.File.genPDFFailed = "Не удалось создать PDF.";
ORYX.I18N.File.printTitle = "Печать";
ORYX.I18N.File.printMsg = "В настоящий момент мы испытываем проблемы с печатью. Мы рекоммендуем экспортировать диаграмму в PDF и затем распечатать ее. Вы хотите продолжить печать?";

if(!ORYX.I18N.Grouping) ORYX.I18N.Grouping = {};

ORYX.I18N.Grouping.grouping = "Группировка";
ORYX.I18N.Grouping.group = "Группировать";
ORYX.I18N.Grouping.groupDesc = "Группировать";
ORYX.I18N.Grouping.ungroup = "Разгруппировать";
ORYX.I18N.Grouping.ungroupDesc = "Разгруппировать";

if(!ORYX.I18N.IBPMN2BPMN) ORYX.I18N.IBPMN2BPMN = {};

//ORYX.I18N.IBPMN2BPMN.group ="Экспорт";
ORYX.I18N.IBPMN2BPMN.name ="Конвертация IBPMN в BPMN";
ORYX.I18N.IBPMN2BPMN.desc ="Конвертировать IBPMN в BPMN";

if(!ORYX.I18N.Loading) ORYX.I18N.Loading = {};

ORYX.I18N.Loading.waiting ="Пожалуйста, подождите...";

if(!ORYX.I18N.Pnmlexport) ORYX.I18N.Pnmlexport = {};

//ORYX.I18N.Pnmlexport.group ="Экспорт";
ORYX.I18N.Pnmlexport.name ="BPMN в PNML";
ORYX.I18N.Pnmlexport.desc ="Экспортировать в выполняемый PNML и развернуть";

if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};

ORYX.I18N.PropertyWindow.name = "Имя";
ORYX.I18N.PropertyWindow.value = "Значение";
ORYX.I18N.PropertyWindow.clickIcon = "Нажмите на значок";
ORYX.I18N.PropertyWindow.add = "Добавить";
ORYX.I18N.PropertyWindow.rem = "Удалить";
ORYX.I18N.PropertyWindow.complex = "Редактор сложных типов";
ORYX.I18N.PropertyWindow.text = "Редактор текста типа";
ORYX.I18N.PropertyWindow.ok = "Ок";
ORYX.I18N.PropertyWindow.cancel = "Отменить";
ORYX.I18N.PropertyWindow.dateFormat = "m/d/y";

if(!ORYX.I18N.ShapeMenuPlugin) ORYX.I18N.ShapeMenuPlugin = {};

ORYX.I18N.ShapeMenuPlugin.drag = "Тянуть";
ORYX.I18N.ShapeMenuPlugin.clickDrag = "Нажать или тянуть";

if(!ORYX.I18N.SimplePnmlexport) ORYX.I18N.SimplePnmlexport = {};

//ORYX.I18N.SimplePnmlexport.group = "Экспорт";
ORYX.I18N.SimplePnmlexport.name = "Простой экспорт BPMN в PNML";
ORYX.I18N.SimplePnmlexport.desc = "Экспорт в PNML";

if(!ORYX.I18N.StepThroughPlugin) ORYX.I18N.StepThroughPlugin = {};

//ORYX.I18N.StepThroughPlugin.group = "Пошаговая симуляция";
ORYX.I18N.StepThroughPlugin.stepThrough = "Пошаговая симуляция";
ORYX.I18N.StepThroughPlugin.stepThroughDesc = "Пошаговая симуляция BPMN модели";
ORYX.I18N.StepThroughPlugin.undo = "Отменить";
ORYX.I18N.StepThroughPlugin.undoDesc = "Вернуться на шаг назад";
ORYX.I18N.StepThroughPlugin.error = "Пошаговая симуляция диаграммы невозможна.";

if(!ORYX.I18N.SyntaxChecker) ORYX.I18N.SyntaxChecker = {};

//ORYX.I18N.SyntaxChecker.group = "Проверка";
ORYX.I18N.SyntaxChecker.name = "Проверка синтекса";
ORYX.I18N.SyntaxChecker.desc = "Проверить синтакс";
ORYX.I18N.SyntaxChecker.noErrors = "Синтаксических ошибок нет.";
ORYX.I18N.SyntaxChecker.invalid = "Неверный ответ сервера.";

if(!ORYX.I18N.Undo) ORYX.I18N.Undo = {};

//ORYX.I18N.Undo.group = "Отменить";
ORYX.I18N.Undo.undo = "Отменить";
ORYX.I18N.Undo.undoDesc = "Отменить последнее действие";
ORYX.I18N.Undo.redo = "Вернуть";
ORYX.I18N.Undo.redoDesc = "Вернуть последнее действие";

if(!ORYX.I18N.View) ORYX.I18N.View = {};

//ORYX.I18N.View.group = "Масштаб";
ORYX.I18N.View.zoomIn = "Увеличить";
ORYX.I18N.View.zoomInDesc = "Увеличить";
ORYX.I18N.View.zoomOut = "Уменьшить";
ORYX.I18N.View.zoomOutDesc = "Уменьшить";


if(!ORYX.I18N.XFormsSerialization) ORYX.I18N.XFormsSerialization = {};

//ORYX.I18N.XFormsSerialization.group = "Сериализация XForms";
ORYX.I18N.XFormsSerialization.exportXForms = "Экспорт XForms";
ORYX.I18N.XFormsSerialization.exportXFormsDesc = "Экспортировать XForms+XHTML";
ORYX.I18N.XFormsSerialization.importXForms = "Импорт XForms";
ORYX.I18N.XFormsSerialization.importXFormsDesc = "Импортировать XForms+XHTML";
ORYX.I18N.XFormsSerialization.noClientXFormsSupport = "Нет поддержки XForms";
ORYX.I18N.XFormsSerialization.noClientXFormsSupportDesc = "<h2>Ваш браузер не поддерживает XForms. Пожалуйста, установите <a href=\"https://addons.mozilla.org/firefox/addon/824\" target=\"_blank\">Mozilla XForms надстройку</a> для Firefox.</h2>";
ORYX.I18N.XFormsSerialization.ok = "Ок";
ORYX.I18N.XFormsSerialization.selectFile = "Выберите файл XHTML (.xhtml) или введите XForms+XHTML для импорта!";
ORYX.I18N.XFormsSerialization.file = "Файл";
ORYX.I18N.XFormsSerialization.impFailed = "Импорт документа не удался.";
ORYX.I18N.XFormsSerialization.impTitle = "Импорт документа XForms+XHTML";
ORYX.I18N.XFormsSerialization.impButton = "Импорт";
ORYX.I18N.XFormsSerialization.impProgress = "Импорт...";
ORYX.I18N.XFormsSerialization.close = "Закрыть";


if(!ORYX.I18N.TreeGraphSupport) ORYX.I18N.TreeGraphSupport = {};

ORYX.I18N.TreeGraphSupport.syntaxCheckName = "Проверка синтаксиса";
ORYX.I18N.TreeGraphSupport.group = "Поддержка деревьев";
ORYX.I18N.TreeGraphSupport.syntaxCheckDesc = "Проверить синтаксис структуры дерева";

ORYX.I18N.PropertyWindow.title = "Свойства";

if(!ORYX.I18N.ShapeRepository) ORYX.I18N.ShapeRepository = {};
ORYX.I18N.ShapeRepository.title = "Репозиторий фигур";

ORYX.I18N.Save.dialogDesciption = "Пожалуйста, введите имя и описание.";
ORYX.I18N.Save.dialogLabelTitle = "Название";
ORYX.I18N.Save.dialogLabelDesc = "Описание";
ORYX.I18N.Save.dialogLabelType = "Тип";

ORYX.I18N.Validator.name = "Проверка BPMN";
ORYX.I18N.Validator.description = "Проверка для BPMN";

ORYX.I18N.SSExtensionLoader.labelImport = "Импорт";
ORYX.I18N.SSExtensionLoader.labelCancel = "Отмена";

Ext.MessageBox.buttonText.yes = "Да";
Ext.MessageBox.buttonText.no = "Нет";
Ext.MessageBox.buttonText.cancel = "Отмена";
Ext.MessageBox.buttonText.ok = "Ок"; 


///** New Language Properties: 08.12.2008 */
//
//ORYX.I18N.PropertyWindow.title = "Properties";
//
//if(!ORYX.I18N.ShapeRepository) ORYX.I18N.ShapeRepository = {};
//ORYX.I18N.ShapeRepository.title = "Shape Repository";
//
//ORYX.I18N.Save.dialogDesciption = "Please enter a name and a description.";
//ORYX.I18N.Save.dialogLabelTitle = "Title";
//ORYX.I18N.Save.dialogLabelDesc = "Description";
//ORYX.I18N.Save.dialogLabelType = "Type";
//
//ORYX.I18N.Validator.name = "BPMN Validator";
//ORYX.I18N.Validator.description = "Validation for BPMN";
//
//ORYX.I18N.SSExtensionLoader.labelImport = "Import";
//ORYX.I18N.SSExtensionLoader.labelCancel = "Cancel";
//
//Ext.MessageBox.buttonText.yes = "Yes";
//Ext.MessageBox.buttonText.no = "No";
//Ext.MessageBox.buttonText.cancel = "Cancel";
//Ext.MessageBox.buttonText.ok = "OK";
//
//
//
///** New Language Properties: 28.01.2009 */
//ORYX.I18N.BPMN2XPDL.group = "Export";
//ORYX.I18N.BPMN2XPDL.xpdlExport = "Export to XPDL";

/*ORYX.I18N.BPMN2PNConverter = {
  name: "Convert to Petri net",
  desc: "Converts BPMN diagrams to Petri nets",
  group: "Export",
  error: "Error",
  errors: {
    server: "Couldn't import BPNM diagram.",
    noRights: "Don't you have read permissions on given model?",
    notSaved: "Model must be saved and reopened for using Petri net exporter!"
  },
  progress: {
      status: "Status",
      importingModel: "Importing BPMN Model",
      fetchingModel: "Fetching",
      convertingModel: "Converting",
      renderingModel: "Rendering"
  }
}*/

/** New Language Properties: 08.05.2009 */
//if(!ORYX.I18N.BPMN2XHTML) ORYX.I18N.BPMN2XHTML = {};
//ORYX.I18N.BPMN2XHTML.group = "Export";
//ORYX.I18N.BPMN2XHTML.XHTMLExport = "Export XHTML Documentation";

/** New Language Properties: 09.05.2009 */
//if(!ORYX.I18N.JSONImport) ORYX.I18N.JSONImport = {};
//
//ORYX.I18N.JSONImport.title = "JSON Import";
//ORYX.I18N.JSONImport.wrongSS = "The stencil set of the imported file ({0}) does not match to the loaded stencil set ({1})."
//ORYX.I18N.JSONImport.invalidJSON = "The JSON to import is invalid.";

if(!ORYX.I18N.Feedback) ORYX.I18N.Feedback = {};
ORYX.I18N.Feedback.name = "Отзыв";
ORYX.I18N.Feedback.failure = "Ошибка";
ORYX.I18N.Feedback.failureMsg = "К сожалению сообщение не может быть отправлено. Произошла ошибка приложения. Пожалуйста, повторите попытку отправки сообщения или свяжитесь с нами через http://code.google.com/p/oryx-editor/";
ORYX.I18N.Feedback.submit = "Отправить";

ORYX.I18N.Feedback.emailDesc = "Ваш адрес электронной почты";
ORYX.I18N.Feedback.titleDesc = "Резюмируйте ваше сообщение коротким заголовком";
ORYX.I18N.Feedback.descriptionDesc = "Опишите ваше предложение, вопрос или проблему."
ORYX.I18N.Feedback.info = '<p>Oryx является исследовательской платформой, предназначенной для поддержки исследователей в области управления бизнес-процессами, посредством предоставления им расширяемой платформы для экспериментальной проверки результатов. </p><p><a href="http://bpt.hpi.uni-potsdam.de/Oryx/DeveloperNetwork" target="_blank">Мы</a> ведем напряженную работу для того, чтобы предоставить вам надежную систему и рады предоставить вам <a href="http://bpt.hpi.uni-potsdam.de/Oryx/ReleaseNotes" target="_blank">новейшие разработки</a> нашей платформы.</p><p>Если у вас есть идея как улучшить Oryx, или вопрос о работе платформы, или вы хотите сообщить о проблеме<strong> Пожалуйста, сделайте это здесь.</strong></p>'; // general info will be shown, if no subject specific info is given
// list subjects in reverse order of appearance!
ORYX.I18N.Feedback.subjects = [
   {
       id: "question",   // ansi-compatible name
       name: "Вопрос", // natural name
       description: "Задайте свой вопрос! \nПожалуйста, предоставьте нам как можно больше информации, чтобы мы могли быстрее дать вам точный ответ.", // default text for the description text input field
       info: "", // optional field to give more info
   },
   {
       id: "problem",   // ansi-compatible name
       name: "Проблема", // natural name
       description: "Извините за неудобство. Предоставьте нам отзыв о проблеме и мы попытаемся решить ее. Пожалуйста, опишите проблему как можно подробнее.", // default text for the description text input field
       info: "", // optional field to give more info
   },
   {
       id: "idea",   // ansi-compatible name
       name: "Предложение", // natural name
       description: "Поделитесь своими предложениями и идеями!", // default text for the description text input field
       info: "", // optional field to give more info
   }
];



//if(!ORYX.I18N.Feedback) ORYX.I18N.Feedback = {};
//
//ORYX.I18N.Feedback.name = "Feedback";
//ORYX.I18N.Feedback.desc = "Contact us for any kind of feedback!";
//ORYX.I18N.Feedback.pTitle = "Contact us for any kind of feedback!";
//ORYX.I18N.Feedback.pName = "Name";
//ORYX.I18N.Feedback.pEmail = "E-Mail";
//ORYX.I18N.Feedback.pSubject = "Subject";
//ORYX.I18N.Feedback.pMsg = "Description/Message";
//ORYX.I18N.Feedback.pEmpty = "* Please provide as detailed information as possible so that we can understand your request.\n* For bug reports, please list the steps how to reproduce the problem and describe the output you expected.";
//ORYX.I18N.Feedback.pAttach = "Attach current model";
//ORYX.I18N.Feedback.pAttachDesc = "This information can be helpful for debugging purposes. If your model contains some sensitive data, remove it before or uncheck this behavior.";
//ORYX.I18N.Feedback.pBrowser = "Information about your browser and environment";
//ORYX.I18N.Feedback.pBrowserDesc = "This information has been auto-detected from your browser. It can be helpful if you encountered a bug associated with browser-specific behavior.";
//ORYX.I18N.Feedback.submit = "Send Message";
//ORYX.I18N.Feedback.sending = "Sending message ...";
//ORYX.I18N.Feedback.success = "Success";
//ORYX.I18N.Feedback.successMsg = "Thank you for your feedback!";
//ORYX.I18N.Feedback.failure = "Failure";
//ORYX.I18N.Feedback.failureMsg = "The message could not be sent!";

/** New Language Properties: 11.05.2009 */
//if(!ORYX.I18N.BPMN2DTRPXMI) ORYX.I18N.BPMN2DTRPXMI = {};
//ORYX.I18N.BPMN2DTRPXMI.group = "Export";
//ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExport = "Export to XMI (Design Thinking)";
//ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExportDescription = "Exports current model to XMI (requires stencil set extension 'BPMN Subset for Design Thinking')";

/** New Language Properties: 14.05.2009 */
//if(!ORYX.I18N.RDFExport) ORYX.I18N.RDFExport = {};
//ORYX.I18N.RDFExport.group = "Export";
//ORYX.I18N.RDFExport.rdfExport = "Export to RDF";

//ORYX.I18N.RDFExport.rdfExportDescription = "Exports current model to the XML serialization defined for the Resource Description Framework (RDF)";

/** New Language Properties: 08.09.2009 */
//if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};
//ORYX.I18N.PropertyWindow.oftenUsed = "Often used";
//ORYX.I18N.PropertyWindow.moreProps = "More Properties";

/** New Language Properties 01.10.2009 */
//if(!ORYX.I18N.SyntaxChecker.BPMN2) ORYX.I18N.SyntaxChecker.BPMN2 = {};
//
//ORYX.I18N.SyntaxChecker.BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = "A Data Input must not have any incoming Data Associations.";
//ORYX.I18N.SyntaxChecker.BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = "A Data Output must not have any outgoing Data Associations.";
//ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS = "Targets of Eventbased Gateways may only have one incoming Sequence Flow.";

/** New Language Properties 02.10.2009 */
//ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS = "An Event Gateway must have two or more outgoing Sequence Flows.";
//ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION = "If Message Intermediate Events are used in the configuration, then Receive Tasks must not be used and vice versa.";
//ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_TRIGGER = "Only the following Intermediate Event triggers are valid: Message, Signal, Timer, Conditional and Multiple.";
//ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION = "The outgoing Sequence Flows of the Event Gateway must not have a condition Expression.";
//ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_NOT_INSTANTIATING = "The Gateway does not meet the conditions to instantiate the Process. Please referr to the manual.";

/** New Language Properties 05.10.2009 */
//ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_MIXED_FAILURE = "The Gateway must have both multiple incoming and outgoing Sequence Flows.";
//ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE = "The Gateway must have multiple incoming but most NOT have multiple outgoing Sequence Flows.";
//ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE = "The Gateway must NOT have multiple incoming but must have multiple outgoing Sequence Flows.";
//ORYX.I18N.SyntaxChecker.BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW = "A Gateway must have a minimum of one outgoing Sequence Flow.";
//ORYX.I18N.SyntaxChecker.BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT = "Receive Tasks used in Event Gateway configurations must not have any attached Intermediate Events.";
//ORYX.I18N.SyntaxChecker.BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION = "An Event Sub-Process must not have any incoming or outgoing Sequence Flow.";

/** New Language Properties 13.10.2009 */
//ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_CONNECTED = "At least on side of the Message Flow has to be connected.";

/** New Language Properties 05.11.2009 */
//if(!ORYX.I18N.RESIZE) ORYX.I18N.RESIZE = {};
//ORYX.I18N.RESIZE.tipGrow = "Increase canvas size:";
//ORYX.I18N.RESIZE.tipShrink = "Decrease canvas size:";
//ORYX.I18N.RESIZE.N = "Top";
//ORYX.I18N.RESIZE.W = "Left";
//ORYX.I18N.RESIZE.S ="Down";
//ORYX.I18N.RESIZE.E ="Right";

/** New Language Properties 24.11.2009 */
//ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_MESSAGES = "A Choreography Acitivty may only have one initiating Message.";
//ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_ALLOWED = "A Message Flow is not allowed here.";

/** New Language Properties 27.11.2009 */
//ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS = "An Eventbased Gateway that is not instantiating must have a minimum of one incoming Sequence Flow.";
//ORYX.I18N.SyntaxChecker.BPMN2_TOO_FEW_INITIATING_PARTICIPANTS = "A Choreography Activity must have one initiating Participant.";
//ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_PARTICIPANTS = "A Choreography Acitivity must not have more than one initiating Participant."

//ORYX.I18N.SyntaxChecker.COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = "The communication must be connected to at least two participants.";
//ORYX.I18N.SyntaxChecker.MESSAGEFLOW_START_MUST_BE_PARTICIPANT = "The message flow's source must be a participant.";
//ORYX.I18N.SyntaxChecker.MESSAGEFLOW_END_MUST_BE_PARTICIPANT = "The message flow's target must be a participant.";
//ORYX.I18N.SyntaxChecker.CONV_LINK_CANNOT_CONNECT_CONV_NODES = "The conversation link must connect a communication or sub conversation node with a participant.";
