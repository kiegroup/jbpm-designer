if(!ORYX) var ORYX = {};

if(!ORYX.I18N) ORYX.I18N = {};

ORYX.I18N.Language = "ja"; //Pattern <ISO language code>_<ISO country code> in lower case!

if(!ORYX.I18N.Oryx) ORYX.I18N.Oryx = {};

ORYX.I18N.Oryx.title		= ORYX.TITLE;
ORYX.I18N.Oryx.noBackendDefined	= "注意! \nバックエンドが定義されていません。\n 要求されたモデルはロードできません。 保存されたプラグイン定義をロードしてください";
//ORYX.I18N.Oryx.pleaseWait 	= "　ロード中　お待ちください...";
ORYX.I18N.Oryx.pleaseWait  = '<center>  <img src="/org.jbpm.designer.jBPMDesigner/images/jbpm_logo.png" align="middle"/>  <b>jBPM Web Designer ロード中. お待ちください...</b></center>';
ORYX.I18N.Oryx.notLoggedOn = "ログオンしていません";
ORYX.I18N.Oryx.editorOpenTimeout = "エディターはまだ開始していません。ポップアップブロッカーが無効もしくはこのサイト内ではポップアップが許可されることを確認してください。このサイト内では広告は表示されません。";

if(!ORYX.I18N.AddDocker) ORYX.I18N.AddDocker = {};

ORYX.I18N.AddDocker.group = "Docker";
ORYX.I18N.AddDocker.add = "ドッカーに追加";
ORYX.I18N.AddDocker.addDesc = "クリックして接線にドッカーを追加";
ORYX.I18N.AddDocker.del = "削除";
ORYX.I18N.AddDocker.delDesc = "ドッカーを削除";

if(!ORYX.I18N.ShapeConnector) ORYX.I18N.ShapeConnector = {};

ORYX.I18N.ShapeConnector.group = "コネクター";
ORYX.I18N.ShapeConnector.add = "接続 シェイプ";
ORYX.I18N.ShapeConnector.addDesc = "マークして 複数のノードを任意の順序に接続";

if(!ORYX.I18N.SSExtensionLoader) ORYX.I18N.SSExtensionLoader = {};

ORYX.I18N.SSExtensionLoader.group = "ステンシルセット";
ORYX.I18N.SSExtensionLoader.add = "追加";
ORYX.I18N.SSExtensionLoader.addDesc = "拡張ステンシルセットを追加";
ORYX.I18N.SSExtensionLoader.loading = "拡張ステンシルセットをローディング";
ORYX.I18N.SSExtensionLoader.noExt = "利用できる拡張はないか全ての利用できる拡張はすでにロードされています。";
ORYX.I18N.SSExtensionLoader.failed1 = "拡張ステンシルセットのロードに失敗しました。構成ファイルが正しくありません。";
ORYX.I18N.SSExtensionLoader.failed2 = "拡張ステンシルセットのロードに失敗しました。要求にたいしてエラーが返されました。";
ORYX.I18N.SSExtensionLoader.panelTitle = "拡張ステンシルセット";
ORYX.I18N.SSExtensionLoader.panelText = "ロードしたい拡張ステンシルセットを選択してください。";

if(!ORYX.I18N.AdHocCC) ORYX.I18N.AdHocCC = {};

ORYX.I18N.AdHocCC.group = "アドホック";
ORYX.I18N.AdHocCC.compl = "完了条件を編集";
ORYX.I18N.AdHocCC.complDesc = "アドホック アクティビティの完了条件を編集";
ORYX.I18N.AdHocCC.notOne = "要素が一つに選択されていません！";
ORYX.I18N.AdHocCC.nodAdHocCC = "選択された要素はアドホック完了条件ではありません！";
ORYX.I18N.AdHocCC.selectTask = "タスクを選択...";
ORYX.I18N.AdHocCC.selectState = "状態を選択...";
ORYX.I18N.AdHocCC.addExp = "表現を追加";
ORYX.I18N.AdHocCC.selectDataField = "データ フィールドを選択...";
ORYX.I18N.AdHocCC.enterEqual = "イコールとなる値を入力...";
ORYX.I18N.AdHocCC.and = "and";
ORYX.I18N.AdHocCC.or = "or";
ORYX.I18N.AdHocCC.not = "not";
ORYX.I18N.AdHocCC.clearCC = "完了条件をクリア";
ORYX.I18N.AdHocCC.editCC = "アドホック 完了条件を編集";
ORYX.I18N.AdHocCC.addExecState = "実行状態の表現を追加: ";
ORYX.I18N.AdHocCC.addDataExp = "データ表現を追加: ";
ORYX.I18N.AdHocCC.addLogOp = "論理オペレーターを追加: ";
ORYX.I18N.AdHocCC.curCond = "現在の完了条件: ";

if(!ORYX.I18N.AMLSupport) ORYX.I18N.AMLSupport = {};

ORYX.I18N.AMLSupport.group = "EPC";
ORYX.I18N.AMLSupport.imp = "AMLファイルをインポート";
ORYX.I18N.AMLSupport.impDesc = "Aris 7 AMLファイルをインポート";
ORYX.I18N.AMLSupport.failed = "AMLファイルのインポートに失敗しました。選択したファイルが正しいAMLファイルか確認してください。エラーメッセージ: ";
ORYX.I18N.AMLSupport.failed2 = "AMLファイルのインポートに失敗しました: ";
ORYX.I18N.AMLSupport.noRights = "マルチプルEPCダイアグラムをインポートする権限がありません（ログインしてください）";
ORYX.I18N.AMLSupport.panelText = "インポートするAML(.xml)ファイルを選択";
ORYX.I18N.AMLSupport.file = "ファイル";
ORYX.I18N.AMLSupport.importBtn = "AMLファイルをインポート";
ORYX.I18N.AMLSupport.get = "ダイアグラムを取得...";
ORYX.I18N.AMLSupport.close = "閉じる";
ORYX.I18N.AMLSupport.title = "タイトル";
ORYX.I18N.AMLSupport.selectDiagrams = "インポートしたいダイアグラムを選択 <br/> モデルが一つ選択されると現在のエディターにインポートされます。モデルが一つ以上選択されるとモデルは直接レポジトリに格納されます。";
ORYX.I18N.AMLSupport.impText = "インポート";
ORYX.I18N.AMLSupport.impProgress = "インポート中...";
ORYX.I18N.AMLSupport.cancel = "キャンセル";
ORYX.I18N.AMLSupport.name = "名前";
ORYX.I18N.AMLSupport.allImported = "インポートされた全てのダイアグラム";
ORYX.I18N.AMLSupport.ok = "OK";

if(!ORYX.I18N.Arrangement) ORYX.I18N.Arrangement = {};

ORYX.I18N.Arrangement.groupZ = "Z-Order";
ORYX.I18N.Arrangement.btf = "前面に移動";
ORYX.I18N.Arrangement.btfDesc = "前面に移動";
ORYX.I18N.Arrangement.btb = "背面に移動";
ORYX.I18N.Arrangement.btbDesc = "背面に移動";
ORYX.I18N.Arrangement.bf = "前方に移動";
ORYX.I18N.Arrangement.bfDesc = "前方に移動";
ORYX.I18N.Arrangement.bb = "後方に移動";
ORYX.I18N.Arrangement.bbDesc = "後方に移動";
ORYX.I18N.Arrangement.groupA = "Alignment";
ORYX.I18N.Arrangement.ab = "下揃え";
ORYX.I18N.Arrangement.abDesc = "下揃え";
ORYX.I18N.Arrangement.am = "上下中央揃え";
ORYX.I18N.Arrangement.amDesc = "上下中央揃え";
ORYX.I18N.Arrangement.at = "上揃え";
ORYX.I18N.Arrangement.atDesc = "上揃え";
ORYX.I18N.Arrangement.al = "左揃え";
ORYX.I18N.Arrangement.alDesc = "左揃え";
ORYX.I18N.Arrangement.ac = "左右中央揃え";
ORYX.I18N.Arrangement.acDesc = "左右中央揃え";
ORYX.I18N.Arrangement.ar = "右揃え";
ORYX.I18N.Arrangement.arDesc = "右揃え";
ORYX.I18N.Arrangement.as = "同じサイズに揃える";
ORYX.I18N.Arrangement.asDesc = "同じサイズに揃える";

if(!ORYX.I18N.BPELSupport) ORYX.I18N.BPELSupport = {};

ORYX.I18N.BPELSupport.group = "BPEL";
ORYX.I18N.BPELSupport.exp = "BPELをエクスポート";
ORYX.I18N.BPELSupport.expDesc = "ダイアグラムをBPELにエクスポート";
ORYX.I18N.BPELSupport.imp = "BPELをインポート";
ORYX.I18N.BPELSupport.impDesc = "BPELファイルをインポート";
ORYX.I18N.BPELSupport.selectFile = "インポートするBPELファイルを選択";
ORYX.I18N.BPELSupport.file = "ファイル";
ORYX.I18N.BPELSupport.impPanel = "BPELファイルをインポート";
ORYX.I18N.BPELSupport.impBtn = "インポート";
ORYX.I18N.BPELSupport.content = "コンテンツ";
ORYX.I18N.BPELSupport.close = "閉じる";
ORYX.I18N.BPELSupport.error = "エラー";
ORYX.I18N.BPELSupport.progressImp = "インポート...";
ORYX.I18N.BPELSupport.progressExp = "エクスポート...";
ORYX.I18N.BPELSupport.impFailed = "インポート中にエラーが発生しました！ <br/>エラーメッセージを確認してください: <br/><br/>";

