SilentInstall silent
RequestExecutionLevel user
CRCCheck On
OutFile "Oryx.exe"
Icon "C:\Users\Philipp\workspace\standalone\srcApp\Oryx-Editor\chrome\icons\default\Oryx.ico"
SetOverwrite ifnewer
SetCompress auto
InstallDir "$LOCALAPPDATA\Oryx-Standalone"


Section "filecopying"

CreateDirectory $INSTDIR
SetOutPath $INSTDIR
File /r "C:\Users\Philipp\workspace\standalone\srcApp\Oryx-Editor\*"
WriteUninstaller "uninstall.exe"

SetOutPath $DESKTOP
CreateShortCut Oryx.lnk $INSTDIR\Oryx.exe

; write uninstall strings
WriteRegStr HKCU  "Software\Microsoft\Windows\CurrentVersion\Uninstall\Oryx-Standalone" "DisplayName" "Oryx-Standalone"
WriteRegStr HKCU  "Software\Microsoft\Windows\CurrentVersion\Uninstall\Oryx-Standalone" "UninstallString" '"$INSTDIR\uninstall.exe"'
WriteRegStr HKCU  "Software\Microsoft\Windows\CurrentVersion\Uninstall\Oryx-Standalone" "InstallLocation " '"$INSTDIR"'
WriteRegStr HKCU  "Software\Microsoft\Windows\CurrentVersion\Uninstall\Oryx-Standalone" "Publisher" "Oryx Team"
WriteRegStr HKCU  "Software\Microsoft\Windows\CurrentVersion\Uninstall\Oryx-Standalone" "HelpLink " "http://bpt.hpi.uni-potsdam.de/Oryx"
WriteRegStr HKCU  "Software\Microsoft\Windows\CurrentVersion\Uninstall\Oryx-Standalone" "NoModify " 1
WriteRegStr HKCU  "Software\Microsoft\Windows\CurrentVersion\Uninstall\Oryx-Standalone" "NoRepair" 1

Sleep 500
Exec "$INSTDIR\Oryx.exe"

SectionEnd



; Uninstaller

UninstallText "This will uninstall Oryx Standalone. Hit next to continue."
UninstallIcon "${NSISDIR}\Contrib\Graphics\Icons\nsis1-uninstall.ico"
Section "Uninstall"

  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Oryx-Standalone"

  RMDir /r "$INSTDIR"
  
SectionEnd
