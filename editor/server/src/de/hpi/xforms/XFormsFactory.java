package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 * 
 */
public class XFormsFactory {
	
	public XForm createXForm() {
		return new XForm();
	}
	
	public Model createModel() {
		return new Model();
	}
	
	public Instance createInstance() {
		return new Instance();
	}
	
	public Bind createBind() {
		return new Bind();
	}
	
	public Submission createSubmission() {
		return new Submission();
	}
	
	public Input createInput() {
		return new Input();
	}
	
	public Secret createSecret() {
		return new Secret();
	}
	
	public Textarea createTextarea() {
		return new Textarea();
	}
	
	public Output createOutput() {
		return new Output();
	}
	
	public Upload createUpload() {
		return new Upload();
	}
	
	public Range createRange() {
		return new Range();
	}
	
	public Trigger createTrigger() {
		return new Trigger();
	}
	
	public Submit createSubmit() {
		return new Submit();
	}
	
	public Select createSelect() {
		return new Select();
	}
	
	public Select1 createSelect1() {
		return new Select1();
	}
	
	public Group createGroup() {
		return new Group();
	}
	
	public Repeat createRepeat() {
		return new Repeat();
	}
	
	public Switch createSwitch() {
		return new Switch();
	}
	
	public Case createCase() {
		return new Case();
	}
	
	public Label createLabel() {
		return new Label();
	}
	
	public Help createHelp() {
		return new Help();
	}
	
	public Hint createHint() {
		return new Hint();
	}
	
	public Alert createAlert() {
		return new Alert();
	}
	
	public Item createItem() {
		return new Item();
	}
	
	public Itemset createItemset() {
		return new Itemset();
	}
	
	public Value createValue() {
		return new Value();
	}
	
	public Copy createCopy() {
		return new Copy();
	}
	
	public Choices createChoices() {
		return new Choices();
	}
	
	public Action createAction() {
		return new Action();
	}
	
	public SetValue createSetValue() {
		return new SetValue();
	}
	
	public Insert createInsert() {
		return new Insert();
	}
	
	public Delete createDelete() {
		return new Delete();
	}
	
	public SetIndex createSetIndex() {
		return new SetIndex();
	}
	
	public Toggle createToggle() {
		return new Toggle();
	}
	
	public SetFocus createSetFocus() {
		return new SetFocus();
	}
	
	public Dispatch createDispatch() {
		return new Dispatch();
	}
	
	public Rebuild createRebuild() {
		return new Rebuild();
	}
	
	public Recalculate createRecalculate() {
		return new Recalculate();
	}
	
	public Revalidate createRevalidate() {
		return new Revalidate();
	}
	
	public Refresh createRefresh() {
		return new Refresh();
	}
	
	public Reset createReset() {
		return new Reset();
	}
	
	public Load createLoad() {
		return new Load();
	}
	
	public Send createSend() {
		return new Send();
	}
	
	public Message createMessage() {
		return new Message();
	}
	
	public XFormsElement createElementByTagName(String tagName) {
		tagName = tagName.toLowerCase();
		if(tagName.equals("model"))
			return createModel();
		else if(tagName.equals("instance"))
			return createInstance();
		else if(tagName.equals("bind"))
			return createBind();
		else if(tagName.equals("submission"))
			return createSubmission();
		else if(tagName.equals("input"))
			return createInput();
		else if(tagName.equals("secret"))
			return createSecret();
		else if(tagName.equals("textarea"))
			return createTextarea();
		else if(tagName.equals("output"))
			return createOutput();
		else if(tagName.equals("upload"))
			return createUpload();
		else if(tagName.equals("range"))
			return createRange();
		else if(tagName.equals("trigger"))
			return createTrigger();
		else if(tagName.equals("submit"))
			return createSubmit();
		else if(tagName.equals("select"))
			return createSelect();
		else if(tagName.equals("select1"))
			return createSelect1();
		else if(tagName.equals("group"))
			return createGroup();
		else if(tagName.equals("repeat"))
			return createRepeat();
		else if(tagName.equals("switch"))
			return createSwitch();
		else if(tagName.equals("case"))
			return createCase();
		else if(tagName.equals("label"))
			return createLabel();
		else if(tagName.equals("help"))
			return createHelp();
		else if(tagName.equals("hint"))
			return createHint();
		else if(tagName.equals("alert"))
			return createAlert();
		else if(tagName.equals("item"))
			return createItem();
		else if(tagName.equals("itemset"))
			return createItemset();
		else if(tagName.equals("value"))
			return createValue();
		else if(tagName.equals("copy"))
			return createCopy();
		else if(tagName.equals("choices"))
			return createChoices();
		else if(tagName.equals("action"))
			return createAction();
		else if(tagName.equals("setvalue"))
			return createSetValue();
		else if(tagName.equals("insert"))
			return createInsert();
		else if(tagName.equals("delete"))
			return createDelete();
		else if(tagName.equals("setindex"))
			return createSetIndex();
		else if(tagName.equals("toggle"))
			return createToggle();
		else if(tagName.equals("setfocus"))
			return createSetFocus();
		else if(tagName.equals("dispatch"))
			return createDispatch();
		else if(tagName.equals("rebuild"))
			return createRebuild();
		else if(tagName.equals("recalculate"))
			return createRecalculate();
		else if(tagName.equals("revalidate"))
			return createRevalidate();
		else if(tagName.equals("refresh"))
			return createRefresh();
		else if(tagName.equals("reset"))
			return createReset();
		else if(tagName.equals("load"))
			return createLoad();
		else if(tagName.equals("send"))
			return createSend();
		else if(tagName.equals("message"))
			return createMessage();
		else return null;
	}

}