if(!ORYX.I18N.BPELLayout) ORYX.I18N.BPELLayout = {};

ORYX.I18N.BPELLayout.group = "BPELレイアウト";
ORYX.I18N.BPELLayout.disable = "レイアウトを無効";
ORYX.I18N.BPELLayout.disDesc = "自動レイアウトのプラグインを無効";
ORYX.I18N.BPELLayout.enable = "レイアウトを有効";
ORYX.I18N.BPELLayout.enDesc = "自動レイアウトのプラグインを有効";

if(!ORYX.I18N.BPEL4Chor2BPELSupport) ORYX.I18N.BPEL4Chor2BPELSupport = {};

ORYX.I18N.BPEL4Chor2BPELSupport.group = "BPEL4Chor";
ORYX.I18N.BPEL4Chor2BPELSupport.exp = "BPELにエクスポート";
ORYX.I18N.BPEL4Chor2BPELSupport.expDesc = "ダイアグラムをBPELにエクスポート";

if(!ORYX.I18N.BPEL4ChorSupport) ORYX.I18N.BPEL4ChorSupport = {};

ORYX.I18N.BPEL4ChorSupport.group = "BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.exp = "BPEL4Chorをエクスポート";
ORYX.I18N.BPEL4ChorSupport.expDesc = "ダイアグラムをBPEL4Chorへエクスポート";
ORYX.I18N.BPEL4ChorSupport.imp = "BPEL4Chorをインポート";
ORYX.I18N.BPEL4ChorSupport.impDesc = "BPEL4Chorファイルをインポート";
ORYX.I18N.BPEL4ChorSupport.gen = "BPEL4Chorジェネレーター";
ORYX.I18N.BPEL4ChorSupport.genDesc = "BPEL4Chor プロパティーの値を生成 (例: sender of messageLink)";
ORYX.I18N.BPEL4ChorSupport.selectFile = "インポートするBPEL4Chorを選択";
ORYX.I18N.BPEL4ChorSupport.file = "ファイル";
ORYX.I18N.BPEL4ChorSupport.impPanel = "BPEL4Chorファイルをインポート";
ORYX.I18N.BPEL4ChorSupport.impBtn = "インポート";
ORYX.I18N.BPEL4ChorSupport.content = "コンテンツ";
ORYX.I18N.BPEL4ChorSupport.close = "閉じる";
ORYX.I18N.BPEL4ChorSupport.error = "エラー";
ORYX.I18N.BPEL4ChorSupport.progressImp = "インポート...";
ORYX.I18N.BPEL4ChorSupport.progressExp = "エクスポート...";
ORYX.I18N.BPEL4ChorSupport.impFailed = "インポート中にエラーが発生しました！ <br/>エラーメッセージをチェックしてください: <br/><br/>";

if(!ORYX.I18N.Bpel4ChorTransformation) ORYX.I18N.Bpel4ChorTransformation = {};

ORYX.I18N.Bpel4ChorTransformation.group = "エクスポート";
ORYX.I18N.Bpel4ChorTransformation.exportBPEL = "BPEL4Chorをエクスポート";
ORYX.I18N.Bpel4ChorTransformation.exportBPELDesc = "ダイアグラムをBPEL4Chorへエクスポート";
ORYX.I18N.Bpel4ChorTransformation.exportXPDL = "XPDL4Chorをエクスポート";
ORYX.I18N.Bpel4ChorTransformation.exportXPDLDesc = "ダイアグラムをXPDL4Chorへエクスポート";
ORYX.I18N.Bpel4ChorTransformation.warning = "警告";
ORYX.I18N.Bpel4ChorTransformation.wrongValue = 'BPEL4Chorへの変換中のエラーを避けるため変更された名前は値"1"をもつ必要があります';
ORYX.I18N.Bpel4ChorTransformation.loopNone = 'BPEL4Chorへ変換可能にするため回復したタスクのループタイプは"None"にする必要があります';
ORYX.I18N.Bpel4ChorTransformation.error = "エラー";
ORYX.I18N.Bpel4ChorTransformation.noSource = "1 with id 2 has no source object.";
ORYX.I18N.Bpel4ChorTransformation.noTarget = "1 with id 2 has no target object.";
ORYX.I18N.Bpel4ChorTransformation.transCall = "変換処理をコール中にエラーが発生しました。 1:2";
ORYX.I18N.Bpel4ChorTransformation.loadingXPDL4ChorExport = "XPDL4Chorへエクスポート";
ORYX.I18N.Bpel4ChorTransformation.loadingBPEL4ChorExport = "BPEL4Chorへエクスポート";
ORYX.I18N.Bpel4ChorTransformation.noGen = "変換インプットは生成されませんでした: 1\n2\n";

ORYX.I18N.BPMN2PNConverter = {
  name: "Petri netへコンバート",
  desc: "BPMNダイアグラムをPetri netへコンバート",
  group: "エクスポート",
  error: "エラー",
  errors: {
    server: "BPNMダイアグラムがインポートできませんでした。",
    noRights: "該当モデルに読み込み許可はありますか？",
    notSaved: "モデルを保存しPetri netエクスポーターを使用するために再オープンする必要があります。"
  },
  progress: {
      status: "状態",
      importingModel: "BPMNモデルをインポート中",
      fetchingModel: "取得中",
      convertingModel: "コンバート中",
      renderingModel: "レンダリング中"
  }
}

if(!ORYX.I18N.TransformationDownloadDialog) ORYX.I18N.TransformationDownloadDialog = {};

ORYX.I18N.TransformationDownloadDialog.error = "エラー";
ORYX.I18N.TransformationDownloadDialog.noResult = "変換サービスは結果を返しませんでした。";
ORYX.I18N.TransformationDownloadDialog.errorParsing = "ダイアグラムをパース中にエラー";
ORYX.I18N.TransformationDownloadDialog.transResult = "変換結果";
ORYX.I18N.TransformationDownloadDialog.showFile = "結果ファイルを表示";
ORYX.I18N.TransformationDownloadDialog.downloadFile = "結果ファイルをダウンロード";
ORYX.I18N.TransformationDownloadDialog.downloadAll = "全ての結果ファイルをダウンロード";

if(!ORYX.I18N.DesynchronizabilityOverlay) ORYX.I18N.DesynchronizabilityOverlay = {};
//TODO desynchronizability is not a correct term
ORYX.I18N.DesynchronizabilityOverlay.group = "オーバーレイ";
ORYX.I18N.DesynchronizabilityOverlay.name = "非同期チェッカー";
ORYX.I18N.DesynchronizabilityOverlay.desc = "非同期チェッカー";
ORYX.I18N.DesynchronizabilityOverlay.sync = "そのnetは非同期です。";
ORYX.I18N.DesynchronizabilityOverlay.error = "そのnetは構文エラーを一つ含みます。";
ORYX.I18N.DesynchronizabilityOverlay.invalid = "サーバーからの不正な応答";

if(!ORYX.I18N.Edit) ORYX.I18N.Edit = {};

ORYX.I18N.Edit.group = "Edit";
ORYX.I18N.Edit.cut = "カット";
ORYX.I18N.Edit.cutDesc = "Oryxクリップボードへカット";
ORYX.I18N.Edit.copy = "コピー";
ORYX.I18N.Edit.copyDesc = "Oryxクリップボードへコピー";
ORYX.I18N.Edit.paste = "ペースト";
ORYX.I18N.Edit.pasteDesc = "キャンバスへOryxクリップボードをペースト";
ORYX.I18N.Edit.del = "削除";
ORYX.I18N.Edit.delDesc = "選択した全てのシェイプを削除";

if(!ORYX.I18N.EPCSupport) ORYX.I18N.EPCSupport = {};

ORYX.I18N.EPCSupport.group = "EPC";
ORYX.I18N.EPCSupport.exp = "EPCをエクスポート";
ORYX.I18N.EPCSupport.expDesc = "ダイアグラムをEPMLへエクスポート";
ORYX.I18N.EPCSupport.imp = "EPCをインポート";
ORYX.I18N.EPCSupport.impDesc = "EPMLファイルをインポート";
ORYX.I18N.EPCSupport.progressExp = "モデルをエクスポート中";
ORYX.I18N.EPCSupport.selectFile = "インポートするEPML (.epml)ファイルを選択";
ORYX.I18N.EPCSupport.file = "ファイル";
ORYX.I18N.EPCSupport.impPanel = "EPMLファイルをインポート";
ORYX.I18N.EPCSupport.impBtn = "インポート";
ORYX.I18N.EPCSupport.close = "閉じる";
ORYX.I18N.EPCSupport.error = "エラー";
ORYX.I18N.EPCSupport.progressImp = "インポート...";

if(!ORYX.I18N.ERDFSupport) ORYX.I18N.ERDFSupport = {};

