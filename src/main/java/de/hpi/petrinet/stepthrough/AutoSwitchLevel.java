package de.hpi.petrinet.stepthrough;

public enum AutoSwitchLevel {
	NoAuto,
	SemiAuto,
	FullAuto,
	HyperAuto;
	
	public boolean lowerLevelExists() {
		if(this.ordinal() == 0) return false;
		return true;
	}
	
	public AutoSwitchLevel lowerLevel() {
		// If no lower level exist, the lowest level is returned
		if(this.ordinal() == 2) return AutoSwitchLevel.SemiAuto;
		else return AutoSwitchLevel.NoAuto;
	}
	
	public static AutoSwitchLevel fromInt(int i) {
		if(i == 3) return AutoSwitchLevel.HyperAuto;
		if(i == 2) return AutoSwitchLevel.FullAuto;
		if(i == 1) return AutoSwitchLevel.SemiAuto;
		return AutoSwitchLevel.NoAuto;
	}
}