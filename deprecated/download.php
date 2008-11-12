<?
   	function force_download ($data, $name, $mimetype, $filesize=false) {
	    // File size not set?
	    if ($filesize == false OR !is_numeric($filesize)) {
	        $filesize = strlen($data);
	    }

	    // Mimetype not set?
	    if (empty($mimetype)) {
	        $mimetype = 'application/octet-stream';
	    }
	
	    // Make sure there's not anything else left
	    ob_clean_all();
	
	    // Start sending headers
	    header("Pragma: public"); // required
	    header("Expires: 0");
	    header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
	    header("Cache-Control: private",false); // required for certain browsers
	    header("Content-Transfer-Encoding: binary");
	    header("Content-Type: " . $mimetype);
	    header("Content-Length: " . $filesize);
	    header("Content-Disposition: attachment; filename=\"" . $name . "\";" );
	
	    // Send data
	    echo $data;
	    die();
	}
	
	function ob_clean_all () {
	    $ob_active = ob_get_length () !== false;
	    while($ob_active) {
	        ob_end_clean();
	        $ob_active = ob_get_length () !== false;
	    }
	
	    return true;
	}
    
    if ($_POST['download_0']) {    
    	require_once('zip.lib.php'); 
    	$zipfile = new zipfile(); 
    
    	$i = 0;
    	while ($_POST['download_'.$i]) {
    		$content = $_POST['download_'.$i];
    		$file = $_POST['file_'.$i];
    		$zipfile ->addFile( stripcslashes( $content ), $file );
    		$i++;
    	}	
    	$dump_buffer = $zipfile -> file();
    	force_download($dump_buffer, "result.zip", "application/zip", false);
    } else if ($_POST['download']) {
    	$result = $_POST['download'];
    	$file = $_POST['file'];
    	force_download( stripcslashes( $result ), $file, '', false);
    } else {
    	echo "There is nothing to be downloaded.";
    }
?>