ORYX.I18N.ERDFSupport.exp = "ERDFへエクスポート";
ORYX.I18N.ERDFSupport.expDesc = "ERDFへエクスポート";
ORYX.I18N.ERDFSupport.imp = "ERDFからインポート";
ORYX.I18N.ERDFSupport.impDesc = "ERDFからインポート";
ORYX.I18N.ERDFSupport.impFailed = "ERDFのインポートに失敗しました";
ORYX.I18N.ERDFSupport.impFailed2 = "インポート中にエラーが発生しました！ <br/>エラーメッセージを確認してください: <br/><br/>";
ORYX.I18N.ERDFSupport.error = "エラー";
ORYX.I18N.ERDFSupport.noCanvas = "XMLファイルにはOryxキャンバスのノードが含まれていません！";
ORYX.I18N.ERDFSupport.noSS = "Oryxキャンバスにはステンシルセットの定義が含まれていません";
ORYX.I18N.ERDFSupport.wrongSS = "そのステンシルセットは現在のエディタにあいません";
ORYX.I18N.ERDFSupport.selectFile = "ERDF (.xml)ファイルを選択 もしくはインポートするERDFを入力してください";
ORYX.I18N.ERDFSupport.file = "ファイル";
ORYX.I18N.ERDFSupport.impERDF = "ERDFをインポート";
ORYX.I18N.ERDFSupport.impBtn = "インポート";
ORYX.I18N.ERDFSupport.impProgress = "インポート中...";
ORYX.I18N.ERDFSupport.close = "閉じる";
ORYX.I18N.ERDFSupport.deprTitle = "eRDFへ本当にエクスポートしますか?";
ORYX.I18N.ERDFSupport.deprText = "将来のバージョンのOryxエディターではサポートされないためeRDFへのエクスポートは推奨されません。可能であればJSONへモデルをエクスポートしてください。エクスポートを続行しますか？";

if(!ORYX.I18N.jPDLSupport) ORYX.I18N.jPDLSupport = {};

ORYX.I18N.jPDLSupport.group = "エクスポート";
ORYX.I18N.jPDLSupport.exp = "jPDLへエクスポート";
ORYX.I18N.jPDLSupport.expDesc = "jPDLへエクスポート";
ORYX.I18N.jPDLSupport.imp = "jPDLからインポート";
ORYX.I18N.jPDLSupport.impDesc = "jPDLファイルをBPMN2へ移行";
ORYX.I18N.jPDLSupport.impFailedReq = "jPDLの移行に失敗しました";
//ORYX.I18N.jPDLSupport.impFailedJson = "jPDLの変換に失敗しました";
ORYX.I18N.jPDLSupport.impFailedJsonAbort = "移行は中断しました";
ORYX.I18N.jPDLSupport.loadSseQuestionTitle = "jBPM 拡張ステンシルセットをロードする必要があります"; 
ORYX.I18N.jPDLSupport.loadSseQuestionBody = "jPDLへ移行するため拡張ステンシルセットをロードする必要があります。続行しますか？";
ORYX.I18N.jPDLSupport.expFailedReq = "モデルのエクスポートに失敗しました";
ORYX.I18N.jPDLSupport.expFailedXml = "jPDLファイルへエクスポート。 エクスポーターの報告: ";
ORYX.I18N.jPDLSupport.error = "エラー";
ORYX.I18N.jPDLSupport.selectFile = "1. jPDL processdefinition.xmlファイルを選択（もしくは入力）";
ORYX.I18N.jPDLSupport.selectGpdFile = "2. jPDL gpd.xmlファイルを選択(もしくは入力)";
ORYX.I18N.jPDLSupport.file = "定義ファイル";
ORYX.I18N.jPDLSupport.gpdfile = "GPDファイル"
ORYX.I18N.jPDLSupport.impJPDL = "BPMN2へ移行";
ORYX.I18N.jPDLSupport.impBtn = "移行";
ORYX.I18N.jPDLSupport.impProgress = "移行中...";
ORYX.I18N.jPDLSupport.close = "閉じる";

if(!ORYX.I18N.FromBPMN2Support) ORYX.I18N.FromBPMN2Support = {};
ORYX.I18N.FromBPMN2Support.selectFile = "BPMN2ファイルを選択 もしくはインポートするBPMN2を入力";
ORYX.I18N.FromBPMN2Support.file = "ファイル";
ORYX.I18N.FromBPMN2Support.impBPMN2 = "BPMN2インポート";
ORYX.I18N.FromBPMN2Support.impBtn = "インポート";
ORYX.I18N.FromBPMN2Support.impProgress = "インポート中...";
ORYX.I18N.FromBPMN2Support.close = "閉じる";

if(!ORYX.I18N.FromJSONSupport) ORYX.I18N.FromJSONSupport = {};
ORYX.I18N.FromJSONSupport.selectFile = "JSONファイルを選択、もしくは入力！";
ORYX.I18N.FromJSONSupport.file = "ファイル";
ORYX.I18N.FromJSONSupport.impBPMN2 = "JSONをインポート";
ORYX.I18N.FromJSONSupport.impBtn = "インポート";
ORYX.I18N.FromJSONSupport.impProgress = "インポート中...";
ORYX.I18N.FromJSONSupport.close = "閉じる";

if(!ORYX.I18N.Bpmn2Bpel) ORYX.I18N.Bpmn2Bpel = {};

ORYX.I18N.Bpmn2Bpel.group = "BPMN実行";
ORYX.I18N.Bpmn2Bpel.show = "変換されたBPELを表示";
ORYX.I18N.Bpmn2Bpel.download = "変換されたBPELをダウンロード";
ORYX.I18N.Bpmn2Bpel.deploy = "変換されたBPELをデプロイ";
ORYX.I18N.Bpmn2Bpel.showDesc = "BPMNをBPELへ変換し新しいウィンドウで表示";
ORYX.I18N.Bpmn2Bpel.downloadDesc = "BPMNをBPELへ変換し結果をダウンロードすることを提供";
ORYX.I18N.Bpmn2Bpel.deployDesc = "BPMNをBPELへ変換しBPEL-Engine Apache ODEのうえにビジネスプロセスをデプロイ";
ORYX.I18N.Bpmn2Bpel.transfFailed = "BPELへの変換が失敗しました";
ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputTitle = "Apache ODE URL";
ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputLabelDeploy = "デプロイプロセス";
ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputLabelCancel = "キャンセル";
ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputPanelText = "Apache ODE BPEL-EngineのURLを入力してください。 例: http://myserver:8080/ode";


if(!ORYX.I18N.Save) ORYX.I18N.Save = {};

ORYX.I18N.Save.group = "File"; // do not translate group names
ORYX.I18N.Save.save = "保存";
ORYX.I18N.Save.autosave = "自動保存";
ORYX.I18N.Save.saveDesc = "保存";
ORYX.I18N.Save.autosaveDesc = "自動保存";
ORYX.I18N.Save.autosaveDesc_on = "自動保存 (on)";
ORYX.I18N.Save.autosaveDesc_off = "自動保存 (off)";
ORYX.I18N.Save.saveAs = "名前を指定して保存...";
ORYX.I18N.Save.saveAsDesc = "名前を指定して保存...";
ORYX.I18N.Save.unsavedData = "保存していないデータがあります。終了する前に保存してください。さもないと変更が失われます";
ORYX.I18N.Save.newProcess = "新しいプロセス";
ORYX.I18N.Save.saveAsTitle = "名前を指定して保存...";
ORYX.I18N.Save.saveBtn = "保存";
ORYX.I18N.Save.close = "クローズ";
ORYX.I18N.Save.savedAs = "名前をして保存";
ORYX.I18N.Save.saved = "保存！";
ORYX.I18N.Save.failed = "保存が失敗しました";
ORYX.I18N.Save.noRights = "変更を保存する権限がありません";
ORYX.I18N.Save.saving = "保存中";
ORYX.I18N.Save.saveAsHint = "プロセス ダイアグラムは以下に格納されます:";

if(!ORYX.I18N.File) ORYX.I18N.File = {};

ORYX.I18N.File.group = "ファイル";
ORYX.I18N.File.print = "印刷";
ORYX.I18N.File.printDesc = "現在のモデルを印刷";
ORYX.I18N.File.pdf = "PDFとしてエクスポート";
ORYX.I18N.File.pdfDesc = "PDFとしてエクスポート";
ORYX.I18N.File.info = "情報";
ORYX.I18N.File.infoDesc = "情報";
ORYX.I18N.File.genPDF = "PDFを生成中";
ORYX.I18N.File.genPDFFailed = "PDFの生成に失敗しました";
ORYX.I18N.File.printTitle = "印刷";
ORYX.I18N.File.printMsg = "現在印刷機能に問題があります。ダイアグラムを印刷するのにPDFエクスポートを使用することを推奨します。印刷を続行しますか？";

if(!ORYX.I18N.Grouping) ORYX.I18N.Grouping = {};

ORYX.I18N.Grouping.grouping = "Grouping";
ORYX.I18N.Grouping.group = "グループ";
ORYX.I18N.Grouping.groupDesc = "選択した全てのシェイプをグループ化";
ORYX.I18N.Grouping.ungroup = "グループ化を解除";
ORYX.I18N.Grouping.ungroupDesc = "選択した全てのシェイプのグループ化を解除";

if(!ORYX.I18N.IBPMN2BPMN) ORYX.I18N.IBPMN2BPMN = {};

