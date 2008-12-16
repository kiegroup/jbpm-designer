/**
 * @author Sergey Smirnov
 * 
 * contains all strings for Russian language (ru)
 * 
 */


// namespace
if(window.Repository == undefined) Repository = {};
if(window.Repository.I18N == undefined) Repository.I18N = {};

Repository.I18N.Language = "ru"; //Pattern <ISO language code>_<ISO country code> in lower case!

// Repository strings

if(!Repository.I18N.Repository) Repository.I18N.Repository = {};

Repository.I18N.Repository.openIdSample = "your.openid.ru";
Repository.I18N.Repository.sayHello = "Здравствуйте";
Repository.I18N.Repository.login = "Войти";
Repository.I18N.Repository.logout = "Выйти";

Repository.I18N.Repository.viewMenu = "Вид";
Repository.I18N.Repository.viewMenuTooltip = "Изменить вид";

Repository.I18N.Repository.windowTimeoutMessage = "Редактор ещё не запустился. Пожалуйста, удостоверьтесь, что ваш браузер разрешает всплывающие окна.";
Repository.I18N.Repository.windowTitle = "Oryx";

Repository.I18N.Repository.noSaveTitle = "Сообщение";
Repository.I18N.Repository.noSaveMessage = "Неавторизованные пользователи не могут сохранять модели. Вы хотите начать моделирование?";
Repository.I18N.Repository.yes = "Да";
Repository.I18N.Repository.no = "Нет";

Repository.I18N.Repository.leftPanelTitle = "Упорядочить модели";
Repository.I18N.Repository.rightPanelTitle = "Информация о модели";
Repository.I18N.Repository.bottomPanelTitle = "Комментарии";

Repository.I18N.Repository.loadingText = "Загружается репозиторий..."

Repository.I18N.Repository.errorText = "<b style='font-size:13px'>Извините!</b><br/>Диаграммы редактируемы только в <a href='http://www.mozilla.com/en-US/products/firefox/' target='_blank'>браузере Firefox</a>.";
Repository.I18N.Repository.errorAuthor = "Автор:"
Repository.I18N.Repository.errorTitle = "Название:"
// Plugin strings
 
// NewModel Plugin
if(!Repository.I18N.NewModel) Repository.I18N.NewModel = {};

Repository.I18N.NewModel.name = "Новая модель";
Repository.I18N.NewModel.tooltipText = "Новая модель выбранного типа";

// TableView Plugin

if(!Repository.I18N.TableView) Repository.I18N.TableView = {};
Repository.I18N.TableView.name = "Таблица";

if(!Repository.I18N.TableView.columns) Repository.I18N.TableView.columns = {};
Repository.I18N.TableView.columns.title = "Название";
Repository.I18N.TableView.columns.type = "Тип модели";
Repository.I18N.TableView.columns.author = "Автор";
Repository.I18N.TableView.columns.summary = "Описание";
Repository.I18N.TableView.columns.creationDate = "Дата создания";
Repository.I18N.TableView.columns.lastUpdate = "Последнее изменение";
Repository.I18N.TableView.columns.id = "Идентификатор";

if(!Repository.I18N.IconView) Repository.I18N.IconView = {};
Repository.I18N.IconView.name = "Эскизы";


if(!Repository.I18N.FullView) Repository.I18N.FullView = {};
Repository.I18N.FullView.name = "Предварительный просмотр";

Repository.I18N.FullView.createdLabel = "Создан";
Repository.I18N.FullView.fromLabel = "Автор";
Repository.I18N.FullView.changeLabel = "Последнее изменение";
Repository.I18N.FullView.descriptionLabel = "Описание";
Repository.I18N.FullView.editorLabel = "Редактировать";
;
// TypeFilter Plugin

if(!Repository.I18N.TypeFilter) Repository.I18N.TypeFilter = {};
Repository.I18N.TypeFilter.name = "Фильтр по типам";

// TagFilter Plugin

if(!Repository.I18N.TagFilter) Repository.I18N.TagFilter = {};
Repository.I18N.TagFilter.name = "Фильтр по тэгам";

// Friend Filter Plugin

if(!Repository.I18N.FriendFilter) Repository.I18N.FriendFilter = {};
Repository.I18N.FriendFilter.name = "Фильтр по друзьям";

if(!Repository.I18N.TagInfo) Repository.I18N.TagInfo = {};
Repository.I18N.TagInfo.name = "Тэги"
Repository.I18N.TagInfo.deleteText = "Удалить"
Repository.I18N.TagInfo.none = "нет"
Repository.I18N.TagInfo.shared = "Общие тэги:"
Repository.I18N.TagInfo.newTag = "Новый тэг"
Repository.I18N.TagInfo.addTag = "Добавить"
		
