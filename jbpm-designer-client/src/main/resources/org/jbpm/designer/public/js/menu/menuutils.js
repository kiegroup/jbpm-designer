function openintab(url) {
    var togo = window.location.protocol + "//" + window.location.host + "/editor?uuid=" + url + "&profile=jbpm&pp=";
    window.open(togo, '_blank');
}