ORYX.I18N.IBPMN2BPMN.group ="エクスポート";
ORYX.I18N.IBPMN2BPMN.name ="IBPMNをBPMNにマッピング";
ORYX.I18N.IBPMN2BPMN.desc ="IBPMNをBPMNに変換";

if(!ORYX.I18N.Loading) ORYX.I18N.Loading = {};

ORYX.I18N.Loading.waiting ="お待ちください...";

if(!ORYX.I18N.Pnmlexport) ORYX.I18N.Pnmlexport = {};

ORYX.I18N.Pnmlexport.group ="エクスポート";
ORYX.I18N.Pnmlexport.name ="BPMN to PNML";
ORYX.I18N.Pnmlexport.desc ="実行可能なPNMLとしてエクスポート、デプロイ";

if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};

ORYX.I18N.PropertyWindow.name = "名前";
ORYX.I18N.PropertyWindow.value = "値";
ORYX.I18N.PropertyWindow.selected = "選択済み";
ORYX.I18N.PropertyWindow.clickIcon = "アイコンをクリック";
ORYX.I18N.PropertyWindow.add = "追加";
ORYX.I18N.PropertyWindow.rem = "削除";
ORYX.I18N.PropertyWindow.complex = "コンプレックスタイプのエディター";
ORYX.I18N.PropertyWindow.text = "テキストタイプのエディター";
ORYX.I18N.PropertyWindow.ok = "OK";
ORYX.I18N.PropertyWindow.cancel = "キャンセル";
ORYX.I18N.PropertyWindow.dateFormat = "m/d/y";

if (!ORYX.I18N.ConditionExpressionEditorField) ORYX.I18N.ConditionExpressionEditorField = {};

ORYX.I18N.ConditionExpressionEditorField.simpleTitle = "エクスプレッションエディター - 自動補完を有効にするには [Ctrl-Z] を押してください";
ORYX.I18N.ConditionExpressionEditorField.sequenceFlowTitle = "シーケンスフロー条件";
ORYX.I18N.ConditionExpressionEditorField.sequenceFlowFullTitle = "シーケンスフロー条件 - 自動補完を有効にするには [Ctrl-Z] を押してください";
ORYX.I18N.ConditionExpressionEditorField.scriptTab = "スクリプト";
ORYX.I18N.ConditionExpressionEditorField.editorTab = "エディター";
ORYX.I18N.ConditionExpressionEditorField.editorDescription = "以下の条件が合えばシーケンスフローを実行"
ORYX.I18N.ConditionExpressionEditorField.processVariable = "プロセス変数:"
ORYX.I18N.ConditionExpressionEditorField.condition = "条件:"
ORYX.I18N.ConditionExpressionEditorField.between = "は次の値の間にある";
ORYX.I18N.ConditionExpressionEditorField.contains = "は次の値を含む";
ORYX.I18N.ConditionExpressionEditorField.endsWith = "は次の値で終了する";
ORYX.I18N.ConditionExpressionEditorField.equalsTo = "は次の値に等しい";
ORYX.I18N.ConditionExpressionEditorField.greaterThan = "は次の値より大きい";
ORYX.I18N.ConditionExpressionEditorField.greaterThanOrEqual = "は次の値以上";
ORYX.I18N.ConditionExpressionEditorField.isEmpty = "は空";
ORYX.I18N.ConditionExpressionEditorField.isFalse = "は false";
ORYX.I18N.ConditionExpressionEditorField.isNull = "は null";
ORYX.I18N.ConditionExpressionEditorField.isTrue = "は true";
ORYX.I18N.ConditionExpressionEditorField.lessThan = "は次の値より小さい";
ORYX.I18N.ConditionExpressionEditorField.lessThanOrEqual = "は次の値以下";
ORYX.I18N.ConditionExpressionEditorField.startsWith = "は次の値で開始する";
ORYX.I18N.ConditionExpressionEditorField.paramsError = "スクリプト式を生成できませんでした。フォームパラメーターを正しく入力してください";
ORYX.I18N.ConditionExpressionEditorField.saveError = "プロパティの値を保存できませんでした。値を確認し、もう一度行ってください";
ORYX.I18N.ConditionExpressionEditorField.scriptParseError = "スクリプトのパース中にエラーが見つかりました: <br/>{0}<br/><br/>OK を押せばエクスプレッションエディターに戻りますが、現在のスクリプトは失われます。Cancel を押せばスクリプトエディターに戻ります";
ORYX.I18N.ConditionExpressionEditorField.scriptGenerationError = "スクリプトの生成中にエラーが見つかりました: <br/>{0}<br/><br/>エクスプレッションエディターに入力したデータを確認してください";
ORYX.I18N.ConditionExpressionEditorField.nonExistingVariable = "このプロセスは \"{0}\" という変数を持っていません";

if(!ORYX.I18N.ShapeMenuPlugin) ORYX.I18N.ShapeMenuPlugin = {};

ORYX.I18N.ShapeMenuPlugin.drag = "ドラッグ";
ORYX.I18N.ShapeMenuPlugin.clickDrag = "クリックもしくはドラッグ";
ORYX.I18N.ShapeMenuPlugin.morphMsg = " シェイプを変形";

if(!ORYX.I18N.SimplePnmlexport) ORYX.I18N.SimplePnmlexport = {};

ORYX.I18N.SimplePnmlexport.group = "エクスポート";
ORYX.I18N.SimplePnmlexport.name = "PNMLへエクスポート";
ORYX.I18N.SimplePnmlexport.desc = "PNMLへエクスポート";

if(!ORYX.I18N.StepThroughPlugin) ORYX.I18N.StepThroughPlugin = {};

ORYX.I18N.StepThroughPlugin.group = "一つずつ実行";
ORYX.I18N.StepThroughPlugin.stepThrough = "一つずつ実行";
ORYX.I18N.StepThroughPlugin.stepThroughDesc = "モデルを一つずつ実行";
ORYX.I18N.StepThroughPlugin.undo = "元に戻す";
ORYX.I18N.StepThroughPlugin.undoDesc = "一つ元に戻す";
ORYX.I18N.StepThroughPlugin.error = "このダイアグラムを一つずつ実行できません";
ORYX.I18N.StepThroughPlugin.executing = "実行中";

if(!ORYX.I18N.SyntaxChecker) ORYX.I18N.SyntaxChecker = {};

ORYX.I18N.SyntaxChecker.group = "検証";
ORYX.I18N.SyntaxChecker.name = "プロセスの検証";
ORYX.I18N.SyntaxChecker.desc = "プロセスの検証";
ORYX.I18N.SyntaxChecker.noErrors = "検証エラーはありません";
ORYX.I18N.SyntaxChecker.hasErrors = "検証エラーが見つかりました";
ORYX.I18N.SyntaxChecker.invalid = "サーバーからの不正な応答";
ORYX.I18N.SyntaxChecker.checkingMessage = "検証中 ...";

if(!ORYX.I18N.Undo) ORYX.I18N.Undo = {};

ORYX.I18N.Undo.group = "Undo";
ORYX.I18N.Undo.undo = "元に戻す";
ORYX.I18N.Undo.undoDesc = "最後のアクションを元に戻す";
ORYX.I18N.Undo.redo = "やり直す";
ORYX.I18N.Undo.redoDesc = "最後のアクションをやり直す";

if(!ORYX.I18N.Validator) ORYX.I18N.Validator = {};
ORYX.I18N.Validator.checking = "チェック中";

if(!ORYX.I18N.View) ORYX.I18N.View = {};

ORYX.I18N.View.group = "ズーム";
ORYX.I18N.View.zoomIn = "ズームイン";
ORYX.I18N.View.zoomInDesc = "拡大";
ORYX.I18N.View.zoomOut = "ズームアウト";
ORYX.I18N.View.zoomOutDesc = "縮小";
ORYX.I18N.View.zoomStandard = "標準ズーム";
ORYX.I18N.View.zoomStandardDesc = "オリジナルサイズ";
ORYX.I18N.View.zoomFitToModel = "モデルにあわせてズーム";
ORYX.I18N.View.zoomFitToModelDesc = "画面にあわせてリサイズ";
ORYX.I18N.View.showInPopout = "ポップアウト";
ORYX.I18N.View.showInPopoutDesc = "ポップアップウィンドウに表示";
ORYX.I18N.View.convertToPDF = "PDF";
ORYX.I18N.View.convertToPDFDesc = "PDFにコンバート";
ORYX.I18N.View.convertToPNG = "PNG";
ORYX.I18N.View.convertToPNGDesc = "PNGコンバート";
ORYX.I18N.View.generateTaskForms = "タスクフォームのテンプレートを生成";
ORYX.I18N.View.editProcessForm = "プロセスフォームを編集";
ORYX.I18N.View.editTaskForm = "タスクフォームを編集";
ORYX.I18N.View.generateTaskFormsDesc = "タスクフォームのテンプレートを生成";
ORYX.I18N.View.editProcessFormDesc = "プロセスフォームを編集";
ORYX.I18N.View.editTaskFormDesc = "タスクフォームを編集";
ORYX.I18N.View.showInfo = "情報";
ORYX.I18N.View.showInfoDesc = "情報";
ORYX.I18N.View.jbpmgroup = "jBPM";
ORYX.I18N.View.migratejPDL = "jPDL 3.2 を BPMN2 へ移行";
ORYX.I18N.View.migratejPDLDesc = "jPDL 3.2 を BPMN2 へ移行";
ORYX.I18N.View.viewDiff = "差異を表示";
ORYX.I18N.View.viewDiffDesc = "プロセスのバージョン間の差異を表示";
ORYX.I18N.View.viewDiffLoadingVersions = "プロセスのバージョンをロード中...";
ORYX.I18N.View.connectServiceRepo = "jBPM サービスレポジトリへ接続";
ORYX.I18N.View.connectServiceRepoDesc = "jBPM サービスレポジトリへ接続";
ORYX.I18N.View.connectServiceRepoDataTitle = "jBPM サービスレポジトリデータ";
ORYX.I18N.View.connectServiceRepoConnecting = "jBPM サービスレポジトリへ接続中...";
ORYX.I18N.View.installingRepoItem = "レポジトリからアイテムをインストール中...";
ORYX.I18N.View.shareProcess = "プロセスを共有";
ORYX.I18N.View.shareProcessDesc = "プロセスを共有";
ORYX.I18N.View.infogroup = "情報";