if(!Repository.I18N.ModelRangeSelection) Repository.I18N.ModelRangeSelection = {};
Repository.I18N.ModelRangeSelection.previous = "« Предыдущая страница"
Repository.I18N.ModelRangeSelection.next = "Следующая страница »"
Repository.I18N.ModelRangeSelection.nextSmall = "»"
Repository.I18N.ModelRangeSelection.previousSmall = "«"
Repository.I18N.ModelRangeSelection.last = "Последний"
Repository.I18N.ModelRangeSelection.first = "Первый"
Repository.I18N.ModelRangeSelection.modelsOfZero = "(0 моделей)" 
Repository.I18N.ModelRangeSelection.modelsOfOne = "(#{from} из #{size} моделей)" 
Repository.I18N.ModelRangeSelection.modelsOfMore = "(#{from}-#{to} из #{size} моделей)" 

if(!Repository.I18N.AccessInfo) Repository.I18N.AccessInfo = {};
Repository.I18N.AccessInfo.name = "Права доступа"
Repository.I18N.AccessInfo.publicText = "Открытый доступ";
Repository.I18N.AccessInfo.notPublicText  = "Ограниченный доступ";
Repository.I18N.AccessInfo.noneIsSelected  = "Ничего не выбрано";
Repository.I18N.AccessInfo.none  = "нет";
Repository.I18N.AccessInfo.deleteText  = "Удалить";
Repository.I18N.AccessInfo.publish  = "Опубликовать";
Repository.I18N.AccessInfo.unPublish  = "Прекратить публикацию";
Repository.I18N.AccessInfo.owner = "Владелец:"
Repository.I18N.AccessInfo.contributer = "Редакторы:"
Repository.I18N.AccessInfo.reader = "Пользователи с правом просмотра:"
Repository.I18N.AccessInfo.openid = "OpenID"
Repository.I18N.AccessInfo.addReader = "Добавить с правом просмотра"
Repository.I18N.AccessInfo.addContributer = "Добавить редактора"
Repository.I18N.AccessInfo.several = "несколько"
Repository.I18N.AccessInfo.noWritePermission = "Нет прав записи"
 

if(!Repository.I18N.SortingSupport) Repository.I18N.SortingSupport = {};
Repository.I18N.SortingSupport.name = "Сортировка";
Repository.I18N.SortingSupport.lastchange = "По дате изменения"
Repository.I18N.SortingSupport.title = "По названию"
Repository.I18N.SortingSupport.rating = "По рейтингу"

if(!Repository.I18N.Export) Repository.I18N.Export = {};
Repository.I18N.Export.name = "Экспорт";
Repository.I18N.Export.title = "Доступные форматы экспорта:"
Repository.I18N.Export.onlyOne = "Для экспорта необходимо выбрать одну модель!"

if(!Repository.I18N.UpdateButton) Repository.I18N.UpdateButton = {};
Repository.I18N.UpdateButton.name = "Обновить"

if(!Repository.I18N.Edit) Repository.I18N.Edit = {};
Repository.I18N.Edit.name = "Редактировать"
Repository.I18N.Edit.editSummary = "Редактировать описание"
Repository.I18N.Edit.editName = "Редактировать название"
Repository.I18N.Edit.nameText = "Название"
Repository.I18N.Edit.summaryText = "Описание"
Repository.I18N.Edit.editText = "Сохранить изменения"
Repository.I18N.Edit.deleteText = "Удалить"
Repository.I18N.Edit.noWriteAccess = "Право удаления имеет лишь владелец"
Repository.I18N.Edit.deleteOneText = "'#{title}'" 
Repository.I18N.Edit.deleteMoreText = "Все #{size} выбранные модели" 


if(!Repository.I18N.Rating) Repository.I18N.Rating = {};
Repository.I18N.Rating.name = "Рейтинг"
Repository.I18N.Rating.total = "Общий рейтинг:"
Repository.I18N.Rating.my = "Мой рейтинг:"
Repository.I18N.Rating.totalNoneText = "нет оценок" 
Repository.I18N.Rating.totalOneText = "#{totalScore} (#{totalVotes})" 
Repository.I18N.Rating.totalMoreText = "Из #{modelCount} моделей #{voteCount} оценены в среднем на #{totalScore} (#{totalVotes})"

if(!Repository.I18N.RatingFilter) Repository.I18N.RatingFilter = {};
Repository.I18N.RatingFilter.name = "Фильтр по рейтингу"

if(!Repository.I18N.AccessFilter) Repository.I18N.AccessFilter = {};
Repository.I18N.AccessFilter.name = "Фильтр по правам доступа"
Repository.I18N.AccessFilter.mine = "Мои"
Repository.I18N.AccessFilter.reader = "Модели, доступные мне для чтения"
Repository.I18N.AccessFilter.writer = "Модели, доступные мне для редактирования"
Repository.I18N.AccessFilter.publicText = "Модели с открытам доступом"
