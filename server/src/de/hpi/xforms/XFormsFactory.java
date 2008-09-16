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
	
	public Group createGroup() {
		return new Group();
	}
	
	public Repeat createRepeat() {
		return new Repeat();
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

}