if(!ORYX.I18N.View.tabs) ORYX.I18N.View.tabs = {};
ORYX.I18N.View.tabs.modelling = "プロセスモデリング";
ORYX.I18N.View.tabs.simResults = "シミュレーション結果";

if(!ORYX.I18N.View.sim) ORYX.I18N.View.sim = {};
ORYX.I18N.View.sim.processPaths = "プロセスパス表示";
ORYX.I18N.View.sim.runSim = "プロセスシミュレーション実行";
ORYX.I18N.View.sim.calculatingPaths = "プロセスパス計算中";
ORYX.I18N.View.sim.dispColor = "表示色";
ORYX.I18N.View.sim.numElements = "要素数";
ORYX.I18N.View.sim.processPathsTitle = "プロセスパス";
ORYX.I18N.View.sim.subProcessPathsTitle = "サブプロセスパス";
ORYX.I18N.View.sim.select = "以下から ";
ORYX.I18N.View.sim.display = " を選択し、「パス表示」をクリック";
ORYX.I18N.View.sim.showPath = "パス表示";
ORYX.I18N.View.sim.selectPath = "プロセスパスを選択してください";
ORYX.I18N.View.sim.numInstances = "インスタンス数";
ORYX.I18N.View.sim.interval = "間隔";
ORYX.I18N.View.sim.intervalUnits = "間隔の単位";
ORYX.I18N.View.sim.runSim = "プロセスシミュレーション実行";
ORYX.I18N.View.sim.runningSim = "プロセスシミュレーション実行中...";
ORYX.I18N.View.sim.simNoResults = "シミュレーションエンジンが結果を返しませんでした: ";
ORYX.I18N.View.sim.unableToPerform = "シミュレーションが実行できません:";
ORYX.I18N.View.sim.resultsInfo = "シミュレーション情報";
ORYX.I18N.View.sim.resultsGraphs = "シミュレーショングラフ";
ORYX.I18N.View.sim.resultsProcessId = "プロセスID: ";
ORYX.I18N.View.sim.resultsProcessName = "プロセス名: ";
ORYX.I18N.View.sim.resultsProcessVersion = "プロセスバージョン: ";
ORYX.I18N.View.sim.resultsSimStartTime = "シミュレーション開始: ";
ORYX.I18N.View.sim.resultsSimEndTime = "シミュレーション終了: ";
ORYX.I18N.View.sim.resultsNumOfExecutions = "実行数: ";
ORYX.I18N.View.sim.resultsInterval = "間隔 "
ORYX.I18N.View.sim.resultsGroupProcess = "プロセス";
ORYX.I18N.View.sim.resultsGroupProcessElements = "プロセス要素";
ORYX.I18N.View.sim.resultsGroupProcessPaths = "パス";
ORYX.I18N.View.sim.resultsTitlesProcessSimResults = "プロセスシミュレーション結果";
ORYX.I18N.View.sim.resultsTitlesTaskSimResults = "タスクシミュレーション結果";
ORYX.I18N.View.sim.resultsTitlesHumanTaskSimResults = "ヒューマンタスクシミュレーション結果";
ORYX.I18N.View.sim.resultsTitlesPathExecutionInfo = "パス実行情報";
ORYX.I18N.View.sim.chartsExecutionTimes = "実行時間";
ORYX.I18N.View.sim.chartsActivityInstances = "アクティビティインスタンス";
ORYX.I18N.View.sim.chartsTotalCost = "トータルコスト";
ORYX.I18N.View.sim.chartsResourceUtilization = "リソース利用";
ORYX.I18N.View.sim.chartsResourceCost = "リソースコスト";
ORYX.I18N.View.sim.chartsPathImage = "パスイメージ";
ORYX.I18N.View.sim.chartsPathInstanceExecution = "パスインスタンス実行";



if(!ORYX.I18N.XFormsSerialization) ORYX.I18N.XFormsSerialization = {};

ORYX.I18N.XFormsSerialization.group = "XForms シリアライゼーション";
ORYX.I18N.XFormsSerialization.exportXForms = "XFormsをエクスポート";
ORYX.I18N.XFormsSerialization.exportXFormsDesc = "XForms+XHTML markupをエクスポート";
ORYX.I18N.XFormsSerialization.importXForms = "XFormsをインポート";
ORYX.I18N.XFormsSerialization.importXFormsDesc = "XForms+XHTML markupをインポート";
ORYX.I18N.XFormsSerialization.noClientXFormsSupport = "XForms のサポートはありません";
ORYX.I18N.XFormsSerialization.noClientXFormsSupportDesc = "<h2>ブラウザがXFormsサポートしていません。 <a href=\"https://addons.mozilla.org/firefox/addon/824\" target=\"_blank\">Mozilla XForms Add-on</a> for Firefox をインストールしてください。</h2>";
ORYX.I18N.XFormsSerialization.ok = "OK";
ORYX.I18N.XFormsSerialization.selectFile = "XHTML (.xhtml)ファイルを選択 もしくは インポートするXForms+XHTML markup を入力";
ORYX.I18N.XFormsSerialization.selectCss = "css ファイルのURLを入力してください";
ORYX.I18N.XFormsSerialization.file = "ファイル";
ORYX.I18N.XFormsSerialization.impFailed = "ドキュメントのインポートに失敗しました";
ORYX.I18N.XFormsSerialization.impTitle = "XForms+XHTML ドキュメントをインポート";
ORYX.I18N.XFormsSerialization.expTitle = "XForms+XHTML ドキュメントをエクスポート";
ORYX.I18N.XFormsSerialization.impButton = "インポート";
ORYX.I18N.XFormsSerialization.impProgress = "インポート中...";
ORYX.I18N.XFormsSerialization.close = "閉じる";


if(!ORYX.I18N.TreeGraphSupport) ORYX.I18N.TreeGraphSupport = {};

ORYX.I18N.TreeGraphSupport.syntaxCheckName = "構文チェック";
ORYX.I18N.TreeGraphSupport.group = "ツリーグラフのサポート";
ORYX.I18N.TreeGraphSupport.syntaxCheckDesc = "ツリーグラフ構造の構文をチェック";

if(!ORYX.I18N.QueryEvaluator) ORYX.I18N.QueryEvaluator = {};

ORYX.I18N.QueryEvaluator.name = "クエリー評価";
ORYX.I18N.QueryEvaluator.group = "検証";
ORYX.I18N.QueryEvaluator.desc = "クエリー評価";
ORYX.I18N.QueryEvaluator.noResult = "クエリーはマッチしませんでした";
ORYX.I18N.QueryEvaluator.invalidResponse = "サーバーからの不正な応答";

// if(!ORYX.I18N.QueryResultHighlighter) ORYX.I18N.QueryResultHighlighter = {};
// 
// ORYX.I18N.QueryResultHighlighter.name = "Query Result Highlighter";

/** New Language Properties: 08.12.2008 */

ORYX.I18N.PropertyWindow.title = "プロパティー";

if(!ORYX.I18N.ShapeRepository) ORYX.I18N.ShapeRepository = {};
ORYX.I18N.ShapeRepository.title = "シェイプ リポジトリ";

ORYX.I18N.Save.dialogDesciption = "名前、説明とコメントを入力してください";
ORYX.I18N.Save.dialogLabelTitle = "タイトル";
ORYX.I18N.Save.dialogLabelDesc = "説明";
ORYX.I18N.Save.dialogLabelType = "タイプ";
ORYX.I18N.Save.dialogLabelComment = "改訂コメント";

ORYX.I18N.Validator.name = "BPMN 検証";
ORYX.I18N.Validator.description = "BPMNの検証";

ORYX.I18N.SSExtensionLoader.labelImport = "インポート";
ORYX.I18N.SSExtensionLoader.labelCancel = "キャンセル";

Ext.MessageBox.buttonText.yes = "Yes";
Ext.MessageBox.buttonText.no = "No";
Ext.MessageBox.buttonText.cancel = "キャンセル";
Ext.MessageBox.buttonText.ok = "OK";


