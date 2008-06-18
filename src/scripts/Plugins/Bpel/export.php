<?php
	require_once "Bpel/Archive.php";
	
	if ($_POST['settings']){ 
	
	  	if (isset($_POST['settings'])) list($definitions, $filenames, $xmlData )= explode("##&Entrie", $_POST['settings']);
	 
	  	//the first Entrie holds an Array with the number of processes and the main Filename
	  	$xmlData= explode("##&Data", $xmlData);
	  	//the second  Entrie holds an Array with the XML string of each process
	    $definitions= explode("##&Data", $definitions);
	    //the third Entrie holds an Array with the filenames of each process
	    $filenames= explode("##&Data", $filenames);
	  
	    $count=$definitions[0];
	    if($count==1){
	
		    header("Pragma: public");
			header("Expires: 0");
			header("Cache-Control: private", false);
			header("Content-Type: text/xml");
			
			//if is only one, is only necessary to make the post of the XML String of the first XML string
			echo $jsonString = str_replace("\\", "", $xmlData[0]);
			
		} else{
	
			$fileName = $definitions[1].".zip";  
		
			$dest = File_Archive::toArchive($fileName, File_Archive::toFiles());
		
			// if is more than one, is only necessary to add it to the zip, 
			// with the proper name, defined in the filename Array and with the string Data in the xml Data
			for ($i=0; $i<$count; $i++){
	
				$dest->newFile($filenames[$i].".bpel" );
			
				$dest->writeData(str_replace("\\", "", $xmlData[$i]));
			}
	
			$dest->close();
	
		    header("Pragma: public");
			header("Expires: 0");
			header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
			header("Cache-Control: private",false);
			header("Content-Type: application/zip");
			header("Content-Disposition: attachment; filename=".basename($fileName ).";" );
			header("Content-Transfer-Encoding: binary");
			header("Content-Length: ".filesize($fileName ));
			readfile($fileName);
				
		}
	} else echo "error";

?> 