/** New Language Properties: 28.01.2009 */
if(!ORYX.I18N.BPMN2XPDL) ORYX.I18N.BPMN2XPDL = {};
ORYX.I18N.BPMN2XPDL.group = "エクスポート";
ORYX.I18N.BPMN2XPDL.xpdlExport = "XPDLへエクスポート";
ORYX.I18N.BPMN2XPDL.xpdlImport = "XPDLからインポート";
ORYX.I18N.BPMN2XPDL.importGroup = "インポート";
ORYX.I18N.BPMN2XPDL.selectFile = "XPDL (.xml)ファイルを選択もしくはインポートするXPDLを入力";
ORYX.I18N.BPMN2XPDL.file = "ファイル";
ORYX.I18N.BPMN2XPDL.impXPDL = "XPDLをインポート";
ORYX.I18N.BPMN2XPDL.impBtn = "インポート";
ORYX.I18N.BPMN2XPDL.impProgress = "インポート中...";
ORYX.I18N.BPMN2XPDL.close = "閉じる";

/** Resource Perspective Additions: 24 March 2009 */
if(!ORYX.I18N.ResourcesSoDAdd) ORYX.I18N.ResourcesSoDAdd = {};

ORYX.I18N.ResourcesSoDAdd.name = "職務の制約の分離を定義";
ORYX.I18N.ResourcesSoDAdd.group = "リソースパースペクティブ";
ORYX.I18N.ResourcesSoDAdd.desc = "選択したタスクの職務の制約の分離を定義";

if(!ORYX.I18N.ResourcesSoDShow) ORYX.I18N.ResourcesSoDShow = {};

ORYX.I18N.ResourcesSoDShow.name = "職務の制約の分離を表示";
ORYX.I18N.ResourcesSoDShow.group = "リソースパースペクティブ";
ORYX.I18N.ResourcesSoDShow.desc = "選択したタスクの職務の制約の分離を表示";

if(!ORYX.I18N.ResourcesBoDAdd) ORYX.I18N.ResourcesBoDAdd = {};

ORYX.I18N.ResourcesBoDAdd.name = "職務の制約の結びつけを定義";
ORYX.I18N.ResourcesBoDAdd.group = "リソースパースペクティブ";
ORYX.I18N.ResourcesBoDAdd.desc = "選択したタスクの職務の制約の結びつけを定義";

if(!ORYX.I18N.ResourcesBoDShow) ORYX.I18N.ResourcesBoDShow = {};

ORYX.I18N.ResourcesBoDShow.name = "職務の制約の結びつけを表示";
ORYX.I18N.ResourcesBoDShow.group = "リソースパースペクティブ";
ORYX.I18N.ResourcesBoDShow.desc = "選択したタスクの職務の制約の結びつけを表示";

if(!ORYX.I18N.ResourceAssignment) ORYX.I18N.ResourceAssignment = {};

ORYX.I18N.ResourceAssignment.name = "リソースの割り当て";
ORYX.I18N.ResourceAssignment.group = "リソース パースペクティブ";
ORYX.I18N.ResourceAssignment.desc = "選択したタスクにリソースを割り当て";

if(!ORYX.I18N.ClearSodBodHighlights) ORYX.I18N.ClearSodBodHighlights = {};

ORYX.I18N.ClearSodBodHighlights.name = "ハイライトとオーバーレイをクリア";
ORYX.I18N.ClearSodBodHighlights.group = "リソース パースペクティブ";
ORYX.I18N.ClearSodBodHighlights.desc = "ハイライト/オーバーレイされた全ての職務の分離と結びつけを削除";


if(!ORYX.I18N.Perspective) ORYX.I18N.Perspective = {};
ORYX.I18N.Perspective.no = "パースペクティブがありません"
ORYX.I18N.Perspective.noTip = "現在のパースペクティブをアンロード"


/** New Language Properties: 21.04.2009 */
ORYX.I18N.JSONSupport = {
    imp: {
        name: "JSONからインポート",
        desc: "JSONからモデルをインポート",
        group: "エクスポート",
        selectFile: "JSON (.json)ファイルを選択もしくはインポートするJSONを入力",
        file: "ファイル",
        btnImp: "インポート",
        btnClose: "閉じる",
        progress: "インポート中 ...",
        syntaxError: "構文エラー"
    },
    exp: {
        name: "JSONへエクスポート",
        desc: "JSONへ現在のモデルをエクスポート",
        group: "エクスポート"
    }
};

ORYX.I18N.TBPMSupport = {
		imp: {
        name: "PNG/JPEGからインポート",
        desc: "TPBMフォトからモデルをインポート",
        group: "エクスポート",
        selectFile: "イメージファイル(.png/.jpeg)を選択",
        file: "ファイル",
        btnImp: "インポート",
        btnClose: "閉じる",
        progress: "インポート中 ...",
        syntaxError: "構文エラー",
        impFailed: "ドキュメントのインポートに失敗しました",
        confirm: "ハイライトされたシェイプのインポートを確定"
    }
};

/** New Language Properties: 08.05.2009 */
if(!ORYX.I18N.BPMN2XHTML) ORYX.I18N.BPMN2XHTML = {};
ORYX.I18N.BPMN2XHTML.group = "エクスポート";
ORYX.I18N.BPMN2XHTML.XHTMLExport = "XHTML ドキュメンテーションをエクスポート";

/** New Language Properties: 09.05.2009 */
if(!ORYX.I18N.JSONImport) ORYX.I18N.JSONImport = {};

ORYX.I18N.JSONImport.title = "JSONのインポート";
ORYX.I18N.JSONImport.wrongSS = "インポートされたファイルのステンシルセット({0})はロードされたステンシルセット({1})とマッチしません";
ORYX.I18N.JSONImport.invalidJSON = "インポートするJSONは不正です";

if(!ORYX.I18N.Feedback) ORYX.I18N.Feedback = {};

ORYX.I18N.Feedback.name = "フィードバック";
ORYX.I18N.Feedback.desc = "いかなる種類のフィードバックでもコンタクトしてください!";
ORYX.I18N.Feedback.pTitle = "いかなる種類のフィードバックでもコンタクトしてください!";
ORYX.I18N.Feedback.pName = "名前";
ORYX.I18N.Feedback.pEmail = "E-Mail";
ORYX.I18N.Feedback.pSubject = "主題";
ORYX.I18N.Feedback.pMsg = "説明/メッセージ";
ORYX.I18N.Feedback.pEmpty = "* 要求を理解できるように可能な限り詳細な情報を提供してください\n* バグレポートについては、問題を再現できるステップと予想される結果を記載してください";
ORYX.I18N.Feedback.pAttach = "現在のモデルを添付";
ORYX.I18N.Feedback.pAttachDesc = "この情報はプロセスをデバッグするのに役立ちます。もしモデルに公にすべきでないデータが含まれていれば、削除するかこの動作のチェックををはずしてください";
ORYX.I18N.Feedback.pBrowser = "ブラウザと環境に関する情報";
ORYX.I18N.Feedback.pBrowserDesc = "この情報はブラウザから自動的に検知されました。この情報はブラウザ特有の動作に関連したバグが発見された場合に役に立ちます";
ORYX.I18N.Feedback.submit = "メッセージを送る";
ORYX.I18N.Feedback.sending = "メッセージを送信中 ...";
ORYX.I18N.Feedback.success = "成功";
ORYX.I18N.Feedback.successMsg = "フィードバックありがとうございます!";
ORYX.I18N.Feedback.failure = "失敗";
ORYX.I18N.Feedback.failureMsg = "残念ながらメッセージは送信されませんでした。こちらの失敗です! 再度試みるか、ここにコンタクトしてください http://code.google.com/p/oryx-editor/";


ORYX.I18N.Feedback.name = "フィードバック";
ORYX.I18N.Feedback.failure = "失敗";
ORYX.I18N.Feedback.failureMsg = "残念ながらメッセージは送信されませんでした。こちらの失敗です! 再度試みるか、ここにコンタクトしてください http://code.google.com/p/oryx-editor/";
ORYX.I18N.Feedback.submit = "メッセージを送信";

ORYX.I18N.Feedback.emailDesc = "e-mail アドレス?";
ORYX.I18N.Feedback.titleDesc = "メッセージを短いタイトルで要約してください";
ORYX.I18N.Feedback.descriptionDesc = "アイデア、質問、もしくは問題を記載してください"
ORYX.I18N.Feedback.info = '<p>Oryx is a research platform ビジネスプロセス管理の分野の研究者のサポートを目的としたリサーチプラットフォームです。研究論文の検証や実験の構築のために、柔軟で拡張可能なツールを提供します。</p><p>　私達は、私達のプラットフォームの<a href="http://bpt.hpi.uni-potsdam.de/Oryx/ReleaseNotes" target="_blank">最新の技術や成果</a>を提供できることを光栄に思います。 <a href="http://bpt.hpi.uni-potsdam.de/Oryx/DeveloperNetwork" target="_blank">私達は</a> 時によっては些細な障害があるかもしれませんが、信頼性の高いシステムを提供するために最前を尽くします。</p><p>Oryxを改善するためのアイデア、プラットフォームに関する質問、または問題を報告したい場合は: <strong>こちらまでご連絡ください</strong></p>'; // general info will be shown, if no subject specific info is given
// list subjects in reverse order of appearance!

ORYX.I18N.Feedback.subjects = [
    {
    	id: "question",   // ansi-compatible name
    	name: "質問", // natural name
    	description: "ここで質問してください! \n回答まえに更なる質問でご迷惑をおかけしないために可能なかぎり多くの情報を提供してください", // default text for the description text input field
    	info: "" // optional field to give more info
    },
    {
    	id: "problem",   // ansi-compatible name
    	name: "問題", // natural name
    	description: "ご不便をおかけします。問題に関するフィードバックを提供してください。改善に向けて努力します。可能なかぎり詳細に記載してください。", // default text for the description text input field
    	info: "" // optional field to give more info
    },
    {
    	id: "idea",   // ansi-compatible name
    	name: "アイデア", // natural name
    	description: "アイデアや考えを共有してください!", // default text for the description text input field
    	info: "" // optional field to give more info
    }
];

/** New Language Properties: 11.05.2009 */
if(!ORYX.I18N.BPMN2DTRPXMI) ORYX.I18N.BPMN2DTRPXMI = {};
ORYX.I18N.BPMN2DTRPXMI.group = "エクスポート";
ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExport = "XMIへエクスポート (デザイン思考)";
ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExportDescription = "現在のモデルをXMIへエクスポート (拡張ステンシルセット'デザイン思考のためのBPMNサブセット'が必要です)";

/** New Language Properties: 14.05.2009 */
if(!ORYX.I18N.RDFExport) ORYX.I18N.RDFExport = {};
ORYX.I18N.RDFExport.group = "エクスポート";
ORYX.I18N.RDFExport.rdfExport = "RDFへエクスポート";
ORYX.I18N.RDFExport.rdfExportDescription = "現在のモデルをResource Description Framework (RDF)用に定義されたXMLシリアライゼーションへエクスポート";

/** New Language Properties: 15.05.2009*/
if(!ORYX.I18N.SyntaxChecker.BPMN) ORYX.I18N.SyntaxChecker.BPMN={};
ORYX.I18N.SyntaxChecker.BPMN_NO_SOURCE = "接線にはソースが必要です";
ORYX.I18N.SyntaxChecker.BPMN_NO_TARGET = "接線にはターゲットが必要です";
ORYX.I18N.SyntaxChecker.BPMN_DIFFERENT_PROCESS = "ソースとターゲットのノードは同じプロセスに含まれる必要があります";
ORYX.I18N.SyntaxChecker.BPMN_SAME_PROCESS = "ソースとターゲットは異なるプールに含まれる必要があります";
ORYX.I18N.SyntaxChecker.BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "フローオブジェクトはプロセスに含まれる必要があります";
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "エンドイベントは内向きのシーケンスフローを持つ必要があります";
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "スタートイベントは外向きのシーケンスフローを持つ必要があります";
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "スタートイベントは外向きのシーケンスフローを持てません";
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "付属の仲介イベントは内向きのシーケンスフローを持てません";
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "付属の仲介イベントは必ず一つの外向きのシーケンスフローを持つ必要があります";
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "エンドイベントは外向きのシーケンスフローを持てません";
ORYX.I18N.SyntaxChecker.BPMN_EVENTBASEDGATEWAY_BADCONTINUATION = "イベントベースのゲートウェイはその後にゲートウェイやサブプロセスが続いてはいけません";
ORYX.I18N.SyntaxChecker.BPMN_NODE_NOT_ALLOWED = "ノードタイプは許可されません";

if(!ORYX.I18N.SyntaxChecker.IBPMN) ORYX.I18N.SyntaxChecker.IBPMN={};
ORYX.I18N.SyntaxChecker.IBPMN_NO_ROLE_SET = "相互関係は送信者と受信者のロールを持つ必要があります";
ORYX.I18N.SyntaxChecker.IBPMN_NO_INCOMING_SEQFLOW = "このノードは内向きのシーケンスフローを持つ必要があります";
ORYX.I18N.SyntaxChecker.IBPMN_NO_OUTGOING_SEQFLOW = "このノードは外向きのシーケンスフローを持つ必要があります";

if(!ORYX.I18N.SyntaxChecker.InteractionNet) ORYX.I18N.SyntaxChecker.InteractionNet={};
ORYX.I18N.SyntaxChecker.InteractionNet_SENDER_NOT_SET = "送信者がセットされていません";
ORYX.I18N.SyntaxChecker.InteractionNet_RECEIVER_NOT_SET = "受信者がセットされていません";
ORYX.I18N.SyntaxChecker.InteractionNet_MESSAGETYPE_NOT_SET = "メッセージタイプがセットされていません";
ORYX.I18N.SyntaxChecker.InteractionNet_ROLE_NOT_SET = "ロールがセットされていません";

if(!ORYX.I18N.SyntaxChecker.EPC) ORYX.I18N.SyntaxChecker.EPC={};
ORYX.I18N.SyntaxChecker.EPC_NO_SOURCE = "それぞれの接線はソースを持つ必要があります";
ORYX.I18N.SyntaxChecker.EPC_NO_TARGET = "それぞれの接線はターゲットを持つ必要があります";
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED = "ノードは接線と接続している必要があります";
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED_2 = "ノードはそれ以上の接線と接続している必要があります";
ORYX.I18N.SyntaxChecker.EPC_TOO_MANY_EDGES = "ノードに接続する接線が多すぎます";
ORYX.I18N.SyntaxChecker.EPC_NO_CORRECT_CONNECTOR = "ノードに正しいコネクターがありません";
ORYX.I18N.SyntaxChecker.EPC_MANY_STARTS = "一つのスタートイベントが必要です";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_OR = "分岐する OR/XOR の後にはファンクションは不要です";
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_OR = "分岐する OR/XOR の後にはプロセスインターフェースは不要です";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_FUNCTION =  "ファンクションの後にはファンクションは不要です";
ORYX.I18N.SyntaxChecker.EPC_EVENT_AFTER_EVENT =  "イベントの後にはイベントは不要です";
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_FUNCTION =  "ファンクションの後にはプロセスインターフェースは不要です";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_PI =  "プロセスインターフェースの後にはファンクションは不要です";

if(!ORYX.I18N.SyntaxChecker.PetriNet) ORYX.I18N.SyntaxChecker.PetriNet={};
ORYX.I18N.SyntaxChecker.PetriNet_NOT_BIPARTITE = "二部グラフではありません";
ORYX.I18N.SyntaxChecker.PetriNet_NO_LABEL = "ラベル付き移行用のラベルがセットされていません";
ORYX.I18N.SyntaxChecker.PetriNet_NO_ID = "IDがないノードがあります";
ORYX.I18N.SyntaxChecker.PetriNet_SAME_SOURCE_AND_TARGET = "二つのフローの関係性は同じソースとターゲットを持ちます";
ORYX.I18N.SyntaxChecker.PetriNet_NODE_NOT_SET = "ノードがフローの関係性用にセットされていません";

/** New Language Properties: 02.06.2009*/
ORYX.I18N.Edge = "接線";
ORYX.I18N.Node = "ノード";

/** New Language Properties: 03.06.2009*/
ORYX.I18N.SyntaxChecker.notice = "マウスを赤い十字の上に動かしてエラーメッセージを表示してください";

ORYX.I18N.Validator.result = "検証結果";
ORYX.I18N.Validator.noErrors = "検証エラーは見つかりませんでした";
ORYX.I18N.Validator.bpmnDeadlockTitle = "デッドロック";
ORYX.I18N.Validator.bpmnDeadlock = "このノードはデッドロックになりました。全ての内向きの分岐がアクティブになっていない状態です";
ORYX.I18N.Validator.bpmnUnsafeTitle = "同期の欠如";
ORYX.I18N.Validator.bpmnUnsafe = "モデルは同期の欠如により悪影響を受けます。マークされた要素は複数の内向きの分岐からアクティベートされます";
ORYX.I18N.Validator.bpmnLeadsToNoEndTitle = "検証結果";
ORYX.I18N.Validator.bpmnLeadsToNoEnd = "プロセスは最終の状態に至りません";

ORYX.I18N.Validator.syntaxErrorsTitle = "構文エラー";
ORYX.I18N.Validator.syntaxErrorsMsg = "構文エラーが含まれるためプロセスを検証できません";
	
ORYX.I18N.Validator.error = "検証が失敗しました";
ORYX.I18N.Validator.errorDesc = '申し訳ありませんがプロセスの検証に失敗しました。プロセスモデルを"フィードバックを送る"ファンクションで送ると、問題を特定するのに役立ちます';

ORYX.I18N.Validator.epcIsSound = "<p><b>EPCは有効です。 問題は見つかりませんでした！</b></p>";
ORYX.I18N.Validator.epcNotSound = "<p><b>EPCは<i>有効ではありません！</i></b></p>";

/** New Language Properties: 05.06.2009*/
if(!ORYX.I18N.RESIZE) ORYX.I18N.RESIZE = {};
ORYX.I18N.RESIZE.tipGrow = "キャンバスサイズを拡大:";
ORYX.I18N.RESIZE.tipShrink = "キャンバスサイズを縮小:";
ORYX.I18N.RESIZE.N = "上";
ORYX.I18N.RESIZE.W = "左";
ORYX.I18N.RESIZE.S ="下";
ORYX.I18N.RESIZE.E ="右";
/** New Language Properties: 14.08.2009*/
if(!ORYX.I18N.PluginLoad) ORYX.I18N.PluginLoad = {};
ORYX.I18N.PluginLoad.AddPluginButtonName = "プラグインを追加";
ORYX.I18N.PluginLoad.AddPluginButtonDesc = "追加プラグインを動的に追加";
ORYX.I18N.PluginLoad.loadErrorTitle="ローディングエラー";
ORYX.I18N.PluginLoad.loadErrorDesc = "プラグインをロードできません \n エラー:\n";
ORYX.I18N.PluginLoad.WindowTitle ="追加プラグインを追加";

ORYX.I18N.PluginLoad.NOTUSEINSTENCILSET = "このステンシルセットでは許可されません！";
ORYX.I18N.PluginLoad.REQUIRESTENCILSET = "別のステンシルセットが必要です!";
ORYX.I18N.PluginLoad.NOTFOUND = "プラグインの名前が見つかりません！"
ORYX.I18N.PluginLoad.YETACTIVATED = "プラグインがまだアクティブです！"

/** New Language Properties: 15.07.2009*/
if(!ORYX.I18N.Layouting) ORYX.I18N.Layouting ={};
ORYX.I18N.Layouting.doing = "レイアウト中...";

/** New Language Properties: 18.08.2009*/
ORYX.I18N.SyntaxChecker.MULT_ERRORS = "複数のエラー";

/** New Language Properties: 08.09.2009*/
if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};
ORYX.I18N.PropertyWindow.oftenUsed = "プロパティー"
ORYX.I18N.PropertyWindow.moreProps = "プロパティー詳細";
ORYX.I18N.PropertyWindow.simulationProps = "シミュレーション";

/** New Language Properties: 17.09.2009*/
if(!ORYX.I18N.Bpmn2_0Serialization) ORYX.I18N.Bpmn2_0Serialization = {};
ORYX.I18N.Bpmn2_0Serialization.show = "BPMN 2.0 DI XMLを表示";
ORYX.I18N.Bpmn2_0Serialization.showDesc = "現在のBPMN 2.0 modelのBPMN 2.0 DI XMLを表示";
ORYX.I18N.Bpmn2_0Serialization.download = "BPMN 2.0 DI XMLをダウンロード";
ORYX.I18N.Bpmn2_0Serialization.downloadDesc = "現在のBPMN 2.0 modelのBPMN 2.0 DI XMLをダウンロード";
ORYX.I18N.Bpmn2_0Serialization.serialFailed = "BPMN 2.0 DI XML シリアライゼーション生成中にエラーが発生しました";
ORYX.I18N.Bpmn2_0Serialization.group = "BPMN 2.0";

/** New Language Properties 01.10.2009 */
if(!ORYX.I18N.SyntaxChecker.BPMN2) ORYX.I18N.SyntaxChecker.BPMN2 = {};

ORYX.I18N.SyntaxChecker.BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = "データ入力は内向きのデータ関連性を含んではいけません";
ORYX.I18N.SyntaxChecker.BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = "データ出力は外向きのデータ関連性を含んではいけません";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS = "イベントベースのゲートウェイのターゲットは一つの内向きのシーケンスフローを持ちます";

/** New Language Properties 02.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS = "イベントベースのゲートウェイは二つかそれ以上の外向きのシーケンスフローを持つ必要があります";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION = "メッセージ仲介イベントは設定で使用されます。受信タスクは設定で使用できません、またその逆も同様です";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_TRIGGER = "これらの仲介イベントのみがトリガーとして有効です: メッセージ, シグナル, タイマー, 条件付きと複数";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION = "イベントゲートウェイの外向きのシーケンスフローは条件表現を含んではいけません";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_NOT_INSTANTIATING = "ゲートウェイはプロセスをインスタンス化する条件に合いません。ゲートウェイにはスタートイベントかインスタンス化される属性を使用してください";

/** New Language Properties 05.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_MIXED_FAILURE = "ゲートウェイは複数の内向きと外向き両方のシーケンスフローを持つ必要があります";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE = "ゲートウェイは複数の内向きのシーケンスフローを持ちますが外向きのシーケンスフローを複数もってはいけません";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE = "ゲートウェイは内向きのシーケンスフローを複数もってはいけませんが外向きのシーケンスフローは複数持つ必要があります";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW = "ゲートウェイは少なくとも一つの外向きのシーケンスフローを持つ必要があります";
ORYX.I18N.SyntaxChecker.BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT = "イベントゲートウェイの設定に使用される受信タスクは付属の仲介イベントをもってはいけません";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION = "イベントサブプロセスは内向き、外向きのシーケンスフローをもってはいけません";

/** New Language Properties 13.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_CONNECTED = "少なくとも片側のメッセージフローが接続されている必要があります";

/** New Language Properties 19.10.2009 */
ORYX.I18N.Bpmn2_0Serialization['import'] = "BPMN 2.0 DI XMLからインポート";
ORYX.I18N.Bpmn2_0Serialization.importDesc = "ファイルもしくはXML文字列からBPMN 2.0 モデルをインポート";
ORYX.I18N.Bpmn2_0Serialization.selectFile = "(*.bpmn) ファイルを選択もしくはインポートするBPMN 2.0 DI XMLを入力";
ORYX.I18N.Bpmn2_0Serialization.file = "ファイル:";
ORYX.I18N.Bpmn2_0Serialization.name = "BPMN 2.0 DI XMLからインポート";
ORYX.I18N.Bpmn2_0Serialization.btnImp = "インポート";
ORYX.I18N.Bpmn2_0Serialization.progress = "BPMN 2.0 DI XMLをインポート中...";
ORYX.I18N.Bpmn2_0Serialization.btnClose = "閉じる";
ORYX.I18N.Bpmn2_0Serialization.error = "BPMN 2.0 DI XMLをインポート中にエラーが発生しました";

/** New Language Properties 24.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_MESSAGES = "コレオグラフィーアクティビティーは一つの初期化メッセージを持ちます";
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_ALLOWED = "メッセージフローはここでは許可されません";

/** New Language Properties 27.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS = "インスタンス化していないイベントベースのゲートウェイは少なくとも一つの内向きのシーケンスフローを持つ必要があります";
ORYX.I18N.SyntaxChecker.BPMN2_TOO_FEW_INITIATING_PARTICIPANTS = "コレオグラフィーアクティビティーはインスタンス化する参加者（白）を一つ持つ必要があります";
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_PARTICIPANTS = "コレオグラフィーアクティビティーはインスタンス化する参加者（白）を一つ以上もってはいけません"
ORYX.I18N.SyntaxChecker.COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = "コミュニケーションは少なくとも二つの参加者で接続されている必要があります";
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_START_MUST_BE_PARTICIPANT = "メッセージフローのソースは参加している必要があります";
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_END_MUST_BE_PARTICIPANT = "メッセージフローのターゲットは参加している必要があります";
ORYX.I18N.SyntaxChecker.CONV_LINK_CANNOT_CONNECT_CONV_NODES = "会話リンクはコミュニケーションかサブの会話ノードに参加者と一緒に接続している必要があります";

/** New Language Properties 30.12.2009 */
ORYX.I18N.Bpmn2_0Serialization.xpdlShow = "XPDL 2.2を表示";
ORYX.I18N.Bpmn2_0Serialization.xpdlShowDesc = "BPMN 2.0 XML (by XSLT)ベースの XPDL 2.2を表示";
ORYX.I18N.Bpmn2_0Serialization.xpdlDownload = "XPDL 2.2としてダウンロード";
ORYX.I18N.Bpmn2_0Serialization.xpdlDownloadDesc = "BPMN 2.0 XML (by XSLT)ベースの XPDL 2.2をダウンロード";


if(!ORYX.I18N.cpntoolsSupport) ORYX.I18N.cpntoolsSupport = {};

ORYX.I18N.cpntoolsSupport.serverConnectionFailed = "サーバー接続に失敗しました";
ORYX.I18N.cpntoolsSupport.importTask = "CPN ファイル(.cpn)を選択もしくはインポートするCPN XML構造を入力";
ORYX.I18N.cpntoolsSupport.File = "ファイル:";
ORYX.I18N.cpntoolsSupport.cpn = "CPN";
ORYX.I18N.cpntoolsSupport.title = "CPN Oryx";
ORYX.I18N.cpntoolsSupport.importLable = "インポート";
ORYX.I18N.cpntoolsSupport.close = "閉じる";
ORYX.I18N.cpntoolsSupport.wrongCPNFile = "正しい CPN - Fileが選択されていません";
ORYX.I18N.cpntoolsSupport.noPageSelection = "ページが選択されていません";
ORYX.I18N.cpntoolsSupport.group = "エクスポート";
ORYX.I18N.cpntoolsSupport.importProgress = "インポート中 ...";
ORYX.I18N.cpntoolsSupport.exportProgress = "エクスポート中 ...";
ORYX.I18N.cpntoolsSupport.exportDescription = "CPN ツールへエクスポート";
ORYX.I18N.cpntoolsSupport.importDescription = "CPN ツールからインポート";

if(!ORYX.I18N.BPMN2YAWLMapper) ORYX.I18N.BPMN2YAWLMapper = {};

ORYX.I18N.BPMN2YAWLMapper.group = "エクスポート";
ORYX.I18N.BPMN2YAWLMapper.name = 'YAWL エクスポート';
ORYX.I18N.BPMN2YAWLMapper.desc = 'ダイアグラムをYAWLにマップしエクスポートしてください。"YAWLにマップするためのBPMNサブセット"がロードされていることを確かめてください';
